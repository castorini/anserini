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

from __future__ import print_function
import os
import re
import argparse
import logging
import json


logging.basicConfig()
class XFoldValidate(object):
    """
    Perform X-Fold cross validation for various 
    parameters and report the average effectiveness
    for each fold. fold_mapping is an optional argument.
    It can be a dictionary {qid:fold_id} that maps 
    each qid to its corresponding fold.
    """
    def __init__(self,output_root,collection,
                 fold=5,fold_mapping=None):
        self.logger = logging.getLogger('x_fold_cv.XFlodValidate')
        self.output_root = output_root
        self.eval_files_root = 'eval_files'
        self.collection = collection
        self.fold = fold
        self.fold_mapping = fold_mapping


    
    def _get_param_average(self):
        # For each parameter set, get its 
        # average performances in each fold,
        # metric, reranking model, and base 
        # ranking model
        avg_performances = {}
        eval_root_dir = os.path.join(self.output_root, self.collection,self.eval_files_root)

        # do x-fold cv for the collection
        for metric in os.listdir(eval_root_dir):
            eval_dir = os.path.join(eval_root_dir,metric)
            if os.path.isfile(eval_dir):
                continue
            # if it is a directory containing effectiveness
            # for a metric, do x-fold cv for the metric
            for fn in os.listdir(eval_dir):
                model, param = fn.split('_')
                if model not in avg_performances:
                    avg_performances[model] = {}

                param_avg_performances = self._get_param_avg_performances(os.path.join(eval_dir,fn))
                for metric in param_avg_performances:
                    if metric not in avg_performances[model]:
                        avg_performances[model][metric] = {}
                    for fold_id in param_avg_performances[metric]:
                        if fold_id not in avg_performances[model][metric]:
                            avg_performances[model][metric][fold_id] = {}
                        avg_performances[model][metric][fold_id][param] = param_avg_performances[metric][fold_id]

        return avg_performances

    def _compute_fold_id(self,qid):
        # compute fold id
        if self.fold_mapping:
            # use the fold mapping passed to it
            return self.fold_mapping[qid]
        else:
            # compute the fold id based on qid
            return int(qid) % self.fold

    def tune(self,verbose):
        # Tune parameter with x-fold. Use x-1 fold
        # for training and 1 fold for testing. Do
        # it for each fold and report average
        avg_performances = self._get_param_average()

        res = {}
        for model in avg_performances:
            res[model] = {}
            for metric in avg_performances[model]:
                if verbose:
                    print('model: {}, metric: {}'.format(model, metric))
                metric_fold_performances = []
                for test_idx in range(self.fold):
                    test_fold_performances = avg_performances[model][metric][test_idx]
                    training_data = {}
                    for train_idx in range(self.fold):
                        if train_idx == test_idx:
                            continue
                        fold_performance = avg_performances[model][metric][train_idx]
                        for param in fold_performance:
                            if param not in training_data:
                                training_data[param] = .0
                            training_data[param] += fold_performance[param]
                    # sort in descending order based on performance first, then use filenames(x[0]) to break ties
                    sorted_training_performance = sorted(training_data.items(),
                                                         key=lambda x:(x[1], x[0]),
                                                         reverse=True)
                    best_param = sorted_training_performance[0][0]
                    if verbose:
                        print('\tFold: {}'.format(test_idx))
                        print('\t\tBest param: {}'.format(best_param))
                        print('\t\ttest performance: {0:.4f}'.format(test_fold_performances[best_param]))

                    metric_fold_performances.append(test_fold_performances[best_param])
                res[model][metric] = round(sum(metric_fold_performances) / len(metric_fold_performances), 4)
        return res

    def _get_param_avg_performances(self,file_path):
        # Given a file, return its average effectiveness
        # for each metric in each fold
        param_performance_list = {}
        for fold_id in range(self.fold):
            param_performance_list[fold_id] = {}
        with open(file_path) as f:
            for line in f:
                line = line.strip()
                if line:
                    row = line.split()
                    metric = row[0]
                    if metric not in param_performance_list[0]:
                        for fold_id in param_performance_list:
                            param_performance_list[fold_id][metric] = []
                    qid = row[1]
                    try:
                        value = float(row[2])
                    except:
                        self.logger.error( 'Cannot parse %s' %(row[2]) )
                        continue
                    else:
                        if qid != 'all':
                            # compute fold id base on qid
                            fold_id = self._compute_fold_id(qid)
                            param_performance_list[fold_id][metric].append(value)
        param_avg_performances = {}

        for metric in param_performance_list[0].keys():
            param_avg_performances[metric] = {}
            for fold_id in param_performance_list:
                param_avg_performances[metric][fold_id] = round(sum(param_performance_list[fold_id][metric])/len(param_performance_list[fold_id][metric]), 4)
        return param_avg_performances

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output_root', default='fine_tuning_results', help='output directory of all results')
    parser.add_argument('--fold', '-f', default=2, type=int, help='number of fold')
    parser.add_argument('--verbose', '-v', action='store_true', help='output in verbose mode')
    parser.add_argument('--collection', required=True, help='the collection key in yaml')
    parser.add_argument('--fold_dir', help='directory of drr fold files')
    args=parser.parse_args()

    fold_mapping = {}
    if args.fold_dir:
        from run_batch import load_drr_fold_mapping
        fold_mapping = load_drr_fold_mapping(args.fold_dir)
    print(json.dumps(XFoldValidate(args.output_root, args.collection, args.fold, fold_mapping).tune(args.verbose), sort_keys=True, indent=2))

if __name__ == '__main__':
    main()

