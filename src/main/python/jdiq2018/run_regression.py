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
import subprocess
import argparse
from multiprocessing import Pool
import json
import logging

import yaml

from search import Search
from evaluation import Evaluation
from effectiveness import Effectiveness

logger = logging.getLogger('jdiq2018')
logger.setLevel(logging.INFO)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
# add the handlers to the logger
logger.addHandler(ch)

parallelism=1


def batch_everything(all_params, func):
    if len(all_params) == 0:
        return
    p = Pool(parallelism)
    p.map(func, all_params)

def isclose(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)


def get_index_path(yaml_data):
    """
    Find the possible index path
    """
    index_path = os.path.join('lucene-index.{0}.pos+docvectors{1}'
                              .format(yaml_data['name'],
                                      '+rawdocs' if '-storeRawDocs' in yaml_data['index_options'] else ''))
    if not os.path.exists(index_path):
        index_path = yaml_data['index_path']
        if not index_path or not os.path.exists(index_path):
            for input_root in yaml_data['input_roots']:
                if os.path.exists(os.path.join(input_root, yaml_data['index_path'])):
                    index_path = os.path.join(input_root, yaml_data['index_path'])
                    break
    return index_path


def batch_retrieval(collection_yaml, models_yaml, output_root):
    all_params = []
    program = os.path.join(collection_yaml['anserini_root'], 'target/appassembler/bin', 'SearchCollection')
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    if not os.path.exists(this_output_root):
        os.makedirs(this_output_root)
    logger.info('='*10+'Batch Retrieval Parameters Generated'+'='*10)
    for topic in collection_yaml['topics']:
        model_params = Search(index_path).gen_batch_retrieval_params(topic['path'], models_yaml, this_output_root)
        for para in model_params:
            this_para = (
                program,
                '-searchtweets' if 'mb' in collection_yaml['name'] else '',
                '-topicreader', collection_yaml['topic_reader'],
                '-index', index_path,
                '-topics', os.path.join(collection_yaml['anserini_root'],
                                        collection_yaml['topic_root'], topic['path']),
                para[0],
                '-output', para[1]
            )
            all_params.append(this_para)
    logger.info('='*10+'Starting Batch Retrieval'+'='*10)
    batch_everything(all_params, atom_retrieval)


def atom_retrieval(para):
    subprocess.call(' '.join(para), shell=True)


def batch_eval(collection_yaml, models_yaml, output_root):
    all_params = []
    index_path = get_index_path(collection_yaml)
    programs = set([eval['command'] for eval in collection_yaml['evals']])
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    if not os.path.exists(this_output_root):
        os.makedirs(this_output_root)
    eval_params = Evaluation(index_path).gen_batch_eval_params(this_output_root)
    for para in eval_params:
        topic_path, run_file_path, eval_output = para
        for topic in collection_yaml['topics']:
            if topic['path'] == topic_path:
                this_para = (
                    [os.path.join(collection_yaml['anserini_root'], program) for program in programs],
                    os.path.join(collection_yaml['anserini_root'], collection_yaml['qrels_root'], topic['qrel']),
                    run_file_path,
                    eval_output
                )
                all_params.append(this_para)
    logger.info('='*10+'Starting Batch Evaluation'+'='*10)
    batch_everything(all_params, atom_eval)


def atom_eval(params):
    Evaluation.output_all_evaluations(*params)


def batch_output_effectiveness(collection_yaml, models_yaml, output_root):
    all_params = []
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    if not os.path.exists(this_output_root):
        os.makedirs(this_output_root)
    all_params.extend( Effectiveness(index_path).gen_output_effectiveness_params(this_output_root) )
    logger.info('='*10+'Starting Output Effectiveness'+'='*10)
    batch_everything(all_params, atom_output_effectiveness)


def atom_output_effectiveness(para):
    index_path = para[0]
    output_fn = para[1]
    input_fns = para[2:]
    Effectiveness(index_path).output_effectiveness(output_fn, input_fns)


def print_optimal_effectiveness(collection_yaml, models_yaml, output_root, metrics=['map']):
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    logger.info('='*30+'JDIQ2018 Effectiveness for '+collection_yaml['name']+'='*30)
    effectiveness = Effectiveness(index_path).load_optimal_effectiveness(this_output_root, metrics)
    success = True
    for e in effectiveness:
        expected = models_yaml[e['model']]['expected'][collection_yaml['name']][e['metric']][e['topic']]
        if isclose(expected, e['actual']):
            logger.info(json.dumps(e, sort_keys=True))
        else:
            success = False
            logger.error('!'*5+'expected:%f'%expected+json.dumps(e, sort_keys=True)+'!'*5)
    if success:
        logger.info("All Tests Passed!")


def del_method_related_files(method_name):
    folders = ['split_results', 'merged_results', 'evals', 'effectiveness']
    for q in g.query:
        collection_name = q['collection']
        index_name = c['index']
        collection_path = os.path.join(_root, index_name)
        for f in folders:
            if os.path.exists( os.path.join(collection_path, f) ):
                logger.info('Deleting ' + os.path.join(collection_path, f) + ' *' + method_name + '*')
                if f == 'split_results' or f == 'merged_results':
                    subprocess.call('find %s -name "*method:%s*" -exec rm -rf {} \\;' %
                                    (os.path.join(collection_path, f), method_name), shell=True)
                else:
                    subprocess.call('find %s -name "*%s*" -exec rm -rf {} \\;' %
                                    (os.path.join(collection_path, f), method_name), shell=True)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # general settings
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument('--collection', required=True, help='the collection key in yaml')
    parser.add_argument('--n', dest='parallelism', type=int, default=16,
                        help='number of parallel threads for retrieval/eval')
    parser.add_argument('--output_root', default='runs.jdiq2018', help='output directory of all results')

    # runtime
    parser.add_argument(
        "--del_method_related_files",
        nargs=1,
        help="Delete all the output files of a method."
    )
    parser.add_argument(
        "--metrics",
        nargs='+',
        default=['map'],
        help="inputs: [metrics]. For example, --metrics map ndcg20"
    )

    args = parser.parse_args()

    if not os.path.exists(args.output_root):
        os.makedirs(args.output_root)

    if args.del_method_related_files:
        del_method_related_files(args.del_method_related_files[0])
    else:
        parallelism = args.parallelism
        with open(os.path.join(args.anserini_root,
                               'src/main/resources/regression/{}.yaml'.format(args.collection))) as f:
            collection_yaml = yaml.safe_load(f)
        with open(os.path.join(args.anserini_root, 'src/main/resources/jdiq2018/models.yaml')) as f:
            models_yaml = yaml.safe_load(f)['models']
        collection_yaml['anserini_root'] = args.anserini_root
        batch_retrieval(collection_yaml, models_yaml, args.output_root)
        batch_eval(collection_yaml, models_yaml, args.output_root)
        batch_output_effectiveness(collection_yaml, models_yaml, args.output_root)
        print_optimal_effectiveness(collection_yaml, models_yaml, args.output_root, args.metrics)
