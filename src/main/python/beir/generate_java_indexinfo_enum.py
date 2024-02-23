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

beir_keys = [
    'trec-covid',
    'bioasq',
    'nfcorpus',
    'nq',
    'hotpotqa',
    'fiqa',
    'signal1m',
    'trec-news',
    'robust04',
    'arguana',
    'webis-touche2020',
    'cqadupstack-android',
    'cqadupstack-english',
    'cqadupstack-gaming',
    'cqadupstack-gis',
    'cqadupstack-mathematica',
    'cqadupstack-physics',
    'cqadupstack-programmers',
    'cqadupstack-stats',
    'cqadupstack-tex',
    'cqadupstack-unix',
    'cqadupstack-webmasters',
    'cqadupstack-wordpress',
    'quora',
    'dbpedia-entity',
    'scidocs',
    'fever',
    'climate-fever',
    'scifact'
]

beir_to_enum_prefix = {
    'trec-covid': 'BEIR_V1_0_0_TREC_COVID',
    'bioasq': 'BEIR_V1_0_0_BIOASQ',
    'nfcorpus': 'BEIR_V1_0_0_NFCORPUS',
    'nq': 'BEIR_V1_0_0_NQ',
    'hotpotqa': 'BEIR_V1_0_0_HOTPOTQA',
    'fiqa': 'BEIR_V1_0_0_FIQA',
    'signal1m': 'BEIR_V1_0_0_SIGNAL1M',
    'trec-news': 'BEIR_V1_0_0_TREC_NEWS',
    'robust04': 'BEIR_V1_0_0_ROBUST04',
    'arguana': 'BEIR_V1_0_0_ARGUANA',
    'webis-touche2020': 'BEIR_V1_0_0_WEBIS_TOUCHE2020',
    'cqadupstack-android': 'BEIR_V1_0_0_CQADUPSTACK_ANDROID',
    'cqadupstack-english': 'BEIR_V1_0_0_CQADUPSTACK_ENGLISH',
    'cqadupstack-gaming': 'BEIR_V1_0_0_CQADUPSTACK_GAMING',
    'cqadupstack-gis': 'BEIR_V1_0_0_CQADUPSTACK_GIS',
    'cqadupstack-mathematica': 'BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA',
    'cqadupstack-physics': 'BEIR_V1_0_0_CQADUPSTACK_PHYSICS',
    'cqadupstack-programmers': 'BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS',
    'cqadupstack-stats': 'BEIR_V1_0_0_CQADUPSTACK_STATS',
    'cqadupstack-tex': 'BEIR_V1_0_0_CQADUPSTACK_TEX',
    'cqadupstack-unix': 'BEIR_V1_0_0_CQADUPSTACK_UNIX',
    'cqadupstack-webmasters': 'BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS',
    'cqadupstack-wordpress': 'BEIR_V1_0_0_CQADUPSTACK_WORDPRESS',
    'quora': 'BEIR_V1_0_0_QUORA',
    'dbpedia-entity': 'BEIR_V1_0_0_DBPEDIA_ENTITY',
    'scidocs': 'BEIR_V1_0_0_SCIDOCS',
    'fever': 'BEIR_V1_0_0_FEVER',
    'climate-fever': 'BEIR_V1_0_0_CLIMATE_FEVER',
    'scifact': 'BEIR_V1_0_0_SCIFACT'
}

checksums = {
    'trec-covid': 'c391e9c6841e3521355eb2ac837fe248',
    'bioasq': '79844df82809e4daa5eca3ceebf2b935',
    'nfcorpus': 'a5be3e39e5922ad742deff6ba9d53266',
    'nq': 'caa451b4a46126659cbf4ffdaeae335b',
    'hotpotqa': '00ba2207aeacb86bc975d150633ecb09',
    'fiqa': 'aabb1a185ff1eba74db655e0a7e3bb08',
    'signal1m': 'e427da1a196f624644a428b7f4fe5065',
    'trec-news': '13afaccb1981824490cf3ab4694eb45c',
    'robust04': 'f27e12a03545933f44fff674b24cc311',
    'arguana': 'a5c3dde9409a7f8bbab651a0b1dca169',
    'webis-touche2020': '76ad8c91f37654a4f34e20f9aa9bb67b',
    'cqadupstack-android': '26e5f2b3e76c029a4dc9d6c0782bf6fa',
    'cqadupstack-english': '22358a8571b8c9b9483c056c5588d474',
    'cqadupstack-gaming': 'fc6dfc94eca7bd635e93ae41ad3da6db',
    'cqadupstack-gis': 'fb6977d2b2568e3b2ee33a033a63b25d',
    'cqadupstack-mathematica': '6e20741b2322bcda8e808f1ea0c66d26',
    'cqadupstack-physics': 'd8f618d161681ad9918249c1fec4de80',
    'cqadupstack-programmers': '8daf4afd332be000c7dd508d46f019af',
    'cqadupstack-stats': '95d513a64cbeecf956e0ff093354a1bc',
    'cqadupstack-tex': 'd1a770ffb3dd02be9fd19a73b7ac6878',
    'cqadupstack-unix': '70d366f4f3a735dfdd0f47053679e5c9',
    'cqadupstack-webmasters': 'e236cfbd1662112e8ac02ad590544d10',
    'cqadupstack-wordpress': 'e6e90dcb387b769a0c27b7e282326d94',
    'quora': '91bec208040d08caeffafb400ea220b2',
    'dbpedia-entity': 'd9fe11b3033f378ad43773bc11e9b9af',
    'scidocs': 'b7bfe2ae6b0df37b14655c16faeb409b',
    'fever': '480ce0b18ab73ccdecc782eaa820d0e9',
    'climate-fever': 'fa3814f8f20ef2642934bdaad6b12d5a',
    'scifact': '379b2f45873b0df722c63189c485ac29'
}

for key in beir_keys:
    print(f'{beir_to_enum_prefix[key]}_BGE_BASE_EN_15("beir-v1.0.0-{key}-bge-base-en-v1.5",')
    print(f'    "Lucene HNSW index of BEIR collection \'{key}\' encoded by BGE-base-en-v1.5.",')
    print(f'    "lucene-hnsw.beir-v1.0.0-{key}-bge-base-en-v1.5.20240223.43c9ec.tar.gz",')
    print( '    new String[] {')
    print(f'        "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-{key}-bge-base-en-v1.5.20240223.43c9ec.tar.gz"' + ' },')
    print(f'    "{checksums[key]}"),\n')
