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

from __future__ import print_function

import argparse
import hashlib
import itertools
import logging
import os
import re
import stat
import tarfile
import time
import yaml
from collections import defaultdict
from multiprocessing import Pool
from subprocess import call, Popen, PIPE
from tqdm import tqdm
from urllib.request import urlretrieve

logger = logging.getLogger('regression_test')
logger.setLevel(logging.INFO)
# create console handler with a higher log level
ch = logging.StreamHandler()
ch.setLevel(logging.INFO)
formatter = logging.Formatter('%(asctime)s %(levelname)s  [python] %(message)s')
ch.setFormatter(formatter)
# add the handlers to the logger
logger.addHandler(ch)

# These are the locations where corpora can be found on specific machines.
# There is no need to specify them on a per-file basis.
CORPUS_ROOTS = [
    '',                           # here, stored in this directory
    '/collection/',               # on hops
    '/',                          # on hops (alternate)
    '/mnt/',                      # on tjena
    '/tuna1/',                    # on tuna
    '/store/',                    # on orca
    '/u4/jimmylin/',              # on linux.cs
    '/System/Volumes/Data/store'  # for new organization of directories in macOS Monterey
]

INDEX_COMMAND = 'bin/run.sh io.anserini.index.IndexCollection'
INDEX_FLAT_DENSE_COMMAND = 'bin/run.sh io.anserini.index.IndexFlatDenseVectors'
INDEX_HNSW_DENSE_COMMAND = 'bin/run.sh io.anserini.index.IndexHnswDenseVectors'
INDEX_INVERTED_DENSE_COMMAND = 'bin/run.sh io.anserini.index.IndexInvertedDenseVectors'

INDEX_STATS_COMMAND = 'bin/run.sh io.anserini.index.IndexReaderUtils'

SEARCH_COMMAND = 'bin/run.sh io.anserini.search.SearchCollection'
SEARCH_FLAT_DENSE_COMMAND = 'bin/run.sh io.anserini.search.SearchFlatDenseVectors'
SEARCH_HNSW_DENSE_COMMAND = 'bin/run.sh io.anserini.search.SearchHnswDenseVectors'
SEARCH_INVERTED_DENSE_COMMAND = 'bin/run.sh io.anserini.search.SearchInvertedDenseVectors'


def is_close(a, b, rel_tol=1e-09, abs_tol=0.0):
    return abs(a-b) <= max(rel_tol * max(abs(a), abs(b)), abs_tol)


def is_close_lucene8(a, b):
    return abs(a-b) <= 0.001


def check_output(command):
    # Python 2.6 compatible subprocess.check_output
    process = Popen(command, shell=True, stdout=PIPE)
    output, err = process.communicate()
    if process.returncode == 0: # success
        return output
    else:
        raise RuntimeError("Command {0} running unsuccessfully".format(command))


def construct_index_path(yaml_data):
    index_path = yaml_data['index_path']
    if not index_path or not os.path.exists(index_path):
        for input_root in CORPUS_ROOTS:
            index_path = os.path.join(input_root, yaml_data['index_path'])
            if os.path.exists(index_path):
                break
    return index_path


def construct_indexing_command(yaml_data, args):
    corpus_path = None
    if args.corpus_path:
        if os.path.exists(args.corpus_path):
            corpus_path = args.corpus_path
    else:
        for input_root in CORPUS_ROOTS:
            test_path = os.path.join(input_root, yaml_data['corpus_path'])
            if os.path.exists(test_path):
                corpus_path = test_path
                break

    if not corpus_path:
        raise RuntimeError("Unable to find the corpus!")

    # Determine the number of indexing threads, either from the command line,
    # or reading the YAML config.
    if args.index_threads != -1:
        threads = args.index_threads
    else:
        threads = yaml_data['index_threads']

    if not os.path.exists('indexes'):
        os.makedirs('indexes')

    if yaml_data.get('index_type') == 'inverted-dense':
        root_cmd = INDEX_INVERTED_DENSE_COMMAND
    elif yaml_data.get('index_type') == 'hnsw':
        root_cmd = INDEX_HNSW_DENSE_COMMAND
    elif yaml_data.get('index_type') == 'flat':
        root_cmd = INDEX_FLAT_DENSE_COMMAND
    else:
        root_cmd = INDEX_COMMAND

    index_command = [
        root_cmd,
        '-collection', yaml_data['collection_class'],
        '-input', corpus_path,
        '-generator', yaml_data['generator_class'],
        '-index', yaml_data['index_path'],
        '-threads', str(threads),
        yaml_data['index_options']
    ]

    return index_command


