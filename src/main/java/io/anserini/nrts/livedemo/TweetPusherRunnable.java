package io.anserini.nrts.livedemo;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;

import io.anserini.index.IndexTweets.StatusField;
import io.anserini.nrts.basicsearcher.TweetSearcher;
import io.anserini.nrts.basicsearcher.TweetStreamIndexer;
import io.anserini.nrts.livedemo.TweetClientAPI.TweetTopic;
import twitter4j.JSONObject;

public class TweetPusherRunnable implements Runnable{
  
  private static final Logger LOG = LogManager.getLogger(TweetClientAPI.class);
 
  static TweetTopic[] topics;
  static IndexWriter indexWriter;
  private IndexReader reader;
  int topN=5;
  Set pushedTweets=new HashSet();
  int dailylimit;
  boolean shutDown=false; // if reached, shutDown=true, and the pushbroker will sleep for the rest of the day
  String clientid;
  Client client= ClientBuilder.newClient();
  float interval;
  String api_base;
  
  TweetPusherRunnable(IndexWriter indexWriter,int dailylimit,String clientid,float interval,String api_base,TweetTopic[] topics){
    this.indexWriter=indexWriter;
    this.dailylimit=dailylimit;
    this.clientid=clientid;
    this.api_base=api_base;
    this.interval=interval;
    this.topics=topics;
    
  }

  @Override
  public void run() {
    LOG.info("Running TweetPusher Thread");
    try {
      while (true) {
        for (int i = 0; i < topics.length; i++) {
          // test
          try {
            Query q = new QueryParser(TweetStreamIndexer.StatusField.TEXT.name, TweetSearcher.ANALYZER)
                .parse(topics[i].query);
            try {
              reader = DirectoryReader.open(indexWriter, true);
            } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
            }
            IndexReader newReader = DirectoryReader.openIfChanged((DirectoryReader) reader, indexWriter, true);
            if (newReader != null) {
              reader.close();
              reader = newReader;
            }
            IndexSearcher searcher = new IndexSearcher(reader);

            TopScoreDocCollector collector = TopScoreDocCollector.create(topN);
            searcher.search(q, collector);
            ScoreDoc[] hits = collector.topDocs().scoreDocs;
            if (0 != hits.length) {
              LOG.info("_______________________________________________");
              LOG.info(
                  "Quering:" + topics[i].query + ", Found " + hits.length + " hits (including old, only push new)");
            }

            for (int j = 0; j < hits.length && j < topN; ++j) {
              int docId = hits[j].doc;
              Document d = searcher.doc(docId);
              if (pushedTweets.size() < dailylimit
                  && !pushedTweets.contains(d.get(TweetStreamIndexer.StatusField.ID.name))) {

                String targetURL = api_base + "tweet/" + topics[i].topid + "/"
                    + String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name)) + "/" + clientid;
                WebTarget webTarget = client.target(targetURL);
                JSONObject pushedTweet=new JSONObject();
                pushedTweet.put("topid", topics[i].topid).put("status.id",String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name))).put("clientid",clientid);
                Response postResponse = webTarget.request(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(pushedTweet, MediaType.APPLICATION_JSON));
                LOG.info("Tweet ID:" + String.valueOf(d.get(TweetStreamIndexer.StatusField.ID.name))
                    + " Tweet text:" + d.get(StatusField.TEXT.name));

                LOG.info("Push to " + targetURL + ":" + postResponse.getStatus());

                pushedTweets.add(d.get(TweetStreamIndexer.StatusField.ID.name)); //mark the pushed tweets

              } else if (pushedTweets.size() >= dailylimit) {
                shutDown = true;
                break;

              }

            }
          } catch (Exception e) {
            e.printStackTrace();
          } // Client pushes tweetid, topid to broker
          if (shutDown) //reached daily limit
            break;
        }
        if (!shutDown)
          Thread.sleep((long) (60000 * interval)); // Let the thread sleep for a while.
        if (shutDown) {
          Calendar now = Calendar.getInstance();
          Calendar tomorrow = Calendar.getInstance();
          tomorrow.set(Calendar.HOUR, 12);
          tomorrow.set(Calendar.MINUTE, 0x0);
          tomorrow.set(Calendar.SECOND, 0);
          LOG.info("Reached dailyLimit, sleep for the rest of the day");
          Thread.sleep((long) tomorrow.getTimeInMillis() - now.getTimeInMillis()); // reached dailyLimit, sleep for the rest of the day
          shutDown = false;

        }
      }
    } catch (InterruptedException e) {
      LOG.info("Thread interrupted.");
    }
  }

}
