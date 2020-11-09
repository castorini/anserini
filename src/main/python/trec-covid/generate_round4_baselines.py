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
    'https://www.dropbox.com/s/mf79huhxfy96g6i/anserini.covid-r4.abstract.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.abstract.qq.bm25.txt'],
    'https://www.dropbox.com/s/4zau6ejrkvgn9m7/anserini.covid-r4.abstract.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.abstract.qdel.bm25.txt'],
    'https://www.dropbox.com/s/bpdopie6gqffv0w/anserini.covid-r4.full-text.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.full-text.qq.bm25.txt'],
    'https://www.dropbox.com/s/rh0uy71ogbpas0v/anserini.covid-r4.full-text.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.full-text.qdel.bm25.txt'],
    'https://www.dropbox.com/s/ifkjm8ff8g2aoh1/anserini.covid-r4.paragraph.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.paragraph.qq.bm25.txt'],
    'https://www.dropbox.com/s/keuogpx1dzinsgy/anserini.covid-r4.paragraph.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r4.paragraph.qdel.bm25.txt'],
    'https://www.dropbox.com/s/zjc0069do0a4gu3/anserini.covid-r4.fusion1.txt?dl=1':
        cumulative_runs['anserini.covid-r4.fusion1.txt'],
    'https://www.dropbox.com/s/qekc9vr3oom777n/anserini.covid-r4.fusion2.txt?dl=1':
        cumulative_runs['anserini.covid-r4.fusion2.txt'],
    'https://www.dropbox.com/s/2jx27rh3lknps9q/anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        cumulative_runs['anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt'],
    'https://www.dropbox.com/s/g3giixyusk4tzro/anserini.final-r4.fusion1.txt?dl=1':
        final_runs['anserini.final-r4.fusion1.txt'],
    'https://www.dropbox.com/s/z4wbqj9gfos8wln/anserini.final-r4.fusion2.txt?dl=1':
        final_runs['anserini.final-r4.fusion2.txt'],
    'https://www.dropbox.com/s/28w83b07yzndlbg/anserini.final-r4.rf.txt?dl=1':
        final_runs['anserini.final-r4.rf.txt'],
    'https://www.dropbox.com/s/wccmsmj2cz4h1t4/anserini.final-r4.fusion1.post-processed.txt?dl=1':
        final_runs['anserini.final-r4.fusion1.post-processed.txt'],
    'https://www.dropbox.com/s/kwgnbgofaql3k4l/anserini.final-r4.fusion2.post-processed.txt?dl=1':
        final_runs['anserini.final-r4.fusion2.post-processed.txt'],
    'https://www.dropbox.com/s/gvha3nj004osrme/anserini.final-r4.rf.post-processed.txt?dl=1':
        final_runs['anserini.final-r4.rf.post-processed.txt'],
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round3_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt'
    round4_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4.txt'
    round4_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'

    verify_stored_runs(stored_runs)
    perform_runs(4, indexes)
    perform_fusion(4, cumulative_runs, check_md5=True)
    prepare_final_submissions(4, final_runs)

    evaluate_runs(round3_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round4_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round4_qrels, final_runs, check_md5=True)


if __name__ == '__main__':
    main()
