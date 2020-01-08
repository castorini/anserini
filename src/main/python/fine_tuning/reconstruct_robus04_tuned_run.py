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

"""Script to reconstruct tuned BM25 + RM3 run.

Takes as arguments an index, the folds, and per-fold parameters to
reconstruct the tuned BM25 + RM3 run.
"""

import argparse
import json
import os
import re

if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--index", type=str, help='index', required=True)
    parser.add_argument("--folds", type=str, help='folds file', required=True)
    parser.add_argument("--params", type=str, help='params file', required=True)
    parser.add_argument("--output", type=str, help='output run file', required=True)

    args = parser.parse_args()
    index = args.index
    folds_file = args.folds
    params_file = args.params

    # This can be hard coded.
    topics_file = 'src/main/resources/topics-and-qrels/topics.robust04.txt'

    # Load folds.
    with open(folds_file) as f:
        folds = json.load(f)

    # Load params.
    with open(params_file) as f:
        params = json.load(f)

    # Load topics.
    topics = []
    with open(topics_file, 'r') as f:
        for line in f:
            if '<top>' in line:
                topics.append(line)
            else:
                topics[-1] += line

    # Generate separate topics for each fold.
    for i in range(len(folds)):
        out = open(f'topics.robust04.fold{i}', 'w')
        for t in range(len(topics)):
            match = re.search(r'Number: (\d+)', topics[t], re.M)
            if match:
                if str(match.group(1)) in folds[i]:
                    out.write(topics[t])
        out.close()

    # Generate run for each fold using tuned parameters.
    folds_run_files = []
    for i in range(len(folds)):
        os.system(f'target/appassembler/bin/SearchCollection -topicreader Trec -index {index} '
                  f'-topics topics.robust04.fold{i} -output {args.output}.fold{i} -hits 1000 {params[i]}')
        folds_run_files.append(f'{args.output}.fold{i}')

    # Concatenate all partial run files together.
    with open(args.output, 'w') as outfile:
        for fname in folds_run_files:
            with open(fname) as infile:
                outfile.write(infile.read())

