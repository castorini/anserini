# Axiomatic Reranking
How to use
==========

To use it with BM25:
==========
```
Anserini/target/appassembler/bin/SearchCollection -topicreader Trec -index /path/to/index/ -hits 1000 -topics Anserini/src/main/resources/topics-and-qrels/topics.51-100.txt -bm25 -axiom -axiom.beta 0.4 -output run_axiom_beta_0.4.txt
```

To use it with Dirichlet Language Model:
==========
```
Anserini/target/appassembler/bin/SearchCollection -topicreader Trec -index /path/to/index/ -hits 1000 -topics Anserini/src/main/resources/topics-and-qrels/topics.51-100.txt -ql -axiom -axiom.beta 0.4 -output run_axiom_beta_0.4.txt
```

Algorithm
==========
1. Rank the documents and pick the top _M_ documents as the reranking documents pool _RP_
2. Randomly select _(R-1)*M_ documents from the index and add them to _RP_ so that we have _R*M_ documents in the reranking pool
3. Build the inverted term-docs list _RTL_ for _RP_
4. For each term in _RTL_, calculate its reranking score as the mutual information between query terms and itself: ```s(q,t)=I(X_q, X_t|RP)=SUM(p(X_q,X_t|W)*log(p(X_q,X_t|W)/p(X_q|W)/p(X_t|W)))``` where `X_q` and `X_t` are two binary random variables that denote the presence/absence of query term q and term t in the document.
5. The final reranking score of each term _t_ in _RTL_ is calculated by summing up its scores for all query terms: ```s(t) = SUM(s(q,t))```
6. Pick top _K_ terms from _RTL_ based on their reranking scores with their weights _s(t)_
7. Rerank the documents by using the _K_ reranking terms with their weights. In Lucene, it is something like _(term1^0.2 term2^0.01 ...)_

Notes
==========
Axiomatic Reranking algorithm is a non-deterministic algorithm since it randomly pick _(R-1)*M_ documents as part of the reranking pool (see algorithm above for details). Here we just list the performance references for major TREC collections. The ranking model we used is _BM25_ and the parameter _beta_ is set as 0.4 for all collections although this is definitely not the optimal value for individual collection. We report MAP for all collections except ClueWeb collections where ndcg@20 is reported.


Please refer to the paper [[Yang et al, 2013] Yang, P., and Fang, H. (2013). Evaluating the Effectiveness of Axiomatic Approaches in Web Track. In TREC 2013.](https://trec.nist.gov/pubs/trec22/papers/udel_fang-web.pdf) for more details.

