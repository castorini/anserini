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

import io.anserini.collection.Collection;
import io.anserini.document.SourceDocument;
import io.anserini.document.JsoupTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public final class IndexThreads {
  private static final Logger LOG = LogManager.getLogger(IndexThreads.class);

  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";

  public final class Counters {
    public AtomicLong indexedDocuments = new AtomicLong();
    public AtomicLong emptyDocuments = new AtomicLong();
    public AtomicLong errors = new AtomicLong();
  }

  private final class IndexerThread extends Thread {
    final private Path inputFile;
    final private IndexWriter writer;

    public IndexerThread(IndexWriter writer, Path inputFile) throws IOException {
      this.writer = writer;
      this.inputFile = inputFile;
      setName(inputFile.getFileName().toString());
    }

    @Override
    public void run() {
      JsoupTransformer transformer = new JsoupTransformer();
      transformer.setKeepStopwords(keepstopwords);
      transformer.setStorePositions(positions);
      transformer.setStoreDocVectors(docVectors);
      transformer.setCounters(counters);

      try {
        int cnt = 0;
        Collection collection = (Collection) collectionClass.newInstance();
        collection.prepareInput(inputFile);
        while (collection.hasNext()) {
          SourceDocument d = (SourceDocument) collection.next();
          if (d == null || !d.indexable()) {
            continue;
          }
          Document doc = transformer.transform(d);
          if (doc != null) {
            writer.addDocument(doc);
            cnt++;
          }
        }
        collection.finishInput();
        LOG.info(inputFile.getParent().getFileName().toString() + File.separator +
            inputFile.getFileName().toString() + ": " + cnt + " docs added.");
        counters.indexedDocuments.addAndGet(cnt);
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }
    }
  }

  private final Path indexPath;
  private final Path docDir;
  private final Class collectionClass;
  private final Collection collection;
  private final Counters counters;

  private boolean keepstopwords = false;
  private boolean positions = false;
  private boolean docVectors = false;
  private boolean optimize = false;

  public void setKeepstopwords(boolean keepstopwords) {
    this.keepstopwords = keepstopwords;
  }

  public void setPositions(boolean positions) {
    this.positions = positions;
  }

  public void setDocVectors(boolean docVectors) {
    this.docVectors = docVectors;
  }

  public void setOptimize(boolean optimize) {
    this.optimize = optimize;
  }

  public IndexThreads(String docsPath, String indexPath, String collectionClass)
          throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InstantiationException {

    this.indexPath = Paths.get(indexPath);
    if (!Files.exists(this.indexPath))
      Files.createDirectories(this.indexPath);

    docDir = Paths.get(docsPath);
    if (!Files.exists(docDir) || !Files.isReadable(docDir) || !Files.isDirectory(docDir)) {
      System.out.println("Document directory " + docDir.toString() + " does not exist or is not readable, please check the path");
      System.exit(1);
    }

    this.collectionClass = Class.forName("io.anserini.collection." + collectionClass);
    collection = (Collection) this.collectionClass.newInstance();
    collection.setInputDir(docDir);

    this.counters = new Counters();
  }

  public int indexWithThreads(int numThreads) throws IOException, InterruptedException {
    LOG.info("Indexing with " + numThreads + " threads to directory " + indexPath.toAbsolutePath() + "...");

    final Directory dir = FSDirectory.open(indexPath);

    final EnglishAnalyzer ea = keepstopwords ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    final IndexWriterConfig iwc = new IndexWriterConfig(ea);

    iwc.setSimilarity(new BM25Similarity());
    iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    iwc.setRAMBufferSizeMB(512);
    iwc.setUseCompoundFile(false);
    iwc.setMergeScheduler(new ConcurrentMergeScheduler());

    final IndexWriter writer = new IndexWriter(dir, iwc);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final Deque<Path> indexFiles = collection.discoverFiles();

    long totalFiles = indexFiles.size();
    LOG.info(totalFiles + " files found at " + docDir.toString());

    for (int i = 0; i < totalFiles; i++) {
      //if (!indexFiles.isEmpty())
        executor.execute(new IndexerThread(writer, indexFiles.removeFirst()));
//      else {
//        if (!executor.isShutdown()) {
//          Thread.sleep(30000);
//          executor.shutdown();
//        }
//        break;
//      }
    }

    executor.shutdown();
    //long first = 0;
    //add some delay to let some threads spawn by scheduler
//    Thread.sleep(30000);

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {

        long completedTaskCount = executor.getCompletedTaskCount();

        LOG.info(String.format("%.2f percent completed", (double) completedTaskCount / totalFiles * 100.0d));

//        if (!indexFiles.isEmpty())
//          for (long i = first; i < completedTaskCount; i++) {
//            if (!indexFiles.isEmpty())
//              executor.execute(new IndexerThread(writer, indexFiles.removeFirst()));
//            else {
//              if (!executor.isShutdown())
//                executor.shutdown();
//            }
//          }

        //first = completedTaskCount;
        //Thread.sleep(1000);
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    if (totalFiles != executor.getCompletedTaskCount()) {
      throw new RuntimeException("totalFiles = " + totalFiles +
          " is not equal to completedTaskCount =  " + executor.getCompletedTaskCount());
    }

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
}
