import sys,os
import ast
import json
import re
import copy
from inspect import currentframe, getframeinfo
from subprocess import Popen, PIPE

class Evaluation(object):
    """
    Get the evaluation of a corpus for a result.
    When constructing, pass the path of the corpus and the path of the result file. 
    For example, "../wt2g/" "../wt2g/results/idf1"
    """
    def __init__(self, index_path):
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            print frameinfo.filename, frameinfo.lineno
            print '[Search Constructor]:Please provide a valid index path - ' + self.index_path
            exit(1)

        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'

    def gen_eval_paras(self, output_root):
        if not os.path.exists(os.path.join(output_root, self.eval_files_root)):
            os.makedirs(os.path.join(output_root, self.eval_files_root))
        all_paras = []
        for fn in os.listdir(os.path.join(output_root, self.run_files_root)):
            if not os.path.exists( os.path.join(output_root, self.eval_files_root, fn) ):
                all_paras.append( (os.path.join(output_root, self.run_files_root, fn), 
                    os.path.join(output_root, self.eval_files_root, fn)) )
        return all_paras


    @classmethod
    def output_all_evaluations(self, qrel_program, qrel_file_path, result_file_path, output_path):
        """
        get all kinds of performance

        @Return: a dict of all performances 
        """
        process = Popen(' '.join([qrel_program, qrel_file_path, result_file_path]), shell=True, stdout=PIPE)
        stdout, stderr = process.communicate()
        if process.returncode == 0:
            if 'trec_eval' in qrel_program:
                with open( output_path, 'wb' ) as o:
                    o.write(stdout)
            elif 'gdeval' in qrel_program:
                # only ndcg@20 is retained
                with open( output_path, 'wb' ) as o:
                    for line in stdout.split('\n')[1:-1]:
                        line = line.strip()
                        if line:
                            row = line.split(',')
                            qid = row[-3]
                            ndcg20 = row[-2]
                            err20 = row[-1]
                            o.write('ndcg20\t%s\t%s\n' % (qid if qid != 'amean' else 'all', ndcg20))
                            o.write('err20\t%s\t%s\n' % (qid if qid != 'amean' else 'all', err20))
        else:
            print '[ERROR]' 

    def get_all_performance_of_some_queries(self, qids):
        """
        get all kinds of performance

        @Input:
            qids (list) : a list contains the qid that to be returned

        @Return: a dict of all performances of qids
        """

        all_performances = self.get_all_performance()
        return {k: all_performances.get(k, None) for k in qids}


if __name__ == '__main__':
    e = Evaluation('../../wt2g', '../../wt2g/results/tf1')
    print e.get_all_performance()

