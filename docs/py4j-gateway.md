### How to use the py4j Gateway

#### Steps
- Build the Maven package and assemble the app.
- Start the GatewayEntryPoint from Java to open up a socket for communication.
```
mvn clean package appassembler:assemble
sh target/appassembler/bin/GatewayEntryPoint
```
- Make sure you have py4j installed for Python or else, issue this command: sudo pip install py4j
- Python tries to connect to a JVM with a gateway (localhost on port 25333).
- Python program can now initialize a Java Gateway object.
- The Python program, search_web_collection, in src/main/python contains a search method that takes a
query string, number of hits and returns a list of document IDs.
