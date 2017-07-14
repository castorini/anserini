import argparse
import re
from hashlib import md5
from collections import namedtuple

from nltk.tokenize import TreebankWordTokenizer

from pyserini import Pyserini
from sm_cnn.bridge import SMModelBridge

class QAModel:
    instance = None
    def __init__(self, model_choice, index_path, w2v_cache, qa_model_file):
        if model_choice == "sm":
            QAModel.instance = SMModelBridge(qa_model_file, w2v_cache, index_path)

def get_answers(pyserini, question, h0, h1, model_choice, index_path, w2v_cache="", qa_model_file=""):
    candidate_passages_scores = pyserini.ranked_passages(question, h0, h1)
    idf_json = pyserini.get_term_idf_json()
    candidate_passages = []
    tokeninzed_answer = []

    if model_choice == "sm":
        qa_model = QAModel(model_choice, index_path, w2v_cache, qa_model_file).instance

        for ps in candidate_passages_scores:
            ps_split = ps.split("\t")
            candidate_passages.append(ps_split[0])

        # TODO: for processing input data. Ideally these settings need to come in as program arguments
        # NOTE: the best model for TrecQA keeps punctuation and keeps dash-words
        flags = {
          "punctuation": "", # ignoring for now  you can {keep|remove} punctuation
          "dash_words": "" # ignoring for now. you can {keep|split} words-with-hyphens
        }
        answers_list = qa_model.rerank_candidate_answers(question, candidate_passages, idf_json, flags)
        sorted_answers = sorted(answers_list, key=lambda x: x[0], reverse=True)

        for score, candidate in sorted_answers:
            tokens = TreebankWordTokenizer().tokenize(candidate.lower().split("\t")[0])
            tokeninzed_answer.append((tokens, score))
        return tokeninzed_answer

    elif model_choice == "idf":
        for candidate in candidate_passages_scores:
            candidate_sent, score = candidate.lower().split("\t")
            tokens = TreebankWordTokenizer().tokenize(candidate_sent.lower())
            tokeninzed_answer.append((tokens, score))
        return tokeninzed_answer

def jaccard(set1, set2):
    return len(set1 & set2) / len(set1 | set2)

def score_candidates(candidates, answers):
    scored_candidates = {}
    Candidate = namedtuple('Candidate', 'retrieved_set retrieved_str retrieved_score in_dataset')

    for candidate in candidates:
        this_candidate = Candidate(retrieved_set=set(candidate[0]), retrieved_str=" ".join(candidate[0]),
                                   retrieved_score=candidate[1], in_dataset=set(answer[1]))

        # xml file doesn't contain answers
        if not answers:
            scored_candidates[this_candidate] = ("empty answer", 0.0, this_candidate.retrieved_score)

        for answer in answers:
            similarity = jaccard(this_candidate.retrieved_set, this_candidate.in_dataset)

            if this_candidate not in scored_candidates:
                scored_candidates[this_candidate] = (answer, similarity, this_candidate.retrieved_score)
            elif similarity > scored_candidates[this_candidate][1]:
                scored_candidates[this_candidate] = (answer, similarity, this_candidate.retrieved_score)

    return scored_candidates


def load_data(fname):
    questions = []
    answers = {}
    labels = {}
    prev = ""
    answer_count = 0

    with open(fname, 'r') as f:
        for line in f:
            line = line.strip()
            qid_match = re.match("<QApairs id=\'(.*)\'>", line)

            if qid_match:
                answer_count = 0
                qid = qid_match.group(1)

                if qid not in answers:
                    answers[qid] = []

                if qid not in labels:
                    labels[qid] = {}

            if prev and prev.startswith("<question>"):
                questions.append(line)

            label = re.match("^<(positive|negative)>", prev)

            if label and qid:
                label = label.group(1)
                label = label == "positive"
                answer = line.lower().split("\t")
                answer_count += 1

                answer_id = "Q{}-A{}".format(qid, answer_count)
                answers[qid].append((answer_id, answer, label))
                labels[qid][answer_id] = label
            prev = line

    return questions, answers, labels


def create_qrel_jaccard(fname, answers):
    with open(fname, "w") as f:
        for qid in answers.keys():
            for answer in answers[qid]:
                f.write("{} 0 {} {}\n".format(qid, answer[0], answer[2]))

# evaluate by TREC QA patterns
def eval_by_pattern(qid, candidates, pattern, out):
    seen = set([])

    for i, cand in enumerate(candidates):
        this_candidate = " ".join(cand[0])

        # use the hash of the candidates as the docid since the sentences do not have docids
        hash_value = md5(this_candidate.encode()).hexdigest()
        answer_id = "QA{}.{}".format(qid, hash_value)

        if answer_id in seen:
            continue
        seen.add(answer_id)

        out.write("{} Q0 {} {} {} TrecQA-pattern\n".format(qid, answer_id, i+1, 1.0/(i + 1)))

# evaluate the run file at different depths
def evaluate_at_k(fname, eval_depth):
    for k in eval_depth:
        with open(fname) as f, open("{}.k{}.txt".format(fname, k), "w") as output_file:
            for line in f:
                det = line.strip().split()
                rank = int(det[3])

                if rank <= k:
                    output_file.write(line)


