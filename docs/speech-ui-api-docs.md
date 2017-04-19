# Commands to get the UI and server running once configured
```
sh target/appassembler/bin/PyseriniEntryPoint
python3 src/main/python/api.py

cd src/main/js
npm start
```

# Setting up Speech to Text Demo

`cd` into `src/main/js`.

```sh
npm install
npm start
```

# Flask API

- Install Flask with the command: pip install flask
- Copy `config.cfg.example` to `config.cfg` and make necessary changes, such as setting the index path and API keys.
- The Flask API can be started with the following command. Default host is 0.0.0.0 and port is 5546 without debugging info.
- Make sure to start the PyseriniEntryPoint (gateway) from Java to open up a socket for communication for Pyserini.
```
sh target/appassembler/bin/PyseriniEntryPoint
python src/main/python/api.py [--debug]
```

- This is the documentation for the API call to send a question to the model and get back the predicted answer.
- The request body fields are: question(required )num_hits(optional) and k(optional).
```
# REQUEST:
HTTP Method: POST
Endpoint: [host]:[port]/answer
Content-Type: application/json
text of body in raw format:
{
    "question": "What is the birthdate of Einstein?",
    "num_hits": 50,
    "k": 30
}
```

- The response body contains answers which is a list of objects with two fields - passage, score.
```
# RESPONSE:
Content-Type: application/json
text of body in raw format:
{
  "answers": [
                {"passage": "Einstein was born in the 1800s", 'score': 0.976},
                {"passage": "Einstein was a physicist", 'score': 0.524}
            ]
}
```

# How to use the py4j Gateway

### Steps
- Build the Maven package and assemble the app.
- Start the PyseriniEntryPoint (gateway) from Java to open up a socket for communication.
```
mvn clean package appassembler:assemble
sh target/appassembler/bin/PyseriniEntryPoint
```
- Make sure you have py4j installed for Python or else, issue this command: sudo pip install py4j
- Python tries to connect to a JVM with a gateway (localhost on port 25333).
- Python program can now initialize a Java Gateway object.
- The Python program, search_web_collection, in src/main/python contains a search method that takes a
query string, number of hits and returns a list of document IDs.



