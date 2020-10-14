/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

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
public class BM25FeatureExtractor implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25FeatureExtractor.class);
  private String lastQueryProcessed = "";
  private Map<String,Integer> lastComputedValue = new HashMap<>();

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
      collectionTermVector = MultiTerms.getTerms(reader, fieldName);
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
   */
  @Override
  public float extract(ContentContext context, QueryContext queryContext) {
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    float score = 0;

    for (String queryToken : queryContext.queryTokens) {
        long docFreq = context.getDocFreq(queryToken);
        double termFreq = context.getTermFreq(queryToken);
        double numerator = (this.k1 + 1) * termFreq;
        double docLengthFactor = this.b * (docSize / avgFL);
        double denominator = termFreq + (this.k1) * (1 - this.b + docLengthFactor);
        double idf = Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
        score += idf * numerator / denominator;
    }
    return score;
  }

  @Override
  public String getName() {
    return "BM25";
  }

  @Override
  public String getField() {
    return null;
  }

  public double getK1() {
    return k1;
  }

  public double getB() {
    return b;
  }

  @Override
  public FeatureExtractor clone() {
    return new BM25FeatureExtractor(this.k1, this.b);
  }
}
