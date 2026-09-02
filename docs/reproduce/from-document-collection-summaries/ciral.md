# ⚗️ Anserini: Reproductions from Document Collections

[<< Main Landing Page for Reproductions from Document Collections](../../ref-reproduce-from-document-collections.md)

Anserini supports end-to-end reproduction experiments on various standard IR test collections out of the box.
Each of these experiments starts from the raw document collection, builds the necessary index, performs retrieval runs, and generates evaluation results.

## CIRAL (v1.0)

Key:

+ QT = BM25 with human-translated queries and the corpus in the original language (🔑)
+ DT = BM25 with English queries and the corpus translated into English (🔑)

| Language |                           QT                            |                              DT                               |
|----------|:-------------------------------------------------------:|:-------------------------------------------------------------:|
| Hausa    | [🔑](../from-document-collection/ciral-v1.0-ha.md) | [🔑](../from-document-collection/ciral-v1.0-ha-en.md) |
| Somali   | [🔑](../from-document-collection/ciral-v1.0-so.md) | [🔑](../from-document-collection/ciral-v1.0-so-en.md) |
| Swahili  | [🔑](../from-document-collection/ciral-v1.0-sw.md) | [🔑](../from-document-collection/ciral-v1.0-sw-en.md) |
| Yoruba   | [🔑](../from-document-collection/ciral-v1.0-yo.md) | [🔑](../from-document-collection/ciral-v1.0-yo-en.md) |
