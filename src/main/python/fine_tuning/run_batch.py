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

import argparse
import json
import logging
import os
import subprocess
from multiprocessing import Pool
from urllib.request import urlretrieve

import yaml
from effectiveness import Effectiveness
from evaluation import Evaluation
from search import Search
from xfold import XFoldValidate

logger = logging.getLogger('fine_tuning')
logger.setLevel(logging.INFO)

GDEVAL_COMMIT = 'd9a27f26089a6019cef1788bdc16f77d8ec0a474'
GDEVAL_URL = f'https://raw.githubusercontent.com/castorini/eval/{GDEVAL_COMMIT}/eval/gdeval.pl'


def batch_everything(all_params, func):
    if len(all_params) == 0:
        return
    p = Pool(min(parallelism, len(all_params)))
    p.map(func, all_params)


# As long as two numbers match to the four decimal place, we're good
def is_close(a, b):
    return abs(round(a, 4) - round(b, 4)) <= 1e-05


def batch_retrieval(collection_yaml, models_yaml, output_root):
    all_params = []
    program = 'bin/run.sh io.anserini.search.SearchCollection'
    index = collection_yaml['index']
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    logger.info(f'{"=" * 10}Generating Batch Retrieval Parameters{"=" * 10}')
    model_params = Search().gen_batch_retrieval_params(models_yaml, this_output_root, parallelism)
    for para in model_params:
        this_para = (
            program,
            '-index', index,
            '-topics', collection_yaml['topic'],
            para[0],
            '-output', para[1]
        )
        all_params.append(this_para)
    logger.info(f'{"=" * 10}Starting Batch Retrieval{"=" * 10}')
    batch_everything(all_params, atom_retrieval)


def atom_retrieval(para):
    subprocess.run(' '.join(para), shell=True, check=True)


def ensure_gdeval_available():
    gdeval_path = 'bin/gdeval.pl'
    if os.path.exists(gdeval_path):
        return

    temporary_path = f'{gdeval_path}.tmp'
    try:
        logger.info(f'Downloading {GDEVAL_URL} to {gdeval_path}')
        urlretrieve(GDEVAL_URL, temporary_path)
        os.chmod(temporary_path, 0o755)
        os.replace(temporary_path, gdeval_path)
    finally:
        if os.path.exists(temporary_path):
            os.remove(temporary_path)


def resolve_qrels_path(qrels):
    result = subprocess.run(
        ['bin/run.sh', 'io.anserini.cli.QrelsRegistry', '--metadata', qrels],
        check=True,
        capture_output=True,
        text=True
    )
    return json.loads(result.stdout)['local_path']


def batch_eval(collection_yaml, output_root):
    all_params = []
    this_output_root = os.path.join(output_root, collection_yaml['name'])

    for eval in collection_yaml['evals']:
        qrels = collection_yaml['qrel']
        if 'gdeval' in eval['command']:
            ensure_gdeval_available()
            qrels = resolve_qrels_path(qrels)
        eval_params = Evaluation().gen_batch_eval_params(this_output_root, eval['metric'])
        for param in eval_params:
            run_file_path, eval_output = param
            this_para = (
                [f'{eval["command"]} {eval["params"]}'],
                qrels,
                f'\'{run_file_path}\'',  # Make sure the filename is quoted
                eval_output
            )
            all_params.append(this_para)

    logger.info(f'{"=" * 10}Starting Batch Evaluation{"=" * 10}')
    batch_everything(all_params, atom_eval)


def atom_eval(params):
    Evaluation.output_all_evaluations(*params)


def batch_output_effectiveness(collection_yaml, output_root):
    all_params = []
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    all_params.extend(Effectiveness().gen_output_effectiveness_params(this_output_root))
    logger.info(f'{"=" * 10}Starting Output Effectiveness{"=" * 10}')
    batch_everything(all_params, atom_output_effectiveness)


def atom_output_effectiveness(para):
    output_fn = para[0]
    input_fns = para[1:]
    Effectiveness().output_effectiveness(output_fn, input_fns)


# How to print colored text in terminal in Python?
# https://stackoverflow.com/questions/287871/how-to-print-colored-text-in-terminal-in-python

