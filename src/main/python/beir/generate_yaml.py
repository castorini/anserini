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

beir_keys = {
    'trec-covid': 'TREC-COVID',
    'bioasq': 'BioASQ',
    'nfcorpus': 'NFCorpus',
    'nq': 'NQ',
    'hotpotqa': 'HotpotQA',
    'fiqa': 'FiQA-2018',
    'signal1m': 'Signal-1M',
    'trec-news': 'TREC-NEWS',
    'robust04': 'Robust04',
    'arguana': 'ArguAna',
    'webis-touche2020': 'Webis-Touche2020',
    'cqadupstack-android': 'CQADupStack-android',
    'cqadupstack-english': 'CQADupStack-english',
    'cqadupstack-gaming': 'CQADupStack-gaming',
    'cqadupstack-gis': 'CQADupStack-gis',
    'cqadupstack-mathematica': 'CQADupStack-mathematica',
    'cqadupstack-physics': 'CQADupStack-physics',
    'cqadupstack-programmers': 'CQADupStack-programmers',
    'cqadupstack-stats': 'CQADupStack-stats',
    'cqadupstack-tex': 'CQADupStack-tex',
    'cqadupstack-unix': 'CQADupStack-unix',
    'cqadupstack-webmasters': 'CQADupStack-webmasters',
    'cqadupstack-wordpress': 'CQADupStack-wordpress',
    'quora': 'Quora',
    'dbpedia-entity': 'DBPedia',
    'scidocs': 'SCIDOCS',
    'fever': 'FEVER',
    'climate-fever': 'Climate-FEVER',
    'scifact': 'SciFact'
}


yaml_template = """---
corpus: beir-v1.0.0-bge-base-en-v1.5
corpus_path: collections/beir-v1.0.0/bge-base-en-v1.5/{corpus_short}

index_path: indexes/lucene-index.beir-v1.0.0-{corpus_short}-bge-base-en-v1.5/
index_type: hnsw
collection_class: JsonDenseVectorCollection
generator_class: HnswDenseVectorDocumentGenerator
index_threads: 16
index_options: -M 16 -efC 100 -memoryBuffer 65536 -noMerge

metrics:
  - metric: nDCG@10
    command: target/appassembler/bin/trec_eval
    params: -c -m ndcg_cut.10
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@100
    command: target/appassembler/bin/trec_eval
    params: -c -m recall.100
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@1000
    command: target/appassembler/bin/trec_eval
    params: -c -m recall.1000
    separator: "\\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false

topic_reader: JsonStringVector
topics:
  - name: "BEIR (v1.0.0): {corpus_long}"
    id: test
    path: topics.beir-v1.0.0-{corpus_short}.test.bge-base-en-v1.5.jsonl.gz
    qrel: qrels.beir-v1.0.0-{corpus_short}.test.txt

models:
  - name: bge-hnsw
    display: BGE-base-en-v1.5
    type: hnsw
    params: -generator VectorQueryGenerator -topicField vector -threads 16 -hits 1000 -efSearch 1000
    results:
      nDCG@10:
        - 0.453
      R@100:
        - 0.992
      R@1000:
        - 0.996
"""

for key in beir_keys:
    with open(f'src/main/resources/regression/beir-v1.0.0-{key}-bge-base-en-v1.5.yaml', 'w') as file:
        formatted = yaml_template.format(corpus_short=key, corpus_long=beir_keys[key])
        print(f'Writing yaml for {key}...')
        file.write(formatted)
