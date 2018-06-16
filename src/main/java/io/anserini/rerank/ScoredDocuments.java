package io.anserini.rerank;

import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 * ScoredDocuments object that converts TopDocs from the searcher into an Anserini format
 */
public class ScoredDocuments {
  // Array of document objects
  public Document[] documents;
  // The docIds as used by the index reader
  public int[] ids;
  // Scores returned from the searcher's similarity
  public float[] scores;
  
  public static ScoredDocuments fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.documents = new Document[rs.scoreDocs.length];
    scoredDocs.ids = new int[rs.scoreDocs.length];
    scoredDocs.scores = new float[rs.scoreDocs.length];

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.documents[i] = searcher.doc(rs.scoreDocs[i].doc);
      } catch (IOException e) {
        e.printStackTrace();
        scoredDocs.documents[i] = null;
      }
      scoredDocs.scores[i] = rs.scoreDocs[i].score;
      scoredDocs.ids[i] = rs.scoreDocs[i].doc;
    }

    return scoredDocs;
  }
}
