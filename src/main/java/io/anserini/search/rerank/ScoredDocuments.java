package io.anserini.search.rerank;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

public class ScoredDocuments {
  public Document[] documents;
  public float[] scores;
  
  public static ScoredDocuments fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.documents = new Document[rs.scoreDocs.length];
    scoredDocs.scores = new float[rs.scoreDocs.length];

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.documents[i] = searcher.doc(rs.scoreDocs[i].doc);
      } catch (IOException e) {
        e.printStackTrace();
        scoredDocs.documents[i] = null;
      }
      scoredDocs.scores[i] = rs.scoreDocs[i].score;
    }

    return scoredDocs;
  }
}
