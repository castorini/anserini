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

from covid_baseline_tools import perform_runs, perform_fusion, prepare_final_submissions,\
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
    'https://www.dropbox.com/s/g80cqdxud1l06wq/anserini.covid-r3.abstract.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.abstract.qq.bm25.txt'],
    'https://www.dropbox.com/s/sjcnxq7h0a3j3xz/anserini.covid-r3.abstract.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.abstract.qdel.bm25.txt'],
    'https://www.dropbox.com/s/4bjx35sgosu0jz0/anserini.covid-r3.full-text.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.full-text.qq.bm25.txt'],
    'https://www.dropbox.com/s/mjt7y1ywae784d0/anserini.covid-r3.full-text.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.full-text.qdel.bm25.txt'],
    'https://www.dropbox.com/s/qwn7jd8vg2chjik/anserini.covid-r3.paragraph.qq.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.paragraph.qq.bm25.txt'],
    'https://www.dropbox.com/s/2928i60fj2i09bt/anserini.covid-r3.paragraph.qdel.bm25.txt?dl=1':
        cumulative_runs['anserini.covid-r3.paragraph.qdel.bm25.txt'],
    'https://www.dropbox.com/s/6vk5iohqf81iy8b/anserini.covid-r3.fusion1.txt?dl=1':
        cumulative_runs['anserini.covid-r3.fusion1.txt'],
    'https://www.dropbox.com/s/n09595t1eqymkks/anserini.covid-r3.fusion2.txt?dl=1':
        cumulative_runs['anserini.covid-r3.fusion2.txt'],
    'https://www.dropbox.com/s/p8fzefgwzkvvbxx/anserini.covid-r3.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        cumulative_runs['anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt'],
    'https://www.dropbox.com/s/ypoe9tgwef17rak/anserini.final-r3.fusion1.txt?dl=1':
        final_runs['anserini.final-r3.fusion1.txt'],
    'https://www.dropbox.com/s/uvfrssp6nw2v2jl/anserini.final-r3.fusion2.txt?dl=1':
        final_runs['anserini.final-r3.fusion2.txt'],
    'https://www.dropbox.com/s/2wrg7ceaca3n7ac/anserini.final-r3.rf.txt?dl=1':
        final_runs['anserini.final-r3.rf.txt'],
    'https://www.dropbox.com/s/ilqgky1tti0zvez/anserini.final-r3.fusion1.post-processed.txt?dl=1':
        final_runs['anserini.final-r3.fusion1.post-processed.txt'],
    'https://www.dropbox.com/s/ue3z6xxxca9krkb/anserini.final-r3.fusion2.post-processed.txt?dl=1':
        final_runs['anserini.final-r3.fusion2.post-processed.txt'],
    'https://www.dropbox.com/s/95vk831wp1ldnpm/anserini.final-r3.rf.post-processed.txt?dl=1':
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

    verify_stored_runs(stored_runs)
    perform_runs(3, indexes)
    perform_fusion(3, cumulative_runs, check_md5=True)
    prepare_final_submissions(3, final_runs, check_md5=True)

    evaluate_runs(round2_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round3_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round3_qrels, final_runs, check_md5=True)


if __name__ == '__main__':
    main()
