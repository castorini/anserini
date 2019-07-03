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
from xfold import XFoldValidate

logger = logging.getLogger('fine_tuning')
logger.setLevel(logging.INFO)
# create console handler with a higher log level
#ch = logging.StreamHandler()
#ch.setLevel(logging.INFO)
#formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
#ch.setFormatter(formatter)
# add the handlers to the logger
#logger.addHandler(ch)

parallelism=1
def batch_everything(all_params, func):
    if len(all_params) == 0:
        return
    p = Pool(min(parallelism, len(all_params)))
    p.map(func, all_params)


# As long as two numbers match to the four decimal place, we're good
def is_close(a, b):
    return abs(round(a, 4) - round(b, 4)) <= 1e-05


def get_index_path(yaml_data):
    """
    Find the possible index path
    """
    for index_root in yaml_data['index_roots']:
        if os.path.exists(os.path.join(index_root, yaml_data['index_path'])):
            index_path = os.path.join(index_root, yaml_data['index_path'])
            break
    return index_path


def batch_retrieval(collection_yaml, models_yaml, output_root):
    all_params = []
    program = os.path.join(collection_yaml['anserini_root'], 'target/appassembler/bin', 'SearchCollection')
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    logger.info('='*10+'Generating Batch Retrieval Parameters'+'='*10)
    model_params = Search(index_path).gen_batch_retrieval_params(models_yaml, this_output_root, parallelism)
    for para in model_params:
        this_para = (
            program,
            '-searchtweets' if 'mb' in collection_yaml['name'] else '',
            '-topicreader', collection_yaml['topic_reader'],
            '-index', index_path,
            '-topics', os.path.join(collection_yaml['anserini_root'], collection_yaml['topic_root'], collection_yaml['topic']),
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
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    for eval in collection_yaml['evals']:
        eval_params = Evaluation(index_path).gen_batch_eval_params(this_output_root, eval['metric'])
        for param in eval_params:
            run_file_path, eval_output = param
            this_para = (
                [os.path.join(collection_yaml['anserini_root'], eval['command']+' '+eval['params'])],
                os.path.join(collection_yaml['anserini_root'], collection_yaml['qrels_root'], collection_yaml['qrel']),
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
    all_params.extend( Effectiveness(index_path).gen_output_effectiveness_params(this_output_root) )
    logger.info('='*10+'Starting Output Effectiveness'+'='*10)
    batch_everything(all_params, atom_output_effectiveness)


def atom_output_effectiveness(para):
    index_path = para[0]
    output_fn = para[1]
    input_fns = para[2:]
    Effectiveness(index_path).output_effectiveness(output_fn, input_fns)


# How to print colored text in terminal in Python?
# https://stackoverflow.com/questions/287871/how-to-print-colored-text-in-terminal-in-python

def verify_effectiveness(collection_yaml, models_yaml, output_root, fold_settings, verbose):
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    effectiveness, per_topic_oracle = Effectiveness(index_path).load_optimal_effectiveness(this_output_root)
    success_optimal = True

    for e in effectiveness:
        if e['metric'] not in models_yaml['expected'][collection_yaml['name']]:
            continue
        expected = models_yaml['expected'][collection_yaml['name']][e['metric']]
        if is_close(expected['best_avg'], e['best_avg']['value']):
            logger.info(' best_avg          --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;42m[OK]\x1b[0m' % (e['model'], e['metric'], expected['best_avg'], e['best_avg']['value']))
        else:
            success_optimal = False
            logger.error('best_avg          --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;41m[ERROR]\x1b[0m' % (e['model'], e['metric'], expected['best_avg'], e['best_avg']['value']))
        if is_close(expected['oracles_per_topic'], e['oracles_per_topic']):
            logger.info(' oracles_per_topic --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;42m[OK]\x1b[0m' % (e['model'], e['metric'], expected['oracles_per_topic'], e['oracles_per_topic']))
        else:
            success_optimal = False
            logger.error('oracles_per_topic --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;41m[ERROR]\x1b[0m' % (e['model'], e['metric'], expected['oracles_per_topic'], e['oracles_per_topic']))

    if fold_settings == '':
        return

    success_xfold = True
    fold = -1

    logger.info('Checking fold settings: ' + fold_settings)

    fold_mapping = {}
    num_folds = 0
    with open(fold_settings) as json_file:
        raw_json_folds = json.load(json_file)
        for fold in raw_json_folds:
            for t in fold:
                fold_mapping[t] = num_folds
            num_folds = num_folds + 1

    logger.info('Number of folds: %d' % (num_folds))
    fold = num_folds

    x_fold_effectiveness = XFoldValidate(output_root, collection_yaml['name'], fold, fold_mapping).tune(verbose)

    for model in x_fold_effectiveness:
        if models_yaml['name'] != model:
            continue
        for metric in x_fold_effectiveness[model]:
            if metric not in models_yaml['expected'][collection_yaml['name']]:
                continue
            expected = models_yaml['expected'][collection_yaml['name']][metric]
            if is_close(expected['%d-fold' % fold], x_fold_effectiveness[model][metric]):
                logger.info(' xvalidation --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;42m[OK]\x1b[0m' % (model, metric, expected['%d-fold' % fold], x_fold_effectiveness[model][metric]))
            else:
                success_optimal = False
                logger.error('xvalidation --- model: %s, metric: %6s, expected: %.4f, actual: %.4f \x1b[6;30;41m[ERROR]\x1b[0m' % (model, metric, expected['%d-fold' % fold], x_fold_effectiveness[model][metric]))

    if success_optimal and success_xfold:
        logger.info('\x1b[6;30;42m[All Tests Passed!]\x1b[0m')
    else:
        logger.info('\x1b[6;30;41m[Tests Failures!]\x1b[0m')


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
                    subprocess.call('find %s -name "*method:%s*" -exec rm -rf {} \\;' % (os.path.join(collection_path, f), method_name), shell=True)
                else:
                    subprocess.call('find %s -name "*%s*" -exec rm -rf {} \\;' % (os.path.join(collection_path, f), method_name), shell=True)


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # general settings
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument('--run', action='store_true', help='Generate the runs files and evaluate them. Otherwise we only output the evaluation results (based on the existing eval files)')
    parser.add_argument('--collection', required=True, help='the collection key in yaml')
    parser.add_argument('--model', required=True, help='model')
    parser.add_argument('--threads', dest='parallelism', type=int, default=16, help='number of parallel threads for retrieval and evaluation')
    parser.add_argument('--output_root', default='fine_tuning_results', help='output directory of all results')
    parser.add_argument('--fold_settings', default='', help='JSON file holding fold definitions, see src/main/resources/fine_tuning/robust04-paper1-folds.json for an example')
    parser.add_argument('--verbose', action='store_true', help='if specified print out model parameters and per fold scores')

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

    if args.del_method_related_files:
        del_method_related_files(args.del_method_related_files[0])
    else:
        parallelism = args.parallelism
        with open(os.path.join(args.anserini_root, 'src/main/resources/fine_tuning/collections.yaml')) as f:
            collections_yaml = yaml.safe_load(f)
        with open(os.path.join(args.anserini_root, 'src/main/resources/fine_tuning/models.yaml')) as f:
            models_yaml = yaml.safe_load(f)['models'][args.model]
        collection_yaml = collections_yaml['collections'][args.collection]
        for k in collections_yaml:
            if k != 'collections':
                collection_yaml[k] = collections_yaml[k]
        collection_yaml['anserini_root'] = args.anserini_root
        if not os.path.exists(os.path.join(args.output_root, collection_yaml['name'])):
            os.makedirs(os.path.join(args.output_root, collection_yaml['name']))

        if args.run:
            batch_retrieval(collection_yaml, models_yaml, args.output_root)
            batch_eval(collection_yaml, models_yaml, args.output_root)
            batch_output_effectiveness(collection_yaml, models_yaml, args.output_root)
        verify_effectiveness(collection_yaml, models_yaml, args.output_root, args.fold_settings, args.verbose)
