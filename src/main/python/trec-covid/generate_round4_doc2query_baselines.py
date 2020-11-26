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

generate_md5 = False

indexes = ['indexes/lucene-index-cord19-abstract-docT5query-2020-06-19',
           'indexes/lucene-index-cord19-full-text-docT5query-2020-06-19',
           'indexes/lucene-index-cord19-paragraph-docT5query-2020-06-19']

cumulative_runs = {
    'expanded.anserini.covid-r4.abstract.qq.bm25.txt': 'd1d32cd6962c4e355a47e7f1fdfb0c74',
    'expanded.anserini.covid-r4.abstract.qdel.bm25.txt': '55ae93b92bae20ed64fc9f191c6ea667',
    'expanded.anserini.covid-r4.full-text.qq.bm25.txt': '512e14c6d15eb36f7fc9c537281badd3',
    'expanded.anserini.covid-r4.full-text.qdel.bm25.txt': '0901d7b083aa28afd431cf330fe7293c',
    'expanded.anserini.covid-r4.paragraph.qq.bm25.txt': 'f8512ba33d5cc79176d71424d05f81cb',
    'expanded.anserini.covid-r4.paragraph.qdel.bm25.txt': '123896c0af4cdbae471c21d2da7de1f7',
    'expanded.anserini.covid-r4.fusion1.txt': '77b619a2e6e87852b85d31637ceb6219',
    'expanded.anserini.covid-r4.fusion2.txt': '1e7bb2a6e483d3629378c3107457b216',
    'expanded.anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt': 'b6b1d949fff00e54b13e533e27455731'
}

final_runs = {
    'expanded.anserini.final-r4.fusion1.txt': 'ae7513f68e2ca82d8b0efdd244082046',
    'expanded.anserini.final-r4.fusion2.txt': '590400c12b72ce8ed3b5af2f4c45f039',
    'expanded.anserini.final-r4.rf.txt': 'b9e7bb80fd8dc97f93908d895fb07f7f'
}

stored_runs = {
    'https://www.dropbox.com/s/yxapvqec9o2ucon/expanded.anserini.covid-r4.abstract.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.abstract.qq.bm25.txt'],
    'https://www.dropbox.com/s/vnk3swwwfcncolk/expanded.anserini.covid-r4.abstract.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.abstract.qdel.bm25.txt'],
    'https://www.dropbox.com/s/pkk3m90bv0rpxru/expanded.anserini.covid-r4.full-text.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.full-text.qq.bm25.txt'],
    'https://www.dropbox.com/s/44hoa9xkf6tv0hq/expanded.anserini.covid-r4.full-text.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.full-text.qdel.bm25.txt'],
    'https://www.dropbox.com/s/z90xag7eh5pi53e/expanded.anserini.covid-r4.paragraph.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.paragraph.qq.bm25.txt'],
    'https://www.dropbox.com/s/eno3z8pi7bnfy2p/expanded.anserini.covid-r4.paragraph.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.paragraph.qdel.bm25.txt'],
    'https://www.dropbox.com/s/zfbt15ivm37tolt/expanded.anserini.covid-r4.fusion1.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.fusion1.txt'],
    'https://www.dropbox.com/s/e7ki5e8jqi718bp/expanded.anserini.covid-r4.fusion2.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.fusion2.txt'],
    'https://www.dropbox.com/s/1uzy5ni33kvxq2o/expanded.anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt'],
    'https://www.dropbox.com/s/mjgb5lz9ftty1w2/expanded.anserini.final-r4.fusion1.txt?dl=1':
        final_runs['expanded.anserini.final-r4.fusion1.txt'],
    'https://www.dropbox.com/s/5epunmkexqtupe6/expanded.anserini.final-r4.fusion2.txt?dl=1':
        final_runs['expanded.anserini.final-r4.fusion2.txt'],
    'https://www.dropbox.com/s/kqbu3cui214ijyh/expanded.anserini.final-r4.rf.txt?dl=1':
        final_runs['expanded.anserini.final-r4.rf.txt']
}


