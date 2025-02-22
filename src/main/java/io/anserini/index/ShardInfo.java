package io.anserini.index;

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