def verify_effectiveness(collection_yaml, models_yaml, output_root, folds_setting, verbose):
    this_output_root = os.path.join(output_root, collection_yaml['name'])
    effectiveness, _per_topic_oracle = Effectiveness().load_optimal_effectiveness(this_output_root)
    success_optimal = True

    for e in effectiveness:
        if e['metric'] not in models_yaml['expected'][collection_yaml['name']]:
            continue
        expected = models_yaml['expected'][collection_yaml['name']][e['metric']]
        if is_close(expected['best_avg'], e['best_avg']['value']):
            logger.info(f' best_avg          --- model: {e["model"]}, metric: {e["metric"]:>6}, expected: {expected["best_avg"]:.4f}, actual: {e["best_avg"]["value"]:.4f} \x1b[6;30;42m[OK]\x1b[0m')
        else:
            success_optimal = False
            logger.error(f'best_avg          --- model: {e["model"]}, metric: {e["metric"]:>6}, expected: {expected["best_avg"]:.4f}, actual: {e["best_avg"]["value"]:.4f} \x1b[6;30;41m[ERROR]\x1b[0m')
        if is_close(expected['oracles_per_topic'], e['oracles_per_topic']):
            logger.info(f' oracles_per_topic --- model: {e["model"]}, metric: {e["metric"]:>6}, expected: {expected["oracles_per_topic"]:.4f}, actual: {e["oracles_per_topic"]:.4f} \x1b[6;30;42m[OK]\x1b[0m')
        else:
            success_optimal = False
            logger.error(f'oracles_per_topic --- model: {e["model"]}, metric: {e["metric"]:>6}, expected: {expected["oracles_per_topic"]:.4f}, actual: {e["oracles_per_topic"]:.4f} \x1b[6;30;41m[ERROR]\x1b[0m')

    if folds_setting == '':
        return

    success_xfold = True

    logger.info(f'Checking fold settings: {folds_setting}')

    fold_mapping = {}
    num_folds = 0
    with open(folds_setting) as json_file:
        raw_json_folds = json.load(json_file)
        for fold in raw_json_folds:
            for t in fold:
                fold_mapping[t] = num_folds
            num_folds = num_folds + 1

    logger.info(f'Number of folds: {num_folds}')

    fold = num_folds
    fold_key = f'{fold}-fold'
    x_fold_effectiveness = XFoldValidate(output_root, collection_yaml['name'], fold, fold_mapping).tune(verbose)

    for model in x_fold_effectiveness:
        if models_yaml['name'] != model:
            continue
        for metric in x_fold_effectiveness[model]:
            if metric not in models_yaml['expected'][collection_yaml['name']]:
                continue
            expected = models_yaml['expected'][collection_yaml['name']][metric]
            if is_close(expected[fold_key], x_fold_effectiveness[model][metric]):
                logger.info(f' xvalidation --- model: {model}, metric: {metric:>6}, expected: {expected[fold_key]:.4f}, actual: {x_fold_effectiveness[model][metric]:.4f} \x1b[6;30;42m[OK]\x1b[0m')
            else:
                success_optimal = False
                logger.error(f'xvalidation --- model: {model}, metric: {metric:>6}, expected: {expected[fold_key]:.4f}, actual: {x_fold_effectiveness[model][metric]:.4f} \x1b[6;30;41m[ERROR]\x1b[0m')

    if success_optimal and success_xfold:
        logger.info('\x1b[6;30;42m[All Tests Passed!]\x1b[0m')
    else:
        logger.info('\x1b[6;30;41m[Tests Failures!]\x1b[0m')


if __name__ == '__main__':
    parser = argparse.ArgumentParser()

    # general settings
    parser.add_argument('--run', action='store_true', help='Generate the runs files and evaluate them. Otherwise we only output the evaluation results (based on the existing eval files)')
    parser.add_argument('--collection', required=True, help='the collection key in yaml')
    parser.add_argument('--model', required=True, help='model')
    parser.add_argument('--parallelism', dest='parallelism', type=int, default=8, help='number of parallel threads for retrieval and evaluation')
    parser.add_argument('--output-root', default='fine_tuning_results', help='output directory of all results')
    parser.add_argument('--folds-setting', default='', help='JSON file holding fold definitions, see src/main/resources/fine_tuning/robust04-paper1-folds.json for an example')
    parser.add_argument('--verbose', action='store_true', help='if specified print out model parameters and per fold scores')
    args = parser.parse_args()

    parallelism = args.parallelism
    with open('src/main/resources/fine_tuning/collections.yaml') as f:
        collections_yaml = yaml.safe_load(f)
    collection_yaml = collections_yaml['collections'][args.collection]

    with open('src/main/resources/fine_tuning/models.yaml') as f:
        models_yaml = yaml.safe_load(f)['models'][args.model]

    if not os.path.exists(os.path.join(args.output_root, collection_yaml['name'])):
        os.makedirs(os.path.join(args.output_root, collection_yaml['name']))

    if args.run:
        batch_retrieval(collection_yaml, models_yaml, args.output_root)
        batch_eval(collection_yaml, args.output_root)
        batch_output_effectiveness(collection_yaml, args.output_root)

    verify_effectiveness(collection_yaml, models_yaml, args.output_root, args.folds_setting, args.verbose)
