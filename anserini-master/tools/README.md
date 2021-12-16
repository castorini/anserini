# Anserini Tools

This repo holds various tools and scripts shared across [anserini](http://anserini.io/), [pyserini](http://pyserini.io/), and [pygaggle](http://pygaggle.ai/) as a Git submodule.

Build the included evaluation tools as follows (you might get warnings, but you can ignore):

```bash
cd eval && tar xvfz trec_eval.9.0.4.tar.gz && cd trec_eval.9.0.4 && make && cd ../..
cd eval && cd ndeval && make && cd ../..
```
