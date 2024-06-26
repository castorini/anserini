# Webapp and REST API

Anserini has a built-in webapp for interactive querying along with a REST API that can be used by other applications.

## Setup

Start the server with

```bash
java -cp $ANSERINI_JAR io.anserini.server.Application --server.port=8081
```

And then navigate to [`http://localhost:8081/`](http://localhost:8081/) in your browser.

## List Indexes

To list all the index information, the endpoint is `api/v1.0/indexes/`

Run

```bash
curl -X GET "http://localhost:8081/api/v1.0/indexes"
```

Output is a mapping from index name to `IndexInfo` enum

```json
{
    "cacm": {
        "urls": [
            "https://github.com/castorini/anserini-data/raw/master/CACM/lucene-index.cacm.20221005.252b5e.tar.gz"
        ],
        "cached": false,
        "md5": "cfe14d543c6a27f4d742fb2d0099b8e0",
        "indexName": "cacm",
        "description": "Lucene index of the CACM corpus.",
        "model": "BM25",
        "corpus": "CACM",
        "filename": "lucene-index.cacm.20221005.252b5e.tar.gz"
    },
    "msmarco-v1-passage": {
        "urls": [
            "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz"
        ],
        "cached": true,
        "md5": "678876e8c99a89933d553609a0fd8793",
        "indexName": "msmarco-v1-passage",
        "description": "Lucene index of the MS MARCO V1 passage corpus.",
        "model": "BM25",
        "corpus": "MS MARCO V1 Passage",
        "filename": "lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz"
    },
    ...
}
```

## Search Queries

The search query endpoint is `api/v1.0/indexes/{index_name}/search?query={query}&hits={hits}&qid={qid}`

Path variables:

- `index_name`: The index name to query. Default is "msmarco-v1-passage"

Query parameters:

- `query`: The query string to search for. Required
- `hits`: The number of hits to return. Default is 10
- `qid`: The query ID. Default is ""

Here's a specific example of using the REST API to issue the query "How does the process of digestion and metabolism of carbohydrates start" to `msmarco-v2.1-doc`:

```bash
curl -X GET "http://localhost:8081/api/v1.0/indexes/msmarco-v2.1-doc/search?query=How%20does%20the%20process%20of%20digestion%20and%20metabolism%20of%20carbohydrates%20start" 
```

The json results are the same as the output of the `-outputRerankerRequests` option in `SearchCollection`

```json
{
  "query": {
    "text": "How does the process of digestion and metabolism of carbohydrates start",
    "qid": 2000138
  },
  "candidates": [
    {
      "docid": "msmarco_v2.1_doc_15_390497775",
      "score": 14.3364,
      "doc": {
        "url": "https://diabetestalk.net/blood-sugar/conversion-of-carbohydrates-to-glucose",
        "title": "Conversion Of Carbohydrates To Glucose | DiabetesTalk.Net",
        "headings": "...",
        "body": "..."
      }
    },
    {
      "docid": "msmarco_v2.1_doc_15_416962410",
      "score": 14.2271,
      "doc": {
        "url": "https://diabetestalk.net/insulin/how-is-starch-converted-to-glucose-in-the-body",
        "title": "How Is Starch Converted To Glucose In The Body? | DiabetesTalk.Net",
        "headings": "...",
        "body": "..."
      }
    },
    ...
  ]
}
```

## Get Document Content by DocId

To access the content of a document in an index, the endpoint is `api/v1.0/indexes/{index_name}/document/{docid}`

Here's an example of getting the document of the top candidate from the above example:

```bash
curl -X GET "http://localhost:8080/api/v1.0/indexes/msmarco-v2.1-doc/documents/msmarco_v2.1_doc_15_390497775"
```

Output is an object of the same format as a candidate from search

```json
{
    "doc": {
        "url": "https://diabetestalk.net/blood-sugar/conversion-of-carbohydrates-to-glucose",
        "title": "Conversion Of Carbohydrates To Glucose | DiabetesTalk.Net",
        "headings": "...",
        "body": "..."
    }
}
```

## Check Index Status

To check whether an index is cached, the endpoint is `api/v1.0/indexes/{index_name}/status`

Here's an example of checking the status of `msmarco-v1-passage`:

```bash
curl -X GET "http://localhost:8081/api/v1.0/indexes/msmarco-v1-passage/status"
```

Output is an object containing the 'cached' property

```json
{
  "cached": true
}
```
