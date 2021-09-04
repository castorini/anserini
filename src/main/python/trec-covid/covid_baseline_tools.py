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

import re
import os
import subprocess
import sys

sys.path.insert(0, '../pyserini/')

import pyserini.util


def perform_runs(round_number, indexes):
    base_topics = f'src/main/resources/topics-and-qrels/topics.covid-round{round_number}.xml'
    udel_topics = f'src/main/resources/topics-and-qrels/topics.covid-round{round_number}-udel.xml'

    # Use cumulative qrels from previous round for relevance feedback runs
    cumulative_qrels = f'src/main/resources/topics-and-qrels/qrels.covid-round{round_number - 1}-cumulative.txt'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = f'anserini.covid-r{round_number}.abstract'
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{abstract_prefix}.qq.bm25.txt -runtag {abstract_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{abstract_prefix}.qdel.bm25.txt -runtag {abstract_prefix}.qdel.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query -removedups ' +
              f'-bm25 -rm3 -rm3.fbTerms 100 -hits 10000 ' +
              f'-rf.qrels {cumulative_qrels} ' +
              f'-output runs/{abstract_prefix}.qdel.bm25+rm3Rf.txt -runtag {abstract_prefix}.qdel.bm25+rm3Rf.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = f'anserini.covid-r{round_number}.full-text'
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{full_text_prefix}.qq.bm25.txt -runtag {full_text_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{full_text_prefix}.qdel.bm25.txt -runtag {full_text_prefix}.qdel.bm25.txt')

    print('')
    print('## Running on paragraph index...')
    print('')

    paragraph_index = indexes[2]
    paragraph_prefix = f'anserini.covid-r{round_number}.paragraph'
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qq.bm25.txt -runtag {paragraph_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qdel.bm25.txt -runtag {paragraph_prefix}.qdel.bm25.txt')


def perform_fusion(round_number, run_checksums, check_md5=True):
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = f'anserini.covid-r{round_number}.fusion1.txt'
    set1 = [f'anserini.covid-r{round_number}.abstract.qq.bm25.txt',
            f'anserini.covid-r{round_number}.full-text.qq.bm25.txt',
            f'anserini.covid-r{round_number}.paragraph.qq.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    if check_md5:
        assert pyserini.util.compute_md5(f'runs/{fusion_run1}') == run_checksums[fusion_run1],\
            f'Error in producing {fusion_run1}!'

    fusion_run2 = f'anserini.covid-r{round_number}.fusion2.txt'
    set2 = [f'anserini.covid-r{round_number}.abstract.qdel.bm25.txt',
            f'anserini.covid-r{round_number}.full-text.qdel.bm25.txt',
            f'anserini.covid-r{round_number}.paragraph.qdel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')

    if check_md5:
        assert pyserini.util.compute_md5(f'runs/{fusion_run2}') == run_checksums[fusion_run2],\
            f'Error in producing {fusion_run2}!'


def prepare_final_submissions(round_number, run_checksums, check_md5=True):
    # Remove teh cumulative qrels from the previous round.
    qrels = f'src/main/resources/topics-and-qrels/qrels.covid-round{round_number - 1}-cumulative.txt'

    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = f'anserini.final-r{round_number}.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r{round_number}.fusion1.txt --output runs/{run1} ' +
              f'--runtag r{round_number}.fusion1')
    run1_md5 = pyserini.util.compute_md5(f'runs/{run1}')

    if check_md5:
        assert run1_md5 == run_checksums[run1], f'Error in producing {run1}!'

    run2 = f'anserini.final-r{round_number}.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r{round_number}.fusion2.txt --output runs/{run2} ' +
              f'--runtag r{round_number}.fusion2')
    run2_md5 = pyserini.util.compute_md5(f'runs/{run2}')

    if check_md5:
        assert run2_md5 == run_checksums[run2], f'Error in producing {run2}!'

    run3 = f'anserini.final-r{round_number}.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r{round_number}.abstract.qdel.bm25+rm3Rf.txt ' +
              f'--output runs/{run3} --runtag r{round_number}.rf')
    run3_md5 = pyserini.util.compute_md5(f'runs/{run3}')

    if check_md5:
        assert run3_md5 == run_checksums[run3], f'Error in producing {run3}!'

    print('')
    print(f'{run1:<35}{run1_md5}')
    print(f'{run2:<35}{run2_md5}')
    print(f'{run3:<35}{run3_md5}')


def evaluate_run(run, qrels):
    metrics = {}
    output = subprocess.check_output(
        f'tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 -m recall.1000 -m map {qrels} runs/{run}', shell=True)

    lines = output.decode('utf-8').split('\n')

    for line in lines:
        arr = line.split()
        if len(arr) == 3:
            metrics[arr[0]] = float(arr[2])

    # trec_eval doesn't seem to be able to evaluate the same metric at different cutoffs, so we have to run again
    output = subprocess.check_output(
        f'tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.20 {qrels} runs/{run}', shell=True)

    lines = output.decode('utf-8').split('\n')

    for line in lines:
        arr = line.split()
        if len(arr) == 3:
            metrics[arr[0]] = float(arr[2])

    output = subprocess.check_output(f'python tools/eval/measure_judged.py --qrels {qrels} ' +
                                     f'--cutoffs 10 20 100 1000 --run runs/{run}', shell=True)

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


def evaluate_runs(qrels, runs, check_md5=True):
    max_length = 0
    for run in runs:
        if len(run) > max_length:
            max_length = len(run)

    padding = 2

    print('')
    print(f'## Evaluation results w/ {qrels}')
    print('')
    print(' ' * (max_length - 2) + 'topics nDCG@10   J@10 nDCG@20   J@20     AP   R@1k   J@1k MD5')

    for run in runs:
        metrics = evaluate_run(run, qrels)

        # It is possible to pass a format string into an f-string:
        # https://stackoverflow.com/questions/54780428/how-to-pass-string-format-as-a-variable-to-an-f-string
        # We use this trick to get the right amount of space padding for the first column.
        format_string = '<' + str(max_length + padding)
        print(f'{run:{format_string}}' +
              f'{metrics["topics"]}{metrics["ndcg_cut_10"]:8.4f}{metrics["judged_cut_10"]:7.4f}'
              f'{metrics["ndcg_cut_20"]:8.4f}{metrics["judged_cut_20"]:7.4f}'
              f'{metrics["map"]:7.4f}{metrics["recall_1000"]:7.4f}{metrics["judged_cut_1000"]:7.4f} ' +
              f'{metrics["md5"]}')

        if check_md5:
            assert metrics['md5'] == runs[run], f'Error in producing {run}!'


def verify_stored_runs(runs):
    # It's okay to store in runs/ since we're going to regenerate runs anyway.
    for url in runs:
        print(f'Verifying stored run at {url}...')
        # Use pyserini tools to download and check the API at the same time.
        pyserini.util.download_url(url, 'runs/', force=True, md5=runs[url])

        # Ugly hack the rename filename with '+' in it, which is URL encoded.
        if '%2B' in url:
            filename = url.split('/')[-1]
            filename = re.sub('\\?dl=1$', '', filename)  # Remove the Dropbox 'force download' parameter
            destination_path = os.path.join('runs/', filename)

            os.rename(destination_path, destination_path.replace('%2B', '+'))
