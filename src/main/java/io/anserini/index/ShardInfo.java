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

// Only referenced by the REST API class for simplicity.
// Dedicated sharded searcher class uses comma-separated list of shards.
public enum ShardInfo {
  MSMARCO_V21_DOC_SEGMENTED_ARCTIC_EMBED_L_HNSW_INT8("msmarco-v2.1-doc-segmented.arctic-embed-l.hnsw-int8",
    new IndexInfo[] {
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD00_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD01_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD02_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD03_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD04_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD05_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD06_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD07_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD08_ARCTIC_EMBED_L_HNSW_INT8,
      IndexInfo.MSMARCO_V21_DOC_SEGMENTED_SHARD09_ARCTIC_EMBED_L_HNSW_INT8
  });

  private final String identifier;
  private final IndexInfo[] shards;

  ShardInfo(String identifier, IndexInfo[] shards) {
    this.identifier = identifier;
    this.shards = shards;
  }

  public String getIdentifier() { return identifier; }
  public IndexInfo[] getShards() { return shards; }

  public static boolean containsShard(String indexName) {
   return IndexInfo.contains(indexName);
  }

  public static IndexInfo getShard(String indexName) {
    return IndexInfo.get(indexName);
  }

  public static ShardInfo fromIdentifier(String identifier) {
    for (ShardInfo collection : values()) {
      if (collection.identifier.equals(identifier)) {
        return collection;
      }
    }
    throw new IllegalArgumentException("No collection found for identifier: " + identifier);
  }

  public static ShardInfo findShardGroup(String indexName) {
    for (ShardInfo info : values()) {
      for (IndexInfo shard : info.shards) {
        if (shard.indexName.equals(indexName)) {
          return info;
        }
      }
    }
    throw new IllegalArgumentException("No shard group contains index: " + indexName);
  }
}
