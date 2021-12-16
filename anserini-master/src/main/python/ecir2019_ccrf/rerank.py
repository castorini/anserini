import argparse
import json
import logging
import os
import pickle
import time

import lightgbm as lgb
import numpy as np
import sklearn.linear_model
import sklearn.svm
import scipy.sparse


def load_train(topic, path):
    X = scipy.sparse.load_npz(os.path.join(path, f'{topic}.npz'))
    y = np.load(os.path.join(path, f'{topic}.npy'))
    return X, y


def load_test(path):
    logging.info('Loading test data...')
    X = scipy.sparse.load_npz(path)
    return X


def load_base_run(topics, rank_file, docid_idx_dict):
    logging.info('Loading base run...')
    score_dict = {}
    with open(rank_file, 'r') as f:
        curqid = None

        docids, indices, scores = [], [], []
        for line in f:
            qid, _, docid, _, score, _ = line.split(' ')

            if qid not in topics:
                continue

            # write file
            if curqid is not None and curqid != qid:
                score_dict[curqid] = [docids.copy(), indices.copy(), scores.copy()]
                del docids[:], indices[:], scores[:]

            curqid = qid
            docids.append(docid)
            indices.append(docid_idx_dict[docid])
            scores.append(float(score))

        score_dict[curqid] = [docids, indices, scores]

    return score_dict


def rerank(test_doc_score, alpha, output, limit, tag):
    def interpolate(old_score, new_score):
        s_min, s_max = min(old_score), max(old_score)
        old_score = (old_score - s_min) / (s_max - s_min)

        s_min, s_max = min(new_score), max(new_score)
        new_score = (new_score - s_min) / (s_max - s_min)

        score = old_score * (1 - alpha) + new_score * alpha
        return score

    filename = f'rerank_{alpha}.txt'
    with open(os.path.join(output, filename), 'w') as f:
        logging.info(f'Writing output for alpha = {alpha}')
        for topic in test_doc_score:
            docid, _, old_score, new_score = test_doc_score[topic]
            score = interpolate(np.array(old_score), new_score)
            sorted_score = sorted(list(zip(docid, score)), key=lambda x: -x[1])

            rank = 1
            for docid, score in sorted_score:
                f.write(f'{topic} Q0 {docid} {rank} {score} h2oloo_{tag}\n')
                rank += 1
                if rank > limit:
                    break


def evaluate_topic(X_train, y_train, X_test, classifier):
    if classifier == 'lr':
        clf = sklearn.linear_model.LogisticRegression(class_weight='balanced', random_state=848)
        clf.fit(X_train, y_train)
        y_test = clf.predict_proba(X_test)[:,1]
        return y_test
    elif classifier == 'svm':
        clf = sklearn.svm.SVC(kernel='linear', class_weight='balanced', probability=True, random_state=848)
        clf.fit(X_train, y_train)
        y_test = clf.predict_proba(X_test)[:,1]
        return y_test
    elif classifier == 'lgb':
        param = {
            'num_leaves':15,
            'num_iterations':100,
            'max_depth': 5,
            'objective':'binary',
            'is_unbalance': True,
            'metric': ['auc', 'binary_logloss'],
            'verbose': -1,
            'seed': 848
        }
        train_data = lgb.Dataset(X_train, label=y_train)
        clf = lgb.train(param, train_data)
        y_test = clf.predict(X_test)
        return y_test


def run_classifier(config, classifier, output_folder):
    start_time = time.time()
    logging.info(f'Begin training/inference using {classifier} classifier...')

    for topic in config['topics']:
        X_train, y_train = load_train(topic, train_feature_folder)
        _, doc_idx, _ = test_doc_score[topic]
        X_test = test_data[doc_idx]

        logging.info(f'Processing topic {topic}')
        y_test = evaluate_topic(X_train, y_train, X_test, classifier)

        test_doc_score[topic].append(y_test)

    for alpha in [0.0, 0.1, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1.0]:
        rerank(test_doc_score, alpha, output_folder, 10000, classifier)

    logging.info(f'Finished with {classifier} in {time.time() - start_time} seconds')


def _safe_mkdir(path):
    if not os.path.exists(path):
        os.makedirs(path)


if __name__ == '__main__':
    logging.basicConfig(level=logging.DEBUG, format='%(asctime)s %(message)s')

    parser = argparse.ArgumentParser()
    parser.add_argument("--config", type=str, help='config file', required=True)
    args = parser.parse_args()
    config_file = args.config

    # Load configuration
    with open(config_file) as f:
        config = json.load(f)

    working_directory = config['working_directory']
    assert os.path.isdir(working_directory)

    train_feature_folder = os.path.join(working_directory, 'features')
    test_feature_path = os.path.join(working_directory, 'test.npz')
    test_docid_idx_path = os.path.join(working_directory, 'test_docid_idx_dict.pkl')
    models_folder = os.path.join(working_directory, 'models')

    # sanity check
    assert os.path.isdir(working_directory)
    assert os.path.isdir(train_feature_folder)
    _safe_mkdir(models_folder)

    for classifier in config['classifiers']:
        logging.info(f'Applying {classifier}...')
        logging.info('Loading docid_idx dict...')
        with open(test_docid_idx_path, 'rb') as f:
            test_docid_idx_dict = pickle.load(f)

        test_doc_score = load_base_run(config['topics'], config['target']['run'], test_docid_idx_dict)
        test_data = load_test(test_feature_path)

        model_folder = os.path.join(models_folder, classifier)
        _safe_mkdir(model_folder)

        # There's some use of global variables above that needs to be undone in refactoring...
        run_classifier(config, classifier, model_folder)