def perform_runs(cumulative_qrels):
    base_topics = 'src/main/resources/topics-and-qrels/topics.covid-round4.xml'
    udel_topics = 'src/main/resources/topics-and-qrels/topics.covid-round4-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = 'expanded.anserini.covid-r4.abstract'
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
              f'-rf.qrels {cumulative_qrels} ' +
              f'-output runs/{abstract_prefix}.qdel.bm25+rm3Rf.txt -runtag {abstract_prefix}.qdel.bm25+rm3Rf.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'expanded.anserini.covid-r4.full-text'
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
    paragraph_prefix = 'expanded.anserini.covid-r4.paragraph'
    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qq.bm25.txt -runtag {paragraph_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {paragraph_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-selectMaxPassage -bm25 -hits 50000 ' +
              f'-output runs/{paragraph_prefix}.qdel.bm25.txt -runtag {paragraph_prefix}.qdel.bm25.txt')


def perform_fusion():
    print('')
    print('## Performing fusion...')
    print('')

    fusion_run1 = 'expanded.anserini.covid-r4.fusion1.txt'
    set1 = ['expanded.anserini.covid-r4.abstract.qq.bm25.txt',
            'expanded.anserini.covid-r4.full-text.qq.bm25.txt',
            'expanded.anserini.covid-r4.paragraph.qq.bm25.txt']

    print(f'Performing fusion to create {fusion_run1}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 '
              f'--out runs/{fusion_run1} --runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    assert generate_md5 or compute_md5(f'runs/{fusion_run1}') == cumulative_runs[fusion_run1], f'Error in producing {fusion_run1}!'

    fusion_run2 = 'expanded.anserini.covid-r4.fusion2.txt'
    set2 = ['expanded.anserini.covid-r4.abstract.qdel.bm25.txt',
            'expanded.anserini.covid-r4.full-text.qdel.bm25.txt',
            'expanded.anserini.covid-r4.paragraph.qdel.bm25.txt']

    print(f'Performing fusion to create {fusion_run2}')
    os.system('PYTHONPATH=../pyserini ' +
              'python -m pyserini.fusion --method rrf --runtag reciprocal_rank_fusion_k=60 --k 10000 ' +
              f'--out runs/{fusion_run2} --runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')

    assert generate_md5 or compute_md5(f'runs/{fusion_run2}') == cumulative_runs[fusion_run2], f'Error in producing {fusion_run2}!'

    if generate_md5:
        cumulative_md5 = {run: compute_md5(f'runs/{run}') for run in cumulative_runs}
        print(f'Checksums for cumulative runs: {cumulative_md5}')


def prepare_final_submissions(qrels):
    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = 'expanded.anserini.final-r4.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r4.fusion1.txt --output runs/{run1} --runtag r4.fusion1')
    run1_md5 = compute_md5(f'runs/{run1}')
    assert generate_md5 or run1_md5 == final_runs[run1], f'Error in producing {run1}!'

    run2 = 'expanded.anserini.final-r4.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r4.fusion2.txt --output runs/{run2} --runtag r4.fusion2')
    run2_md5 = compute_md5(f'runs/{run2}')
    assert generate_md5 or run2_md5 == final_runs[run2], f'Error in producing {run2}!'

    run3 = 'expanded.anserini.final-r4.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r4.rf')
    run3_md5 = compute_md5(f'runs/{run3}')
    assert generate_md5 or run3_md5 == final_runs[run3], f'Error in producing {run3}!'

    if generate_md5:
        final_md5 = {run: compute_md5(f'runs/{run}') for run in final_runs}
        print(f'Checksums for final runs: {final_md5}')


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round3_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt'
    round4_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4.txt'
    round4_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'

    verify_stored_runs(stored_runs)
    perform_runs(round3_cumulative_qrels)
    perform_fusion()
    prepare_final_submissions(round3_cumulative_qrels)

    evaluate_runs(round4_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round4_qrels, final_runs, check_md5=True)


if __name__ == '__main__':
    main()
