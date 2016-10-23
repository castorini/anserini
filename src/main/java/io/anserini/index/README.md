DumpIndex provides an easy way to print various information of the index via CLI

`-index`

The path of the index

`-s`

Print statistics for the Repository: total docs count, total terms count, etc.

`-t`

Print term info: stemmed, total counts, doc counts

`-di`

Print the internal document IDs of documents

`-dn`

Print the text representation of a document ID
The document ID is the internal document ID starting from 1

`-dt`

Print the text of a document (only if the raw documents are stored)
The document ID is the internal document ID starting from 1

`-dv`

Print the document vector of a document (only if the document vectors are stored)
The document ID is the internal document ID starting from 1