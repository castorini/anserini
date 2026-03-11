#!/usr/bin/env python3
import sys
import math

def dcg_at_k(relevances, k):
    """Calculate DCG@k"""
    relevances = relevances[:k]
    dcg = 0.0
    for i, rel in enumerate(relevances, 1):
        dcg += rel / math.log2(i + 1) if i <= k else 0
    return dcg

def ndcg_at_k(qrels_file, run_file, k=10):
    """Calculate NDCG@k"""
    # Load qrels
    qrels = {}
    with open(qrels_file, 'r') as f:
        for line in f:
            parts = line.strip().split()
            if len(parts) >= 4:
                query_id, _, doc_id, rel = parts[0], parts[1], parts[2], int(parts[3])
                if query_id not in qrels:
                    qrels[query_id] = {}
                qrels[query_id][doc_id] = rel
    
    # Load run
    run = {}
    with open(run_file, 'r') as f:
        for line in f:
            parts = line.strip().split()
            if len(parts) >= 6:
                query_id, _, doc_id, rank, score, _ = parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]
                if query_id not in run:
                    run[query_id] = []
                run[query_id].append((doc_id, int(rank), float(score)))
    
    # Calculate NDCG
    total_ndcg = 0.0
    query_count = 0
    
    for query_id in qrels:
        if query_id not in run:
            continue
            
        query_count += 1
        
        # Get relevance scores for retrieved documents
        retrieved_docs = run[query_id][:k]
        relevances = []
        for doc_id, rank, score in retrieved_docs:
            rel = qrels[query_id].get(doc_id, 0)
            relevances.append(rel)
        
        # Calculate ideal DCG (perfect ranking)
        ideal_relevances = sorted([rel for rel in qrels[query_id].values() if rel > 0], reverse=True)
        ideal_dcg = dcg_at_k(ideal_relevances, k)
        
        # Calculate actual DCG
        actual_dcg = dcg_at_k(relevances, k)
        
        # Calculate NDCG
        if ideal_dcg > 0:
            ndcg = actual_dcg / ideal_dcg
        else:
            ndcg = 0.0
            
        total_ndcg += ndcg
    
    return total_ndcg / query_count if query_count > 0 else 0.0

if __name__ == "__main__":
    if len(sys.argv) != 3:
        print('Usage: python ndcg_eval.py <qrels> <run>')
        sys.exit(1)
    
    qrels_file = sys.argv[1]
    run_file = sys.argv[2]
    
    ndcg_10 = ndcg_at_k(qrels_file, run_file, k=10)
    print(f'ndcg_cut_10\t\tall\t{ndcg_10:.4f}')
