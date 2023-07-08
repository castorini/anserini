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

package io.anserini.util;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.AnalyzerMap;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.index.IndexCollection;
import io.anserini.search.topicreader.TopicReader;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Utility to dump out query terms that have analyzed with Anserini's default Lucene Analyzer, DefaultEnglishAnalyzer
 * with Porter stemming. Query terms are taken from the "title" of topics. Output is a TSV file, with (topic id,
 * analyzed query) tuples; the analyzed query comprises space-delimited tokens.
 */
public class DumpAnalyzedQueries {

  private static final Logger LOG = LogManager.getLogger(DumpAnalyzedQueries.class);

  public static class Args {
    @Option(name = "-topicreader", metaVar = "[class]", usage = "topic reader")
    public String topicReader = null;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "queries")
    public Path topicsFile;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "queries")
    public String output;

    @Option(name = "-language", usage = "Analyzer Language")
    public String language = "en";
  }

  static Analyzer getAnalyzer(Args args) {
    try {
      if (AnalyzerMap.analyzerMap.containsKey(args.language)) {
        LOG.info("Using language-specific analyzer");
        LOG.info("Language: " + args.language);
        return AnalyzerMap.getLanguageSpecificAnalyzer(args.language);
      } else if (args.language.equals("sw") || args.language.equals("te")) {
        LOG.info("Using WhitespaceAnalyzer");
        return new WhitespaceAnalyzer();
      } else {
        // Default to English
        LOG.info("Using DefaultEnglishAnalyzer");
        return IndexCollection.DEFAULT_ANALYZER;
      }
    } catch (Exception e) {
      return null;
    }
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] argv) throws IOException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));
    Analyzer analyzer;

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      return;
    }

    TopicReader<?> tr;
    try {
      // Can we infer the TopicReader?
      Class<? extends TopicReader> clazz = TopicReader.getTopicReaderClassByFile(args.topicsFile.toString());
      if (clazz != null) {
        LOG.warn(String.format("Inferring %s has TopicReader class %s.", args.topicsFile, clazz));
      } else {
        // If not, get it from the command-line argument.
        LOG.info(String.format("Unable to infer TopicReader class for %s, using specified class %s.",
            args.topicsFile, args.topicReader));
        if (args.topicReader == null) {
          System.err.println("Must specify TopicReader with -topicreader!");
          System.exit(-1);
        }

        clazz = (Class<? extends TopicReader>) Class.forName(
            "io.anserini.search.topicreader." + args.topicReader + "TopicReader");
      }

      tr = (TopicReader<?>) clazz.getConstructor(Path.class).newInstance(args.topicsFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Unable to load TopicReader: " + args.topicReader);
    }
    SortedMap<?, Map<String, String>> topics = tr.read();

    analyzer = getAnalyzer(args);
    FileOutputStream out = new FileOutputStream(args.output);
    for (Map.Entry<?, Map<String, String>> entry : topics.entrySet()) {
      List<String> tokens = AnalyzerUtils.analyze(analyzer, entry.getValue().get("title"));
      out.write((entry.getKey() + "\t" + StringUtils.join(tokens, " ") + "\n").getBytes());
    }
    out.close();

    LOG.info("Done!");
  }
}
