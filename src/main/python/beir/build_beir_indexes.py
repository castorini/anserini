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


collection_base = '/scratch2/collections'
# Alternatives:
#   - on my iMac Pro: '/System/Volumes/Data/store/collections'
#   - on orca: '/store/collections'

commitid_full = '1842eeffcbf4d18698d401b1c5a4b1c868f32fc6'
commitid_short = '1842ee'
date_abbreviated = '20220501'
date_full = '2022/05/01'

models = ['flat', 'multifield', 'splade-distil-cocodenser-medium']
metrics = ['nDCG@10', 'R@100', 'R@1000']


def generate_flat_command(corpora):
    return f'nohup target/appassembler/bin/IndexCollection \\\n' + \
           f'  -collection BeirFlatCollection \\\n' + \
           f'  -input {collection_base}/beir-v1.0.0/corpus/{corpora} \\\n' + \
           f'  -index indexes/lucene-index.beir-v1.0.0-{corpora}-flat.{date_abbreviated}.{commitid_short}/ \\\n' + \
           f'  -generator DefaultLuceneDocumentGenerator \\\n' + \
           f'  -threads 16 -storePositions -storeDocvectors -storeRaw -optimize \\\n' + \
           f'  >& logs/log.beir-v1.0.0-{corpora}-flat.{date_abbreviated}.{commitid_short} &'


def generate_multifield_command(corpora):
    return f'nohup target/appassembler/bin/IndexCollection \\\n' + \
           f'  -collection BeirMultifieldCollection \\\n' + \
           f'  -input {collection_base}/beir-v1.0.0/corpus/{corpora} \\\n' + \
           f'  -index indexes/lucene-index.beir-v1.0.0-{corpora}-multifield.{date_abbreviated}.{commitid_short}/ \\\n' + \
           f'  -generator DefaultLuceneDocumentGenerator \\\n' + \
           f'  -threads 16 -fields title -storePositions -storeDocvectors -storeRaw -optimize \\\n' + \
           f'  >& logs/log.beir-v1.0.0-{corpora}-multifield.{date_abbreviated}.{commitid_short} &'


def generate_splade_command(corpora):
    return f'nohup target/appassembler/bin/IndexCollection \\\n' +\
           f'  -collection JsonVectorCollection \\\n' + \
           f'  -generator DefaultLuceneDocumentGenerator \\\n' + \
           f'  -input {collection_base}/beir-v1.0.0/splade_distil_cocodenser_medium/{corpora} \\\n' + \
           f'  -index indexes/lucene-index.beir-v1.0.0-{corpora}-splade_distil_cocodenser_medium.{date_abbreviated}.{commitid_short}/ \\\n' + \
           f'  -threads 16 -impact -pretokenized -optimize \\\n' +\
           f'  >& logs/log.beir-v1.0.0-{corpora}-splade_distil_cocodenser_medium.{date_abbreviated}.{commitid_short} &'


for key in beir_keys:
    cmd = generate_flat_command(key)
    print(f'{cmd}\n')
    with open(f'logs/lucene-index.beir-v1.0.0-{key}-flat.{date_abbreviated}.{commitid_short}.README.md', 'w') as f:
        f.write(f'# BEIR (v1.0.0) — {beir_keys[key]}\n\n')
        f.write(f'This **"flat" Lucene index** was generated on {date_full} at Anserini commit [`{commitid_short}`](https://github.com/castorini/anserini/commit/{commitid_full}) on `damiano` with the following command:\n\n')
        f.write('```\n')
        f.write(f'{cmd}\n')
        f.write('```\n')

for key in beir_keys:
    cmd = generate_multifield_command(key)
    print(f'{cmd}\n')
    with open(f'logs/lucene-index.beir-v1.0.0-{key}-multifield.{date_abbreviated}.{commitid_short}.README.md', 'w') as f:
        f.write(f'# BEIR (v1.0.0) — {beir_keys[key]}\n\n')
        f.write(f'This **"multifield" Lucene index** was generated on {date_full} at Anserini commit [`{commitid_short}`](https://github.com/castorini/anserini/commit/{commitid_full}) on `damiano` with the following command:\n\n')
        f.write('```\n')
        f.write(f'{cmd}\n')
        f.write('```\n')

for key in beir_keys:
    cmd = generate_splade_command(key)
    print(f'{cmd}\n')
    with open(f'logs/lucene-index.beir-v1.0.0-{key}-splade_distil_cocodenser_medium.{date_abbreviated}.{commitid_short}.README.md', 'w') as f:
        f.write(f'# BEIR (v1.0.0) — {beir_keys[key]}\n\n')
        f.write(f'This Lucene impact index for **SPLADE-distill CoCodenser-medium** was generated on {date_full} at Anserini commit [`{commitid_short}`](https://github.com/castorini/anserini/commit/{commitid_full}) on `damiano` with the following command:\n\n')
        f.write('```\n')
        f.write(f'{cmd}\n')
        f.write('```\n')
