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

import hashlib
import os
import sys

from covid_baseline_tools import evaluate_runs, verify_stored_runs

sys.path.insert(0, './')
sys.path.insert(0, '../pyserini/')

from pyserini.util import compute_md5


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
    'anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt': 'f4e9128320182308b09c0eb5f0ac016d'
}

final_runs = {
    'anserini.final-r5.fusion1.txt': '12122c12089c2b07a8f6c7247aebe2f6',
    'anserini.final-r5.fusion2.txt': 'ff1a0bac315de6703b937c552b351e2a',
    'anserini.final-r5.rf.txt': '9b9f77840f2cb529c54dde8cbf477c87'
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
        final_runs['anserini.final-r5.rf.txt']
}


def perform_runs():
    base_topics = 'src/main/resources/topics-and-qrels/topics.covid-round5.xml'
    udel_topics = 'src/main/resources/topics-and-qrels/topics.covid-round5-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = 'anserini.covid-r5.abstract'
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
              f'-rf.qrels src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt ' +
              f'-output runs/{abstract_prefix}.qdel.bm25+rm3Rf.txt -runtag {abstract_prefix}.qdel.bm25+rm3Rf.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'anserini.covid-r5.full-text'
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
    paragraph_prefix = 'anserini.covid-r5.paragraph'
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -strip_segment_id -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qq.bm25.txt -runtag {paragraph_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -strip_segment_id -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qdel.bm25.txt -runtag {paragraph_prefix}.qdel.bm25.txt')


def perform_fusion():
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = 'anserini.covid-r5.fusion1.txt'
    set1 = ['anserini.covid-r5.abstract.qq.bm25.txt',
            'anserini.covid-r5.full-text.qq.bm25.txt',
            'anserini.covid-r5.paragraph.qq.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    assert compute_md5(f'runs/{fusion_run1}') == cumulative_runs[fusion_run1], f'Error in producing {fusion_run1}!'

    fusion_run2 = 'anserini.covid-r5.fusion2.txt'
    set2 = ['anserini.covid-r5.abstract.qdel.bm25.txt',
            'anserini.covid-r5.full-text.qdel.bm25.txt',
            'anserini.covid-r5.paragraph.qdel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')

    assert compute_md5(f'runs/{fusion_run2}') == cumulative_runs[fusion_run2], f'Error in producing {fusion_run2}!'


def prepare_final_submissions(qrels):
    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = 'anserini.final-r5.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r5.fusion1.txt --output runs/{run1} --runtag r5.fusion1')
    run1_md5 = compute_md5(f'runs/{run1}')
    assert run1_md5 == final_runs[run1], f'Error in producing {run1}!'

    run2 = 'anserini.final-r5.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r5.fusion2.txt --output runs/{run2} --runtag r5.fusion2')
    run2_md5 = compute_md5(f'runs/{run2}')
    assert run2_md5 == final_runs[run2], f'Error in producing {run2}!'

    run3 = 'anserini.final-r5.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r5.rf')
    run3_md5 = compute_md5(f'runs/{run3}')
    assert run3_md5 == final_runs[run3], f'Error in producing {run3}!'

    print('')
    print(run1 + ' ' * (35 - len(run1)) + run1_md5)
    print(run2 + ' ' * (35 - len(run2)) + run2_md5)
    print(run3 + ' ' * (35 - len(run3)) + run3_md5)


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'

    verify_stored_runs(stored_runs)
    perform_runs()
    perform_fusion()
    prepare_final_submissions(cumulative_qrels)
    evaluate_runs(cumulative_qrels, cumulative_runs)


if __name__ == '__main__':
    main()
