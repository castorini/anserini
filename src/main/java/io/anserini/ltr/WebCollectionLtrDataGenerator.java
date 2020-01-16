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

import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Terms;

import java.io.IOException;
import java.io.PrintStream;

/**
 * A reranker that will be used to dump feature vectors
 * for documents retrieved from a search
 */
public class WebCollectionLtrDataGenerator implements Reranker<Integer> {
  private static final Logger LOG = LogManager.getLogger(WebCollectionLtrDataGenerator.class);

  private PrintStream out;
  private Qrels qrels;
  private final FeatureExtractors extractorChain;

  /**
   * Constructor
   * @param out         The output stream to actually print it
   */
  public WebCollectionLtrDataGenerator(PrintStream out, Qrels qrels, FeatureExtractors extractors) {
    this.out = out;
    this.qrels = qrels;
    this.extractorChain = extractors == null ? WebFeatureExtractor.getDefaultExtractors() : extractors;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext<Integer> context) {
    Document[] documents = docs.documents;
    IndexReader reader = context.getIndexSearcher().getIndexReader();
    int qid = context.getQueryId();
    LOG.info("Beginning rerank");
    for (int i =0; i < docs.documents.length; i++ ) {
      try {
        Terms terms = reader.getTermVector(docs.ids[i], LuceneDocumentGenerator.FIELD_BODY);
        float[] features = this.extractorChain.extractAll(documents[i], terms, context);
        String docId = documents[i].get(LuceneDocumentGenerator.FIELD_ID);
        // QREL 0 in this case, will be assigned if needed later
        //qid
        BaseFeatureExtractor.writeFeatureVector(out, qid, this.qrels.getRelevanceGrade(qid, docId), docId,  features);
        LOG.info("Finished writing vectors");
      } catch (IOException e) {
        LOG.error(String.format("IOExecption trying to retrieve feature vector for %d doc", docs.ids[i]));
        continue;
      }
    }
    // Does nothing to the actual docs, we just need to extract the feature vector
    return docs;
  }
  
  @Override
  public String tag() { return ""; }
}
