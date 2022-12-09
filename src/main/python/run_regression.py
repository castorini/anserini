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
from multiprocessing import Pool
from subprocess import call, Popen, PIPE
from urllib.request import urlretrieve

import yaml
from tqdm import tqdm

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
    '/tuna1/',                    # on tuna
    '/store/',                    # on orca
    '/collection',                # on hops (1st location)
    '/',                          # on hops (2nd location)
    '/scratch2/',                 # on damiano
    '/System/Volumes/Data/store'  # for new organization of directories in macOS Monterey
]

INDEX_COMMAND = 'target/appassembler/bin/IndexCollection'
INDEX_STATS_COMMAND = 'target/appassembler/bin/IndexReaderUtils'
SEARCH_COMMAND = 'target/appassembler/bin/SearchCollection'


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
            corpus_path = os.path.join(input_root, yaml_data['corpus_path'])
            if os.path.exists(corpus_path):
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

    index_command = [
        INDEX_COMMAND,
        '-collection', yaml_data['collection_class'],
        '-generator', yaml_data['generator_class'],
        '-threads', str(threads),
        '-input', corpus_path,
        '-index', yaml_data['index_path'],
        yaml_data['index_options']
    ]

    return index_command


def construct_runfile_path(corpus, id, model_name):
    return os.path.join('runs/', 'run.{0}.{1}.{2}'.format(corpus, id, model_name))


def construct_search_commands(yaml_data):
    ranking_commands = [
        [
            SEARCH_COMMAND,
            '-index', construct_index_path(yaml_data),
            '-topics', os.path.join(yaml_data['topic_root'], topic_set['path']),
            '-topicreader', topic_set['topic_reader'] if 'topic_reader' in topic_set and topic_set['topic_reader'] else yaml_data['topic_reader'],
            '-output', construct_runfile_path(yaml_data['corpus'], topic_set['id'], model['name']),
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
            '--input', construct_runfile_path(yaml_data['corpus'], topic_set['id'], model['name']) + conversion['in_file_ext'],
            '--output', construct_runfile_path(yaml_data['corpus'], topic_set['id'], model['name']) + conversion['out_file_ext'],
            conversion['params'] if 'params' in conversion and conversion['params'] else '',
            topic_set['convert_params'] if 'convert_params' in topic_set and topic_set['convert_params'] else '',
        ]
        for (model, topic_set, conversion) in list(itertools.product(yaml_data['models'], yaml_data['topics'], yaml_data['conversions']))
    ]
    return converting_commands

def evaluate_and_verify(yaml_data, dry_run):
    fail_str = '\033[91m[FAIL]\033[0m '
    ok_str = '   [OK] '
    okish_str = '  \033[94m[OK*]\033[0m '
    failures = False

    logger.info('='*10 + ' Verifying Results: ' + yaml_data['corpus'] + ' ' + '='*10)
    for model in yaml_data['models']:
        for i, topic_set in enumerate(yaml_data['topics']):
            for metric in yaml_data['metrics']:
                eval_cmd = [
                  os.path.join(metric['command']), metric['params'] if 'params' in metric and metric['params'] else '',
                  os.path.join(yaml_data['qrels_root'], topic_set['qrel']) if 'qrel' in topic_set and topic_set['qrel'] else '',
                  construct_runfile_path(yaml_data['corpus'], topic_set['id'], model['name']) + (yaml_data['conversions'][-1]['out_file_ext'] if 'conversions' in yaml_data and yaml_data['conversions'][-1]['out_file_ext'] else '')
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
                result_str = 'expected: {0:.4f} actual: {1:.4f} - metric: {2:<8} model: {3} topics: {4}'.format(
                    expected, actual, metric['metric'], model['name'], topic_set['id'])
                if is_close(expected, actual):
                    logger.info(ok_str + result_str)
                else:
                    if args.lucene8 and is_close_lucene8(expected, actual):
                        logger.info(okish_str + result_str)
                    else:
                        logger.error(fail_str + result_str)
                        failures = True

    end = time.time()

    if not dry_run:
        if failures:
            logger.info(f'\033[91mFailed tests!\033[0m Total elapsed time: {end - start:.0f}s')
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
        index_utils_command = [INDEX_STATS_COMMAND, '-index', construct_index_path(yaml_data), '-stats']
        verification_command = ' '.join(index_utils_command)
        logger.info(verification_command)
        if not args.dry_run:
            out = check_output(' '.join(index_utils_command)).decode('utf-8').split('\n')
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
