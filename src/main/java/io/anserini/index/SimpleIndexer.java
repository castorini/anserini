/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.analysis.AnalyzerMap;
import io.anserini.analysis.HuggingFaceTokenizerAnalyzer;
import io.anserini.index.IndexCollection.Args;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import io.anserini.collection.FileSegment;
import io.anserini.collection.JsonCollection;
import io.anserini.index.generator.GeneratorException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

public class SimpleIndexer {
  private static final Logger LOG = LogManager.getLogger(SimpleIndexer.class);

  private final Path indexPath;
  private final IndexWriter writer;
  private final Analyzer analyzer;
  private final LuceneDocumentGenerator generator;

  public SimpleIndexer(String indexPath) throws IOException {
    this.indexPath = Paths.get(indexPath);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }

    analyzer = DefaultEnglishAnalyzer.newDefaultInstance();
    generator = new DefaultLuceneDocumentGenerator();
    final Directory dir = FSDirectory.open(this.indexPath);
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);

    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(2048);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    writer = new IndexWriter(dir, config);
  }

  public SimpleIndexer(String[] argv) throws Exception {
    this(parseArgs(argv));
  }

  private static Args parseArgs(String[] argv) throws CmdLineException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + SimpleIndexer.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      throw e;
    }
    return args;
  }

  @SuppressWarnings("unchecked")
  public SimpleIndexer(Args args) throws Exception {
    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }
    Class generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    generator = (LuceneDocumentGenerator) 
        generatorClass.getDeclaredConstructor(Args.class).newInstance(args);
    analyzer = getAnalyzer(args);

    final Directory dir = FSDirectory.open(this.indexPath);
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);

    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(2048);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    writer = new IndexWriter(dir, config);
  }

  private Analyzer getAnalyzer(Args args) throws Exception {
    if (args.analyzeWithHuggingFaceTokenizer != null){
      LOG.info("Bert Tokenizer");
      return new HuggingFaceTokenizerAnalyzer(args.analyzeWithHuggingFaceTokenizer);
    } else if (AnalyzerMap.analyzerMap.containsKey(args.language)){
      LOG.info("Language: " + args.language);
      return AnalyzerMap.getLanguageSpecificAnalyzer(args.language);
    } else if (args.pretokenized || args.language.equals("sw")) {
      LOG.info("Pretokenized");
      return new WhitespaceAnalyzer();
    } else {
      // Default to English
      LOG.info("Language: en");
      LOG.info("Stemmer: " + args.stemmer);
      LOG.info("Keep stopwords? " + args.keepStopwords);
      LOG.info("Stopwords file: " + args.stopwords);
      return DefaultEnglishAnalyzer.fromArguments(args.stemmer, args.keepStopwords, args.stopwords);
    }
  }

  @SuppressWarnings("unchecked")
  public boolean addDocument(String raw) {
    try {
      JsonCollection.Document doc = JsonCollection.Document.fromString(raw);
      writer.addDocument(generator.createDocument(doc));
    } catch (GeneratorException e) {
      e.printStackTrace();
      return false;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }

    return true;
  }

  public void close() {
    close(false);
  }

  public void close(boolean optimize) {
    // Do a final commit.
    try {
      if (writer != null) {
        writer.commit();
        if (optimize) {
          writer.forceMerge(1);
        }
      }
    } catch (IOException e) {
      // It is possible that this happens... but nothing much we can do at this point,
      // so just log the error and move on.
      LOG.error(e.getMessage());
    } finally {
      try {
        if (writer != null) {
          writer.close();
        }
      } catch (IOException e) {
        // It is possible that this happens... but nothing much we can do at this point,
        // so just log the error and move on.
        LOG.error(e.getMessage());
      }
    }
  }

  // Main here exists only as a convenience. Once we're happy with the APIs, there should *not* be
  // a separate code entry point; one less thing to debug, one less thing to go wrong.
  public static void main(String[] argv) throws Exception {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: " + SimpleIndexer.class.getSimpleName() +
          parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    JsonCollection collection = new JsonCollection(Paths.get(args.input));

    int cnt = 0;
    SimpleIndexer indexer = new SimpleIndexer(args);

    LOG.info("input: " + args.input);
    LOG.info("collection: " + args.index);

    for (FileSegment<JsonCollection.Document> segment : collection ) {
      for (JsonCollection.Document doc : segment) {
        //System.out.println(doc.raw());
        indexer.addDocument(doc.raw());
        cnt++;
        if (cnt % 100000 == 0) {
          LOG.info(cnt + " docs indexed");
        }
      }
      segment.close();
    }

    indexer.close();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info(String.format("Total %,d documents indexed in %s", cnt,
        DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss")));

  }
}