def construct_runfile_path(index, id, model_name):
    # If the index is 'indexes/lucene-inverted.msmarco-passage-ca/', we pull out 'msmarco-passage-ca'.
    #  'indexes/lucene-hnsw-int8.msmarco-v1-passage.cos-dpr-distil/' -> 'hnsw-int8.msmarco-v1-passage.cos-dpr-distil'
    #  'indexes/lucene-hnsw.msmarco-v1-passage.cos-dpr-distil/' -> 'hnsw.msmarco-v1-passage.cos-dpr-distil/'
    # Be careful, for 'indexes/lucene-inverted.mrtydi-v1.1-arabic/', we want to pull out 'inverted-mrtydi-v1.1-arabic'.
    index_part = index.split('/')[1].split('-', 1)[1]
    return os.path.join('runs/', 'run.{0}.{1}.{2}'.format(index_part, id, model_name))


def construct_search_commands(yaml_data):
    ranking_commands = [
        [
            SEARCH_INVERTED_DENSE_COMMAND if model.get('type') == 'inverted-dense' else SEARCH_HNSW_DENSE_COMMAND if model.get('type') == 'hnsw' else SEARCH_FLAT_DENSE_COMMAND if model.get('type') == 'flat' else SEARCH_COMMAND,
            '-index', construct_index_path(yaml_data),
            '-topics', os.path.join('tools/topics-and-qrels', topic_set['path']),
            '-topicReader', topic_set['topic_reader'] if 'topic_reader' in topic_set and topic_set['topic_reader'] else yaml_data['topic_reader'],
            '-output', construct_runfile_path(yaml_data['index_path'], topic_set['id'], model['name']),
            model['params']
        ]
        for (model, topic_set) in list(itertools.product(yaml_data['models'], yaml_data['topics']))
    ]
    return ranking_commands


def construct_convert_commands(yaml_data):
    converting_commands = [
        [
            conversion['command'],
            '--index', construct_index_path(yaml_data),
            '--topics', topic_set['id'],
            '--input', construct_runfile_path(yaml_data['index_path'], topic_set['id'], model['name']) + conversion['in_file_ext'],
            '--output', construct_runfile_path(yaml_data['index_path'], topic_set['id'], model['name']) + conversion['out_file_ext'],
            conversion['params'] if 'params' in conversion and conversion['params'] else '',
            topic_set['convert_params'] if 'convert_params' in topic_set and topic_set['convert_params'] else '',
        ]
        for (model, topic_set, conversion) in list(itertools.product(yaml_data['models'], yaml_data['topics'], yaml_data['conversions']))
    ]
    return converting_commands


beir_flat_int8_onnx = defaultdict(lambda: 0.004)
beir_flat_int8_onnx['ArguAna'] = 0.03
beir_flat_int8_onnx['NFCorpus'] = 0.007
beir_flat_int8_onnx['Signal-1M'] = 0.006
beir_flat_int8_onnx['TREC-NEWS'] = 0.01
beir_flat_int8_onnx['Webis-Touche2020'] = 0.007

beir_flat_int8_cached = defaultdict(lambda: 0.004)
beir_flat_int8_cached['BioASQ'] = 0.005
beir_flat_int8_cached['NFCorpus'] = 0.006
beir_flat_int8_cached['Signal-1M'] = 0.007
beir_flat_int8_cached['TREC-NEWS'] = 0.01
beir_flat_int8_cached['Webis-Touche2020'] = 0.007

beir_flat_onnx = defaultdict(lambda: 0.001)
beir_flat_onnx['ArguAna'] = 0.02
beir_flat_onnx['CQADupStack-wordpress'] = 0.002
beir_flat_onnx['Quora'] = 0.002
beir_flat_onnx['Robust04'] = 0.004

beir_flat_cached = defaultdict(lambda: 1e-9)

beir_flat_tolerance = {
    'flat-int8-onnx': beir_flat_int8_onnx,
    'flat-int8-cached': beir_flat_int8_cached,
    'flat-onnx': beir_flat_onnx,
    'flat-cached': beir_flat_cached,
}

