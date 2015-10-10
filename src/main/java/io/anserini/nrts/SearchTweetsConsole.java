package io.anserini.nrts;

import io.anserini.nrts.IndexTwitterStream.StatusField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

public class SearchTweetsConsole implements Runnable {
    private static int topN = 5;

    @Override
    public void run() {
	try {
	    IndexReader reader = DirectoryReader.open(NRTSearch.indexWriter, true);
	    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

	    while (true) {
		String queryStr = br.readLine();
		Query q;
		try {
		    q = new QueryParser(StatusField.TEXT.name, NRTSearch.ANALYZER).parse(queryStr);
		} catch (ParseException e) {
		    System.err.println(e.getMessage());
		    continue;
		}
		IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, NRTSearch.indexWriter, true);
		if (newReader != null) {
		    reader.close();
		    reader = newReader;
		}
		IndexSearcher searcher = new IndexSearcher(reader);
		TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		System.out.println("Top " + hits.length + " tweets about topic \"" + queryStr + "\".");
		for (int i = 0; i < hits.length; ++i) {
		    int docId = hits[i].doc;
		    Document d = searcher.doc(docId);
		    String tweet = d.get(StatusField.TEXT.name).replace("\n", " ");
		    String time = (new SimpleDateFormat("HH:mm")).format(Long.parseLong(d.get(StatusField.EPOCH.name)) * 1000);
		    String name = d.get(StatusField.SCREEN_NAME.name);
		    System.out.println((i + 1) + ". " + name + "@" + time + " --> " + tweet);
		}
	    }
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

}
