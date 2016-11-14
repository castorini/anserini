package io.anserini.rts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.MMapDirectory;

public class Indexer {
  static Analyzer ANALYZER = new WhitespaceAnalyzer();
  private static Thread itsThread;
  static IndexWriter indexWriter;

  public static String StartIndexing(String dir) throws IOException {
    FileUtils.deleteDirectory(new File(dir));
    Directory index = new MMapDirectory(Paths.get(dir));
    IndexWriterConfig config = new IndexWriterConfig(ANALYZER);
    indexWriter = new IndexWriter(index, config);
    TRECIndexerRunnable its = new TRECIndexerRunnable(indexWriter);
    itsThread = new Thread(its);
    itsThread.start();
    return dir;
  }

  public static void close() throws IOException {
    indexWriter.close();
  }

  public static void join() throws InterruptedException {
    itsThread.join();
  }

}
