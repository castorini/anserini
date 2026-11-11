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

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests for {@link SearchShardedHnswDenseVectors}
 */
public class SearchShardedHnswDenseVectorsTest {
  // Note, cannot extend StdOutStdErrRedirectableLuceneTestCase due to concurrency issues.
  // As a result, we cannot just call suppressJvmLogging() - must duplicate code below.
  private static final String SHARDED_RUN = "target/run-sharded-test";
  private static final String SHARDED_VECTORS_RUN = "target/run-sharded-vectors-test";

  @BeforeClass
  public static void setupClass() {
    java.util.logging.Logger root = java.util.logging.Logger.getLogger("");
    root.setLevel(java.util.logging.Level.OFF); // suppress INFO and below
    for (var handler : root.getHandlers()) {
      handler.setLevel(java.util.logging.Level.OFF);
    }

    Configurator.setLevel(SearchShardedHnswDenseVectorsTest.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchShardedHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(HnswDenseSearcher.class.getName(), Level.ERROR);
  }

  @After
  public void tearDown() {
    deleteRunFiles(SHARDED_RUN);
    deleteRunFiles(SHARDED_VECTORS_RUN);
  }

  @Test
  public void testBasicShardedSearch() throws Exception {
    // Verify the paths exist before running test
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";

    assertTrue("Shard path 1 doesn't exist: " + shardPath1, Files.exists(Paths.get(shardPath1)));
    assertTrue("Shard path 2 doesn't exist: " + shardPath2, Files.exists(Paths.get(shardPath2)));

    // Verify topic file exists
    String topicFile = "src/test/resources/sample_topics/arctic.tsv";
    assertTrue("Topic file doesn't exist: " + topicFile, Files.exists(Paths.get(topicFile)));

    String runfile = SHARDED_RUN;
    deleteRunFiles(runfile);

    // Create parent directory for output if it doesn't exist
    Files.createDirectories(Paths.get(runfile).getParent());

    String[] searchArgs = new String[] {
        "-index", String.format("%s,%s", shardPath1, shardPath2),
        "-topics", topicFile,
        "-output", runfile,
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-threads", "1", // threadsPerShard
        "-efSearch", "100", // Reduced from 1000 to reduce memory usage
        "-hits", "10" }; // Reduced hit count to reduce memory usage

    SearchShardedHnswDenseVectors.main(searchArgs);

    assertRunFileExistsAndNonEmpty(runfile);
    assertRunFileExistsAndNonEmpty(runfile + ".shard00");
    assertRunFileExistsAndNonEmpty(runfile + ".shard01");
  }

  @Test
  public void testShardedSearchWithPreEncodedVectors() throws Exception {
    // Verify the paths exist before running test
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";

    assertTrue("Shard path 1 doesn't exist: " + shardPath1, Files.exists(Paths.get(shardPath1)));
    assertTrue("Shard path 2 doesn't exist: " + shardPath2, Files.exists(Paths.get(shardPath2)));

    // Verify topic file exists
    String topicFile = "src/test/resources/sample_topics/arctic.jsonl";
    assertTrue("Topic file doesn't exist: " + topicFile, Files.exists(Paths.get(topicFile)));

    String runfile = SHARDED_VECTORS_RUN;
    deleteRunFiles(runfile);

    // Create parent directory for output if it doesn't exist
    Files.createDirectories(Paths.get(runfile).getParent());

    String[] searchArgs = new String[] {
        "-index", String.format("%s,%s", shardPath1, shardPath2),
        "-topics", topicFile,
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-threads", "1", // threadsPerShard
        "-efSearch", "100", // Reduced from 1000 to reduce memory usage
        "-hits", "10" }; // Reduced hit count to reduce memory usage

    SearchShardedHnswDenseVectors.main(searchArgs);

    assertRunFileExistsAndNonEmpty(runfile);
    assertRunFileExistsAndNonEmpty(runfile + ".shard00");
    assertRunFileExistsAndNonEmpty(runfile + ".shard01");
  }

  private static void assertRunFileExistsAndNonEmpty(String runfile) {
    File f = new File(runfile);
    assertTrue("Output file doesn't exist: " + runfile, f.exists());
    assertTrue("Output file is empty: " + runfile, f.length() > 0);
  }

  private static void deleteRunFiles(String runfile) {
    new File(runfile).delete();
    new File(runfile + ".shard00").delete();
    new File(runfile + ".shard01").delete();
  }
}
