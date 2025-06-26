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

/**
 * <p>This class provides a base for all Lucene searchers, handling three basic common post-processing operations
 * (duplicate removal, docid-as-qid removal, and MaxP) on ranked lists based on the supplied configuration.</p>
 *
 * <p>In more detail:</p>
 *
 * <ul>
 *   <li><b>Duplicate removal.</b> If the <code>-removeDuplicates</code> flag is set, then we remove duplicate docids in
 *   the ranked list. This is set false by default because duplicate documents usually indicate some underlying corpus
 *   or indexing issues, and we don't want to just eat errors silently.</li>
 *
 *   <li><b>Docid-as-qid removal.</b> In some test collections, a document is used as a query, usually denoted by
 *   setting the qid as the docid. If the <code>-removeQuery</code> is set, then we remove the docid from the ranked
 *   list.</li>
 *
 *   <li><b>MaxP.</b> If the flag <code>-selectMaxPassage</code> is set, then we select the max scoring passage from a
 *   document as the score for that document. This technique dates from Dai and Callan (SIGIR 2019) in the context of
 *   BERT, although the general approach dates back to Callan (SIGIR 1994). We take <code>-selectMaxPassage.delimiter</code>
 *   as the doc/passage delimiter; defaults to "." (dot), so the passages within a docid are labeled as "docid.00000",
 *   "docid.00001", "docid.00002", etc. Using "#" (hash) is a common alternative, e.g., "docid#0". The number of docs
 *   to return in the final ranked list is controlled by the parameter <code>-selectMaxPassage.hits</code>.
 *   </li>
 * </ul>
 *
 * @param <K> type of qid, typically string or integer
 */
public class BaseSearcher<K extends Comparable<K>> {
  protected final BaseSearchArgs args;
  private IndexSearcher searcher;

  /**
   * Creates an instance of this class with supplied arguments.
   *
   * @param args configuration for duplicate removal, docid-as-qid removal, and MaxP
   */
  public BaseSearcher(BaseSearchArgs args) {
    this.args = args;
  }

  /**
   * Creates an instance of this class with supplied arguments.
   *
   * @param args configuration for duplicate removal, docid-as-qid removal, and MaxP
   * @param searcher {@link IndexSearcher} used for accessing documents from the index
   */
  public BaseSearcher(BaseSearchArgs args, IndexSearcher searcher) {
    this.args = args;
    this.searcher = searcher;
  }

  /**
   * Sets the {@link IndexSearcher} used for accessing documents from the index.
   *
   * @param searcher the {@link IndexSearcher} used for accessing documents from the index
   */
  protected void setIndexSearcher(IndexSearcher searcher) {
    this.searcher = searcher;
  }

  /**
   * Gets the {@link IndexSearcher} used for accessing documents from the index.
   *
   * @return the {@link IndexSearcher} used for accessing documents from the index
   */
  protected IndexSearcher getIndexSearcher() {
    return this.searcher;
  }

  /**
   * Processes Lucene {@link TopDocs} for a query based on the configuration for duplicate removal, docid-as-qid
   * removal, and MaxP. By default, retains references to the original Lucene docs (which can be memory intensive for
   * long ranked lists).
   *
   * @param qid query id
   * @param docs Lucene {@link TopDocs}
   * @return processed ranked list
   */
  public ScoredDoc[] processLuceneTopDocs(K qid, TopDocs docs) {
    return processLuceneTopDocs(qid, docs, true);
  }

