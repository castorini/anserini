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

from __future__ import print_function
import os
import itertools
from subprocess import call, Popen, PIPE
from multiprocessing import Pool
import argparse
import json
import logging

import yaml

logger = logging.getLogger('regression_test')
logger.setLevel(logging.INFO)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
# add the handlers to the logger
logger.addHandler(ch)


def is_close(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)


def check_output(command):
    """Python 2.6 compatible subprocess.check_output."""
    process = Popen(command, shell=True, stdout=PIPE)
    output, err = process.communicate()
    if process.returncode == 0: # success
        return output
    else:
        raise RuntimeError("Command {0} running unsuccessfully".format(command))


def get_index_path(yaml_data):
    """Find the index path."""
    index_path = os.path.join('lucene-index.{0}.pos+docvectors{1}'.format(yaml_data['name'], \
        '+rawdocs' if '-storeRawDocs' in yaml_data['index_options'] else ''))
    if not os.path.exists(index_path):
        index_path = yaml_data['index_path']
        if not index_path or not os.path.exists(index_path):
            for input_root in yaml_data['input_roots']:
                if os.path.exists(os.path.join(input_root, yaml_data['index_path'])):
                    index_path = os.path.join(input_root, yaml_data['index_path'])
                    break
    return index_path


def construct_indexing_command(yaml_data, args):
    """Construct the Anserini indexing command.

    Args:
        yaml_data (dict): The yaml config read from config file.
        args: the command-line arguments.

    Returns:
        (:obj:`list` of :obj:`str`): the command as a list that can be executed by calling subprocess.call(command)
    """
    logger.info('='*10+'Indexing'+'='*10)

    # Determine the input collection path, either from the command line,
    # or by checking various locations specified in the YAML.
    collection_path = None
    if args.collection_path != '':
        if  os.path.exists(args.collection_path):
            collection_path = args.collection_path
    else:
        for input_root in yaml_data['input_roots']:
            collection_path = os.path.join(input_root, yaml_data['input'])
            if os.path.exists(collection_path):
                break

    if not collection_path:
        raise RuntimeError("Unable to find input collection path!")

    # Determine the number of indexing threads, either from the command line,
    # or reading the YAML config.
    if args.indexing_threads != -1:
        threads = args.indexing_threads
    else:
        threads = yaml_data['threads']

    index_command = [
        os.path.join(yaml_data['root'], yaml_data['index_command']),
        '-collection', yaml_data['collection'],
        '-generator', yaml_data['generator'],
        '-threads', str(threads),
        '-input', collection_path,
        '-index', 'lucene-index.{0}.pos+docvectors{1}'
            .format(yaml_data['name'], '+rawdocs' if '-storeRawDocs' in yaml_data['index_options'] else '')
    ]
    index_command.extend(yaml_data['index_options'])
    return index_command


def verify_index(yaml_data, build_index=True, dry_run=False):
    """Verify index statistics (e.g., total documents, total terms) so that we know we are searching
    against the correct index.

    Args:
        yaml_data (dict): the yaml config
    """
    logger.info('='*10+'Verifying Index'+'='*10)
    index_path = get_index_path(yaml_data)
    logger.info('[Index]: ' + index_path)
    index_utils_command = [
        os.path.join(yaml_data['root'], yaml_data['index_utils_command']),
        '-index', index_path, '-stats'
    ]
    if dry_run:
        logger.info(index_utils_command)
    else:
        out = check_output(' '.join(index_utils_command)).decode('utf-8').split('\n')
        for line in out:
            stat = line.split(':')[0]
            if stat in yaml_data['index_stats']:
                value = int(line.split(':')[1])
                if value != yaml_data['index_stats'][stat]:
                    print('{}: expected={}, actual={}'.format(stat, yaml_data['index_stats'][stat], value))
                assert value == yaml_data['index_stats'][stat]
                logger.info(line)
        logger.info('='*10+'Verifying Index Succeed'+'='*10)


def construct_ranking_command(output_root, yaml_data, build_index=True):
    """Construct the Anserini ranking commands for regression tests.

    Args:
        output_root (string): location of folder for run files
        yaml_data (dict): the yaml config
        build_index (bool): if the index is not built by this script then read the index path from config

    Returns:
        (:obj:`list` of :obj:`list` of :obj:`str`): the ranking commands as several commands that can be
        executed by calling subprocess.call(command)
    """
    ranking_commands = [
        [
            os.path.join(yaml_data['root'], yaml_data['search_command']),
            '-topicreader', yaml_data['topic_reader'],
            '-index', get_index_path(yaml_data),
            ' '.join(model['params']),
            '-topics', os.path.join(yaml_data['root'], yaml_data['topic_root'], topic['path']),
            '-output', os.path.join(output_root, 'run.{0}.{1}.{2}'.format(yaml_data['name'], model['name'], topic['path'])),
        ] + (yaml_data['search_options'] if 'search_options' in yaml_data else [])
        for (model, topic) in list(itertools.product(yaml_data['models'], yaml_data['topics']))
    ]
    return ranking_commands


