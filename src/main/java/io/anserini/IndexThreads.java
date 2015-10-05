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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.lucene.benchmark.byTask.feeds.DocData;
import org.apache.lucene.benchmark.byTask.feeds.NoMoreDataException;
import org.apache.lucene.benchmark.byTask.feeds.TrecContentSource;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexWriter;

class IndexThreads {

  final IngestRatePrinter printer;
  final CountDownLatch startLatch = new CountDownLatch(1);
  final AtomicBoolean stop;
  final AtomicBoolean failed;
  final TrecContentSource tcs;
  final Thread[] threads;

  public IndexThreads(IndexWriter w, boolean positions,
      TrecContentSource tcs, int numThreads, int docCountLimit, boolean printDPS) throws IOException, InterruptedException {

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

    if (printDPS) {
      printer = new IngestRatePrinter(count, stop);
      printer.start();
    } else {
      printer = null;
    }
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
    if (printer != null) {
      printer.join();
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
      doc.add(new StringField("docname", dd.getName(), Store.YES));
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
            if(!(ex.getCause()!=null && ex.getCause().getMessage().contains("HTML framesets") )) {
              System.err.println("Failed: "+ex.getMessage());
            }
            continue;
          } catch (Exception e) {
            if(e instanceof NoMoreDataException) {
              break;
            } else {
              System.err.println("Failed: "+e.getMessage());
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
            System.out.println("Indexer: " + docCount + " docs... (" + (System.currentTimeMillis() - tStart)/1000.0 + " sec)");
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

  private static class IngestRatePrinter extends Thread {

    private final AtomicInteger count;
    private final AtomicBoolean stop;
    public IngestRatePrinter(AtomicInteger count, AtomicBoolean stop){
      this.count = count;
      this.stop = stop;
    }

    @Override
    public void run() {
      long time = System.currentTimeMillis();
      System.out.println("startIngest: " + time);
      final long start = time;
      int lastCount = count.get();
      while(!stop.get()) {
        try {
          Thread.sleep(200);
        } catch(Exception ex) {
        }
        int numDocs = count.get();

        double current = numDocs - lastCount;
        long now = System.currentTimeMillis();
        double seconds = (now-time) / 1000.0d;
        System.out.println("ingest: " + (current / seconds) + " " + (now - start));
        time = now;
        lastCount = numDocs;
      }
    }
  }
}
