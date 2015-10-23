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
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

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


  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {

    IndexArgs indexArgs = new IndexArgs();

    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: IndexGov2" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final String dirPath = indexArgs.index;
    final String dataDir = indexArgs.input;
    final int docCountLimit = indexArgs.doclimit;
    final int numThreads = indexArgs.threads;

    final boolean positions = indexArgs.positions;
    final boolean optimize = indexArgs.optimize;

    final Analyzer a = new EnglishAnalyzer();
    final TrecContentSource trecSource = createGov2Source(dataDir);
    final Directory dir = FSDirectory.open(Paths.get(dirPath));

    LOG.info("Index path: " + dirPath);
    LOG.info("Doc limit: " + (docCountLimit == -1 ? "all docs" : ""+docCountLimit));
    LOG.info("Threads: " + numThreads);
    LOG.info("Positions: " + positions);
    LOG.info("Optimize (merge segments): " + optimize);

    final IndexWriterConfig config = new IndexWriterConfig(a);

    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

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
    if (docCountLimit != -1 && writer.maxDoc() != docCountLimit) {
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

    // TODO: Doesn't compile for 5.3.1 - do we actually need this?
    //LOG.info("Indexer: at close: " + writer.segString());
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
