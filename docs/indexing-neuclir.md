# Indexing the NeuCLIR Document Collection

This page contains instructions on how to index the [NeuCLIR collection](https://neuclir.github.io/neuclir1.html) on 
Anserini. The collection contains data in 3 languages (Chinese, Persian, and Russian) drawn from the Common Crawl news collection.

# Downloading the dataset

- The NeuCLIR1 document collection is available for download by those registered for TREC 2022 on the [NIST website](https://trec.nist.gov/act_part/tracks2022.html).
  
    OR

- Clone the [NeuCLIR Repository](https://github.com/NeuCLIR/download-collection) and follow the download instructions.

## Indexing

- Typical indexing command:

    ```
    target/appassembler/bin/IndexCollection \
      -collection NeuClirCollection \
      -input /path/to/language_corpus \
      -index indexes/lucene-index.neuclir.${lang}/ \
      -generator DefaultLuceneDocumentGenerator \
      -threads 1 -storePositions -storeDocvectors -storeRaw  -language ru \
      >& logs/log.neuclir &
    ```

you can find more indexing options at [common-indexing-options](common-indexing-options.md)