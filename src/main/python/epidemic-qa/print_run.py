"""Script to print Epidemic QA runs into a human-readable format."""

import argparse
import csv

import collections
import json
from pathlib import Path
from os import path

from typing import Dict
from tqdm import tqdm

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

def get_document_title(path_to_toplevel_docs_directory, filename):
    """
    Returns the Python object corresponding to the document's parsed JSON.
    """

    if path.exists(path_to_toplevel_docs_directory + "/" + filename +".json"):
        filepath = path_to_toplevel_docs_directory + "/" + filename +".json"
    else:
    # The final-round consumer primary corpus has nested subdirectories
    # breaking up documents by search.  Also, some of the consumer documents 
    # are suffixed with an additional GUID.  Therefore, we need to do a global
    # search for the file if the initial search doesn't work.
        filepaths = list(Path(path_to_toplevel_docs_directory).rglob(filename+"*"))
        if len(filepaths) == 0:
            print("Unable to find document named " + filename)
            return ""
        elif len(filepaths) > 1:
            print("Multiple paths found for document named " + filename)

        filepath = filepaths[0]
    
    with open(filepath) as f:
        raw_json = f.read()
        parsed_json = json.loads(raw_json)
    metadata = parsed_json["metadata"]
    # Some consumer documents don't have titles.
    if metadata["title"]:
        return metadata["title"]
    return ""


parser = argparse.ArgumentParser(
    description='Print Epidemic QA runs into a human readable format.')
parser.add_argument('--queries', required=True, help='The path to the queries file.')
parser.add_argument('--run', required=True, help='The path to the run file.')
parser.add_argument('--docs-path', required=True, help='The path to the directory containing the document JSON files')
parser.add_argument('--docs-per-query', type=int, default=10, help='Number of documents per query to print.')

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
        title = get_document_title(args.docs_path, doc_id)
        output = [str(rank + 1), doc_id, title]
        print(' | '.join(output))
    print('-' * 50)
