#!/usr/bin/env python3

# Pyserini Conceptual Framework for Retrieval
# This script demonstrates the bi-encoder architecture and BM25 as a sparse representation

from pyserini.index.lucene import LuceneIndexReader
from pyserini.analysis import Analyzer, get_lucene_analyzer
from pyserini.search.lucene import LuceneSearcher
import json

def main():
    print("=== Pyserini Conceptual Framework for Retrieval ===\n")
    
    # Step 1: Extract BM25 document vector
    print("Step 1: Extracting BM25 document vector for docid 7187158...")
    index_reader = LuceneIndexReader('indexes/lucene-index-msmarco-passage')
    tf = index_reader.get_document_vector('7187158')
    bm25_weights = {
        term: index_reader.compute_bm25_term_weight('7187158', term, analyzer=None) 
        for term in tf.keys()
    }
    
    print("BM25 Document Vector:")
    print(json.dumps(bm25_weights, indent=4, sort_keys=True))
    print()
    
    # Step 2: Generate query representation (multi-hot vector)
    print("Step 2: Generating query representation...")
    analyzer = Analyzer(get_lucene_analyzer())
    query_tokens = analyzer.analyze('what is paula deen\'s brother')
    multihot_query_weights = {k: 1 for k in query_tokens}
    
    print(f"Query tokens: {query_tokens}")
    print(f"Query weights: {multihot_query_weights}")
    print()
    
    # Step 3: Compute inner product manually
    print("Step 3: Computing inner product manually...")
    # Using dictionary comprehension (more efficient)
    dot_product_dict = sum({
        term: bm25_weights[term] 
        for term in bm25_weights.keys() & multihot_query_weights.keys()
    }.values())
    
    print(f"Manual dot product: {dot_product_dict}")
    print(f"Expected: 17.949487686157227")
    print()
    
    # Step 4: Verify with Lucene search
    print("Step 4: Verifying with Lucene search...")
    searcher = LuceneSearcher('indexes/lucene-index-msmarco-passage')
    hits = searcher.search('what is paula deen\'s brother')
    
    print("Top 10 results:")
    for i in range(0, 10):
        print(f'{i+1:2} {hits[i].docid:7} {hits[i].score:.5f}')
    
    print(f"\nHit 1 score: {hits[0].score:.5f}")
    print(f"Manual computation: {dot_product_dict:.5f}")
    print(f"Match: {abs(hits[0].score - dot_product_dict) < 0.001}")
    print()
    
    # Step 5: Demonstrate bi-encoder concept
    print("Step 5: Bi-Encoder Architecture Demonstration")
    print("=" * 50)
    print("Document Encoder: BM25 (sparse lexical vector)")
    print("Query Encoder: Multi-hot vector")
    print("Comparison Function: Inner product (dot product)")
    print("Retrieval Method: Inverted index for efficient top-k")
    print()
    
    # Step 6: Show sparse vs dense concept
    print("Step 6: Sparse vs Dense Representations")
    print("=" * 50)
    print(f"BM25 Vector Dimensions: {len(bm25_weights)} non-zero terms")
    print(f"Vector Type: Sparse lexical (bag-of-words)")
    print(f"Learning: Unsupervised (heuristic BM25 formula)")
    print()
    
    print("For dense retrieval:")
    print("- Vector Type: Dense semantic (embeddings)")
    print("- Learning: Supervised (transformer models)")
    print("- Retrieval: HNSW index for approximate nearest neighbor")
    print()
    
    print("=== Conceptual Framework Complete ===")

if __name__ == "__main__":
    main()
