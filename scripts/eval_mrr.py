from pathlib import Path

# project root
root = Path(__file__).parent.parent

qrels_file = root / "collections/msmarco-passage/qrels.dev.small.trec"
run_file = root / "runs/run.msmarco-passage.dev.bge.txt"

qrels = {}
with open(qrels_file) as f:
    for line in f:
        qid, _, docid, rel = line.strip().split()
        if int(rel) > 0:
            qrels.setdefault(qid, set()).add(docid)

runs = {}
with open(run_file) as f:
    for line in f:
        qid, _, docid, rank, score, _ = line.strip().split()
        runs.setdefault(qid, []).append(docid)

mrr_total = 0
for qid, docs in runs.items():
    rr = 0
    for i, docid in enumerate(docs[:10], start=1):
        if qid in qrels and docid in qrels[qid]:
            rr = 1 / i
            break
    mrr_total += rr

mrr_at_10 = mrr_total / len(runs)
print(f"MRR@10: {mrr_at_10:.4f}")
