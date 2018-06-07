# Anserini: Common Indexing Options

Common indexing options:

+ `-storePositions`: builds a standard positional index
+ `-storeDocvectors`: stores doc vectors (required for relevance feedback)
+ `-storeRawDocs`: stores raw documents
+ `-optimize`: merges index into a single segment (slow for large collections)
