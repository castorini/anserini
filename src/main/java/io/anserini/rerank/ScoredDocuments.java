package io.anserini.rerank;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;

/**
 * ScoredDocuments object that converts TopDocs from the searcher into an Anserini format
 */
public class ScoredDocuments {
  // Array of document objects
  public List<Document> documents;
  // The docIds as used by the index reader
  public List<Integer> ids;
  // Scores returned from the searcher's similarity
  public List<Float> scores;
  
  public static ScoredDocuments fromTopDocs(TopDocs rs, IndexSearcher searcher) {
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.documents = new ArrayList<>(rs.scoreDocs.length);
    scoredDocs.ids = new ArrayList<>(rs.scoreDocs.length);
    scoredDocs.scores = new ArrayList<>(rs.scoreDocs.length);

    for (int i=0; i<rs.scoreDocs.length; i++) {
      try {
        scoredDocs.documents.add(searcher.doc(rs.scoreDocs[i].doc));
      } catch (IOException e) {
        e.printStackTrace();
        scoredDocs.documents.add(null);
      }
      scoredDocs.scores.add(rs.scoreDocs[i].score);
      scoredDocs.ids.add(rs.scoreDocs[i].doc);
    }

    return scoredDocs;
  }
}
