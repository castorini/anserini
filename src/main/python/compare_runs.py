# -*- coding: utf-8 -*-
#
# Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

"""Script to compare the effectiveness of two runs in standard trec_eval format.

Takes as arguments a base run, a comparison run, a qrels file, and a
metric: performs per-topic analysis of differences and computes
statistical significance of differences. Makes an external call to
trec_eval for the actual computation of metrics.
"""

import argparse
import os
import numpy as np
import scipy.stats
import statistics
import matplotlib.pyplot as plt
plt.switch_backend('agg')
from operator import itemgetter

plt.style.use('ggplot')

from msmarco_compare import compute_metrics_from_files 

def load_metrics(file):
    metrics = {}
    with open(file, 'r') as f:
        for line in f:
            metric, qid, score = line.split('\t')
            metric = metric.strip()
            qid = qid.strip()
            score = score.strip()
            if qid == 'all':
                continue
            if metric not in metrics:
                metrics[metric] = {}
            metrics[metric][qid] = float(score)

    return metrics


def plot(all_results, ymin=-1, ymax=1, output_path="."):
    fig, ax = plt.subplots(1, 1, figsize=(16, 3))
    all_results.sort(key = itemgetter(1), reverse=True)
    x = [_x+0.5 for _x in range(len(all_results))]
    y = [float(ele[1]) for ele in all_results]
    ax.bar(x, y, width=0.6, align='edge')
    ax.set_xticks(x)
    ax.set_xticklabels([int(ele[0]) for ele in all_results], {'fontsize': 4}, rotation='vertical')
    ax.grid(True)
    ax.set_title("Per-topic analysis on {}".format(metric))
    ax.set_xlabel('Topics')
    ax.set_ylabel('{} Diff'.format(metric))
    ax.set_ylim(ymin, ymax)
    output_fn = os.path.join(output_path, 'per_query_{}.pdf'.format(metric))
    plt.savefig(output_fn, bbox_inches='tight', format='pdf')

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", type=str, help='base run', required=True)
    parser.add_argument("--comparison", type=str, help='comparison run', required=True)
    parser.add_argument("--qrels", type=str, help='qrels', required=True)
    parser.add_argument("--metric", type=str, help='metric', default="map")
    parser.add_argument("--msmarco", action='store_true', default=False, help='whether to use masarco eval script')
    parser.add_argument("--ymin", type=float, help='min value of the y axis', default=-1)
    parser.add_argument("--ymax", type=float, help='max value of the y axis', default=1)

    args = parser.parse_args()
    base = args.base
    comp = args.comparison
    qrels = args.qrels
    metric = args.metric

    if args.msmarco:
        base_all, base_metrics = compute_metrics_from_files(qrels, base, per_query_score=True) 
        comp_all, comp_metrics = compute_metrics_from_files(qrels, comp, per_query_score=True) 
    else:
        os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 -m {metric} {qrels} {base} > eval.base')
        os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 -m {metric} {qrels} {comp} > eval.comp')

        base_metrics = load_metrics('eval.base')
        comp_metrics = load_metrics('eval.comp')

    # trec_eval expects something like 'P.10' on the command line but outputs 'P_10'
    if "." in metric:
        metric = "_".join(metric.split("."))

    all_results = []
    num_better = 0
    num_worse = 0
    num_unchanged = 0
    biggest_gain = 0
    biggest_gain_topic = ''
    biggest_loss = 0
    biggest_loss_topic = ''
    if args.msmarco:
        metric = "MRR@10"
    keys = []
    for key in base_metrics[metric]:
        base_score = base_metrics[metric][key]
        if key not in comp_metrics[metric]:
            continue
        keys.append(key)
        comp_score = comp_metrics[metric][key]
        diff = comp_score - base_score
        # This is our relatively arbitrary definition of "better", "worse", and "unchanged".
        if diff > 0.01:
            num_better += 1
        elif diff < -0.01:
            num_worse += 1
        else:
            num_unchanged += 1
        if diff > biggest_gain:
            biggest_gain = diff
            biggest_gain_topic = key
        if diff < biggest_loss:
            biggest_loss = diff
            biggest_loss_topic = key
        all_results.append((key, diff))
        print(f'{key}\t{base_score:.4}\t{comp_score:.4}\t{diff:.4}')

    # Extract the paired scores
    a = [base_metrics[metric][k] for k in keys]
    b = [comp_metrics[metric][k] for k in keys]

    (tstat, pvalue) = scipy.stats.ttest_rel(a, b)
    print(f'base mean: {np.mean(a):.4}')
    print(f'comp mean: {np.mean(b):.4}')
    print(f't-statistic: {tstat:.6}, p-value: {pvalue:.6}')
    print(f'better (diff > 0.01): {num_better:>3}')
    print(f'worse  (diff > 0.01): {num_worse:>3}')
    print(f'(mostly) unchanged  : {num_unchanged:>3}')
    print(f'biggest gain: {biggest_gain:.4} (topic {biggest_gain_topic})')
    print(f'biggest loss: {biggest_loss:.4} (topic {biggest_loss_topic})')

    plot(all_results, ymin=args.ymin, ymax=args.ymax)
