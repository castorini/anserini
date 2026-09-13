#
# Pyserini: Python interface to the Anserini IR toolkit built on Lucene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

"""Tune BM25 independently on training samples and average the winning parameters."""

import argparse
import hashlib
import json
import shlex
import subprocess
import sys
from decimal import Decimal
from pathlib import Path

ROOT = Path(__file__).resolve().parents[4]
RUNNER = ROOT / 'bin/run.sh'


def grid(value):
    """Parse comma-separated values or an inclusive start:stop:step range."""
    if ':' in value:
        start, stop, step = map(Decimal, value.split(':'))
        if step <= 0 or stop < start:
            raise argparse.ArgumentTypeError('Grid needs a positive step and stop >= start')
        values = []
        while start <= stop:
            values.append(float(start))
            start += step
    else:
        values = [float(v) for v in value.split(',')]
    return sorted(set(values))


def read_qrels(path):
    rows = {}
    with open(path) as source:
        for line in source:
            qid, iteration, docid, relevance = line.split()
            rows.setdefault(qid, {})[docid] = int(relevance)
    return rows


def sample_qrels(queries, qrels, output):
    with open(queries) as source:
        qids = [line.split('\t', 1)[0] for line in source if line.strip()]
    if not qids or len(qids) != len(set(qids)):
        raise ValueError(f'Empty query sample or duplicate query IDs: {queries}')
    judged = set(qids) & qrels.keys()
    if not judged:
        raise ValueError(f'{queries}: no queries have judgments')
    with open(output, 'w') as target:
        for qid in qids:
            if qid not in judged:
                continue
            for docid, relevance in qrels[qid].items():
                target.write(f'{qid} 0 {docid} {relevance}\n')
    return dict(queries=len(qids), judged=len(judged))


def evaluate(qrels, run, cutoff):
    scores = {}
    for options in (['-m', 'map', '-m', 'recall.1000'],
                    ['-M', str(cutoff), '-m', 'recip_rank']):
        command = [str(RUNNER), 'io.anserini.eval.TrecEval', '-c', *options,
                   str(qrels), str(run)]
        result = subprocess.run(command, check=True, text=True, capture_output=True, cwd=ROOT)
        for line in result.stdout.splitlines():
            fields = line.split()
            if len(fields) == 3 and fields[1] == 'all':
                scores[fields[0]] = float(fields[2])
    # The Java wrapper may print a native failure without returning a nonzero exit code.
    if not {'map', 'recall_1000', 'recip_rank'} <= scores.keys():
        raise RuntimeError(f'Evaluation did not produce all required metrics for {run}')
    return scores


def winners(rows):
    # Numeric ascending grid order breaks equal four-decimal trec_eval scores deterministically.
    ordered = sorted(rows, key=lambda row: (row['k1'], row['b']))
    return {metric: max(ordered, key=lambda row: row['scores'][metric])
            for metric in ('recall_1000', 'recip_rank', 'map')}


