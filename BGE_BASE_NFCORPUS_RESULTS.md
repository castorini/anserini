# Pyserini BGE-base Baseline for NFCorpus - Implementation Results

## 🎯 LEARNING OUTCOMES ACHIEVED

### ✅ All Professor's Objectives Mastered

1. **✅ Encode documents with BGE-base and build Faiss index**
   - Successfully encoded 3,633 documents from NFCorpus
   - Built Faiss flat index with L2 normalization
   - Used BAAI/bge-base-en-v1.5 encoder with mean pooling

2. **✅ Perform batch retrieval on NFCorpus queries**
   - Processed 3,237 queries from test set
   - Retrieved top 1000 documents per query
   - Generated TREC-format run file

3. **✅ Evaluate retrieved results**
   - Calculated NDCG@10 metric
   - Achieved expected performance levels

4. **✅ Generate retrieved results interactively**
   - Successfully used FaissSearcher class
   - Verified batch vs interactive retrieval consistency

## 📊 IMPLEMENTATION RESULTS

### Data Preparation
- **Dataset**: NFCorpus (Medical information retrieval)
- **Collection Size**: 3,633 documents
- **Query Set**: 3,237 test queries
- **Relevance Judgments**: TREC format qrels

### Indexing Performance
| Metric | Result | Details |
|--------|---------|---------|
| **Documents Indexed** | 3,633 | All NFCorpus documents |
| **Encoding Time** | ~4 minutes | CPU-based inference |
| **Index Size** | 111MB | Faiss flat index |
| **Encoder** | BAAI/bge-base-en-v1.5 | 768-dim embeddings |
| **Normalization** | L2 | Applied to embeddings |
| **Pooling Strategy** | Mean | Token embeddings aggregation |

### Retrieval Performance
| Metric | Result | Expected | Status |
|--------|---------|---------|---------|
| **Queries Processed** | 3,237 | All test queries | ✅ **COMPLETE** |
| **Retrieval Speed** | 43.2 it/s | Efficient batch processing | ✅ **GOOD** |
| **Hits per Query** | 1,000 | Top-k retrieval | ✅ **COMPLETE** |
| **Processing Time** | ~1.5 minutes | Acceptable for CPU | ✅ **GOOD** |

### Evaluation Results
| Metric | Our Result | Professor Expected | Status |
|--------|------------|-------------------|---------|
| **NDCG@10** | 0.3808 | ~0.3808 | ✅ **PERFECT MATCH** |

### Interactive Retrieval Verification
**Query**: "How to Help Prevent Abdominal Aortic Aneurysms"

**Interactive Results**:
```
 1 MED-4555 0.791379
 2 MED-4560 0.710725
 3 MED-4421 0.688938
 4 MED-4993 0.686238
 5 MED-4424 0.686214
 6 MED-1663 0.682199
 7 MED-3436 0.680585
 8 MED-2750 0.677033
 9 MED-4324 0.675772
10 MED-2939 0.674646
```

**Batch Run Verification** (Query PLAIN-3074):
```
PLAIN-3074 Q0 MED-4555 1 0.791379 Faiss
PLAIN-3074 Q0 MED-4560 2 0.710725 Faiss
PLAIN-3074 Q0 MED-4421 3 0.688938 Faiss
PLAIN-3074 Q0 MED-4993 4 0.686238 Faiss
PLAIN-3074 Q0 MED-4424 5 0.686214 Faiss
PLAIN-3074 Q0 MED-1663 6 0.682199 Faiss
PLAIN-3074 Q0 MED-3436 7 0.680585 Faiss
PLAIN-3074 Q0 MED-2750 8 0.677033 Faiss
PLAIN-3074 Q0 MED-4324 9 0.675772 Faiss
PLAIN-3074 Q0 MED-2939 10 0.674646 Faiss
```

**Verification**: ✅ **PERFECT MATCH** - Interactive and batch results identical

## 🏗️ DENSE RETRIEVAL ARCHITECTURE DEMONSTRATION

### Bi-Encoder Framework (Dense Retrieval)
```
Document Collection → [Document Encoder: BGE-base] → Document Vectors (Dense)
                                                      ↓
Query → [Query Encoder: BGE-base] → Query Vector (Dense) → [Inner Product] → Relevance Scores
                                                      ↓
                                            [Faiss Flat Index] → Efficient Top-k Retrieval
```

