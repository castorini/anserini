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

"""Compute various statistics for qrels."""

import argparse
import collections


def is_relevant(grade: int, threshold: int):
    return grade > threshold


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=lambda prog: argparse.HelpFormatter(prog, width=100))
    parser.add_argument('--qrels', metavar='FILE', type=str, required=True, help='Qrels file.')
    parser.add_argument('--output', metavar='FILE', type=str, help='File to store per-topic statistics.')
    parser.add_argument('--relevance-threshold', metavar='INT', type=int, default=0,
                        help='Threshold for considering a judgment "relevant". ' +
                             'Default is zero, which means any relevance grade above zero is considered relevant.')

    args = parser.parse_args()

    qrels = collections.defaultdict(dict)
    with open(args.qrels) as f:
        for i, line in enumerate(f):
            qid, _, docid, relevance = line.rstrip().split()
            qrels[qid][docid] = int(relevance)

    total_judgments_count = 0
    total_topics_count = 0
    total_rel_count = 0

    max_per_topic_judged = 0
    min_per_topic_judged = 10000000

    max_per_topic_relevant = 0
    min_per_topic_relevant = 10000000

    per_topic_output = ['qid,rel_count,not_rel_count,total']

    for qid in sorted(qrels):
        total_topics_count += 1
        rel_count = 0
        not_rel_count = 0
        for docid in qrels[qid]:
            if is_relevant(qrels[qid][docid], args.relevance_threshold):
                rel_count += 1
                total_rel_count += 1
            else:
                not_rel_count += 1
            total_judgments_count += 1

        assert len(qrels[qid]) == rel_count + not_rel_count

        if rel_count > max_per_topic_relevant:
            max_per_topic_relevant = rel_count

        if rel_count + not_rel_count > max_per_topic_judged:
            max_per_topic_judged = rel_count + not_rel_count

        if rel_count < min_per_topic_relevant:
            min_per_topic_relevant = rel_count

        if rel_count + not_rel_count < min_per_topic_judged:
            min_per_topic_judged = rel_count + not_rel_count

        per_topic_output.append(f'{qid},{rel_count},{not_rel_count},{rel_count+not_rel_count}')

    per_topic_output.append(f'total,{total_rel_count},{total_judgments_count-total_rel_count},{total_judgments_count}')
    per_topic_output.append(f'avg,{total_rel_count/total_topics_count:.2f},' +
                            f'{(total_judgments_count-total_rel_count)/total_topics_count:.2f},' +
                            f'{total_judgments_count/total_topics_count:.2f}')

    if args.output:
        with open(args.output, 'w') as f:
            for line in per_topic_output:
                f.write(line + '\n')

    print('# Summary Statistics')
    print(f'Total number of topics: {total_topics_count}')
    print(f'Total number of judgments: {total_judgments_count}')
    print(f'Total number of relevant labels: {total_rel_count}')
    print(f'Avg. judgments per topic: {total_judgments_count/total_topics_count:.2f}' +
          f' (max = {max_per_topic_judged}, min = {min_per_topic_judged})')
    print(f'Avg. relevant labels per topic: {total_rel_count/total_topics_count:.2f}' +
          f' (max = {max_per_topic_relevant}, min = {min_per_topic_relevant})')


if __name__ == "__main__":
    main()