def create_qrel_pattern(all_sentences, pattern, qrels, qid):
    seen = set([])
    for i, cand in enumerate(all_sentences):
        this_candidate = cand.lower().split("\t")[0]
        result = re.findall(r"{}".format(pattern.lower()), this_candidate)

        # use the hash of the candidates as the docid since the sentences do not have docids
        hash_value = md5(this_candidate.encode()).hexdigest()
        answer_id = "QA{}.{}".format(qid, hash_value)
        if answer_id in seen:
            continue
        seen.add(answer_id)

        qrels.write("{} 0 {} {}\n".format(qid, answer_id, 1 if len(result) else 0))

# sentences with at least one nonstop question words
def clean_sentences(question, all_sentences, stop_words):
    question_words = set(question.strip().split())
    non_stop_question_words = question_words - stop_words
    cleaned_sentences = []

    for sent in all_sentences:
        this_candidate = sent.lower().split("\t")[0]
        this_candidate_tokens = set(this_candidate.split())

        word_overlap = this_candidate_tokens.intersection(non_stop_question_words)

        if len(word_overlap) > 0:
            cleaned_sentences.append(sent)

    return cleaned_sentences

# load the stopwords from the resource file
def get_stopwords():
    stopwords = set([])
    with open("src/main/resources/io/anserini/qa/english-stoplist.txt") as stop:
        for line in stop:
            if not line.startswith("#"):
                stopwords.add(line.strip())
    return stopwords

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Evaluate the QA system")
    parser.add_argument("-input", help="path of a TrecQA file", required=True)
    parser.add_argument("-output", help="path of output directory", default=".")
    parser.add_argument("-qrel", help="path of qrel file")
    parser.add_argument("-h0", help="Number of documets to be retrieved (hits)", default=1000)
    parser.add_argument("-h1", help="h1 passages to be reranked", default=100)
    parser.add_argument("-k", help="evaluate at depth k", default="5")
    parser.add_argument("-model", help="[idf|sm]", default="idf")
    parser.add_argument("-index", help="path of the index", required=True)
    parser.add_argument("-w2v-cache", help="word embeddings cache file")
    parser.add_argument("-qa-model-file", help="the path to the model file")
    parser.add_argument("-eval", help="[pattern|jaccard]", default="pattern")
    parser.add_argument("-pattern", help="path to the pattern file")

    args = parser.parse_args()

    if args.model == "sm" and not args.w2v_cache and not args.qa_model_file:
        print("Pass the word embeddings cache file and the model file")
        parser.print_help()
        exit()

    pattern_dict = {}
    if args.eval == "pattern":
        if not args.pattern:
            print("Path to the pattern file should be included if the method of evaluation is pattern based")
            parser.print_help()
            exit()
        else:
            with open(args.pattern, "r") as pattern_file:
                for line in pattern_file:
                    det = line.strip().split(" ", 1)

                    if det[0] not in pattern_dict:
                        pattern_dict[det[0]] = det[1]

    pyserini = Pyserini(args.index)
    questions, answers, labels_actual = load_data(args.input)

    # based on observation, fix the threshold for Jaccard similarity to be 0.7
    threshold = 0.7

    qrel_file = ""

    if args.qrel:
        qrel_file = args.qrel
    elif args.eval == "jaccard":
        qrel_file = "{}/qrel.jaccard.txt".format(args.output)
        create_qrel_jaccard(qrel_file, answers)
    elif args.eval == "pattern":
        qrel_file = "{}/qrel.pattern.txt".format(args.output)

        stopwords = get_stopwords()

        with open(qrel_file, "w") as qrel:
            for qid, question in zip(answers.keys(), questions):
                # retrieve all sentences corresponding to h0 hits
                all_sentences = pyserini.get_all_sentences(question, args.h0)
                cleaned_sentences = clean_sentences(question, all_sentences, stopwords)
                try:
                    create_qrel_pattern(cleaned_sentences, pattern_dict[qid], qrel, qid)
                except KeyError as e:
                    print(e)
                    print("pattern not found")

    output_file = "{}/run.qa.{}.{}.h0_{}.h1_{}".format(args.output, args.model,  args.eval, args.h0, args.h1)
    seen_docs = []

    with open(output_file, "w") as out:
        for qid, question in zip(answers.keys(), questions):
            # sentence re-ranking after ad-hoc retrieval
            candidates = get_answers(pyserini, question, int(args.h0), int(args.h1), args.model, args.index, args.w2v_cache,
                                   args.qa_model_file)

            if args.eval == "jaccard":
                scored_candidates = score_candidates(candidates, answers[qid])
                i, unjudged_count = 0, 0

                for key, value in scored_candidates.items():
                  jaccard_similarity = value[1]
                  i += 1

                # check if the answer already exists in the TrecQA test set
                if jaccard_similarity >= threshold:
                    doc_id = value[0][0]
                    if doc_id not in seen_docs:
                        out.write("{} Q0 {} {} {} TRECQA\n".format(qid, doc_id, i, value[2]))
                        seen_docs.append(doc_id)
                    else:
                        unjudged_count += 1
                        doc_id = "unjudged{}".format(unjudged_count)
                        out.write("{} Q0 {} {} {} TRECQA\n".format(qid, doc_id, i, value[2]))

            elif args.eval == "pattern":
                if not args.pattern:
                    print("Path to the pattern file should be included if the method of evaluation is pattern based")
                    args.print_help()
                    exit()
                try:
                    eval_by_pattern(qid, candidates, pattern_dict[qid], out)
                except KeyError as e:
                    print("Pattern not found for question: {}".format(e))

        eval_depth = map(int, args.k.strip().split())
        evaluate_at_k(output_file, eval_depth)

