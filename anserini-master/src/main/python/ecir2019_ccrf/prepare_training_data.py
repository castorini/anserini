import argparse
import json
import logging
import os
import pickle
import time

import numpy as np
import scipy.sparse
import sklearn.preprocessing

import utils


def dump_docvectors(config):
    for source in config['sources']:
        qrels = source['qrels']
        source_name = source['name']
        index = source['index']
        docids_file = os.path.join(config['working_directory'], f'docids.{source_name}')
        logging.info(f'Extracting docvectors for {source_name} with Anserini...')
        cmd = f'cut -f 3 -d " " {qrels} | sort | uniq > {docids_file}'
        os.system(cmd)
        cmd = f'target/appassembler/bin/IndexUtils -index {index} -dumpDocVectors {docids_file} -docVectorWeight TF_IDF'
        os.system(cmd)


def build_docid_idx_and_label(config):
    logging.info(f'Building docid_idx dict and topic_docid_label dict...')
    cur_idx = 0
    docid_idx_dict = {}
    topic_docid_label = {topic: {} for topic in config['topics']}

    for source in config['sources']:
        rank_file = source['qrels']
        logging.info(f'Building docid_idx dict for {rank_file}...')
        with open(os.path.join(rank_file), 'r') as f:
            for line in f:
                topic, _, docid, label = line.strip().split(' ')
                if topic in config['topics']:
                    if docid not in docid_idx_dict:
                        # Build docid_idx_dict: the idx is the row index for that document in the sparse matrix
                        docid_idx_dict[docid] = cur_idx
                        docid_idx_dict[cur_idx] = docid
                        cur_idx += 1

                    # The "label" in this case is relevant/not-relevant
                    topic_docid_label[topic][docid_idx_dict[docid]] = 0 if label == '0' else 1

    logging.info(f'Finished building docid_idx dict and topic_docid_label dict.')
    logging.info(f'{cur_idx} documents found in total.')
    return docid_idx_dict, topic_docid_label


def build_vocab_tfidf_dict(config, docid_idx_dict, is_alpha):
    logging.info('Building tf.idf dict for all sources...')
    vocab_idx_dict, tfidf_dict = {}, {}
    cur_idx = 0

    for source in config['sources']:
        source_name = source['name']
        tfidf_raw = os.path.join(config['working_directory'], f'docids.{source_name}.docvector.TF_IDF.tar.gz')
        logging.info(f'Building tf.idf dict for {tfidf_raw}')
        count = 0

        reader = utils.TfidfTgzReader(tfidf_raw)
        while reader.hasnextdoc():
            docid = reader.getnextdoc().strip()
            count += 1
            if count % 100000 == 0:
                logging.info(f'{count} docs processed...')

            if docid not in docid_idx_dict:
                continue

            doc_idx = docid_idx_dict[docid]
            while reader.hasnexttfidf():
                word, tfidf = reader.getnexttfidf()
                if not is_alpha or word.isalpha():
                    if word not in vocab_idx_dict:
                        vocab_idx_dict[word] = cur_idx
                        vocab_idx_dict[cur_idx] = word
                        cur_idx += 1
                    tfidf_dict[(doc_idx, vocab_idx_dict[word])] = float(tfidf)

    logging.info(f'Finished building tf.idf dict, {cur_idx} unique words in total.')
    return vocab_idx_dict, tfidf_dict


def to_sparse(tfidf_dict, docid_idx_dict, vocab_idx_dict):
    logging.info('Converting tf.idf dict into a sparse matrix...')
    num_docs, num_vocabs = len(docid_idx_dict) // 2, len(vocab_idx_dict) // 2
    indices = tuple(zip(*tfidf_dict.keys()))
    values = list(tfidf_dict.values())
    tfidf_sp = scipy.sparse.csr_matrix((values, indices), shape=(num_docs, num_vocabs), dtype=np.float32)

    return sklearn.preprocessing.normalize(tfidf_sp, norm='l2')


def write_train_feature(topic_docid_label: dict, feature_matrix: scipy.sparse.csr_matrix, out_path: str):
    logging.info('Writing training data...')

    for topic, labels in topic_docid_label.items():
        logging.info(f'Topic {topic}')
        X_idx, y = list(zip(*labels.items()))
        X, y = feature_matrix[X_idx, :], np.array(y)
        scipy.sparse.save_npz(os.path.join(out_path, f'{topic}.npz'), X)
        np.save(os.path.join(out_path, f'{topic}.npy'), np.array(y))


def _safe_mkdir(path):
    if not os.path.exists(path):
        os.makedirs(path)


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s %(message)s')
    start_time = time.time()

    parser = argparse.ArgumentParser()
    parser.add_argument("--config", type=str, help='config file', required=True)
    parser.add_argument("--only-alpha", '-a', type=bool, default=False,
                        help='whether to only keep alpha words')

    args = parser.parse_args()
    is_alpha = args.only_alpha
    config_file = args.config

    # Load the configuration
    with open(config_file) as f:
        config = json.load(f)

    features_folder = os.path.join(config['working_directory'], 'features')

    _safe_mkdir(config['working_directory'])
    _safe_mkdir(features_folder)

    # pipeline from here
    logging.info(f'Preparing training data...')

    dump_docvectors(config)
    docid_idx_dict, topic_docid_label = build_docid_idx_and_label(config)
    vocab_idx_dict, tfidf_dict = build_vocab_tfidf_dict(config, docid_idx_dict, is_alpha)

    out_docid_idx_file = os.path.join(config['working_directory'], 'train_docid_idx_dict.pkl')
    logging.info(f"Writing docid_idx_dict to {out_docid_idx_file}...")
    with open(out_docid_idx_file, 'wb') as f:
        pickle.dump(docid_idx_dict, f)

    out_vocab_idx_file = os.path.join(config['working_directory'], 'vocab_idx_dict.pkl')
    logging.info(f"Writing vocab_idx_dict to {out_vocab_idx_file}...")
    with open(out_vocab_idx_file, 'wb') as f:
        pickle.dump(vocab_idx_dict, f)

    tfidf_sp = to_sparse(tfidf_dict, docid_idx_dict, vocab_idx_dict)
    write_train_feature(topic_docid_label, tfidf_sp, out_path=features_folder)

    logging.info(f'Finished in {time.time() - start_time} seconds')
