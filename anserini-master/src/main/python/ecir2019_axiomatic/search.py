# -*- coding: utf-8 -*-
#
# Anserini: A toolkit for reproducible information retrieval research built on Lucene
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

import os
import random
import itertools
from inspect import currentframe, getframeinfo
from subprocess import Popen, PIPE
import logging

logging.basicConfig()

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
        self.random_seed_run_files_root = 'random_seed_run_files'

    def drange(self, x, y, jump):
        while x < y:
            yield float(x)
            x += jump

    def gen_batch_retrieval_params(self, model_yaml, output_root, parallelism=4):
        all_params = []
        if not os.path.exists(os.path.join(output_root, self.run_files_root)):
            os.makedirs(os.path.join(output_root, self.run_files_root))
        for model in model_yaml['models']:
            para_str = '-threads %d -%s %s' % (parallelism, model_yaml['name'], model_yaml['fixed_params'])
            if random: # generate the runs with random seeds
                random.seed(42)
            else:
                for param_name, params in model_yaml['params'].items():
                    para_str += ' -%s' % (param_name)
                    for p in self.drange(params['lower'], params['upper']+1e-8, params['pace']):
                        is_float = True if params['type'] == 'float' else False
                        para_str += ' %.2f' % (p) if is_float else ' %d' % (p)
                results_fn = os.path.join(output_root, self.run_files_root, model+'_'+model_yaml['name'])
                all_params.append( (model, para_str, results_fn) )
                # Adding baseline models
                all_params.append( (model, '-inmem -skipexists ', os.path.join(output_root, self.run_files_root, model+'_baseline_'+model)) )

        return all_params

    def gen_random_batch_retrieval_params(self, model_yaml, output_root, beta, parallelism=4, seed = 42, round = 100):
        all_params = []
        if not os.path.exists(os.path.join(output_root, self.random_seed_run_files_root)):
            os.makedirs(os.path.join(output_root, self.random_seed_run_files_root))

        for model in model_yaml['models']:
            para_str = '-threads %d -%s %s' % (parallelism, model_yaml['name'], model_yaml['fixed_params'])
            para_str += ' -rerankCutoff 20 -axiom.beta {} -axiom.seed '.format(beta)
            random.seed(42)
            for i in range(round):
                seed = random.randint(1, 10000)
                para_str += ' {}'.format(seed)
            results_fn = os.path.join(output_root, self.random_seed_run_files_root, model+'_'+model_yaml['name'])
            all_params.append( (model, para_str, results_fn) )

        return all_params
