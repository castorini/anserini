package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.search.similarities.DefaultSimilarity;
import org.apache.lucene.search.similarities.TFIDFSimilarity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Computes the TFIDF feature according to Lucene's formula,
 * Not the same because we don't compute length norm or query norm, with boost 1
 */
public class TFIDFFeatureExtractor implements FeatureExtractor{
  private static final Logger LOG = LogManager.getLogger(TFIDFFeatureExtractor.class);

  @Override
  public float extract(Document doc, Terms terms, RerankerContext context) {
    float score = 0.0f;
    Map<String, Long> countMap = new HashMap<>();
    Map<String, Integer> docFreqs = new HashMap<>();
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    long numDocs =  reader.numDocs();
    for (String queryToken : context.getQueryTokens()) {
      try {
        docFreqs.put(queryToken, reader.docFreq(new Term(context.getField(), queryToken)));
      } catch (IOException e) {
        LOG.error("Error trying to read document frequency");
        docFreqs.put(queryToken, 0);
      }
    }

    try {
      TermsEnum termsEnum = terms.iterator();
      while (termsEnum.next() != null) {
        String termString = termsEnum.term().utf8ToString();
        if (context.getQueryTokens().contains(termString)) {
          countMap.put(termString, termsEnum.totalTermFreq());
        }
      }
    } catch (IOException e) {
      LOG.error("Error while accessing term vector");
    }

    TFIDFSimilarity similarity = new DefaultSimilarity();

    // number of query tokens found
    // how many of our query tokens were found
    float coord = similarity.coord(countMap.size(), context.getQueryTokens().size());

    for (String token : context.getQueryTokens()) {
      long termFreq = countMap.containsKey(token) ? countMap.get(token) : 0;
      long docFreq = docFreqs.containsKey(token) ? docFreqs.get(token) : 0;
      float tf = similarity.tf(termFreq);
      float idf = similarity.idf(docFreq, numDocs);
      score += tf * idf*idf;
    }

    score *= coord;

    return score;
  }

  @Override
  public String getName() {
    return "TFIDF";
  }
}
