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


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--base", type=str, help='base run', required=True)
    parser.add_argument("--comparison", type=str, help='comparison run', required=True)
    parser.add_argument("--qrels", type=str, help='qrels', required=True)
    parser.add_argument("--metric", type=str, help='metric', required=True)

    args = parser.parse_args()
    base = args.base
    comp = args.comparison
    qrels = args.qrels
    metric = args.metric

    os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 {qrels} {base} > eval.base')
    os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 {qrels} {comp} > eval.comp')

    base_metrics = load_metrics('eval.base')
    comp_metrics = load_metrics('eval.comp')

    for key in base_metrics[metric]:
        base_score = base_metrics[metric][key]
        comp_score = comp_metrics[metric][key]
        diff = comp_score - base_score
        print(f'{key}\t{base_score:.4}\t{comp_score:.4}\t{diff:.4}')

    # Extract the paired scores
    a = [base_metrics[metric][k] for k in sorted(base_metrics[metric])]
    b = [comp_metrics[metric][k] for k in sorted(comp_metrics[metric])]

    (tstat, pvalue) = scipy.stats.ttest_rel(a, b)
    print(f'base mean: {np.mean(a):.4}')
    print(f'comp mean: {np.mean(b):.4}')
    print(f't-statistic: {tstat:.6}, p-value: {pvalue:.6}')
