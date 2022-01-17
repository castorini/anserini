# Anserini: Reproducibility Notes for MS MARCO V1

<blockquote class="twitter-tweet"><p lang="en" dir="ltr">Reproducibility is hard.</p>&mdash; Jimmy Lin (@lintool) <a href="https://twitter.com/lintool/status/1458853999298465796?ref_src=twsrc%5Etfw">November 11, 2021</a></blockquote>

The context: For MS MARCO V1 document ranking, doc2query-T5 generates expansions for individual document segments (in our approach, sliding window of sentences).
This is due to the length limitations of T5; it is impractical to perform inference on long documents in their entirety all at once.
This segmentation was performed using a version of spaCy in late 2019 by Rodrigo.
At that point in time, we made two mistakes:

1. We did not record the version of spaCy used.
2. We did not materialize the segmented corpus. That is, we did not separately store a copy of the segemented texts.

The doc2query-T5 sentence predictions were made on a collection that contains 20,545,677 segments.
However, when the Anserini regressions were built (specifically, the `msmarco-doc-docTTTTTquery-per-passage` condition), the index only had 20,544,550 segments.
This meant that in the regressions, some relatively small fraction of segments were misaligned with the doc2query-T5 expansions.

We did not discover this discrepancy until November 2021.
This meant that a bug was "set in stone" as "ground truth" in our regression framework.

Separately, around July 2021, Xueguang performed extensive experiments with many many different versions of spaCy and was able to find a version that also produced 20,545,677 segments (spaCy 2.1.6).
This was for dense retrieval experiments, as we were not aware of the doc2query-T5 issues at the time.
It is very likely, but we cannot know for sure, that this was the same segmentation that generated the original doc2query-T5 expansions.
Fortunately, Xueguang was able to save a copy of this segmented corpus.

---

In January 2022, we completely refactored the doc2query-T5 expansion data for the MS MARCO (V1) corpora.
They are now available as Huggingface Datasets:

+ [`msmarco_v1_passage_doc2query-t5_expansions`](https://huggingface.co/datasets/castorini/msmarco_v1_passage_doc2query-t5_expansions): passage expansions
+ [`msmarco_v1_doc_doc2query-t5_expansions`](https://huggingface.co/datasets/castorini/msmarco_v1_doc_doc2query-t5_expansions): document expansions
+ [`msmarco_v1_doc_segmented_doc2query-t5_expansions`](https://huggingface.co/datasets/castorini/msmarco_v1_doc_segmented_doc2query-t5_expansions): document segment expansions

So now we have the following new regressions:

+ `msmarco-doc`: document corpus in Anserini's jsonl format with 3,213,835 documents. Each contains URL, title, body, delimited by newlines.
+ `msmarco-doc-docTTTTTquery`: same as above, but with docTTTTTquery expansions, delimited by another newline.
+ `msmarco-segmented`: segmented document corpus in Anserini's jsonl format with 20,545,677 segments. Each contains URL, title, segment, delimited by newlines.
+ `msmarco-segmented-docTTTTTquery`: same as above, but with docTTTTTquery expansions, delimited by another newline.

These new versions yield end-to-end scores that are slightly different, so if numbers reported in a paper do not exactly match, this may be the reason.

*TODO:* Circle back and add links to scripts once everything has been verified and checked in.
