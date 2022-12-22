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

"""Perform Anserini baseline runs for TREC-COVID Round 1."""

import os
import sys

from covid_baseline_tools import perform_runs, perform_fusion, evaluate_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-covid-2020-04-10',
           'indexes/lucene-index-covid-full-text-2020-04-10',
           'indexes/lucene-index-covid-paragraph-2020-04-10']

runs = {
    'anserini.covid-r1.abstract.query.bm25.txt': '',
    'anserini.covid-r1.abstract.question.bm25.txt': '',
    'anserini.covid-r1.abstract.query+question.bm25.txt': '',
    'anserini.covid-r1.abstract.query+question+narrative.bm25.txt': '',
    'anserini.covid-r1.abstract.query-udel.bm25.txt': '',
    'anserini.covid-r1.abstract.query-covid19.bm25.txt': '',
    'anserini.covid-r1.full-text.query.bm25.txt': '',
    'anserini.covid-r1.full-text.question.bm25.txt': '',
    'anserini.covid-r1.full-text.query+question.bm25.txt': '',
    'anserini.covid-r1.full-text.query+question+narrative.bm25.txt': '',
    'anserini.covid-r1.full-text.query-udel.bm25.txt': '',
    'anserini.covid-r1.full-text.query-covid19.bm25.txt': '',
    'anserini.covid-r1.paragraph.query.bm25.txt': '',
    'anserini.covid-r1.paragraph.question.bm25.txt': '',
    'anserini.covid-r1.paragraph.query+question.bm25.txt': '',
    'anserini.covid-r1.paragraph.query+question+narrative.bm25.txt': '',
    'anserini.covid-r1.paragraph.query-udel.bm25.txt': '',
    'anserini.covid-r1.paragraph.query-covid19.bm25.txt': '',
    'anserini.covid-r1.fusion1.txt': '',
    'anserini.covid-r1.fusion2.txt': ''
}


def perform_runs():
    base_topics = f'src/main/resources/topics-and-qrels/topics.covid-round1.xml'
    udel_topics = f'src/main/resources/topics-and-qrels/topics.covid-round1-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.query.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield question ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.query+question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question+narrative ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.query+question+narrative.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.query-udel.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query -querygenerator Covid19QueryGenerator ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.abstract.query-covid19.bm25.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.query.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield question ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.query+question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question+narrative ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.query+question+narrative.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.query-udel.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query -querygenerator Covid19QueryGenerator ' +
              f'-removedups -bm25 ' +
              f'-output runs/anserini.covid-r1.full-text.query-covid19.bm25.txt')

    print('')
    print('## Running on paragraph index...')
    print('')

    paragraph_index = indexes[2]
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.query.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield question ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.query+question.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question+narrative ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.query+question+narrative.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.query-udel.bm25.txt')
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query -querygenerator Covid19QueryGenerator ' +
              f'-removedups -bm25 -selectMaxPassage ' +
              f'-output runs/anserini.covid-r1.paragraph.query-covid19.bm25.txt')


