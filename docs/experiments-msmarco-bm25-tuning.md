# Reproducing MS MARCO BM25 tuning

Run these commands from the Anserini repository root. Requirements are Java 21,
Maven 3.9+, Python 3 (standard library only), `curl`, and `gzip`. Build the current
checkout with `bin/qbuild.sh`. Retrieval uses `SearchCollection`; evaluation uses
the bundled native `trec_eval` through `io.anserini.eval.TrecEval`. Pyserini and
an external `tools/` checkout are not required.

## Training inputs

Use the five published samples for each task, rather than drawing new samples.
Each file contains 10,000 query IDs and query texts, separated by a tab.

```bash
mkdir -p tmp/bm25-tuning/inputs
for task in passage doc; do
  for sample in 1 2 3 4 5; do
    curl -fL "https://raw.githubusercontent.com/castorini/anserini-data/master/MSMARCO/msmarco-${task}.train.sample10k-${sample}.tsv.gz" \
      -o "tmp/bm25-tuning/inputs/msmarco-${task}.train.sample10k-${sample}.tsv.gz"
    gzip -dc "tmp/bm25-tuning/inputs/msmarco-${task}.train.sample10k-${sample}.tsv.gz" \
      > "tmp/bm25-tuning/inputs/msmarco-${task}.train.sample10k-${sample}.tsv"
  done
done

curl -fL https://msmarco.z22.web.core.windows.net/msmarcoranking/qrels.train.tsv \
  -o tmp/bm25-tuning/inputs/qrels.passage.train.tsv
curl -fL https://msmarco.z22.web.core.windows.net/msmarcoranking/msmarco-doctrain-qrels.tsv.gz \
  -o tmp/bm25-tuning/inputs/qrels.doc.train.tsv.gz
gzip -dc tmp/bm25-tuning/inputs/qrels.doc.train.tsv.gz \
  > tmp/bm25-tuning/inputs/qrels.doc.train.tsv
```

Both judgment files already have four whitespace-separated TREC qrels columns:
`query-id 0 document-id relevance`. No conversion is needed. The tuner accepts
these through `--qrels-trec`; the legacy `--qrels-tsv` option is optional and, if
supplied, must contain the same judgments. It never substitutes passage judgments
for document judgments.

Some sampled passage queries have no judgments. The tuner writes a separate
qrels subset for each sample and reports the total and judged query counts.
Metrics use `trec_eval -c` on that subset: judged queries with no retrieved hits
score zero, and queries outside the sample do not affect the denominator.

## Sweeps and averaging

For passages, the checked-in historical grid contains 35 parameter pairs:
`k1=0.6,...,1.2` and `b=0.5,...,0.9`, both in steps of 0.1.

```bash
python3 src/main/python/msmarco/tune_bm25.py \
  --base-directory tmp/bm25-tuning/passage \
  --task passage --index msmarco-v1-passage \
  --queries tmp/bm25-tuning/inputs/msmarco-passage.train.sample10k-{1,2,3,4,5}.tsv \
  --qrels-trec tmp/bm25-tuning/inputs/qrels.passage.train.tsv \
  --k1 0.6:1.2:0.1 --b 0.5:0.9:0.1 --threads 8 --discard-runs
```

The original document grid was not preserved. The following explicitly defined
replacement searches `k1=0.5,...,5.0` in steps of 0.1 and `b=0.5,...,1.0` in steps
of 0.05: 506 pairs per sample, or 2,530 searches. This is a new tuning experiment,
not an exact reconstruction of the undocumented historical grid. It can take
many hours; use `--dry-run` to inspect the commands first.

```bash
python3 src/main/python/msmarco/tune_bm25.py \
  --base-directory tmp/bm25-tuning/doc \
  --task doc --index msmarco-v1-doc-slim \
  --queries tmp/bm25-tuning/inputs/msmarco-doc.train.sample10k-{1,2,3,4,5}.tsv \
  --qrels-trec tmp/bm25-tuning/inputs/qrels.doc.train.tsv \
  --k1 0.5:5.0:0.1 --b 0.5:1.0:0.05 --threads 8 --discard-runs
```

Ranges are inclusive; comma-separated lists are also accepted. Supply explicit
document grids: the script's default grid is the historical **passage** grid.
The named prebuilt indexes download on first use. Alternatively, pass an absolute
path to an existing index to avoid metadata lookup on each search. The document
slim index has the same BM25 postings and norms as the standard document index;
stored text, positions, and document vectors are unnecessary for this experiment.
Do not substitute a document-expansion or learned sparse index.

Each search retrieves 1,000 hits in MS MARCO format, then invokes the checked-in
`convert_msmarco_to_trec_run.py` converter. Its TREC scores are reciprocal ranks,
which preserve the emitted ranking even when BM25 scores tie. This retains the
original passage tuner's conversion behavior and agrees with the leaderboard's
rank-based MRR convention. Evaluating native BM25 scores directly with `trec_eval`
can reorder tied documents and give slightly different MRR and MAP values.
The tuner evaluates
MAP and recall@1000 on all hits, and MRR with `-M 10` for passages or `-M 100` for
documents. It chooses the best pair separately for each sample and objective,
then takes the arithmetic mean of the five winning `k1` values and, separately,
the five winning `b` values. It does not round the means back to the search grid.
Ties at `trec_eval`'s four-decimal output precision favor smaller `k1`, then smaller
`b`. Historical MRR results used a different evaluator with more printed digits,
so near-ties can change the selected pair.

Each base directory contains:

