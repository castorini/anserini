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
import subprocess


sys.path.insert(0, './')


def evaluate_run(run):
    qrels = 'src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt'
    metrics = {}
    output = subprocess.check_output(
        f'tools/eval/trec_eval.9.0.4/trec_eval -c -m ndcg_cut.10 -m recall.1000 {qrels} runs/{run}', shell=True)

    lines = output.decode('utf-8').split('\n')

    arr = lines[0].split()
    metrics[arr[0]] = float(arr[2])
    arr = lines[1].split()
    metrics[arr[0]] = float(arr[2])

    output = subprocess.check_output(f'python tools/eval/measure_judged.py --qrels {qrels} ' +
                                     f'--cutoffs 10 100 1000 --run runs/{run}', shell=True)

    arr = output.decode('utf-8').split()
    metrics[arr[0]] = float(arr[2])

    output = subprocess.check_output(f"cut -d ' ' -f 1 runs/{run} | sort | uniq | wc", shell=True)
    arr = output.decode('utf-8').split()
    metrics['topics'] = int(arr[0])

    return metrics


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
              f'-topicreader Covid -topics {udel_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{abstract_prefix}.qdel.bm25.txt -runtag {abstract_prefix}.qdel.bm25.txt')

    print('')
    print('## Running on full-text index...')
    print('')

    full_text_index = indexes[1]
    full_text_prefix = 'anserini.covid-r4.full-text'
    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {base_topics} -topicfield query+question ' +
              f'-removedups -bm25 -hits 10000 ' +
              f'-output runs/{full_text_prefix}.qq.bm25.txt -runtag {full_text_prefix}.qq.bm25.txt')

    os.system(f'target/appassembler/bin/SearchCollection -index {full_text_index} ' +
              f'-topicreader Covid -topics {udel_topics} -topicfield query+question ' +
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
              f'-topicreader Covid -topics {udel_topics} -topicfield query+question ' +
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


def evaluate_runs():
    qq_a_metrics = evaluate_run('anserini.covid-r4.abstract.qq.bm25.txt')
    qd_a_metrics = evaluate_run('anserini.covid-r4.abstract.qdel.bm25.txt')

    qq_f_metrics = evaluate_run('anserini.covid-r4.full-text.qq.bm25.txt')
    qd_f_metrics = evaluate_run('anserini.covid-r4.full-text.qdel.bm25.txt')

    qq_p_metrics = evaluate_run('anserini.covid-r4.paragraph.qq.bm25.txt')
    qd_p_metrics = evaluate_run('anserini.covid-r4.paragraph.qdel.bm25.txt')

    qq_u_metrics = evaluate_run('anserini.covid-r4.fusion1.txt')
    qd_u_metrics = evaluate_run('anserini.covid-r4.fusion2.txt')

    print(f'                                          topics   nDCG@10   Judged@10   Recall@1000')
    print(f'anserini.covid-r4.abstract.qq.bm25.txt' +
          f'      {qq_a_metrics["topics"]}      {qq_a_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qq_a_metrics["judged_cut_10"]:.4f}      {qq_a_metrics["recall_1000"]:.4f}')
    print(f'anserini.covid-r4.abstract.qdel.bm25.txt' +
          f'    {qd_a_metrics["topics"]}      {qd_a_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qd_a_metrics["judged_cut_10"]:.4f}      {qd_a_metrics["recall_1000"]:.4f}')

    print(f'anserini.covid-r4.full-text.qq.bm25.txt' +
          f'     {qq_f_metrics["topics"]}      {qq_f_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qq_f_metrics["judged_cut_10"]:.4f}      {qq_f_metrics["recall_1000"]:.4f}')
    print(f'anserini.covid-r4.full-text.qdel.bm25.txt' +
          f'   {qd_f_metrics["topics"]}      {qd_f_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qd_f_metrics["judged_cut_10"]:.4f}      {qd_f_metrics["recall_1000"]:.4f}')

    print(f'anserini.covid-r4.paragraph.qq.bm25.txt' +
          f'     {qq_p_metrics["topics"]}      {qq_p_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qq_p_metrics["judged_cut_10"]:.4f}      {qq_p_metrics["recall_1000"]:.4f}')
    print(f'anserini.covid-r4.paragraph.qdel.bm25.txt' +
          f'   {qd_p_metrics["topics"]}      {qd_p_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qd_p_metrics["judged_cut_10"]:.4f}      {qd_p_metrics["recall_1000"]:.4f}')

    print(f'anserini.covid-r4.fusion1.txt' +
          f'               {qq_u_metrics["topics"]}      {qq_u_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qq_u_metrics["judged_cut_10"]:.4f}      {qq_u_metrics["recall_1000"]:.4f}')
    print(f'anserini.covid-r4.fusion2.txt' +
          f'               {qd_u_metrics["topics"]}      {qd_u_metrics["ndcg_cut_10"]:.4f}' +
          f'    {qd_u_metrics["judged_cut_10"]:.4f}      {qd_u_metrics["recall_1000"]:.4f}')


indexes = ['indexes/lucene-index-cord19-abstract-2020-06-19',
           'indexes/lucene-index-cord19-full-text-2020-06-19',
           'indexes/lucene-index-cord19-paragraph-2020-06-19']


def main():
    if not (os.path.isdir(indexes[0]) and os.path.isdir(indexes[1]) and os.path.isdir(indexes[2])):
        print('Required indexes do not exist. Please download first.')

    perform_runs()
    perform_fusion()
    evaluate_runs()


if __name__ == '__main__':
    main()
