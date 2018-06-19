package io.anserini.rerank;

import org.apache.lucene.document.Document;

import java.util.SortedSet;
import java.util.TreeSet;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/**
 * Created by jimmylin on 6/19/18.
 */
public class TiebreakerReranker implements Reranker {
    // Sort by score, break ties by sort order of collection docid.
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

        return this.docid.compareTo(docid);
      }

      public boolean equals(Object other) {
        if (other == null) {
          return false;
        } if (other.getClass() != this.getClass()) {
          return false;
        }

        return ((Result) other).docid == this.docid;
      }
    }

    @Override
    public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
      SortedSet<Result> sortedResults = new TreeSet<>();
      for (int i=0; i<docs.documents.length; i++ ) {
        sortedResults.add(new Result(docs.scores[i], docs.ids[i],
            docs.documents[i].getField(FIELD_ID).stringValue(), docs.documents[i]));
      }

      int numResults = sortedResults.size();
      ScoredDocuments rerankedDocs = new ScoredDocuments();
      rerankedDocs.documents = new Document[numResults];
      rerankedDocs.ids = new int[numResults];
      rerankedDocs.scores = new float[numResults];

      int i = 0;
      int dup = 0;
      float prevScore = 0;
      for (Result result : sortedResults) {
        float curScore = result.score;
        // If we encounter ties, we want to perturb the final score a bit.
        if (Math.abs(curScore - prevScore) > 0.001f) {
          dup = 0;
        } else {
          dup ++;
          curScore = curScore - 0.000001f * dup;
        }

        rerankedDocs.documents[i] = result.doc;
        rerankedDocs.ids[i] = result.ldocid;
        rerankedDocs.scores[i] = curScore;
        prevScore = result.score;
        i++;
      }

      return rerankedDocs;
    }
}
