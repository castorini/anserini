# Anserini: Reproducibility Notes for MS MARCO V1 Doc Ranking

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">Reproducibility is hard.</p>&mdash; Jimmy Lin (@lintool) <a href="https://twitter.com/lintool/status/1458853999298465796?ref_src=twsrc%5Etfw">November 11, 2021</a></blockquote>

The context: For MS MARCO V1 document ranking, doc2query-T5 generates expansions for individual document segments (in our approach, sliding window of sentences).
This is due to the length limitations of T5; it is impractical to perform inference on long documents in their entirety all at once.
This segmentation was performed using a version of spaCy in late 2019 by Rodrigo.
At that point in time, we made two mistakes:

1. We did not record the version of spaCy used.
2. We did not materialize the segmented corpus. That is, we did not separately store a copy of the segemented texts.

The doc2query-T5 sentence predictions were made on a collection that contains 20,545,677 segments.
However, when the Anserini regressions were built (specifically, the [`msmarco-doc-docTTTTTquery-per-passage`](regressions-msmarco-doc-docTTTTTquery-per-passage.md) condition), the index only had 20,544,550 segments.
This meant that in the regressions, some relatively small fraction of segments were misaligned with the doc2query-T5 expansions.

We did not discover this discrepancy until November 2021.
This meant that a bug was "set in stone" as "ground truth" in our regression framework.

Separately, around July 2021, Xueguang performed extensive experiments with many many different versions of spaCy and was able to find a version that also produced 20,545,677 segments (spaCy 2.1.6).
This was for dense retrieval experiments, as we were not aware of the doc2query-T5 issues at the time.
It is very likely, but we cannot know for sure, that this was the same segmentation that generated the original doc2query-T5 expansions.
Fortunately, Xueguang was able to save a copy of this segmented corpus.

So, now we have:

+ `doc-per-passage-v2`: materialized corpus with 20,545,677 segments.
+ `doc-per-passage-v3`: same as above, except with URL and title (delimited by newlines). Note that bag-of-words search over this variant yields higher effectiveness than above, but for input to an encoder, you probably don't want to include the URL.
+ `doc-docTTTTTquery-per-passage3`: `doc-per-passage-v3`, but with the doc2query-T5 expansions added in.

And, for symmetry:

+ `doc-v3`: this is the "per-doc" counterpart of `doc-per-passage-v3`. This differs slightly from `doc`, which is based on the corpus in TREC doc format.
+ `doc-docTTTTTquery-per-doc-v3`: `doc-v3`, but with the doc2query-T5 expansions added in.
