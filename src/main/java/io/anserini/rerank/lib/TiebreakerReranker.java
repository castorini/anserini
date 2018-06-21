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

import java.util.SortedSet;
import java.util.TreeSet;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/**
 * <p>Reranker that ensures consistent ordering of documents that have the same score. Scoring ties are broken based on
 * the lexicographic ordering of the collection docid. This is accomplished by rounding original document scores to the
 * fourth decimal place, and then adding a tiny score perturbation to break scoring ties.</p>
 *
 * <p>This is necessary for repeatable runs: due to multi-threaded indexing, documents are added to the index in
 * arbitrary order, which makes Lucene's internal mechanism for resolving scoring ties non-deterministic across
 * different indexes.</p>
 *
 * <p>Note however, that this reranker also is not sufficient for completely repeatable runs due to scoring ties that
 * span the rank cutoff <i>k</i>. Due to scoring ties, the top <i>k</i> might vary across indexes; there is nothing that
 * this reranker can do for such cases. The only solution is to retrieve more than top <i>k</i>, break scoring ties, and
 * then truncate to top <i>k</i>.</p>
 */
public class TiebreakerReranker implements Reranker {
    // Sort by score, break ties by lexicographic ordering of collection docid.
    private class Result implements Comparable<Result> {
      public float score;
      public String docid;
      public int ldocid;
      public Document doc;

      public Result(float score, int ldocid, String docid, Document doc) {
        this.score = score;
        this.ldocid = ldocid;
        this.docid = docid;
        this.doc = doc;
      }

      public int compareTo(Result other) {
        if (this.score > other.score) {
          return -1;
        } else if (this.score < other.score) {
          return 1;
        }

        return this.docid.compareTo(other.docid);
      }
    }

    @Override
    public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
      SortedSet<Result> sortedResults = new TreeSet<>();
      for (int i=0; i<docs.documents.length; i++ ) {
        float rounded = Math.round(docs.scores[i] * 1e4f) / 1e4f;
        sortedResults.add(new Result(rounded, docs.ids[i],
            docs.documents[i].getField(FIELD_ID).stringValue(), docs.documents[i]));
      }

      int numResults = sortedResults.size();
      ScoredDocuments rerankedDocs = new ScoredDocuments();
      rerankedDocs.documents = new Document[numResults];
      rerankedDocs.ids = new int[numResults];
      rerankedDocs.scores = new float[numResults];

      int i = 0;
      int dup = 0;
      float prevScore = 0.0f;
      for (Result result : sortedResults) {
        float curScore = result.score;

        // If we encounter ties, we want to perturb the final score a tiny bit.
        // Here's the basic approach, by example. Let's say our starting ranked list was:
        //
        //   1 docA 23.439316
        //   2 docS 22.087432
        //   3 docT 22.087432
        //   4 docZ 21.602508
        //
        // The point is that we want to perturb the scores in a small way such that the scores give us the exact sort
        // order we want, independent of how any external evaluation tool (e.g., trec_eval) breaks ties.
        // We accomplish this by rounding all scores to 1e-4, and then subtracting a minor delta of 1e-6 for each tie.
        // So, the above becomes:
        //
        //   1 docA 23.4393   (dup=0)
        //   2 docS 22.0874   (dup=0)
        //   3 docT 22.0874 - (dup=1)*1e-6
        //   4 docZ 21.6025   (dup=0)
        //
        // Note that we can't use equality comparison directly to detect duplicates, because in the case of multiple
        // ties, we would have perturbed the scores, leading the scores to not be equal (hence we check for score
        // difference greater than 1e-4).
        //
        // Why 1e-4 and 1e-6? If we make the former larger, than we lose score resolution in the original score. If we
        // make 1e-4 smaller we have to make 1e-6 smaller, in which case we start bumping into floating point precision
        // issues during subtraction.
        if ( prevScore == 0.0f || prevScore - curScore > 1e-4f ) {
          dup = 0;
        } else {
          dup++;
          curScore = curScore - 1e-6f * dup;
        }

        rerankedDocs.documents[i] = result.doc;
        rerankedDocs.ids[i] = result.ldocid;
        rerankedDocs.scores[i] = curScore;
        prevScore = curScore;
        i++;
      }

      return rerankedDocs;
    }
}