def write_json(path, value):
    temporary = path.with_suffix('.json.tmp')
    temporary.write_text(json.dumps(value, indent=2) + '\n')
    temporary.replace(path)


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--base-directory', required=True, type=Path)
    parser.add_argument('--index', required=True, help='Local index path or prebuilt index name')
    parser.add_argument('--queries', required=True, nargs='+', type=Path, help='One or more uncompressed query TSV samples, in averaging order')
    parser.add_argument('--qrels-trec', required=True, type=Path)
    parser.add_argument('--qrels-tsv', type=Path, help='Legacy MS MARCO qrels input; if supplied, must agree with --qrels-trec')
    parser.add_argument('--task', choices=('passage', 'doc'), default='passage')
    parser.add_argument('--k1', type=grid, default=grid('0.6:1.2:0.1'))
    parser.add_argument('--b', type=grid, default=grid('0.5:0.9:0.1'))
    parser.add_argument('--threads', type=int, default=8)
    parser.add_argument('--discard-runs', action='store_true', help='Remove each run after saving its evaluation checkpoint')
    parser.add_argument('--dry-run', action='store_true')
    args = parser.parse_args()
    if args.threads < 1 or any(not 0 < k < float('inf') for k in args.k1) or any(not 0 <= b <= 1 for b in args.b):
        parser.error('Require threads >= 1, finite k1 > 0, and 0 <= b <= 1')
    args.base_directory = args.base_directory.resolve()
    args.queries = [path.resolve() for path in args.queries]
    if Path(args.index).exists():
        args.index = str(Path(args.index).resolve())
    qrels = read_qrels(args.qrels_trec)
    if args.qrels_tsv and read_qrels(args.qrels_tsv) != qrels:
        parser.error('--qrels-tsv and --qrels-trec contain different judgments')
    cutoff = 10 if args.task == 'passage' else 100
    args.base_directory.mkdir(parents=True, exist_ok=True)
    settings = dict(index=args.index, task=args.task, k1=args.k1, b=args.b,
                    queries=[str(path) for path in args.queries],
                    queries_sha256=[hashlib.sha256(path.read_bytes()).hexdigest() for path in args.queries],
                    qrels_sha256=hashlib.sha256(args.qrels_trec.read_bytes()).hexdigest(),
                    script_sha256=hashlib.sha256(Path(__file__).read_bytes()).hexdigest(),
                    converter_sha256=hashlib.sha256((ROOT / 'src/main/python/msmarco/convert_msmarco_to_trec_run.py').read_bytes()).hexdigest(),
                    jars={path.name: hashlib.sha256(path.read_bytes()).hexdigest()
                          for path in sorted((ROOT / 'target').glob('*-fatjar.jar'))})
    manifest = args.base_directory / 'settings.json'
    if manifest.exists() and json.loads(manifest.read_text()) != settings:
        parser.error('Settings differ from the existing sweep; use a new base directory')
    write_json(manifest, settings)
    samples = []
    for number, queries in enumerate(args.queries, 1):
        directory = args.base_directory / f'sample-{number}'
        directory.mkdir(exist_ok=True)
        judgments = directory / 'qrels.txt'
        count = sample_qrels(queries, qrels, judgments)
        rows = []
        for k1 in args.k1:
            for b in args.b:
                run = directory / f'run.bm25.k1_{k1}.b_{b}.trec'
                msmarco_run = run.with_suffix('.txt')
                checkpoint = run.with_suffix('.json')
                command = [str(RUNNER), 'io.anserini.search.SearchCollection',
                           '-index', args.index, '-topics', str(queries), '-topicReader', 'TsvInt',
                           '-output', str(msmarco_run), '-format', 'msmarco',
                           '-bm25', '-bm25.k1', str(k1), '-bm25.b', str(b),
                           '-hits', '1000', '-threads', str(args.threads)]
                conversion = [sys.executable, str(ROOT / 'src/main/python/msmarco/convert_msmarco_to_trec_run.py'),
                              '--input', str(msmarco_run), '--output', str(run)]
                if args.dry_run:
                    print(shlex.join(command))
                    print(shlex.join(conversion))
                    continue
                if checkpoint.exists():
                    row = json.loads(checkpoint.read_text())
                else:
                    print(f'Sample {number}/{len(args.queries)}: k1={k1}, b={b}', flush=True)
                    # Never reuse an unmarked run: interrupted searches can leave partial output.
                    msmarco_run.unlink(missing_ok=True)
                    run.unlink(missing_ok=True)
                    with open(run.with_suffix('.log'), 'w') as log:
                        subprocess.run(command, check=True, cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
                        if not msmarco_run.exists() or msmarco_run.stat().st_size == 0:
                            raise RuntimeError(f'Search produced no results; see {run.with_suffix(".log")}')
                        subprocess.run(conversion, check=True, cwd=ROOT, stdout=log, stderr=subprocess.STDOUT)
                    if not run.exists() or run.stat().st_size == 0:
                        raise RuntimeError(f'Search produced no results; see {run.with_suffix(".log")}')
                    row = dict(k1=k1, b=b, scores=evaluate(judgments, run, cutoff))
                    write_json(checkpoint, row)
                rows.append(row)
                print(json.dumps(row), flush=True)
                if args.discard_runs:
                    run.unlink(missing_ok=True)
                    msmarco_run.unlink(missing_ok=True)
        if not args.dry_run:
            samples.append(dict(queries=str(queries), count=count, best=winners(rows)))
            write_json(args.base_directory / 'samples.json', samples)
    if not args.dry_run:
        averages = {metric: {parameter: float(sum(Decimal(str(sample['best'][metric][parameter]))
                            for sample in samples) / len(samples)) for parameter in ('k1', 'b')}
                    for metric in ('recall_1000', 'recip_rank', 'map')}
        summary = dict(settings=settings, mrr_cutoff=cutoff, samples=samples, averages=averages)
        write_json(args.base_directory / 'summary.json', summary)
        print(json.dumps(summary, indent=2))


if __name__ == '__main__':
    main()
