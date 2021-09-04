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
    'https://www.dropbox.com/s/sa6abjrk1esxn38/expanded.anserini.covid-r5.abstract.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qq.bm25.txt'],
    'https://www.dropbox.com/s/t3s3oj9g0b1nphk/expanded.anserini.covid-r5.abstract.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qdel.bm25.txt'],
    'https://www.dropbox.com/s/utvw91nluzwm3ex/expanded.anserini.covid-r5.full-text.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.full-text.qq.bm25.txt'],
    'https://www.dropbox.com/s/xk2jyiwh5fjdwst/expanded.anserini.covid-r5.full-text.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.full-text.qdel.bm25.txt'],
    'https://www.dropbox.com/s/rjbyljcpziv31xx/expanded.anserini.covid-r5.paragraph.qq.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.paragraph.qq.bm25.txt'],
    'https://www.dropbox.com/s/f4h2jhhla4o26wr/expanded.anserini.covid-r5.paragraph.qdel.bm25.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.paragraph.qdel.bm25.txt'],
    'https://www.dropbox.com/s/bj00pfwngi2j2g1/expanded.anserini.covid-r5.fusion1.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.fusion1.txt'],
    'https://www.dropbox.com/s/f5ro0ex38gkvnqc/expanded.anserini.covid-r5.fusion2.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.fusion2.txt'],
    'https://www.dropbox.com/s/j6op32bcaszd1up/expanded.anserini.covid-r5.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        cumulative_runs['expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt'],
    'https://www.dropbox.com/s/5ke2c4x2z8de31h/expanded.anserini.final-r5.fusion1.txt?dl=1':
        final_runs['expanded.anserini.final-r5.fusion1.txt'],
    'https://www.dropbox.com/s/j1qdqr88cbsybae/expanded.anserini.final-r5.fusion2.txt?dl=1':
        final_runs['expanded.anserini.final-r5.fusion2.txt'],
    'https://www.dropbox.com/s/5bm4pdngh5bx3px/expanded.anserini.final-r5.rf.txt?dl=1':
        final_runs['expanded.anserini.final-r5.rf.txt'],
    'https://www.dropbox.com/s/ojphpgilqs8xexc/expanded.anserini.final-r5.fusion1.post-processed.txt?dl=1':
        final_runs['expanded.anserini.final-r5.fusion1.post-processed.txt'],
    'https://www.dropbox.com/s/q7vx0l8n2u81s7z/expanded.anserini.final-r5.fusion2.post-processed.txt?dl=1':
        final_runs['expanded.anserini.final-r5.fusion2.post-processed.txt'],
    'https://www.dropbox.com/s/l4l1bbbi8msmrfh/expanded.anserini.final-r5.rf.post-processed.txt?dl=1':
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


def perform_fusion():
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

    assert generate_md5 or compute_md5(f'runs/{fusion_run1}') == cumulative_runs[fusion_run1], f'Error in producing {fusion_run1}!'

    fusion_run2 = 'expanded.anserini.covid-r5.fusion2.txt'
    set2 = ['expanded.anserini.covid-r5.abstract.qdel.bm25.txt',
            'expanded.anserini.covid-r5.full-text.qdel.bm25.txt',
            'expanded.anserini.covid-r5.paragraph.qdel.bm25.txt']

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

    run1 = 'expanded.anserini.final-r5.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.fusion1.txt --output runs/{run1} --runtag r5.fusion1')
    run1_md5 = compute_md5(f'runs/{run1}')
    assert generate_md5 or run1_md5 == final_runs[run1], f'Error in producing {run1}!'

    run2 = 'expanded.anserini.final-r5.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.fusion2.txt --output runs/{run2} --runtag r5.fusion2')
    run2_md5 = compute_md5(f'runs/{run2}')
    assert generate_md5 or run2_md5 == final_runs[run2], f'Error in producing {run2}!'

    run3 = 'expanded.anserini.final-r5.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/expanded.anserini.covid-r5.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r5.rf')
    run3_md5 = compute_md5(f'runs/{run3}')
    assert generate_md5 or run3_md5 == final_runs[run3], f'Error in producing {run3}!'

    if generate_md5:
        final_md5 = {run: compute_md5(f'runs/{run}') for run in final_runs}
        print(f'Checksums for final runs: {final_md5}')


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    round4_cumulative_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt'
    complete_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-complete.txt'
    round5_qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round5.txt'

    verify_stored_runs(stored_runs)
    perform_runs()
    perform_fusion()
    prepare_final_submissions(round4_cumulative_qrels)

    evaluate_runs(round4_cumulative_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(complete_qrels, cumulative_runs, check_md5=True)
    evaluate_runs(round5_qrels, final_runs, check_md5=True)


if __name__ == '__main__':
    main()
