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

import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexHnswDenseVectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SearchShardedHnswDenseVectors}
 */
public class SearchShardedHnswDenseVectorsTest {
  private static final Logger LOG = LogManager.getLogger(SearchShardedHnswDenseVectorsTest.class);
  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  /**
   * Attempt to clean up memory between tests
   */
  @After
  public void cleanupMemory() {
    System.gc();
    try {
      Thread.sleep(500); // Give GC time to work
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
  }

  @BeforeClass
  public static void setupClass() {
    // Set log levels to INFO for better debugging
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.INFO);
    Configurator.setLevel(IndexHnswDenseVectors.class.getName(), Level.INFO);
    Configurator.setLevel(SearchShardedHnswDenseVectors.class.getName(), Level.INFO);
    Configurator.setLevel(HnswDenseSearcher.class.getName(), Level.INFO);
    Configurator.setLevel(SearchShardedHnswDenseVectorsTest.class.getName(), Level.INFO);
  }

  @Test
  public void testBasicShardedSearch() throws Exception {
    // Verify the paths exist before running test
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";
    
    LOG.info("Verifying shard paths exist: {} and {}", shardPath1, shardPath2);
    assertTrue("Shard path 1 doesn't exist: " + shardPath1, Files.exists(Paths.get(shardPath1)));
    assertTrue("Shard path 2 doesn't exist: " + shardPath2, Files.exists(Paths.get(shardPath2)));
    
    // Verify topic file exists
    String topicFile = "src/test/resources/sample_topics/arctic.tsv";
    LOG.info("Verifying topic file exists: {}", topicFile);
    assertTrue("Topic file doesn't exist: " + topicFile, Files.exists(Paths.get(topicFile)));

    String timestamp = String.valueOf(System.currentTimeMillis());
    String runfile = "target/run-sharded-" + timestamp;
    
    LOG.info("Setting up test with output file: {}", runfile);
    
    // Create parent directory for output if it doesn't exist
    Files.createDirectories(Paths.get(runfile).getParent());
    
    // Capture stderr for debugging
    redirectStderr();
    
    try {
      String[] searchArgs = new String[] {
          "-index", String.format("%s,%s", shardPath1, shardPath2),
          "-topics", topicFile,
          "-output", runfile,
          "-topicReader", "TsvString",
          "-topicField", "title",
          "-encoder", "ArcticEmbedL",
          "-threads", "1", // threadsPerShard
          "-efSearch", "100",  // Reduced from 1000 to reduce memory usage
          "-hits", "10"};      // Reduced hit count to reduce memory usage

      LOG.info("Running search with args: {}", String.join(" ", searchArgs));
      SearchShardedHnswDenseVectors.main(searchArgs);
      
      File f = new File(runfile);
      LOG.info("Checking if output file exists: {}", f.getAbsolutePath());
      LOG.info("File exists: {}, File length: {}", f.exists(), f.exists() ? f.length() : 0);
      
      assertTrue("Output file doesn't exist: " + runfile, f.exists());
      assertTrue("Output file is empty: " + runfile, f.length() > 0);
      
      f.delete();
    } finally {
      String errors = err.toString();
      if (!errors.isEmpty()) {
        LOG.error("Errors encountered during search: {}", errors);
      }
      restoreStderr();
    }
  }

  @Test
  public void testShardedSearchWithPreEncodedVectors() throws Exception {
    // Verify the paths exist before running test
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";
    
    LOG.info("Verifying shard paths exist: {} and {}", shardPath1, shardPath2);
    assertTrue("Shard path 1 doesn't exist: " + shardPath1, Files.exists(Paths.get(shardPath1)));
    assertTrue("Shard path 2 doesn't exist: " + shardPath2, Files.exists(Paths.get(shardPath2)));
    
    // Verify topic file exists
    String topicFile = "src/test/resources/sample_topics/arctic.jsonl";
    LOG.info("Verifying topic file exists: {}", topicFile);
    assertTrue("Topic file doesn't exist: " + topicFile, Files.exists(Paths.get(topicFile)));

    String timestamp = String.valueOf(System.currentTimeMillis());
    String runfile = "target/run-sharded-vectors-" + timestamp;
    
    LOG.info("Setting up test with output file: {}", runfile);
    
    // Create parent directory for output if it doesn't exist
    Files.createDirectories(Paths.get(runfile).getParent());
    
    // Capture stderr for debugging
    redirectStderr();
    
    try {
      String[] searchArgs = new String[] {
          "-index", String.format("%s,%s", shardPath1, shardPath2),
          "-topics", topicFile,
          "-output", runfile,
          "-generator", "VectorQueryGenerator",
          "-topicReader", "JsonIntVector",
          "-topicField", "vector",
          "-threads", "1", // threadsPerShard
          "-efSearch", "100",  // Reduced from 1000 to reduce memory usage
          "-hits", "10"};      // Reduced hit count to reduce memory usage

      LOG.info("Running search with args: {}", String.join(" ", searchArgs));
      SearchShardedHnswDenseVectors.main(searchArgs);
      
      File f = new File(runfile);
      LOG.info("Checking if output file exists: {}", f.getAbsolutePath());
      LOG.info("File exists: {}, File length: {}", f.exists(), f.exists() ? f.length() : 0);
      
      assertTrue("Output file doesn't exist: " + runfile, f.exists());
      assertTrue("Output file is empty: " + runfile, f.length() > 0);
      
      f.delete();
    } finally {
      String errors = err.toString();
      if (!errors.isEmpty()) {
        LOG.error("Errors encountered during search: {}", errors);
      }
      restoreStderr();
    }
  }
}