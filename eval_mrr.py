from collections import defaultdict

qrels_file = "collections/msmarco-passage/qrels.dev.small.tsv"
run_file = "runs/run.msmarco-passage.bm25.k1-0.6.b0.4.dev.small.trec"

qrels = defaultdict(set)

with open(qrels_file, encoding="utf-8") as f:
    for line in f:
        p = line.split()
        if len(p) >= 4 and int(p[3]) > 0:
            qrels[int(p[0])].add(p[2])

runs = defaultdict(list)

with open(run_file, encoding="utf-8") as f:
    for line in f:
        p = line.split()
        if len(p) >= 3:
            runs[int(p[0])].append(p[2])

scores = []

for qid, docs in runs.items():
    score = 0.0
    for rank, docid in enumerate(docs[:10], 1):
        if docid in qrels[qid]:
            score = 1.0 / rank
            break
    scores.append(score)

print("Queries evaluated:", len(scores))
print("MRR@10: %.6f" % (sum(scores) / len(scores)))
