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

package io.anserini.search;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.anserini.search.topicreader.TopicReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Main entry point for inverted dense vector search.
 */
public final class SearchInvertedDenseVectors<K extends Comparable<K>> implements Runnable, Closeable {
  private static final Logger LOG = LogManager.getLogger(SearchInvertedDenseVectors.class);

  public static class Args extends InvertedDenseSearcher.Args {
    @Option(name = "-topics", metaVar = "[file]", handler = StringArrayOptionHandler.class, required = true, usage = "topics file")
    public String[] topics;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
    public String output;

    @Option(name = "-topicReader", usage = "TopicReader to use.")
    public String topicReader = "JsonIntVector";

    @Option(name = "-topicField", usage = "Topic field that should be used as the query.")
    public String topicField = "vector";

    @Option(name = "-hits", metaVar = "[number]", usage = "max number of hits to return")
    public int hits = 1000;

    @Option(name = "-runtag", metaVar = "[tag]", usage = "runtag")
    public String runtag = "Anserini";

    @Option(name = "-format", metaVar = "[output format]", usage = "Output format, default \"trec\", alternative \"msmarco\".")
    public String format = "trec";

    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;
  }

  private final Args args;
  private final InvertedDenseSearcher<K> searcher;
  private final List<K> qids= new ArrayList<>();
  private final List<String> queries = new ArrayList<>();

  public SearchInvertedDenseVectors(Args args) {
    this.args = args;
    this.searcher = new InvertedDenseSearcher<>(args);

    LOG.info(String.format("============ Initializing %s ============", this.getClass().getSimpleName()));
    LOG.info("Index: " + args.index);
    LOG.info("Topics: " + Arrays.toString(args.topics));
    LOG.info("Encoding: " + args.encoding);
    LOG.info("Threads: " + args.threads);

    // We might not be able to successfully read topics for a variety of reasons. Gather all possible
    // exceptions together as an unchecked exception to make initialization and error reporting clearer.
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();
    for (String topicsFile : args.topics) {
      Path topicsFilePath = Paths.get(topicsFile);
      if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        throw new IllegalArgumentException(String.format("\"%s\" does not appear to be a valid topics file.", topicsFilePath));
      }
      try {
        @SuppressWarnings("unchecked")
        TopicReader<K> tr = (TopicReader<K>) Class
            .forName(String.format("io.anserini.search.topicreader.%sTopicReader", args.topicReader))
            .getConstructor(Path.class).newInstance(topicsFilePath);

        topics.putAll(tr.read());
      } catch (Exception e) {
        throw new IllegalArgumentException(String.format("Unable to load topic reader \"%s\".", args.topicReader));
      }
    }

    // Now iterate through all the topics to pick out the right field with proper exception handling.
    try {
      topics.forEach((qid, topic) -> {
        String query = topic.get(args.topicField);
        assert query != null;
        qids.add(qid);
        queries.add(query);
      });
    } catch (AssertionError|Exception e) {
      throw new IllegalArgumentException(String.format("Unable to read topic field \"%s\".", args.topicField));
    }
  }

  @Override
  public void close() throws IOException {
    searcher.close();
  }

  @Override
  public void run() {
    LOG.info("============ Launching Search Threads ============");
    SortedMap<K, ScoredDoc[]> results = searcher.batch_search(queries, qids, args.hits, args.threads);

    try(RunOutputWriter<K> out = new RunOutputWriter<>(args.output, args.format, args.runtag, null)) {
     results.forEach((qid, hits) -> {
        try {
          out.writeTopic(qid, queries.get(qids.indexOf(qid)), results.get(qid));
        } catch (JsonProcessingException e) {
          // Rethrow as unchecked; if we encounter an exception here, the caller should really look into it.
          throw new RuntimeException(e);
        }
      });
    } catch (IOException e) {
      // Rethrow as unchecked; if we encounter an exception here, the caller should really look into it.
      throw new RuntimeException(e);
    }
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (searchArgs.options) {
        System.err.printf("Options for %s:\n\n", SearchInvertedDenseVectors.class.getSimpleName());
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

    // We're at top-level already inside a main; makes no sense to propagate exceptions further, so reformat the
    // exception messages and display on console.
    try(SearchInvertedDenseVectors<?> searcher = new SearchInvertedDenseVectors<>(searchArgs)) {
      searcher.run();
    } catch (RuntimeException e) {
      System.err.printf("Error: %s\n", e.getMessage());
      return;
    }
  }
}