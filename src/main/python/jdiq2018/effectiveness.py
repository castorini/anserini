# -*- coding: utf-8 -*-
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

import os
import json
import ast
from operator import itemgetter
from inspect import currentframe, getframeinfo
import logging

class Effectiveness(object):
    """
    Handle the performace. For example, get all the effectiveness of one method(has multiple parameters).
    When constructing, pass the index path
    """
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

    def gen_output_effectiveness_params(self, output_root):
        if not os.path.exists(os.path.join(output_root, self.effectiveness_root)):
            os.makedirs(os.path.join(output_root, self.effectiveness_root))
        all_params = []
        all_results = {}
        for fn in os.listdir(os.path.join(output_root, self.eval_files_root)):
            if len(fn.split('_')) == 3:
                topic_type, model, model_params = fn.split('_')
            elif len(fn.split('_')) == 2:
                topic_type, model = fn.split('_')
            performace_fn = os.path.join(output_root, self.effectiveness_root, topic_type+'_'+model)
            if not os.path.exists(performace_fn):
                k = topic_type+'_'+model
                if k not in all_results:
                    all_results[k] = []
                all_results[k].append( os.path.join(output_root, self.eval_files_root, fn) )
        for k in all_results:
            performace_fn = os.path.join(output_root, self.effectiveness_root, k)
            tmp = [ self.index_path, performace_fn ]
            tmp.extend( all_results[k] )
            all_params.append(tuple(tmp))

        return all_params

    def output_effectiveness(self, output_fn, eval_fn_list):
        all_results = {}
        for fn in eval_fn_list:
            eval_res = self.read_eval_file(fn)
            for metric in eval_res:
                if metric not in all_results:
                    all_results[metric] = {}
                for qid in eval_res[metric]:
                    if qid not in all_results[metric]:
                        all_results[metric][qid] = []
                    all_results[metric][qid].extend( eval_res[metric][qid] )
        final_results = {}
        for metric in all_results:
            final_results[metric] = {}
            for qid in all_results[metric]:
                final_results[metric][qid] = {}
                all_results[metric][qid].sort(key=itemgetter(0), reverse=True)
                final_results[metric][qid]['max'] = {'value':all_results[metric][qid][0][0], 'para':all_results[metric][qid][0][1]}

        with open(output_fn, 'w') as o:
            json.dump(final_results, o, indent=2, sort_keys=True)

    def read_eval_file(self, fn):
        """
        return {qid: {metric: [(value, para), ...]}}
        """
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
                    if qid not in res[metric]:
                        res[metric][qid] = []
                    res[metric][qid].append((value, params))

        return res

    def load_optimal_effectiveness(self, output_root, metrics=['map']):
        data = []
        effectiveness_root = os.path.join(output_root, self.effectiveness_root)
        for fn in os.listdir(effectiveness_root):
            topic, model = fn.split('_')
            with open(os.path.join(effectiveness_root, fn)) as f:
                all_performance = json.load(f)
                for metric in metrics:
                    res = {
                        'model': model,
                        'topic': topic,
                        'metric': metric,
                        'params': all_performance[metric]['all']['max']['para'],
                        'actual': all_performance[metric]['all']['max']['value']
                    }
                    data.append(res)
        return data
