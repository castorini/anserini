import collections
import json
import xml.etree.cElementTree as ET

from tqdm import tqdm
from typing import Dict


def load_corpus(path):
    print('Loading corpus...')
    corpus = {}
    with open(path) as f:
        for line in tqdm(f):
            obj = json.loads(line.strip())
            corpus[obj['id']] = obj
    print(f'Loaded {len(corpus)} docs')
    return corpus


def load_queries(path: str) -> Dict[str, str]:
    queries = collections.OrderedDict()
    with open(path) as f:
        text = f.read()
        tree = ET.ElementTree(ET.fromstring(text))
        root = tree.getroot()
        for topic in root:
            query_id = topic.attrib['number']

            query = topic.find('.//query').text
            question = topic.find('.//question').text
            # narrative = topic.find('.//narrative').text

            queries[query_id] = (query, question)

    return queries


def load_run(path):
    """Loads run into a dict of key: query_id, value: list of candidate doc
    ids."""
    print('Loading run...')
    run = collections.OrderedDict()
    with open(path) as f:
        for line in tqdm(f):
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
