/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.rerank.lib;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;

/**
 * Reranker that truncates the number of results to a specified <i>k</i>.
 */
public class TruncateHitsReranker implements Reranker {
  private final int k;

  public TruncateHitsReranker(int k) {
    this.k = k;
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    if (docs.documents.length <= k) {
      return docs;
    }

    ScoredDocuments rerankedDocs = new ScoredDocuments();
    rerankedDocs.documents = new Document[k];
    rerankedDocs.ids = new int[k];
    rerankedDocs.scores = new float[k];

    System.arraycopy(docs.documents, 0, rerankedDocs.documents, 0, k);
    System.arraycopy(docs.ids, 0, rerankedDocs.ids, 0, k);
    System.arraycopy(docs.scores, 0, rerankedDocs.scores, 0, k);

    return rerankedDocs;
  }
}