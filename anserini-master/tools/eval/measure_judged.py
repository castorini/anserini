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

"""Compute the fraction of judged documents at various cutoffs."""

import argparse
import collections

from typing import Dict
from typing import List
from typing import Set


def load_qrels(path: str) -> Dict[str, Set[str]]:
    """Loads qrels into a dict of key: query_id, value: set of relevant doc ids."""
    qrels = collections.defaultdict(set)
    with open(path) as f:
        for i, line in enumerate(f):
            line = ' '.join(line.split())
            query_id, _, doc_id, relevance = line.rstrip().split()
            qrels[query_id].add(doc_id)

    return qrels


def load_run(path: str) -> Dict[str, List[str]]:
    """Loads run into a dict of key: query_id, value: list of candidate doc ids."""
    run = collections.OrderedDict()
    with open(path) as f:
        for line in f:
            query_id, _, doc_title, rank, _, _ = line.split()
            if query_id not in run:
                run[query_id] = []
            run[query_id].append((doc_title, int(rank)))

    # Sort candidate docs by rank.
    sorted_run = collections.OrderedDict()
    for query_id, doc_titles_ranks in run.items():
        doc_titles_ranks.sort(key=lambda x: x[1])
        doc_titles = [doc_titles for doc_titles, _ in doc_titles_ranks]
        sorted_run[query_id] = doc_titles

    return sorted_run


def main():
    parser = argparse.ArgumentParser(description=__doc__,
                                     formatter_class=lambda prog: argparse.HelpFormatter(prog, width=100))
    parser.add_argument('--qrels', metavar='FILE', type=str, required=True, help='Qrels file.')
    parser.add_argument('--run', metavar='FILE', type=str, required=True, help='Run file.')
    parser.add_argument('--cutoffs', metavar='N', nargs='+', type=int, default=[10, 100, 1000],
                        help='Space-separated list of cutoffs, e.g., --cutoffs 10 100 1000.')
    parser.add_argument('--q', '-q', action='store_true', dest='print_topic', help='Print metrics per topic.')
    parser.add_argument('--topics-in-qrels-only', action='store_true', help='Ignore unlisted topicIds in qrels')

    args = parser.parse_args()

    qrels = load_qrels(args.qrels)
    run = load_run(args.run)

    # Filters out topicIds from the run that are not in the qrels
    if args.topics_in_qrels_only:
        run = {key: value for key, value in run.items() if key in qrels}


    for max_rank in args.cutoffs:
        percentage_judged = 0

        for query_id, doc_ids in run.items():
            doc_ids = doc_ids[:max_rank]
            n_judged = len(set(doc_ids).intersection(qrels[query_id]))
            percentage_judged_per_topic = n_judged / len(doc_ids)
            if args.print_topic:
                print(f'judged_cut_{max_rank}\t{query_id}\t{percentage_judged_per_topic:.4f}')
            percentage_judged += percentage_judged_per_topic

        percentage_judged /= max(1, len(run))
        print(f'judged_cut_{max_rank}\tall\t{percentage_judged:.4f}')


if __name__ == "__main__":
    main()
