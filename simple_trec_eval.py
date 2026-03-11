#!/usr/bin/env python3
import sys

def calculate_map_and_recall(qrels_file, run_file, query_id_filter=None):
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
    
    # Calculate metrics
    total_map = 0.0
    total_recall_1000 = 0.0
    total_mrr10 = 0.0
    query_count = 0
    
    for query_id in qrels:
        if query_id not in run:
            continue
            
        query_count += 1
        relevant_docs = set(doc_id for doc_id, rel in qrels[query_id].items() if rel > 0)
        total_relevant = len(relevant_docs)
        
        if total_relevant == 0:
            continue
        
        # Calculate MAP
        retrieved_docs = run[query_id][:1000]  # Top 1000
        precision_sum = 0.0
        relevant_found = 0
        
        for i, (doc_id, rank, score) in enumerate(retrieved_docs, 1):
            if doc_id in relevant_docs:
                relevant_found += 1
                precision_sum += relevant_found / i
        
        ap = precision_sum / total_relevant if total_relevant > 0 else 0.0
        total_map += ap
        
        # Calculate Recall@1000
        retrieved_set = set(doc_id for doc_id, rank, score in retrieved_docs)
        relevant_retrieved = len(relevant_docs.intersection(retrieved_set))
        recall_1000 = relevant_retrieved / total_relevant if total_relevant > 0 else 0.0
        total_recall_1000 += recall_1000
        
        # Calculate MRR@10
        retrieved_10 = run[query_id][:10]  # Top 10
        mrr10 = 0.0
        for i, (doc_id, rank, score) in enumerate(retrieved_10, 1):
            if doc_id in relevant_docs:
                mrr10 = 1.0 / i
                break
        total_mrr10 += mrr10
        
        # Print per-query MRR@10 if requested
        if query_id_filter is not None and query_id_filter == query_id:
            print(f"{query_id} {mrr10:.6f}")
    
    # Calculate averages
    avg_map = total_map / query_count if query_count > 0 else 0.0
    avg_recall_1000 = total_recall_1000 / query_count if query_count > 0 else 0.0
    avg_mrr10 = total_mrr10 / query_count if query_count > 0 else 0.0
    
    print(f"map                     all     {avg_map:.4f}")
    print(f"recall_1000.0           all     {avg_recall_1000:.4f}")
    print(f"recip_rank              all     {avg_mrr10:.4f}")
    print(f"num_q                   all     {query_count}")

if __name__ == "__main__":
    if len(sys.argv) < 3:
        print("Usage: python simple_trec_eval.py <qrels_file> <run_file> [query_id]")
        sys.exit(1)
    
    qrels_file = sys.argv[1]
    run_file = sys.argv[2]
    query_id_filter = sys.argv[3] if len(sys.argv) > 3 else None
    calculate_map_and_recall(qrels_file, run_file, query_id_filter)
