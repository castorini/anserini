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

import hashlib
import os
import sys

from covid_baseline_tools import evaluate_runs

sys.path.insert(0, './')
sys.path.insert(0, '../pyserini/')

import pyserini.util


def perform_runs():
    base_topics = 'src/main/resources/topics-and-qrels/topics.covid-round3.xml'
    udel_topics = 'src/main/resources/topics-and-qrels/topics.covid-round3-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = 'anserini.covid-r3.abstract'
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
              f'-rf.qrels src/main/resources/topics-and-qrels/qrels.covid-round12.txt ' +
              f'-output runs/{abstract_prefix}.qdel.bm25+rm3Rf.txt -runtag {abstract_prefix}.qdel.bm25+rm3Rf.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'anserini.covid-r3.full-text'
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{full_text_prefix}.qq.bm25.txt -runtag {full_text_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{full_text_prefix}.qdel.bm25.txt -runtag {full_text_prefix}.qdel.bm25.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    paragraph_index = indexes[2]
    paragraph_prefix = 'anserini.covid-r3.paragraph'
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

    set1 = ['anserini.covid-r3.abstract.qq.bm25.txt',
            'anserini.covid-r3.full-text.qq.bm25.txt',
            'anserini.covid-r3.paragraph.qq.bm25.txt']

    os.system('python src/main/python/fusion.py --method RRF --max_docs 10000 ' +
              '--out runs/anserini.covid-r3.fusion1.txt ' +
              f'--runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    set2 = ['anserini.covid-r3.abstract.qdel.bm25.txt',
            'anserini.covid-r3.full-text.qdel.bm25.txt',
            'anserini.covid-r3.paragraph.qdel.bm25.txt']

    os.system('python src/main/python/fusion.py --method RRF --max_docs 10000 ' +
              '--out runs/anserini.covid-r3.fusion2.txt ' +
              f'--runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')


def prepare_final_submissions(qrels):
    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = 'anserini.final-r3.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r3.fusion1.txt --output runs/{run1} --runtag r3.fusion1')

    run2 = 'anserini.final-r3.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r3.fusion2.txt --output runs/{run2} --runtag r3.fusion2')

    run3 = 'anserini.final-r3.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r3.rf')

    print('')
    print(f'{run1} checksum=' + hashlib.md5(open(f'runs/{run1}', 'rb').read()).hexdigest())
    print(f'{run2} checksum=' + hashlib.md5(open(f'runs/{run2}', 'rb').read()).hexdigest())
    print(f'{run3} checksum=' + hashlib.md5(open(f'runs/{run3}', 'rb').read()).hexdigest())


indexes = ['indexes/lucene-index-cord19-abstract-2020-05-19',
           'indexes/lucene-index-cord19-full-text-2020-05-19',
           'indexes/lucene-index-cord19-paragraph-2020-05-19']


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    perform_runs()
    perform_fusion()
    prepare_final_submissions('src/main/resources/topics-and-qrels/qrels.covid-round12.txt')

    os.system('cat src/main/resources/topics-and-qrels/qrels.covid-round1.txt ' +
              'src/main/resources/topics-and-qrels/qrels.covid-round2.txt ' +
              '> src/main/resources/topics-and-qrels/qrels.covid-round12.txt')

    runs = ['anserini.covid-r3.abstract.qq.bm25.txt',
            'anserini.covid-r3.abstract.qdel.bm25.txt',
            'anserini.covid-r3.full-text.qq.bm25.txt',
            'anserini.covid-r3.full-text.qdel.bm25.txt',
            'anserini.covid-r3.paragraph.qq.bm25.txt',
            'anserini.covid-r3.paragraph.qdel.bm25.txt',
            'anserini.covid-r3.fusion1.txt',
            'anserini.covid-r3.fusion2.txt',
            'anserini.covid-r3.abstract.qdel.bm25+rm3Rf.txt']

    evaluate_runs('src/main/resources/topics-and-qrels/qrels.covid-round12.txt', runs)
    evaluate_runs('src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt', runs)

    # Download the NIST post-processed runs.
    print('')
    pyserini.util.download_url(
        'https://www.dropbox.com/s/ilqgky1tti0zvez/anserini.final-r3.fusion1.post-processed.txt?dl=1', 'runs')
    pyserini.util.download_url(
        'https://www.dropbox.com/s/ue3z6xxxca9krkb/anserini.final-r3.fusion2.post-processed.txt?dl=1', 'runs')
    pyserini.util.download_url(
        'https://www.dropbox.com/s/95vk831wp1ldnpm/anserini.final-r3.rf.post-processed.txt?dl=1', 'runs')

    runs = ['anserini.final-r3.fusion1.txt',
            'anserini.final-r3.fusion1.post-processed.txt',
            'anserini.final-r3.fusion2.txt',
            'anserini.final-r3.fusion2.post-processed.txt',
            'anserini.final-r3.rf.txt',
            'anserini.final-r3.rf.post-processed.txt']

    evaluate_runs('src/main/resources/topics-and-qrels/qrels.covid-round3.txt', runs)


if __name__ == '__main__':
    main()
