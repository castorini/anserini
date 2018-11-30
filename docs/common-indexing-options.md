# Anserini: Common Indexing Options

Common indexing options:

+ `-storePositions`: builds a standard positional index
+ `-storeDocvectors`: stores doc vectors (required for relevance feedback)
+ `-storeRawDocs`: stores raw documents
+ `-optimize`: merges index into a single segment (slow for large collections)
+ `-threads`: number of threads (_NOTICE:_ number of unique terms is only available if the index is built using 1 thread)
+ `-uniqueDocid`: Anserini by default does not explicitly remove the duplicated docids when indexing
Enabling this flag will remove duplicated documents with the same doc id when indexing
Please note that this option may slow the indexing a lot so if you are sure there is no duplicated document ids in the
corpus you shouldn't use this option
+ `-whitelist`: file containing docids, one per line; only specified docids will be indexed

Note: For Solr highlighting to work, the `-storeTransformedDocs` flag needs to be passed to ensure the text and positions are stored in the same field.
