import argparse
import logging
import os
import pickle
import time

from scipy.sparse import save_npz, csr_matrix
import sklearn.preprocessing
import numpy as np

from utils import TfidfTgzReader

topic_list = [
    '321', '336', '341',
    '347', '350', '362',
    '363', '367', '375', '378', '393',
    '397', '400', '408', '414',
    '422', '426', '427', '433',
    '439', '442', '445', '626', '646',
    '690'
  ]

def read_vocab(path):
  """

  """
  logging.info("loading vocabulary dictionary...")
  with open(path, 'rb') as f:
    vocab = pickle.load(f)

  return vocab

def build_docid_idx_dict(rank_file):
  """

  """
  logging.info('building docid idx dict...')
  cur_idx = 0
  docid_idx_dict = {}
  with open(rank_file, 'r') as f:
    for line in f:
      topic, _, docid, _, _, _ = line.split(' ')
      if topic in topic_list and docid not in docid_idx_dict:
        docid_idx_dict[docid] = cur_idx
        docid_idx_dict[cur_idx] = docid
        cur_idx += 1
  return docid_idx_dict

def write_docid_idx_dict(docid_idx_dict, filename):
  """

  """
  logging.info(f"writting docid-idx-dict to {filename}")
  with open(filename, 'wb') as f:
    pickle.dump(docid_idx_dict, f)


def build_tfidf_matrix(tfidf_raw, docid_idx_dict, vocab_idx_dict):
  """

  """
  num_docs, num_vocabs = len(docid_idx_dict) // 2, len(vocab_idx_dict) // 2
  logging.info(f'start building tfidf sparse matrix with {num_docs} docs and {num_vocabs} vocabs...')

  tfidf_dict = {}
  count = 0

  reader = TfidfTgzReader(tfidf_raw)
  while reader.hasnextdoc():
    docid = reader.getnextdoc().strip()
    count += 1
    if count % 100000 == 0:
      logging.info(f'{count} files have been processed...')
    
    if docid not in docid_idx_dict:
      reader.skipdoc()
      continue
      
    doc_idx = docid_idx_dict[docid]
    while reader.hasnexttfidf():
      word, tfidf = reader.getnexttfidf()
      if word in vocab_idx_dict:
        vocab_idx = vocab_idx_dict[word]
        tfidf_dict[(doc_idx, vocab_idx)] = float(tfidf)

  logging.info(f'finish building tfidf dict, {count} files in total.')

  indices = tuple(zip(*tfidf_dict.keys()))
  values = list(tfidf_dict.values())
  tfidf_sp = csr_matrix((values, indices), shape=(num_docs, num_vocabs), dtype=np.float32)
  logging.info(f'finish building tfidf sparse matrix.')
  return sklearn.preprocessing.normalize(tfidf_sp, norm='l2')

def _safe_mkdir(path):
  if not os.path.exists(path):
    os.makedirs(path)

if __name__ == '__main__':
  logging.basicConfig(level=logging.DEBUG, 
  format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S ')
  start_time = time.time()

  parser = argparse.ArgumentParser()
  parser.add_argument("--tfidf-file", '-t', type=str, 
    help='path to tfidf file', required=True)
  parser.add_argument("--rank-file", '-r', type=str, 
    help='path to qrels_file', required=True)
  parser.add_argument("--vocab-folder", '-v', type=str, 
    help='folder contains vocab-idx-dict.pkl', required=True)
  parser.add_argument("--output-folder", '-o', type=str, 
    help='output folder to dump training data for each topic', required=True)

  args = parser.parse_args()

  tfidf_raw = args.tfidf_file
  rank_file = args.rank_file
  out_folder = args.output_folder
  vocab_folder = args.vocab_folder

  # sanity check
  assert os.path.isdir(vocab_folder)

  # constant
  vocab_path = os.path.join(vocab_folder, 'vocab-idx-dict.pkl')
  out_docid_idx_file = os.path.join(out_folder, 'test-docid-idx-dict.pkl')
  out_feature_file = os.path.join(out_folder, 'test.npz')

  # preprocessing
  _safe_mkdir(out_folder)

  # pipeline from here
  logging.info(f'start building test...')

  vocab_dict = read_vocab(vocab_path)
  docid_idx_dict = build_docid_idx_dict(rank_file)
  tfidf_sp = build_tfidf_matrix(tfidf_raw, docid_idx_dict, vocab_dict)

  write_docid_idx_dict(docid_idx_dict, out_docid_idx_file)

  logging.info(f'writing test data to {out_feature_file}...')
  save_npz(out_feature_file, tfidf_sp)

  logging.info(f'build test finished in {time.time() - start_time} seconds')

