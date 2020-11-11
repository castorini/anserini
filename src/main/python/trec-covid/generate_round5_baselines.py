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
    'https://www.dropbox.com/s/lbgevu4wiztd9e4/anserini.covid-r5.abstract.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.abstract.qq.bm25.txt'],
    'https://www.dropbox.com/s/pdy5o4xyalcnm2n/anserini.covid-r5.abstract.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.abstract.qdel.bm25.txt'],
    'https://www.dropbox.com/s/zhrkqvgbh6mwjdc/anserini.covid-r5.full-text.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.full-text.qq.bm25.txt'],
    'https://www.dropbox.com/s/4c3ifc8gt96qiio/anserini.covid-r5.full-text.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.full-text.qdel.bm25.txt'],
    'https://www.dropbox.com/s/xfx3g54map005sy/anserini.covid-r5.paragraph.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.paragraph.qq.bm25.txt'],
    'https://www.dropbox.com/s/nmb11wtx4yde939/anserini.covid-r5.paragraph.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r5.paragraph.qdel.bm25.txt'],
    'https://www.dropbox.com/s/mq94s9t7snqlizw/anserini.covid-r5.fusion1.txt?dl=1':
        cumulative_runs['anserini.covid-r5.fusion1.txt'],
    'https://www.dropbox.com/s/4za9i29gxv090ut/anserini.covid-r5.fusion2.txt?dl=1':
        cumulative_runs['anserini.covid-r5.fusion2.txt'],
    'https://www.dropbox.com/s/9cw0qhr5meskg9y/anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        cumulative_runs['anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt'],
    'https://www.dropbox.com/s/2uyws7fnbpxo8s6/anserini.final-r5.fusion1.txt?dl=1':
        final_runs['anserini.final-r5.fusion1.txt'],
    'https://www.dropbox.com/s/vyolaecpxu28vjw/anserini.final-r5.fusion2.txt?dl=1':
        final_runs['anserini.final-r5.fusion2.txt'],
    'https://www.dropbox.com/s/27wy54cibmyg7lp/anserini.final-r5.rf.txt?dl=1':
        final_runs['anserini.final-r5.rf.txt'],
    'https://www.dropbox.com/s/lycp9x404bp6u1l/anserini.final-r5.fusion1.post-processed.txt?dl=1':
        final_runs['anserini.final-r5.fusion1.post-processed.txt'],
    'https://www.dropbox.com/s/qtwny6bd6k3ijzq/anserini.final-r5.fusion2.post-processed.txt?dl=1':
        final_runs['anserini.final-r5.fusion2.post-processed.txt'],
    'https://www.dropbox.com/s/1ak8w2280dzrflu/anserini.final-r5.rf.post-processed.txt?dl=1':
        final_runs['anserini.final-r5.rf.post-processed.txt'],
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round4_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'
    complete_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-complete.txt'
    round5_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round5.txt'

    verify_stored_runs(stored_runs)
    perform_runs(5, indexes)
    perform_fusion(5, cumulative_runs, check_md5=True)
    prepare_final_submissions(5, final_runs, check_md5=True)

    evaluate_runs(round4_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(complete_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round5_qrels, final_runs, check_md5=True)


if __name__ == '__main__':
    main()
