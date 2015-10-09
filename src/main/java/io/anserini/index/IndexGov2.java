package io.anserini.index;

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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.benchmark.byTask.utils.Config;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public final class IndexGov2 {
  private static final Logger LOG = LogManager.getLogger(IndexGov2.class);

  private static TrecContentSource createGov2Source(String dataDir) {
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
  private static final String UPDATE_OPTION = "update";
  private static final String POSITIONS_OPTION = "positions";
  private static final String OPTIMIZE_OPTION = "optimize";

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

    options.addOption(POSITIONS_OPTION, false, "index positions");
    options.addOption(OPTIMIZE_OPTION, false, "merge all index segments");

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
      formatter.printHelp(IndexGov2.class.getCanonicalName(), options);
      System.exit(-1);
    }

    final String dirPath = cmdline.getOptionValue(INDEX_OPTION);
    final String dataDir = cmdline.getOptionValue(INPUT_OPTION);
    final int docCountLimit = cmdline.hasOption(DOCLIMIT_OPTION) ? Integer.parseInt(cmdline.getOptionValue(DOCLIMIT_OPTION)) : -1;
    final int numThreads = Integer.parseInt(cmdline.getOptionValue(THREADS_OPTION));

    final boolean doUpdate = cmdline.hasOption(UPDATE_OPTION);
    final boolean positions = cmdline.hasOption(POSITIONS_OPTION);
    final boolean optimize = cmdline.hasOption(OPTIMIZE_OPTION);

    final Analyzer a = new EnglishAnalyzer();
    final TrecContentSource trecSource = createGov2Source(dataDir);
    final Directory dir = FSDirectory.open(Paths.get(dirPath));

    LOG.info("Index path: " + dirPath);
    LOG.info("Doc limit: " + (docCountLimit == -1 ? "all docs" : ""+docCountLimit));
    LOG.info("Threads: " + numThreads);
    LOG.info("Positions: " + positions);
    LOG.info("Optimize (merge segments): " + optimize);

    final IndexWriterConfig config = new IndexWriterConfig(a);

    if (doUpdate) {
      config.setOpenMode(IndexWriterConfig.OpenMode.APPEND);
    } else {
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    }

    final IndexWriter writer = new IndexWriter(dir, config);
    Gov2IndexThreads threads = new Gov2IndexThreads(writer, positions, trecSource, numThreads, docCountLimit);
    LOG.info("Indexer: start");

    final long t0 = System.currentTimeMillis();

    threads.start();

    while (!threads.done()) {
      Thread.sleep(100);
    }
    threads.stop();

    final long t1 = System.currentTimeMillis();
    LOG.info("Indexer: indexing done (" + (t1-t0)/1000.0 + " sec); total " + writer.maxDoc() + " docs");
    if (!doUpdate && docCountLimit != -1 && writer.maxDoc() != docCountLimit) {
      throw new RuntimeException("w.maxDoc()=" + writer.maxDoc() + " but expected " + docCountLimit);
    }
    if (threads.failed.get()) {
      throw new RuntimeException("exceptions during indexing");
    }


    final long t2;
    t2 = System.currentTimeMillis();

    final Map<String,String> commitData = new HashMap<String,String>();
    commitData.put("userData", "multi");
    writer.setCommitData(commitData);
    writer.commit();
    final long t3 = System.currentTimeMillis();
    LOG.info("Indexer: commit multi (took " + (t3-t2)/1000.0 + " sec)");

    if (optimize) {
      LOG.info("Indexer: merging all segments");
      writer.forceMerge(1);
      final long t4 = System.currentTimeMillis();
      LOG.info("Indexer: segments merged (took " + (t4 - t3) / 1000.0 + " sec)");
    }

    LOG.info("Indexer: at close: " + writer.segString());
    final long tCloseStart = System.currentTimeMillis();
    writer.close();
    LOG.info("Indexer: close took " + (System.currentTimeMillis() - tCloseStart)/1000.0 + " sec");
    dir.close();
    final long tFinal = System.currentTimeMillis();
    LOG.info("Indexer: finished (" + (tFinal-t0)/1000.0 + " sec)");
    LOG.info("Indexer: net bytes indexed " + threads.getBytesIndexed());
    LOG.info("Indexer: " + (threads.getBytesIndexed()/1024./1024./1024./((tFinal-t0)/3600000.)) + " GB/hour plain text");
  }
}
