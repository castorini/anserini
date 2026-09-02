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

# Simple script for tuning BM25 parameters (k1 and b) for MS MARCO

import argparse
import os
import re
import subprocess

parser = argparse.ArgumentParser(description='Tunes BM25 parameters for MS MARCO Passages')
parser.add_argument('--base-directory', required=True, help='base directory for storing runs')
parser.add_argument('--index', required=True, help='index to use')
parser.add_argument('--queries', required=True, help='queries for evaluation')
parser.add_argument('--qrels-trec', required=True, help='qrels for evaluation (TREC format)')
parser.add_argument('--qrels-tsv', required=True, help='qrels for evaluation (MS MARCO format)')

args = parser.parse_args()

base_directory = args.base_directory
index = args.index
queries = args.queries
qrels_trec = args.qrels_trec
qrels_tsv = args.qrels_tsv

if not os.path.exists(base_directory):
    os.makedirs(base_directory)

print('# Settings')
print(f'base directory: {base_directory}')
print(f'index: {index}')
print(f'queries: {queries}')
print(f'qrels (TREC): {qrels_trec}')
print(f'qrels (MS MARCO): {qrels_tsv}')
print('\n')

for k1 in [0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2]:
    for b in [0.5, 0.6, 0.7, 0.8, 0.9]:
        print(f'Trying... k1 = {k1}, b = {b}')
        filename = f'run.bm25.k1_{k1}.b_{b}.txt'
        if os.path.isfile(f'{base_directory}/{filename}'):
            print('Run already exists, skipping!')
        else:
            subprocess.call(f'python tools/scripts/msmarco/retrieve.py --index {index} --queries {queries} \
                --output {base_directory}/{filename} --k1 {k1} --b {b} --hits 1000', shell=True)

print('\n\nStarting evaluation...')

# We're going to be tuning to maximize recall, although we'll compute MRR and MAP also just for reference.
max_score = 0
max_file = ''

for filename in sorted(os.listdir(base_directory)):
    # TREC output run file, perhaps left over from a previous tuning run: skip.
    if filename.endswith('trec'):
        continue

    # Convert to a TREC run and evaluate with trec_eval:
    subprocess.call(f'python tools/scripts/msmarco/convert_msmarco_to_trec_run.py \
        --input {base_directory}/{filename} --output {base_directory}/{filename}.trec', shell=True)
    results = subprocess.check_output(['tools/eval/trec_eval.9.0.4/trec_eval', qrels_trec,
                                       f'{base_directory}/{filename}.trec', '-mrecall.1000', '-mmap'])
    match = re.search('map +\tall\t([0-9.]+)', results.decode('utf-8'))
    ap = float(match.group(1))
    match = re.search('recall_1000 +\tall\t([0-9.]+)', results.decode('utf-8'))
    recall = float(match.group(1))

    # Evaluate with official scoring script
    results = subprocess.check_output(['python', 'tools/scripts/msmarco/msmarco_passage_eval.py',
                                       'collections/msmarco-passage/qrels.train.tsv',
                                       f'{base_directory}/{filename}'])
    match = re.search(r'MRR @10: ([\d.]+)', results.decode('utf-8'))
    rr = float(match.group(1))
    print(f'{filename}: MRR@10 = {rr}, MAP = {ap}, R@1000 = {recall}')
    if recall > max_score:
        max_score = recall
        max_file = filename

print(f'\n\nBest parameters: {max_file}: R@1000 = {max_score}')
