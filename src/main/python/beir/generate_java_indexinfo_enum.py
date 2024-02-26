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

checksums_flat = {
    'trec-covid': '57b812594b11d064a23123137ae7dade',
    'bioasq': 'cf8d4804b06bb8678d30b1375b46a0b3',
    'nfcorpus': '34c0b11ad13a4715a78d025902061d37',
    'nq': 'a2c5db4dd3780fff3c7c6bfea1dd08e8',
    'hotpotqa': '3be2875f93537369641287dcdf25add9',
    'fiqa': '409b779e8a39813d2fbdfd1ea2f009e9',
    'signal1m': 'd0828b92a3df814bfa4b73bddeb25da7',
    'trec-news': '98df3de34b4b76a4390520c606817ec4',
    'robust04': '89dfcb7297c12a772d1bfd7917df908d',
    'arguana': 'd6c005689a9e7e91f3b1a7fbc74063e1',
    'webis-touche2020': '20c6e9f29461eea1a520cd1abead709a',
    'cqadupstack-android': '9f9f35e34f76336bc6e516599cbaf75b',
    'cqadupstack-english': '7d887497d32eedd92c314c93feaca28e',
    'cqadupstack-gaming': '140e16ee86a69c8fd4d16a83a6d51591',
    'cqadupstack-gis': '4bd93695f28af0a11172f387ef41fee6',
    'cqadupstack-mathematica': '5b5b7ab3d0437428e29a5a1431de1ca5',
    'cqadupstack-physics': '6864144bca1bb169a452321e14ef12e0',
    'cqadupstack-programmers': '7b7d2bbf7cc5d53924d09c3b781dba8a',
    'cqadupstack-stats': '0b09b7bee2b60df0ff73710a93a79218',
    'cqadupstack-tex': '48a2541bd7d1adec06f053486655e815',
    'cqadupstack-unix': 'a6cc0a867f6210ad44755c0a36fd682a',
    'cqadupstack-webmasters': 'a04f65d575b4233a151c4960b82815b9',
    'cqadupstack-wordpress': '4ab079b9f7d0463955ce073b5d53e64d',
    'quora': '53fa2bd0667d23a50f95adaf169b87a1',
    'dbpedia-entity': '6bc15a920e262d12ec3842401755e934',
    'scidocs': 'f1fba96a71a62bc567ecbd167de3794b',
    'fever': '1b06f43ea36e2ed450d1b1d90099ae67',
    'climate-fever': '68811e2614b3bac9e1b879c883fc722e',
    'scifact': '6f6e55f1cf80c362f86bee65529b71de'
}

checksums_multifield = {
    'trec-covid': '7501a330a0c9246e6350413c3f6ced7c',
    'bioasq': 'cc01ab450cac0b8865bd1e70e2a58596',
    'nfcorpus': '904e53b80fe04b3844b97847bc77a772',
    'nq': '693ca315de9fbbbf7f664be313a03847',
    'hotpotqa': 'ef8c2f40097e652eec99e6bf25e151cd',
    'fiqa': '073f3f19a94689e5fac511af49316fe1',
    'signal1m': '4482ae02f18e8336c0a95ea33b5b6ede',
    'trec-news': '3151122da3cf081a0c8894af7b75be43',
    'robust04': 'fdf741a75efe089d0451de5720b52c3a',
    'arguana': 'a8201952860d31c56ea8a54c31e88b51',
    'webis-touche2020': 'e160ea813990cff4dbdb9f50d509f8ea',
    'cqadupstack-android': 'de85f92a018d83a7ea496d9ef955b8c5',
    'cqadupstack-english': '71c5d3db04586283772f6069668f5bfa',
    'cqadupstack-gaming': 'ff7c628b568f916c3bc3f7bf2af831eb',
    'cqadupstack-gis': '4083830da4922d1294b3fb38873ba5a2',
    'cqadupstack-mathematica': 'baa9414c385db88eaafffa95d5ec7d48',
    'cqadupstack-physics': '342b105462067b87e78730921dd7288d',
    'cqadupstack-programmers': '2e95b82caf156d0f0b109c62e0011eab',
    'cqadupstack-stats': '87c53df624baed7921672286beb94f9c',
    'cqadupstack-tex': '86407171e4ff305ecb173afdd49eef7c',
    'cqadupstack-unix': 'acb0cc50cccb9e8dfca0ed599df0cfaa',
    'cqadupstack-webmasters': '7701f016b6fc643c30630742f7712bbd',
    'cqadupstack-wordpress': 'd791cf8449a18ebe698d404f526375ee',
    'quora': '2d92b46f715df08ce146167ed1b12079',
    'dbpedia-entity': 'b3f6b64bfd7903ff25ca2fa01a288392',
    'scidocs': '04c1e9aad3751dc552027d8bc3491323',
    'fever': '28ea09308760235ea2ec72d6f9b2f432',
    'climate-fever': '827f2759cdfc45c47bbb67835cfcb1f2',
    'scifact': 'efbafbc3e4909a026fe80bf8b1444b08'
}

