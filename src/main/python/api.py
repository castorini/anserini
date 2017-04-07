import argparse
import configparser
import os
import sys 

from flask import Flask, jsonify, request
# FIXME: separate this out to a classifier class where we can switch out the models
from pyserini import Pyserini
from jaccard import Jaccard
from castorini_smmodel_bridge import SMModelBridge

app = Flask(__name__)
path_to_castorini = "/Users/royalsequeira/Castorini"  # change this path
model = SMModelBridge(path_to_castorini + '/models/sm_model/sm_model.TrecQA.TRAIN-ALL.2017-04-02.castor',
                        path_to_castorini + '/data/word2vec/aquaint+wiki.txt.gz.ndim=50.cache',
                        path_to_castorini + '/data/TrecQA/stopwords.txt',
                        path_to_castorini + '/data/TrecQA/word2dfs.p')

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
    return jsonify({'WITAI_API_SECRET': app.config['witai_api_secret']})

# FIXME: separate this out to a classifier class where we can switch out the models
def get_answers(question, num_hits, k):
    pyserini = Pyserini(app.config.get('index'))
    candidate_passages_scores = pyserini.ranked_passages(question, num_hits, k)
    candidate_passages = []
    for ps in candidate_passages_scores:
        ps_split = ps.split('\t')
        candidate_passages.append(ps_split[0])

    print(candidate_passages)

    answers_list = model.rerank_candidate_answers(question, candidate_passages)

    print("---------*******-------")
    print(answers_list)

    answers = []
    for score, sent in answers_list:
        answers.append({'passage': sent, 'score': score})

    return answers

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Start the Flask API at the specified host, port')
    parser.add_argument('--config', help='config to use', required=False, type=str, default='config.cfg')
    parser.add_argument("--debug", help="print debug info", action="store_true")
    args = parser.parse_args()

    if not os.path.isfile(args.config):
        print("The configuration file ({}) does not exist!".format(args.config))
        sys.exit(1)

    config = configparser.ConfigParser()
    config.read(args.config)
    for name, value in config.items('Flask'):
        app.config[name] = value

    print("Config: {}".format(args.config))
    print("Index: {}".format(app.config['index']))
    print("Host: {}".format(app.config['host']))
    print("Port: {}".format(app.config['port']))
    print("Debug info: {}".format(args.debug))

    app.run(debug=args.debug, host=app.config['host'], port=int(app.config['port']))
