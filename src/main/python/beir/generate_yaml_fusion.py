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

import os.path

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
corpus: beir-v1.0.0-{corpus_short}
corpus_path: collections/beir-v1.0.0/corpus/{corpus_short}/

metrics:
  - metric: nDCG@10
    command: bin/trec_eval
    params: -c -m ndcg_cut.10
    separator: "\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@100
    command: bin/trec_eval
    params: -c -m recall.100
    separator: "\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false
  - metric: R@1000
    command: bin/trec_eval
    params: -c -m recall.1000
    separator: "\t"
    parse_index: 2
    metric_precision: 4
    can_combine: false

topic_reader: TsvString
topics:
  - name: "BEIR (v1.0.0): {corpus_long}"
    id: test
    path: topics.beir-v1.0.0-{corpus_short}.test.tsv.gz
    qrel: qrels.beir-v1.0.0-{corpus_short}.test.txt

# Run dependencies for fusion
runs:
  - name: flat-bm25
    dependency: beir-v1.0.0-{corpus_short}.flat.yaml
    file: runs/run.inverted.beir-v1.0.0-{corpus_short}.flat.test.bm25
  - name: bge-flat-cached
    dependency: beir-v1.0.0-{corpus_short}.bge-base-en-v1.5.parquet.flat.cached.yaml
    file: runs/run.flat.beir-v1.0.0-{corpus_short}.bge-base-en-v1.5.test.bge-flat-cached

methods:
  - name: rrf
    k: 1000
    depth: 1000
    rrf_k: 60
    output: runs/runs.fuse.rrf.beir-v1.0.0-{corpus_short}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-{corpus_short}.test.txt
    results:
      nDCG@10:
        - 0.8714
      R@100:
        - 0.9966
      R@1000:
        - 0.9999
  - name: average
    output: runs/runs.fuse.avg.beir-v1.0.0-{corpus_short}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-{corpus_short}.test.txt
    results:
      nDCG@10:
        - 0.8019
      R@100:
        - 0.9801
      R@1000:
        - 0.9950
  - name: interpolation
    alpha: 0.5
    output: runs/runs.fuse.interp.beir-v1.0.0-{corpus_short}.flat.bm25.bge-base-en-v1.5.bge-flat-cached.topics.beir-v1.0.0-{corpus_short}.test.txt
    results:      
      nDCG@10:
        - 0.8019
      R@100:
        - 0.9801
      R@1000:
        - 0.9950
"""

file_path = 'src/main/resources/fuse_regression/beir-v1.0.0-{key}.yaml'

for key in beir_keys:
    yaml_path = file_path.format(key=key)
    if os.path.exists(yaml_path):
        print(f"YAML for {key} already exists, skipping")
        continue
    
    with open(yaml_path, 'w') as file:
        formatted = yaml_template.format(corpus_short=key, corpus_long=beir_keys[key])
        print(f'Writing yaml for {key}...')
        file.write(formatted)
