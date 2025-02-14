/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.index;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ShardInfoTest {

  @Test
  public void testFromIdentifier() {
    String identifier = "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8";
    ShardInfo shardInfo = ShardInfo.fromIdentifier(identifier);
    assertNotNull(String.format("Expected non-null ShardInfo for identifier '%s'", identifier), shardInfo);
    assertEquals(String.format("Expected identifier '%s', got '%s'", identifier, shardInfo.getIdentifier()),
                identifier, shardInfo.getIdentifier());
    assertEquals(String.format("Expected 10 shards, got %d shards", shardInfo.getShards().length),
                10, shardInfo.getShards().length);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFromIdentifierInvalid() {
    ShardInfo.fromIdentifier("invalid-identifier");
  }

  @Test
  public void testContainsAndGetShardValid() {
    ShardInfo shardInfo = ShardInfo.fromIdentifier("msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8");
    String validIndexName = shardInfo.getShards()[0].indexName;

    assertTrue(String.format("Expected containsShard to return true for index '%s'", validIndexName),
              ShardInfo.containsShard(validIndexName));

    IndexInfo retrieved = ShardInfo.getShard(validIndexName);
    assertNotNull(String.format("Expected non-null IndexInfo for index '%s'", validIndexName), retrieved);
    assertEquals(String.format("Expected index name '%s', got '%s'", validIndexName, retrieved.indexName),
                validIndexName, retrieved.indexName);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetShardInvalid() {
    ShardInfo.getShard("nonexistent-index");
  }

  @Test
  public void testFindShardGroupValid() {
    ShardInfo shardInfo = ShardInfo.fromIdentifier("msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8");
    String validIndexName = shardInfo.getShards()[0].indexName;

    ShardInfo foundGroup = ShardInfo.findShardGroup(validIndexName);
    assertNotNull(String.format("Expected non-null ShardInfo for index '%s'", validIndexName), foundGroup);
    assertEquals(String.format("Expected group identifier '%s', got '%s'",
                             "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8", foundGroup.getIdentifier()),
                "msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8", foundGroup.getIdentifier());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testFindShardGroupInvalid() {
    ShardInfo.findShardGroup("nonexistent-index");
  }
}