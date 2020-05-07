# -*- coding: utf-8 -*-
'''
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
'''
import argparse
import collections
import pytrec_eval


def load_qrels(path):
    qrels = collections.defaultdict(dict)
    for line in open(path):
        line = ' '.join(line.split())
        query_id, _, doc_id, relevance = line.split(' ')
        qrels[query_id][doc_id] = int(relevance)
    return qrels


def load_run(path):
    run = collections.defaultdict(dict)
    for line in open(path):
        query_id, _, doc_id, _, score, _ = line.split(' ')
        run[query_id][doc_id] = float(score)

    return run


parser = argparse.ArgumentParser(
    description='Interpolate 2 runs by first normalizing their scores (between'
                '0 and 1) and then doing: alpha * run1_score + (1 - alpha) * '
                'run2_score.')
parser.add_argument('--qrels', required=True, type=str, help='qrels file.')
parser.add_argument('--run1', required=True, type=str, help='run 1 file.')
parser.add_argument('--run2', required=True, type=str, help='run 2 file.')
parser.add_argument('--output', required=True, help='output run file.')
parser.add_argument('--metric', required=True, type=str,
                    help='metric for grid search.')
parser.add_argument('--steps', default=10, type=int,
                    help='Number of alpha increments during parameter search.')

args = parser.parse_args()

qrels = load_qrels(args.qrels)
run1 = load_run(args.run1)
run2 = load_run(args.run2)
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

        run_fusion[query_id] = doc_scores
    results = evaluator.evaluate(run_fusion)

    score = sum(metrics[args.metric.replace('.', '_')]
                for metrics in results.values())
    score /= len(run_fusion)

    print('alpha:', alpha, 'score:', score)

    if score > best_score:
        best_run = run_fusion
        best_score = score
        best_alpha = alpha

print('best_alpha:', best_alpha, 'best_score:', best_score)

with open(args.output, 'w') as fout:
    for query_id, doc_id_scores in best_run.items():
        for rank, (doc_id, score) in enumerate(doc_id_scores.items()):
            rank += 1
            fout.write(f'{query_id} Q0 {doc_id} {rank} {score} Fusion\n')

print('Done!')
