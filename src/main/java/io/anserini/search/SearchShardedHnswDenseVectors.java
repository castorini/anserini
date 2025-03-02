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
import io.anserini.search.topicreader.Topics;
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
import java.util.stream.IntStream;

/**
 * Main entry point for sharded HNSW dense vector search.
 */
public final class SearchShardedHnswDenseVectors<K extends Comparable<K>> implements Runnable, Closeable {
  private static final Logger LOG = LogManager.getLogger(SearchShardedHnswDenseVectors.class);

  public static class Args extends SearchHnswDenseVectors.Args {
    //No additional arguments needed
  }

  private final Args args;
  private final List<SearchHnswDenseVectors<K>> searchers;
  private final String[] shardPaths;
  private final int threadsPerShard;

  /*
   * Constructor for sharded HNSW dense vector search.
   * Any index names can be used as long as they are registered in IndexInfo.
   * The caller must provide comma-separated index paths:
   * e.g., "-index msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8,msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8"
   */
  public SearchShardedHnswDenseVectors(Args args) throws IOException {
    this.args = args;
    this.searchers = new ArrayList<>();
    
    // Parse comma-separated shard paths
    this.shardPaths = args.index.split(",");
    this.threadsPerShard = args.threads;

    LOG.info("============ Initializing {} ============", this.getClass().getSimpleName());
    LOG.info("Using {} shards", this.shardPaths.length);
    LOG.info("Topics: {}", Arrays.toString(args.topics));
    LOG.info("Query generator: {}", args.queryGenerator);
    LOG.info("Encoder: {}", args.encoder);
    LOG.info("Threads: {}", args.threads);
    LOG.info("Threads per shard: {}", threadsPerShard);

    // Validate topic reader before initializing searchers
    if (args.topicReader != null) {
      try {
        if (TopicReader.getTopicReaderClassByFile(args.topicReader) == null) {
          throw new IOException("Unable to load topic reader \"" + args.topicReader + "\".");
        }
      } catch (Exception e) {
        throw new IOException("Unable to load topic reader \"" + args.topicReader + "\".");
      }
    }

    // Initialize searchers for each shard
    try {
      for (String shardPath : this.shardPaths) {
        // Validate index path before attempting to create searcher
        if (!Files.exists(Paths.get(shardPath))) {
          throw new IOException("No collection found for identifier: " + shardPath);
        }
        
        Args shardArgs = new Args();
        // Copy all args from the parent
        shardArgs.topics = args.topics;
        shardArgs.output = args.output;
        shardArgs.topicReader = args.topicReader;
        shardArgs.topicField = args.topicField;
        shardArgs.hits = args.hits;
        shardArgs.runtag = args.runtag;
        shardArgs.format = args.format;
        shardArgs.options = args.options;

        // Set shard-specific args
        shardArgs.index = shardPath;
        shardArgs.encoder = args.encoder;
        shardArgs.queryGenerator = args.queryGenerator;
        shardArgs.efSearch = args.efSearch;
        shardArgs.threads = threadsPerShard;

        searchers.add(new SearchHnswDenseVectors<K>(shardArgs));
      }
    } catch (IOException e) {
      for (SearchHnswDenseVectors<K> searcher : searchers) {
        try {
          searcher.close();
        } catch (IOException ex) {
          LOG.warn("Error closing searcher during initialization failure", ex);
        }
      }
      throw e;
    }
  }

  @Override
  public void close() throws IOException {
    LOG.info("Closing searchers...");
    for (SearchHnswDenseVectors<K> searcher : searchers) {
      try {
        searcher.close();
        LOG.info("Closed searcher: {}", searcher.getClass().getSimpleName());
      } catch (IOException e) {
        LOG.warn("Error closing searcher: {}", searcher.getClass().getSimpleName(), e);
      }
    }
    LOG.info("All searchers closed.");
  }

  @Override
  public void run() {
    LOG.info("============ Running Sharded Search ============");

    List<String> shardOutputPaths = new ArrayList<>();
    final List<Exception> exceptions = new ArrayList<>();

    IntStream.range(0, searchers.size()).parallel().forEach(i -> {
      SearchHnswDenseVectors<K> searcher = searchers.get(i);
      String shardOutputPath = args.output.replaceFirst("\\.txt$", ".shard" + String.format("%02d", i) + ".txt");
      synchronized (shardOutputPaths) {
        shardOutputPaths.add(shardOutputPath);
      }
      LOG.info("Processing shard {} -> {}", i, shardOutputPath);

      try {
        searcher.args.output = shardOutputPath;
        searcher.run();
        searcher.close();
        LOG.info("Closed searcher for shard {}", i);
      } catch (Exception e) {
        synchronized (exceptions) {
          exceptions.add(e);
        }
        LOG.error("Error processing shard {}: {}", i, e.getMessage(), e);
      }
    });

    // Check if any exceptions occurred during processing
    if (!exceptions.isEmpty()) {
      throw new RuntimeException("Error processing shards: " + exceptions.get(0).getMessage(), exceptions.get(0));
    }

    LOG.info("Concatenating shard results into {}", args.output);
    try {
      Files.createDirectories(Paths.get(args.output).getParent());
      Files.write(Paths.get(args.output), new byte[0]);

      for (String shardPath : shardOutputPaths) {
        if (Files.exists(Paths.get(shardPath))) {
          Files.write(Paths.get(args.output), Files.readAllBytes(Paths.get(shardPath)), java.nio.file.StandardOpenOption.APPEND);
        }
      }
      LOG.info("All results concatenated successfully.");
      LOG.info("Individual shard results are not deleted. Shard results for each shard are stored in the output directory as separate files.");
    } catch (IOException e) {
      throw new RuntimeException("Error concatenating shard results: " + e.getMessage(), e);
    }
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (searchArgs.options) {
        System.err.printf("Options for %s:\n\n", SearchShardedHnswDenseVectors.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });
        System.err.printf("\nRequired options are %s\n", required);
        
        System.err.println("\nUsage example:");
        System.err.println("  -index \"msmarco-v2.1-doc-segmented-shard00.arctic-embed-l.hnsw-int8,msmarco-v2.1-doc-segmented-shard01.arctic-embed-l.hnsw-int8\"");
      } else {
        System.err.printf("Error: %s\n", e.getMessage());
      }

      return;
    }

    try (SearchShardedHnswDenseVectors<?> searcher = new SearchShardedHnswDenseVectors<>(searchArgs)) {
      searcher.run();
    } catch (Exception e) {
      System.err.printf("Error: %s\n", e.getMessage());
    }
  }
}
