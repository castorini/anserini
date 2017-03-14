### Flask API

- The Flask API can be started with the following command. Default host is 0.0.0.0 and port is 5546 without debugging info.
```
python api.py --host [host] --port [port] [--debug]
```

- This is the documentation for the API call to send a question to the model and get back the predicted answer.
```
# REQUEST:
HTTP Method: POST
Endpoint: [host]:[port]/answer
Content-Type: application/json
text of body in raw format: 
{
    "question": "What is the birthdate of Einstein?"
}

# RESPONSE: 
Content-Type: application/json
text of body in raw format: 
{
  "answer": "Albert Einstein (14 March 1879 – 18 April 1955) was a German-born theoretical physicist."
}
```