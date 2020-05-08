# -*- coding: utf-8 -*-
"""
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""
import argparse
import collections
import pytrec_eval

from typing import Dict
from typing import List
from typing import Set
from typing import Tuple


def load_qrels(path: str) -> Dict[str, Set[str]]:
    qrels = collections.defaultdict(dict)
    for line in open(path):
        line = ' '.join(line.split())
        query_id, _, doc_id, relevance = line.split(' ')
        qrels[query_id][doc_id] = int(relevance)
    return qrels


def load_run(path: str) -> Dict[str, Dict[str, float]]:
    run = collections.defaultdict(dict)
    for line in open(path):
        query_id, _, doc_id, _, score, _ = line.split(' ')
        run[query_id][doc_id] = float(score)

    return run


def write_run(path: str, run: Dict[str, List[Tuple[str, float]]]) -> None:
    with open(path, 'w') as fout:
        for query_id, doc_id_scores in run.items():
            for rank, (doc_id, score) in enumerate(doc_id_scores.items()):
                rank += 1
                fout.write(f'{query_id} Q0 {doc_id} {rank} {score} {args.runtag}\n')


parser = argparse.ArgumentParser(
    'Interpolate 2 runs by first normalizing their scores (between 0 and 1) and then doing: '
    'alpha * run1_score + (1 - alpha) * run2_score.')

parser.add_argument('--run1', type=str, required=True, help='run 1 file.')
parser.add_argument('--run2', type=str, required=True, help='run 2 file.')
parser.add_argument('--qrels', type=str, default=None,
                    help='qrels file. Optional. Used to compute the best alpha.')
parser.add_argument('--metric', required=True, type=str, help='metric for compute the best alpha.')
parser.add_argument('--output', type=str, default=None,
                    help='If passed, runs will be saved to files with this prefix.')
parser.add_argument('--k', default=1000, type=int, help='Maximum number of hits to be saved.')
parser.add_argument('--runtag', default='Fusion', help='run tag in the output file.')
parser.add_argument('--steps', default=10, type=int,
                    help='Number of alpha increments during parameter search.')

args = parser.parse_args()

run1 = load_run(args.run1)
run2 = load_run(args.run2)

if args.qrels:
    qrels = load_qrels(args.qrels)
    evaluator = pytrec_eval.RelevanceEvaluator(qrels, {args.metric})
    best_score = -1e9

for alpha in range(args.steps):
    alpha /= args.steps
    run_fusion = {}
    for query_id, doc_ids_scores1 in run1.items():
        doc_scores = collections.defaultdict(float)
        min_score = min(doc_ids_scores1.values())
        max_score = max(doc_ids_scores1.values())
        for doc_id, score in doc_ids_scores1.items():
            norm_score = (score - min_score) / (max_score - min_score)
            doc_scores[doc_id] = alpha * norm_score

        min_score = min(run2[query_id].values())
        max_score = max(run2[query_id].values())
        for doc_id, score in run2[query_id].items():
            norm_score = (score - min_score) / (max_score - min_score)
            doc_scores[doc_id] += (1.0 - alpha) * norm_score

        # Sort docs by score so we can limit the number of docs.
        doc_scores = sorted(doc_scores.items(), key=lambda x: x[1], reverse=True)

        # Convert it back to an OrderedDict because evaluator only accepts runs as dictionaries.
        doc_scores = collections.OrderedDict(
            {doc_id: score for doc_id, score in doc_scores[:args.k]})
        run_fusion[query_id] = doc_scores

    if args.output:
        write_run(run=run_fusion, path=args.output + f'.alpha={alpha}')

    if args.qrels:
        results = evaluator.evaluate(run_fusion)

        score = sum(metrics[args.metric.replace('.', '_')] for metrics in results.values())
        score /= len(run_fusion)

        print('alpha:', alpha, 'score:', score)
        if score > best_score:
            best_run = run_fusion
            best_score = score
            best_alpha = alpha


if args.qrels:
    print('best_alpha:', best_alpha, 'best_score:', best_score)
    if args.output:
        write_run(path=args.output + f'.best_alpha={best_alpha}', run=best_run)

print('Done!')
