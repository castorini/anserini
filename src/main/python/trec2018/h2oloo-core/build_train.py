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


# all files rank file folder
def build_docid_idx_and_label(rank_file_folder):
  """
  """
  logging.info(f'start building docid idx dict and topic docid label dict')
  cur_idx = 0
  docid_idx_dict = {}
  topic_docid_label = {topic:{} for topic in topic_list}

  rank_files = os.listdir(rank_file_folder)
  
  for rank_file in rank_files:
    logging.info(f'building docid idx dict for {rank_file}')
    with open(os.path.join(rank_file_folder, rank_file), 'r') as f:
      for line in f:
        topic, _, docid, label = line.strip().split(' ')
        if topic in topic_list:
          if docid not in docid_idx_dict:
            # build docid_idx_dict
            docid_idx_dict[docid] = cur_idx
            docid_idx_dict[cur_idx] = docid
            cur_idx += 1

          # update topic_docid_label
          topic_docid_label[topic][docid_idx_dict[docid]] = 0 if label == '0' else 1

  logging.info(f'finish building docid idx dict and topic docid label dict.')
  logging.info(f'{cur_idx} files found in total.')
  return docid_idx_dict, topic_docid_label

def build_vocab_tfidf_dict(tfidf_raw_folder, docid_idx_dict, isalpha):
  """

  """
  logging.info('start building vocab tfidf dict')
  vocab_idx_dict, tfidf_dict = {}, {}
  cur_idx = 0

  tfidf_raw_files = os.listdir(tfidf_raw_folder)
  for tfidf_raw in tfidf_raw_files:
    logging.info(f'start building vocab tfidf dict for {tfidf_raw}')
    count = 0

    reader = TfidfTgzReader(os.path.join(tfidf_raw_folder, tfidf_raw))
    while reader.hasnextdoc():
      docid = reader.getnextdoc().strip()
      count += 1
      if count % 100000 == 0:
        logging.info(f'{count} files have been processed...')
      
      if docid not in docid_idx_dict:
        # reader.skipdoc()
        continue
        
      doc_idx = docid_idx_dict[docid]
      while reader.hasnexttfidf():
        word, tfidf = reader.getnexttfidf()
        if not isalpha or word.isalpha():
          if word not in vocab_idx_dict:
            vocab_idx_dict[word] = cur_idx
            vocab_idx_dict[cur_idx] = word
            cur_idx += 1
          tfidf_dict[(doc_idx, vocab_idx_dict[word])] = float(tfidf)

  logging.info(f'finish building vocab tfidf dict, {cur_idx} words in total.')
  return vocab_idx_dict, tfidf_dict


def write_docid_idx_dict(docid_idx_dict, filename):
  """
  """
  logging.info(f"writting docid-idx-dict to {filename}")
  with open(filename, 'wb') as f:
    pickle.dump(docid_idx_dict, f)
    
def write_vocab_idx_dict(vocab_idx_dict, filename):
  """
  """
  logging.info(f"writting vocab-idx-dict to {filename}")
  with open(filename, 'wb') as f:
    pickle.dump(vocab_idx_dict, f)


def to_sparse(tfidf_dict, docid_idx_dict, vocab_idx_dict):
  """

  """
  logging.info("converting tfidf info to sparse matrix")
  num_docs, num_vocabs = len(docid_idx_dict) // 2, len(vocab_idx_dict) // 2
  indices = tuple(zip(*tfidf_dict.keys()))
  values = list(tfidf_dict.values())
  tfidf_sp = csr_matrix((values, indices), shape=(num_docs, num_vocabs), dtype=np.float32)

  return sklearn.preprocessing.normalize(tfidf_sp, norm='l2')

def write_train_feature(topic_docid_label: dict,
            feature_matrix: csr_matrix,
            docid_idx_dict: dict,
            out_path: str):
  logging.info("writting training data...")

  for topic, labels in topic_docid_label.items():
    logging.info(f'writing topic {topic}')
    X_idx, y = list(zip(*labels.items()))
    X, y = feature_matrix[X_idx, :], np.array(y)
    save_npz(os.path.join(out_path, f'{topic}.npz'), X)
    np.save(os.path.join(out_path, f'{topic}.npy'), np.array(y))

def _safe_mkdir(path):
  if not os.path.exists(path):
    os.makedirs(path)

if __name__ == '__main__':
  logging.basicConfig(level=logging.DEBUG, 
  format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S ')
  start_time = time.time()

  parser = argparse.ArgumentParser()
  parser.add_argument("--tfidf-folder", '-t', type=str, 
    help='path to tfidf file folder', required=True)
  parser.add_argument("--qrels-folder", '-q', type=str, 
    help='path to qrels file folder', required=True)
  parser.add_argument("--output-folder", '-o', type=str, 
    help='output folder to dump every file into', required=True)
  parser.add_argument("--only-alpha", '-a', type=bool, default=False,
    help='whether to only keep alpha word')

  # argument parse
  args = parser.parse_args()
  tfidf_file_folder = args.tfidf_folder
  rank_file_folder = args.qrels_folder
  out_folder = args.output_folder
  isalpha = args.only_alpha

  # sanity check
  assert os.path.isdir(tfidf_file_folder)
  assert os.path.isdir(rank_file_folder)

  # constants
  out_feature_folder = os.path.join(out_folder, 'features')
  out_docid_idx_file = os.path.join(out_folder, 'train-docid-idx-dict.pkl')
  out_vocab_idx_file = os.path.join(out_folder, 'vocab-idx-dict.pkl')

  # preprocessing
  _safe_mkdir(out_folder)
  _safe_mkdir(out_feature_folder)

  # pipeline from here
  logging.info(f'start building train...')

  docid_idx_dict, topic_docid_label = build_docid_idx_and_label(rank_file_folder)
  vocab_idx_dict, tfidf_dict = build_vocab_tfidf_dict(tfidf_file_folder, docid_idx_dict, isalpha)

  write_docid_idx_dict(docid_idx_dict, out_docid_idx_file)
  write_vocab_idx_dict(vocab_idx_dict, out_vocab_idx_file)

  tfidf_sp = to_sparse(tfidf_dict, docid_idx_dict, vocab_idx_dict)
  write_train_feature(topic_docid_label, tfidf_sp, docid_idx_dict, out_path=out_feature_folder)

  logging.info(f'build train finished in {time.time() - start_time} seconds')
