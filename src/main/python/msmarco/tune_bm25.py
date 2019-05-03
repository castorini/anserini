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

# Simple script for tuning BM25 parameters (k1 and b) for MS MARCO
#
# The output of the script should be (on dev set):
#   Best parameters: run.bm25.b1_0.6.k_0.8.txt: MRR@10 = 0.1906588552326375
#
# Compared to default Anserini parameters of b1=0.9, k=0.4, MRR@10 = 0.18388092964024202

import argparse
import os
import re
import subprocess

parser = argparse.ArgumentParser(description='Retrieve MS MARCO Passages')
parser.add_argument('--base_directory', required=True, help='base directory for storing runs')
parser.add_argument('--index', required=True, help='index to use')
parser.add_argument('--queries', required=True, help='queries for evaluation')
parser.add_argument('--qrels', required=True, help='qrels for evaluation')

args = parser.parse_args()

base_directory = args.base_directory
index = args.index
qrels = args.qrels
queries = args.queries

if not os.path.exists(args.base_directory):
  os.makedirs(args.base_directory)

print('# Settings')
print('base directory: {}'.format(base_directory))
print('index: {}'.format(index))
print('queries: {}'.format(queries))
print('qrels: {}'.format(qrels))
print('\n')

for b1 in [0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2]:
    for k in [0.5, 0.6, 0.7, 0.8, 0.9]:
        print('Trying... b1 = {}, k = {}'.format(b1, k))
        filename = 'run.bm25.b1_{}.k_{}.txt'.format(b1, k)
        if os.path.isfile('{}/{}'.format(base_directory, filename)):
           print('Run already exists, skipping!')
        else:
           subprocess.call('python src/main/python/msmarco/retrieve.py \
               --index {} --qid_queries {} --output {}/{} \
               --b1 {} --k {} --hits 1000'.format(index, queries, base_directory, filename, b1, k), shell=True)

print('\n\nStarting evaluation...')

# We're going to be tuning to maximize recall, although we'll compute MRR and MAP also just for reference.
max_score = 0
max_file = ''
for filename in sorted(os.listdir(base_directory)):
   # trec file, perhaps left over from a previous tuning run: skip.
   if filename.endswith('trec'):
       continue
   # convert to a trec run and evaluate with trec_eval
   subprocess.call('python src/main/python/msmarco/convert_msmarco_to_trec_run.py \
       --input {}/{} --output {}/{}.trec'.format(base_directory, filename, base_directory, filename), shell=True)
   results = subprocess.check_output(['eval/trec_eval.9.0.4/trec_eval', 'msmarco_data/qrels.train.trec',
       '{}/{}.trec'.format(base_directory, filename), '-mrecall.1000', '-mmap'])
   match = re.search('map +\tall\t([0-9.]+)', results.decode('utf-8'))
   ap = float(match.group(1))
   match = re.search('recall_1000 +\tall\t([0-9.]+)', results.decode('utf-8'))
   recall = float(match.group(1))
   # evaluate with official scoring script
   results = subprocess.check_output(['python', 'src/main/python/msmarco/msmarco_eval.py', \
       '{}'.format(qrels), '{}/{}'.format(base_directory, filename)])
   match = re.search('MRR @10: ([\d.]+)', results.decode('utf-8'))
   rr = float(match.group(1))
   print('{}: MRR@10 = {}, MAP = {}, R@1000 = {}'.format(filename, rr, ap, recall))
   if recall > max_score:
      max_score = recall
      max_file = filename

print('\n\nBest parameters: {}: R@1000 = {}'.format(max_file, max_score))
