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

"""Perform Anserini baseline runs for TREC-COVID Round 5."""

import os
import sys

from covid_baseline_tools import perform_runs, perform_fusion, prepare_final_submissions, \
    evaluate_runs, verify_stored_runs

# This makes errors more readable,
# see https://stackoverflow.com/questions/27674602/hide-traceback-unless-a-debug-flag-is-set
sys.tracebacklimit = 0

indexes = ['indexes/lucene-index-cord19-abstract-2020-07-16',
           'indexes/lucene-index-cord19-full-text-2020-07-16',
           'indexes/lucene-index-cord19-paragraph-2020-07-16']

cumulative_runs = {
    'anserini.covid-r5.abstract.qq.bm25.txt': 'b1ccc364cc9dab03b383b71a51d3c6cb',
    'anserini.covid-r5.abstract.qdel.bm25.txt': 'ee4e3e6cf87dba2fd021fbb89bd07a89',
    'anserini.covid-r5.full-text.qq.bm25.txt': 'd7457dd746533326f2bf8e85834ecf5c',
    'anserini.covid-r5.full-text.qdel.bm25.txt': '8387e4ad480ec4be7961c17d2ea326a1',
    'anserini.covid-r5.paragraph.qq.bm25.txt': '62d713a1ed6a8bf25c1454c66182b573',
    'anserini.covid-r5.paragraph.qdel.bm25.txt':  '16b295fda9d1eccd4e1fa4c147657872',
    'anserini.covid-r5.fusion1.txt': '16875b6d32a9b5ef96d7b59315b101a7',
    'anserini.covid-r5.fusion2.txt': '8f7d663d551f831c65dceb8e4e9219c2',
    'anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt': '909ccbbd55736eff60c7dbeff1404c94'
}

final_runs = {
    'anserini.final-r5.fusion1.txt': '12122c12089c2b07a8f6c7247aebe2f6',
    'anserini.final-r5.fusion1.post-processed.txt': 'f1ebdd7f7b8403b53e89a5993fb55dd2',
    'anserini.final-r5.fusion2.txt': 'ff1a0bac315de6703b937c552b351e2a',
    'anserini.final-r5.fusion2.post-processed.txt': '77ce612916becbb5ccfd6d891f797d1d',
    'anserini.final-r5.rf.txt': '74e2a73b5ffd2908dc23b14c765171a1',
    'anserini.final-r5.rf.post-processed.txt': 'dd765fa9491c585476735115eb966ea2'
}

