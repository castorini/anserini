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
import json
import ast
from operator import itemgetter
from inspect import currentframe, getframeinfo
import logging

logging.basicConfig()

class Effectiveness(object):
    """Handles the effectiveness.
    
    For example, get all the effectiveness of one method (has multiple parameters).
    When constructing, pass the index path."""
    def __init__(self, index_path):
        self.logger = logging.getLogger('effectiveness.Effectiveness')
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            self.logger.error(frameinfo.filename, frameinfo.lineno)
            self.logger.error('[Effectiveness Constructor]:Please provide a valid index path - ' + self.index_path)
            exit(1)

        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'
        self.effectiveness_root = 'effectiveness_files'

    def output_effectiveness(self, output_root):
        if not os.path.exists(os.path.join(output_root, self.effectiveness_root)):
            os.makedirs(os.path.join(output_root, self.effectiveness_root))
        all_params = []
        all_results = {}
        for metric_dir in os.listdir(os.path.join(output_root, self.eval_files_root)):
            for fn in os.listdir(os.path.join(output_root, self.eval_files_root, metric_dir)):
                if len(fn.split('_')) == 3:
                    basemodel, model, model_params = fn.split('_')
                elif len(fn.split('_')) == 2:
                    basemodel, model = fn.split('_')
                eval_res = self.read_eval_file(os.path.join(output_root, self.eval_files_root, metric_dir, fn))
                for metric in eval_res:
                    if metric not in all_results:
                        all_results[metric] = {}
                    if basemodel not in all_results[metric]:
                        all_results[metric][basemodel] = []
                    all_results[metric][basemodel].append(eval_res[metric]['all'])

        for metric in all_results:
            with open(os.path.join(output_root, self.effectiveness_root, 'axiom_paras_sensitivity_%s.csv' % metric), 'w') as f:
                for basemodel in all_results[metric]:
                    all_results[metric][basemodel].sort(key = itemgetter(0))
                    for ele in all_results[metric][basemodel]:
                        f.write('%s,%.1f,%.4f\n' % (basemodel, ele[0], ele[1]))

    def read_eval_file(self, fn):
        """return {qid: {metric: [(value, para), ...]}}"""
        split_fn = os.path.basename(fn).split('_')
        params = split_fn[-1] if len(split_fn) == 3 else ''
        res = {}
        with open(fn) as _in:
            for line in _in:
                line = line.strip()
                if line:
                    row = line.split()
                    metric = row[0]
                    qid = row[1]
                    try:
                        value = ast.literal_eval(row[2])
                    except:
                        continue
                    if metric not in res:
                        res[metric] = {}
                    for param in params.split(','):
                        if 'axiom.beta' in param:
                            beta = float(param.split(':')[1])
                            res[metric][qid] = (beta, value)
                    if split_fn[1] == 'baseline': # baseline
                        res[metric][qid] = (-1, value)
        return res
