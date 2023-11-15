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

import com.fasterxml.jackson.databind.JsonNode;
import io.anserini.analysis.AnalyzerMap;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.analysis.HuggingFaceTokenizerAnalyzer;
import io.anserini.collection.FileSegment;
import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexCollection.Args;
import io.anserini.index.generator.GeneratorException;
import io.anserini.index.generator.LuceneDocumentGenerator;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
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
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class SimpleIndexer {
  private static final Logger LOG = LogManager.getLogger(SimpleIndexer.class);

  private final Path indexPath;
  private final IndexWriter writer;
  private final Analyzer analyzer;
  private final LuceneDocumentGenerator generator;
  private final int threads;

  private static Args parseArgs(String[] argv) throws CmdLineException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args);
    parser.parseArgument(argv);

    return args;
  }

  public SimpleIndexer(String[] argv) throws Exception {
    this(parseArgs(argv));
  }

  public SimpleIndexer(String indexPath) throws Exception {
    this(new String[] {
        "-input", "",
        "-index", indexPath,
        "-collection", "JsonCollection"});
  }

  public SimpleIndexer(String indexPath, int threads) throws Exception {
    this(new String[] {
        "-input", "",
        "-index", indexPath,
        "-collection", "JsonCollection",
        "-threads", threads + ""});
  }

  public SimpleIndexer(String indexPath, boolean append) throws Exception {
    // First line of constructor must be "this", which leads to a slightly awkward implementation.
    this(append ?
        new String[] {"-input", "", "-index", indexPath, "-collection", "JsonCollection", "-append"} :
        new String[] {"-input", "", "-index", indexPath, "-collection", "JsonCollection"});
  }

  public SimpleIndexer(String indexPath, boolean append, int threads) throws Exception {
    // First line of constructor must be "this", which leads to a slightly awkward implementation.
    this(append ?
        new String[] {"-input", "", "-index", indexPath,
            "-collection", "JsonCollection", "-threads", threads + "", "-append"} :
        new String[] {"-input", "", "-index", indexPath,
            "-collection", "JsonCollection", "-threads", threads + ""});
  }

  @SuppressWarnings("unchecked")
  public SimpleIndexer(Args args) throws Exception {
    this.threads = args.threads;
    this.indexPath = Paths.get(args.index);
    if (!Files.exists(this.indexPath)) {
      Files.createDirectories(this.indexPath);
    }
    Class generatorClass = Class.forName("io.anserini.index.generator." + args.generatorClass);
    generator = (LuceneDocumentGenerator) generatorClass.getDeclaredConstructor(Args.class).newInstance(args);
    analyzer = getAnalyzer(args);

    final Directory dir = FSDirectory.open(this.indexPath);
    final IndexWriterConfig config = new IndexWriterConfig(analyzer);

    config.setOpenMode(args.append ? IndexWriterConfig.OpenMode.CREATE_OR_APPEND : IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(2048);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    writer = new IndexWriter(dir, config);
  }

  private Analyzer getAnalyzer(Args args) {
    try {
      if (args.analyzeWithHuggingFaceTokenizer != null) {
        LOG.info("Using HuggingFaceTokenizerAnalyzer");
        return new HuggingFaceTokenizerAnalyzer(args.analyzeWithHuggingFaceTokenizer);
      } else if (AnalyzerMap.analyzerMap.containsKey(args.language)) {
        LOG.info("Using language-specific analyzer");
        LOG.info("Language: " + args.language);
        return AnalyzerMap.getLanguageSpecificAnalyzer(args.language);
      } else if (args.pretokenized || args.language.equals("sw")) {
        LOG.info("Using WhitespaceAnalyzer");
        return new WhitespaceAnalyzer();
      } else {
        // Default to English
        LOG.info("Using DefaultEnglishAnalyzer");
        LOG.info("Stemmer: " + args.stemmer);
        LOG.info("Keep stopwords? " + args.keepStopwords);
        LOG.info("Stopwords file: " + args.stopwords);
        return DefaultEnglishAnalyzer.fromArguments(args.stemmer, args.keepStopwords, args.stopwords);
      }
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public boolean addRawDocument(String raw) {
    try {
      JsonCollection.Document doc = JsonCollection.Document.fromString(raw);
      writer.addDocument(generator.createDocument(doc));
    } catch (GeneratorException e) {
      LOG.error(e);
      return false;
    } catch (IOException e) {
      LOG.error(e);
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean addJsonDocument(JsonCollection.Document doc) {
    try {
      writer.addDocument(generator.createDocument(doc));
    } catch (GeneratorException e) {
      LOG.error(e);
      return false;
    } catch (IOException e) {
      LOG.error(e);
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  public boolean addJsonNode(JsonNode node) {
    try {
      writer.addDocument(generator.createDocument(new JsonCollection.Document(node)));
    } catch (GeneratorException e) {
      LOG.error(e);
      return false;
    } catch (IOException e) {
      LOG.error(e);
      return false;
    }

    return true;
  }

  @SuppressWarnings("unchecked")
  public int addRawDocuments(String[] docs) throws IOException {
    return addToIndex(docs, (doc) -> {
      try {
        return JsonCollection.Document.fromString(doc);
      } catch (IOException e) {
        // There's not much we can do here, because we might have partially indexed a batch,
        // so a return value would not be accurate.
        throw new RuntimeException(e);
      }
    });
  }

  @SuppressWarnings("unchecked")
  public int addJsonDocuments(JsonCollection.Document[] docs) {
    return addToIndex(docs, Function.identity());
  }

  public int addJsonNodes(JsonNode[] nodes) {
    return addToIndex(nodes, JsonCollection.Document::new);
  }

  @SuppressWarnings("unchecked")
  private <T> int addToIndex(T[] objects, Function<T, JsonCollection.Document> func) {
    ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threads);
    AtomicInteger cnt = new AtomicInteger();

    for (T object : objects) {
      executor.execute(() -> {
        try {
          writer.addDocument(generator.createDocument(func.apply(object)));
          cnt.incrementAndGet();
        } catch (GeneratorException e) {
          throw new CompletionException(e);
        } catch (IOException e) {
          throw new CompletionException(e);
        }
      });
    }

    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
        // Opportunity to perform status logging, but no-op here because logging interferes with Python tqdm
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }

    return cnt.get();
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
}