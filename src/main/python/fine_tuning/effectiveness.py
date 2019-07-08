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

logging.basicConfig()
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
        for metric_dir in os.listdir(os.path.join(output_root, self.eval_files_root)):
            for fn in os.listdir(os.path.join(output_root, self.eval_files_root, metric_dir)):
                model, model_params = fn.split('_')
                output_fn = model+'_'+metric_dir
                if not os.path.exists(output_fn):
                    if output_fn not in all_results:
                        all_results[output_fn] = []
                    all_results[output_fn].append( os.path.join(output_root, self.eval_files_root, metric_dir, fn) )
        for output_fn in all_results:
            performace_fn = os.path.join(output_root, self.effectiveness_root, output_fn)
            tmp = [ self.index_path, performace_fn ]
            tmp.extend( all_results[output_fn] )
            all_params.append(tuple(tmp))

        return all_params

    def output_effectiveness(self, output_fn, eval_fn_list):
        all_best_results = {}
        for fn in eval_fn_list:
            eval_res = self.read_eval_file(fn)
            for metric in eval_res:
                if metric not in all_best_results:
                    all_best_results[metric] = {}
                for qid in eval_res[metric]:
                    eval_res[metric][qid].sort(key=itemgetter(0), reverse=True)
                    if qid not in all_best_results[metric] or eval_res[metric][qid][0][0] > all_best_results[metric][qid]['value']:
                        all_best_results[metric][qid] = {'value': eval_res[metric][qid][0][0], 'para': eval_res[metric][qid][0][1]}

        with open(output_fn, 'w') as o:
            json.dump(all_best_results, o, indent=2, sort_keys=True)

    def read_eval_file(self, fn):
        """
        return {qid: {metric: [(value, para), ...]}}
        """
        split_fn = os.path.basename(fn).split('_')
        params = split_fn[1]
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

    def add_up_all_optimal(self, json_data, per_topic_oracle_with_metric):
        sum_optimal = 0.0
        n = 0
        params_dist = {}
        for qid in json_data:
            if qid != 'all':
                sum_optimal += json_data[qid]['value']
                n+=1
                for param in json_data[qid]['para'].split(','):
                    if len(param.split(':')) > 1:
                        param_name, param_value = param.split(':')
                        if param_name not in params_dist:
                            params_dist[param_name] = {}
                        if param_value not in params_dist[param_name]:
                            params_dist[param_name][param_value] = 0
                        params_dist[param_name][param_value] += 1
                if qid not in per_topic_oracle_with_metric:
                    per_topic_oracle_with_metric[qid] = json_data[qid]['value']
                else:
                    per_topic_oracle_with_metric[qid] = max(json_data[qid]['value'], per_topic_oracle_with_metric[qid])
        return round(sum_optimal/n, 4), params_dist

    def load_optimal_effectiveness(self, output_root):
        data = []
        per_topic_oracle = {} # per topic optimal across all kinds of methods
        effectiveness_root = os.path.join(output_root, self.effectiveness_root)
        for fn in os.listdir(effectiveness_root):
            model, metric = fn.split('_')
            with open(os.path.join(effectiveness_root, fn)) as f:
                for real_metric, all_performance in json.load(f).items():
                    if real_metric not in per_topic_oracle:
                        per_topic_oracle[real_metric] = {}
                    all_optimal = self.add_up_all_optimal(all_performance, per_topic_oracle[real_metric])
                    res = {
                        'model': model,
                        'metric': real_metric,
                        'best_avg': all_performance['all'],
                        'oracles_per_topic': all_optimal[0]
                    }
                    data.append(res)
        return data, per_topic_oracle
