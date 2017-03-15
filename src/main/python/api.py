import argparse

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

# FIXME: separate this out to a classifier class where we can switch out the models
def get_answer(question):
    pyserini = Pyserini(app.config.get('index'))
    jaccard = Jaccard()
    candidate_passages = pyserini.ranked_passages(query_string=question, num_hits=30, k=20)
    answer = jaccard.most_similar_passage(question, candidate_passages)
    return answer

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Start the Flask API at the specified host, port')
    parser.add_argument('--index', help='directory path for index', required=True, type=str)
    parser.add_argument('--host', help='host address', required=False, type=str, default='0.0.0.0')
    parser.add_argument('--port', help='port', required=False, type=int, default=5546)
    parser.add_argument("--debug", help="print debug info", action="store_true")
    args = parser.parse_args()
    print("Index: {}".format(args.index))
    print("Host: {}".format(args.host))
    print("Port: {}".format(args.port))
    print("Debug info: {}".format(args.debug))

    app.config['index'] = args.index
    app.run(debug=args.debug, host=args.host, port=args.port)
