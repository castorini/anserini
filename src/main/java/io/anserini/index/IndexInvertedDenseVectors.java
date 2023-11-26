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

import io.anserini.analysis.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.lexlsh.LexicalLshAnalyzer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.codecs.lucene95.Lucene95Codec;
import org.apache.lucene.index.ConcurrentMergeScheduler;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class IndexInvertedDenseVectors extends AbstractIndexer {
  private static final Logger LOG = LogManager.getLogger(IndexInvertedDenseVectors.class);

  public static final String FW = "fw";
  public static final String LEXLSH = "lexlsh";

  public static final class InvertedDenseArgs extends AbstractIndexer.Args {
    @Option(name = "-encoding", metaVar = "[word]", usage = "Encoding method: {'fw', 'lexlsh'}.")
    public String encoding = FW;

    @Option(name = "-fw.q", metaVar = "[int]", usage = "Fake Words encoding: quantization factor.")
    public int q = FakeWordsEncoderAnalyzer.DEFAULT_Q;

    @Option(name = "-lexlsh.n", metaVar = "[int]", usage = "LexLSH encoding: n-gram size.")
    public int ngrams = 2;

    @Option(name = "-lexlsh.d", metaVar = "[int]", usage = "LexLSH encoding: decimal digits.")
    public int decimals = 1;

    @Option(name = "-lexlsh.hsize", metaVar = "[int]", usage = "LexLSH encoding: hash set size.")
    public int hashSetSize = 1;

    @Option(name = "-lexlsh.h", metaVar = "[int]", usage = "LexLSH encoding: hash count.")
    public int hashCount = 1;

    @Option(name = "-lexlsh.b", metaVar = "[int]", usage = "LexLSH encoding: bucket count.")
    public int bucketCount = 300;
  }

  public IndexInvertedDenseVectors(InvertedDenseArgs args) {
    super(args);

    LOG.info("InvertedDenseIndexer settings:");
    LOG.info(" + Encoding: " + args.encoding);

    Analyzer vectorAnalyzer;
    if (args.encoding.equalsIgnoreCase(FW)) {
      vectorAnalyzer = new FakeWordsEncoderAnalyzer(args.q);
    } else if (args.encoding.equalsIgnoreCase(LEXLSH)) {
      vectorAnalyzer = new LexicalLshAnalyzer(args.decimals, args.ngrams, args.hashCount, args.bucketCount, args.hashSetSize);
    } else {
      throw new IllegalArgumentException("Invalid encoding scheme.");
    }

    Map<String, Analyzer> map = new HashMap<>();
    map.put(Constants.VECTOR, vectorAnalyzer);
    Analyzer analyzer = new PerFieldAnalyzerWrapper(new StandardAnalyzer(), map);

    try {
      final Directory dir = FSDirectory.open(Paths.get(args.index));
      final IndexWriterConfig config = new IndexWriterConfig(analyzer).setCodec(new Lucene95Codec());
      config.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
      config.setRAMBufferSizeMB(args.memorybufferSize);
      config.setUseCompoundFile(false);
      config.setMergeScheduler(new ConcurrentMergeScheduler());
      this.writer = new IndexWriter(dir, config);
    } catch (Exception e) {
      throw new IllegalArgumentException(String.format("Unable to create IndexWriter: %s.", e.getMessage()));
    }

    this.threads = args.threads;
    this.optimize = args.optimize;
  }

  public static void main(String[] args) throws Exception {
    InvertedDenseArgs indexArgs = new InvertedDenseArgs();
    CmdLineParser parser = new CmdLineParser(indexArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (indexArgs.options) {
        System.err.printf("Options for %s:\n\n", IndexInvertedDenseVectors.class.getSimpleName());
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

    new IndexInvertedDenseVectors(indexArgs).run();
  }
}
