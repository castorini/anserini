### Segmenting collection

`segment.py` can be called from command line by specifying the following arguments:  
- `--input {path to input directory containing collection}`
- `--collection {collection class}`
- `--generator {generator class}`
- `--output {path to create output collection directory}`
- `--threads {max number of threads} `
- `--tokenize {tokenizing function to call}`
- `--raw` if raw text to be used instead of transformed body contents  

### Python-Java Bridging

`collection_iterator.py` replicates `IndexCollection` logic for iterating over collections and generating Lucene documents with:
- id (FIELD_ID) 
- parsed contents (FIELD_BODY)
- raw contents (FIELD_RAW)  

`utils.py` contains Java classes from Anserini accessed with Pyjnius.  

### Document Tokenizing

Instead of performing indexing steps in `IndexCollection`, a tokenizer function defined in `document_tokenizer.py` 
can be called on either the parsed or raw document content to split into segments and output as JSON arrays into a JsonCollection:  

```
[
    {
        "id":"{$DOCID}.000000",
        "content":"{segment-content}"
	},
    {
        "id":"{$DOCID}.000001",
        "content":"{segment-content}"
    }
]
```