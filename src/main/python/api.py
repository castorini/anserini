from flask import Flask, jsonify, request
import argparse

app = Flask(__name__)

@app.route("/", methods=['GET'])
def hello():
    return "Hello! The server is working properly... :)"

@app.route('/answer', methods=['POST'])
def get_answer():
    try:
        req = request.get_json(force=True)
        question = req["question"]
        print("Question: %s" % (question))
        # get the answer from the PyTorch model here
        answer = "Albert Einstein (14 March 1879 – 18 April 1955) was a German-born theoretical physicist."
        answer_dict = {"answer": answer}
        return jsonify(answer_dict)
    except Exception as e:
        error_dict = {"error": "ERROR - could not parse the question or get answer."}
        return jsonify(error_dict)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description='Start the Flask API at the specified host, port')
    parser.add_argument('--host', help='host address', required=False, type=str, default='0.0.0.0')
    parser.add_argument('--port', help='port', required=False, type=int, default=5546)
    parser.add_argument("--debug", help="print debug info", action="store_true")
    args = parser.parse_args()
    print("Host: %s" % args.host)
    print("Port: %s" % args.port)
    print("Debug info: %r" % args.debug)
    app.run(debug=args.debug, host=args.host, port=args.port)