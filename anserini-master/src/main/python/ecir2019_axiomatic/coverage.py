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

import os
from inspect import currentframe, getframeinfo
import logging

logging.basicConfig()

class Coverage(object):
    def __init__(self, index_path):
        self.logger = logging.getLogger('coverage.Coverage')
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            self.logger.error(frameinfo.filename, frameinfo.lineno)
            self.logger.error('[Coverage Constructor]:Please provide a valid index path - ' + self.index_path)
            exit(1)

        self.run_files_root = 'run_files'
        self.coverage_root = 'coverage'

    def get_qrels(self, qrels_file):
        qrels = {}
        with open(qrels_file) as f:
            for line in f:
                qid, n, docid, qrel = line.split()
                if qid not in qrels:
                    qrels[qid] = set()
                qrels[qid].add(docid)
        return qrels

    def cal_single_run_coverage(self, run_file, qrels):
        run = {}
        run_top = {20: {}, 50: {}, 100: {}}
        for param in run_file.split('_')[-1].split(','):
            if 'axiom.beta' in param:
                beta = float(param.split(':')[1])
        with open(run_file) as f:
            i = 0
            for line in f:
                row = line.split()
                qid = row[0]
                docid = row[2]
                score = row[4]
                if qid not in run:
                    i = 0
                    run[qid] = set()
                    for k in run_top:
                        run_top[k][qid] = set()
                run[qid].add(docid)
                for k in run_top:
                    if i < k:
                        run_top[k][qid].add(docid)
                i+=1
        overall = [beta, {}]
        for qid in qrels:
            for k in run_top:
                if qid in run_top[k]:
                    if k not in overall[1]:
                        overall[1][k] = 0.0
                    overall[1][k] += (k - (len(qrels[qid] & run_top[k][qid]))) * 1.0 / k
        for k in overall[1]:
            overall[1][k] /= len(qrels)

        return overall

    def cal_coverage(self, model_yaml, qrels_path, output_root):
        if not os.path.exists(os.path.join(output_root, self.coverage_root)):
            os.makedirs(os.path.join(output_root, self.coverage_root))
        all_results = {}
        for fn in os.listdir(os.path.join(output_root, self.run_files_root)):
            should_skip = False
            for model in model_yaml['models']:
                if model not in fn or 'baseline' in fn:
                    should_skip = True
                    break
            if should_skip:
                continue
            qrels = self.get_qrels(qrels_path)
            avg_overlap = self.cal_single_run_coverage(os.path.join(output_root, self.run_files_root, fn), qrels)
            if avg_overlap[0] == 0.0: # ignore beta = 0.0
                continue
            for k in avg_overlap[1]:
                if k not in all_results:
                    all_results[k] = []
                all_results[k].append((float(avg_overlap[0]), avg_overlap[1][k]))

        for k in all_results:
            with open(os.path.join(output_root, self.coverage_root, 'coverage_%d_avg.csv' % k), 'w') as f:
                for ele in sorted(all_results[k]):
                    f.write('%.1f,%.4f\n' % (ele[0], ele[1]))
