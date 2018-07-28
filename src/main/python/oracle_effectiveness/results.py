# -*- coding: utf-8 -*-
import sys,os
from inspect import currentframe, getframeinfo
import argparse

reload(sys)
sys.setdefaultencoding('utf-8')


class Results(object):
    """
    Handle the results. For example, merge the split results.
    When constructing, pass the path of the corpus. For example, "../wt2g/"
    """
    def __init__(self, collection_path):
        self.corpus_path = os.path.abspath(collection_path)
        if not os.path.exists(self.corpus_path):
            frameinfo = getframeinfo(currentframe())
            print frameinfo.filename, frameinfo.lineno
            print '[Evaluation Constructor]:Please provide a valid corpus path'
            exit(1)

        self.split_queries_root = os.path.join(self.corpus_path, 'split_queries')
        self.split_results_root = os.path.join(self.corpus_path, 'split_results')
        self.merged_results_root = os.path.join(self.corpus_path, 'merged_results')
        if not os.path.exists(self.merged_results_root):
            os.makedirs(self.merged_results_root)

    def gen_merge_split_results_paras(self, total_query_cnt, use_which_part=['title']):
        all_paras = []
        all_results = {}
        for fn in os.listdir(self.split_results_root):
            #print fn
            query = fn.split('-')[0]
            method = '-'.join(fn.split('-')[1:])
            query_part, qid = query.split('_')
            label = query_part+'-'+method
            collect_results_fn = os.path.join(self.merged_results_root, label)
            if not os.path.exists(collect_results_fn):
                if label not in all_results:
                    all_results[label] = []
                all_results[label].append( os.path.join(self.split_results_root, fn) )

        for label in all_results:
            if len(all_results[label]) < total_query_cnt:
                print 'Results of '+ self.corpus_path + ':' + label +' not enough (%d/%d).' % (len(all_results[label]), total_query_cnt)
                continue
            tmp = [os.path.join(self.merged_results_root, label)]
            tmp.extend( all_results[label] )
            all_paras.append(tmp)

        return all_paras


if __name__ == '__main__':
    pass

