Anserini Additional Documentation
========

+ [Axiomatic Reranking](docs/axiom-reranking.md)
+ `IndexUtils` is a utility to interact with an index using the command line (e.g., print index statistics). Refer to `target/appassembler/bin/IndexUtils -h` for more details.
+ `MapCollections` is a generic mapper framework for processing a document collection in parallel. Developers can write their own mappers for different tasks: one simple example is `CountDocumentMapper` which counts the number of documents in a collection:

   ```
   target/appassembler/bin/MapCollections -collection ClueWeb09Collection \
     -threads 16 -input ~/collections/web/ClueWeb09b/ClueWeb09_English_1/ \
     -mapper CountDocumentMapper -context CountDocumentMapperContext
   ```
