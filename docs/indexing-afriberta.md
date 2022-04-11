# Indexing the AfriBERTa Corpus

This document contains instructions to index the [AfriBERTa corpus](https://aclanthology.org/2021.mrl-1.11/) hosted on 
huggingface at https://huggingface.co/datasets/castorini/afriberta-corpus. The corpus contains train and evaluation sets
in 10 African languages.

# Languages
- Afaanoromoo
- Amharic
- Gahuza
- Hausa
- Igbo
- Pidgin
- Somali
- Swahili
- Tigrinya
- Yoruba

## Indexing

Typical indexing command:

```
target/appassembler/bin/IndexCollection \
  -collection AfribertaCollection \
  -input /path/to/language_corpus \
  -index indexes/lucene-index.afriberta.lang/ \
  -generator DefaultLuceneDocumentGenerator \
  -threads 1 -storePositions -storeDocvectors -storeRaw  -language langcode \
  >& logs/log.afriberta &
```

you can find more indexing options at [common-indexing-options](common-indexing-options.md)