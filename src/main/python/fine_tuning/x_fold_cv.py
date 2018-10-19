"""
tuning parameters using x-fold cross-validation
"""

import os
import re
import argparse
import logging


class XFlodValidate(object):
    """
    Tune x-fold 
    """
    def __init__(self,output_root,fold=2):
        self.logger = logging.getLogger('x_fold_cv.XFlodValidate')
        self.output_root = output_root
        self.eval_files_root = 'eval_files'
        self.fold = fold

    def _get_param_average(self):
        # Get the average performance of each 
        # parameter set in each fold
        avg_performances = {}
        for collection_name in os.walk(self.output_root).next()[1]:
            eval_root_dir = os.path.join(self.output_root, collection_name,self.eval_files_root)
            for metric in os.walk(eval_root_dir).next()[1]: 
                eval_dir = os.path.join(eval_root_dir,metric)
                for fn in  os.listdir(eval_dir):
                    basemodel, model, param = fn.split("_")
                    if basemodel not in avg_performances:
                        avg_performances[basemodel] = {}
                    if model not in avg_performances[basemodel]:
                        avg_performances[basemodel][model] = {}

                    param_avg_performances = self._get_param_avg_performances(os.path.join(eval_dir,fn))
                    for metric in param_avg_performances:
                        if metric not in avg_performances[basemodel][model]:
                            avg_performances[basemodel][model][metric] = {}
                        for i in param_avg_performances[metric]:
                            if i not in avg_performances[basemodel][model][metric]:
                                avg_performances[basemodel][model][metric][i] = {}
                            avg_performances[basemodel][model][metric][i][param] = param_avg_performances[metric][i]

        return avg_performances

    def tune(self,quite):
        # x-fold tuning
        avg_performances = self._get_param_average()
        for basemodel in avg_performances:
            for model in avg_performances[basemodel]:
                for metric in avg_performances[basemodel][model]:
                    print "For %s %s %s" %(basemodel, model, metric)
                    metric_fold_performances = []
                    for i in xrange(self.fold):
                        test_fold_performances = avg_performances[basemodel][model][metric][i]
                        training_data = {}
                        for j in xrange(self.fold):
                            if j == i:
                                continue
                            fold_performance = avg_performances[basemodel][model][metric][j]
                            for param in  fold_performance:
                                if param not in training_data:
                                    training_data[param] = .0
                                training_data[param] += fold_performance[param]
                        sorted_training_performance = sorted(training_data.items(),
                                                             key=lambda x:x[1],
                                                             reverse=True)
                        best_param = sorted_training_performance[0][0]
                        if not quite:
                            print "\tFold: %d" %(i)
                            print "\t\tBest param: %s" %(best_param)
                            print "\t\ttest performance: {0:.4f}".format(test_fold_performances[best_param])

                        metric_fold_performances.append(test_fold_performances[best_param])
                    print "\tAverage {0:.4f}".format( sum(metric_fold_performances) / len(metric_fold_performances))

    def _get_param_avg_performances(self,file_path):
        param_performance_list = {}
        for i in xrange(self.fold):
            param_performance_list[i] = {}
        with open(file_path) as f:
            for line in f:
                line = line.strip()
                if line:
                    row = line.split()
                    metric = row[0]
                    if metric not in param_performance_list[0]:
                        for i in param_performance_list:
                            param_performance_list[i][metric] = []
                    qid = row[1]
                    try:
                        value = float(row[2])
                    except:
                        self.logger.error( "Cannot parse %s" %(row[2]) )
                        continue
                    else:
                        if qid != "all":
                            # compute fold id base on qid
                            idx = int(qid) % self.fold
                            param_performance_list[idx][metric].append(value)
        param_avg_performances = {}

        for metric in param_performance_list[0].keys():
            param_avg_performances[metric] = {}
            for i in param_performance_list:
                param_avg_performances[metric][i] = sum(param_performance_list[i][metric])/len(param_performance_list[i][metric])
        return param_avg_performances

def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--output_root', default='fine_tuning_results', help='output directory of all results')
    parser.add_argument('--fold',"-f", default=2, type=int, help='number of fold')
    parser.add_argument('--quite',"-q", action="store_true", help="output in quite mode")
    args=parser.parse_args()
    
    XFlodValidate(args.output_root,args.fold).tune(args.quite)

if __name__=="__main__":
    main()

