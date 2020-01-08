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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

public class TweetsLtrDataGenerator implements Reranker {
  private final PrintStream out;
  private final Qrels qrels;
  private final FeatureExtractors extractorChain;


  public TweetsLtrDataGenerator(PrintStream out, Qrels qrels, FeatureExtractors extractors) throws FileNotFoundException {
    this.out = out;
    this.qrels = qrels;
    this.extractorChain = extractors == null ? WebFeatureExtractor.getDefaultExtractors() : extractors;

  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    IndexReader reader = context.getIndexSearcher().getIndexReader();

    for (int i = 0; i < docs.documents.length; i++) {
      Terms terms = null;
      try {
        terms = reader.getTermVector(docs.ids[i], TweetGenerator.FIELD_BODY);
      } catch (IOException e) {
        continue;
      }

      String qid = ((String)context.getQueryId()).replaceFirst("^MB0*", "");
      String docid = docs.documents[i].getField(TweetGenerator.FIELD_ID).stringValue();

      out.print(qrels.getRelevanceGrade(qid, docid));
      out.print(" qid:" + qid);
      out.print(" 1:" + docs.scores[i]);

      float[] intFeatures = this.extractorChain.extractAll(docs.documents[i], terms, context);

      for (int j=0; j<intFeatures.length; j++ ) {
        out.print(" " + (j+2) + ":" + intFeatures[j]);
      }

      out.print(" # docid:" + docid);
      out.print("\n");
    }

    return docs;
  }
  
  @Override
  public String tag() { return ""; }
}
