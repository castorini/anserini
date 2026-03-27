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


- @kwamearhinPORTFL (a5393dd): Reproduced BM25 baselines for MS MARCO passage ranking. Obtained expected results (MRR@10 ≈ 0.187, MAP ≈ 0.1957, Recall@1000 ≈ 0.8573). Environment: WSL2 Ubuntu 22.04, OpenJDK 21, Python 3. Dense retrieval attempted but not completed due to hardware limitations.


