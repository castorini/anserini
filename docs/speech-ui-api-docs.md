# Installing libraries for demo

`cd` into `src/main/js`.

```sh
npm install
```

# Installing py4j Gateway

- Build the Maven package and assemble the app from the root directory.
```
mvn clean package appassembler:assemble
```

# Flask

- Flask is used as the server for the API
- Copy `config.cfg.example` to `config.cfg` and make necessary changes, such as setting the index path and API keys.


# Run the Demo

```sh
./run_ui.sh
```

How this works under the hood:
- This script starts the PyseriniEntryPoint (gateway) from Java to open up a socket for communication and the Python API server.
- Python program tries to connect to a JVM with a gateway (localhost on port 25333).
- The Python program, search_web_collection, in src/main/python contains a search method that takes a
query string, number of hits and returns a list of document IDs. This calls the Java function.


# Additional Notes
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
