package io.anserini.index;

/*
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

import io.anserini.document.ClueWeb09WarcRecord;
import io.anserini.document.ClueWeb12WarcRecord;
import io.anserini.document.Collection;
import io.anserini.document.Gov2Record;
import io.anserini.document.WarcRecord;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jsoup.Jsoup;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

/**
 * Indexer for Gov2, ClueWeb09, and ClueWeb12 corpara.
 */
public final class IndexWebCollection {

  private static final Logger LOG = LogManager.getLogger(IndexWebCollection.class);

  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";
  public static final String RESPONSE = "response";

  private final class IndexerThread extends Thread {

    final private Path inputWarcFile;

    final private IndexWriter writer;

    public IndexerThread(IndexWriter writer, Path inputWarcFile) throws IOException {
      this.writer = writer;
      this.inputWarcFile = inputWarcFile;
      setName(inputWarcFile.getFileName().toString());
    }

    private int indexWarcRecord(WarcRecord warcRecord) throws IOException {
      // see if it's a response record
      if (!RESPONSE.equals(warcRecord.type()))
        return 0;

      String id = warcRecord.id();

      org.jsoup.nodes.Document jDoc;
      try {
        jDoc = Jsoup.parse(warcRecord.content());
      } catch (java.lang.IllegalArgumentException iae) {
        LOG.error("Parsing document with JSoup failed, skipping document : " + id, iae);
        System.err.println(id);
        return 1;
      }

      String contents = jDoc.text();
      // don't index empty documents but count them
      if (contents.trim().length() == 0) {
        System.err.println(id);
        return 1;
      }

      // make a new, empty document
      Document document = new Document();

      // document ID
      document.add(new StringField(FIELD_ID, id, Field.Store.YES));

      FieldType fieldType = new FieldType();
      fieldType.setStored(true);
      fieldType.setStoreTermVectors(true);

        // entire document
      if (positions) {
          // Important, lucene 5 no longer has simple setIndexed option
          // set through index options
          fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
          fieldType.setStoreTermVectorPositions(true);
          document.add(new Field(FIELD_BODY, contents, fieldType));
      } else {
          fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
          document.add(new Field(FIELD_BODY, contents, fieldType));
      }
      writer.addDocument(document);
      return 1;

    }

