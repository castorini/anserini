# Reproducibility vs. Replicability

The terms "reproducibility" and "replicability" are often used in imprecise and confusing ways.
In the context of Anserini, we use these terms as defined by ACM's [Artifact Review and Badging](https://www.acm.org/publications/policies/artifact-review-and-badging-current) Policy.
Note that the policy itself is confusing in that a previous version of the policy had the meaning of "reproducibility" and "replicability" swapped.

To be precise, per the policy:

+ Repeatability = same team, same experimental setup
+ Reproducibility = different team, same experimental setup
+ Replicability = different team, different experimental setup

In this context, if you are able to run our code and get the same results, then you have successfully _reproduced_ our results.
For the most part, replicability is not applicable in the context of Anserini, because the term implies a different implementation.

At the bottom of many pages you'll find a "Reproduction Log", which keeps track of users who have successfully reproduced the results reported on that page.
Note that we stretch the meaning of "same team" a bit in these logs: we still consider it a successful reproduction if another member of our research group is able to obtain the same results, as long as the person was not the primary author of the code in question.


## 2026-03-18 (commit: XXXXXXX)

Successfully reproduced the BM25 baseline for MS MARCO Passage Ranking using both Anserini and Pyserini.

Environment:
- OS: Windows 11 with WSL2 (Ubuntu 22.04)
- Java: OpenJDK 21
- Python: 3.x (pyserini-final environment)

Anserini:
- Built Lucene index and ran BM25 retrieval (k1=0.82, b=0.68)
- Evaluated with trec_eval
- Results:
  - MRR@10 ≈ 0.1875
  - MAP ≈ 0.1957
  - Recall@1000 ≈ 0.8573

Pyserini:
- Reused Anserini index
- Ran BM25 retrieval via Python
- Converted MS MARCO → TREC format
- Results:
  - MRR@10 ≈ 0.1874
  - MAP ≈ 0.1957
  - Recall@1000 ≈ 0.8573

Interactive Retrieval:
- Attempted using LuceneSearcher
- Limited by hardware when loading index
- Understood query execution and result inspection

Dense Retrieval:
- Attempted but not completed due to hardware limitations (memory constraints)
- Understood dense retrieval concepts and improvements over BM25

Notes:
- Avoided redundant steps (e.g., re-indexing)
- All completed results match expected baselines
