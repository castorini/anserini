import argparse
import gzip
import json
import os
import time
from collections import defaultdict


def clean(text):
    return text.replace('\n', ' ').replace('\t', ' ')


def get_ids(start, end, year_ids):
    result = []
    for year in range(start, end+1):
        result.extend(year_ids[year])
    return set(result)


def get_id_years(file_name, data_type):
    print('Collecting paper ids and their publication years...')
    year_ids = defaultdict(list)
    with open(file_name) as f:
        for line_num, line in enumerate(f):
            obj = json.loads(line.strip())
            doc_id = obj['id']
            if 'year' not in obj:
                continue
            year = int(obj['year'])

            year_ids[year].append(doc_id)
            if line_num % 1000000 == 0:
                print('Processed {} lines. Collected {} docs.'.format(
                    line_num + 1, len(year_ids)))

    train_ranges = {'dblp': (1966, 2007), 'pubmed': (1966, 2008)}
    dev_ranges = {'dblp': (2008, 2008), 'pubmed': (2009, 2009)}
    test_ranges = {'dblp': (2009, 2011), 'pubmed': (2010, 2013)}

    train_ids = get_ids(train_ranges[data_type][0], train_ranges[data_type][1], year_ids)
    dev_ids = get_ids(dev_ranges[data_type][0], dev_ranges[data_type][1], year_ids)
    test_ids = get_ids(test_ranges[data_type][0], test_ranges[data_type][1], year_ids)

    num_train = len(train_ids)
    num_dev = len(dev_ids)
    num_test = len(test_ids)

    print('Collected {}, {}, {} papers for training, dev, and test sets.'.format(
        num_train, num_dev, num_test))
    
    return train_ids, dev_ids, test_ids, year_ids


def create_dataset(args):
    print('Converting data...')
    queries_files = {}
    qrels_files = {}
    for set_name in ['train', 'dev', 'test']:
        queries_filepath = os.path.join(
            args.output_folder, 'queries.{}.tsv'.format(set_name))
        qrels_filepath = os.path.join(
            args.output_folder, 'qrels.{}'.format(set_name))
        queries_files[set_name] = open(queries_filepath, 'w')
        qrels_files[set_name] = open(qrels_filepath, 'w')

    file_name = os.path.join(args.collection_path, 'corpus.json')

    train_ids, dev_ids, test_ids, year_ids = get_id_years(
        file_name=file_name, data_type=args.data_type)
    
    doc_ids = train_ids | dev_ids | test_ids

    # Write train_ids to file for future use
    candidates_file = open(os.path.join(args.output_folder, 'candidates.txt'), 'w')
    for train_id in train_ids:
        candidates_file.write(train_id+'\n')

    id_years = {}
    for y in year_ids:
        for i in year_ids[y]:
            id_years[i] = y

    n_docs = 0
    file_index = 0
    num_train = 0
    num_dev = 0
    num_test = 0
    start_time = time.time()

    with open(file_name) as f:
        for line in f:
            obj = json.loads(line.strip())
            doc_id = obj['id']
            if doc_id not in doc_ids:
                continue
            if n_docs % args.max_docs_per_file == 0:
                if n_docs > 0:
                    output_jsonl_file.close()
                output_path = os.path.join(
                    args.output_folder, 'corpus/docs{:02d}.json'.format(file_index))
                output_jsonl_file = open(output_path, 'w')
                file_index += 1
            doc_text = '[Title]: {} [Abstract]: {}'.format(
                obj['title'], obj['abstract'])
            doc_text = clean(doc_text)
            output_dict = {'id': doc_id, 'contents': doc_text}
            output_jsonl_file.write(json.dumps(output_dict) + '\n')
            n_docs += 1
    
            out_citations = obj['out_citations']
            
            # Remove citations not in the corpus.
            out_citations = [
            out_citation for out_citation in out_citations 
            if out_citation in doc_ids
            ]

            if doc_id in train_ids:
                if len(out_citations) == 0:
                    continue
                set_name = 'train'
                num_train += 1  
            elif doc_id in dev_ids:
                if len(out_citations) == 0:
                    continue
                set_name = 'dev'
                num_dev += 1
            elif doc_id in test_ids:
                # Remove self citations.
                out_citations = [
                out_citation for out_citation in out_citations 
                if out_citation != doc_id
                ]

                # Use only citations that have an older publication year than the citing
                # paper's or do not have an year.
                out_citations2 = []
                for out_citation in out_citations: 
                    if out_citation in id_years:
                        if id_years[out_citation] <= obj['year']:
                            out_citations2.append(out_citation)
                out_citations = out_citations2

                # Follow Bhagavatula's setting to restrict our citations candidates to train_ids only
                out_citations = set(out_citations)
                out_citations.intersection_update(train_ids)

                # Skip papers have out citations < 10.
                if len(out_citations) < 10:
                    continue
                    
                set_name = 'test'
                num_test += 1

            queries_file = queries_files[set_name]
            qrels_file = qrels_files[set_name]

            doc_title = obj['title']
            doc_title = clean(doc_title)
            if args.use_abstract_in_query:
                doc_abstract = clean(obj['abstract'])
                query = '[Title]: ' + doc_title + ' [Abstract]: ' + doc_abstract
            else:
                query = doc_title
            queries_file.write('{}\t{}\n'.format(doc_id, query))
            for out_citation in out_citations:
                qrels_file.write('{} 0 {} 1\n'.format(doc_id, out_citation))

        print('Examples: {} train, {} valid, {} test'.format(
                    num_train, num_dev, num_test))

    # Close queries and qrels files.
    for queries_file in queries_files.values():
        queries_file.close()
    for qrels_file in qrels_files.values():
        qrels_file.close()


if __name__ == '__main__':
    parser = argparse.ArgumentParser(
        description='Converts DBLP Corpus json collection to '
        'Anserini\'s jsonl files.')
    parser.add_argument('--collection_path', required=True, 
        help='DBLP json collection file')
    parser.add_argument('--output_folder', required=True, help='output file')
    parser.add_argument('--max_docs_per_file', default=1000000, type=int, 
        help='maximum number of documents in each jsonl file.')
    parser.add_argument('--data_type', required=True, default='dblp', help='dblp or pubmed')
    parser.add_argument('--use_abstract_in_query', action='store_true',
        help='If True use title and a abstract as query. If '
        'False, use only title.')

    args = parser.parse_args()

    if not os.path.exists(args.output_folder):
        os.makedirs(args.output_folder)
        os.makedirs(os.path.join(args.output_folder, 'corpus'))

    create_dataset(args)
    print('Done!')