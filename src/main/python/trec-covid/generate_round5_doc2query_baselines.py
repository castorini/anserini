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

"""Perform Anserini baseline runs for TREC-COVID Round 4."""

import os
import sys

from pyserini.util import compute_md5

from covid_baseline_tools import evaluate_runs, verify_stored_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-cord19-abstract-docT5query-2020-07-16',
           'indexes/lucene-index-cord19-full-text-docT5query-2020-07-16',
           'indexes/lucene-index-cord19-paragraph-docT5query-2020-07-16']

cumulative_runs = {
    'expanded.anserini.covid-r5.abstract.qq.bm25.txt': '9923233a31ac004f84b7d563baf6543c',
    'expanded.anserini.covid-r5.abstract.qdel.bm25.txt': 'e0c7a1879e5b1742045bba0f5293d558',
    'expanded.anserini.covid-r5.full-text.qq.bm25.txt': '78aa7f481de91d22192163ed934d02ee',
    'expanded.anserini.covid-r5.full-text.qdel.bm25.txt': '51cbae025bf90dadf8f26c5c31af9f66',
    'expanded.anserini.covid-r5.paragraph.qq.bm25.txt': '0b80444c8a737748ba9199ddf0795421',
    'expanded.anserini.covid-r5.paragraph.qdel.bm25.txt': '2040b9a4759af722d50610f26989c328',
    'expanded.anserini.covid-r5.fusion1.txt': 'c0ffc7b1719f64d2f37ce99a9ef0413c',
    'expanded.anserini.covid-r5.fusion2.txt': '329f13267abf3f3d429a1593c1bd862f',
    'expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt': 'a5e016c84d5547519ffbcf74c9a24fc8'
}

final_runs = {
    'expanded.anserini.final-r5.fusion1.txt': '2295216ed623d2621f00c294f7c389e1',
    'expanded.anserini.final-r5.fusion1.post-processed.txt': '03ad001d94c772649e17f4d164d4b2e2',
    'expanded.anserini.final-r5.fusion2.txt': 'a65fabe7b5b7bc4216be632296269ce6',
    'expanded.anserini.final-r5.fusion2.post-processed.txt': '4137c93e76970616e0eff2803501cd08',
    'expanded.anserini.final-r5.rf.txt': '24f0b75a25273b7b00d3e65065e98147',
    'expanded.anserini.final-r5.rf.post-processed.txt': '3dfba85c0630865a7b581c4358cf4587'
}

