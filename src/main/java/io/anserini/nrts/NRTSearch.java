package io.anserini.nrts;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

import cc.twittertools.index.TweetAnalyzer;

public class NRTSearch {
    private static final String UI_OPTION = "ui";
    private static final String DIR_OPTION = "dir";

    private static final String UI_CONSOLE = "console";
    private static final String UI_WEB = "web";

    public static Directory index;
    public static IndexWriter indexWriter;
    public static final Analyzer ANALYZER = new TweetAnalyzer();

    public NRTSearch(String dir) throws IOException {
	index = new MMapDirectory(Paths.get(dir));
	IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
	indexWriter = new IndexWriter(index, config);
    }

    public void close() throws IOException {
	indexWriter.close();
    }

    public static void main(String[] args) throws IOException, InterruptedException, ParseException {
	Options options = new Options();
	options.addOption(UI_OPTION, true, "UI Type (console, web)");
	options.addOption(DIR_OPTION, true, "Directory path for the index");

	CommandLine cmdline = null;
	CommandLineParser parser = new GnuParser();
	try {
	    cmdline = parser.parse(options, args);
	} catch (org.apache.commons.cli.ParseException e) {
	    System.err.println("Error parsing command line: " + e.getMessage());
	    System.exit(-1);
	}

	if (!cmdline.hasOption(UI_OPTION) || !cmdline.hasOption(DIR_OPTION)) {
	    HelpFormatter formatter = new HelpFormatter();
	    formatter.printHelp(NRTSearch.class.getName(), options);
	    System.exit(-1);
	}

	NRTSearch nrtsearch = new NRTSearch(cmdline.getOptionValue(DIR_OPTION));

	IndexTwitterStream its = new IndexTwitterStream();
	Thread itsThread = new Thread(its);
	itsThread.start();

	if (cmdline.getOptionValue(UI_OPTION).equals(UI_CONSOLE)) {
	    SearchTweetsConsole st = new SearchTweetsConsole();
	    Thread stThread = new Thread(st);
	    stThread.start();
	    itsThread.join();
	    stThread.join();
	} else if (cmdline.getOptionValue(UI_OPTION).equals(UI_WEB)) {
	    // http://localhost:8080/search?query=happy
	    HTTPServer webServer = new HTTPServer(8080);
	    webServer.start();
	    itsThread.join();
	    webServer.join();
	}

	nrtsearch.close();
    }
}
