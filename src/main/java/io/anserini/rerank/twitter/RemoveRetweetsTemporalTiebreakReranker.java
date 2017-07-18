package io.anserini.rerank.twitter;

import io.anserini.rerank.Reranker;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import org.apache.lucene.document.Document;

import java.util.SortedSet;
import java.util.TreeSet;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public class RemoveRetweetsTemporalTiebreakReranker implements Reranker {
  // Sort by score, break ties by higher docid first (i.e., more temporally recent first)
  public static class Result implements Comparable<Result> {
    public float score;
    public long docid;
    public int id;
    public Document document;

    public int compareTo(Result other) {
      if (this.score > other.score) {
        return -1;
      } else if (this.score < other.score) {
        return 1;
      } else {
        if (this.docid > other.docid) {
          return -1;
        } else if (this.docid < other.docid) {
          return 1;
        } else {
          return 0;
        }
      }
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
    // Resort results based on score, breaking ties by larger docid first (i.e., recent first).
    SortedSet<Result> sortedResults = new TreeSet<Result>();
    for (int i=0; i<docs.documents.length; i++ ) {
      Result result = new Result();
      result.document = docs.documents[i];
      result.score = docs.scores[i];
      result.id = docs.ids[i];
      result.docid = Long.parseLong(docs.documents[i].getField(FIELD_ID).stringValue());

      sortedResults.add(result);
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

      rerankedDocs.documents[i] = result.document;
      rerankedDocs.ids[i] = result.id;
      rerankedDocs.scores[i] = (float) curScore;
      prevScore = result.score;
      i++;
    }

    return rerankedDocs;
  }
}
