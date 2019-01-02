import argparse
import json
import logging
import os
import pickle
import time

import scipy.sparse
import sklearn.preprocessing
import numpy as np

import utils


def dump_docvectors(config):
    run = config['target']['run']
    source_name = config['target']['name']
    index = config['target']['index']
    docids_file = os.path.join(working_directory, f'docids.{source_name}')
    logging.info(f'Extracting docvectors for {source_name} with Anserini...')
    cmd = f'cut -f 3 -d " " {run} | sort | uniq > {docids_file}'
    os.system(cmd)
    cmd = f'target/appassembler/bin/IndexUtils -index {index} -dumpDocVectors {docids_file} -docVectorWeight TF_IDF'
    os.system(cmd)


def build_docid_idx_dict(config):
    logging.info('Building docid_idx_dict...')
    cur_idx = 0
    docid_idx_dict = {}
    with open(config['target']['run'], 'r') as f:
        for line in f:
            topic, _, docid, _, _, _ = line.split(' ')
            if topic in config['topics'] and docid not in docid_idx_dict:
                # docid_idx_dict and vocab_idx_dict actually store two different mappings:
                # both docid (vocab) -> idx and idx -> docid (vocab).
                #
                # The docid -> idx mapping is to transform the docid to the row number of the matrix, and
                # the idx -> docid mapping is to generate the final submission from the index.
                # The vocab -> mapping is to transform the word into the column number of the matrix, and
                # the idx -> vocab mapping is easy for us to check the importance of features.
                #
                # The docid and vocab are in string format and indices are integers, so there won't be any conflict.
                # However, this isn't a good design, and should probably be refactored later.
                docid_idx_dict[docid] = cur_idx
                docid_idx_dict[cur_idx] = docid
                cur_idx += 1
    return docid_idx_dict


def build_tfidf_matrix(config, docid_idx_dict, vocab_idx_dict):
    num_docs, num_vocabs = len(docid_idx_dict) // 2, len(vocab_idx_dict) // 2
    logging.info(f'Building tf.idf sparse matrix with {num_docs} docs and {num_vocabs} features...')

    tfidf_dict = {}
    count = 0

    source_name = config['target']['name']
    tfidf_raw = os.path.join(working_directory, f'docids.{source_name}.docvector.TF_IDF.tar.gz')
    reader = utils.TfidfTgzReader(tfidf_raw)
    while reader.hasnextdoc():
        docid = reader.getnextdoc().strip()
        count += 1
        if count % 100000 == 0:
            logging.info(f'{count} documents processed...')

        if docid not in docid_idx_dict:
            reader.skipdoc()
            continue

        doc_idx = docid_idx_dict[docid]
        while reader.hasnexttfidf():
            word, tfidf = reader.getnexttfidf()
            if word in vocab_idx_dict:
                vocab_idx = vocab_idx_dict[word]
                tfidf_dict[(doc_idx, vocab_idx)] = float(tfidf)

    logging.info(f'Finished building tf.idf dict, {count} documents in total.')

    indices = tuple(zip(*tfidf_dict.keys()))
    values = list(tfidf_dict.values())
    tfidf_sp = scipy.sparse.csr_matrix((values, indices), shape=(num_docs, num_vocabs), dtype=np.float32)
    logging.info(f'Finished building tf.idf sparse matrix.')
    return sklearn.preprocessing.normalize(tfidf_sp, norm='l2')


def _safe_mkdir(path):
    if not os.path.exists(path):
        os.makedirs(path)


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s %(message)s')
    start_time = time.time()

    parser = argparse.ArgumentParser()
    parser.add_argument("--config", type=str, help='config file', required=True)
    args = parser.parse_args()
    config_file = args.config

    # Load configuration
    with open(config_file) as f:
        config = json.load(f)

    working_directory = config['working_directory']
    assert os.path.isdir(working_directory)

    out_docid_idx_file = os.path.join(working_directory, 'test_docid_idx_dict.pkl')
    out_feature_file = os.path.join(working_directory, 'test.npz')

    _safe_mkdir(working_directory)

    logging.info(f'Preparing test data...')
    logging.info("Loading vocabulary...")
    with open(os.path.join(working_directory, 'vocab_idx_dict.pkl'), 'rb') as f:
        vocab_dict = pickle.load(f)

    dump_docvectors(config)
    docid_idx_dict = build_docid_idx_dict(config)
    tfidf_sp = build_tfidf_matrix(config, docid_idx_dict, vocab_dict)

    logging.info(f'Writing docid_idx_dict to {out_docid_idx_file}')
    with open(out_docid_idx_file, 'wb') as f:
        pickle.dump(docid_idx_dict, f)

    logging.info(f'Writing test data to {out_feature_file}...')
    scipy.sparse.save_npz(out_feature_file, tfidf_sp)

    logging.info(f'Finished in {time.time() - start_time} seconds')
