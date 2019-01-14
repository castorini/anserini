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
from inspect import currentframe, getframeinfo
from subprocess import Popen, PIPE
import logging

logging.basicConfig()
class Evaluation(object):
    """Get the evaluation of a corpus for a result."""
    def __init__(self, index_path):
        self.logger = logging.getLogger('evalation.Evaluation')
        self.index_path = os.path.abspath(index_path)
        if not os.path.exists(self.index_path):
            frameinfo = getframeinfo(currentframe())
            self.logger.error(frameinfo.filename, frameinfo.lineno)
            self.logger.error('[Search Constructor]:Please provide a valid index path - ' + self.index_path)
            exit(1)

        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'

        self.random_seed_run_files_root = 'random_seed_run_files'
        self.random_seed_eval_files_root = 'random_seed_eval_files'

    def gen_batch_eval_params(self, output_root, metric):
        if not os.path.exists(os.path.join(output_root, self.eval_files_root, metric)):
            os.makedirs(os.path.join(output_root, self.eval_files_root, metric))
        if not os.path.exists(os.path.join(output_root, self.random_seed_eval_files_root, metric)):
            os.makedirs(os.path.join(output_root, self.random_seed_eval_files_root, metric))
        all_params = []
        for fn in os.listdir(os.path.join(output_root, self.run_files_root)):
            if not os.path.exists( os.path.join(output_root, self.eval_files_root, metric, fn) ):
                all_params.append((
                    os.path.join(output_root, self.run_files_root, fn),
                    os.path.join(output_root, self.eval_files_root, metric, fn)
                ))
        for fn in os.listdir(os.path.join(output_root, self.random_seed_run_files_root)):
            if not os.path.exists( os.path.join(output_root, self.random_seed_eval_files_root, metric, fn) ):
                all_params.append((
                    os.path.join(output_root, self.random_seed_run_files_root, fn),
                    os.path.join(output_root, self.random_seed_eval_files_root, metric, fn)
                ))
        return all_params


    @classmethod
    def output_all_evaluations(self, qrel_programs, qrel_file_path, result_file_path, output_path):
        """Returns various effectiveness figures.

        @Return: a dict of all performances 
        """
        for i, qrel_program in enumerate(qrel_programs):
            process = Popen(' '.join([qrel_program, qrel_file_path, result_file_path]), shell=True, stdout=PIPE)
            stdout, stderr = process.communicate()
            if process.returncode == 0:
                try:
                    if i == 0:
                        o = open( output_path, 'w')
                    else:
                        o = open( output_path, 'a')
                    if 'trec_eval' in qrel_program:
                        o.write(stdout)
                    elif 'gdeval' in qrel_program:
                        for line in stdout.split('\n')[1:-1]:
                            line = line.strip()
                            if line:
                                row = line.split(',')
                                qid = row[-3]
                                ndcg20 = row[-2]
                                err20 = row[-1]
                                o.write('ndcg20\t%s\t%s\n' % (qid if qid != 'amean' else 'all', ndcg20))
                                o.write('err20\t%s\t%s\n' % (qid if qid != 'amean' else 'all', err20))
                finally:
                    o.close()
            else:
                logger.error('ERROR when running the evaluation for:' + result_file_path)
