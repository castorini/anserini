package io.anserini.rerank.lib;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;

import java.util.SortedSet;
import java.util.TreeSet;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/**
 * Reranker that ensures consistent ordering of documents that have the same score. Scoring ties are broken based on the
 * lexicographic ordering of the collection docid. This is accomplished by rounding original document scores to the
 * fourth decimal place, and then adding a tiny score perturbation to break scoring ties.
 *
 * This is necessary for repeatable runs: due to multi-threaded indexing, documents are added to the index in arbitrary
 * order, which makes Lucene's internal mechanism for resolving scoring ties non-deterministic across different indexes.
 *
 * Note however, that this reranker also is not sufficient for completely repeatable runs due to scoring ties that span
 * the rank cutoff <i>k</i>. Due to scoring ties, the top <i>k</i> might vary across indexes; there is nothing that this
 * reranker can do for such cases. The only solution is to retrieve more than top <i>k</i>, break scoring ties, and then
 * truncate to tope <i>k</i>.
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

        // If we encounter ties, we want to perturb the final score a bit.
        // Note that we can't use equality comparison directly, because in the case of multiple
        // ties, we would have perturbed the scores, leading the scores to being not equal.
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
