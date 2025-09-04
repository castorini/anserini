from pathlib import Path
from pyserini.search import SimpleDenseSearcher

root = Path(__file__).parent.parent

index_dir = root / "indexes/msmarco-v1-passage.bge-base-en-v1.5"
queries_file = root / "collections/msmarco-passage/queries.dev.small.tsv"
output_file = root / "runs/run.msmarco-passage.dev.bge.txt"

searcher = SimpleDenseSearcher(str(index_dir), "castorini/bge-base-en-v1.5")

queries = {}
with open(queries_file) as f:
    for line in f:
        qid, text = line.strip().split("\t")
        queries[qid] = text

with open(output_file, "w") as out:
    for qid, text in queries.items():
        hits = searcher.search(text, k=1000)
        for rank, hit in enumerate(hits, start=1):
            out.write(f"{qid} Q0 {hit.docid} {rank} {hit.score} BGE\n")

print(f"Done. Results saved to {output_file}")
