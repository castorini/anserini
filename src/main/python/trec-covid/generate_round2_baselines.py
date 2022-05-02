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

"""Perform Anserini baseline runs for TREC-COVID Round 2."""

import os
import sys

import pyserini.util

from covid_baseline_tools import perform_runs, perform_fusion, prepare_final_submissions, \
    evaluate_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-cord19-abstract-2020-05-01',
           'indexes/lucene-index-cord19-full-text-2020-05-01',
           'indexes/lucene-index-cord19-paragraph-2020-05-01']

cumulative_runs = {
    'anserini.covid-r2.abstract.qq.bm25.txt': '9cdea30a3881f9e60d3c61a890b094bd',
    'anserini.covid-r2.abstract.qdel.bm25.txt': '1e1bcdf623f69799a2b1b2982f53c23d',
    'anserini.covid-r2.full-text.qq.bm25.txt': '6d704c60cc2cf134430c36ec2a0a3faa',
    'anserini.covid-r2.full-text.qdel.bm25.txt': '352a8b35a0626da21cab284bddb2e4e5',
    'anserini.covid-r2.paragraph.qq.bm25.txt': 'b48c9ffb3cf9b35269ca9321ac39e758',
    'anserini.covid-r2.paragraph.qdel.bm25.txt': '580fd34fbbda855dd09e1cb94467cb19',
    'anserini.covid-r2.fusion1.txt': '2a131517308d088c3f55afa0b8d5bb04',
    'anserini.covid-r2.fusion2.txt': '9760124d8cfa03a0e3aae3a4c6e32550'
}

final_runs = {
    'anserini.final-r2.fusion1.txt': '89544da0409435c74dd4f3dd5fc9dc62',
    'anserini.final-r2.fusion2.txt': '12679197846ed77306ecb2ca7895b011'
}


def perform_runs():
    base_topics = f'src/main/resources/topics-and-qrels/topics.covid-round2.xml'
    udel_topics = f'src/main/resources/topics-and-qrels/topics.covid-round2-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.abstract.qq.bm25.txt -runtag anserini.covid-r2.abstract.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {abstract_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.abstract.qdel.bm25.txt -runtag anserini.covid-r2.abstract.qdel.bm25.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.full-text.qq.bm25.txt -runtag anserini.covid-r2.full-text.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.full-text.qdel.bm25.txt -runtag anserini.covid-r2.full-text.qdel.bm25.txt')

    print('')
    print('## Running on paragraph index...')
    print('')

    paragraph_index = indexes[2]
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-selectMaxPassage -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.paragraph.qq.bm25.txt -runtag anserini.covid-r2.paragraph.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-selectMaxPassage -bm25 -hits 10000 ' +
              f'-output runs/anserini.covid-r2.paragraph.qdel.bm25.txt -runtag anserini.covid-r2.paragraph.qdel.bm25.txt')


