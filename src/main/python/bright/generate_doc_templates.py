#
# Anserini: A Lucene toolkit for reproducible information retrieval research
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

bright_keys = {
    'biology': 'Biology',
    'earth-science': 'Earth Science',
    'economics': 'Economics',
    'psychology': 'Psychology',
    'robotics': 'Robotics',
    'stackoverflow': 'Stack Overflow',
    'sustainable-living': 'Sustainable Living',
    'leetcode': 'LeetCode',
    'pony': 'Pony',
    'aops': 'AoPS',
    'theoremqa-theorems': 'TheoremQA-T',
    'theoremqa-questions': 'TheoremQA-Q'
}

doc_template1 = """# Anserini Regressions: BRIGHT &mdash; {corpus_long}

**Model**: [BGE-large-en-v1.5](https://huggingface.co/BAAI/bge-large-en-v1.5) with flat indexes (using ONNX for on-the-fly query encoding)

This page documents regression experiments, integrated into Anserini's regression testing framework, for [BRIGHT &mdash; {corpus_long}](https://brightbenchmark.github.io/) using [BGE-large-en-v1.5](https://huggingface.co/BAAI/bge-large-en-v1.5).
The model itself can be download [here](https://huggingface.co/BAAI/bge-large-en-v1.5).
See the following paper for more details:

> Shitao Xiao, Zheng Liu, Peitian Zhang, and Niklas Muennighoff. [C-Pack: Packaged Resources To Advance General Chinese Embedding.](https://arxiv.org/abs/2309.07597) _arXiv:2309.07597_, 2023.

In these experiments, we are using ONNX to perform query encoding on the fly.

"""

doc_template2 = """The exact configurations for these regressions are stored in [this YAML file](${yaml}).
Note that this page is automatically generated from [this template](${template}) as part of Anserini's regression pipeline, so do not modify this page directly; modify the template instead and build Anserini to rebuild the documentation.

From one of our Waterloo servers (e.g., `orca`), the following command will perform the complete regression, end to end:

```
python src/main/python/run_regression.py --index --verify --search --regression ${test_name}
```

All the BRIGHT corpora, encoded by the BGE-large-en-v1.5 model, are available for download:

```bash
wget https://huggingface.co/datasets/castorini/collections-bright/resolve/main/bright-bge-large-en-v1.5.tar -P collections/
tar xvf collections/bright-bge-large-en-v1.5.tar -C collections/
```

The tarball is 13 GB and has MD5 checksum `0ce2634d34d3d467cd1afd74f2f63c7b`.
After download and unpacking the corpora, the `run_regression.py` command above should work without any issue.

## Indexing

Typical indexing command, building flat indexes:

```
${index_cmds}
```

The path `/path/to/${corpus}/` should point to the corpus downloaded above.

## Retrieval

Topics and qrels are stored [here](https://github.com/castorini/anserini-tools/tree/master/topics-and-qrels), which is linked to the Anserini repo as a submodule.

After indexing has completed, you should be able to perform retrieval as follows:

```
${ranking_cmds}
```

Evaluation can be performed using `trec_eval`:

```
${eval_cmds}
```

## Effectiveness

With the above commands, you should be able to reproduce the following results:

${effectiveness}

With ONNX query encoding on non-quantized flat indexes, observed results may differ slightly (typically, lower), but scores should generally be within 0.001 of the results reported above (with some outliers).
"""

for key in bright_keys:
    with open(f'src/main/resources/docgen/templates/bright-{key}.bge-large-en-v1.5.flat.onnx.template', 'w') as file:
        formatted = doc_template1.format(corpus_long=bright_keys[key])
        print(f'Writing doc template for {key}...')
        file.write(formatted)
        file.write(doc_template2)