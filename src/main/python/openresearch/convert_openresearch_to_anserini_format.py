import argparse
import gzip
import json
import numpy as np
import os
import time
from os import listdir
from os.path import isfile, join


def clean(text):
  return text.replace('\n', ' ').replace('\t', ' ')


def get_id_years(file_paths, train_fraction):
  print('Collecting paper ids and their publication years...')
  id_years = []
  for file_num, file_path in enumerate(file_paths):
    with gzip.open(file_path) as f:
      for line_num, line in enumerate(f):
        obj = json.loads(line.strip())
        doc_id = obj['id']
        in_citations = obj['inCitations']
        out_citations = obj['outCitations']

        # Following Bhagavatula et al. (2018, "Content-Based Citation 
        # Recommendation") we do not use papers with no citations and papers 
        # that cite less than 2 times.
        if len(in_citations) > 0 and len(out_citations) >= 2:
          if 'year' not in obj:
            continue
          year = int(obj['year'])

          id_years.append((doc_id, year))
        if line_num % 100000 == 0:
          print('Processed {} lines. Collected {} docs.'.format(
              line_num, len(id_years)))

  print('Sorting papers by year...')
  id_years.sort(key = lambda x: x[1])

  num_train = int(len(id_years) * train_fraction)
  train_ids = id_years[:num_train]
  num_dev = (len(id_years) - num_train) // 2
  dev_ids = id_years[num_train:num_train + num_dev]
  test_ids = id_years[num_train + num_dev:]

  train_ids = set(doc_id for doc_id, _ in train_ids)
  dev_ids = set(doc_id for doc_id, _ in dev_ids)
  test_ids = set(doc_id for doc_id, _ in test_ids)

  print('Collected {}, {}, {} papers for training, dev, and test sets.'.format(
      len(train_ids), len(dev_ids), len(test_ids)))

  return train_ids, dev_ids, test_ids


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
  
  file_names = listdir(args.collection_path)
  file_paths = []
  for file_name in file_names:
    file_path = join(args.collection_path, file_name)
    if not isfile(file_path):
      continue
    if not file_path.endswith('.gz'):
      continue
    file_paths.append(file_path)

  print('{} files found'.format(len(file_paths)))

  # We first need to collect papers by year, sort them, and split between 
  # training, dev, and test sets.
  train_ids, dev_ids, test_ids = get_id_years(
      file_paths=file_paths, train_fraction=args.train_fraction)

  number_citations_all = []
  n_docs = 0
  n_docs_used = 0
  file_index = 0
  num_train = 0
  num_dev = 0
  num_test = 0
  start_time = time.time()
  for file_num, file_path in enumerate(file_paths):
    with gzip.open(file_path) as f:
      for line in f:
        obj = json.loads(line.strip())
        doc_id = obj['id']
        doc_text = '[Title]: {} [Abstract]: {}'.format(
            obj['title'], obj['paperAbstract'])
        out_citations = obj['outCitations']
        in_citations = obj['inCitations']

        doc_text = clean(doc_text)

        if n_docs % args.max_docs_per_file == 0:
          if n_docs > 0:
            output_jsonl_file.close()
          output_path = os.path.join(
              args.output_folder, 'corpus/docs{:02d}.json'.format(file_index))
          output_jsonl_file = open(output_path, 'w')
          file_index += 1

        output_dict = {'id': doc_id, 'contents': doc_text}
        output_jsonl_file.write(json.dumps(output_dict) + '\n')
        n_docs += 1
        number_citations_all.append(len(in_citations))
        
        if doc_id in train_ids:
          set_name = 'train'
          num_train += 1  
        elif doc_id in dev_ids:
          set_name = 'dev'
          num_dev += 1
        elif doc_id in test_ids:
          set_name = 'test'
          num_test += 1
        else:
          continue

        queries_file = queries_files[set_name]
        qrels_file = qrels_files[set_name]

        doc_title = obj['title']
        doc_title = clean(doc_title)
        if args.use_abstract_in_query:
          doc_abstract = clean(obj['paperAbstract'])
          query = doc_title + ' [SEP] ' + doc_abstract
        else:
          query = doc_title
        queries_file.write('{}\t{}\n'.format(doc_id, query))
        for out_citation in out_citations:
          qrels_file.write('{} 0 {} 1\n'.format(doc_id, out_citation))

        n_docs_used += 1

        if n_docs % 100000 == 0:
          print('Read {}/{} files. {} lines converted in {} files in {} secs.'.format(
              file_num, len(file_paths), n_docs, file_index, 
              int(time.time() - start_time)))
          print('Total examples: {} ({} train, {} valid, {} test)'.format(
              n_docs_used, num_train, num_dev, num_test))

  # Close queries and qrels files.
  for queries_file in queries_files.values():
    queries_file.close()
  for qrels_file in qrels_files.values():
    qrels_file.close()


if __name__ == '__main__':
  parser = argparse.ArgumentParser(description='''Converts Open Research Corpus jsonl collection to Anserini's jsonl files.''')
  parser.add_argument('--collection_path', required=True, help='Open Research jsonl collection file')
  parser.add_argument('--output_folder', required=True, help='output file')
  parser.add_argument('--max_docs_per_file', default=1000000, type=int, help='maximum number of documents in each jsonl file.')
  parser.add_argument('--train_fraction', default=0.8, type=float, help='Fraction of the whole dataset that will be used for training. Data is sorted by year in the first train_fraction is used for training, the remaining is evenly split between dev and test sets.')
  parser.add_argument('--use_abstract_in_query', action='store_true', help='If given, use title and a abstract as query. Otherwise, use only title.')

  args = parser.parse_args()

  if not os.path.exists(args.output_folder):
      os.makedirs(args.output_folder)
      os.makedirs(os.path.join(args.output_folder, 'corpus'))

  create_dataset(args)
  print('Done!')
