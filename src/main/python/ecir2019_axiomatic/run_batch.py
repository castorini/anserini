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
import subprocess
import argparse
from multiprocessing import Pool
import json
import logging

import yaml

from search import Search
from evaluation import Evaluation
from effectiveness import Effectiveness
from coverage import Coverage

logger = logging.getLogger('ecir2019_axiomatic')
logger.setLevel(logging.INFO)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
ch.setFormatter(formatter)
# add the handlers to the logger
#logger.addHandler(ch)

parallelism=1
def batch_everything(all_params, func):
    if len(all_params) == 0:
        return
    p = Pool(min(parallelism, len(all_params)))
    p.map(func, all_params)

def isclose(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)

def get_index_path(yaml_data):
    """Find the index path."""
    for index_root in yaml_data['index_roots']:
        if os.path.exists(os.path.join(index_root, yaml_data['index_path'])):
            index_path = os.path.join(index_root, yaml_data['index_path'])
            break
    return index_path

def batch_retrieval(collection_yaml, models_yaml, output_root, random = False, dry_run = False):
    all_params = []
    program = os.path.join(collection_yaml['anserini_root'], 'target/appassembler/bin', 'SearchCollection')
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    logger.info('='*10+'Generating Batch Retrieval Parameters'+'='*10)
    if random:
        if collection_yaml['name'] == 'disk12' or collection_yaml['name'] == 'robust04' or collection_yaml['name'] == 'robust05' or collection_yaml['name'] == 'core17':
            beta = 0.5
        elif collection_yaml['name'] == 'mb11' or collection_yaml['name'] == 'mb13':
            beta = 1.0
        else: # Web collections
            beta = 0.1
        model_params = Search(index_path).gen_random_batch_retrieval_params(models_yaml, this_output_root, beta, parallelism)
    else:
        model_params = Search(index_path).gen_batch_retrieval_params(models_yaml, this_output_root, parallelism)
    for para in model_params:
        this_para = (
            program,
            '-searchtweets' if 'mb' in collection_yaml['name'] else '',
            '-topicreader', collection_yaml['topic_reader'],
            '-index', index_path,
            '-topics', ' '.join([os.path.join(collection_yaml['anserini_root'], collection_yaml['topic_root'], topic) for topic in collection_yaml['topics']]),
            '-{}'.format(para[0]),
            para[1],
            '-output', para[2]
        )
        all_params.append(this_para)
    logger.info('='*10+'Starting Batch Retrieval'+'='*10)
    if dry_run:
        for params in all_params:
            logger.info(' '.join(params))
    else:
        batch_everything(all_params, atom_retrieval)

def atom_retrieval(para):
    subprocess.call(' '.join(para), shell=True)

def batch_eval(collection_yaml, models_yaml, output_root, dry_run = False):
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
    if dry_run:
        for params in all_params:
            logger.info(params)
    else:
        batch_everything(all_params, atom_eval)

def atom_eval(params):
    Evaluation.output_all_evaluations(*params)

def batch_output_effectiveness(collection_yaml, models_yaml, output_root, random = False):
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    logger.info('='*10+'Starting Output Effectiveness'+'='*10)
    Effectiveness(index_path).output_effectiveness(this_output_root, random)

def plot(collection_yaml, output_root, metrics, random = False):
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    if random:
        if collection_yaml['name'] == 'disk12' or collection_yaml['name'] == 'robust04' or collection_yaml['name'] == 'robust05' or collection_yaml['name'] == 'core17':
            beta = 0.5
        elif collection_yaml['name'] == 'mb11' or collection_yaml['name'] == 'mb13':
            beta = 1.0
        else: # Web collections
            beta = 0.1
        Plots().plot_random_seeds(collection_yaml['name'], this_output_root, beta, metrics)
    else:
        Plots().plot_params_sensitivity(collection_yaml['name'], this_output_root, metrics)

def cal_coverage(collection_yaml, model_yaml, output_root):
    index_path = get_index_path(collection_yaml)
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    Coverage(index_path).cal_coverage(
        model_yaml,
        os.path.join(collection_yaml['anserini_root'], collection_yaml['qrels_root'], collection_yaml['qrel']),
        this_output_root
    )

def plot_coverage(collection_yaml, output_root):
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    Plots().plot_coverage(collection_yaml['name'], this_output_root)


def plot_per_topic_analysis(collection_yaml, output_root):
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    Plots().plot_per_topic_analysis(collection_yaml['name'], this_output_root)

