# Steps to setup an end2end evaluation framework for TrecQA

Start the PyseriniEntryPoint (gateway).

```
mvn clean package appassembler:assemble
sh target/appassembler/bin/PyseriniEntryPoint
```

Please take a look at [Py4J Gateway](https://github.com/rosequ/Anserini/blob/qaVisualization/docs/speech-ui-api-docs.md#how-to-use-the-py4j-gateway) 
if you need help setting up the gateway.

Run the following command to evaluate the end2end system:

```bash
python src/main/python/run_trecqa.py
```

Possible parameters are:

```
-input (required)
```

Path to the {dev,train,test} TrecQA file. It can be downloaded from [here](http://cs.jhu.edu/~xuchen/packages/jacana-qa-naacl2013-data-results.tar.bz2)

```
-index (required)
```

Path to the Lucene index that has been built over TREC QA collection. 


```
-h0 (optional: positive integer)
```

Number of documents to be retrieved per question (default: 1000)

```
-h1 (optional: positive integer)
```

Top-h1 passages to be re-ranked by the answer extractor (default: 10)

```
-k (optional: list of positive integers)
```

Create run files with depth k (default: 10)

```
-model (optional: idf or sm)
```

Model to re-rank the passages (default: idf)

```
-output (optional: file path)
```

Path of the output folder where the run file, qrel file will be created (default:.)

```
-qrel (optional: file path)
```
Path of an existing qrel file. A new qrel file will be created if this path is not passed.

```
-pattern (optional: pattern file path)
```
Path of the TREC13 QA pattern file `trec13factpats.txt`, which can be found 
[here](http://trec.nist.gov/data/qa/2004_qadata/04.patterns.zip). The file path is a required argument if the type of 
evaluation is pattern based.

```
-qa-model-file (Optional: SM model file path)
```
This path is a required argument if the SM model is used as an answer extractor.

```
-w2v-cache (Optional: word2vec cache file path)
```
The path of the cache file is a required argument if the SM model is used as an answer extractor.

### Sample commands:
```bash
python src/main/python/run_trecqa.py -index  lucene-index.TrecQA.pos+docvectors+rawdocs -input \
jacana-qa-naacl2013-data-results/test-less-than-40.manual-edit.xml  -w2v-cache \
../data/word2vec/aquaint+wiki.txt.gz.ndim=50.cache -qa-model-file sm_cnn.idf_source-corpus-index.punctuation-keep.dash_words-keep.model \ 
-h0 1000 -h1 100 -k 5 -model sm -eval pattern -pattern Trec13/patterns/trec13factpats.txt 
```
The above command is to evaluate an end-to-end pipeline system by using the TREC13 QA patterns and it uses the SM model to 
rerank the answers.
It will create a run file in the `trec_eval` format and a qrel file
called qrel.jaccard.txt. 

```bash
python src/main/python/run_trecqa.py -index lucene-index.TrecQA.pos+docvectors+rawdocs -input \ 
jacana-qa-naacl2013-data-results/test-less-than-40.manual-edit.xml\
-h0 1000 -h1 100 -k 5 -model idf -eval jaccard 
```
The above command is to evaluate an end-to-end pipeline system by using IDF based passage scorer and Jaccard similarity 
as tge evaluation method. 
It will create a run file in the `trec_eval` format and a qrel file
called qrel.jaccard.txt. 

### Calculating RBP:

To calculate RBP for the above run file:

- Install `rbp_eval` from[here](https://github.com/castorini/Anserini/tree/master/eval)
- Read more about RBP[here](http://people.eng.unimelb.edu.au/ammoffat/abstracts/mz08acmtois.html)
- run the following command to get rbp values:

`rbp_eval-0.2/rbp_eval/rbp_eval <qrel-file> <run-file>
`