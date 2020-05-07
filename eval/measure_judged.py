# -*- coding: utf-8 -*-
"""
Anserini: A Lucene toolkit for replicable information retrieval research

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

import argparse
import collections

from typing import Dict
from typing import List
from typing import Set


def load_qrels(path: str) -> Dict[str, Set[str]]:
    """Loads qrels into a dict of key: query_id, value: set of relevant doc
    ids."""
    qrels = collections.defaultdict(set)
    with open(path) as f:
        for i, line in enumerate(f):
            line = ' '.join(line.split())
            query_id, _, doc_id, relevance = line.rstrip().split(' ')
            qrels[query_id].add(doc_id)

    return qrels


def load_run(path: str) -> Dict[str, List[str]]:
    """Loads run into a dict of key: query_id, value: list of candidate doc
    ids."""
    run = collections.OrderedDict()
    with open(path) as f:
        for line in f:
            query_id, _, doc_title, rank, _, _ = line.split(' ')
            if query_id not in run:
                run[query_id] = []
            run[query_id].append((doc_title, int(rank)))

    # Sort candidate docs by rank.
    sorted_run = collections.OrderedDict()
    for query_id, doc_titles_ranks in run.items():
        sorted(doc_titles_ranks, key=lambda x: x[1])
        doc_titles = [doc_titles for doc_titles, _ in doc_titles_ranks]
        sorted_run[query_id] = doc_titles

    return sorted_run


if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description='measure the percentage of judged documents at various '
                    'cutoffs.')
    parser.add_argument('--qrels', type=str, required=True, help='qrels file')
    parser.add_argument('--run', type=str, required=True, help='run file')
    parser.add_argument('--cutoffs', nargs='+', type=int,
                        default=[5, 10, 20, 30],
                        help='Space-separate list of cutoffs. '
                             'E.g.: --cutoffs 5 10 20')

    args = parser.parse_args()

    qrels = load_qrels(args.qrels)
    run = load_run(args.run)

    for max_rank in args.cutoffs:
        percentage_judged = 0

        for query_id, doc_ids in run.items():
            doc_ids = doc_ids[:max_rank]
            n_judged = len(set(doc_ids).intersection(qrels[query_id]))
            percentage_judged += n_judged / len(doc_ids)

        percentage_judged /= max(1, len(run))
        print(f'judged@{max_rank}: {percentage_judged}')

    print('\nDone')