beir_hnsw_int8_onnx = defaultdict(lambda: 0.005)
beir_hnsw_int8_onnx['ArguAna'] = 0.03
beir_hnsw_int8_onnx['BioASQ'] = 0.02
beir_hnsw_int8_onnx['DBPedia'] = 0.007
beir_hnsw_int8_onnx['FiQA-2018'] = 0.007
beir_hnsw_int8_onnx['HotpotQA'] = 0.008
beir_hnsw_int8_onnx['NFCorpus'] = 0.006
beir_hnsw_int8_onnx['Robust04'] = 0.006
beir_hnsw_int8_onnx['Signal-1M'] = 0.04
beir_hnsw_int8_onnx['TREC-NEWS'] = 0.02
beir_hnsw_int8_onnx['Webis-Touche2020'] = 0.01

beir_hnsw_int8_cached = defaultdict(lambda: 0.005)
beir_hnsw_int8_cached['BioASQ'] = 0.02
beir_hnsw_int8_cached['FiQA-2018'] = 0.007
beir_hnsw_int8_cached['HotpotQA'] = 0.007
beir_hnsw_int8_cached['Signal-1M'] = 0.04
beir_hnsw_int8_cached['TREC-NEWS'] = 0.02
beir_hnsw_int8_cached['Webis-Touche2020'] = 0.006

beir_hnsw_onnx = defaultdict(lambda: 0.003)
beir_hnsw_onnx['ArguAna'] = 0.02
beir_hnsw_onnx['BioASQ'] = 0.01
beir_hnsw_onnx['CQADupStack-wordpress'] = 0.004
beir_hnsw_onnx['DBPedia'] = 0.006
beir_hnsw_onnx['FEVER'] = 0.007
beir_hnsw_onnx['FiQA-2018'] = 0.007
beir_hnsw_onnx['HotpotQA'] = 0.007
beir_hnsw_onnx['Robust04'] = 0.004
beir_hnsw_onnx['Signal-1M'] = 0.05
beir_hnsw_onnx['TREC-NEWS'] = 0.02

beir_hnsw_cached = defaultdict(lambda: 0.003)
beir_hnsw_cached['BioASQ'] = 0.01
beir_hnsw_cached['DBPedia'] = 0.006
beir_hnsw_cached['FEVER'] = 0.008
beir_hnsw_cached['FiQA-2018'] = 0.008
beir_hnsw_cached['HotpotQA'] = 0.007
beir_hnsw_cached['Signal-1M'] = 0.05
beir_hnsw_cached['TREC-NEWS'] = 0.025

beir_hnsw_tolerance = {
    'hnsw-int8-onnx': beir_hnsw_int8_onnx,
    'hnsw-int8-cached': beir_hnsw_int8_cached,
    'hnsw-onnx': beir_hnsw_onnx,
    'hnsw-cached': beir_hnsw_cached,
}

flat_model_type_pattern = re.compile(r'(flat-int8-onnx|flat-int8-cached|flat-onnx|flat-cached)$')
hnsw_model_type_pattern = re.compile(r'(hnsw-int8-onnx|hnsw-int8-cached|hnsw-onnx|hnsw-cached)$')

beir_dataset_pattern = re.compile(r'BEIR \(v1.0.0\): (.*)$')

msmarco_v1_flat_int8_onnx = defaultdict(lambda: 0.002)
msmarco_v1_flat_int8_cached = defaultdict(lambda: 0.002)
msmarco_v1_flat_int8_cached['openai-ada2-flat-int8-cached'] = 0.008
msmarco_v1_flat_onnx = defaultdict(lambda: 0.0001)
msmarco_v1_flat_cached = defaultdict(lambda: 1e-9)

msmarco_v1_flat_tolerance = {
    'flat-int8-onnx': msmarco_v1_flat_int8_onnx,
    'flat-int8-cached': msmarco_v1_flat_int8_cached,
    'flat-onnx': msmarco_v1_flat_onnx,
    'flat-cached': msmarco_v1_flat_cached,
}

dl19_flat_int8_onnx = defaultdict(lambda: 0.002)
dl19_flat_int8_onnx['bge-flat-int8-onnx'] = 0.008
dl19_flat_int8_cached = defaultdict(lambda: 0.002)
dl19_flat_int8_cached['bge-flat-int8-cached'] = 0.005
dl19_flat_int8_cached['openai-ada2-flat-int8-cached'] = 0.008
dl19_flat_onnx = defaultdict(lambda: 0.0001)
dl19_flat_onnx['bge-flat-onnx'] = 0.008
dl19_flat_cached = defaultdict(lambda: 1e-9)

