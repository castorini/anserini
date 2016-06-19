package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiFields;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.BM25Similarity;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This feature extractor will compute BM25 score as according to Lucene 5.3 documentation
 * The formula is the same, but the computation of docSize is slghtly different,
 * Lucene uses the norm value encoded in the index, we are calculating it as is
 * also we do not have any boosting, the field norm is also not available
 */
public class BM25FeatureExtractor implements FeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(BM25FeatureExtractor.class);

  public static Map<String, Integer> getDocFreqs(IndexReader reader, List<String> queryTokens, String field) throws IOException {
    Map<String,Integer> docFreqs = new HashMap<>();
    // Must retrieve from multifields
    for (String queryToken : queryTokens) {
      docFreqs.put(queryToken, reader.docFreq(new Term(field, queryToken)));
    }
    return docFreqs;
  }

  // Default values, could be changed
  private double k1 = 1.2;
  private double b = 0.75;

  public BM25FeatureExtractor() { }

  public BM25FeatureExtractor(double k, double b) {
    this.k1 = k;
    this.b = b;
  }

  // Computed as log(1 + (numDocs - docFreq + 0.5)/(docFreq + 0.5)).
  private double computeIDF(long docFreq, long numDocs) {
    double denominator = docFreq + 0.5d;
    double numerator = numDocs - docFreq + 0.5d;
    return Math.log(1 + (numerator / denominator) );
  }

  // sumTotalTermFreq / maxDoc, 1 if sumTotalTermFreq not stored, or missing
  private double computeAvgFL(long sumTermFreqs, long maxDocs) {
    if (sumTermFreqs == 0) {
      return 1.0d;
    } else {
      return (sumTermFreqs/ (double) maxDocs);
    }
  }

  private long getSumTermFrequency(IndexReader reader, String fieldName) {
    Terms collectionTermVector = null;
    try {
      collectionTermVector = MultiFields.getTerms(reader, fieldName);
      long totalTermFreq = collectionTermVector.getSumTotalTermFreq();
      return totalTermFreq;
    } catch (IOException e) {
      LOG.warn("Unable to get total term frequency, it might not be indexed");
    }
    return 0;
  }


  /**
   * We will implement this according to the Lucene specification
   * the formula used:
   * sum ( IDF(qi) * (df(qi,D) * (k+1)) / (df(qi,D) + k * (1-b + b*|D| / avgFL))
   * IDF and avgFL computation are described above.
   * @param doc
   * @param terms
   * @param context
   * @return
   */
  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    Set<String> queryTokens = new HashSet<>(context.getQueryTokens());

    TermsEnum termsEnum = null;
    try {
      termsEnum = terms.iterator();
    } catch (IOException e) {
      LOG.warn("Error computing BM25, unable to retrieve terms enum");
      return 0.0f;
    }

    IndexReader reader = context.getIndexSearcher().getIndexReader();
    long maxDocs = reader.numDocs();
    long sumTotalTermFreq = getSumTermFrequency(reader, context.getField());
    // Compute by iterating
    long docSize  = 0L;

    // NOTE df cannot be retrieved just from the term vector,
    // the term vector here is only a partial term vector that treats this as if we only have 1 document in the index
    Map<String, Integer> docFreqMap = null;
    try {
      docFreqMap = getDocFreqs(reader, context.getQueryTokens(), context.getField());
    } catch (IOException e) {
      LOG.warn("Unable to retrieve document frequencies.");
      docFreqMap = new HashMap<>();
    }

    Map<String, Long> termFreqMap = new HashMap<>();
    try {
      while (termsEnum.next() != null) {
        String termString = termsEnum.term().utf8ToString();
        docSize += termsEnum.totalTermFreq();
        if (queryTokens.contains(termString)) {
          termFreqMap.put(termString, termsEnum.totalTermFreq());
        }
      }
    } catch (IOException e) {
      LOG.warn("Unable to retrieve termsEnum, treating as 0");
    }

    float score = 0.0f;
    // Iterate over the query tokens
    double avgFL = computeAvgFL(sumTotalTermFreq, maxDocs);
    for (String token : queryTokens) {
      long docFreq = docFreqMap.containsKey(token) ? docFreqMap.get(token) : 0;
      double termFreq = termFreqMap.containsKey(token) ? termFreqMap.get(token) : 0;
      double numerator = (this.k1 + 1) * termFreq;
      double docLengthFactor = this.b * (docSize / avgFL);
      double denominator = termFreq + (this.k1) * (1 - this.b + docLengthFactor);
      score += computeIDF(docFreq, maxDocs) * numerator / denominator;
    }

    return score;
  }

  @Override
  public String getName() {
    return "BM25Feature";
  }
}
