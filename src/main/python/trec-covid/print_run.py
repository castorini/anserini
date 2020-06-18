"""Script to print TREC runs into a human readable format"""
import argparse
import csv
import utils
from tqdm import tqdm


def load_metadata(path):
    metadata = {}
    with open(path) as f:
        for row in tqdm(csv.DictReader(f)):
            metadata[row['cord_uid']] = row['title'], row['abstract']
    return metadata


parser = argparse.ArgumentParser(
    description='Print TREC runs into a human readable format.')
parser.add_argument('--queries', required=True, help='Queries file')
parser.add_argument('--run', required=True, help='Run file')
parser.add_argument('--metadata', required=True, help='Metadata file')
parser.add_argument('--k', type=int, default=10, help='number of documents per query to print.')
parser.add_argument('--abstract', action='store_true', default=False, help='Print abstract.')

args = parser.parse_args()

queries = utils.load_queries(args.queries)
run = utils.load_run(args.run)
metadata = load_metadata(args.metadata)

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
