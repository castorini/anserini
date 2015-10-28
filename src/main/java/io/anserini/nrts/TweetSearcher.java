package io.anserini.nrts;

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
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import io.anserini.nrts.TweetServlet;
import io.anserini.index.twitter.TweetAnalyzer;

public class TweetSearcher {
  private static final Logger LOG = LogManager.getLogger(TweetSearcher.class);

  private static final String INDEX_OPTION = "index";
  private static final String PORT_OPTION = "port";

  public static Directory index;
  public static IndexWriter indexWriter;
  public static final Analyzer ANALYZER = new TweetAnalyzer();

  public TweetSearcher(String dir) throws IOException {
    index = new MMapDirectory(Paths.get(dir));
    IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
    indexWriter = new IndexWriter(index, config);
  }

  public void close() throws IOException {
    indexWriter.close();
  }

  public static void main(String[] args) throws IOException, InterruptedException, ParseException {
    Options options = new Options();
    options.addOption(INDEX_OPTION, true, "index path");
    options.addOption(PORT_OPTION, true, "port");

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (org.apache.commons.cli.ParseException e) {
      System.err.println("Error parsing command line: " + e.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(TweetSearcher.class.getName(), options);
      System.exit(-1);
    }

    int port = cmdline.hasOption(PORT_OPTION) ? Integer.parseInt(cmdline.getOptionValue(PORT_OPTION)) : 8080;
    TweetSearcher nrtsearch = new TweetSearcher(cmdline.getOptionValue(INDEX_OPTION));

    TweetStreamIndexer its = new TweetStreamIndexer();
    Thread itsThread = new Thread(its);
    itsThread.start();

    // http://localhost:port/search?query=happy
    LOG.info("Starting HTTP server on port " + port);

    HandlerList mainHandler = new HandlerList();

    Server server = new Server(port);
    ResourceHandler resource_handler = new ResourceHandler();
    resource_handler.setResourceBase("src/main/java/io/anserini/nrts/public");

    ServletHandler handler = new ServletHandler();
    handler.addServletWithMapping(TweetServlet.class, "/search");

    mainHandler.addHandler(resource_handler);
    mainHandler.addHandler(handler);
    server.setHandler(mainHandler);
    try {
      server.start();
      LOG.info("Accepting connections on port " + port);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    server.join();
    itsThread.join();
    nrtsearch.close();
  }
}
