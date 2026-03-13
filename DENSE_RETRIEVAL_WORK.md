# Dense Retrieval for MS MARCO Passage Ranking - Process Documentation

## Overview
This document details the process of implementing dense retrieval for MS MARCO passage ranking using Anserini with prebuilt indexes and BGE model.

## Prerequisites
- Anserini with Java 21 (for full compilation)
- Access to prebuilt indexes from UWaterloo servers
- Sufficient disk space (26 GB for HNSW index)
- Python environment for evaluation

## Step 1: BM25 Retrieval with Prebuilt Index

### Command Intended:
```bash
bin/run.sh io.anserini.search.SearchCollection \
  -index msmarco-v1-passage \
  -topics collections/msmarco-passage/queries.dev.small.tsv \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.dev.bm25.txt \
  -parallelism 4 \
  -bm25 -bm25.k1 0.82 -bm25.b 0.68 -hits 1000
```

### What This Does:
- Uses prebuilt index `msmarco-v1-passage` instead of local index
- Downloads index from UWaterloo servers to `~/.cache/pyserini/indexes`
- Runs BM25 retrieval with tuned parameters (k1=0.82, b=0.68)
- Outputs results in TREC format

### Expected Result:
- MRR@10 should be 0.1875
- Slight difference from previous lesson due to format conversion differences

### Current Status:
⚠️ **Issue**: Cannot compile Anserini due to Java version incompatibility
✅ **Workaround**: Use existing run files from previous lesson

## Step 2: Dense Retrieval with BGE Model

### Command:
```bash
bin/run.sh io.anserini.search.SearchHnswDenseVectors \
  -index msmarco-v1-passage.bge-base-en-v1.5.hnsw \
  -topics collections/msmarco-passage/queries.dev.small.tsv \
  -topicReader TsvInt \
  -output runs/run.msmarco-passage.dev.bge.txt \
  -encoder BgeBaseEn15 -hits 1000 -threads 4
```

### Key Differences from BM25:
1. **Different Class**: `SearchHnswDenseVectors` instead of `SearchCollection`
2. **Different Index**: HNSW index for dense vectors (26 GB)
3. **Encoder Parameter**: Specifies BGE-base model (`BgeBaseEn15`)
4. **Threading**: Uses `-threads 4` instead of `-parallelism 4`

### What Happens:
1. Downloads HNSW index from UWaterloo servers
2. Loads BGE-base encoder model
3. Converts queries to dense vectors using BGE
4. Performs approximate nearest neighbor search using HNSW
5. Returns top 1000 most similar passages

### Expected Performance:
- **MRR@10**: 0.3521 (significantly better than BM25's 0.1875)
- **Runtime**: ~4 minutes on M2 MacBook with 24GB RAM
- **Index Size**: 26 GB (much larger than BM25 index)

### Troubleshooting:
If encoder loading errors occur:
```bash
rm -rf ~/.cache/pyserini/encoders
```

## Step 3: Evaluation

### BM25 Evaluation:
```bash
bin/trec_eval -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.bm25.txt
```

### Dense Retrieval Evaluation:
```bash
bin/trec_eval -c -M 10 -m recip_rank \
  collections/msmarco-passage/qrels.dev.small.trec \
  runs/run.msmarco-passage.dev.bge.txt
```

## Key Concepts Learned

### Prebuilt Indexes
- Eliminate need for local indexing
- Stored on UWaterloo servers
- Automatic download to `~/.cache/pyserini/indexes`
- Available for both BM25 and dense retrieval

### Dense Retrieval vs BM25
| Aspect | BM25 | Dense Retrieval (BGE) |
|--------|------|----------------------|
| Index Type | Inverted | HNSW |
| Index Size | Smaller | 26 GB |
| MRR@10 | 0.1875 | 0.3521 |
| Retrieval Method | Lexical matching | Semantic similarity |
| Model | Statistical | Neural (BGE) |

### HNSW Indexes
- Hierarchical Navigable Small World graphs
- Enable fast approximate nearest neighbor search
- Essential for large-scale dense retrieval
- Trade memory for speed

### BGE Model
- BGE-base-en-v1.5: Base English BGE model
- Trained for semantic search
- Converts text to 768-dimensional vectors
- State-of-the-art for passage retrieval

## Production Implementation Considerations

### Infrastructure Requirements
1. **Storage**: 26+ GB for dense indexes
2. **Memory**: Sufficient RAM for HNSW graphs
3. **Compute**: GPU acceleration for encoding (optional but recommended)
4. **Network**: Bandwidth for index downloads

### Performance Optimization
1. **Index Caching**: Keep indexes in fast storage
2. **Model Caching**: Pre-load encoder models
3. **Batch Processing**: Process multiple queries together
4. **Threading**: Proper thread configuration

### Hybrid Approaches
- Combine BM25 and dense retrieval for better results
- Use BM25 for initial filtering, dense for reranking
- Implement score fusion techniques

## Current Implementation Status

### Completed ✅
1. Environment setup and Java version identification
2. BM25 retrieval with local indexes
3. Evaluation pipeline setup
4. Understanding of dense retrieval concepts

### Blocked ⚠️
1. Prebuilt index usage (Java compilation issue)
2. Dense retrieval execution (requires compiled Anserini)
3. HNSW index download and usage

### Workarounds Applied
1. Used existing run files for evaluation
2. Documented expected processes and results
3. Created comprehensive implementation guide

## Next Steps for Full Implementation

1. **Resolve Java Version Issue**: Upgrade to Java 21 or find compatible build
2. **Complete Dense Retrieval**: Execute BGE-based retrieval commands
3. **Performance Comparison**: Run actual benchmarks between BM25 and dense
4. **Production Deployment**: Set up web service and API endpoints
5. **Scaling**: Implement distributed retrieval for large-scale usage

## Files and Commands Reference

### Prebuilt Index Locations
- BM25: `msmarco-v1-passage`
- Dense: `msmarco-v1-passage.bge-base-en-v1.5.hnsw`

### Cache Directories
- Indexes: `~/.cache/pyserini/indexes`
- Encoders: `~/.cache/pyserini/encoders`

### Evaluation Commands
```bash
# MRR@10 evaluation
bin/trec_eval -c -M 10 -m recip_rank [qrels] [run]

# Full metrics
bin/trec_eval -c -mrecall.1000 -mmap [qrels] [run]
```

---
*Documentation created: 2025-03-06*
*Target: Dense Retrieval with BGE model*
*Status: Process documented, execution blocked by Java version*
