/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import io.anserini.index.IndexCollection;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.StringUtils;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

/**
 * Utility to dump out analyzed queries (i.e., "title" of topics).
 */
public class DumpAnalyzedQueries {
  public static class Args {
    @Option(name = "-topicreader", metaVar = "[class]", usage = "topic reader")
    public String topicReader = null;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "queries")
    public Path topicsFile;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "queries")
    public String output;
  }

  @SuppressWarnings("unchecked")
  public static void main(String[] argv) throws IOException {
    Args args = new Args();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: DumpAnalyzedQueries " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    TopicReader<?> tr;
    try {
      // Can we infer the TopicReader?
      Class<? extends TopicReader> clazz = TopicReader.getTopicReaderByFile(args.topicsFile.toString());
      if (clazz != null) {
        System.out.println(String.format("Inferring %s has TopicReader class %s", args.topicsFile, clazz));
      } else {
        // If not, get it from the command-line argument.
        System.out.println(String.format("Unable to infer TopicReader class for %s, using specified class %s",
            args.topicsFile, args.topicReader));
        if (args.topicReader == null) {
          System.err.println("Must specify TopicReader with -topicreader!");
          System.exit(-1);
        }

        clazz = (Class<? extends TopicReader>) Class.forName("io.anserini.search.topicreader." + args.topicReader + "TopicReader");
      }

      tr = (TopicReader<?>) clazz.getConstructor(Path.class).newInstance(args.topicsFile);
    } catch (Exception e) {
      e.printStackTrace();
      throw new IllegalArgumentException("Unable to load topic reader: " + args.topicReader);
    }

    SortedMap<?, Map<String, String>> topics = tr.read();

    FileOutputStream out = new FileOutputStream(args.output);
    for (Map.Entry<?, Map<String, String>> entry : topics.entrySet()) {
      List<String> tokens = AnalyzerUtils.tokenize(IndexCollection.DEFAULT_ANALYZER, entry.getValue().get("title"));
      out.write((entry.getKey() + "\t" + StringUtils.join(tokens, " ") + "\n").getBytes());
    }
    out.close();
  }
}
