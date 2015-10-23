package io.anserini.nrts;

import io.anserini.nrts.TweetStreamIndexer.StatusField;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class TweetServlet extends HttpServlet {

  // TODO Auto-generated serialVersionUID
  private static final long serialVersionUID = 1L;
  private IndexReader reader;

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (request.getRequestURI().equals("/search")) {
      response.setStatus(HttpServletResponse.SC_OK);
      response.setContentType("text/html");
      Query q;
      try {
        q = new QueryParser(StatusField.TEXT.name, TweetSearcher.ANALYZER).parse(request.getParameter("query"));
        try {
          reader = DirectoryReader.open(TweetSearcher.indexWriter, true);
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
        IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, TweetSearcher.indexWriter,
            true);
        if (newReader != null) {
          reader.close();
          reader = newReader;
        }
        IndexSearcher searcher = new IndexSearcher(reader);
        
        int topN;
        if (request.getParameter("top") != null) {
          topN = Integer.parseInt(request.getParameter("top"));
        } else {
          // TODO configurable, default(parameter unspecified in url) topN = 20
          topN = 20;
        }
        TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        response.getWriter().println("<HTML>");
        response.getWriter().println("<HEAD><TITLE>Query</TITLE></HEAD>");
        response.getWriter().println("<BODY>");
        response.getWriter()
            .println("<H1>Query = \"" + request.getParameter("query") + "\". Found " + hits.length + " hits.</H1>");
        response.getWriter().println("<OL>");
        for (int i = 0; i < hits.length; ++i) {
          int docId = hits[i].doc;
          Document d = searcher.doc(docId);
          response.getWriter().println("<LI>" + d.get(StatusField.TEXT.name) + "</LI>");
        }
        response.getWriter().println("</OL>");
        response.getWriter().println("</BODY></HTML>");
      } catch (ParseException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    } else {
      response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
  }
}
