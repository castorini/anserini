/**
 * Anserini: An information retrieval toolkit built on Lucene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.index;

import io.anserini.collection.Collection;
import io.anserini.document.SourceDocument;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.CharArraySet;
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

public final class MultithreadedIndexer {
  private static final Logger LOG = LogManager.getLogger(MultithreadedIndexer.class);

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
      try {
        LuceneDocumentGenerator transformer = (LuceneDocumentGenerator) transformerClass.newInstance();
        transformer.config(args);
        transformer.setCounters(counters);

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

  private final IndexArgs args;
  private final Path indexPath;
  private final Path collectionPath;
  private final Class collectionClass;
  private final Class transformerClass;
  private final Collection collection;
  private final Counters counters;

  public MultithreadedIndexer(IndexArgs args) throws Exception {
    this.args = args;

    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    collectionPath = Paths.get(args.input);
    if (!Files.exists(collectionPath) || !Files.isReadable(collectionPath) || !Files.isDirectory(collectionPath)) {
      throw new RuntimeException("Document directory " + collectionPath.toString() +
          " does not exist or is not readable, please check the path");
    }

    this.transformerClass = Class.forName("io.anserini.index.generator." + args.generatorClass);

    this.collectionClass = Class.forName("io.anserini.collection." + args.collectionClass);
    collection = (Collection) this.collectionClass.newInstance();
    collection.setInputDir(collectionPath);

    this.counters = new Counters();
  }

  public int run() throws IOException, InterruptedException {
    int numThreads = args.threads;
    LOG.info("Indexing with " + numThreads + " threads to directory " + indexPath.toAbsolutePath() + "...");

    final Directory dir = FSDirectory.open(indexPath);
    final EnglishAnalyzer analyzer = args.keepstop ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);
    config.setSimilarity(new BM25Similarity());
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(512);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    final IndexWriter writer = new IndexWriter(dir, config);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(numThreads);
    final Deque<Path> indexFiles = collection.discoverFiles();

    long totalFiles = indexFiles.size();
    LOG.info(totalFiles + " files found at " + collectionPath.toString());
    for (int i = 0; i < totalFiles; i++) {
      executor.execute(new IndexerThread(writer, indexFiles.removeFirst()));
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
       LOG.info(String.format("%.2f percent completed",
           (double) executor.getCompletedTaskCount() / totalFiles * 100.0d));
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
      if (args.optimize)
        writer.forceMerge(1);
    } finally {
      writer.close();
    }

    LOG.info("Indexed documents: " + counters.indexedDocuments.get());
    LOG.info("Empty documents: " + counters.emptyDocuments.get());
    LOG.info("Errors: " + counters.errors.get());

    return numIndexed;
  }
}
