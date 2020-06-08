# Indexing and Retrieving a Custom Collection with Anserini

Anserini supports indexing and retrieving a custom collection. 

If you are looking to build a search pipeline with such collection, you might want to try with the following steps. Anserini is suitable for use cases that require
* Essential, comprehensible search capability (BM25 similarity scoring) at first, and
* Flexible, programmable configurations in the future.

### Quickstart 

<b>Prepare your custom collection in Anserini input.</b> 
    
* Write a script (e.g. like [this one](https://github.com/castorini/anserini/blob/master/src/main/python/msmarco/convert_collection_to_jsonl.py)) to convert the collection into the MS MACRO JSON format. 
* The collection will become a directory with <code>.json</code> files. Inside each <code>.json</code> file is either 
	* A JSON Object (one document) or,

		    {
		        "id": "doc1",
		        "contents": "this is the contents."
		    }
		    
	* A JSON Array (multiple documents). 
		 
		    [
		        {
		            "id": "doc1",
		            "contents": "this is the contents 1."
		        },
		        {
		            "id": "doc2",
                    "contents": "this is the contents 2."
		        }
		    ]

		    
* In this way, your data preparation step aligns with the [MS Marco pipeline](https://github.com/castorini/anserini/blob/master/docs/experiments-msmarco-passage.md#data-prep).

<b>Prepare a queries file for your collection</b> 
* One query per line. Each line has the format of `qid[\t]query[\n]`. 

<b>Run the indexing step from the [MS MARCO pipeline](https://github.com/castorini/anserini/blob/master/docs/experiments-msmarco-passage.md)</b>
* This step resembles below line in above documentation. 
    > We can now index these docs as a `JsonCollection` using Anserini: 
    
* That is, you would use an indexing command such as below, 
    
    ```
    sh ./target/appassembler/bin/IndexCollection -collection JsonCollection \
     -generator DefaultLuceneDocumentGenerator -threads 9 -input [YOUR_JSON_COLLECTION_PATH] \
     -index [INDEX_PATH] -storePositions -storeDocvectors -storeRaw 
    ```
<b>Run the retrieval step from the [MS MARCO pipeline](https://github.com/castorini/anserini/blob/master/docs/experiments-msmarco-passage.md#retrieving-and-evaluating-the-dev-set)</b> 
* This step resembles below line in above documentation.   
    > We can now retrieve this ...

* That is, you would use a retrieving command such as below. The output is a <code>.tsv</code> file of top-hit retrieval results.  
    ```
    python ./src/main/python/msmarco/retrieve.py --hits [NUM_OF_HITS_AS_RESULTS] --threads 1 \
     --index [INDEX_PATH] --qid_queries [QUERY_FILE_PATH] \
     --output [OUTPUT_PATH]
    ```
<b>Finally, screen retrieval results, and modify above pipeline to fit your workflow!</b>
