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

import io.anserini.analysis.AnalyzerMap;
import io.anserini.analysis.AutoCompositeAnalyzer;
import io.anserini.analysis.CompositeAnalyzer;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.analysis.HuggingFaceTokenizerAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.collection.SourceDocument;
import io.anserini.index.generator.LuceneDocumentGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.ImpactSimilarity;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.codecs.lucene99.Lucene99Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadPoolExecutor;

public final class IndexCollection extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexCollection.class);

  // This is the default analyzer used, unless another stemming algorithm or language is specified.
  public static final Analyzer DEFAULT_ANALYZER = DefaultEnglishAnalyzer.newDefaultInstance();

  public static class Args extends AbstractIndexer.Args {
    @Option(name = "-append", usage = "Append documents.")
    public boolean append = false;

    @Option(name = "-generator", metaVar = "[class]",
        usage = "Document generator class in package 'io.anserini.index.generator'.")
    public String generatorClass = "DefaultLuceneDocumentGenerator";

    @Option(name = "-fields", handler = StringArrayOptionHandler.class,
        usage = "List of fields to index (space separated), in addition to the default 'contents' field.")
    public String[] fields = new String[]{};

    @Option(name = "-storePositions",
        usage = "Boolean switch to index store term positions; needed for phrase queries.")
    public boolean storePositions = false;

    @Option(name = "-storeDocvectors",
        usage = "Boolean switch to store document vectors; needed for (pseudo) relevance feedback.")
    public boolean storeDocvectors = false;

    @Option(name = "-storeContents",
        usage = "Boolean switch to store document contents.")
    public boolean storeContents = false;

    @Option(name = "-storeRaw",
        usage = "Boolean switch to store raw source documents.")
    public boolean storeRaw = false;

    @Option(name = "-keepStopwords",
        usage = "Boolean switch to keep stopwords.")
    public boolean keepStopwords = false;

    @Option(name = "-stopwords", metaVar = "[file]", forbids = "-keepStopwords",
        usage = "Path to file with stopwords.")
    public String stopwords = null;

    @Option(name = "-stemmer", metaVar = "[stemmer]",
        usage = "Stemmer: one of the following {porter, krovetz, none}; defaults to 'porter'.")
    public String stemmer = "porter";

    @Option(name = "-whitelist", metaVar = "[file]",
        usage = "File containing list of docids, one per line; only these docids will be indexed.")
    public String whitelist = null;

    @Option(name = "-impact",
        usage = "Boolean switch to store impacts (no norms).")
    public boolean impact = false;

    @Option(name = "-bm25.accurate",
        usage = "Boolean switch to use AccurateBM25Similarity (computes accurate document lengths).")
    public boolean bm25Accurate = false;

    @Option(name = "-language", metaVar = "[language]",
        usage = "Analyzer language (ISO 3166 two-letter code).")
    public String language= "en";

    @Option(name = "-pretokenized",
        usage = "index pre-tokenized collections without any additional stemming, stopword processing")
    public boolean pretokenized = false;

    @Option(name = "-analyzeWithHuggingFaceTokenizer",
        usage = "index a collection by tokenizing text with pretrained huggingface tokenizers")
    public String analyzeWithHuggingFaceTokenizer = null;

    @Option(name = "-useCompositeAnalyzer",
        usage="index a collection using a Lucene Analyzer & a pretrained HuggingFace tokenizer")
    public boolean useCompositeAnalyzer = false;

    @Option(name = "-useAutoCompositeAnalyzer",
        usage="index a collection using the AutoCompositeAnalyzer")
    public boolean useAutoCompositeAnalyzer = false;

    // Tweet options

    @Option(name = "-tweet.keepRetweets",
        usage = "Boolean switch to index retweets.")
    public boolean tweetKeepRetweets = false;

    @Option(name = "-tweet.keepUrls",
        usage = "Boolean switch to keep URLs.")
    public boolean tweetKeepUrls = false;

    @Option(name = "-tweet.stemming",
        usage = "Boolean switch to apply Porter stemming while indexing tweets.")
    public boolean tweetStemming = false;

    @Option(name = "-tweet.maxId", metaVar = "[id]",
        usage = "Max tweet id to index (long); all tweets with larger tweet ids will be skipped.")
    public long tweetMaxId = Long.MAX_VALUE;

    @Option(name = "-tweet.deletedIdsFile", metaVar = "[file]",
        usage = "File that contains deleted tweet ids (longs), one per line; these tweets will be skipped during indexing.")
    public String tweetDeletedIdsFile = "";
  }

  private final Set<String> whitelistDocids;

  @SuppressWarnings("unchecked")
  public IndexCollection(Args args) throws Exception {
    super(args);

    try {
      super.generatorClass = (Class<LuceneDocumentGenerator<? extends SourceDocument>>)
              Class.forName("io.anserini.index.generator." + args.generatorClass);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to load generator class \"%s\".", args.generatorClass));
    }

    if (args.whitelist != null) {
      List<String> lines = FileUtils.readLines(new File(args.whitelist), "utf-8");
      this.whitelistDocids = new HashSet<>(lines);
    } else {
      this.whitelistDocids = null;
    }

    final Directory dir = FSDirectory.open(Paths.get(args.index));
    final IndexWriterConfig config = new IndexWriterConfig(getAnalyzer());

    if (args.bm25Accurate) {
      // Necessary during indexing as the norm used in BM25 is already determined at index time.
      config.setSimilarity(new AccurateBM25Similarity());
    } else if (args.impact ) {
      config.setSimilarity(new ImpactSimilarity());
    } else {
      config.setSimilarity(new BM25Similarity());
    }
    config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
    config.setRAMBufferSizeMB(args.memoryBuffer);
    config.setUseCompoundFile(false);
    config.setMergeScheduler(new ConcurrentMergeScheduler());

    super.writer = new IndexWriter(dir, config);

    LOG.info("IndexCollection settings:");
    LOG.info(" + Generator: " + args.generatorClass);
    LOG.info(" + Language: " + args.language);
    LOG.info(" + Stemmer: " + args.stemmer);
    LOG.info(" + Keep stopwords? " + args.keepStopwords);
    LOG.info(" + Stopwords: " + args.stopwords);
    LOG.info(" + Store positions? " + args.storePositions);
    LOG.info(" + Store docvectors? " + args.storeDocvectors);
    LOG.info(" + Store document \"contents\" field? " + args.storeContents);
    LOG.info(" + Store document \"raw\" field? " + args.storeRaw);
    LOG.info(" + Additional fields to index: " + Arrays.toString(args.fields));
    LOG.info(" + Whitelist: " + args.whitelist);
    LOG.info(" + Pretokenized?: " + args.pretokenized);
    LOG.info(" + Codec: " + this.writer.getConfig().getCodec());
  }

  private Analyzer getAnalyzer() {
    try {
      // args is stored in the super-class; here, explicitly get from super-class and down-cast.
      Args castedArgs = (Args) super.args;
      if (castedArgs.collectionClass.equals("TweetCollection")) {
        return new TweetAnalyzer(castedArgs.tweetStemming);
      } else if (castedArgs.useAutoCompositeAnalyzer) {
        LOG.info("Using AutoCompositeAnalyzer");
        return AutoCompositeAnalyzer.getAnalyzer(castedArgs.language, castedArgs.analyzeWithHuggingFaceTokenizer);
      } else if (castedArgs.useCompositeAnalyzer) {
        final Analyzer languageSpecificAnalyzer;
        if (AnalyzerMap.analyzerMap.containsKey(castedArgs.language)) {
          languageSpecificAnalyzer = AnalyzerMap.getLanguageSpecificAnalyzer(castedArgs.language);
        } else if (castedArgs.language.equals("en")) {
          languageSpecificAnalyzer = DefaultEnglishAnalyzer.fromArguments(castedArgs.stemmer, castedArgs.keepStopwords, castedArgs.stopwords);
        } else {
          languageSpecificAnalyzer = new WhitespaceAnalyzer();
        }
        String message = "Using CompositeAnalyzer with HF Tokenizer: %s & Analyzer %s";
        LOG.info(String.format(message, castedArgs.analyzeWithHuggingFaceTokenizer, languageSpecificAnalyzer.getClass().getName()));
        return new CompositeAnalyzer(castedArgs.analyzeWithHuggingFaceTokenizer, languageSpecificAnalyzer);
      } else if (castedArgs.analyzeWithHuggingFaceTokenizer!= null) {
        return new HuggingFaceTokenizerAnalyzer(castedArgs.analyzeWithHuggingFaceTokenizer);
      } else if (AnalyzerMap.analyzerMap.containsKey(castedArgs.language)) {
        LOG.info("Using language-specific analyzer");
        LOG.info("Language: " + castedArgs.language);
        return AnalyzerMap.getLanguageSpecificAnalyzer(castedArgs.language);
      } else if ( Arrays.asList("ha","so","sw","yo").contains(castedArgs.language)) {
        return new WhitespaceAnalyzer();
      } else if (castedArgs.pretokenized) {
        return new WhitespaceAnalyzer();
      } else {
        // Default to English
        LOG.info("Using DefaultEnglishAnalyzer");
        LOG.info("Stemmer: " + castedArgs.stemmer);
        LOG.info("Keep stopwords? " + castedArgs.keepStopwords);
        LOG.info("Stopwords file: " + castedArgs.stopwords);
        return DefaultEnglishAnalyzer.fromArguments(castedArgs.stemmer, castedArgs.keepStopwords, castedArgs.stopwords);
      }
    } catch (Exception e) {
      return null;
    }
  }

  protected void processSegments(ThreadPoolExecutor executor, List<Path> segmentPaths) {
    segmentPaths.forEach((segmentPath) -> {
      try {
        // Each thread gets its own document generator, so we don't need to make any assumptions about its thread safety.
        @SuppressWarnings("unchecked")
        LuceneDocumentGenerator<SourceDocument> generator = (LuceneDocumentGenerator<SourceDocument>)
                generatorClass.getDeclaredConstructor(Args.class).newInstance(this.args);

        executor.execute(new AbstractIndexer.IndexerThread(segmentPath, generator, whitelistDocids));
      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
        throw new IllegalArgumentException(String.format("Unable to load LuceneDocumentGenerator \"%s\".", generatorClass.getSimpleName()));
      }
    });
  }

  public static void main(String[] args) throws Exception {
    Args indexArgs = new Args();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (indexArgs.options) {
        System.err.printf("Options for %s:\n\n", IndexCollection.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }

    new IndexCollection(indexArgs).run();
  }
}
