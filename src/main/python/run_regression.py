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
import yaml
import itertools
from subprocess import call, check_output
import argparse

OKBLUE = '\033[94m'
FAIL = '\033[91m'
ENDC = '\033[0m'

def construct_indexing_command(yaml_data):
  """Construct the Anserini indexing command for regression test
  Args:
    yaml_data (dict): The yaml config read from config file.

  Returns:
    (:obj:`list` of :obj:`str`): The command as a list that can be run by calling subprocess.call(command)
  """
  index_command = [
    os.path.join(yaml_data['root'], yaml_data['index_command']),
    '-collection', yaml_data['collection'],
    '-generator', yaml_data['generator'],
    '-threads', str(yaml_data['threads']),
    '-input', yaml_data['input'],
    '-index', yaml_data['name']+'_index',
    *(['-'+c for c in yaml_data['index_options']])
  ]
  return index_command

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
      '-index', os.path.join(yaml_data['index_root'] if yaml_data['index_root'] else "", yaml_data['name']+'_index')
      if build_index else yaml_data['index_path'],
      '-topics', os.path.join(yaml_data['root'], yaml_data['topic_root'], topic),
      '-output', 'run.{}.{}.{}'.format(yaml_data['name'], model, topic),
      *(['-'+c for c in yaml_data['models'][model]['paras']])
    ]
    for (model, topic) in list(itertools.product(yaml_data['models'].keys(), yaml_data['topics']))
  ]
  return ranking_commands

def eval_n_verify(yaml_data, dry_run):
  """Evaluate the ranking files and verify the results with what are stored in yaml file
  Args:
    yaml_data (dict): The yaml config read from config file.
    dry_run (bool): If True, we just print out the commands without actually running them

  Returns:
  """
  print('=========Verifying=========')
  try:
    eval_cmds = [
      [
        os.path.join(yaml_data['root'], yaml_data['eval']),
        *(yaml_data['eval_paras']),
        os.path.join(yaml_data['root'], yaml_data['qrels_root'], qrels),
        'run.{}.{}.{}'.format(yaml_data['name'], model, topic)
      ]
      for (model, (topic, qrels)) in list(itertools.product(
        yaml_data['models'].keys(), zip(yaml_data['topics'], yaml_data['qrels'])))
    ]
    i = 0
    for model in yaml_data['models']:
      for j, topic in enumerate(yaml_data['topics']):
        if dry_run:
          print(' '.join(eval_cmds[i]))
        else:
          out = check_output(' '.join(eval_cmds[i]), shell=True).decode('utf-8')
          if not out.strip():
            continue
          eval_out_split = out.split(yaml_data['eval_separator'])
          for metric in yaml_data['models'][model]:
            if metric == 'paras':
              continue
            expected = yaml_data['models'][model][metric][j]
            real = round(float(eval_out_split[yaml_data['eval_'+metric+'_idx']].split( )[-1]), yaml_data['metric_precision'])
            if expected == real:
              print(OKBLUE, yaml_data['name'], model, topic, metric, expected, real)
            else:
              print(FAIL, yaml_data['name'], model, topic, metric, expected, real, '!!!!')
        i+=1
  finally:
    print(ENDC)


if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='Regression Tests')
  parser.add_argument('--config', required=True, help='Yaml config file')
  parser.add_argument('--collection', required=True, help='the collection key in yaml')
  parser.add_argument('--index', dest='index', action='store_true', help='rebuild index from scratch')
  parser.add_argument('--dry_run', dest='dry_run', action='store_true',
  help='output the commands but not actually running them. this is useful for development/debug')
  args = parser.parse_args()

  with open(args.config) as f:
    # use safe_load instead load
    dataMap = yaml.safe_load(f)

  # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
  if args.index:
    print('=========Indexing=========')
    if args.dry_run:
      print(' '.join(construct_indexing_command(dataMap[args.collection])))
    else:
      call(' '.join(construct_indexing_command(dataMap[args.collection])), shell=True)
  print('=========Ranking=========')
  run_cmds = construct_ranking_command(dataMap[args.collection], args.index)
  for cmd in run_cmds:
    if args.dry_run:
      print(' '.join(cmd))
    else:
      call(' '.join(cmd), shell=True)
  eval_n_verify(dataMap[args.collection], args.dry_run)
