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

import cc.twittertools.index.TweetAnalyzer;

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
    TweetSearcherServer webServer = new TweetSearcherServer(port);
    webServer.start();
    itsThread.join();
    webServer.join();

    nrtsearch.close();
  }
}
