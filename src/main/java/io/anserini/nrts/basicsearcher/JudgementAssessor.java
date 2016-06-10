package io.anserini.nrts.basicsearcher;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;

import io.anserini.index.twitter.TweetAnalyzer;
import io.anserini.nrts.basicsearcher.TweetSearcherAPI;
import io.anserini.nrts.basicsearcher.TweetServlet;
import twitter4j.RawStreamListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;

public class JudgementAssessor {
  private static final Logger LOG = LogManager.getLogger(JudgementAssessor.class);

  public void close() throws IOException {
  }
  
  public class StreamConnector implements Runnable {
    TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
    RawStreamListener rawListener = new RawStreamListener(){

      @Override
      public void onException(Exception ex) {
        // TODO Auto-generated method stub
        return;
      }

      @Override
      public void onMessage(String rawString) {
        // TODO Auto-generated method stub
        return;
      }
    
    };
      
    @Override
    public void run() {
      // TODO Auto-generated method stub
      twitterStream.addListener(rawListener);
      twitterStream.sample();
      
    }}

  public static void main(String[] args) throws IOException, InterruptedException, ParseException {
    
    JudgementAssessor nrtsearch = new JudgementAssessor();

    StreamConnector its = nrtsearch.new StreamConnector();
    Thread itsThread = new Thread(its);
    itsThread.start();
    

    

    LOG.info("Starting HTTP server on port 8080");
    HandlerList mainHandler = new HandlerList();
    Server server = new Server(8080);

    ResourceHandler resource_handler = new ResourceHandler();
    
    resource_handler.setResourceBase("src/main/java/io/anserini/nrts/public");
    resource_handler.setWelcomeFiles(new String[]{"index_judge.html"});

    ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);
    handler.setContextPath("/");
    ServletHolder jerseyServlet = new ServletHolder(ServletContainer.class);
    jerseyServlet.setInitParameter("jersey.config.server.provider.classnames",JudgementAssessorAPI.class.getCanonicalName());
    handler.addServlet(jerseyServlet,"/*");

    mainHandler.addHandler(resource_handler);
    mainHandler.addHandler(handler);
    server.setHandler(mainHandler);
    try {
      server.start();
      LOG.info("Accepting connections on port 8080");
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    server.join();
    itsThread.join();
    nrtsearch.close();
  }
}