stored_runs = {
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.abstract.qq.bm25.txt':
        cumulative_runs['anserini.covid-r5.abstract.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.abstract.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r5.abstract.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.full-text.qq.bm25.txt':
        cumulative_runs['anserini.covid-r5.full-text.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.full-text.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r5.full-text.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.paragraph.qq.bm25.txt':
        cumulative_runs['anserini.covid-r5.paragraph.qq.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.paragraph.qdel.bm25.txt':
        cumulative_runs['anserini.covid-r5.paragraph.qdel.bm25.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.fusion1.txt':
        cumulative_runs['anserini.covid-r5.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.fusion2.txt':
        cumulative_runs['anserini.covid-r5.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt':
        cumulative_runs['anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.fusion1.txt':
        final_runs['anserini.final-r5.fusion1.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.fusion2.txt':
        final_runs['anserini.final-r5.fusion2.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.rf.txt':
        final_runs['anserini.final-r5.rf.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.fusion1.post-processed.txt':
        final_runs['anserini.final-r5.fusion1.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.fusion2.post-processed.txt':
        final_runs['anserini.final-r5.fusion2.post-processed.txt'],
    'https://git.uwaterloo.ca/jimmylin/covidex-trec-covid-runs/raw/master/round5/anserini.final-r5.rf.post-processed.txt':
        final_runs['anserini.final-r5.rf.post-processed.txt'],
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round4_cumulative_qrels = 'tools/topics-and-qrels/qrels.covid-round4-cumulative.txt'
    complete_qrels = 'tools/topics-and-qrels/qrels.covid-complete.txt'
    round5_qrels = 'tools/topics-and-qrels/qrels.covid-round5.txt'

    # MD5 checksums don't match anymore, see https://github.com/castorini/anserini/issues/1669
    check_md5_flag = False

    verify_stored_runs(stored_runs)
    perform_runs(5, indexes)
    perform_fusion(5, cumulative_runs, check_md5=check_md5_flag)
    prepare_final_submissions(5, final_runs, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r5.abstract.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4569, 'judged_cut_10': 0.5860, 'ndcg_cut_20': 0.4417,
             'judged_cut_20': 0.5930, 'map': 0.1904, 'recall_1000': 0.4525, 'judged_cut_1000': 0.2264},
        'anserini.covid-r5.abstract.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4903, 'judged_cut_10': 0.6180, 'ndcg_cut_20': 0.4597,
             'judged_cut_20': 0.6030, 'map': 0.2041, 'recall_1000': 0.4714, 'judged_cut_1000': 0.2351},
        'anserini.covid-r5.full-text.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.3240, 'judged_cut_10': 0.5660, 'ndcg_cut_20': 0.3055,
             'judged_cut_20': 0.5250, 'map': 0.1324, 'recall_1000': 0.3758, 'judged_cut_1000': 0.2171},
        'anserini.covid-r5.full-text.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4634, 'judged_cut_10': 0.6460, 'ndcg_cut_20': 0.4387,
             'judged_cut_20': 0.6280, 'map': 0.1793, 'recall_1000': 0.4368, 'judged_cut_1000': 0.2425},
        'anserini.covid-r5.paragraph.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4061, 'judged_cut_10': 0.6160, 'ndcg_cut_20': 0.3900,
             'judged_cut_20': 0.5910, 'map': 0.1980, 'recall_1000': 0.4877, 'judged_cut_1000': 0.2661},
        'anserini.covid-r5.paragraph.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4905, 'judged_cut_10': 0.6440, 'ndcg_cut_20': 0.4569,
             'judged_cut_20': 0.6250, 'map': 0.2163, 'recall_1000': 0.5099, 'judged_cut_1000': 0.2710},
        'anserini.covid-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4696, 'judged_cut_10': 0.6540, 'ndcg_cut_20': 0.4525,
             'judged_cut_20': 0.6500, 'map': 0.2042, 'recall_1000': 0.5027, 'judged_cut_1000': 0.2750},
        'anserini.covid-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5057, 'judged_cut_10': 0.6800, 'ndcg_cut_20': 0.4954,
             'judged_cut_20': 0.6690, 'map': 0.2303, 'recall_1000': 0.5377, 'judged_cut_1000': 0.2851},
        'anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6207, 'judged_cut_10': 0.6560, 'ndcg_cut_20': 0.5782,
             'judged_cut_20': 0.6470, 'map': 0.2656, 'recall_1000': 0.5509, 'judged_cut_1000': 0.2563},
    }
    evaluate_runs(round4_cumulative_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.covid-r5.abstract.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6899, 'judged_cut_10': 0.9760, 'ndcg_cut_20': 0.6557,
             'judged_cut_20': 0.9700, 'map': 0.3009, 'recall_1000': 0.4636, 'judged_cut_1000': 0.4159},
        'anserini.covid-r5.abstract.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7260, 'judged_cut_10': 0.9960, 'ndcg_cut_20': 0.6975,
             'judged_cut_20': 0.9900, 'map': 0.3229, 'recall_1000': 0.4838, 'judged_cut_1000': 0.4286},
        'anserini.covid-r5.full-text.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.4709, 'judged_cut_10': 0.8920, 'ndcg_cut_20': 0.4382,
             'judged_cut_20': 0.8370, 'map': 0.1777, 'recall_1000': 0.3427, 'judged_cut_1000': 0.3397},
        'anserini.covid-r5.full-text.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6286, 'judged_cut_10': 0.9840, 'ndcg_cut_20': 0.5973,
             'judged_cut_20': 0.9630, 'map': 0.2391, 'recall_1000': 0.4087, 'judged_cut_1000': 0.3875},
        'anserini.covid-r5.paragraph.qq.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5832, 'judged_cut_10': 0.9600, 'ndcg_cut_20': 0.5656,
             'judged_cut_20': 0.9390, 'map': 0.2808, 'recall_1000': 0.4695, 'judged_cut_1000': 0.4412},
        'anserini.covid-r5.paragraph.qdel.bm25.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6764, 'judged_cut_10': 0.9840, 'ndcg_cut_20': 0.6367,
             'judged_cut_20': 0.9740, 'map': 0.3089, 'recall_1000': 0.4949, 'judged_cut_1000': 0.4542},
        'anserini.covid-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6488, 'judged_cut_10': 0.9860, 'ndcg_cut_20': 0.6190,
             'judged_cut_20': 0.9800, 'map': 0.2952, 'recall_1000': 0.4966, 'judged_cut_1000': 0.4674},
        'anserini.covid-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6946, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.6787,
             'judged_cut_20': 1.000, 'map': 0.3329, 'recall_1000': 0.5313, 'judged_cut_1000': 0.4869},
        'anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.8406, 'judged_cut_10': 1.0000, 'ndcg_cut_20': 0.7965,
             'judged_cut_20': 1.000, 'map': 0.3912, 'recall_1000': 0.5540, 'judged_cut_1000': 0.4610},
    }
    evaluate_runs(complete_qrels, cumulative_runs, expected=expected_metrics, check_md5=check_md5_flag)

    expected_metrics = {
        'anserini.final-r5.fusion1.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5665, 'judged_cut_10': 0.9140, 'ndcg_cut_20': 0.5247,
             'judged_cut_20': 0.8490, 'map': 0.2302, 'recall_1000': 0.5616, 'judged_cut_1000': 0.2149},
        'anserini.final-r5.fusion1.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.5726, 'judged_cut_10': 0.9240, 'ndcg_cut_20': 0.5313,
             'judged_cut_20': 0.8570, 'map': 0.2314, 'recall_1000': 0.5615, 'judged_cut_1000': 0.2151},
        'anserini.final-r5.fusion2.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6387, 'judged_cut_10': 0.9620, 'ndcg_cut_20': 0.5955,
             'judged_cut_20': 0.9090, 'map': 0.2719, 'recall_1000': 0.6013, 'judged_cut_1000': 0.2263},
        'anserini.final-r5.fusion2.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.6474, 'judged_cut_10': 0.9780, 'ndcg_cut_20': 0.6007,
             'judged_cut_20': 0.9150, 'map': 0.2734, 'recall_1000': 0.6012, 'judged_cut_1000': 0.2267},
        'anserini.final-r5.rf.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7789, 'judged_cut_10': 0.9700, 'ndcg_cut_20': 0.7188,
             'judged_cut_20': 0.9270, 'map': 0.3234, 'recall_1000': 0.6378, 'judged_cut_1000': 0.2197},
        'anserini.final-r5.rf.post-processed.txt':
            {'topics': 50, 'ndcg_cut_10': 0.7944, 'judged_cut_10': 0.9860, 'ndcg_cut_20': 0.7346,
             'judged_cut_20': 0.9470, 'map': 0.3280, 'recall_1000': 0.6378, 'judged_cut_1000': 0.2201},
    }
    evaluate_runs(round5_qrels, final_runs, expected=expected_metrics, check_md5=check_md5_flag)


if __name__ == '__main__':
    main()
