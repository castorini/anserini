import argparse
import configparser
import os
import sys

from flask import Flask, jsonify, request
# FIXME: separate this out to a classifier class where we can switch out the models
from pyserini import Pyserini
from sm_cnn.bridge import SMModelBridge

app = Flask(__name__)


@app.route("/", methods=['GET'])
def hello():
    return "Hello! The server is working properly... :)"

@app.route('/answer', methods=['POST'])
def answer():
    try:
        req = request.get_json(force=True)
        question = req["question"]
        num_hits = req.get('num_hits', 30)
        k = req.get('k', 20)
        print("Question: {}".format(question))
        # FIXME: get the answer from the PyTorch model here
        answers = get_answers(question, num_hits, k)
        answer_dict = {"answers": answers}
        return jsonify(answer_dict)
    except Exception as e:
        print(e)
        error_dict = {"error": "ERROR - could not parse the question or get answer. "}
        return jsonify(error_dict)

@app.route('/wit_ai_config', methods=['GET'])
def wit_ai_config():
    return jsonify({'WITAI_API_SECRET': app.config['Frontend']['witai_api_secret']})

# FIXME: separate this out to a classifier class where we can switch out the models
def get_answers(question, num_hits, k):
    pyserini = Pyserini(app.config['Flask']['index'])
    candidate_passages_scores = pyserini.ranked_passages(question, num_hits, k)

    candidate_sent_scores = []
    candidate_passages_sm = []

    for ps in candidate_passages_scores:
        ps_split = ps.split('\t')
        candidate_passages_sm.append(ps_split[0])
        candidate_sent_scores.append((float(ps_split[1]), ps_split[0]))

    if app.config['Flask']['model'] == "sm":
        path_to_castorini = os.getcwd() + "/.."
        model = SMModelBridge(path_to_castorini + '/models/sm_model/sm_model.fixed_ext_feats_paper.puncts_stay',
                              path_to_castorini + '/data/word2vec/aquaint+wiki.txt.gz.ndim=50.cache',
                              app.config['Flask']['index'])
        idf_json = pyserini.get_term_idf_json()
        answers_list = model.rerank_candidate_answers(question, candidate_passages_sm, idf_json)
        sorted_answers = sorted(answers_list, key=lambda x: x[0], reverse=True)
    else:
        # the re-ranking model chosen is idf
        sorted_answers = list(candidate_sent_scores)

    print("in idf:{}".format(sorted_answers))
    answers = []
    for score, sent in sorted_answers:
        print(sent)
        answers.append({'passage': sent, 'score': score})

    return answers

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Start the Flask API at the specified host, port')
    parser.add_argument('--config', help='config to use', required=False, type=str, default='config.cfg')
    parser.add_argument("--debug", help="print debug info", action="store_true")
    parser.add_argument("--model", help="[idf|sm]", default="idf")
    args = parser.parse_args()

    if not os.path.isfile(args.config):
        print("The configuration file ({}) does not exist!".format(args.config))
        sys.exit(1)

    config = configparser.ConfigParser()
    config.read(args.config)

    for name, section in config.items():
        if name == 'DEFAULT':
            continue

        app.config[name] = {}
        for key, value in config.items(name):
            app.config[name][key] = value

    app.config['Flask']['model'] = args.model

    print("Config: {}".format(args.config))
    print("Index: {}".format(app.config['Flask']['index']))
    print("Host: {}".format(app.config['Flask']['host']))
    print("Port: {}".format(app.config['Flask']['port']))
    print("Re-ranking Model: {}".format(app.config['Flask']['model']))
    print("Debug info: {}".format(args.debug))

    app.run(debug=args.debug, host=app.config['Flask']['host'], port=int(app.config['Flask']['port']))
