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
import itertools
from inspect import currentframe, getframeinfo
from subprocess import Popen, PIPE
import logging

class Search(object):
    def __init__(self, index_path):
        self.logger = logging.getLogger('search.Search')
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            self.logger.error(frameinfo.filename, frameinfo.lineno)
            self.logger.error('[Search Constructor]:Please provide a valid index path - ' + self.index_path)
            exit(1)

        self.run_files_root = 'run_files'

    def drange(self, x, y, jump):
        while x < y:
            yield float(x)
            x += jump

    def gen_batch_retrieval_params(self, model_yaml, output_root):
        all_params = []
        if not os.path.exists(os.path.join(output_root, self.run_files_root)):
            os.makedirs(os.path.join(output_root, self.run_files_root))
        for p in itertools.product(*[self.drange(param['lower'], param['upper']+1e-8, param['pace']) for param in model_yaml['params'].values()]):
            para_str = '-%s %s' % (model_yaml['name'], model_yaml['fixed_params'])
            rfn = model_yaml['name']+'_'
            for k_idx, k in enumerate(model_yaml['params'].keys()):
                is_float = True if model_yaml['params'][k]['type'] == 'float' else False
                para_str += ' -%s %.1f' % (k, p[k_idx]) if is_float else ' -%s %d' % (k, p[k_idx])
                if 'nexus_params' in model_yaml:
                    for nexus_params in model_yaml['nexus_params']:
                        if model_yaml['nexus_params'][nexus_params] == k:
                            para_str += ' -%s %.1f' % (nexus_params, p[k_idx]) if is_float else ' -%s %d' % (nexus_params, p[k_idx])
                if k_idx != 0:
                    rfn += ','
                rfn += '%s:%.1f' % (k, p[k_idx]) if is_float else '%s:%d' % (k, p[k_idx])
            results_fn = os.path.join(output_root, self.run_files_root, model_yaml['basemodel']+'_'+rfn)
            if not os.path.exists(results_fn):
                all_params.append( (para_str, results_fn) )

        # always include a baseline here
        no_params_fn = os.path.join(output_root, self.run_files_root, model_yaml['basemodel']+'_'+model_yaml['name']+'_noparams')
        if not os.path.exists(no_params_fn):
            all_params.append(('-%s' % (model_yaml['name']), no_params_fn))
        return all_params
