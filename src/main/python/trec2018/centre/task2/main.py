"""
Anserini: A toolkit for reproducible information retrieval research built on Lucene

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

from __future__ import print_function
from multiprocessing import Pool
import os
import argparse
import subprocess
from operator import itemgetter
import csv
import matplotlib.pyplot as plt

parallelism=1
def batch_everything(all_params, func):
    if len(all_params) == 0:
        return
    p = Pool(parallelism)
    p.map(func, all_params)

def atom_retrieval(retrieval_command):
    output_fn = retrieval_command.split(' ')[-1]
    if not os.path.exists(output_fn):
        subprocess.call(retrieval_command, shell=True)

def batch_retrieval(anserini_root, results_root, target_index, expansion_index, dry_run = False):
    all_commands = []
    for model in ['bm25', 'ql', 'f2log']:
        for beta in range(1, 31):
            beta /= 10.0
            all_commands.append(
                '%s -index %s -topicreader Webxml -topics %s -%s -rerankCutoff %d -axiom -axiom.deterministic -axiom.beta %.1f %s -output %s'
                % (os.path.join(anserini_root, 'target/appassembler/bin/SearchCollection'),
                   target_index,
                   os.path.join(anserini_root, 'src/main/resources/topics-and-qrels/topics.web.201-250.txt'),
                   model, 0, beta,
                   '' if expansion_index is None or target_index == expansion_index else '-axiom.index '+ expansion_index,
                   os.path.join(results_root, '%s,%s_%s_%.1f.txt'
                        % (os.path.basename(os.path.abspath(target_index)), os.path.basename(os.path.abspath(expansion_index)), model, beta))
                   )
            )
    print('='*10+'Starting Batch Retrieval'+'='*10)
    if dry_run:
        print('\n'.join(all_commands))
    else:
        batch_everything(all_commands, atom_retrieval)

def atom_eval(params, all_results):
    command = params[0]
    k = params[1]
    beta = float(params[2])
    process = subprocess.Popen(command, shell=True, stdout=subprocess.PIPE)
    out, unused_err = process.communicate()
    performance = float(out.split(b'\n')[-2].split(b',')[-1])
    all_results[k].append((beta, performance))

def batch_eval(anserini_root, results_root = 'results', eval_output='trec2018_centre_task2.csv', dry_run = False):
    qrels_fn = os.path.join(anserini_root, 'src/main/resources/topics-and-qrels/qrels.web.201-250.txt')
    all_results = {}
    all_commands = []
    for fn in os.listdir(results_root):
        (indexes, model, beta) = os.path.splitext(fn)[0].split('_')
        k = (indexes, model)
        if k not in all_results:
            all_results[k] = []
        eval_bin = os.path.join(anserini_root, 'eval/gdeval.pl')
        command = '%s %s %s | grep "amean"' % (eval_bin, qrels_fn, os.path.join(results_root, fn))
        all_commands.append((command, k, beta))
    print('='*10+'Starting Batch Evaluation'+'='*10)
    if dry_run:
        print('\n'.join([ele[0] for ele in all_commands]))
    else:
        for command in all_commands:
            atom_eval(command, all_results)
        with open(eval_output, 'w') as f:
            writer = csv.writer(f)
            for k in all_results:
                all_results[k].sort(key = itemgetter(0))
                for ele in all_results[k]:
                    writer.writerow((k[0], k[1], '%.1f'%ele[0], '%.5f'%ele[1]))

def read_eval_data(fn):
    all_results = {}
    with open(fn) as f:
        r = csv.reader(f)
        for row in r:
            indexes, model, beta, score = row
            if indexes not in all_results:
                all_results[indexes] = {}
            if model not in all_results[indexes]:
                all_results[indexes][model] = []
            all_results[indexes][model].append((float(beta), float(score)))
    return all_results

def plot(fn, dry_run = False):
    all_results = read_eval_data(fn)
    ls = ['-', '--', ':']
    for indexes in sorted(all_results):
        output_fn = '%s_%s.eps' % (fn.split('.')[0], indexes)
        print('Saving EPS: '+output_fn)
        if dry_run:
            continue
        fig, ax = plt.subplots(1, 1, figsize=(6, 4))
        for (model, linestyle) in zip(sorted(all_results[indexes]), ls):
            all_results[indexes][model].sort(key = itemgetter(0))
            x = [float(ele[0]) for ele in all_results[indexes][model]]
            y = [float(ele[1]) for ele in all_results[indexes][model]]
            ax.plot(x, y, linestyle=linestyle, marker='o', ms=5, label=model)
            ax.grid(True)
            ax.set_title(indexes)
            ax.set_xlabel(r'$\beta$')
            ax.set_ylabel('ERR20')
            ax.legend()
        plt.savefig(output_fn, bbox_inches='tight', format='eps')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Regression Tests')
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument('--results_root', default='results', help='The directory where the results will be written to')
    parser.add_argument('--target_index', help='The target index where the final results come from')
    parser.add_argument('--expansion_index', help='The expansion index where the expansion terms come from')
    parser.add_argument('--retrieval', dest='retrieval', action='store_true', help='do retrieval')
    parser.add_argument('--eval', dest='eval', action='store_true', help='do evaluation')
    parser.add_argument('--eval_output', default='trec2018_centre_task2.csv', help='the output filename of evaluation')
    parser.add_argument('--plot', dest='plot', action='store_true', help='do plot')
    parser.add_argument('--dry_run', dest='dry_run', action='store_true',
                        help='output the commands but not actually running them. this is useful for development/debug')
    parser.add_argument('--n', dest='parallelism', type=int, default=4, help='number of parallel threads for retrieval/eval')
    args = parser.parse_args()

    parallelism = args.parallelism
    if not os.path.exists(args.results_root):
        os.makedirs(args.results_root)
    if args.retrieval:
        batch_retrieval(args.anserini_root, args.results_root, args.target_index, args.expansion_index, args.dry_run)
    if args.eval:
        batch_eval(args.anserini_root, args.results_root, args.eval_output, args.dry_run)
    if args.plot:
        plot(args.eval_output, args.dry_run)
