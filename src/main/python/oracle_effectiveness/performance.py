# -*- coding: utf-8 -*-
import sys,os
import json
import csv
import ast
from operator import itemgetter
from inspect import currentframe, getframeinfo
import argparse

reload(sys)
sys.setdefaultencoding('utf-8')


class Performances(object):
    """
    Handle the performace. For example, get all the performances of one method(has multiple parameters).
    When constructing, pass the path of the corpus. For example, "../wt2g/"
    """
    def __init__(self, index_path):
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            print frameinfo.filename, frameinfo.lineno
            print '[Performances Constructor]:Please provide a valid index path - ' + self.index_path
            exit(1)

        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'
        self.performances_root = 'performance_files'

    def gen_output_performances_paras(self, output_root):
        if not os.path.exists(os.path.join(output_root, self.performances_root)):
            os.makedirs(os.path.join(output_root, self.performances_root))
        all_paras = []
        all_results = {}
        for fn in os.listdir(os.path.join(output_root, self.eval_files_root)):
            topic_type, model = fn.split('_')
            model_name = model.split('-')[0]
            performace_fn = os.path.join(output_root, self.performances_root, topic_type+'-'+model_name)
            if not os.path.exists(performace_fn):
                method_paras = model.split('-')[1] if len(model.split('-')) > 1 else ''
                k = topic_type+'-'+model_name
                if k not in all_results:
                    all_results[k] = []
                all_results[k].append( os.path.join(output_root, self.eval_files_root, fn) )
        for k in all_results:
            performace_fn = os.path.join(output_root, self.performances_root, k)
            tmp = [ self.index_path, performace_fn ]
            tmp.extend( all_results[k] )
            all_paras.append(tuple(tmp))

        return all_paras

    def output_performances(self, output_fn, eval_fn_list):
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

        with open(output_fn, 'wb') as o:
            json.dump(final_results, o, indent=2, sort_keys=True)

    def read_eval_file(self, fn):
        """
        return {qid: {metric: [(value, para), ...]}}
        """
        split_fn = os.path.basename(fn).split('-')
        paras = split_fn[1] if len(split_fn) > 1 else ''
        res = {}
        with open(fn) as _in:
            for line in _in:
                line = line.strip()
                if line:
                    row = line.split()
                    metric = row[0]
                    qid = row[1]
                    value = ast.literal_eval(row[2])
                    if metric not in res:
                        res[metric] = {}
                    if qid not in res[metric]:
                        res[metric][qid] = []
                    res[metric][qid].append((value, paras)) 

        return res

    def load_optimal_performance(self, output_root, metrics=['map']):
        data = {}
        performances_root = os.path.join(output_root, self.performances_root)
        for fn in os.listdir(performances_root):
            model_name = fn
            data[model_name] = []
            with open(os.path.join(performances_root, model_name)) as f:
                all_performance = json.load(f)
                for metric in metrics:
                    if metric not in data:
                        data[metric] = []
                    data[metric].append((model_name, all_performance[metric]['all']['max']['value'], all_performance[metric]['all']['max']['para']))
        return data

    def print_optimal_performance(self, output_root, metrics=['map']):
        optimal_performances = self.load_optimal_performance(output_root, metrics)
        for metric in metrics:
            print metric
            print '-'*30
            for ele in optimal_performances[metric]:
                print ele


if __name__ == '__main__':
    pass