def concatenate_qrels():
    filenames = ['src/main/resources/topics-and-qrels/qrels.51-100.txt',
                 'src/main/resources/topics-and-qrels/qrels.101-150.txt',
                 'src/main/resources/topics-and-qrels/qrels.151-200.txt']
    with open('src/main/resources/topics-and-qrels/qrels.disk12.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())

    filenames = ['src/main/resources/topics-and-qrels/qrels.701-750.txt',
                 'src/main/resources/topics-and-qrels/qrels.751-800.txt',
                 'src/main/resources/topics-and-qrels/qrels.801-850.txt']
    with open('src/main/resources/topics-and-qrels/qrels.gov2.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())

    filenames = ['src/main/resources/topics-and-qrels/qrels.web.51-100.txt',
                 'src/main/resources/topics-and-qrels/qrels.web.101-150.txt',
                 'src/main/resources/topics-and-qrels/qrels.web.151-200.txt']
    with open('src/main/resources/topics-and-qrels/qrels.cw09.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())

    filenames = ['src/main/resources/topics-and-qrels/qrels.web.201-250.txt',
                 'src/main/resources/topics-and-qrels/qrels.web.251-300.txt']
    with open('src/main/resources/topics-and-qrels/qrels.cw12.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())

    filenames = ['src/main/resources/topics-and-qrels/qrels.microblog2011.txt',
                 'src/main/resources/topics-and-qrels/qrels.microblog2012.txt']
    with open('src/main/resources/topics-and-qrels/qrels.mb11.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())

    filenames = ['src/main/resources/topics-and-qrels/qrels.microblog2013.txt',
                 'src/main/resources/topics-and-qrels/qrels.microblog2014.txt']
    with open('src/main/resources/topics-and-qrels/qrels.mb13.all.txt', 'w') as outfile:
        for fname in filenames:
            with open(fname) as infile:
                outfile.write(infile.read())


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # general settings
    parser.add_argument('--anserini_root', default='', help='Anserini path')
    parser.add_argument('--run', action='store_true', help='Generate the runs files and evaluate them. Otherwise we only output the evaluation results (based on the existing eval files)')
    parser.add_argument('--random', action='store_true', help='Generate the random seed runs files and evaluate them. Otherwise we only output the evaluation results (based on the existing eval files)')
    parser.add_argument('--plot', action='store_true', help='Plot the parameters sensitivity from performances CSV file')
    parser.add_argument('--collection', required=True, help='the collection key in yaml')
    parser.add_argument('--models', nargs='+', default='bm25', help='the list of base ranking models, choose from [bm25, ql, f2exp] (any ones or all of them)')
    parser.add_argument('--n', dest='parallelism', type=int, default=16, help='number of parallel threads for retrieval/eval')
    parser.add_argument('--output_root', default='ecir2019_axiomatic', help='output directory of all results')
    parser.add_argument('--dry_run', action='store_true', help='dry run the commands without actually running them')
    parser.add_argument('--cal_coverage', action='store_true', help='calculate the qrels coverage')
    parser.add_argument('--per_topic_analysis', action='store_true', help='plot the per-topic analysis figures')

    # runtime
    parser.add_argument(
        "--metrics",
        nargs='+',
        default=['map'],
        help="inputs: [metrics]. For example, --metrics map ndcg20"
    )

    args = parser.parse_args()

    # concatenate qrels together for easier evaluation
    concatenate_qrels()

    parallelism = args.parallelism
    resources_root = 'src/main/resources/ecir2019_axiomatic/'
    with open(os.path.join(args.anserini_root, resources_root, 'collections.yaml')) as f:
        collections_yaml = yaml.safe_load(f)
    with open(os.path.join(args.anserini_root, resources_root, 'models.yaml')) as f:
        models_yaml = yaml.safe_load(f)
    collection_yaml = collections_yaml['collections'][args.collection]
    for k in collections_yaml:
        if k != 'collections':
            collection_yaml[k] = collections_yaml[k]
    collection_yaml['anserini_root'] = args.anserini_root
    if not os.path.exists(os.path.join(args.output_root, collection_yaml['name'])):
        os.makedirs(os.path.join(args.output_root, collection_yaml['name']))
    models_yaml['models'] = args.models

    if args.run:
        batch_retrieval(collection_yaml, models_yaml, args.output_root, args.random, args.dry_run)
        batch_eval(collection_yaml, models_yaml, args.output_root, args.dry_run)
        batch_output_effectiveness(collection_yaml, models_yaml, args.output_root, args.random)

    if args.cal_coverage:
        cal_coverage(collection_yaml, models_yaml, args.output_root)

    if args.plot:
        from plots import Plots
        if args.cal_coverage:
            plot_coverage(collection_yaml, args.output_root, args.metrics)
        else:
            plot(collection_yaml, args.output_root, args.metrics, args.random)

    if args.per_topic_analysis:
        from plots import Plots
        plot_per_topic_analysis(collection_yaml, args.output_root)
