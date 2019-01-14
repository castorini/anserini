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

    def get_qrels(self, qrels_file):
        qrels = {}
        with open(qrels_file) as f:
            for line in f:
                qid, n, docid, qrel = line.split()
                if qid not in qrels:
                    qrels[qid] = set()
                qrels[qid].add(docid)
        return qrels

    def cal_single_run_coverage(self, run_file):
        qrels = self.get_qrels()
        run = {}
        run_top = {20: {}, 50: {}, 100: {}}
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
        overall = {}
        for qid in qrels:
            for k in run_top:
                if qid in run_top[k]:
                    if k not in overall:
                        overall[k] = 0.0
                    overall[k] += (k - (len(qrels[qid] & run_top[k][qid]))) * 1.0 / k
        for k in overall:
            overall[k] /= len(qrels)
        return overall

    def cal_coverage(self):
        all_results = {}
        for fn in os.listdir(self.results_root):
            (ct, model, beta) = os.path.splitext(fn)[0].split('_')
            collection = ct.split('-')[0]
            topic = '-'.join(ct.split('-')[1:])
            k = (ct, model)
            if collection.startswith('cw'):
                qrels_fn = qrels_root+'web.'+topic+'.txt'
            else:
                qrels_fn = qrels_root+topic+'.txt'
            qrels = get_qrels(qrels_fn)
            avg_overlap = self.cal_coverage(os.path.join(results_root, fn), qrels)
            if k not in all_results:
                all_results[k] = []
            all_results[k].append((float(beta), avg_overlap))

        with open('axiom_judgement_overlap_top%d_avg.csv' % top, 'w') as f:
            for k in sorted(all_results, key=itemgetter(0,1)):
                all_results[k].sort(key = itemgetter(0))
                for ele in all_results[k]:
                    f.write('%s,%s,%.1f,%.4f\n' % (k[0], k[1], ele[0], ele[1]))
