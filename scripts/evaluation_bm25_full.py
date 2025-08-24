# evaluation_bm25_full.py
# Compute MRR@10, MAP, and Recall@1000 for BM25 on all dev queries

import os

# --- 0. Define file paths relative to the project root ---
ROOT_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))

qrels_file = os.path.join(ROOT_DIR, "collections", "msmarco-passage", "qrels.dev.small.tsv")
run_file = os.path.join(ROOT_DIR, "runs", "run.msmarco-passage.dev.bm25.tsv")

# --- 1. Load qrels ---
qrels = {}
with open(qrels_file, encoding="utf-8") as f:
    for line in f:
        qid, _, docid, rel = line.strip().split()
        rel = int(rel)
        if rel > 0:
            qrels.setdefault(qid, set()).add(docid)

# --- 2. Load BM25 run output ---
runs = {}
with open(run_file, encoding="utf-8") as f:
    for line in f:
        qid, docid, rank = line.strip().split()
        rank = int(rank)
        runs.setdefault(qid, []).append(docid)

# --- 3. Compute MRR@10 ---
mrr_scores = []
for qid, rel_docs in qrels.items():
    rr = 0
    for i, docid in enumerate(runs.get(qid, [])[:10], 1):
        if docid in rel_docs:
            rr = 1.0 / i
            break
    mrr_scores.append(rr)
mrr_10 = sum(mrr_scores) / len(mrr_scores)
print(f"✅ MRR@10: {mrr_10:.4f}")

# --- 4. Compute MAP and Recall@1000 ---
map_scores = []
recall_scores = []
for qid, rel_docs in qrels.items():
    retrieved = runs.get(qid, [])[:1000]
    num_rel = len(rel_docs)
    num_rel_retrieved = 0
    ap_sum = 0
    for i, docid in enumerate(retrieved, 1):
        if docid in rel_docs:
            num_rel_retrieved += 1
            ap_sum += num_rel_retrieved / i
    if num_rel > 0:
        map_scores.append(ap_sum / num_rel)
        recall_scores.append(num_rel_retrieved / num_rel)
    else:
        map_scores.append(0)
        recall_scores.append(0)

map_val = sum(map_scores) / len(map_scores)
recall_val = sum(recall_scores) / len(recall_scores)
print(f"✅ MAP: {map_val:.4f}")
print(f"✅ Recall@1000: {recall_val:.4f}")

# --- 5. Number of queries processed ---
print(f"Queries evaluated: {len(qrels)}")