  /**
   * Processes Lucene {@link TopDocs} for a query based on the configuration for duplicate removal, docid-as-qid
   * removal, and MaxP. Explicitly supports control over whether to retain references to the original Lucene docs
   * (and hence memory usage).
   *
   * @param qid query id
   * @param docs Lucene {@link TopDocs}
   * @param keepLuceneDocument whether to retain references to the original Lucene docs
   * @return processed ranked list
   */
  public ScoredDoc[] processLuceneTopDocs(K qid, TopDocs docs, boolean keepLuceneDocument) {
    List<ScoredDoc> results = new ArrayList<>();
    Set<String> docids = new HashSet<>(); // For removing duplicate docids.

    int rank = 1;
    for (int i = 0; i < docs.scoreDocs.length; i++) {
      int lucene_docid = docs.scoreDocs[i].doc;
      Document lucene_document;
      try {
        lucene_document = searcher.storedFields().document(docs.scoreDocs[i].doc);
      } catch (IOException e) {
        throw new RuntimeException(String.format("Unable to fetch document %d", docs.scoreDocs[i].doc));
      }
      String docid = lucene_document.get(Constants.ID);

      if (args.selectMaxPassage) {
        docid = docid.split(args.selectMaxPassageDelimiter)[0];
      }

      if (docids.contains(docid))
        continue;

      // Remove docids that are identical to the query id if flag is set.
      if (args.removeQuery && docid.equals(qid))
        continue;

      // Note that if keepLuceneDocument == true, then we're retaining references to a lot of objects that cannot be
      // garbage collected. If we're running lots of queries, e.g., from SearchCollection, this can easily exhaust
      // the heap.
      results.add(new ScoredDoc(docid, lucene_docid, docs.scoreDocs[i].score,
          keepLuceneDocument ? lucene_document : null));

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

    return results.toArray(new ScoredDoc[0]);
  }

  /**
   * Processes {@link ScoredDocs} for a query based on the configuration for duplicate removal, docid-as-qid removal,
   * and MaxP. By default, retains references to the original Lucene docs (which can be memory intensive for long
   * ranked lists).
   *
   * @param qid query id
   * @param docs {@link ScoredDocs} to process
   * @return processed ranked list
   */
  public ScoredDoc[] processScoredDocs(K qid, ScoredDocs docs) {
    return processScoredDocs(qid, docs, true);
  }

  /**
   * Processes {@link ScoredDocs} for a query based on the configuration for duplicate removal, docid-as-qid removal,
   * and MaxP. Explicitly supports control over whether to retain references to the original Lucene docs (and hence
   * memory usage).
   *
   * @param qid query id
   * @param docs {@link ScoredDocs} to process
   * @param keepLuceneDocument whether to retain references to the original Lucene docs
   * @return processed ranked list
   */
  public ScoredDoc[] processScoredDocs(K qid, ScoredDocs docs, boolean keepLuceneDocument) {
    assert docs.docids != null;
    assert docs.lucene_docids != null;
    assert docs.lucene_documents != null;
    assert docs.scores != null;

    List<ScoredDoc> results = new ArrayList<>();
    // For removing duplicate docids.
    Set<String> docids = new HashSet<>();

    int rank = 1;
    for (int i = 0; i < docs.lucene_documents.length; i++) {
      String docid = docs.docids[i];

      if (args.selectMaxPassage) {
        docid = docid.split(args.selectMaxPassageDelimiter)[0];
      }

      if (docids.contains(docid))
        continue;

      // Remove docids that are identical to the query id if flag is set.
      if (args.removeQuery && docid.equals(qid))
        continue;

      // Note that if keepLuceneDocument == true, then we're retaining references to a lot of objects that cannot be
      // garbage collected. If we're running lots of queries, e.g., from SearchCollection, this can easily exhaust
      // the heap.
      results.add(new ScoredDoc(docid, docs.lucene_docids[i], docs.scores[i],
          keepLuceneDocument ? docs.lucene_documents[i] : null));

      // Note that this option is set to false by default because duplicate documents usually indicate some
      // underlying indexing issues, and we don't want to just eat errors silently.
      //
      // However, when we're performing passage retrieval, i.e., with "selectMaxSegment", we *do* want to remove
      // duplicates.
      if (args.removeDuplicates || args.selectMaxPassage) {
        docids.add(docid);
      }

      rank++;

      if (args.selectMaxPassage && rank > args.selectMaxPassageHits) {
        break;
      }
    }

    return results.toArray(new ScoredDoc[0]);
  }
}
