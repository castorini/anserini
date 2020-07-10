#
# Pyserini: Python interface to the Anserini IR toolkit built on Lucene
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

import hashlib
import os
import shutil
import subprocess
import sys

from random import randint

sys.path.insert(0, '../pyserini/')

import pyserini.util


def evaluate_run(run, qrels):
    metrics = {}
    output = subprocess.check_output(
        f'tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 -m recall.1000 -m map {qrels} runs/{run}', shell=True)

    lines = output.decode('utf-8').split('\n')

    for line in lines:
        arr = line.split()
        if len(arr) == 3:
            metrics[arr[0]] = float(arr[2])

    output = subprocess.check_output(f'python tools/eval/measure_judged.py --qrels {qrels} ' +
                                     f'--cutoffs 10 100 1000 --run runs/{run}', shell=True)

    lines = output.decode('utf-8').split('\n')

    for line in lines:
        arr = line.split()
        if len(arr) == 3:
            metrics[arr[0]] = float(arr[2])

    # Count number of unique topics; Note, run through sed first to convert tabs to spaces.
    output = subprocess.check_output(f"sed 's/\t/ /g' runs/{run} | tr -s ' ' | cut -d ' ' -f 1 | sort | uniq | wc",
                                     shell=True)
    arr = output.decode('utf-8').split()
    metrics['topics'] = int(arr[0])

    metrics['md5'] = pyserini.util.compute_md5(f'runs/{run}')

    return metrics


def evaluate_runs(qrels, runs):
    max_length = 0
    for run in runs:
        if len(run) > max_length:
            max_length = len(run)

    padding = 2

    print('')
    print(f'## Evaluation results w/ {qrels}')
    print('')
    print(' ' * (max_length - 2) + 'topics nDCG@10   J@10     AP   R@1k   J@1k MD5')

    for run in runs:
        metrics = evaluate_run(run, qrels)

        # It is possible to pass a format string into an f-string:
        # https://stackoverflow.com/questions/54780428/how-to-pass-string-format-as-a-variable-to-an-f-string
        # We use this trick to get the right amount of space padding for the first column.
        format_string = '<' + str(max_length + padding)
        print(f'{run:{format_string}}' +
              f'{metrics["topics"]}{metrics["ndcg_cut_10"]:8.4f}' +
              f'{metrics["judged_cut_10"]:7.4f}{metrics["map"]:7.4f}' +
              f'{metrics["recall_1000"]:7.4f}{metrics["judged_cut_1000"]:7.4f} ' +
              f'{metrics["md5"]}')

        assert metrics['md5'] == runs[run], f'Error in producing {run}!'


def verify_stored_runs(runs):
    print('')
    print(f'## Verifying Stored Runs')
    print('')

    tmp = f'tmp{randint(0, 10000)}'

    # In the rare event there's a collision
    if os.path.exists(tmp):
        shutil.rmtree(tmp)

    os.mkdir(tmp)
    for url in runs:
        print(f'Verifying stored run at {url}...')
        # Use pyserini tools to download and check the API at the same time.
        pyserini.util.download_url(url, tmp, force=True, md5=runs[url])

    shutil.rmtree(tmp)
