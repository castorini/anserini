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
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SearchShardedHnswDenseVectors}
 */
public class SearchShardedHnswDenseVectorsTest {
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

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.ERROR);
    Configurator.setLevel(IndexHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchShardedHnswDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(HnswDenseSearcher.class.getName(), Level.ERROR);
  }

  @Test
  public void testBasicShardedSearch() throws Exception {
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";

    String timestamp = String.valueOf(System.currentTimeMillis());
    String runfile = "target/run-sharded-" + timestamp;
    
    String[] searchArgs = new String[] {
        "-index", String.format("%s,%s", shardPath1, shardPath2),
        "-topics", "src/test/resources/sample_topics/arctic.tsv",
        "-output", runfile,
        "-topicReader", "TsvString",
        "-topicField", "title",
        "-encoder", "ArcticEmbedL",
        "-efSearch", "1000",
        "-hits", "5"};

    SearchShardedHnswDenseVectors.main(searchArgs);

    File f = new File(runfile);
    assertTrue(f.exists());
    assertTrue(f.length() > 0);
    f.delete();
  }

  @Test
  public void testShardedSearchWithPreEncodedVectors() throws Exception {
    String shardPath1 = "src/test/resources/prebuilt_indexes/fake-index-shard00";
    String shardPath2 = "src/test/resources/prebuilt_indexes/fake-index-shard01";

    String timestamp = String.valueOf(System.currentTimeMillis());
    String runfile = "target/run-sharded-vectors-" + timestamp;
    
    // Properly format the sharded index paths as a comma-separated list
    String[] searchArgs = new String[] {
        "-index", String.format("%s,%s", shardPath1, shardPath2),
        "-topics", "src/test/resources/sample_topics/arctic.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-efSearch", "1000",
        "-hits", "5"};

    SearchShardedHnswDenseVectors.main(searchArgs);

    File f = new File(runfile);
    assertTrue(f.exists());
    assertTrue(f.length() > 0);
    f.delete();
  }
}