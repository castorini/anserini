import argparse
import os
import re

from nltk.tokenize import TreebankWordTokenizer
from pyserini import Pyserini
from sm_cnn.bridge import SMModelBridge

class QAModel:
  instance = None
  def __init__(self, model_choice, index_path, w2v_cache, qa_model_file):
    if not QAModel.instance:
       path = os.getcwd() + '/..'
       if model_choice == "sm":         
         QAModel.instance = SMModelBridge(qa_model_file,
                             w2v_cache,
                             index_path)


def get_answers(pyserini, question, num_hits, k, model_choice, index_path, w2v_cache, qa_model_file):
  candidate_passages_scores = pyserini.ranked_passages(question, num_hits, k)
  idf_json = pyserini.get_term_idf_json()
  candidate_passages = []
  tokeninzed_answer = []

  if model_choice == 'sm':

    qa_DL_model = QAModel(model_choice, index_path, w2v_cache, qa_model_file).instance

    for ps in candidate_passages_scores:
      ps_split = ps.split('\t')
      candidate_passages.append(ps_split[0])

    # TODO: for processing input data. Ideally these settings need to come in as program arguments
    # NOTE: the best model for TrecQA keeps punctuation and keeps dash-words
    flags = {
      "punctuation": "", # ignoring for now  you can {keep|remove} punctuation
      "dash_words": "" # ignoring for now. you can {keep|split} words-with-hyphens
    }
    answers_list = qa_DL_model.rerank_candidate_answers(question, candidate_passages, idf_json, flags)
    sorted_answers = sorted(answers_list, key=lambda x: x[0], reverse=True)

    for score, candidate in sorted_answers:
      tokens = TreebankWordTokenizer().tokenize(candidate.lower().split("\t")[0])
      tokeninzed_answer.append((tokens, score))
    return tokeninzed_answer

  elif model_choice == 'idf':
    for candidate in candidate_passages_scores:
      candidate_sent, score = candidate.lower().split("\t")
      tokens = TreebankWordTokenizer().tokenize(candidate_sent.lower())
      tokeninzed_answer.append((tokens, score))
    return tokeninzed_answer

def jaccard(set1, set2):
  intersection = set1.intersection(set2)
  union = set1.union(set2)
  return len(intersection) / float(len(union))

def score_candidates(candidates, answers):
  scored_candidates = {}

  for candidate in candidates:
    this_candidate = " ".join(candidate[0])

    # xml file doesn't contain answers
    if not answers:
      scored_candidates[this_candidate] = ("empty answer", 0.0, candidate[1])

    for answer in answers:
      similarity = jaccard(set(candidate[0]), set(answer[1]))

      if this_candidate not in scored_candidates:
        scored_candidates[this_candidate] = (answer, similarity, candidate[1])
      elif similarity > scored_candidates[this_candidate][1]:
        scored_candidates[this_candidate] = (answer, similarity, candidate[1])

  return scored_candidates


def load_data(fname):
  questions = []
  answers = {}
  labels = {}
  prev = ''
  answer_count = 0

  with open(fname, 'r') as f:
    for line in f:
      line = line.strip()
      qid_match = re.match('<QApairs id=\'(.*)\'>', line)

      if qid_match:
        answer_count = 0
        qid = qid_match.group(1)

        if qid not in answers:
          answers[qid] = []

        if qid not in labels:
          labels[qid] = {}

      if prev and prev.startswith('<question>'):
        questions.append(line)

      label = re.match('^<(positive|negative)>', prev)

      if label:
        label = label.group(1)
        label = 1 if label == 'positive' else 0
        answer = line.split('\t')
        answer_count += 1

        answer_id = 'Q{}-A{}'.format(qid, answer_count)
        answers[qid].append((answer_id, answer, label))
        labels[qid][answer_id] = label
      prev = line

  return questions, answers, labels


def create_ground_truth(fname, answers):
  with open(fname, 'w') as f:
    for qid in answers.keys():
      for answer in answers[qid]:
        f.write('{} 0 {} {}\n'.format(qid, answer[0], answer[2]))


def get_precision(actual, predicted, k):
  total_count = 0.0
  hits = 0.0

  for qid in predicted.keys():
    run_labels = predicted.copy()

    if len(predicted[qid]) > k:
      run_labels = run_labels[qid][:k]
    else:
      run_labels = run_labels[qid]

    for p in run_labels:
      total_count += 1

      if not actual[qid]:
        continue

      if "unjudged" in p:
        hits += 0
      else:
        hits += actual[qid][p]

  if total_count > 0:
    return hits / total_count
  else:
    return 0


if __name__ == "__main__":
  parser = argparse.ArgumentParser(description='Evaluate the QA system')
  parser.add_argument('-input', help='path of a TrecQA file', required=True)
  parser.add_argument("-output", help="path of output file")
  parser.add_argument("-qrel", help="path of qrel file")
  parser.add_argument("-hits", help="number of hits", default=20)
  parser.add_argument("-k", help="top-k passages", default=10)
  parser.add_argument("-model", help="[idf|sm]", default='idf')
  parser.add_argument('-index', help="path of the index", required=True)
  parser.add_argument('-w2v-cache', help="word embeddings cache file", required=True)
  parser.add_argument('-qa-model-file', help="the path to the model file", required=True)
  
  args = parser.parse_args()

  pyserini = Pyserini(args.index)
  questions, answers, labels_actual = load_data(args.input)
  threshold = 0.7
  # TODO: what is this ^^ magic number


  if args.qrel:
    qrel_file = args.qrel
  else:
    qrel_file = "qrels.txt"

  create_ground_truth(qrel_file, answers)

  labels_predicted = {}

  if args.output:
    output_file = args.output
  else:
    output_file = 'run.qa.{}.h{}.k{}.txt'.format(args.model, args.hits, args.k)

  seen_docs = []
  with open(output_file, 'w') as out:
    for qid, question in zip(answers.keys(), questions):
      candidates = get_answers(pyserini, question, int(args.hits), int(args.k), args.model, args.index, args.w2v_cache, args.qa_model_file)
      scored_candidates = score_candidates(candidates, answers[qid])

      if qid not in labels_predicted:
        labels_predicted[qid] = []

      i = 0
      unjudged_count = 0

      for key, value in scored_candidates.items():
        jaccard_similarity = value[1]
        i += 1

        # check if the answer already exists in the ranked-list
        if jaccard_similarity >= threshold:
          doc_id = value[0][0]
          if doc_id not in seen_docs:
            out.write('{} Q0 {} {} {} TRECQA\n'.format(qid, doc_id, i, value[2]))
            labels_predicted[qid].append(doc_id)
            seen_docs.append(doc_id)
        else:
          unjudged_count += 1
          doc_id = 'unjudged{}'.format(unjudged_count)
          out.write('{} Q0 {} {} {} TRECQA\n'.format(qid, doc_id, i, value[2]))
          labels_predicted[qid].append(doc_id)

  depth_range = [5, 10, 15, 20, 50, 100]
  for k in depth_range:
    print('P@{}: {}'.format(k, get_precision(labels_actual, labels_predicted, k)))