dl19_flat_tolerance = {
    'flat-int8-onnx': dl19_flat_int8_onnx,
    'flat-int8-cached': dl19_flat_int8_cached,
    'flat-onnx': dl19_flat_onnx,
    'flat-cached': dl19_flat_cached,
}

dl20_flat_int8_onnx = defaultdict(lambda: 0.002)
dl20_flat_int8_onnx['bge-flat-int8-onnx'] = 0.004
dl20_flat_int8_onnx['cos-dpr-distil-flat-int8-onnx'] = 0.004
dl20_flat_int8_cached = defaultdict(lambda: 0.002)
dl20_flat_int8_cached['bge-flat-int8-cached'] = 0.005
dl20_flat_int8_cached['cos-dpr-distil-flat-int8-cached'] = 0.004
dl20_flat_int8_cached['cohere-embed-english-v3.0-flat-int8-cached'] = 0.004
dl20_flat_int8_cached['openai-ada2-flat-int8-cached'] = 0.003
dl20_flat_onnx = defaultdict(lambda: 0.0001)
dl20_flat_onnx['bge-flat-onnx'] = 0.005
dl20_flat_cached = defaultdict(lambda: 1e-9)

dl20_flat_tolerance = {
    'flat-int8-onnx': dl20_flat_int8_onnx,
    'flat-int8-cached': dl20_flat_int8_cached,
    'flat-onnx': dl20_flat_onnx,
    'flat-cached': dl20_flat_cached,
}

msmarco_v1_hnsw_int8_onnx = defaultdict(lambda: 0.01)
msmarco_v1_hnsw_int8_cached = defaultdict(lambda: 0.01)
msmarco_v1_hnsw_onnx = defaultdict(lambda: 0.01)
msmarco_v1_hnsw_onnx['cos-dpr-distil-hnsw-onnx']  = 0.015
msmarco_v1_hnsw_cached = defaultdict(lambda: 0.01)
msmarco_v1_hnsw_cached['cos-dpr-distil-hnsw-cached'] = 0.015

msmarco_v1_hnsw_tolerance = {
    'hnsw-int8-onnx': msmarco_v1_hnsw_int8_onnx,
    'hnsw-int8-cached': msmarco_v1_hnsw_int8_cached,
    'hnsw-onnx': msmarco_v1_hnsw_onnx,
    'hnsw-cached': msmarco_v1_hnsw_cached,
}

dl19_hnsw_int8_onnx = defaultdict(lambda: 0.01)
dl19_hnsw_int8_onnx['bge-hnsw-int8-onnx'] = 0.025
dl19_hnsw_int8_onnx['cos-dpr-distil-hnsw-int8-onnx'] = 0.025
dl19_hnsw_int8_cached = defaultdict(lambda: 0.01)
dl19_hnsw_int8_cached['bge-hnsw-int8-cached'] = 0.02
dl19_hnsw_int8_cached['cohere-embed-english-v3.0-hnsw-int8-cached'] = 0.02
dl19_hnsw_int8_cached['cos-dpr-distil-hnsw-int8-cached'] = 0.025
dl19_hnsw_int8_cached['openai-ada2-hnsw-int8-cached'] = 0.015
dl19_hnsw_onnx = defaultdict(lambda: 0.015)
dl19_hnsw_onnx['bge-hnsw-onnx'] = 0.02
dl19_hnsw_cached = defaultdict(lambda: 0.015)
dl19_hnsw_cached['cohere-embed-english-v3.0-hnsw-cached'] = 0.02

dl19_hnsw_tolerance = {
    'hnsw-int8-onnx': dl19_hnsw_int8_onnx,
    'hnsw-int8-cached': dl19_hnsw_int8_cached,
    'hnsw-onnx': dl19_hnsw_onnx,
    'hnsw-cached': dl19_hnsw_cached,
}

dl20_hnsw_int8_onnx = defaultdict(lambda: 0.02)
dl20_hnsw_int8_cached = defaultdict(lambda: 0.02)
dl20_hnsw_onnx = defaultdict(lambda: 0.015)
dl20_hnsw_cached = defaultdict(lambda: 0.015)
dl20_hnsw_cached['cohere-embed-english-v3.0-hnsw-cached'] = 0.025

