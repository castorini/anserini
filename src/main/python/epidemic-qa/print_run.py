"""Script to print Epidemic QA runs into a human-readable format."""

import argparse
import csv
from tqdm import tqdm

import collections
import json

from typing import Dict


def load_queries(path: str):
    """
    Loads queries into a dictionary of query_id -> (query, question)
    """
    queries = collections.OrderedDict()
    with open(path) as f:
        raw_json = f.read()
        parsed_json = json.loads(raw_json)
        for topic in parsed_json:
            # Question ID is the integer question ID prefixed with "CQ"
            # or "EQ" depending on whether it's a consumer or expert question.
            # This has to be parsed out so it can be joined against the run
            # file.
            question_id_string = topic["question_id"]
            # The prefix is of length 2.
            question_id = int(question_id_string[2:])
            question = topic["question"]
            query = topic["query"]
            queries[question_id] = (query, question)

    return queries


def load_run(path):
    """
    Loads run into a dict of key: query_id, value: list of (candidate doc
    ids, rank).
    """
    print('Loading run...')
    run = collections.OrderedDict()
    with open(path) as f:
        for line in tqdm(f):
            query_id, _, doc_id, rank, _, _ = line.split()
            query_id = int(query_id)
            if query_id not in run:
                run[query_id] = []
            run[query_id].append((doc_id, int(rank)))

    # Sort candidate docs by rank.
    sorted_run = collections.OrderedDict()
    for query_id, doc_ids_ranks in run.items():
        sorted(doc_ids_ranks, key=lambda x: x[1])
        doc_ids = [doc_id for doc_id, _ in doc_ids_ranks]
        sorted_run[query_id] = doc_ids

    return sorted_run

def get_document_title(path):
    """
    Returns the Python object corresponding to the document's parsed JSON.
    """
    with open(path) as f:
        raw_json = f.read()
        parsed_json = json.loads(raw_json)
    metadata = parsed_json["metadata"]
    return metadata["title"]

parser = argparse.ArgumentParser(
    description='Print Epidemic QA runs into a human readable format.')
parser.add_argument('--queries', required=True, help='The path to the queries file.')
parser.add_argument('--run', required=True, help='The path to the run file.')
parser.add_argument('--docs-path', required=True, help='The path to the directory containing the document JSON files')
parser.add_argument('--docs-per-query', type=int, default=5, help='Number of documents per query to print.')

args = parser.parse_args()
print(args)

queries = load_queries(args.queries)
run = load_run(args.run)
for query_id, (query, question) in queries.items():
    if query_id not in run:
        print(f'>> Missing query_id: {query_id}')
        continue

    print(f'query id: {query_id} | query: {query} | question: {question}')
    output = 'rank | doc_id | title'
    print(output)
    for rank, doc_id in enumerate(run[query_id][:args.docs_per_query]):
        title = get_document_title(args.docs_path+"/"+doc_id+".json")
        output = [str(rank + 1), doc_id, title]
        print(' | '.join(output))
    print('-' * 50)
