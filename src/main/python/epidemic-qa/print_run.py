"""Script to print Epidemic QA runs into a human-readable format."""

import argparse
import csv
from tqdm import tqdm

import collections
import json

from typing import Dict


def load_queries(path: str) -> collections.OrderedDict[str, (str, str)]:
    """
    Loads queries into a dictionary of query_id -> (query, question)
    """
    queries = collections.OrderedDict()
    with open(path) as f:
        raw_json = f.read()
        parsed_json = json.loads(raw_json)
        for topic in raw_json:
            query_id = topic

            queries[query_id] = (query, question)

    return queries


def load_run(path) -> Dict[str, [str]]:
    """Loads run into a dict of key: query_id, value: list of candidate doc
    ids."""
    print('Loading run...')
    run = collections.OrderedDict()
    with open(path) as f:
        for line in tqdm(f):
            query_id, _, doc_title, rank, _, _ = line.split()
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

parser = argparse.ArgumentParser(
    description='Print Epidemic QA runs into a human readable format.')
parser.add_argument('--queries', required=True, help='Queries file')
parser.add_argument('--run', required=True, help='Run file')
parser.add_argument('--k', type=int, default=10, help='number of documents per query to print.')
parser.add_argument('--abstract', action='store_true', default=False, help='Print abstract.')

args = parser.parse_args()

queries = load_queries(args.queries)
run = load_run(args.run)

for query_id, (query, question) in queries.items():
    if query_id not in run:
        print(f'>> Missing query_id: {query_id}')
        continue

    print(f'query id: {query_id} | query: {query} | question: {question}')
    output = 'rank | doc_id | title'
    if args.abstract:
        output += ' | abstract'
    print(output)
    for rank, doc_id in enumerate(run[query_id][:args.k]):
        title, abstract = metadata[doc_id]
        output = [str(rank + 1), doc_id, title]
        if args.abstract:
            output.append(abstract)
        print(' | '.join(output))
    print('-' * 50)