dl20_hnsw_tolerance = {
    'hnsw-int8-onnx': dl20_hnsw_int8_onnx,
    'hnsw-int8-cached': dl20_hnsw_int8_cached,
    'hnsw-onnx': dl20_hnsw_onnx,
    'hnsw-cached': dl20_hnsw_cached,
}


def evaluate_and_verify(yaml_data, dry_run):
    fail_str = '\033[91m[FAIL]\033[0m '
    ok_str = '   [OK] '
    okish_str = '  \033[94m[OK*]\033[0m '
    failures = False
    okish = False

    logger.info('='*10 + ' Verifying Results: ' + yaml_data['corpus'] + ' ' + '='*10)
    for model in yaml_data['models']:
        for i, topic_set in enumerate(yaml_data['topics']):
            for metric in yaml_data['metrics']:
                eval_cmd = [
                  os.path.join(metric['command']), metric['params'] if 'params' in metric and metric['params'] else '',
                  os.path.join('tools/topics-and-qrels', topic_set['qrel']) if 'qrel' in topic_set and topic_set['qrel'] else '',
                  construct_runfile_path(yaml_data['index_path'], topic_set['id'], model['name']) + (yaml_data['conversions'][-1]['out_file_ext'] if 'conversions' in yaml_data and yaml_data['conversions'][-1]['out_file_ext'] else '')
                ]
                if dry_run:
                    logger.info(' '.join(eval_cmd))
                    continue

                out = [line for line in
                       check_output(' '.join(eval_cmd)).decode('utf-8').split('\n') if line.strip()][-1]
                if not out.strip():
                    continue
                eval_out = out.strip().split(metric['separator'])[metric['parse_index']]
                expected = round(model['results'][metric['metric']][i], metric['metric_precision'])
                actual = round(float(eval_out), metric['metric_precision'])

                using_hnsw = True if 'type' in model and model['type'] == 'hnsw' else False
                using_flat = True if 'type' in model and model['type'] == 'flat' else False

                if using_flat:
                    # Extract model
                    match = flat_model_type_pattern.search(model['name'])
                    model_type = match.group(1)

                    if 'BEIR' in topic_set['name']:
                        # Extract BEIR dataset
                        match = beir_dataset_pattern.search(topic_set['name'])
                        beir_dataset = match.group(1)

                        tolerance_ok = beir_flat_tolerance[model_type][beir_dataset]
                    elif 'MS MARCO Passage' in topic_set['name']:
                        tolerance_ok = msmarco_v1_flat_tolerance[model_type][model['name']]
                    elif 'DL19' in topic_set['name']:
                        tolerance_ok = dl19_flat_tolerance[model_type][model['name']]
                    elif using_flat and 'DL20' in topic_set['name']:
                        tolerance_ok = dl20_flat_tolerance[model_type][model['name']]

                if using_hnsw:
                    # Extract model
                    match = hnsw_model_type_pattern.search(model['name'])
                    model_type = match.group(1)

                    if 'BEIR' in topic_set['name']:
                        # Extract BEIR dataset
                        match = beir_dataset_pattern.search(topic_set['name'])
                        beir_dataset = match.group(1)

                        tolerance_ok = beir_hnsw_tolerance[model_type][beir_dataset]
                    elif 'MS MARCO Passage' in topic_set['name']:
                        tolerance_ok = msmarco_v1_hnsw_tolerance[model_type][model['name']]
                    elif 'DL19' in topic_set['name']:
                        tolerance_ok = dl19_hnsw_tolerance[model_type][model['name']]
                    elif 'DL20' in topic_set['name']:
                        tolerance_ok = dl20_hnsw_tolerance[model_type][model['name']]

                if using_flat or using_hnsw:
                    result_str = (f'expected: {expected:.4f} actual: {actual:.4f} '
                                  f'(delta={abs(expected-actual):.4f}, tolerance={abs(tolerance_ok):.4f}) - '
                                  f'metric: {metric["metric"]:<8} model: {model["name"]} topics: {topic_set["id"]}')
                else:
                    result_str = (f'expected: {expected:.4f} actual: {actual:.4f} (delta={abs(expected-actual):.4f}) - '
                                  f'metric: {metric["metric"]:<8} model: {model["name"]} topics: {topic_set["id"]}')

                # For flat and HNSW indexes:
                #   - to get "OK", we need to be within specified tolerance.
                #   - to get "OKish", we need to be within 150% of specified tolerance.
                if is_close(expected, actual) or actual > expected or \
                        (using_flat and is_close(expected, actual, abs_tol=tolerance_ok)) or \
                        (using_hnsw and is_close(expected, actual, abs_tol=tolerance_ok)):
                    logger.info(ok_str + result_str)
                elif (using_flat and is_close(expected, actual, abs_tol=tolerance_ok * 1.5)) or \
                        (using_hnsw and is_close(expected, actual, abs_tol=tolerance_ok * 1.5)):
                    logger.info(okish_str + result_str)
                    okish = True
                else:
                    if args.lucene8 and is_close_lucene8(expected, actual):
                        logger.info(okish_str + result_str)
                    else:
                        logger.error(fail_str + result_str)
                        failures = True

    end = time.time()

    if not dry_run:
        if failures:
            logger.error(f'{fail_str}Total elapsed time: {end - start:.0f}s')
        elif okish:
            logger.info(f'{okish_str}Total elapsed time: {end - start:.0f}s')
        else:
            logger.info(f'All Tests Passed! Total elapsed time: {end - start:.0f}s')


