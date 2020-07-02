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

import pyserini.util


def perform_runs():
    base_topics = 'src/main/resources/topics-and-qrels/topics.covid-round4.xml'
    udel_topics = 'src/main/resources/topics-and-qrels/topics.covid-round4-udel.xml'

    print('')
    print('## Running on abstract index...')
    print('')

    abstract_index = indexes[0]
    abstract_prefix = 'anserini.covid-r4.abstract'
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
    print('## Running on paragraph index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'anserini.covid-r4.full-text'
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
    paragraph_prefix = 'anserini.covid-r4.paragraph'
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

    set1 = ['anserini.covid-r4.abstract.qq.bm25.txt',
            'anserini.covid-r4.full-text.qq.bm25.txt',
            'anserini.covid-r4.paragraph.qq.bm25.txt']

    os.system('python src/main/python/fusion.py --method RRF --max_docs 10000 ' +
              '--out runs/anserini.covid-r4.fusion1.txt ' +
              f'--runs runs/{set1[0]} runs/{set1[1]} runs/{set1[2]}')

    set2 = ['anserini.covid-r4.abstract.qdel.bm25.txt',
            'anserini.covid-r4.full-text.qdel.bm25.txt',
            'anserini.covid-r4.paragraph.qdel.bm25.txt']

    os.system('python src/main/python/fusion.py --method RRF --max_docs 10000 ' +
              '--out runs/anserini.covid-r4.fusion2.txt ' +
              f'--runs runs/{set2[0]} runs/{set2[1]} runs/{set2[2]}')


def prepare_final_submissions(qrels):
    print('')
    print('## Preparing final submission files by removing qrels...')
    print('')

    run1 = 'anserini.final-r4.fusion1.txt'
    print(f'Generating {run1}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r4.fusion1.txt --output runs/{run1} --runtag r4.fusion1')

    run2 = 'anserini.final-r4.fusion2.txt'
    print(f'Generating {run2}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r4.fusion2.txt --output runs/{run2} --runtag r4.fusion2')

    run3 = 'anserini.final-r4.rf.txt'
    print(f'Generating {run3}')
    os.system(f'python tools/scripts/filter_run_with_qrels.py --discard --qrels {qrels} ' +
              f'--input runs/anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt --output runs/{run3} --runtag r4.rf')

    print('')
    print(f'{run1} checksum=' + hashlib.md5(open(f'runs/{run1}', 'rb').read()).hexdigest())
    print(f'{run2} checksum=' + hashlib.md5(open(f'runs/{run2}', 'rb').read()).hexdigest())
    print(f'{run3} checksum=' + hashlib.md5(open(f'runs/{run3}', 'rb').read()).hexdigest())


indexes = ['indexes/lucene-index-cord19-abstract-2020-06-19',
           'indexes/lucene-index-cord19-full-text-2020-06-19',
           'indexes/lucene-index-cord19-paragraph-2020-06-19']

stored_runs = {
    'https://www.dropbox.com/s/mf79huhxfy96g6i/anserini.covid-r4.abstract.qq.bm25.txt?dl=1':
        '56ac5a0410e235243ca6e9f0f00eefa1',
    'https://www.dropbox.com/s/4zau6ejrkvgn9m7/anserini.covid-r4.abstract.qdel.bm25.txt?dl=1':
        '115d6d2e308b47ffacbc642175095c74',
    'https://www.dropbox.com/s/bpdopie6gqffv0w/anserini.covid-r4.full-text.qq.bm25.txt?dl=1':
        'af0d10a5344f4007e6781e8d2959eb54',
    'https://www.dropbox.com/s/rh0uy71ogbpas0v/anserini.covid-r4.full-text.qdel.bm25.txt?dl=1':
        '594d469b8f45cf808092a3d8e870eaf5',
    'https://www.dropbox.com/s/ifkjm8ff8g2aoh1/anserini.covid-r4.paragraph.qq.bm25.txt?dl=1':
        '6f468b7b60aaa05fc215d237b5475aec',
    'https://www.dropbox.com/s/keuogpx1dzinsgy/anserini.covid-r4.paragraph.qdel.bm25.txt?dl=1':
        'b7b39629c12573ee0bfed8687dacc743',
    'https://www.dropbox.com/s/zjc0069do0a4gu3/anserini.covid-r4.fusion1.txt?dl=1':
        '8ae9d1fca05bd1d9bfe7b24d1bdbe270',
    'https://www.dropbox.com/s/qekc9vr3oom777n/anserini.covid-r4.fusion2.txt?dl=1':
        'e1894209c815c96c6ddd4cacb578261a',
    'https://www.dropbox.com/s/2jx27rh3lknps9q/anserini.covid-r4.abstract.qdel.bm25%2Brm3Rf.txt?dl=1':
        '9d954f31e2f07e11ff559bcb14ef16af',
    'https://www.dropbox.com/s/g3giixyusk4tzro/anserini.final-r4.fusion1.txt?dl=1':
        'a8ab52e12c151012adbfc8e37d666760',
    'https://www.dropbox.com/s/z4wbqj9gfos8wln/anserini.final-r4.fusion2.txt?dl=1':
        '1500104c928f463f38e76b58b91d4c07',
    'https://www.dropbox.com/s/28w83b07yzndlbg/anserini.final-r4.rf.txt?dl=1':
        '41d746eb86a99d2f33068ebc195072cd'
}


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    verify_stored_runs(stored_runs)

    perform_runs()
    perform_fusion()
    prepare_final_submissions('src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt')

    runs = ['anserini.covid-r4.abstract.qq.bm25.txt',
            'anserini.covid-r4.abstract.qdel.bm25.txt',
            'anserini.covid-r4.full-text.qq.bm25.txt',
            'anserini.covid-r4.full-text.qdel.bm25.txt',
            'anserini.covid-r4.paragraph.qq.bm25.txt',
            'anserini.covid-r4.paragraph.qdel.bm25.txt',
            'anserini.covid-r4.fusion1.txt',
            'anserini.covid-r4.fusion2.txt',
            'anserini.covid-r4.abstract.qdel.bm25+rm3Rf.txt']

    evaluate_runs('src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt', runs)


if __name__ == '__main__':
    main()