- `settings.json`: input paths, grids, task, and SHA-256 hashes of queries,
  judgments, the tuning script, the converter, and the fatjar.
- `sample-N/qrels.txt`: that sample's judgment subset.
- `sample-N/run.bm25.*.log` and `.json`: retrieval logs and completed metric checkpoints.
- `samples.json` and `summary.json`: per-sample winners and averaged parameter pairs.

Rerun the same command to resume. Only completed score checkpoints are reused;
partial runs are searched again. Changed inputs, code, or grids require a new
base directory. Keep the underlying local index unchanged when resuming.
`--discard-runs` removes each large MS MARCO and TREC file after saving its scores; omit it to
retain every run. With 10 million run lines per parameter pair, retained runs
can consume hundreds of gigabytes. Logs and scores are always retained.

## Development-set evaluation

After all five samples finish, evaluate the averaged parameters on the development
set. The following loop works for either task; set `task=passage` or `task=doc`.
Prebuilt topic and qrels names download their small files on first use.

```bash
task=passage
if [ "$task" = passage ]; then
  index=msmarco-v1-passage
  topics=msmarco-passage.dev-subset
  cutoff=10
else
  index=msmarco-v1-doc-slim
  topics=msmarco-doc.dev
  cutoff=100
fi

python3 - "$task" <<'PY' > "tmp/bm25-tuning/${task}/averages.tsv"
import json
import sys
with open(f'tmp/bm25-tuning/{sys.argv[1]}/summary.json') as source:
    summary = json.load(source)
assert len(summary['samples']) == 5
for objective, pair in summary['averages'].items():
    print(objective, pair['k1'], pair['b'])
PY

while read -r objective k1 b; do
  run="tmp/bm25-tuning/${task}/dev.${objective}.trec"
  bin/run.sh io.anserini.search.SearchCollection \
    -index "$index" -topics "$topics" -output "$run.msmarco" -format msmarco \
    -threads 8 -bm25 -bm25.k1 "$k1" -bm25.b "$b" -hits 1000
  python3 src/main/python/msmarco/convert_msmarco_to_trec_run.py \
    --input "$run.msmarco" --output "$run"
  bin/run.sh io.anserini.eval.TrecEval -c -m map -m recall.1000 "$topics" "$run"
  bin/run.sh io.anserini.eval.TrecEval -c -M "$cutoff" -m recip_rank "$topics" "$run"
done < "tmp/bm25-tuning/${task}/averages.tsv"
```

For comparison, rerun development retrieval with the historical parameter pairs
in the [passage](experiments-msmarco-passage.md#bm25-tuning) and
[document](experiments-msmarco-doc.md#bm25-tuning) tables. Evaluating those fixed
pairs checks the old development results; it does not establish that the training
sweeps selected the same parameters.

## Verification status

Local verification on 2026-09-11 started from commit
`58217175b415efb953fdf5d7e84d3773ae09f966` with the accompanying local fixes,
Java 21.0.7, Maven 3.9.9, and Lucene 10.5.0. The 2022 prebuilt passage index
and document slim index were used; the latter's archive MD5 is
`1ac67c1150d5e6c9ec2b70b3ce1fb5e0`. Input and runtime hashes are recorded in the
sweep manifests.

The following development measurements evaluate the **historical fixed parameter
pairs**, with the rank-preserving conversion above. They do not establish that
a new sweep selects those parameters. MRR uses cutoff 10 for passages and 100 for
documents; MAP and recall use all 1,000 retrieved hits.

| Task | k1 | b | MRR | MAP | Recall@1000 |
|:-----|---:|--:|----:|----:|------------:|
| passage | 0.6 | 0.62 | 0.1892 | 0.1972 | 0.8555 |
| passage | 0.82 | 0.68 | 0.1874 | 0.1957 | 0.8573 |
| passage | 0.9 | 0.4 | 0.1840 | 0.1926 | 0.8526 |
| doc | 0.9 | 0.4 | 0.2299 | 0.2306 | 0.8856 |
| doc | 3.8 | 0.87 | 0.2784 | 0.2790 | 0.9322 |
| doc | 4.46 | 0.82 | 0.2766 | 0.2772 | 0.9357 |

All three passage rows match the historical table at four decimal places.
Rank preservation matters: evaluating the native-score passage run with
`k1=0.82`, `b=0.68` directly gives MRR@10 0.1875 and MAP 0.1958, versus 0.1874
and 0.1957 after conversion. At `k1=0.6`, `b=0.62`, native-score MRR is 0.1891,
versus rank-preserving MRR 0.1892. These differences arise without changing the
retrieval run: `trec_eval` orders tied scores differently from the emitted ranks.

The document rows retain differences of up to 0.0004 from the historical table,
even after preserving ranks. The original table used an older index/Lucene
combination; the precise cause of these remaining differences has not been
isolated. Keep the historical measurements labeled as such. For comparison,
the current maintained `msmarco-v1-doc` regression uses native-score TREC evaluation:
its default MAP 0.2305, MRR@100 0.2299, and recall@1000 0.8856 were also verified.

For the published passage samples, the numbers of judged queries are
6,205, 6,222, 6,167, 6,309, and 6,205. All five document samples have 10,000 judged
queries. Seven Python regression tests and 41 targeted Java tests passed.
Small end-to-end sweeps across five samples per task also verified conversion,
metric cutoffs, parameter averaging, and checkpoint reuse.

The full five-sample passage and replacement document sweeps are running locally;
development evaluation of their averaged parameters remains pending. No full-sweep
parameter averages are claimed here yet.
