"""
This module trains a simple LogisticRegression model upon LTR features of MSMARCO dataset.
Command line:
python train_ltr.py --features ltr-features.tsv --output ltr-model.pkl

Creation Date: 11/14/2019
Last Modified: 11/14/2019
Author: Kamyar Ghajar <k.ghajar@gmail.com>
"""
import argparse
import time
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import mean_squared_error
import joblib
import pandas as pd


def preprocess_features(raw_features: pd.DataFrame, max_hits_rank: int):
    features = raw_features[['bm25_score', 'qterms_count']]
    labels = max_hits_rank - raw_features['rank']
    return features, labels


def train(features, labels):
    clf = LogisticRegression(random_state=42, solver='lbfgs', multi_class='auto', n_jobs=-1).fit(features, labels)
    predictions = clf.predict(features)
    mse = mean_squared_error(labels, predictions)
    print('Mean Squared Error on training is {}.'.format(mse))
    return clf


def save(save_path: str, model):
    with open(save_path, 'wb') as fout:
        joblib.dump(model, fout)
    print('Model saved on {}.'.format(save_path))


if __name__ == '__main__':
    parser = argparse.ArgumentParser(description='Train LTR MSMARCO learn to rank features.')
    parser.add_argument('--features', required=True, default='', help='features file')
    parser.add_argument('--output', required=True, default='', help='output model dump')
    parser.add_argument('--hits', default=10, help='number of hits to retrieve')
    
    args = parser.parse_args()

    total_start_time = time.time()

    raw_features = pd.read_csv(args.features, sep='\t', header=None, names=['qid', 'docid', 'bm25_score', 'qterms_count', 'rank'])
    features, labels = preprocess_features(raw_features, max_hits_rank=args.hits)
    model = train(features, labels)
    
    save(args.output, model)

    total_time = (time.time() - total_start_time)
    print('Total LTR model training time: {:0.3f} s'.format(total_time))
    print('Done!')
    