def perform_fusion(run_checksums, check_md5=True):
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = f'anserini.covid-r2.fusion1.txt'
    set1 = [f'anserini.covid-r2.abstract.qq.bm25.txt',
            f'anserini.covid-r2.full-text.qq.bm25.txt',
            f'anserini.covid-r2.paragraph.qq.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    if check_md5:
        assert pyserini.util.compute_md5(f'runs/{fusion_run1}') == run_checksums[fusion_run1], \
            f'Error in producing {fusion_run1}!'

    fusion_run2 = f'anserini.covid-r2.fusion2.txt'
    set2 = [f'anserini.covid-r2.abstract.qdel.bm25.txt',
            f'anserini.covid-r2.full-text.qdel.bm25.txt',
            f'anserini.covid-r2.paragraph.qdel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')

    if check_md5:
        assert pyserini.util.compute_md5(f'runs/{fusion_run2}') == run_checksums[fusion_run2], \
            f'Error in producing {fusion_run2}!'


def prepare_final_submissions(run_checksums, check_md5=True):
    # Remove the cumulative qrels from the previous round.
    qrels = f'src/main/resources/topics-and-qrels/qrels.covid-round1.txt'

    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = f'anserini.final-r2.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r2.fusion1.txt --output runs/{run1} ' +
              f'--runtag r2.fusion1')
    run1_md5 = pyserini.util.compute_md5(f'runs/{run1}')

    if check_md5:
        assert run1_md5 == run_checksums[run1], f'Error in producing {run1}!'

    run2 = f'anserini.final-r2.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r2.fusion2.txt --output runs/{run2} ' +
              f'--runtag r2.fusion2')
    run2_md5 = pyserini.util.compute_md5(f'runs/{run2}')

    if check_md5:
        assert run2_md5 == run_checksums[run2], f'Error in producing {run2}!'

    print('')
    print(f'{run1:<35}{run1_md5}')
    print(f'{run2:<35}{run2_md5}')


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round1_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round1.txt'
    round2_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round2.txt'

    # Note that this script was written after this issue was noted: https://github.com/castorini/anserini/issues/1669
    # Thus, no point in checking MD5.
    check_md5_flag = False

    perform_runs()
    perform_fusion(cumulative_runs, check_md5=check_md5_flag)
    prepare_final_submissions(final_runs, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r2.abstract.qq.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3522, 'judged_cut_10': 0.5371, 'ndcg_cut_20': 0.3171,
             'judged_cut_20': 0.5100, 'map': 0.1752, 'recall_1000': 0.6601, 'judged_cut_1000': 0.1013},
        'anserini.covid-r2.abstract.qdel.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3781, 'judged_cut_10': 0.5371, 'ndcg_cut_20': 0.3462,
             'judged_cut_20': 0.4829, 'map': 0.1804, 'recall_1000': 0.6485, 'judged_cut_1000': 0.0958},
        'anserini.covid-r2.full-text.qq.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.2070, 'judged_cut_10': 0.4286, 'ndcg_cut_20': 0.1931,
             'judged_cut_20': 0.3929, 'map': 0.1159, 'recall_1000': 0.5953, 'judged_cut_1000': 0.0995},
        'anserini.covid-r2.full-text.qdel.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3123, 'judged_cut_10': 0.4229, 'ndcg_cut_20': 0.2738,
             'judged_cut_20': 0.3929, 'map': 0.1473, 'recall_1000': 0.6517, 'judged_cut_1000': 0.1022},
        'anserini.covid-r2.paragraph.qq.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.2772, 'judged_cut_10': 0.4400, 'ndcg_cut_20': 0.2579,
             'judged_cut_20': 0.4529, 'map': 0.1607, 'recall_1000': 0.7248, 'judged_cut_1000': 0.1220},
        'anserini.covid-r2.paragraph.qdel.bm25.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3353, 'judged_cut_10': 0.4343, 'ndcg_cut_20': 0.2956,
             'judged_cut_20': 0.4329, 'map': 0.1772, 'recall_1000': 0.7196, 'judged_cut_1000': 0.1136},
        'anserini.covid-r2.fusion1.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3297, 'judged_cut_10': 0.4657, 'ndcg_cut_20': 0.3060,
             'judged_cut_20': 0.4643, 'map': 0.1914, 'recall_1000': 0.7561, 'judged_cut_1000': 0.1304},
        'anserini.covid-r2.fusion2.txt':
            {'topics': 35, 'ndcg_cut_10': 0.3679, 'judged_cut_10': 0.4829, 'ndcg_cut_20': 0.3360,
             'judged_cut_20': 0.4557, 'map': 0.2066, 'recall_1000': 0.7511, 'judged_cut_1000': 0.1200},
    }
    evaluate_runs(round1_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    # Note that recall@1k doesn't match the figures reported here:
    # https://github.com/castorini/anserini/blob/master/docs/experiments-covid.md
    expected_metrics = {
        'anserini.final-r2.fusion1.txt':
            {'topics': 35, 'ndcg_cut_10': 0.4827, 'judged_cut_10': 0.9543, 'ndcg_cut_20': 0.4512,
             'judged_cut_20': 0.8614, 'map': 0.2431, 'recall_1000': 0.6475, 'judged_cut_1000': 0.1463},
        'anserini.final-r2.fusion2.txt':
            {'topics': 35, 'ndcg_cut_10': 0.5553, 'judged_cut_10': 0.9743, 'ndcg_cut_20': 0.5058,
             'judged_cut_20': 0.8957, 'map': 0.2739, 'recall_1000': 0.6832, 'judged_cut_1000': 0.1528},
    }
    evaluate_runs(round2_qrels, final_runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
