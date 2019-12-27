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

package io.anserini.ltr;

import io.anserini.index.generator.TweetGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.Qrels;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Used to rerank according to features
 *
 */
public class TwitterFeatureReranker implements Reranker<Integer> {
  private final PrintStream out;
  private final Qrels qrels;
  private final FeatureExtractors extractors;

  public TwitterFeatureReranker(PrintStream out, Qrels qrels, FeatureExtractors extractors) {
    this.out = out;
    this.qrels = qrels;
    this.extractors = extractors == null ? TwitterFeatureExtractor.getDefaultExtractors() : extractors;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext<Integer> context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], TweetGenerator.FIELD_BODY);
      } catch (IOException e) {
        continue;
      }

      int qid = context.getQueryId();
      String docid = docs.documents[i].getField( TweetGenerator.FIELD_ID).stringValue();

      out.print(qrels.getRelevanceGrade(qid, docid));
      out.print(" qid:" + qid);

      float[] intFeatures = this.extractors.extractAll(docs.documents[i], terms, context);

      // TODO use model to rerank
    }

    return docs;
  }
  
  @Override
  public String tag() { return ""; }
}
