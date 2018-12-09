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
    p = Pool(min(parallelism, len(all_params)))
    p.map(func, all_params)

def isclose(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)

def get_index_path(yaml_data):
    """
    Find the possible index path
    """
    for index_root in yaml_data['index_roots']:
        if os.path.exists(os.path.join(index_root, yaml_data['index_path'])):
            index_path = os.path.join(index_root, yaml_data['index_path'])
            break
    return index_path

def load_drr_fold_mapping(fold_dir):
    # load the qid: fold_id mapping of the 5-folds used 
    # in the deep relevance ranking paper
    # (https://github.com/nlpaueb/deep-relevance-ranking)
    fold_mapping = {}
    for fold_id in xrange(5):
        fold_fn = os.path.join(fold_dir,'rob04.test.s{}.json'.format(fold_id+1))
        try:
            fold_info = json.load(open(fold_fn))
        except IOError:
            logger.error("Error when parsing fold file: {}\n".format(fold_fn))
            return None
        else:
            for q_info in fold_info['questions']:
                qid = q_info['id']
                fold_mapping[qid] = fold_id
    return fold_mapping


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
            '-{}'.format(models_yaml['basemodel']),
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

def verify_effectiveness(collection_yaml, models_yaml, output_root, use_drr_fold):
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    effectiveness, per_topic_oracle = Effectiveness(index_path).load_optimal_effectiveness(this_output_root)
    success_optimal = True
    for e in effectiveness:
        if e['basemodel'] != models_yaml['basemodel'] or e['model'] != models_yaml['name'] or e['metric'] not in models_yaml['expected'][collection_yaml['name']]:
            continue
        expected = models_yaml['expected'][collection_yaml['name']][e['metric']]
        if isclose(expected['best_avg'], e['best_avg']['value']):
            logger.info(json.dumps(e, sort_keys=True))
        else:
            success_optimal = False
            logger.error('!'*5+'base model: %s model: %s metric: %s expected best: %f actual: %s ' % (e['basemodel'], e['model'], e['metric'], expected['best_avg'], e['best_avg']['value'])+'!'*5)
        if isclose(expected['oracles_per_topic'], e['oracles_per_topic']):
            logger.info(json.dumps(e, sort_keys=True))
        else:
            success_optimal = False
            logger.error('!'*5+'base model: %s model: %s metric: %s oracles_per_topic: %f actual: %s ' % (e['basemodel'], e['model'], e['metric'], expected['oracles_per_topic'], e['oracles_per_topic'])+'!'*5)

    success_xfold = True
    for fold in [2, 5]:
        if collection_yaml['name'] == 'robust04' and fold == 5 and use_drr_fold:
            fold_dir = os.path.join(collection_yaml['anserini_root'], 'src/main/resources/fine_tuning/drr_folds')
            fold_mapping = load_drr_fold_mapping(fold_dir)
            x_fold_effectiveness = XFoldValidate(output_root, collection_yaml['name'], fold, fold_mapping).tune(False)
        else:
            x_fold_effectiveness = XFoldValidate(output_root, collection_yaml['name'], fold).tune(False)
        for basemodel in x_fold_effectiveness:
            if models_yaml['basemodel'] != basemodel:
                continue
            for model in x_fold_effectiveness[basemodel]:
                if models_yaml['name'] != model:
                    continue
                for metric in x_fold_effectiveness[basemodel][model]:
                    if metric not in models_yaml['expected'][collection_yaml['name']]:
                        continue
                    expected = models_yaml['expected'][collection_yaml['name']][metric]
                    if isclose(expected['%d-fold' % fold], x_fold_effectiveness[basemodel][model][metric]):
                        logger.info(json.dumps(x_fold_effectiveness[basemodel][model], sort_keys=True))
                    else:
                        success_optimal = False
                        logger.error('!'*5+'base model: %s model: %s fold: %d metric: %s expected: %f actual: %s ' % (basemodel, model, fold, metric, expected['%d-fold' % fold], x_fold_effectiveness[basemodel][model][metric])+'!'*5)

    if success_optimal and success_xfold:
        logger.info('[Regression Tests Passed] All Passed^^^')

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
    parser.add_argument('--basemodel', default='bm25', choices=['bm25', 'ql'], help='the ranking model')
    parser.add_argument('--model', default='axiom', choices=['bm25', 'ql', 'axiom', 'rm3', 'bm25+axiom', 'bm25+rm3'], help='the higher level model')
    parser.add_argument('--n', dest='parallelism', type=int, default=16, help='number of parallel threads for retrieval/eval')
    parser.add_argument('--output_root', default='fine_tuning_results', help='output directory of all results')
    parser.add_argument('--use_drr_fold', action='store_true', help='if specified, use the 5-folds from the deep-relevance-ranking paper')

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
        models_yaml['basemodel'] = args.basemodel

        if args.run:
            batch_retrieval(collection_yaml, models_yaml, args.output_root)
            batch_eval(collection_yaml, models_yaml, args.output_root)
            batch_output_effectiveness(collection_yaml, models_yaml, args.output_root)
        verify_effectiveness(collection_yaml, models_yaml, args.output_root, args.use_drr_fold)

