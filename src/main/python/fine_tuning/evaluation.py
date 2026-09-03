#
# Anserini: A Lucene toolkit for reproducible information retrieval research
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
#

import logging
import os
from subprocess import PIPE, Popen

logging.basicConfig()


class Evaluation:
    """
    Get the evaluation of a corpus for a result
    """
    def __init__(self):
        self.logger = logging.getLogger('evalation.Evaluation')
        self.run_files_root = 'run_files'
        self.eval_files_root = 'eval_files'

    def gen_batch_eval_params(self, output_root, metric):
        if not os.path.exists(os.path.join(output_root, self.eval_files_root, metric)):
            os.makedirs(os.path.join(output_root, self.eval_files_root, metric))
        all_params = []
        for fn in os.listdir(os.path.join(output_root, self.run_files_root)):
            if not os.path.exists( os.path.join(output_root, self.eval_files_root, metric, fn) ):
                all_params.append((
                    os.path.join(output_root, self.run_files_root, fn),
                    os.path.join(output_root, self.eval_files_root, metric, fn)
                ))
        return all_params

    @classmethod
    def output_all_evaluations(self, qrel_programs, qrel_file_path, result_file_path, output_path):
        """
        get all kinds of performance

        @Return: a dict of all performances 
        """
        for i, qrel_program in enumerate(qrel_programs):
            process = Popen(f'{qrel_program} {qrel_file_path} {result_file_path}', shell=True, stdout=PIPE, stderr=PIPE)
            stdout, stderr = process.communicate()
            if process.returncode == 0:
                mode = 'w' if i == 0 else 'a'
                with open(output_path, mode) as o:
                    if 'trec_eval' in qrel_program:
                        o.write(stdout.decode("utf-8"))
                    elif 'gdeval' in qrel_program:
                        for line in stdout.decode("utf-8").split('\n')[1:-1]:
                            line = line.strip()
                            if line:
                                row = line.split(',')
                                qid = row[-3]
                                ndcg20 = row[-2]
                                err20 = row[-1]
                                o.write('ndcg20\t{}\t{}\n'.format(qid if qid != 'amean' else 'all', ndcg20))
                                o.write('err20\t{}\t{}\n'.format(qid if qid != 'amean' else 'all', err20))
            else:
                raise RuntimeError('Error when running the evaluation for {}: {}'.format(result_file_path, stderr.decode('utf-8').strip()))
