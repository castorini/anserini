package io.anserini.search.rerank.twitter;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.search.rerank.Reranker;
import io.anserini.search.rerank.RerankerContext;
import io.anserini.search.rerank.ScoredDocuments;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.document.Document;

public class RemoveRetweetsTemporalTiebreakReranker implements Reranker {

  public static class Result {
    public float rsv;
    public long id;
    public Document document;
  }

  public static class ResultComparable implements Comparable<ResultComparable> {
    private Result tresult;

    public ResultComparable(Result tresult) {
      this.tresult = tresult;
    }

    public Result getTResult() {
      return tresult;
    }

    public int compareTo(ResultComparable other) {
      if (tresult.rsv > other.tresult.rsv) {
        return -1;
      } else if (tresult.rsv < other.tresult.rsv) {
        return 1;
      } else {
        if (tresult.id > other.tresult.id) {
          return -1;
        } else if (tresult.id < other.tresult.id) {
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

      return ((ResultComparable) other).tresult.id == this.tresult.id;
    }
  }

  @Override
  public ScoredDocuments rerank(ScoredDocuments docs, RerankerContext context) {
    
    SortedSet<ResultComparable> sortedResults = new TreeSet<ResultComparable>();
    for (int i=0; i<docs.documents.length; i++ ) {
      Result result = new Result();
      result.document = docs.documents[i];
      result.rsv = docs.scores[i];
      result.id = (long) docs.documents[i].getField(StatusField.ID.name).numericValue();

      // Throw away retweets.
      if (docs.documents[i].getField(StatusField.RETWEETED_STATUS_ID.name) == null) {
        sortedResults.add(new ResultComparable(result));
      }
    }

    int numResults = sortedResults.size();

    ScoredDocuments rerankedDocs = new ScoredDocuments();
    rerankedDocs.documents = new Document[numResults];
    rerankedDocs.scores = new float[numResults];

    int i = 0;
    int dupliCount = 0;
    double rsvPrev = 0;
    for (ResultComparable sortedResult : sortedResults) {
      Result result = sortedResult.getTResult();
      double rsvCurr = result.rsv;
      if (Math.abs(rsvCurr - rsvPrev) > 0.001) {
        dupliCount = 0;
      } else {
        dupliCount ++;
        rsvCurr = rsvCurr - 0.000001 * dupliCount;
      }

      rerankedDocs.documents[i] = sortedResult.getTResult().document;
      rerankedDocs.scores[i] = (float) rsvCurr;
      rsvPrev = result.rsv;
      i++;
    }

    return rerankedDocs;
  }

}
