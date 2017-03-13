from flask import Flask, jsonify, request

app = Flask(__name__)

@app.route("/")
def hello():
    return "Hello World!"

# REQUEST: 
# Content-Type: application/json
# text of body in raw format: 
# {
#     "question": "birthdate of Einstein?"
# }
@app.route('/answer', methods=['POST'])
def get_answer():
    try:
        req = request.get_json(force=True)
        question = req["question"]
        print("Question: %s" % (question))
        # get the answer here and respond with the answer
        answer = "the answer string will be here, for e.g. Einstein was born in 1944 or sth."
        answer_dict = {"answer": answer}
        return jsonify(answer_dict)
    except Exception as e:
        error_dict = {"error": "ERROR - could not parse the question or get answer."}
        return jsonify(error_dict)

if __name__ == "__main__":
    app.run(debug=True, host='0.0.0.0', port=5546)