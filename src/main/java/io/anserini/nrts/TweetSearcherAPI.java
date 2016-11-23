package io.anserini.nrts;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Path("/api")
public class TweetSearcherAPI {
  private static final long serialVersionUID = 1L;
  private IndexReader reader;

  static class SearchAPIQuery{
    private String query;
    private int count;
    public SearchAPIQuery() { count = 20;}

    public int getCount() {
      return count;
    }

    public void setCount(int count) {
      this.count = count;
    }

    public SearchAPIQuery(String query, int count) {

      this.query = query;
      this.count = count;
    }

    public String getQuery() {
      return query;
    }

    public void setQuery(String query) {
      this.query = query;
    }
  }
  static class SearchResult{
    String docid;

    public SearchResult() {}

    public String getDocid() {

      return docid;
    }

    public void setDocid(String docid) {
      this.docid = docid;
    }

    public SearchResult(String docid) {
      this.docid = docid;
    }
  }
  @POST
  @Path("search")
  @Produces(MediaType.APPLICATION_JSON)
  public List<SearchResult> search(SearchAPIQuery query){
    try {
      Query q = new QueryParser(TweetStreamIndexer.StatusField.TEXT.name, TweetSearcher.ANALYZER).parse(query.getQuery());
      try {
        reader = DirectoryReader.open(TweetSearcher.indexWriter, true, true);
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, TweetSearcher.indexWriter, true);
      if (newReader != null) {
        reader.close();
        reader = newReader;
      }
      IndexSearcher searcher = new IndexSearcher(reader);

      int topN = query.getCount();
      TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
      searcher.search(q, collector);
      ScoreDoc[] hits = collector.topDocs().scoreDocs;
      List<SearchResult> resultHits = new ArrayList<>();

      for (int i = 0; i < hits.length && i < topN; ++i) {
        int docId = hits[i].doc;
        Document d = searcher.doc(docId);
        resultHits.add(new SearchResult(String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name))));
      }
      return resultHits;
    }catch (Exception e){
      e.printStackTrace();
      return new ArrayList<>();
    }
  }
}