def perform_fusion():
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = f'anserini.covid-r1.fusion1.txt'
    set1 = [f'anserini.covid-r1.abstract.query+question.bm25.txt',
            f'anserini.covid-r1.full-text.query+question.bm25.txt',
            f'anserini.covid-r1.paragraph.query+question.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    fusion_run2 = f'anserini.covid-r1.fusion2.txt'
    set2 = [f'anserini.covid-r1.abstract.query-udel.bm25.txt',
            f'anserini.covid-r1.full-text.query-udel.bm25.txt',
            f'anserini.covid-r1.paragraph.query-udel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round1_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round1.txt'

    # Note that this script was written after this issue was noted: https://github.com/castorini/anserini/issues/1669
    # Thus, no point in checking MD5.
    check_md5_flag = False

    perform_runs()
    perform_fusion()

    expected_metrics = {
        'anserini.covid-r1.abstract.query.bm25.txt':
            {'ndcg_cut_10': 0.4100, 'judged_cut_10': 0.8267, 'recall_1000': 0.5279},
        'anserini.covid-r1.abstract.question.bm25.txt':
            {'ndcg_cut_10': 0.5179, 'judged_cut_10': 0.9833, 'recall_1000': 0.6313},
        'anserini.covid-r1.abstract.query+question.bm25.txt':
            {'ndcg_cut_10': 0.5514, 'judged_cut_10': 0.9833, 'recall_1000': 0.6989},
        'anserini.covid-r1.abstract.query+question+narrative.bm25.txt':
            {'ndcg_cut_10': 0.5294, 'judged_cut_10': 0.9333, 'recall_1000': 0.6929},
        'anserini.covid-r1.abstract.query-udel.bm25.txt':
            {'ndcg_cut_10': 0.5824, 'judged_cut_10': 0.9567, 'recall_1000': 0.6927},
        'anserini.covid-r1.abstract.query-covid19.bm25.txt':
            {'ndcg_cut_10': 0.4520, 'judged_cut_10': 0.6500, 'recall_1000': 0.5061},
        'anserini.covid-r1.full-text.query.bm25.txt':
            {'ndcg_cut_10': 0.3900, 'judged_cut_10': 0.7433, 'recall_1000': 0.6277},
        'anserini.covid-r1.full-text.question.bm25.txt':
            {'ndcg_cut_10': 0.3439, 'judged_cut_10': 0.9267, 'recall_1000': 0.6389},
        'anserini.covid-r1.full-text.query+question.bm25.txt':
            {'ndcg_cut_10': 0.4064, 'judged_cut_10': 0.9367, 'recall_1000': 0.6714},
        'anserini.covid-r1.full-text.query+question+narrative.bm25.txt':
            {'ndcg_cut_10': 0.3280, 'judged_cut_10': 0.7567, 'recall_1000': 0.6591},
        'anserini.covid-r1.full-text.query-udel.bm25.txt':
            {'ndcg_cut_10': 0.5407, 'judged_cut_10': 0.9067, 'recall_1000': 0.7214},
        'anserini.covid-r1.full-text.query-covid19.bm25.txt':
            {'ndcg_cut_10': 0.2434, 'judged_cut_10': 0.5233, 'recall_1000': 0.5692},
        'anserini.covid-r1.paragraph.query.bm25.txt':
            {'ndcg_cut_10': 0.4302, 'judged_cut_10': 0.8400, 'recall_1000': 0.4327},
        'anserini.covid-r1.paragraph.question.bm25.txt':
            {'ndcg_cut_10': 0.4410, 'judged_cut_10': 0.9167, 'recall_1000': 0.5111},
        'anserini.covid-r1.paragraph.query+question.bm25.txt':
            {'ndcg_cut_10': 0.5450, 'judged_cut_10': 0.9733, 'recall_1000': 0.5743},
        'anserini.covid-r1.paragraph.query+question+narrative.bm25.txt':
            {'ndcg_cut_10': 0.4899, 'judged_cut_10': 0.8967, 'recall_1000': 0.5918},
        'anserini.covid-r1.paragraph.query-udel.bm25.txt':
            {'ndcg_cut_10': 0.5544, 'judged_cut_10': 0.9200, 'recall_1000': 0.5640},
        'anserini.covid-r1.paragraph.query-covid19.bm25.txt':
            {'ndcg_cut_10': 0.3180, 'judged_cut_10': 0.5333, 'recall_1000': 0.3552},
        'anserini.covid-r1.fusion1.txt':
            {'ndcg_cut_10': 0.5716, 'judged_cut_10': 0.9867, 'recall_1000': 0.8122},
        'anserini.covid-r1.fusion2.txt':
            {'ndcg_cut_10': 0.6019, 'judged_cut_10': 0.9733, 'recall_1000': 0.8121}
    }
    evaluate_runs(round1_qrels, runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
