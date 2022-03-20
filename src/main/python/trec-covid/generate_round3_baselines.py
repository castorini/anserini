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

"""Perform Anserini baseline runs for TREC-COVID Round 3."""

import os
import sys

from covid_baseline_tools import perform_runs, perform_fusion, prepare_final_submissions, \
    evaluate_runs, verify_stored_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-cord19-abstract-2020-05-19',
           'indexes/lucene-index-cord19-full-text-2020-05-19',
           'indexes/lucene-index-cord19-paragraph-2020-05-19']

cumulative_runs = {
    'anserini.covid-r3.abstract.qq.bm25.txt': 'd08d85c87e30d6c4abf54799806d282f',
    'anserini.covid-r3.abstract.qdel.bm25.txt': 'd552dff90995cd860a5727637f0be4d1',
    'anserini.covid-r3.full-text.qq.bm25.txt': '6c9f4c09d842b887262ca84d61c61a1f',
    'anserini.covid-r3.full-text.qdel.bm25.txt': 'c5f9db7733c72eea78ece2ade44d3d35',
    'anserini.covid-r3.paragraph.qq.bm25.txt': '872673b3e12c661748d8899f24d3ba48',
    'anserini.covid-r3.paragraph.qdel.bm25.txt': 'c1b966e4c3f387b6810211f339b35852',
    'anserini.covid-r3.fusion1.txt': '61cbd73c6e60ba44f18ce967b5b0e5b3',
    'anserini.covid-r3.fusion2.txt': 'd7eabf3dab840104c88de925e918fdab',
    'anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt': 'e6a44f1f7183de10f892c6d922110934'
}

final_runs = {
    'anserini.final-r3.fusion1.txt': 'c1caf63a9c3b02f0b12e233112fc79a6',
    'anserini.final-r3.fusion1.post-processed.txt': 'f7c69c9bff381a847af86e5a8daf7526',
    'anserini.final-r3.fusion2.txt': '12679197846ed77306ecb2ca7895b011',
    'anserini.final-r3.fusion2.post-processed.txt': '84c5fd2c7de0a0282266033ac4f27c22',
    'anserini.final-r3.rf.txt': '7192a08c5275b59d5ef18395917ff694',
    'anserini.final-r3.rf.post-processed.txt': '3e79099639a9426cb53afe7066239011'
}

