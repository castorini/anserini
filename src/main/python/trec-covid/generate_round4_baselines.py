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

from covid_baseline_tools import perform_runs, perform_fusion, prepare_final_submissions, \
    evaluate_runs, verify_stored_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-cord19-abstract-2020-06-19',
           'indexes/lucene-index-cord19-full-text-2020-06-19',
           'indexes/lucene-index-cord19-paragraph-2020-06-19']

cumulative_runs = {
    'anserini.covid-r4.abstract.qq.bm25.txt': '56ac5a0410e235243ca6e9f0f00eefa1',
    'anserini.covid-r4.abstract.qdel.bm25.txt': '115d6d2e308b47ffacbc642175095c74',
    'anserini.covid-r4.full-text.qq.bm25.txt': 'af0d10a5344f4007e6781e8d2959eb54',
    'anserini.covid-r4.full-text.qdel.bm25.txt': '594d469b8f45cf808092a3d8e870eaf5',
    'anserini.covid-r4.paragraph.qq.bm25.txt': '6f468b7b60aaa05fc215d237b5475aec',
    'anserini.covid-r4.paragraph.qdel.bm25.txt':  'b7b39629c12573ee0bfed8687dacc743',
    'anserini.covid-r4.fusion1.txt': '8ae9d1fca05bd1d9bfe7b24d1bdbe270',
    'anserini.covid-r4.fusion2.txt': 'e1894209c815c96c6ddd4cacb578261a',
    'anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt': '9d954f31e2f07e11ff559bcb14ef16af'
}

final_runs = {
    'anserini.final-r4.fusion1.txt': 'a8ab52e12c151012adbfc8e37d666760',
    'anserini.final-r4.fusion1.post-processed.txt': 'b0ebafe36d8fc721ea6923da5837aa8c',
    'anserini.final-r4.fusion2.txt': '1500104c928f463f38e76b58b91d4c07',
    'anserini.final-r4.fusion2.post-processed.txt': 'e7e0b870c6822e7127df71608923e76b',
    'anserini.final-r4.rf.txt': '41d746eb86a99d2f33068ebc195072cd',
    'anserini.final-r4.rf.post-processed.txt': '2fcd53854461e0cbe3c9170c0da234d9'
}