checksums_splade = {
    'arguana': '59be25716db84b574f503a1680824c6d',
    'bioasq': 'd153c06c23bcc6c1a1c9617d3defcef9',
    'climate-fever': '32e7d4e30fa28c66db83722bf1ba7fd2',
    'cqadupstack-android': 'e5179184bf85d2c18ae98be033674208',
    'cqadupstack-english': 'e99b9439465c8038794873fdef9478fa',
    'cqadupstack-gaming': 'cd1248b1ecaa3284f1b7fcad4e6afae6',
    'cqadupstack-gis': '42a1c93fd7a012a34e7cd872c4b87528',
    'cqadupstack-mathematica': '3cb36e0043de37f47e1cb0fb5ea5d07c',
    'cqadupstack-physics': 'adf4d56e558cd2503a2b72214cc50950',
    'cqadupstack-programmers': '042c2ef13a09b6da5a924b1db72a967b',
    'cqadupstack-stats': 'f5fa111b03094cd6351f0a6a6ed9cb03',
    'cqadupstack-tex': 'aa2fa8df7e9dd834967519738f7b6666',
    'cqadupstack-unix': '4fef94bad65d1374bce9532fd5bd1689',
    'cqadupstack-webmasters': 'b883e6e3bb444689378d15af308280da',
    'cqadupstack-wordpress': 'eda8eb8917514c64b43f5eaafde1a50b',
    'dbpedia-entity': '2598e1588671d249c024ce7d44d2fec2',
    'fever': '11f2e5c2259a55cc82052bed11a29039',
    'fiqa': '4dd93efc25f77afceb7d409211863b7b',
    'hotpotqa': '415c855c411681dc43012f905d9826a3',
    'nfcorpus': 'f0d5659c4483ecb6fe8e32409ecd5002',
    'nq': '34ebea38ab05066f7a8dc45f72f88d57',
    'quora': '7c0fea9ccae8db35fabc8a5f329ccb3c',
    'robust04': 'a454bb33b6edb3b057f37c32d8712f4a',
    'scidocs': 'b3b643dc2c09d3d68660ab796ac96ac2',
    'scifact': 'f8b03611fbb322a8f860a15e8ba52b14',
    'signal1m': 'f12141cdbe242511f3dca72d03b87d0a',
    'trec-covid': '09c7bc8500e8c70bfb2134556261e6e2',
    'trec-news': 'cc86753ff81ee0bcabde75b537d1bea6',
    'webis-touche2020': 'c7ae4e8458e1ecec2e879beb6547d08f'
}

checksums_bge = {
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

# for key in beir_keys:
#     print(f'{beir_to_enum_prefix[key]}_BGE_BASE_EN_15("beir-v1.0.0-{key}-bge-base-en-v1.5",')
#     print(f'    "Lucene HNSW index of BEIR collection \'{key}\' encoded by BGE-base-en-v1.5.",')
#     print(f'    "lucene-hnsw.beir-v1.0.0-{key}-bge-base-en-v1.5.20240223.43c9ec.tar.gz",')
#     print('    new String[] {' + f' "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-{key}-bge-base-en-v1.5.20240223.43c9ec.tar.gz"' + ' },')
#     print(f'    "{checksums_bge[key]}"),\n')
#
# for key in beir_keys:
#     print(f'{beir_to_enum_prefix[key]}_FLAT("beir-v1.0.0-{key}.flat",')
#     print(f'    "Lucene inverted \'flat\' index of BEIR collection \'{key}\'.",')
#     print(f'    "lucene-index.beir-v1.0.0-{key}.flat.20221116.505594.tar.gz",')
#     print('    new String[] {' + f' "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-{key}.flat.20221116.505594.tar.gz"' + ' },')
#     print(f'    "{checksums_flat[key]}"),\n')

# for key in beir_keys:
#     print(f'{beir_to_enum_prefix[key]}_MULTIFIELD("beir-v1.0.0-{key}.multifield",')
#     print(f'    "Lucene inverted \'multifield\' index of BEIR collection \'{key}\'.",')
#     print(f'    "lucene-index.beir-v1.0.0-{key}.multifield.20221116.505594.tar.gz",')
#     print('    new String[] {' + f' "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-{key}.multifield.20221116.505594.tar.gz"' + ' },')
#     print(f'    "{checksums_multifield[key]}"),\n')

for key in beir_keys:
    print(f'{beir_to_enum_prefix[key]}_SPLADE_PP_ED("beir-v1.0.0-{key}.splade-pp-ed",')
    print(f'    "Lucene impact index of BEIR collection \'{key}\' encoded by SPLADE++ EnsembleDistil",')
    print(f'    "lucene-index.beir-v1.0.0-{key}.splade-pp-ed.20231124.a66f86f.tar.gz",')
    print('    new String[] {' + f' "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-{key}.splade-pp-ed.20231124.a66f86f.tar.gz"' + ' },')
    print(f'    "{checksums_splade[key]}"),\n')