stored_runs = {
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.abstract.qq.bm25.txt':
        cumulative_runs['anserini.covid-r3.abstract.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.abstract.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r3.abstract.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.full-text.qq.bm25.txt':
        cumulative_runs['anserini.covid-r3.full-text.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.full-text.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r3.full-text.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.paragraph.qq.bm25.txt':
        cumulative_runs['anserini.covid-r3.paragraph.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.paragraph.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r3.paragraph.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.fusion1.txt':
        cumulative_runs['anserini.covid-r3.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.fusion2.txt':
        cumulative_runs['anserini.covid-r3.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.covid-r3.abstract.qdel.bm25%2Brm3Rf.txt':
        cumulative_runs['anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.fusion1.txt':
        final_runs['anserini.final-r3.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.fusion2.txt':
        final_runs['anserini.final-r3.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.rf.txt':
        final_runs['anserini.final-r3.rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.fusion1.post-processed.txt':
        final_runs['anserini.final-r3.fusion1.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.fusion2.post-processed.txt':
        final_runs['anserini.final-r3.fusion2.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round3/anserini.final-r3.rf.post-processed.txt':
        final_runs['anserini.final-r3.rf.post-processed.txt']
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    os.system('cat src/main/resources/topics-and-qrels/qrels.covid-round1.txt ' +
              'src/main/resources/topics-and-qrels/qrels.covid-round2.txt ' +
              '> src/main/resources/topics-and-qrels/qrels.covid-round2-cumulative.txt')

    round2_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round2-cumulative.txt'
    round3_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3.txt'
    round3_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt'

    # MD5 checksums don't match anymore, see https://github.com/castorini/anserini/issues/1669
    check_md5_flag = False

    verify_stored_runs(stored_runs)
    perform_runs(3, indexes)
    perform_fusion(3, cumulative_runs, check_md5=check_md5_flag)
    prepare_final_submissions(3, final_runs, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r3.abstract.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.2117, 'judged_cut_10': 0.3300, 'ndcg_cut_20': 0.2043,
             'judged_cut_20': 0.3150, 'map': 0.0951, 'recall_1000': 0.4398, 'judged_cut_1000': 0.1275},
        'anserini.covid-r3.abstract.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.2466, 'judged_cut_10': 0.3375, 'ndcg_cut_20': 0.2253,
             'judged_cut_20': 0.3175, 'map': 0.1023, 'recall_1000': 0.4537, 'judged_cut_1000': 0.1248},
        'anserini.covid-r3.full-text.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.2337, 'judged_cut_10': 0.4650, 'ndcg_cut_20': 0.2259,
             'judged_cut_20': 0.4425, 'map': 0.1099, 'recall_1000': 0.4817, 'judged_cut_1000': 0.1490},
        'anserini.covid-r3.full-text.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3430, 'judged_cut_10': 0.5025, 'ndcg_cut_20': 0.3077,
             'judged_cut_20': 0.4888, 'map': 0.1426, 'recall_1000': 0.5267, 'judged_cut_1000': 0.1575},
        'anserini.covid-r3.paragraph.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.2848, 'judged_cut_10': 0.5175, 'ndcg_cut_20': 0.2734,
             'judged_cut_20': 0.4938, 'map': 0.1390, 'recall_1000': 0.5527, 'judged_cut_1000': 0.1727},
        'anserini.covid-r3.paragraph.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3604, 'judged_cut_10': 0.5050, 'ndcg_cut_20': 0.3213,
             'judged_cut_20': 0.4875, 'map': 0.1520, 'recall_1000': 0.5676, 'judged_cut_1000': 0.1672},
        'anserini.covid-r3.fusion1.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3093, 'judged_cut_10': 0.4975, 'ndcg_cut_20': 0.2933,
             'judged_cut_20': 0.5025, 'map': 0.1400, 'recall_1000': 0.5566, 'judged_cut_1000': 0.1750},
        'anserini.covid-r3.fusion2.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3568, 'judged_cut_10': 0.5250, 'ndcg_cut_20': 0.3273,
             'judged_cut_20': 0.4925, 'map': 0.1564, 'recall_1000': 0.5769, 'judged_cut_1000': 0.1715},
        'anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3633, 'judged_cut_10': 0.3800, 'ndcg_cut_20': 0.3175,
             'judged_cut_20': 0.3600, 'map': 0.1526, 'recall_1000': 0.5722, 'judged_cut_1000': 0.1398},
    }
    evaluate_runs(round2_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r3.abstract.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5780, 'judged_cut_10': 0.8875, 'ndcg_cut_20': 0.5359,
             'judged_cut_20': 0.8325, 'map': 0.2348, 'recall_1000': 0.5040, 'judged_cut_1000': 0.2351},
        'anserini.covid-r3.abstract.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6289, 'judged_cut_10': 0.9300, 'ndcg_cut_20': 0.5971,
             'judged_cut_20': 0.8925, 'map': 0.2525, 'recall_1000': 0.5215, 'judged_cut_1000': 0.2370},
        'anserini.covid-r3.full-text.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.3977, 'judged_cut_10': 0.7500, 'ndcg_cut_20': 0.3681,
             'judged_cut_20': 0.7213, 'map': 0.1646, 'recall_1000': 0.4708, 'judged_cut_1000': 0.2471},
        'anserini.covid-r3.full-text.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5790, 'judged_cut_10': 0.9050, 'ndcg_cut_20': 0.5234,
             'judged_cut_20': 0.8525, 'map': 0.2236, 'recall_1000': 0.5313, 'judged_cut_1000': 0.2693},
        'anserini.covid-r3.paragraph.qq.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5396, 'judged_cut_10': 0.9425, 'ndcg_cut_20': 0.5079,
             'judged_cut_20': 0.9050, 'map': 0.2498, 'recall_1000': 0.5766, 'judged_cut_1000': 0.2978},
        'anserini.covid-r3.paragraph.qdel.bm25.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6327, 'judged_cut_10': 0.9600, 'ndcg_cut_20': 0.5793,
             'judged_cut_20': 0.9162, 'map': 0.2753, 'recall_1000': 0.5923, 'judged_cut_1000': 0.2956},
        'anserini.covid-r3.fusion1.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5924, 'judged_cut_10': 0.9625, 'ndcg_cut_20': 0.5563,
             'judged_cut_20': 0.9362, 'map': 0.2700, 'recall_1000': 0.5956, 'judged_cut_1000': 0.3045},
        'anserini.covid-r3.fusion2.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6515, 'judged_cut_10': 0.9875, 'ndcg_cut_20': 0.6200,
             'judged_cut_20': 0.9675, 'map': 0.3027, 'recall_1000': 0.6194, 'judged_cut_1000': 0.3076},
        'anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 40, 'ndcg_cut_10': 0.7459, 'judged_cut_10': 0.9875, 'ndcg_cut_20': 0.7023,
             'judged_cut_20': 0.9637, 'map': 0.3190, 'recall_1000': 0.6125, 'judged_cut_1000': 0.2600},
    }
    evaluate_runs(round3_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.final-r3.fusion1.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5339, 'judged_cut_10': 0.8400, 'ndcg_cut_20': 0.4875,
             'judged_cut_20': 0.7637, 'map': 0.2283, 'recall_1000': 0.6160, 'judged_cut_1000': 0.1370},
        'anserini.final-r3.fusion1.post-processed.txt':
            {'topics': 40, 'ndcg_cut_10': 0.5359, 'judged_cut_10': 0.8475, 'ndcg_cut_20': 0.4902,
             'judged_cut_20': 0.7675, 'map': 0.2293, 'recall_1000': 0.6160, 'judged_cut_1000': 0.1373},
        'anserini.final-r3.fusion2.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6072, 'judged_cut_10': 0.9025, 'ndcg_cut_20': 0.5599,
             'judged_cut_20': 0.8337, 'map': 0.2631, 'recall_1000': 0.6441, 'judged_cut_1000': 0.1431},
        'anserini.final-r3.fusion2.post-processed.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6100, 'judged_cut_10': 0.9100, 'ndcg_cut_20': 0.5617,
             'judged_cut_20': 0.8375, 'map': 0.2641, 'recall_1000': 0.6441, 'judged_cut_1000': 0.1434},
        'anserini.final-r3.rf.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6812, 'judged_cut_10': 0.9600, 'ndcg_cut_20': 0.6255,
             'judged_cut_20': 0.8450, 'map': 0.2787, 'recall_1000': 0.6399, 'judged_cut_1000': 0.1246},
        'anserini.final-r3.rf.post-processed.txt':
            {'topics': 40, 'ndcg_cut_10': 0.6883, 'judged_cut_10': 0.9750, 'ndcg_cut_20': 0.6321,
             'judged_cut_20': 0.8538, 'map': 0.2817, 'recall_1000': 0.6399, 'judged_cut_1000': 0.1250},
    }
    evaluate_runs(round3_qrels, final_runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
