import argparse
import ConfigParser

from flask import Flask, jsonify, request
# FIXME: separate this out to a classifier class where we can switch out the models
from pyserini import Pyserini
from jaccard import Jaccard

app = Flask(__name__)

@app.route("/", methods=['GET'])
def hello():
    return "Hello! The server is working properly... :)"

@app.route('/answer', methods=['POST'])
def answer():
    try:
        req = request.get_json(force=True)
        question = req["question"]
        print("Question: {}".format(question))
        # FIXME: get the answer from the PyTorch model here
        answer = get_answer(question)
        answer_dict = {"answer": answer}
        return jsonify(answer_dict)
    except Exception as e:
        error_dict = {"error": "ERROR - could not parse the question or get answer."}
        return jsonify(error_dict)

@app.route('/wit_ai_config', methods=['GET'])
def wit_ai_config():
    return jsonify({'WITAI_API_SECRET': app.config['witai_api_secret']})

# FIXME: separate this out to a classifier class where we can switch out the models
def get_answer(question):
    pyserini = Pyserini(app.config.get('index'))
    jaccard = Jaccard()
    candidate_passages = pyserini.ranked_passages(query_string=question, num_hits=30, k=20)
    answer = jaccard.most_similar_passage(question, candidate_passages)
    return answer

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Start the Flask API at the specified host, port')
    parser.add_argument('--config', help='config to use', required=False, type=str, default='config.cfg')
    parser.add_argument("--debug", help="print debug info", action="store_true")
    args = parser.parse_args()

    config = ConfigParser.ConfigParser()
    config.read(args.config)
    for name, value in config.items('Flask'):
        app.config[name] = value

    print("Config: {}".format(args.config))
    print("Index: {}".format(app.config['index']))
    print("Host: {}".format(app.config['host']))
    print("Port: {}".format(app.config['port']))
    print("Debug info: {}".format(args.debug))

    app.run(debug=args.debug, host=app.config['host'], port=app.config['port'])
