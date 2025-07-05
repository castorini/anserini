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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Main entry point for sharded HNSW dense vector search.
 */
public final class SearchShardedHnswDenseVectors<K extends Comparable<K>> implements Runnable, Closeable {
  private static final Logger LOG = LogManager.getLogger(SearchShardedHnswDenseVectors.class);

  public static class Args extends SearchHnswDenseVectors.Args {
    // No additional arguments needed
  }

  private final Args args;
  private final List<SearchHnswDenseVectors<K>> searchers;

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
    String[] shardPaths = args.index.split(",");
    int threadsPerShard = args.threads;

    LOG.info("============ Initializing {} ============", this.getClass().getSimpleName());
    LOG.info("Using {} shards", shardPaths.length);
    LOG.info("Topics: {}", Arrays.toString(args.topics));
    LOG.info("Query generator: {}", args.queryGenerator);
    LOG.info("Encoder: {}", args.encoder);
    LOG.info("Threads: {}", args.threads);
    LOG.info("Threads per shard: {}", threadsPerShard);

    // Initialize searchers for each shard
    // Each individual searcher will validate its own parameters
    try {
      for (String shardPath : shardPaths) {
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
    } catch (Exception e) {
      close();
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
    IntStream.range(0, searchers.size()).parallel().forEach(i -> {
      SearchHnswDenseVectors<K> searcher = searchers.get(i);
      String shardOutputPath = args.output.replaceFirst("\\.txt$", ".shard" + String.format("%02d", i) + ".txt");
      if (!args.output.endsWith(".txt")) {
        shardOutputPath = args.output + ".shard" + String.format("%02d", i);
      }
      shardOutputPaths.add(shardOutputPath);
      LOG.info("Processing shard {} -> {}", i, shardOutputPath);

      searcher.args.output = shardOutputPath;
      searcher.run();
    });

    LOG.info("Concatenating shard results into {}", args.output);
    try {
      Files.createDirectories(Paths.get(args.output).getParent());
      Files.write(Paths.get(args.output), new byte[0]);

      boolean anyShardHasContent = false;
      for (String shardPath : shardOutputPaths) {
        Path path = Paths.get(shardPath);
        if (Files.exists(path) && Files.size(path) > 0) {
          anyShardHasContent = true;
          Files.write(Paths.get(args.output), Files.readAllBytes(path), StandardOpenOption.APPEND);
          LOG.info("Appended content from shard file: {} (size: {} bytes)", shardPath, Files.size(path));
        } else {
          LOG.warn("Shard file {} does not exist or is empty", shardPath);
        }
      }
      
      if (!anyShardHasContent) {
        LOG.warn("No shard files contained any content. Output file will be empty.");
      } else {
        LOG.info("All results concatenated successfully.");
      }
      
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
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n",
          e.getMessage());
      }

      return;
    }

    try (SearchShardedHnswDenseVectors<?> searcher = new SearchShardedHnswDenseVectors<>(searchArgs)) {
      searcher.run();
    } catch (RuntimeException e) {
      System.err.printf("Error: %s\n", e.getMessage());
    }
  }
}
