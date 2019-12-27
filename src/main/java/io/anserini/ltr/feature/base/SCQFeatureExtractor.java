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

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.util.List;

/**
 * This feature computes collection query similarity, avgSCQ defined as
 * Avg( (1 + log(tf(t,D))) * idf(t)) found on page 33 of Carmel, Yom-Tov 2010
 * D is the collection term frequency
 */
public class SCQFeatureExtractor<T> implements FeatureExtractor<T> {
  private static final Logger LOG = LogManager.getLogger(SCQFeatureExtractor.class);

  private String lastQueryProcessed = "";
  private float lastComputedScore = 0.0f;

  // Computed as log( 1+ (N - N_t + 0.5)/(N_t + 0.5))
  private float computeIDF(long docFreq, long numDocs) {
    return (float) Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
  }

  private float sumSCQ(IndexReader reader, List<String> queryTokens,
                       String field) throws IOException {

    long numDocs = reader.numDocs() - reader.numDeletedDocs();
    float scq = 0.0f;

    for (String token : queryTokens) {
      long docFreq = reader.docFreq(new Term(field, token));
      //TODO what about tf = 0
      long termFreq = reader.totalTermFreq(new Term(field, token));
      if (termFreq == 0) continue;
      scq += 1 + Math.log(termFreq* computeIDF(docFreq, numDocs));
    }

    return scq;
  }

  @Override
  public float extract(Document doc, Terms terms, RerankerContext<T> context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    if (!lastQueryProcessed.equals(context.getQueryText())) {
      this.lastQueryProcessed = context.getQueryText();
      this.lastComputedScore = 0.0f;

      try {
        float sumScq = sumSCQ(reader, context.getQueryTokens(), LuceneDocumentGenerator.FIELD_BODY);
        this.lastComputedScore = sumScq / context.getQueryTokens().size();
      } catch (IOException e) {
        this.lastComputedScore = 0.0f;
      }
    }

    return this.lastComputedScore;
  }

  @Override
  public String getName() {
    return "AvgSCQ";
  }
}
