import argparse
import logging
import os
import pickle
import time

import numpy as np
from sklearn import linear_model, neural_network, svm
from scipy.sparse import load_npz
from sklearn.externals import joblib
from sklearn.ensemble import RandomForestClassifier
import lightgbm as lgb

topic_list = [
    '321', '336', '341',
    '347', '350', '362',
    '363', '367', '375', '378', '393',
    '397', '400', '408', '414',
    '422', '426', '427', '433',
    '439', '442', '445', '626', '646',
    '690'
  ]


def load_train(topic, path):
  """

  """
  X = load_npz(os.path.join(path, f'{topic}.npz'))
  y = np.load(os.path.join(path, f'{topic}.npy'))
  return X, y


def load_test(path):
  """

  """
  logging.info('loading test data...')
  X = load_npz(path)
  return X

def load_docid_idx(path):
  """

  """
  logging.info('loading docid idx dict...')
  with open(path, 'rb') as f:
    docid_idx_dict = pickle.load(f)
  return docid_idx_dict


def generate_test_score(rank_file, docid_idx_dict):
  """

  """
  logging.info('generating test score...')
  score_dict = {}
  with open(rank_file, 'r') as f:
    curqid = None
    
    docids, indices, scores = [], [], []
    for line in f:
      qid, _, docid, _, score, _ = line.split(' ')

      if qid not in topic_list:
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
  """

  """
  def interpolate(old_score, new_score):
    s_min, s_max = min(old_score), max(old_score)
    old_score = (old_score - s_min) / (s_max - s_min)

    s_min, s_max = min(new_score), max(new_score)
    new_score = (new_score - s_min) / (s_max - s_min)

    score = old_score * (1 - alpha) + new_score * alpha
    return score
  
  filename = f'rerank_{alpha}.txt'
  with open(os.path.join(output, filename), 'w') as f:
    logging.info(f'dump file for alpha = {alpha}...') 
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
  """

  """
  if classifier == 'lr2':
    clf = linear_model.LogisticRegression(class_weight='balanced', random_state=848)
    clf.fit(X_train, y_train)
    y_test = clf.predict_proba(X_test)[:,1]
    return y_test
  elif classifier == 'lr1':
    clf = linear_model.LogisticRegression(random_state=848)
    clf.fit(X_train, y_train)
    y_test = clf.predict_proba(X_test)[:,1]
    return y_test
  elif classifier == 'par':
    clf = linear_model.PassiveAggressiveRegressor(max_iter=100, random_state=848)
  elif classifier == 'ridge':
    clf = linear_model.Ridge(random_state=848)
  elif classifier == 'sgdr':
    clf = linear_model.SGDRegressor(max_iter=1000, random_state=848)
  elif classifier == 'sgdc':
    clf = linear_model.SGDClassifier(loss='log', class_weight='balanced', max_iter=10, random_state=848)
    clf.fit(X_train, y_train)
    y_test = clf.predict_proba(X_test)[:,1]
    return y_test
  elif classifier == 'mlp':
    clf = neural_network.MLPRegressor(learning_rate_init=0.0005, max_iter=5, random_state=848)
  elif classifier == 'svm':
    clf = svm.SVC(kernel='linear', class_weight='balanced', probability=True, random_state=848)
    clf.fit(X_train, y_train)
    y_test = clf.predict_proba(X_test)[:,1]
    return y_test
  elif classifier == 'svr':
    clf = svm.SVR()
  elif classifier == 'linearsvr':
    clf = svm.LinearSVR(random_state=848)
  elif classifier == 'nusvr':
    clf = svm.NuSVR()
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
  elif classifier == 'rf':
    clf = RandomForestClassifier(
      n_estimators=200,
      class_weight='balanced',
      max_depth=3,
      random_state=848
    )
    clf.fit(X_train, y_train)
    y_test = clf.predict_proba(X_test)[:,1]
    return y_test
  else:
    raise Exception('Wrong classifier')
  
  clf.fit(X_train, y_train)
  y_test = clf.predict(X_test)
  
  return y_test

def _safe_mkdir(path):
  if not os.path.exists(path):
    os.makedirs(path)

if __name__ == '__main__':
  logging.basicConfig(level=logging.DEBUG, 
  format='%(asctime)s %(message)s', datefmt='%m/%d/%Y %I:%M:%S ')
  start_time = time.time()

  parser = argparse.ArgumentParser()
  parser.add_argument("--train-folder", '-t', type=str, 
    help='path to train folder', required=True)
  parser.add_argument("--test-folder", '-p', type=str, 
    help='path to test folder', required=True)
  parser.add_argument("--rank-file", '-r', type=str, 
    help='path to rank file', required=True)
  parser.add_argument("--classifier", '-c', type=str,
    help='classifier used to train, choose from lr and svm', default='lr')
  parser.add_argument("--output-folder", '-o', type=str, 
    help='output folder to write rerank file', required=True)
  parser.add_argument("--limit", '-l', type=int,
    help='the number of hits to write in file', default=10000)
  

  # argument parse
  args = parser.parse_args()
  train_folder = args.train_folder
  test_folder = args.test_folder
  rank_file = args.rank_file
  clf = args.classifier
  output_folder = args.output_folder
  limit = args.limit

  # sanity check
  assert os.path.isdir(train_folder)
  assert os.path.isdir(test_folder)

  # constants
  train_feature_folder = os.path.join(train_folder, 'features')
  test_feature_path = os.path.join(test_folder, 'test.npz')
  test_docid_idx_path = os.path.join(test_folder, 'test-docid-idx-dict.pkl')

  # preprocessing
  test_docid_idx_dict = load_docid_idx(test_docid_idx_path)
  test_doc_score = generate_test_score(rank_file, test_docid_idx_dict)
  test_data = load_test(test_feature_path)

  _safe_mkdir(output_folder)
  
  # pipeline from here
  logging.info(f'start training using {clf} as classifier...')

  for topic in topic_list:
    X_train, y_train = load_train(topic, train_feature_folder)
    _, doc_idx, _ = test_doc_score[topic]
    X_test = test_data[doc_idx]

    logging.info(f'Train and test on topic {topic}')
    y_test = evaluate_topic(X_train, y_train, X_test, clf)
    
    test_doc_score[topic].append(y_test)
     
  for alpha in [0.4, 0.5, 0.6, 0.7, 0.8, 0.9, 1]:
    rerank(test_doc_score, alpha, output_folder, limit, clf)

  logging.info(f'train with {clf} finished in {time.time() - start_time} seconds')
