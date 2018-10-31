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


class XFoldValidate(object):
    """
    Perform X-Fold cross validation for various 
    parameters and report the average effectiveness
    for each fold
    """
    def __init__(self,output_root,fold=2):
        self.logger = logging.getLogger('x_fold_cv.XFlodValidate')
        self.output_root = output_root
        self.eval_files_root = 'eval_files'
        self.fold = fold

    def _get_param_average(self):
        # For each parameter set, get its 
        # average performances in each fold,
        # metric, reranking model, and base 
        # ranking model
        avg_performances = {}
        for collection_name in os.listdir(self.output_root):
            eval_root_dir = os.path.join(self.output_root, collection_name,self.eval_files_root)
            if os.path.isfile(eval_root_dir):
                continue
            # if it is a directory for a collection,
            # do x-fold cv for the collection
            for metric in os.listdir(eval_root_dir):
                eval_dir = os.path.join(eval_root_dir,metric)
                if os.path.isfile(eval_dir):
                    continue
                # if it is a directory containing effectiveness
                # for a metric, do x-fold cv for the metric
                for fn in os.listdir(eval_dir):
                    basemodel, model, param = fn.split('_')
                    if basemodel not in avg_performances:
                        avg_performances[basemodel] = {}
                    if model not in avg_performances[basemodel]:
                        avg_performances[basemodel][model] = {}

                    param_avg_performances = self._get_param_avg_performances(os.path.join(eval_dir,fn))
                    for metric in param_avg_performances:
                        if metric not in avg_performances[basemodel][model]:
                            avg_performances[basemodel][model][metric] = {}
                        for fold_id in param_avg_performances[metric]:
                            if fold_id not in avg_performances[basemodel][model][metric]:
                                avg_performances[basemodel][model][metric][fold_id] = {}
                            avg_performances[basemodel][model][metric][fold_id][param] = param_avg_performances[metric][fold_id]

        return avg_performances

    def tune(self,verbose):
        # Tune parameter with x-fold. Use x-1 fold
        # for training and 1 fold for testing. Do
        # it for each fold and report average
        avg_performances = self._get_param_average()
        res = {}
        for basemodel in avg_performances:
            res[basemodel] = {}
            for model in avg_performances[basemodel]:
                res[basemodel][model] = {}
                for metric in avg_performances[basemodel][model]:
                    if verbose:
                        print('For {} {} {}'.format(basemodel, model, metric))
                    metric_fold_performances = []
                    for test_idx in xrange(self.fold):
                        test_fold_performances = avg_performances[basemodel][model][metric][test_idx]
                        training_data = {}
                        for train_idx in xrange(self.fold):
                            if train_idx == test_idx:
                                continue
                            fold_performance = avg_performances[basemodel][model][metric][train_idx]
                            for param in fold_performance:
                                if param not in training_data:
                                    training_data[param] = .0
                                training_data[param] += fold_performance[param]
                        sorted_training_performance = sorted(training_data.items(),
                                                             key=lambda x:x[1],
                                                             reverse=True)
                        best_param = sorted_training_performance[0][0]
                        if verbose:
                            print('\tFold: {}'.format(test_idx))
                            print('\t\tBest param: {}'.format(best_param))
                            print('\t\ttest performance: {0:.4f}'.format(test_fold_performances[best_param]))

                        metric_fold_performances.append(test_fold_performances[best_param])
                    res[basemodel][model][metric] = round(sum(metric_fold_performances) / len(metric_fold_performances), 4)
        return res

    def _get_param_avg_performances(self,file_path):
        # Given a file, return its average effectiveness
        # for each metric in each fold
        param_performance_list = {}
        for fold_id in xrange(self.fold):
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
                            fold_id = int(qid) % self.fold
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
    args=parser.parse_args()

    print(json.dumps(XFoldValidate(args.output_root, args.fold).tune(args.verbose), sort_keys=True, indent=2))

if __name__ == '__main__':
    main()

