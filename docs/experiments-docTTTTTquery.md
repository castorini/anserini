# Anserini: docTTTTTquery Experiments

docTTTTTquery is the latest version of [doc2query](experiments-doc2query.md) family of document expansion models.
The basic idea is to train a model, that when given an input document, generates questions that the document might answer (or more broadly, queries for which the document might be relevant).
These predicted questions (or queries) are then appended to the original documents, which are then indexed as before.
docTTTTTquery gets its name from the use of T5 as the expansion model.

For more details, check out our paper:

+ Rodrigo Nogueira and Jimmy Lin.  [From doc2query to docTTTTTquery.](https://cs.uwaterloo.ca/~jimmylin/publications/Nogueira_Lin_2019_docTTTTTquery.pdf)

Why's the paper so short? Check out [our proposal for micropublications](https://github.com/lintool/guide/blob/master/micropublications.md)!

We have [a separate repo](https://github.com/castorini/docTTTTTquery) describing how to replicate our results.