    private int indexClueWeb12WarcFile() throws IOException {

      int i = 0;

      try (DataInputStream inStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(inputWarcFile, StandardOpenOption.READ)))) {
        // iterate through our stream
        ClueWeb12WarcRecord wDoc;
        while ((wDoc = ClueWeb12WarcRecord.readNextWarcRecord(inStream, ClueWeb12WarcRecord.WARC_VERSION)) != null) {
          i += indexWarcRecord(wDoc);
        }
      }
      return i;
    }

    private int indexClueWeb09WarcFile() throws IOException {

      int i = 0;

      try (DataInputStream inStream = new DataInputStream(new GZIPInputStream(Files.newInputStream(inputWarcFile, StandardOpenOption.READ)))) {
        // iterate through our stream
        ClueWeb09WarcRecord wDoc;
        while ((wDoc = ClueWeb09WarcRecord.readNextWarcRecord(inStream, ClueWeb09WarcRecord.WARC_VERSION)) != null) {
          i += indexWarcRecord(wDoc);
        }
      }
      return i;
    }

    private int indexGov2File() throws IOException {

      int i = 0;

      StringBuilder builder = new StringBuilder();

      boolean found = false;

      try (
              InputStream stream = new GZIPInputStream(Files.newInputStream(inputWarcFile, StandardOpenOption.READ), Gov2Record.BUFFER_SIZE);
              BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {


        for (; ; ) {
          String line = reader.readLine();
          if (line == null)
            break;

          line = line.trim();

          if (line.startsWith(Gov2Record.DOC)) {
            found = true;
            continue;
          }

          if (line.startsWith(Gov2Record.TERMINATING_DOC)) {
            found = false;
            WarcRecord gov2 = Gov2Record.parseGov2Record(builder);
            i += indexWarcRecord(gov2);
            builder.setLength(0);
          }

          if (found)
            builder.append(line).append(" ");
        }
      }

      return i;
    }

    @Override
    public void run() {
      {
        try {
          if (Collection.CW09.equals(collection)) {
            int addCount = indexClueWeb09WarcFile();
            System.out.println("*./" + inputWarcFile.getParent().getFileName().toString() + File.separator + inputWarcFile.getFileName().toString() + "  " + addCount);
          } else if (Collection.CW12.equals(collection)) {
            int addCount = indexClueWeb12WarcFile();
            System.out.println("./" + inputWarcFile.getParent().getFileName().toString() + File.separator + inputWarcFile.getFileName().toString() + "\t" + addCount);
          } else if (Collection.GOV2.equals(collection)) {
            int addCount = indexGov2File();
            System.out.println("./" + inputWarcFile.getParent().getFileName().toString() + File.separator + inputWarcFile.getFileName().toString() + "\t" + addCount);
          }

        } catch (IOException ioe) {
          LOG.error(Thread.currentThread().getName() + ": ERROR: unexpected IOException:", ioe);
        }
      }
    }
  }

  private final Path indexPath;
  private final Path docDir;

  private boolean positions = false;

  public void setPositions(boolean positions) {
    this.positions = positions;
  }

  private boolean optimize = false;

  public void setOptimize(boolean optimize) {
    this.optimize = optimize;
  }

  private int doclimit = -1;

  public void setDocLimit(int doclimit) {
    this.doclimit = doclimit;
  }

  private final Collection collection;

  public IndexWebCollection(String docsPath, String indexPath, Collection collection) throws IOException {

    this.indexPath = Paths.get(indexPath);
    if (!Files.exists(this.indexPath))
      Files.createDirectories(this.indexPath);

    docDir = Paths.get(docsPath);
    if (!Files.exists(docDir) || !Files.isReadable(docDir) || !Files.isDirectory(docDir)) {
      System.out.println("Document directory '" + docDir.toString() + "' does not exist or is not readable, please check the path");
      System.exit(1);
    }

    this.collection = collection;
  }


  static Deque<Path> discoverWarcFiles(Path p, final String suffix) {

    final Deque<Path> stack = new ArrayDeque<>();

    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {

      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

        Path name = file.getFileName();
        if (name != null && name.toString().endsWith(suffix))
          stack.add(file);
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if ("OtherData".equals(dir.getFileName().toString())) {
          LOG.info("Skipping: " + dir);
          return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException ioe) {
        LOG.error("Visiting failed for " + file.toString(), ioe);
        return FileVisitResult.SKIP_SUBTREE;
      }
    };

    try {
      Files.walkFileTree(p, fv);
    } catch (IOException e) {
      LOG.error("IOException during file visiting", e);
    }
    return stack;
  }


  public int indexWithThreads(int numThreads) throws IOException, InterruptedException {

    LOG.info("Indexing with " + numThreads + " threads to directory '" + indexPath.toAbsolutePath() + "'...");

    final Directory dir = FSDirectory.open(indexPath);

    final IndexWriterConfig iwc = new IndexWriterConfig(new EnglishAnalyzer());

    iwc.setSimilarity(new BM25Similarity());
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    iwc.setRAMBufferSizeMB(512);
    iwc.setUseCompoundFile(false);
    iwc.setMergeScheduler(new ConcurrentMergeScheduler());

    final IndexWriter writer = new IndexWriter(dir, iwc);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final String suffix = Collection.GOV2.equals(collection) ? ".gz" : ".warc.gz";
    final Deque<Path> warcFiles = discoverWarcFiles(docDir, suffix);

    if (doclimit > 0 && warcFiles.size() < doclimit)
      for (int i = doclimit; i < warcFiles.size(); i++)
        warcFiles.removeFirst();

    long totalWarcFiles = warcFiles.size();
    LOG.info(totalWarcFiles + " many " + suffix + " files found under the docs path : " + docDir.toString());


    for (int i = 0; i < 2000; i++) {
      if (!warcFiles.isEmpty())
        executor.execute(new IndexerThread(writer, warcFiles.removeFirst()));
      else {
        if (!executor.isShutdown()) {
          Thread.sleep(30000);
          executor.shutdown();
        }
        break;
      }
    }


    long first = 0;
    //add some delay to let some threads spawn by scheduler
    Thread.sleep(30000);


    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {

        final long completedTaskCount = executor.getCompletedTaskCount();

        LOG.info(String.format("%.2f percentage completed", (double) completedTaskCount / totalWarcFiles * 100.0d));

        if (!warcFiles.isEmpty())
          for (long i = first; i < completedTaskCount; i++) {
            if (!warcFiles.isEmpty())
              executor.execute(new IndexerThread(writer, warcFiles.removeFirst()));
            else {
              if (!executor.isShutdown())
                executor.shutdown();
            }
          }

        first = completedTaskCount;
        Thread.sleep(1000);
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    if (totalWarcFiles != executor.getCompletedTaskCount())
      throw new RuntimeException("totalWarcFiles = " + totalWarcFiles + " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());


    int numIndexed = writer.maxDoc();

    try {
      writer.commit();
      if (optimize)
        writer.forceMerge(1);
    } finally {
      writer.close();
    }

    return numIndexed;
  }

  public static void main(String[] args) throws IOException, InterruptedException {

    IndexArgs indexArgs = new IndexArgs();

    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: IndexWebCollection" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    IndexWebCollection indexer = new IndexWebCollection(indexArgs.input, indexArgs.index, indexArgs.collection);

    indexer.setPositions(indexArgs.positions);
    indexer.setOptimize(indexArgs.optimize);
    indexer.setDocLimit(indexArgs.doclimit);

    LOG.info("Index path: " + indexArgs.index);
    LOG.info("Threads: " + indexArgs.threads);
    LOG.info("Positions: " + indexArgs.positions);
    LOG.info("Optimize (merge segments): " + indexArgs.optimize);
    LOG.info("Doc limit: " + (indexArgs.doclimit == -1 ? "all docs" : "" + indexArgs.doclimit));

    LOG.info("Indexer: start");

    int numIndexed = indexer.indexWithThreads(indexArgs.threads);
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numIndexed + " documents indexed in " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
