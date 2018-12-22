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

    os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 {qrels} {base} > eval.base')
    os.system(f'eval/trec_eval.9.0.4/trec_eval -q -M1000 {qrels} {comp} > eval.comp')

    base_metrics = load_metrics('eval.base')
    comp_metrics = load_metrics('eval.comp')

    metric = 'map'

    #print(comp_metrics)
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