def run_search(cmd):
    logger.info(' '.join(cmd))
    call(' '.join(cmd), shell=True)


def run_convert(cmd):
    logger.info(' '.join(cmd))
    call(' '.join(cmd), shell=True)


# https://gist.github.com/leimao/37ff6e990b3226c2c9670a2cd1e4a6f5
class TqdmUpTo(tqdm):
    def update_to(self, b=1, bsize=1, tsize=None):
        """
        b  : int, optional
            Number of blocks transferred so far [default: 1].
        bsize  : int, optional
            Size of each block (in tqdm units) [default: 1].
        tsize  : int, optional
            Total size (in tqdm units). If [default: None] remains unchanged.
        """
        if tsize is not None:
            self.total = tsize
        self.update(b * bsize - self.n)  # will also set self.n = b * bsize


# For large files, we need to compute MD5 block by block. See:
# https://stackoverflow.com/questions/1131220/get-md5-hash-of-big-files-in-python
def compute_md5(file, block_size=2**20):
    m = hashlib.md5()
    with open(file, 'rb') as f:
        while True:
            buf = f.read(block_size)
            if not buf:
                break
            m.update(buf)
    return m.hexdigest()


def download_url(url, save_dir, local_filename=None, md5=None, force=False, verbose=True):
    # If caller does not specify local filename, figure it out from the download URL:
    if not local_filename:
        filename = url.split('/')[-1]
        filename = re.sub('\\?dl=1$', '', filename)  # Remove the Dropbox 'force download' parameter
    else:
        # Otherwise, use the specified local_filename:
        filename = local_filename

    destination_path = os.path.join(save_dir, filename)

    if verbose:
        logger.info(f'Downloading {url} to {destination_path}...')

    # Check to see if file already exists, if so, simply return (quietly) unless force=True, in which case we remove
    # destination file and download fresh copy.
    if os.path.exists(destination_path):
        if verbose:
            logger.info(f'{destination_path} already exists!')
        if not force:
            if verbose:
                logger.info(f'Skipping download.')
            return destination_path
        if verbose:
            logger.info(f'force=True, removing {destination_path}; fetching fresh copy...')
        os.remove(destination_path)

    with TqdmUpTo(unit='B', unit_scale=True, unit_divisor=1024, miniters=1, desc=filename) as t:
        urlretrieve(url, filename=destination_path, reporthook=t.update_to)

    if md5:
        md5_computed = compute_md5(destination_path)
        assert md5_computed == md5, f'{destination_path} does not match checksum! Expecting {md5} got {md5_computed}.'

    return destination_path


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Run Anserini regression tests.')
    parser.add_argument('--regression', required=True, help='Name of the regression test.')
    parser.add_argument('--corpus-path', dest='corpus_path', default='', help='Override corpus path from YAML')
    parser.add_argument('--download', dest='download', action='store_true', help='Build index.')
    parser.add_argument('--index', dest='index', action='store_true', help='Build index.')
    parser.add_argument('--index-threads', type=int, default=-1, help='Override number of indexing threads from YAML')
    parser.add_argument('--verify', dest='verify', action='store_true', help='Verify index statistics.')
    parser.add_argument('--search', dest='search', action='store_true', help='Search and verify results.')
    parser.add_argument('--search-pool', dest='search_pool', type=int, default=4,
                        help='Number of ranking runs to execute in parallel.')
    parser.add_argument('--convert-pool', dest='convert_pool', type=int, default=4,
                        help='Number of converting runs to execute in parallel.')
    parser.add_argument('--dry-run', dest='dry_run', action='store_true',
                        help='Output commands without actual execution.')
    parser.add_argument('--lucene8', dest='lucene8', action='store_true', help='Enable more lenient score matching for Lucene 8 index compatibility.')
    args = parser.parse_args()

    start = time.time()
    with open('src/main/resources/regression/{}.yaml'.format(args.regression)) as f:
        yaml_data = yaml.safe_load(f)

    if args.download:
        logger.info('='*10 + ' Downloading Corpus ' + '='*10)
        if not yaml_data['download_url']:
            raise ValueError('Corpus download URL known!')
        url = yaml_data['download_url']
        download_url(url, 'collections', md5=yaml_data['download_checksum'])

        filename = url.split('/')[-1]
        local_tarball = os.path.join('collections', filename)
        logger.info(f'Extracting {local_tarball}...')
        tarball = tarfile.open(local_tarball)
        tarball.extractall('collections')
        tarball.close()

        # e.g., MS MARCO V2: need to rename the corpus
        if 'download_corpus' in yaml_data:
            src = os.path.join('collections', yaml_data['download_corpus'])
            dest = os.path.join('collections', yaml_data['corpus'])
            logger.info(f'Renaming {src} to {dest}')
            os.chmod(src, stat.S_IRUSR | stat.S_IWUSR | stat.S_IXUSR)
            os.rename(src, dest)

        path = os.path.join('collections', yaml_data['corpus'])
        logger.info(f'Corpus path is {path}')
        args.corpus_path = path

    # Build indexes.
    if args.index:
        logger.info('='*10 + ' Indexing ' + '='*10)
        indexing_command = ' '.join(construct_indexing_command(yaml_data, args))
        logger.info(indexing_command)
        if not args.dry_run:
            call(indexing_command, shell=True)

    # Verify index statistics.
    if args.verify:
        logger.info('='*10 + ' Verifying Index ' + '='*10)
        if yaml_data.get('index_type') == 'hnsw':
            logger.info('Skipping verification step for HNSW dense indexes.')
        elif yaml_data.get('index_type') == 'flat':
            logger.info('Skipping verification step for flat dense indexes.')
        else:
            verification_command_args = [INDEX_STATS_COMMAND, '-index', construct_index_path(yaml_data), '-stats']
            if yaml_data.get('index_type') == 'inverted-dense':
                verification_command_args.extend(['-field', 'vector'])
            verification_command = ' '.join(verification_command_args)
            logger.info(verification_command)
            if not args.dry_run:
                out = check_output(verification_command).decode('utf-8').split('\n')
                for line in out:
                    stat = line.split(':')[0]
                    if stat in yaml_data['index_stats']:
                        value = int(line.split(':')[1])
                        if value != yaml_data['index_stats'][stat]:
                            print('{}: expected={}, actual={}'.format(stat, yaml_data['index_stats'][stat], value))
                        assert value == yaml_data['index_stats'][stat]
                        logger.info(line)
                logger.info('Index statistics successfully verified!')

    # Search and verify results.
    if args.search:
        logger.info('='*10 + ' Ranking ' + '='*10)
        if args.lucene8:
            logger.info('Enabling Lucene 8 index compatibility.')
        search_cmds = construct_search_commands(yaml_data)
        if args.dry_run:
            for cmd in search_cmds:
                logger.info(' '.join(cmd))
        else:
            with Pool(args.search_pool) as p:
                p.map(run_search, search_cmds)

        if 'conversions' in yaml_data and yaml_data['conversions']:
            logger.info('='*10 + ' Converting ' + '='*10)
            convert_cmds = construct_convert_commands(yaml_data)
            if args.dry_run:
                for cmd in convert_cmds:
                    logger.info(' '.join(cmd))
            else:
                with Pool(args.convert_pool) as p:
                    p.map(run_convert, convert_cmds)

        evaluate_and_verify(yaml_data, args.dry_run)