stored_runs = {
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.abstract.qq.bm25.txt':
        cumulative_runs['anserini.covid-r4.abstract.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.abstract.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r4.abstract.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.full-text.qq.bm25.txt':
        cumulative_runs['anserini.covid-r4.full-text.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.full-text.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r4.full-text.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.paragraph.qq.bm25.txt':
        cumulative_runs['anserini.covid-r4.paragraph.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.paragraph.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r4.paragraph.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.fusion1.txt':
        cumulative_runs['anserini.covid-r4.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.fusion2.txt':
        cumulative_runs['anserini.covid-r4.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt':
        cumulative_runs['anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.fusion1.txt':
        final_runs['anserini.final-r4.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.fusion2.txt':
        final_runs['anserini.final-r4.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.rf.txt':
        final_runs['anserini.final-r4.rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.fusion1.post-processed.txt':
        final_runs['anserini.final-r4.fusion1.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.fusion2.post-processed.txt':
        final_runs['anserini.final-r4.fusion2.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round4/anserini.final-r4.rf.post-processed.txt':
        final_runs['anserini.final-r4.rf.post-processed.txt'],
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round3_cumulative_qrels = 'tools/topics-and-qrels/qrels.covid-round3-cumulative.txt'
    round4_qrels = 'tools/topics-and-qrels/qrels.covid-round4.txt'
    round4_cumulative_qrels = 'tools/topics-and-qrels/qrels.covid-round4-cumulative.txt'

    # MD5 checksums don't match anymore, see https://github.com/castorini/anserini/issues/1669
    check_md5_flag = False

    verify_stored_runs(stored_runs)
    perform_runs(4, indexes)
    perform_fusion(4, cumulative_runs, check_md5=check_md5_flag)
    prepare_final_submissions(4, final_runs, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r4.abstract.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3084, 'judged_cut_10': 0.4444, 'ndcg_cut_20': 0.2964,
             'judged_cut_20': 0.4367, 'map': 0.1292, 'recall_1000': 0.4257, 'judged_cut_1000': 0.1671},
        'anserini.covid-r4.abstract.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3230, 'judged_cut_10': 0.4378, 'ndcg_cut_20': 0.3159,
             'judged_cut_20': 0.4489, 'map': 0.1381, 'recall_1000': 0.4433, 'judged_cut_1000': 0.1706},
        'anserini.covid-r4.full-text.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.2108, 'judged_cut_10': 0.4044, 'ndcg_cut_20': 0.2119,
             'judged_cut_20': 0.4011, 'map': 0.1007, 'recall_1000': 0.3891, 'judged_cut_1000': 0.1776},
        'anserini.covid-r4.full-text.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3499, 'judged_cut_10': 0.5067, 'ndcg_cut_20': 0.3260,
             'judged_cut_20': 0.4833, 'map': 0.1399, 'recall_1000': 0.4537, 'judged_cut_1000': 0.1952},
        'anserini.covid-r4.paragraph.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3228, 'judged_cut_10': 0.5267, 'ndcg_cut_20': 0.3072,
             'judged_cut_20': 0.5022, 'map': 0.1480, 'recall_1000': 0.4863, 'judged_cut_1000': 0.2110},
        'anserini.covid-r4.paragraph.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.4011, 'judged_cut_10': 0.5333, 'ndcg_cut_20': 0.3663,
             'judged_cut_20': 0.4978, 'map': 0.1647, 'recall_1000': 0.5050, 'judged_cut_1000': 0.2099},
        'anserini.covid-r4.fusion1.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3446, 'judged_cut_10': 0.5289, 'ndcg_cut_20': 0.3358,
             'judged_cut_20': 0.5289, 'map': 0.1496, 'recall_1000': 0.5033, 'judged_cut_1000': 0.2160},
        'anserini.covid-r4.fusion2.txt':
            {'topics': 45, 'ndcg_cut_10': 0.3987, 'judged_cut_10': 0.5378, 'ndcg_cut_20': 0.3743,
             'judged_cut_20': 0.5100, 'map': 0.1712, 'recall_1000': 0.5291, 'judged_cut_1000': 0.2186},
        'anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 45, 'ndcg_cut_10': 0.4521, 'judged_cut_10': 0.5000, 'ndcg_cut_20': 0.4388,
             'judged_cut_20': 0.5167, 'map': 0.1904, 'recall_1000': 0.5322, 'judged_cut_1000': 0.1888},
    }
    evaluate_runs(round3_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r4.abstract.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6619, 'judged_cut_10': 0.9356, 'ndcg_cut_20': 0.6147,
             'judged_cut_20': 0.9100, 'map': 0.2779, 'recall_1000': 0.5018, 'judged_cut_1000': 0.2876},
        'anserini.covid-r4.abstract.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.7087, 'judged_cut_10': 0.9844, 'ndcg_cut_20': 0.6662,
             'judged_cut_20': 0.9633, 'map': 0.2993, 'recall_1000': 0.5233, 'judged_cut_1000': 0.2987},
        'anserini.covid-r4.full-text.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.4192, 'judged_cut_10': 0.8067, 'ndcg_cut_20': 0.3984,
             'judged_cut_20': 0.7544, 'map': 0.1712, 'recall_1000': 0.4139, 'judged_cut_1000': 0.2740},
        'anserini.covid-r4.full-text.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6110, 'judged_cut_10': 0.9400, 'ndcg_cut_20': 0.5668,
             'judged_cut_20': 0.8933, 'map': 0.2344, 'recall_1000': 0.4856, 'judged_cut_1000': 0.3079},
        'anserini.covid-r4.paragraph.qq.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.5610, 'judged_cut_10': 0.9133, 'ndcg_cut_20': 0.5324,
             'judged_cut_20': 0.8756, 'map': 0.2713, 'recall_1000': 0.5385, 'judged_cut_1000': 0.3386},
        'anserini.covid-r4.paragraph.qdel.bm25.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6476, 'judged_cut_10': 0.9644, 'ndcg_cut_20': 0.6084,
             'judged_cut_20': 0.9322, 'map': 0.2975, 'recall_1000': 0.5625, 'judged_cut_1000': 0.3443},
        'anserini.covid-r4.fusion1.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6279, 'judged_cut_10': 0.9689, 'ndcg_cut_20': 0.5969,
             'judged_cut_20': 0.9422, 'map': 0.2904, 'recall_1000': 0.5623, 'judged_cut_1000': 0.3520},
        'anserini.covid-r4.fusion2.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6803, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.6577,
             'judged_cut_20': 0.9956, 'map': 0.3286, 'recall_1000': 0.5947, 'judged_cut_1000': 0.3626},
        'anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 45, 'ndcg_cut_10': 0.8057, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.7661,
             'judged_cut_20': 0.9978, 'map': 0.3661, 'recall_1000': 0.5951, 'judged_cut_1000': 0.3229},
    }
    evaluate_runs(round4_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.final-r4.fusion1.txt':
            {'topics': 45, 'ndcg_cut_10': 0.5632, 'judged_cut_10': 0.8578, 'ndcg_cut_20': 0.5211,
             'judged_cut_20': 0.7933, 'map': 0.2657, 'recall_1000': 0.6573, 'judged_cut_1000': 0.1474},
        'anserini.final-r4.fusion1.post-processed.txt':
            {'topics': 45, 'ndcg_cut_10': 0.5658, 'judged_cut_10': 0.8578, 'ndcg_cut_20': 0.5244,
             'judged_cut_20': 0.7978, 'map': 0.2666, 'recall_1000': 0.6571, 'judged_cut_1000': 0.1475},
        'anserini.final-r4.fusion2.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6389, 'judged_cut_10': 0.9778, 'ndcg_cut_20': 0.6054,
             'judged_cut_20': 0.8989, 'map': 0.3077, 'recall_1000': 0.6928, 'judged_cut_1000': 0.1559},
        'anserini.final-r4.fusion2.post-processed.txt':
            {'topics': 45, 'ndcg_cut_10': 0.6428, 'judged_cut_10': 0.9844, 'ndcg_cut_20': 0.6089,
             'judged_cut_20': 0.9022, 'map': 0.3088, 'recall_1000': 0.6928, 'judged_cut_1000': 0.1559},
        'anserini.final-r4.rf.txt':
            {'topics': 45, 'ndcg_cut_10': 0.7471, 'judged_cut_10': 0.9756, 'ndcg_cut_20': 0.6960,
             'judged_cut_20': 0.9244, 'map': 0.3504, 'recall_1000': 0.6960, 'judged_cut_1000': 0.1408},
        'anserini.final-r4.rf.post-processed.txt':
            {'topics': 45, 'ndcg_cut_10': 0.7516, 'judged_cut_10': 0.9867, 'ndcg_cut_20': 0.6976,
             'judged_cut_20': 0.9278, 'map': 0.3519, 'recall_1000': 0.6962, 'judged_cut_1000': 0.1409},
    }
    evaluate_runs(round4_qrels, final_runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
