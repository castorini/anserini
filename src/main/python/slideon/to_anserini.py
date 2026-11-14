"""Convert the Parquet sparse vectors into Anserini inputs (JsonVectorCollection corpus, TsvString topics).

Corpus:  {"id": "...", "contents": "", "vector": {"<latent>": int_weight, ...}}
Queries: "<qid>\t<latent> <latent> ..." (TsvString: each latent repeated int_weight times, as in
         Anserini's SPLADE regressions) or, for a .jsonl dst, {"qid": "...", "vector": {...}}
Vectors are cut to their top-k prefix (each row is sorted by decreasing weight), then weights are
quantized as round(weight * scale); entries that round to 0 are dropped.

  python to_anserini.py corpus/ out.jsonl.gz --k 512
  python to_anserini.py queries/test/ topics.tsv.gz --k 128 --query
"""
import argparse
import glob
import gzip
import json
from pathlib import Path

import numpy as np
import pyarrow.parquet as pq


def main():
    p = argparse.ArgumentParser()
    p.add_argument("src", help="Directory of part-*.parquet files")
    p.add_argument("dst", help="Output .jsonl or .jsonl.gz")
    p.add_argument("--k", type=int, default=None, help="Keep the k strongest latents (default: all stored)")
    p.add_argument("--scale", type=float, default=100.0)
    p.add_argument("--query", action="store_true")
    a = p.parse_args()
    Path(a.dst).parent.mkdir(parents=True, exist_ok=True)
    opener = gzip.open if a.dst.endswith(".gz") else open
    with opener(a.dst, "wt", encoding="utf-8") as out:
        for f in sorted(glob.glob(f"{a.src}/part-*.parquet")):
            t = pq.read_table(f)
            for doc_id, idx, w in zip(t["id"].to_pylist(), t["indices"].to_numpy(zero_copy_only=False),
                                      t["weights"].to_numpy(zero_copy_only=False)):
                q = np.rint(w[:a.k].astype(np.float32) * a.scale).astype(np.int64)
                keep = q > 0
                vector = dict(zip(map(str, idx[:a.k][keep].tolist()), q[keep].tolist()))
                if a.query and ".tsv" in a.dst:
                    out.write(doc_id + "\t" + " ".join(" ".join([t] * n) for t, n in vector.items()) + "\n")
                    continue
                row = {"qid": doc_id, "vector": vector} if a.query else {"id": doc_id, "contents": "", "vector": vector}
                out.write(json.dumps(row) + "\n")


if __name__ == "__main__":
    main()
