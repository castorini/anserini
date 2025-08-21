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

from pyserini.index.lucene import LuceneIndexReader

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


yaml_template = """---
corpus: bright-{corpus_short}.bge-large-en-v1.5
corpus_path: collections/bright/bge-large-en-v1.5/{corpus_short}

index_path: indexes/lucene-flat.bright-{corpus_short}.bge-large-en-v1.5/
index_type: flat
collection_class: JsonDenseVectorCollection
generator_class: DenseVectorDocumentGenerator
index_threads: 16
index_options: ""

metrics:
  - metric: nDCG@10
    command: bin/trec_eval
    params: -c -m ndcg_cut.10
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@100
    command: bin/trec_eval
    params: -c -m recall.100
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@1000
    command: bin/trec_eval
    params: -c -m recall.1000
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false

topic_reader: JsonStringVector
topics:
  - name: "BRIGHT: {corpus_long}"
    id: topics
    path: topics.bright-{corpus_short}.bge-large-en-v1.5.jsonl.gz
    qrel: qrels.bright-{corpus_short}.txt

models:
  - name: bge-flat-cached
    display: BGE-large-en-v1.5
    type: flat
    params: -hits 1000 -removeQuery -threads 16
    results:
      nDCG@10:
        - 0.3952
      R@100:
        - 0.4469
      R@1000:
        - 0.7051
"""

for key in bright_keys:
    with open(f'src/main/resources/regression/bright-{key}.bge-large-en-v1.5.flat.cached.yaml', 'w') as file:
        formatted = yaml_template.format(corpus_short=key, corpus_long=bright_keys[key])
        print(f'Writing yaml for {key}...')
        file.write(formatted)
