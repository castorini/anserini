package io.anserini;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.InfoStream;
import org.apache.lucene.util.PrintStreamInfoStream;

public final class TrecIngester {
  private static TrecContentSource createTrecSource(String dataDir) {
    TrecContentSource tcs = new TrecContentSource();
    Properties props = new Properties();
    props.setProperty("print.props", "false");
    props.setProperty("content.source.verbose", "false");
    props.setProperty("content.source.excludeIteration", "true");
    props.setProperty("docs.dir", dataDir);
    props.setProperty("trec.doc.parser", "org.apache.lucene.benchmark.byTask.feeds.TrecGov2Parser");
    props.setProperty("content.source.forever", "false");
    tcs.setConfig(new Config(props));
    try {
      tcs.resetInputs();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return tcs;
  }

  private static final String INPUT_OPTION = "input";
  private static final String INDEX_OPTION = "index";
  private static final String DOCLIMIT_OPTION = "doclimit";
  private static final String THREADS_OPTION = "threads";
  private static final String VERBOSE_OPTION = "verbose";
  private static final String DPS_OPTION = "dps";
  private static final String UPDATE_OPTION = "update";
  private static final String POSITIONS_OPTION = "positions";

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();
    options.addOption(OptionBuilder.withArgName("path")
        .hasArg().withDescription("input data path").create(INPUT_OPTION));
    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("output index path").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("number of indexer threads").create(THREADS_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("max number of documents to index (-1 to index everything)")
        .create(DOCLIMIT_OPTION));

    options.addOption(VERBOSE_OPTION, false, "verbose");
    options.addOption(DPS_OPTION, false, "print dps");
    options.addOption(POSITIONS_OPTION, false, "index positions");

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(INPUT_OPTION) || !cmdline.hasOption(INDEX_OPTION)
        || !cmdline.hasOption(THREADS_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.setWidth(100);
      formatter.printHelp(TrecIngester.class.getCanonicalName(), options);
      System.exit(-1);
    }

    final String dirPath = cmdline.getOptionValue(INDEX_OPTION);
    final String dataDir = cmdline.getOptionValue(INPUT_OPTION);
    final int docCountLimit = cmdline.hasOption(DOCLIMIT_OPTION) ? Integer.parseInt(cmdline.getOptionValue(DOCLIMIT_OPTION)) : -1;
    final int numThreads = Integer.parseInt(cmdline.getOptionValue(THREADS_OPTION));

    final boolean verbose = cmdline.hasOption(VERBOSE_OPTION);
    final boolean printDPS = cmdline.hasOption(DPS_OPTION);
    final boolean doUpdate = cmdline.hasOption(UPDATE_OPTION);
    final boolean positions = cmdline.hasOption(POSITIONS_OPTION);

    final Analyzer a = new EnglishAnalyzer();
    final TrecContentSource trecSource = createTrecSource(dataDir);
    final Directory dir = FSDirectory.open(Paths.get(dirPath));

    System.out.println("Index path: " + dirPath);
    System.out.println("Doc count limit: " + (docCountLimit == -1 ? "all docs" : ""+docCountLimit));
    System.out.println("Threads: " + numThreads);
    System.out.println("Verbose: " + (verbose ? "yes" : "no"));
    System.out.println("Positions: " + (positions ? "yes" : "no"));

    if (verbose) {
      InfoStream.setDefault(new PrintStreamInfoStream(System.out));
    }

    final IndexWriterConfig iwc = new IndexWriterConfig(a);

    if (doUpdate) {
      iwc.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
    } else {
      iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    }

    System.out.println("IW config=" + iwc);

    final IndexWriter w = new IndexWriter(dir, iwc);
    IndexThreads threads = new IndexThreads(w, positions, trecSource, numThreads, docCountLimit, printDPS);
    System.out.println("\nIndexer: start");

    final long t0 = System.currentTimeMillis();

    threads.start();

    while (!threads.done()) {
      Thread.sleep(100);
    }
    threads.stop();

    final long t1 = System.currentTimeMillis();
    System.out.println("\nIndexer: indexing done (" + (t1-t0)/1000.0 + " sec); total " + w.maxDoc() + " docs");
    if (!doUpdate && docCountLimit != -1 && w.maxDoc() != docCountLimit) {
      throw new RuntimeException("w.maxDoc()=" + w.maxDoc() + " but expected " + docCountLimit);
    }
    if (threads.failed.get()) {
      throw new RuntimeException("exceptions during indexing");
    }


    final long t2;
    t2 = System.currentTimeMillis();

    final Map<String,String> commitData = new HashMap<String,String>();
    commitData.put("userData", "multi");
    w.setCommitData(commitData);
    w.commit();
    final long t3 = System.currentTimeMillis();
    System.out.println("\nIndexer: commit multi (took " + (t3-t2)/1000.0 + " sec)");

    System.out.println("\nIndexer: at close: " + w.segString());
    final long tCloseStart = System.currentTimeMillis();
    w.close();
    System.out.println("\nIndexer: close took " + (System.currentTimeMillis() - tCloseStart)/1000.0 + " sec");
    dir.close();
    final long tFinal = System.currentTimeMillis();
    System.out.println("\nIndexer: finished (" + (tFinal-t0)/1000.0 + " sec)");
    System.out.println("\nIndexer: net bytes indexed " + threads.getBytesIndexed());
    System.out.println("\nIndexer: " + (threads.getBytesIndexed()/1024./1024./1024./((tFinal-t0)/3600000.)) + " GB/hour plain text");
  }
}
