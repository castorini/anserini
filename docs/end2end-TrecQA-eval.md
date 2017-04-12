# Steps to setup an end2end evaluation framework for TrecQA

Start the PyseriniEntryPoint (gateway).

```
mvn clean package appassembler:assemble
sh target/appassembler/bin/PyseriniEntryPoint
```

Please take a look at [Py4J Gateway](https://github.com/rosequ/Anserini/blob/qaVisualization/docs/speech-ui-api-docs.md#how-to-use-the-py4j-gateway) 
if you need help setting up the gateway.

Run the following command to evaluate the end2end system:

```
python3 src/main/python/run_trecqa.py
```

Possible parameters are:

```
-input (required)
```

Path of a TrecQA file


```
-index (required)
```

Path of the index


```
-hits (optional: positive integer)
```

Number of passages to be retrieved per question (default: 20)

```
-k (optional: positive integer)
```

Top-k passages presented by the system (default: 10)

```
-model (optional: idf or sm)
```

Model to re-rank the passages (default: idf)

```
-output (optional: file path)
```

Path of the run file to be created

```
-qrel (optional: file path)
```
Path of the qrel file to be created


The above command will create a run file in the `trec_eval` format and a qrel file
called qrels.txt

### Calculating RBP:

To calculate RBP for the above run file:

- Install `rbp_eval` from[here](https://github.com/castorini/Anserini/tree/master/eval)
- Read more about RBP[here](http://people.eng.unimelb.edu.au/ammoffat/abstracts/mz08acmtois.html)
- run the following command to get rbp values:

`rbp_eval-0.2/rbp_eval/rbp_eval <qrel-file> <run-file>
`