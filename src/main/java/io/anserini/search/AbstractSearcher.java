/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.search;

import io.anserini.index.Constants;
import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AbstractSearcher<K extends Comparable<K>> {
  protected final BaseSearchArgs args;

  public AbstractSearcher(BaseSearchArgs args) {
    this.args = args;
  }

  public ScoredDoc[] processLuceneTopDocs(IndexSearcher searcher, K qid, TopDocs docs) throws IOException {
    List<ScoredDoc> results = new ArrayList<>();
    // For removing duplicate docids.
    Set<String> docids = new HashSet<>();

    int rank = 1;
    for (int i = 0; i < docs.scoreDocs.length; i++) {
      int lucene_docid = docs.scoreDocs[i].doc;
      Document lucene_document = searcher.storedFields().document(docs.scoreDocs[i].doc);
      String docid = lucene_document.get(Constants.ID);

      if (args.selectMaxPassage) {
        docid = docid.split(args.selectMaxPassageDelimiter)[0];
      }

      if (docids.contains(docid))
        continue;

      // Remove docids that are identical to the query id if flag is set.
      if (args.removeQuery && docid.equals(qid))
        continue;

      results.add(new ScoredDoc(docid, lucene_docid, docs.scoreDocs[i].score, lucene_document));

      // Note that this option is set to false by default because duplicate documents usually indicate some
      // underlying indexing issues, and we don't want to just eat errors silently.
      //
      // However, when we're performing passage retrieval, i.e., with "selectMaxPassage", we *do* want to remove
      // duplicates.
      if (args.removeDuplicates || args.selectMaxPassage) {
        docids.add(docid);
      }

      rank++;

      if (args.selectMaxPassage && rank > args.selectMaxPassageHits) {
        break;
      }
    }

    return results.toArray(new ScoredDoc[results.size()]);
  }
}