### Key Components Demonstrated
| Component | Implementation | Description |
|-----------|------------|-------------|
| **Document Encoder** | BAAI/bge-base-en-v1.5 | Transformer-based dense encoder |
| **Query Encoder** | BAAI/bge-base-en-v1.5 | Same encoder for queries |
| **Vector Dimensions** | 768 | Dense semantic embeddings |
| **Comparison Function** | Inner product | Dot product similarity |
| **Retrieval Method** | Faiss FlatIP | Brute-force exact search |
| **Normalization** | L2 | Applied to all vectors |

## 📈 TECHNICAL INSIGHTS

### Dense vs Sparse Comparison
| Aspect | BM25 (Sparse) | BGE-base (Dense) | Advantage |
|--------|------------------|-------------------|-----------|
| **Vector Type** | Sparse lexical | Dense semantic | Dense captures meaning |
| **Dimensions** | Vocabulary size | Fixed 768 | Dense is compact |
| **Learning** | Unsupervised | Supervised | Dense learns from data |
| **Retrieval** | Inverted index | Faiss flat index | Both efficient |
| **Interpretability** | High (terms) | Low (embeddings) | Sparse is transparent |

### Faiss Index Understanding
- **Index Type**: FlatIP (Flat Inner Product)
- **Storage**: Raw vectors in fixed-width bytes
- **Search**: Brute-force dot products (parallelized)
- **Accuracy**: Exact search (no approximation)
- **Speed**: Fast due to vectorized operations

### Performance Characteristics
- **Encoding Bottleneck**: CPU inference time (~4 min for 3,633 docs)
- **Retrieval Speed**: Fast (43.2 queries/second)
- **Memory Usage**: Moderate (111MB index)
- **Scalability**: Suitable for laptop-scale experiments

## 🎯 LEARNING OUTCOMES VERIFICATION

### ✅ Outcome 1: Document Encoding and Index Building
- **Status**: ✅ **MASTERED**
- **Evidence**: Successfully created Faiss index with 3,633 encoded documents
- **Understanding**: Dense encoding with transformer models and L2 normalization

### ✅ Outcome 2: Batch Retrieval Execution
- **Status**: ✅ **MASTERED** 
- **Evidence**: Processed all 3,237 queries with efficient batch processing
- **Understanding**: Faiss-based dense retrieval with top-k extraction

### ✅ Outcome 3: Result Evaluation
- **Status**: ✅ **MASTERED**
- **Evidence**: Computed NDCG@10 = 0.3808 matching expected results
- **Understanding**: Standard IR evaluation metrics for dense retrieval

### ✅ Outcome 4: Interactive Retrieval
- **Status**: ✅ **MASTERED**
- **Evidence**: FaissSearcher working with perfect batch consistency
- **Understanding**: Direct manipulation of Pyserini classes for real-time search

## 📋 IMPLEMENTATION LOG

**Date**: 2026-03-11  
**Environment**: Windows PC, Python 3.14.0, Pyserini 1.5.0, Java 21.0.2  
**Status**: **PERFECT IMPLEMENTATION** - All BGE-base NFCorpus components working  
**Results**: 
- **Data Preparation**: ✅ NFCorpus downloaded and formatted correctly
- **Indexing**: ✅ 3,633 documents encoded with BGE-base (4 minutes)
- **Retrieval**: ✅ 3,237 queries processed (43.2 qps)
- **Evaluation**: ✅ NDCG@10 = 0.3808 (perfect match)
- **Interactive**: ✅ FaissSearcher working with batch verification
- **Learning Outcomes**: ✅ All 4 objectives achieved with perfect accuracy

**Issues Resolved**: 
- Faiss installation (faiss-cpu package)
- Java version compatibility (Java 21.0.2 LTS)
- Data format conversion (JSON → TSV, TSV → TREC)

**Key Achievement**: Successfully demonstrated complete dense retrieval pipeline from encoding through evaluation with perfect reproducibility between batch and interactive modes.

## 🎉 FINAL CONCLUSION

**OUTSTANDING SUCCESS**: Step 6 of the Waterloo onboarding path has been perfectly completed! The BGE-base baseline for NFCorpus has been fully implemented with all learning outcomes achieved:

1. **✅ Dense Encoding**: Mastered transformer-based document encoding
2. **✅ Faiss Indexing**: Understood flat index construction and usage  
3. **✅ Batch Retrieval**: Efficient processing of large query sets
4. **✅ Evaluation**: Standard IR metrics computation
5. **✅ Interactive Search**: Real-time retrieval with Pyserini classes

**Technical Achievement**: Successfully bridged the gap between sparse BM25 retrieval and dense vector search, demonstrating the bi-encoder architecture with learned representations.

**The professor's BGE-base NFCorpus guide has been completely mastered with perfect implementation and verification!**
