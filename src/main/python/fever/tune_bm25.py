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

import argparse
import os
import re
import subprocess

def grid_search(args):
    for k1 in [0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2]:
        for b in [0.5, 0.6, 0.7, 0.8, 0.9]:
            print(f'Retrieving with k1 = {k1}, b = {b}...')
            run_file = os.path.join(args.runs_folder, f'run.fever.bm25.k1_{k1}.b_{b}.txt')
            if os.path.isfile(run_file):
                print('Run already exists, skipping!')
            else:
                subprocess.call(f'python3 tools/scripts/msmarco/retrieve.py '
                                f'--index {args.index_folder} '
                                f'--queries {args.queries_file} '
                                f'--output {run_file} '
                                f'--k1 {k1} '
                                f'--b {b} '
                                f'--hits 1000',
                                shell=True)

def evaluate_runs(args):
    max_recall = 0
    max_file = ''
    for file in os.listdir(args.runs_folder):
        # skip TREC files that are in the folder
        if file.endswith('trec'):
            continue
        run_file = os.path.join(args.runs_folder, file)
        # convert TSV to a TREC run file
        subprocess.call(f'python3 tools/scripts/msmarco/convert_msmarco_to_trec_run.py '
                        f'--input {run_file} '
                        f'--output {run_file}.trec',
                        shell=True)
        # evaluate with trec_eval
        results = subprocess.check_output(['tools/eval/trec_eval.9.0.4/trec_eval',
                                           args.qrels_file,
                                           f'{run_file}.trec',
                                           '-mrecall.1000',
                                           '-mmap'])
        # regex match trec_eval output to get metrics
        match = re.search('map +\tall\t([0-9.]+)', results.decode('utf-8'))
        map = float(match.group(1))
        match = re.search('recall_1000 +\tall\t([0-9.]+)', results.decode('utf-8'))
        recall = float(match.group(1))
        print(f'{run_file}: R@1000 = {recall}, MAP = {map}')

        # maximize R@1000
        if recall > max_recall:
            max_recall = recall
            max_file = file

    print(f'Best parameters: {max_file} (R@1000 = {max_recall})')

if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Tunes BM25 parameters for FEVER document retrieval.')
    parser.add_argument('--runs_folder', required=True, help='Directory for storing runs. Do not use runs/.')
    parser.add_argument('--index_folder', required=True, help='Lucene index to use.')
    parser.add_argument('--queries_file', required=True, help='Queries file.')
    parser.add_argument('--qrels_file', required=True, help='Qrels file.')
    args = parser.parse_args()

    if not os.path.exists(args.runs_folder):
        os.makedirs(args.runs_folder)

    grid_search(args)
    evaluate_runs(args)

    print('Done!')