stored_runs = {
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.abstract.qq.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.abstract.qdel.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.full-text.qq.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.full-text.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.full-text.qdel.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.full-text.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.paragraph.qq.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.paragraph.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.paragraph.qdel.bm25.txt':
        cumulative_runs['expanded.anserini.covid-r5.paragraph.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.fusion1.txt':
        cumulative_runs['expanded.anserini.covid-r5.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.fusion2.txt':
        cumulative_runs['expanded.anserini.covid-r5.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.fusion1.txt':
        final_runs['expanded.anserini.final-r5.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.fusion2.txt':
        final_runs['expanded.anserini.final-r5.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.rf.txt':
        final_runs['expanded.anserini.final-r5.rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.fusion1.post-processed.txt':
        final_runs['expanded.anserini.final-r5.fusion1.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.fusion2.post-processed.txt':
        final_runs['expanded.anserini.final-r5.fusion2.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/expanded.anserini.final-r5.rf.post-processed.txt':
        final_runs['expanded.anserini.final-r5.rf.post-processed.txt'],
}


def perform_runs():
    base_topics = 'src/main/resources/topics-and-qrels/topics.covid-round5.xml'
    udel_topics = 'src/main/resources/topics-and-qrels/topics.covid-round5-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = 'expanded.anserini.covid-r5.abstract'
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
              f'-rf.qrels src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt ' +
              f'-output runs/{abstract_prefix}.qdel.bm25+rm3Rf.txt -runtag {abstract_prefix}.qdel.bm25+rm3Rf.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'expanded.anserini.covid-r5.full-text'
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
    paragraph_prefix = 'expanded.anserini.covid-r5.paragraph'
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qq.bm25.txt -runtag {paragraph_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qdel.bm25.txt -runtag {paragraph_prefix}.qdel.bm25.txt')


def perform_fusion(check_md5=True):
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = 'expanded.anserini.covid-r5.fusion1.txt'
    set1 = ['expanded.anserini.covid-r5.abstract.qq.bm25.txt',
            'expanded.anserini.covid-r5.full-text.qq.bm25.txt',
            'expanded.anserini.covid-r5.paragraph.qq.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    if check_md5:
        assert compute_md5(f'runs/{fusion_run1}') == cumulative_runs[fusion_run1], f'Error in producing {fusion_run1}!'

    fusion_run2 = 'expanded.anserini.covid-r5.fusion2.txt'
    set2 = ['expanded.anserini.covid-r5.abstract.qdel.bm25.txt',
            'expanded.anserini.covid-r5.full-text.qdel.bm25.txt',
            'expanded.anserini.covid-r5.paragraph.qdel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')

    if check_md5:
        assert compute_md5(f'runs/{fusion_run2}') == cumulative_runs[fusion_run2], f'Error in producing {fusion_run2}!'

    if check_md5:
        cumulative_md5 = {run: compute_md5(f'runs/{run}') for run in cumulative_runs}
        print(f'Checksums for cumulative runs: {cumulative_md5}')


def prepare_final_submissions(qrels, check_md5=True):
    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = 'expanded.anserini.final-r5.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.fusion1.txt --output runs/{run1} --runtag r5.fusion1')
    run1_md5 = compute_md5(f'runs/{run1}')

    if check_md5:
        assert run1_md5 == final_runs[run1], f'Error in producing {run1}!'

    run2 = 'expanded.anserini.final-r5.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.fusion2.txt --output runs/{run2} --runtag r5.fusion2')
    run2_md5 = compute_md5(f'runs/{run2}')

    if check_md5:
        assert run2_md5 == final_runs[run2], f'Error in producing {run2}!'

    run3 = 'expanded.anserini.final-r5.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r5.rf')
    run3_md5 = compute_md5(f'runs/{run3}')

    if check_md5:
        assert run3_md5 == final_runs[run3], f'Error in producing {run3}!'

    if check_md5:
        final_md5 = {run: compute_md5(f'runs/{run}') for run in final_runs}
        print(f'Checksums for final runs: {final_md5}')


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round4_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'
    complete_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-complete.txt'
    round5_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round5.txt'

    # MD5 checksums don't match anymore, see https://github.com/castorini/anserini/issues/1669
    check_md5_flag = False

    verify_stored_runs(stored_runs)
    perform_runs()
    perform_fusion(check_md5=check_md5_flag)
    prepare_final_submissions(round4_cumulative_qrels, check_md5=check_md5_flag)

    expected_metrics = {
        'expanded.anserini.covid-r5.abstract.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4635, 'judged_cut_10': 0.5300, 'ndcg_cut_20': 0.4326,
             'judged_cut_20': 0.5120, 'map': 0.1728, 'recall_1000': 0.4462, 'judged_cut_1000': 0.2059},
        'expanded.anserini.covid-r5.abstract.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4548, 'judged_cut_10': 0.5000, 'ndcg_cut_20': 0.4260,
             'judged_cut_20': 0.4880, 'map': 0.1742, 'recall_1000': 0.4527, 'judged_cut_1000': 0.2051},
        'expanded.anserini.covid-r5.full-text.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4450, 'judged_cut_10': 0.6020, 'ndcg_cut_20': 0.4208,
             'judged_cut_20': 0.5820, 'map': 0.1801, 'recall_1000': 0.4473, 'judged_cut_1000': 0.2393},
        'expanded.anserini.covid-r5.full-text.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4817, 'judged_cut_10': 0.6040, 'ndcg_cut_20': 0.4598,
             'judged_cut_20': 0.5920, 'map': 0.1970, 'recall_1000': 0.4711, 'judged_cut_1000': 0.2439},
        'expanded.anserini.covid-r5.paragraph.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4904, 'judged_cut_10': 0.5820, 'ndcg_cut_20': 0.4622,
             'judged_cut_20': 0.5630, 'map': 0.2107, 'recall_1000': 0.5004, 'judged_cut_1000': 0.2511},
        'expanded.anserini.covid-r5.paragraph.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4940, 'judged_cut_10': 0.5700, 'ndcg_cut_20': 0.4542,
             'judged_cut_20': 0.5420, 'map': 0.2107, 'recall_1000': 0.5070, 'judged_cut_1000': 0.2486},
        'anserini.covid-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4908, 'judged_cut_10': 0.5880, 'ndcg_cut_20': 0.4753,
             'judged_cut_20': 0.5800, 'map': 0.2017, 'recall_1000': 0.5119, 'judged_cut_1000': 0.2599},
        'expanded.anserini.covid-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4846, 'judged_cut_10': 0.5740, 'ndcg_cut_20': 0.4565,
             'judged_cut_20': 0.5400, 'map': 0.2045, 'recall_1000': 0.5218, 'judged_cut_1000': 0.2578},
        'expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6095, 'judged_cut_10': 0.6320, 'ndcg_cut_20': 0.5693,
             'judged_cut_20': 0.5990, 'map': 0.2344, 'recall_1000': 0.5280, 'judged_cut_1000': 0.2257},
    }
    evaluate_runs(round4_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'expanded.anserini.covid-r5.abstract.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6808, 'judged_cut_10': 0.9980, 'ndcg_cut_20': 0.6375,
             'judged_cut_20': 0.9600, 'map': 0.2718, 'recall_1000': 0.4550, 'judged_cut_1000': 0.3845},
        'expanded.anserini.covid-r5.abstract.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6939, 'judged_cut_10': 0.9920, 'ndcg_cut_20': 0.6524,
             'judged_cut_20': 0.9610, 'map': 0.2752, 'recall_1000': 0.4595, 'judged_cut_1000': 0.3825},
        'expanded.anserini.covid-r5.full-text.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6300, 'judged_cut_10': 0.9680, 'ndcg_cut_20': 0.5843,
             'judged_cut_20': 0.9260, 'map': 0.2475, 'recall_1000': 0.4201, 'judged_cut_1000': 0.3921},
        'expanded.anserini.covid-r5.full-text.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6611, 'judged_cut_10': 0.9800, 'ndcg_cut_20': 0.6360,
             'judged_cut_20': 0.9610, 'map': 0.2746, 'recall_1000': 0.4496, 'judged_cut_1000': 0.4073},
        'expanded.anserini.covid-r5.paragraph.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6827, 'judged_cut_10': 0.9800, 'ndcg_cut_20': 0.6477,
             'judged_cut_20': 0.9670, 'map': 0.3080, 'recall_1000': 0.4936, 'judged_cut_1000': 0.4360},
        'expanded.anserini.covid-r5.paragraph.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7067, 'judged_cut_10': 0.9960, 'ndcg_cut_20': 0.6614,
             'judged_cut_20': 0.9760, 'map': 0.3127, 'recall_1000': 0.4985, 'judged_cut_1000': 0.4328},
        'expanded.anserini.covid-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7072, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.6731,
             'judged_cut_20': 0.9920, 'map': 0.2964, 'recall_1000': 0.5063, 'judged_cut_1000': 0.4528},
        'expanded.anserini.covid-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7131, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.6755,
             'judged_cut_20': 0.9910, 'map': 0.3036, 'recall_1000': 0.5166, 'judged_cut_1000': 0.4518},
        'expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.8160, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.7787,
             'judged_cut_20': 0.9960, 'map': 0.3421, 'recall_1000': 0.5249, 'judged_cut_1000': 0.4107},
    }
    evaluate_runs(complete_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'expanded.anserini.final-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5789, 'judged_cut_10': 0.9520, 'ndcg_cut_20': 0.5374,
             'judged_cut_20': 0.8530, 'map': 0.2236, 'recall_1000': 0.5798, 'judged_cut_1000': 0.2132},
        'expanded.anserini.final-r5.fusion1.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5817, 'judged_cut_10': 0.9580, 'ndcg_cut_20': 0.5414,
             'judged_cut_20': 0.8610, 'map': 0.2246, 'recall_1000': 0.5798, 'judged_cut_1000': 0.2135},
        'expanded.anserini.final-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5801, 'judged_cut_10': 0.9620, 'ndcg_cut_20': 0.5393,
             'judged_cut_20': 0.8650, 'map': 0.2310, 'recall_1000': 0.5861, 'judged_cut_1000': 0.2135},
        'expanded.anserini.final-r5.fusion2.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5825, 'judged_cut_10': 0.9680, 'ndcg_cut_20': 0.5436,
             'judged_cut_20': 0.8700, 'map': 0.2319, 'recall_1000': 0.5861, 'judged_cut_1000': 0.2138},
        'expanded.anserini.final-r5.rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6628, 'judged_cut_10': 0.9460, 'ndcg_cut_20': 0.6040,
             'judged_cut_20': 0.8370, 'map': 0.2410, 'recall_1000': 0.6039, 'judged_cut_1000': 0.1995},
        'expanded.anserini.final-r5.rf.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6757, 'judged_cut_10': 0.9620, 'ndcg_cut_20': 0.6124,
             'judged_cut_20': 0.8470, 'map': 0.2433, 'recall_1000': 0.6039, 'judged_cut_1000': 0.1998},
    }
    evaluate_runs(round5_qrels, final_runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
