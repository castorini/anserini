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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;

class Gov2IndexThreads {
  private static final Logger LOG = LogManager.getLogger(Gov2IndexThreads.class);

  final CountDownLatch startLatch = new CountDownLatch(1);
  final AtomicBoolean stop;
  final AtomicBoolean failed;
  final TrecContentSource tcs;
  final Thread[] threads;

  public Gov2IndexThreads(IndexWriter w, boolean positions, TrecContentSource tcs, int numThreads, int docCountLimit)
      throws IOException, InterruptedException {

    this.tcs = tcs;
    threads = new Thread[numThreads];

    final CountDownLatch stopLatch = new CountDownLatch(numThreads);
    final AtomicInteger count = new AtomicInteger();
    stop = new AtomicBoolean(false);
    failed = new AtomicBoolean(false);

    for(int thread=0;thread<numThreads;thread++) {
      threads[thread] = new IndexThread(startLatch, stopLatch, w, positions, tcs, docCountLimit, count, stop, failed);
      threads[thread].start();
    }

    Thread.sleep(10);
  }

  public void start() {
    startLatch.countDown();
  }

  public long getBytesIndexed() {
    return tcs.getBytesCount();
  }

  public void stop() throws InterruptedException, IOException {
    stop.getAndSet(true);
    for(Thread t : threads) {
      t.join();
    }
    tcs.close();
  }

  public boolean done() {
    for(Thread t: threads) {
      if (t.isAlive()) {
        return false;
      }
    }

    return true;
  }

  private static class IndexThread extends Thread {
    private final TrecContentSource tcs;
    private final int numTotalDocs;
    private final IndexWriter w;
    private final AtomicInteger count;
    private final CountDownLatch startLatch;
    private final CountDownLatch stopLatch;
    private final AtomicBoolean failed;
    private final boolean positions;

    public IndexThread(CountDownLatch startLatch, CountDownLatch stopLatch, IndexWriter w, boolean positions,
        TrecContentSource tcs, int numTotalDocs, AtomicInteger count,
        AtomicBoolean stop, AtomicBoolean failed) {
      this.startLatch = startLatch;
      this.stopLatch = stopLatch;
      this.w = w;
      this.positions = positions;
      this.tcs = tcs;
      this.numTotalDocs = numTotalDocs;
      this.count = count;
      this.failed = failed;
    }

    private Document getDocumentFromDocData(DocData dd) {
      Document doc = new Document();
      doc.add(new StringField("docid", dd.getName(), Store.YES));
      if(positions) {
        doc.add(new TextField("body", dd.getTitle(), Store.NO));
        doc.add(new TextField("body", dd.getBody(), Store.NO));
      } else {
        doc.add(new NoPositionsTextField("body", dd.getTitle()));
        doc.add(new NoPositionsTextField("body", dd.getBody()));
      }
      return doc;
    }

    @Override
    public void run() {
      try {
        final long tStart = System.currentTimeMillis();

        try {
          startLatch.await();
        } catch (InterruptedException ie) {
          Thread.currentThread().interrupt();
          return;
        }

        while (true) {
          DocData dd = new DocData();

          try {
            dd = tcs.getNextDocData(dd);
          } catch (IOException ex) {
            // The HTML parser used with this trec parser doesn't support HTML pages with framesets.
            if (!(ex.getCause() != null && ex.getCause().getMessage().contains("HTML framesets"))) {
              LOG.error("getNextDocData exception: " + ex.getMessage());
            }
            continue;
          } catch (Exception e) {
            if (e instanceof NoMoreDataException) {
              break;
            } else {
              LOG.error("getNextDocData exception: " + e.getMessage());
              continue;
            }
          }

          Document doc = getDocumentFromDocData(dd);
          if (doc == null) {
            break;
          }
          int docCount = count.incrementAndGet();
          if (numTotalDocs != -1 && docCount > numTotalDocs) {
            break;
          }
          if ((docCount % 100000) == 0) {
            LOG.info("Indexer: " + docCount + " docs... (" + (System.currentTimeMillis() - tStart)/1000.0 + " sec)");
          }
          w.addDocument(doc);
        }

      } catch (Exception e) {
        failed.set(true);
        throw new RuntimeException(e);
      } finally {
        stopLatch.countDown();
      }
    }
  }
}
