# -*- coding: utf-8 -*-
import sys,os
import decimal
import itertools
from inspect import currentframe, getframeinfo
from subprocess import Popen, PIPE

reload(sys)
sys.setdefaultencoding('utf-8')

class Search(object):
    def __init__(self, index_path):
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            print frameinfo.filename, frameinfo.lineno
            print '[Search Constructor]:Please provide a valid index path - ' + self.index_path
            exit(1)

        self.run_files_root = 'run_files'

    def drange(self, x, y, jump):
        while x < y:
            yield float(x)
            x += jump

    def gen_run_batch_paras(self, topic_type, methods, output_root):
        all_paras = []
        if not os.path.exists(os.path.join(output_root, self.run_files_root)):
            os.makedirs(os.path.join(output_root, self.run_files_root))
        for m in methods:
            if 'paras' in m:
                for p in itertools.product(*[self.drange(ele[0], ele[1]+1e-8, ele[2]) for ele in m['paras'].values()]):
                    para_str = '-%s' % m['name']
                    rfn = m['name']+'-'
                    for k_idx, k in enumerate(m['paras'].keys()):
                        para_str += ' -%s %s' % (k, p[k_idx])
                        if k_idx != 0:
                            rfn += ','
                        rfn += '%s:%s' % (k, p[k_idx])
                    results_fn = os.path.join(output_root, self.run_files_root, topic_type+'_'+rfn)
                    if not os.path.exists(results_fn):
                        all_paras.append( (para_str, results_fn) )
            else:
                para_str = '-%s' % m['name']
                results_fn = os.path.join(self.run_files_root, topic_type+'_'+m['name'])
                if not os.path.exists(results_fn):
                    all_paras.append( (para_str, results_fn) )
            
        return all_paras
        