def evaluate_and_verify(output_root, yaml_data, fail_eval, dry_run):
    """Evaluate run files and verify results stored in the yaml config.

    Args:
        output_root (string): location of folder for run files
        yaml_data (dict): the yaml config
        dry_run (bool): if True, print out commands without actually running them
    """
    logger.info('='*10+'Verifying Results'+'='*10)
    success = True
    for model in yaml_data['models']:
        for i, topic in enumerate(yaml_data['topics']):
            for eval in yaml_data['evals']:
                eval_cmd = [
                  os.path.join(yaml_data['root'], eval['command']),
                  ' '.join(eval['params']) if eval['params'] else '',
                  os.path.join(yaml_data['root'], yaml_data['qrels_root'], topic['qrel']),
                  os.path.join(output_root, 'run.{0}.{1}.{2}'.format(yaml_data['name'], model['name'], topic['path']))
                ]
                if dry_run:
                    logger.info(' '.join(eval_cmd))
                    continue

                out = [line for line in check_output(' '.join(eval_cmd)).decode('utf-8').split('\n') if line.strip()][-1]
                if not out.strip():
                    continue
                eval_out = out.strip().split(eval['separator'])[eval['parse_index']]
                expected = round(model['results'][eval['metric']][i], eval['metric_precision'])
                actual = round(float(eval_out), eval['metric_precision'])
                res = {
                    'collection': yaml_data['name'],
                    'model': model['name'],
                    'topic': topic['name'],
                    'metric': eval['metric'],
                    'expected': expected,
                    'actual': actual
                }
                if is_close(expected, actual):
                    logger.info(json.dumps(res, sort_keys=True))
                else:
                    success = False
                    logger.error('!'*5+json.dumps(res, sort_keys=True)+'!'*5)
                    if fail_eval:
                        assert False
    if success:
        logger.info("All Tests Passed!")


def ranking_atom(cmd):
    logger.info(' '.join(cmd))
    if not args.dry_run:
        call(' '.join(cmd), shell=True)


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Regression Tests')
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument('--collection', required=True, help='collection key in yaml')
    parser.add_argument('--index', dest='index', action='store_true', help='build index from scratch')
    parser.add_argument('--no_retrieval', dest='no_retrieval', action='store_true', help='do not perform retrieval')
    parser.add_argument('--dry_run', dest='dry_run', action='store_true',
                        help='output commands without actual execution')
    parser.add_argument('--n', dest='parallelism', type=int, default=4, help='number of parallel threads for ranking')
    parser.add_argument('--fail_eval', dest='fail_eval', action='store_true',
                        help='fail when any run does not match expected effectiveness')
    parser.add_argument('--output_root', default='runs.regression', help='output directory of all results')

    parser.add_argument('--indexing_threads', type=int, default=-1, help='override number of indexing threads from YAML')
    parser.add_argument('--collection_path', default='', help='override collection input path from YAML')
    args = parser.parse_args()

    if not os.path.exists(args.output_root):
        os.makedirs(args.output_root)

    # TODO: A better way might be using dataclasses as the model to hold the data
    # https://docs.python.org/3/library/dataclasses.html
    with open(os.path.join(args.anserini_root,
                           'src/main/resources/regression/{}.yaml'.format(args.collection))) as f:
        yaml_data = yaml.safe_load(f)

    yaml_data['root'] = args.anserini_root

    # Decide if we're going to index from scratch. If not, use pre-stored index at known location.
    if args.index:
        indexing_command = ' '.join(construct_indexing_command(yaml_data, args))
        logger.info(indexing_command)
        if not args.dry_run:
            call(indexing_command, shell=True)

    verify_index(yaml_data, args.index, args.dry_run)

    if not args.no_retrieval:
        logger.info('='*10+'Ranking'+'='*10)
        run_cmds = construct_ranking_command(args.output_root, yaml_data, args.index)
        p = Pool(args.parallelism)
        p.map(ranking_atom, run_cmds)

    evaluate_and_verify(args.output_root, yaml_data, args.fail_eval, args.dry_run)
