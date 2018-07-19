#!/Users/peiliny/miniconda3/bin/python
"""
Anserini: An information retrieval toolkit built on Lucene

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
import itertools
from subprocess import call, Popen, PIPE
from multiprocessing import Pool
import argparse

import yaml

OKBLUE = '\033[94m'
FAIL = '\033[91m'
ENDC = '\033[0m'

def isclose(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)

def check_output(command):
    """
    Python 2.6 compatible subprocess.check_output
    """
    process = Popen(command, shell=True, stdout=PIPE)
    output, err = process.communicate()
    if process.returncode == 0: # success
        return output
    else:
        raise RuntimeError("Command {0} running unsuccessfully".format(command))

def construct_indexing_command(yaml_data):
    """Construct the Anserini indexing command for regression test
    Args:
      yaml_data (dict): The yaml config read from config file.

    Returns:
      (:obj:`list` of :obj:`str`): The command as a list that can be run by calling subprocess.call(command)
    """
    print('='*10, 'Indexing', '='*10)
    index_command = [
        os.path.join(yaml_data['root'], yaml_data['index_command']),
        '-collection', yaml_data['collection'],
        '-generator', yaml_data['generator'],
        '-threads', str(yaml_data['threads']),
        '-input', yaml_data['input'],
        '-index', 'lucene-index.{0}.pos+docvectors{1}'.format(yaml_data['name'], '+rawdocs' if 'storeRawdocs' in yaml_data['index_options'] else '')
    ]
    index_command.extend(yaml_data['index_options'])
    return index_command

def verify_index(yaml_data, build_index=True):
    """Verify the index statistics (e.g. total documents, total terms) so that we know we are searching
    against the correct index

    Args:
      yaml_data (dict): The yaml config read from config file.
    """
    print('='*10, 'Verifying Index', '='*10)
    index_utils_command = [
        os.path.join(yaml_data['root'], yaml_data['index_utils_command']),
        '-index', os.path.join(yaml_data['index_root'] if yaml_data['index_root'] else '',
        'lucene-index.{0}.pos+docvectors{1}'.format(yaml_data['name'], '+rawdocs' if 'storeRawdocs' in yaml_data['index_options'] else ''))
        if build_index else yaml_data['index_path'],
        '-stats'
    ]
    out = check_output(' '.join(index_utils_command)).decode('utf-8').split('\n')
    for line in out:
        stat = line.split(':')[0]
        if stat in yaml_data['index_stats']:
            value = int(line.split(':')[1])
            assert value == yaml_data['index_stats'][stat]
            print(line)
    print(OKBLUE, '='*10, 'Verifying Index Succeed', '='*10, ENDC)

def construct_ranking_command(yaml_data, build_index=True):
    """Construct the Anserini ranking commands for regression test
    Args:
      yaml_data (dict): The yaml config read from config file.
      build_index (bool): If the index is not built by this script then read the index path for config

    Returns:
      (:obj:`list` of :obj:`list` of :obj:`str`):
      The ranking commands as several commands that can be run by calling subprocess.call(command)
    """
    ranking_commands = [
        [
            os.path.join(yaml_data['root'], yaml_data['search_command']),
            '-topicreader', yaml_data['topic_reader'],
            '-index', os.path.join(yaml_data['index_root'] if yaml_data['index_root'] else '',
            'lucene-index.{0}.pos+docvectors{1}'.format(yaml_data['name'], '+rawdocs' if 'storeRawdocs' in yaml_data['index_options'] else ''))
            if build_index else yaml_data['index_path'],
            ' '.join(model['params']),
            '-topics', os.path.join(yaml_data['root'], yaml_data['topic_root'], topic['path']),
            '-output', 'run.{0}.{1}.{2}'.format(yaml_data['name'], model['name'], topic['path'])
        ]
        for (model, topic) in list(itertools.product(yaml_data['models'], yaml_data['topics']))
    ]
    return ranking_commands

def eval_n_verify(yaml_data, dry_run, fail_eval):
    """Evaluate the ranking files and verify the results with what are stored in yaml file
    Args:
      yaml_data (dict): The yaml config read from config file.
      dry_run (bool): If True, we just print out the commands without actually running them
    """
    print('='*10, 'Verifying Results', '='*10)
    try:
        for model in yaml_data['models']:
            for i, topic in enumerate(yaml_data['topics']):
                for eval in yaml_data['evals']:
                    eval_cmd = [
                      os.path.join(yaml_data['root'], eval['command']),
                      ' '.join(eval['params']) if eval['params'] else '',
                      os.path.join(yaml_data['root'], yaml_data['qrels_root'], topic['qrel']),
                      'run.{0}.{1}.{2}'.format(yaml_data['name'], model['name'], topic['path'])
                    ]
                    if dry_run:
                        print(' '.join(eval_cmd))
                        continue

                    out = [line for line in check_output(' '.join(eval_cmd)).decode('utf-8').split('\n') if line.strip()][-1]
                    if not out.strip():
                        continue
                    eval_out = out.strip().split(eval['separator'])[eval['parse_index']]
                    expected = round(model['results'][eval['metric']][i], eval['metric_precision'])
                    real = round(float(eval_out), eval['metric_precision'])
                    if isclose(expected, real):
                        print(OKBLUE, '[OK]', yaml_data['name'], model['name'], topic['name'], eval['metric'], expected, real, ENDC)
                    else:
                        print(FAIL, ['ERROR'], yaml_data['name'], model['name'], topic['name'], eval['metric'], expected, real, '!!!!', ENDC)
                        if fail_eval:
                            assert False
    finally:
        print(ENDC)


def ranking_atom(cmd):
    print(' '.join(cmd))
    if not args.dry_run:
        call(' '.join(cmd), shell=True)


if __name__ == '__main__':
      parser = argparse.ArgumentParser(description='Regression Tests')
      parser.add_argument('--config', default='src/main/resources/regression/all.yaml', help='Yaml config file')
      parser.add_argument('--anserini_root', default='', help='Anserini path')
      parser.add_argument('--collection', required=True, help='the collection key in yaml')
      parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')
      parser.add_argument('--dry_run', dest='dry_run', action='store_true',
          help='output the commands but not actually running them. this is useful for development/debug')
      parser.add_argument('--n', dest='parallelism', type=int, default=4, help='number of parallel threads for ranking')
      parser.add_argument('--fail_eval', dest='fail_eval', action='store_true', help='when enabled any eval inconsistency will fail the program')
      args = parser.parse_args()

      # TODO: A better way might be using dataclasses as the model to hold the data
      # https://docs.python.org/3/library/dataclasses.html
      with open(os.path.join(args.anserini_root, args.config)) as f:
          dataMap = yaml.safe_load(f)

      yaml_data = dataMap['collections'][args.collection]
      yaml_data['root'] = args.anserini_root
      # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
      if args.index:
          print(' '.join(construct_indexing_command(yaml_data)))
          if not args.dry_run:
              call(' '.join(construct_indexing_command(yaml_data)), shell=True)

      verify_index(yaml_data, args.index)

      print('='*10, 'Ranking', '='*10)
      run_cmds = construct_ranking_command(yaml_data, args.index)
      p = Pool(args.parallelism)
      p.map(ranking_atom, run_cmds)

      eval_n_verify(yaml_data, args.dry_run, args.fail_eval)
