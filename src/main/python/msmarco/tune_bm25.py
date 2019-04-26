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

import os
import subprocess
import re

base_directory = 'msmarco_data/bm25_tuning'
index = 'msmarco_data/lucene-index-msmarco'

for b1 in [0.5, 0.6, 0.7, 0.8, 0.9, 1.0, 1.1, 1.2]:
    for k in [0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]:
        print('Trying... b1 = {}, k = {}'.format(b1, k))
        filename = 'run.bm25.b1_{}.k_{}.txt'.format(b1, k)
        if os.path.isfile('{}/{}'.format(base_directory, filename)):
           print('Run already exists, skipping!')
        else:
           subprocess.call('python src/main/python/msmarco/retrieve.py \
               --index {} --qid_queries msmarco_data/queries.dev.small.tsv \
               --output {}/{} --b1 {} --k {} --hits 1000'.format(index, base_directory, filename, b1, k), shell=True)

print('\n\nStarting evaluation...')

max_score = 0
max_file = ''
for filename in os.listdir(base_directory):
   results = subprocess.check_output(['python', 'src/main/python/msmarco/msmarco_eval.py', \
       'msmarco_data/qrels.dev.small.tsv', '{}/{}'.format(base_directory, filename)])
   match = re.search('MRR @10: ([\d.]+)', results.decode('utf-8'))
   score = float(match.group(1))
   print('{}: MRR@10 = {}'.format(filename, score))
   if score > max_score:
      max_score = score
      max_file = filename

print('\n\nBest parameters: {}: MRR@10 = {}'.format(max_file, max_score))
