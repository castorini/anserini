import argparse
import logging
import os
import pickle
import time

from scipy.sparse import save_npz, csr_matrix
import sklearn.preprocessing
import numpy as np

from utils import TfidfTgzReader

# Global variables, which we're later going to refactor into an external config file.

topics = ['321', '336', '341', '347', '350', '362', '363', '367', '375', '378',
          '393', '397', '400', '408', '414', '422', '426', '427', '433', '439',
          '442', '445', '626', '646', '690']

config = {
    'target': {'name': 'core18',
               'run': 'run.core18.bm25+rm3.topics.core18.txt',
               'index': 'lucene-index.core18.pos+docvectors+rawdocs'}
}

working_directory = 'ccrf'


def dump_docvectors():
    run = config['target']['run']
    source_name = config['target']['name']
    index = config['target']['index']
    docids_file = os.path.join(working_directory, f'docids.{source_name}')
    logging.info(f'Extracting docvectors for {source_name} with Anserini')
    cmd = f'cut -f 3 -d " " {run} | sort | uniq > {docids_file}'
    os.system(cmd)
    cmd = f'target/appassembler/bin/IndexUtils -index {index} -dumpDocVectors {docids_file} -docVectorWeight TF_IDF'
    os.system(cmd)


def read_vocab(path):
    logging.info("loading vocabulary dictionary...")
    with open(path, 'rb') as f:
        vocab = pickle.load(f)

    return vocab

def build_docid_idx_dict():
    logging.info('building docid idx dict...')
    cur_idx = 0
    docid_idx_dict = {}
    with open(config['target']['run'], 'r') as f:
        for line in f:
            topic, _, docid, _, _, _ = line.split(' ')
            if topic in topics and docid not in docid_idx_dict:
                docid_idx_dict[docid] = cur_idx
                docid_idx_dict[cur_idx] = docid
                cur_idx += 1
    return docid_idx_dict


def write_docid_idx_dict(docid_idx_dict, filename):
    logging.info(f"writing docid-idx-dict to {filename}")
    with open(filename, 'wb') as f:
        pickle.dump(docid_idx_dict, f)


def build_tfidf_matrix(docid_idx_dict, vocab_idx_dict):
    num_docs, num_vocabs = len(docid_idx_dict) // 2, len(vocab_idx_dict) // 2
    logging.info(f'start building tfidf sparse matrix with {num_docs} docs and {num_vocabs} vocabs...')

    tfidf_dict = {}
    count = 0

    source_name = config['target']['name']
    tfidf_raw = os.path.join(working_directory, f'docids.{source_name}.docvector.TF_IDF.tar.gz')
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
    args = parser.parse_args()

    assert os.path.isdir(working_directory)

    # constants
    vocab_path = os.path.join(working_directory, 'vocab-idx-dict.pkl')
    out_docid_idx_file = os.path.join(working_directory, 'test-docid-idx-dict.pkl')
    out_feature_file = os.path.join(working_directory, 'test.npz')

    # preprocessing
    _safe_mkdir(working_directory)

    # pipeline from here
    logging.info(f'start building test...')

    dump_docvectors()
    vocab_dict = read_vocab(vocab_path)
    docid_idx_dict = build_docid_idx_dict()
    tfidf_sp = build_tfidf_matrix(docid_idx_dict, vocab_dict)

    write_docid_idx_dict(docid_idx_dict, out_docid_idx_file)

    logging.info(f'writing test data to {out_feature_file}...')
    save_npz(out_feature_file, tfidf_sp)

    logging.info(f'build test finished in {time.time() - start_time} seconds')

