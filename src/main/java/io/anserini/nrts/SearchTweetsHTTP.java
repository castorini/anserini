package io.anserini.nrts;

import io.anserini.nrts.IndexTwitterStream.StatusField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class SearchTweetsHTTP implements Runnable {

    private static int topN = 20;
    private static List<Socket> pool = new LinkedList<Socket>();
    private IndexReader reader;

    public SearchTweetsHTTP() {
	try {
	    reader = DirectoryReader.open(NRTSearch.indexWriter, true);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static Map<String, String> readParamsIntoMap(String url) throws URISyntaxException {
	Map<String, String> params = new HashMap<String, String>();
	List<NameValuePair> result = URLEncodedUtils.parse(new URI(url), "UTF-8");
	for (NameValuePair nvp : result) {
	    params.put(nvp.getName(), nvp.getValue());
	}
	return params;
    }

    public static void processRequest(Socket request) {
	synchronized (pool) {
	    pool.add(pool.size(), request);
	    pool.notifyAll();
	}
    }

    @Override
    public void run() {
	while (true) {
	    Socket connection;
	    synchronized (pool) {
		while (pool.isEmpty()) {
		    try {
			pool.wait();
		    } catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		}
		connection = (Socket) pool.remove(0);
	    }

	    PrintStream out = null;
	    try {
		out = new PrintStream(connection.getOutputStream(), true, "UTF-8");
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

		String get = in.readLine();
		System.out.println(get);

		StringTokenizer st = new StringTokenizer(get);
		String method = st.nextToken();
		String version = "";
		if (method.equals("GET")) {
		    String query = st.nextToken();

		    if (st.hasMoreTokens()) {
			version = st.nextToken();
		    }

		    if (query.startsWith("/search?")) {
			if (version.startsWith("HTTP")) {
			    out.println("HTTP/1.0 200 OK");
			    Date now = new Date();
			    out.println("Date: " + now);
			    out.println("Server: JHTTP 1.0");
			    out.println("Content-Type: text/html");
			    out.println();
			}

			Map<String, String> params = readParamsIntoMap(query);
			Query q = new QueryParser(StatusField.TEXT.name, NRTSearch.ANALYZER).parse(params.get("query"));
			IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, NRTSearch.indexWriter, true);
			if (newReader != null) {
			    reader.close();
			    reader = newReader;
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;

			out.println("<HTML>");
			out.println("<HEAD><TITLE>Query</TITLE></HRAD>");
			out.println("<BODY>");
			out.println("<H1>Query = \"" + params.get("query") + "\". Found " + hits.length + " hits.</H1>");
			out.println("<OL>");
			for (int i = 0; i < hits.length; ++i) {
			    int docId = hits[i].doc;
			    Document d = searcher.doc(docId);
			    out.println("<LI>" + d.get(StatusField.TEXT.name) + "</LI>");
			}
			out.println("</OL>");
			out.println("</BODY></HTML>");
		    } else {
			if (version.startsWith("HTTP")) {
			    out.println("HTTP/1.0 404 File Not Found");
			    Date now = new Date();
			    out.println("Date: " + now);
			    out.println("Server: JHTTP 1.0");
			    out.println("Content-Type: text/html");
			    out.println();
			}
		    }
		} else {
		    if (version.startsWith("HTTP")) {
			out.println("HTTP/1.0 501 Not Implemented");
			Date now = new Date();
			out.println("Date: " + now);
			out.println("Server: JHTTP 1.0");
			out.println("Content-Type: text/html");
			out.println();
		    }
		}

	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (URISyntaxException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (ParseException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } finally {
		try {
		    out.close();
		    connection.close();
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		    e.printStackTrace();
		}
	    }
	}
    }
}
