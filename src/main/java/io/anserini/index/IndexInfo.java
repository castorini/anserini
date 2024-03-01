/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.index;

public enum IndexInfo {
  CACM("cacm",
      "Lucene index of the CACM corpus.",
      "lucene-index.cacm.tar.gz",
      new String[] {
          "https://github.com/castorini/anserini-data/raw/master/CACM/lucene-index.cacm.20221005.252b5e.tar.gz" },
      "cfe14d543c6a27f4d742fb2d0099b8e0"),

  MSMARCO_V1_PASSAGE("msmarco-v1-passage",
      "Lucene index of the MS MARCO V1 passage corpus.",
      "lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage.20221004.252b5e.tar.gz" },
      "c697b18c9a0686ca760583e615dbe450"),

  MSMARCO_V1_PASSAGE_SPLADE_PP_ED("msmarco-v1-passage-splade-pp-ed",
      "Lucene impact index of the MS MARCO V1 passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil.",
      "lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v1-passage-splade-pp-ed.20230524.a59610.tar.gz" },
      "4b3c969033cbd017306df42ce134c395"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL("msmarco-v1-passage-cos-dpr-distil",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cos-dpr-distil.20240108.825148.tar.gz" },
      "4aa1b08067b9aa313d8aba8ca9d7d8a2"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL_QUANTIZED("msmarco-v1-passage-cos-dpr-distil-quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw.msmarco-v1-passage-cos-dpr-distil-int8.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cos-dpr-distil-int8.20240108.825148.tar.gz" },
      "cc52b5cabe9886d42c58f9d87a5dfab1"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN_15("msmarco-v1-passage-bge-base-en-v1.5",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5.20240117.53514b.tar.gz" },
      "29d41b7a3b6ffb23f09a54aea453cc4e"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN_15_QUANTIZED("msmarco-v1-passage-bge-base-en-v1.5-quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5-int8.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-bge-base-en-v1.5-int8.20240117.53514b.tar.gz" },
      "51261598a7a108e88fa854971637b39c"),

  MSMARCO_V1_PASSAGE_COHERE_EMBED_ENGLISH_30("msmarco-v1-passage-cohere-embed-english-v3.0",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0.",
      "lucene-hnsw.msmarco-v1-passage-cohere-embed-english-v3.0.20240228.eacd13.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cohere-embed-english-v3.0.20240228.eacd13.tar.gz" },
      "eb551aaa3a0d1f14abdc00083db4cbbc"),

  MSMARCO_V1_PASSAGE_COHERE_EMBED_ENGLISH_30_QUANTIZED("msmarco-v1-passage-cohere-embed-english-v3.0-quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0.",
      "lucene-hnsw.msmarco-v1-passage-cohere-embed-english-v3.0-int8.20240228.eacd13.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.msmarco-v1-passage-cohere-embed-english-v3.0-int8.20240228.eacd13.tar.gz" },
      "cc7f0b2bcdacf1e0dd5d247c52906f12"),

  // BEIR: flat
  BEIR_V1_0_0_TREC_COVID_FLAT("beir-v1.0.0-trec-covid.flat",
      "Lucene inverted 'flat' index of BEIR collection 'trec-covid'.",
      "lucene-index.beir-v1.0.0-trec-covid.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-covid.flat.20221116.505594.tar.gz" },
      "57b812594b11d064a23123137ae7dade"),

  BEIR_V1_0_0_BIOASQ_FLAT("beir-v1.0.0-bioasq.flat",
      "Lucene inverted 'flat' index of BEIR collection 'bioasq'.",
      "lucene-index.beir-v1.0.0-bioasq.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-bioasq.flat.20221116.505594.tar.gz" },
      "cf8d4804b06bb8678d30b1375b46a0b3"),

  BEIR_V1_0_0_NFCORPUS_FLAT("beir-v1.0.0-nfcorpus.flat",
      "Lucene inverted 'flat' index of BEIR collection 'nfcorpus'.",
      "lucene-index.beir-v1.0.0-nfcorpus.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nfcorpus.flat.20221116.505594.tar.gz" },
      "34c0b11ad13a4715a78d025902061d37"),

  BEIR_V1_0_0_NQ_FLAT("beir-v1.0.0-nq.flat",
      "Lucene inverted 'flat' index of BEIR collection 'nq'.",
      "lucene-index.beir-v1.0.0-nq.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nq.flat.20221116.505594.tar.gz" },
      "a2c5db4dd3780fff3c7c6bfea1dd08e8"),

  BEIR_V1_0_0_HOTPOTQA_FLAT("beir-v1.0.0-hotpotqa.flat",
      "Lucene inverted 'flat' index of BEIR collection 'hotpotqa'.",
      "lucene-index.beir-v1.0.0-hotpotqa.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-hotpotqa.flat.20221116.505594.tar.gz" },
      "3be2875f93537369641287dcdf25add9"),

  BEIR_V1_0_0_FIQA_FLAT("beir-v1.0.0-fiqa.flat",
      "Lucene inverted 'flat' index of BEIR collection 'fiqa'.",
      "lucene-index.beir-v1.0.0-fiqa.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fiqa.flat.20221116.505594.tar.gz" },
      "409b779e8a39813d2fbdfd1ea2f009e9"),

  BEIR_V1_0_0_SIGNAL1M_FLAT("beir-v1.0.0-signal1m.flat",
      "Lucene inverted 'flat' index of BEIR collection 'signal1m'.",
      "lucene-index.beir-v1.0.0-signal1m.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-signal1m.flat.20221116.505594.tar.gz" },
      "d0828b92a3df814bfa4b73bddeb25da7"),

  BEIR_V1_0_0_TREC_NEWS_FLAT("beir-v1.0.0-trec-news.flat",
      "Lucene inverted 'flat' index of BEIR collection 'trec-news'.",
      "lucene-index.beir-v1.0.0-trec-news.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-news.flat.20221116.505594.tar.gz" },
      "98df3de34b4b76a4390520c606817ec4"),

  BEIR_V1_0_0_ROBUST04_FLAT("beir-v1.0.0-robust04.flat",
      "Lucene inverted 'flat' index of BEIR collection 'robust04'.",
      "lucene-index.beir-v1.0.0-robust04.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-robust04.flat.20221116.505594.tar.gz" },
      "89dfcb7297c12a772d1bfd7917df908d"),

  BEIR_V1_0_0_ARGUANA_FLAT("beir-v1.0.0-arguana.flat",
      "Lucene inverted 'flat' index of BEIR collection 'arguana'.",
      "lucene-index.beir-v1.0.0-arguana.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-arguana.flat.20221116.505594.tar.gz" },
      "d6c005689a9e7e91f3b1a7fbc74063e1"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_FLAT("beir-v1.0.0-webis-touche2020.flat",
      "Lucene inverted 'flat' index of BEIR collection 'webis-touche2020'.",
      "lucene-index.beir-v1.0.0-webis-touche2020.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-webis-touche2020.flat.20221116.505594.tar.gz" },
      "20c6e9f29461eea1a520cd1abead709a"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_FLAT("beir-v1.0.0-cqadupstack-android.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-android'.",
      "lucene-index.beir-v1.0.0-cqadupstack-android.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-android.flat.20221116.505594.tar.gz" },
      "9f9f35e34f76336bc6e516599cbaf75b"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_FLAT("beir-v1.0.0-cqadupstack-english.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-english'.",
      "lucene-index.beir-v1.0.0-cqadupstack-english.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-english.flat.20221116.505594.tar.gz" },
      "7d887497d32eedd92c314c93feaca28e"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_FLAT("beir-v1.0.0-cqadupstack-gaming.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gaming'.",
      "lucene-index.beir-v1.0.0-cqadupstack-gaming.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gaming.flat.20221116.505594.tar.gz" },
      "140e16ee86a69c8fd4d16a83a6d51591"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_FLAT("beir-v1.0.0-cqadupstack-gis.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gis'.",
      "lucene-index.beir-v1.0.0-cqadupstack-gis.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gis.flat.20221116.505594.tar.gz" },
      "4bd93695f28af0a11172f387ef41fee6"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_FLAT("beir-v1.0.0-cqadupstack-mathematica.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-mathematica'.",
      "lucene-index.beir-v1.0.0-cqadupstack-mathematica.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-mathematica.flat.20221116.505594.tar.gz" },
      "5b5b7ab3d0437428e29a5a1431de1ca5"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_FLAT("beir-v1.0.0-cqadupstack-physics.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-physics'.",
      "lucene-index.beir-v1.0.0-cqadupstack-physics.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-physics.flat.20221116.505594.tar.gz" },
      "6864144bca1bb169a452321e14ef12e0"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_FLAT("beir-v1.0.0-cqadupstack-programmers.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-programmers'.",
      "lucene-index.beir-v1.0.0-cqadupstack-programmers.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-programmers.flat.20221116.505594.tar.gz" },
      "7b7d2bbf7cc5d53924d09c3b781dba8a"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_FLAT("beir-v1.0.0-cqadupstack-stats.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-stats'.",
      "lucene-index.beir-v1.0.0-cqadupstack-stats.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-stats.flat.20221116.505594.tar.gz" },
      "0b09b7bee2b60df0ff73710a93a79218"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_FLAT("beir-v1.0.0-cqadupstack-tex.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-tex'.",
      "lucene-index.beir-v1.0.0-cqadupstack-tex.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-tex.flat.20221116.505594.tar.gz" },
      "48a2541bd7d1adec06f053486655e815"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_FLAT("beir-v1.0.0-cqadupstack-unix.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-unix'.",
      "lucene-index.beir-v1.0.0-cqadupstack-unix.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-unix.flat.20221116.505594.tar.gz" },
      "a6cc0a867f6210ad44755c0a36fd682a"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_FLAT("beir-v1.0.0-cqadupstack-webmasters.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-webmasters'.",
      "lucene-index.beir-v1.0.0-cqadupstack-webmasters.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-webmasters.flat.20221116.505594.tar.gz" },
      "a04f65d575b4233a151c4960b82815b9"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_FLAT("beir-v1.0.0-cqadupstack-wordpress.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-wordpress'.",
      "lucene-index.beir-v1.0.0-cqadupstack-wordpress.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-wordpress.flat.20221116.505594.tar.gz" },
      "4ab079b9f7d0463955ce073b5d53e64d"),

  BEIR_V1_0_0_QUORA_FLAT("beir-v1.0.0-quora.flat",
      "Lucene inverted 'flat' index of BEIR collection 'quora'.",
      "lucene-index.beir-v1.0.0-quora.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-quora.flat.20221116.505594.tar.gz" },
      "53fa2bd0667d23a50f95adaf169b87a1"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_FLAT("beir-v1.0.0-dbpedia-entity.flat",
      "Lucene inverted 'flat' index of BEIR collection 'dbpedia-entity'.",
      "lucene-index.beir-v1.0.0-dbpedia-entity.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-dbpedia-entity.flat.20221116.505594.tar.gz" },
      "6bc15a920e262d12ec3842401755e934"),

  BEIR_V1_0_0_SCIDOCS_FLAT("beir-v1.0.0-scidocs.flat",
      "Lucene inverted 'flat' index of BEIR collection 'scidocs'.",
      "lucene-index.beir-v1.0.0-scidocs.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scidocs.flat.20221116.505594.tar.gz" },
      "f1fba96a71a62bc567ecbd167de3794b"),

  BEIR_V1_0_0_FEVER_FLAT("beir-v1.0.0-fever.flat",
      "Lucene inverted 'flat' index of BEIR collection 'fever'.",
      "lucene-index.beir-v1.0.0-fever.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fever.flat.20221116.505594.tar.gz" },
      "1b06f43ea36e2ed450d1b1d90099ae67"),

  BEIR_V1_0_0_CLIMATE_FEVER_FLAT("beir-v1.0.0-climate-fever.flat",
      "Lucene inverted 'flat' index of BEIR collection 'climate-fever'.",
      "lucene-index.beir-v1.0.0-climate-fever.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-climate-fever.flat.20221116.505594.tar.gz" },
      "68811e2614b3bac9e1b879c883fc722e"),

  BEIR_V1_0_0_SCIFACT_FLAT("beir-v1.0.0-scifact.flat",
      "Lucene inverted 'flat' index of BEIR collection 'scifact'.",
      "lucene-index.beir-v1.0.0-scifact.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scifact.flat.20221116.505594.tar.gz" },
      "6f6e55f1cf80c362f86bee65529b71de"),

  // BEIR: multifield
  BEIR_V1_0_0_TREC_COVID_MULTIFIELD("beir-v1.0.0-trec-covid.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'trec-covid'.",
      "lucene-index.beir-v1.0.0-trec-covid.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-covid.multifield.20221116.505594.tar.gz" },
      "7501a330a0c9246e6350413c3f6ced7c"),

  BEIR_V1_0_0_BIOASQ_MULTIFIELD("beir-v1.0.0-bioasq.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'bioasq'.",
      "lucene-index.beir-v1.0.0-bioasq.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-bioasq.multifield.20221116.505594.tar.gz" },
      "cc01ab450cac0b8865bd1e70e2a58596"),

  BEIR_V1_0_0_NFCORPUS_MULTIFIELD("beir-v1.0.0-nfcorpus.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'nfcorpus'.",
      "lucene-index.beir-v1.0.0-nfcorpus.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nfcorpus.multifield.20221116.505594.tar.gz" },
      "904e53b80fe04b3844b97847bc77a772"),

  BEIR_V1_0_0_NQ_MULTIFIELD("beir-v1.0.0-nq.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'nq'.",
      "lucene-index.beir-v1.0.0-nq.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nq.multifield.20221116.505594.tar.gz" },
      "693ca315de9fbbbf7f664be313a03847"),

  BEIR_V1_0_0_HOTPOTQA_MULTIFIELD("beir-v1.0.0-hotpotqa.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'hotpotqa'.",
      "lucene-index.beir-v1.0.0-hotpotqa.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-hotpotqa.multifield.20221116.505594.tar.gz" },
      "ef8c2f40097e652eec99e6bf25e151cd"),

  BEIR_V1_0_0_FIQA_MULTIFIELD("beir-v1.0.0-fiqa.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'fiqa'.",
      "lucene-index.beir-v1.0.0-fiqa.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fiqa.multifield.20221116.505594.tar.gz" },
      "073f3f19a94689e5fac511af49316fe1"),

  BEIR_V1_0_0_SIGNAL1M_MULTIFIELD("beir-v1.0.0-signal1m.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'signal1m'.",
      "lucene-index.beir-v1.0.0-signal1m.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-signal1m.multifield.20221116.505594.tar.gz" },
      "4482ae02f18e8336c0a95ea33b5b6ede"),

  BEIR_V1_0_0_TREC_NEWS_MULTIFIELD("beir-v1.0.0-trec-news.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'trec-news'.",
      "lucene-index.beir-v1.0.0-trec-news.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-news.multifield.20221116.505594.tar.gz" },
      "3151122da3cf081a0c8894af7b75be43"),

  BEIR_V1_0_0_ROBUST04_MULTIFIELD("beir-v1.0.0-robust04.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'robust04'.",
      "lucene-index.beir-v1.0.0-robust04.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-robust04.multifield.20221116.505594.tar.gz" },
      "fdf741a75efe089d0451de5720b52c3a"),

  BEIR_V1_0_0_ARGUANA_MULTIFIELD("beir-v1.0.0-arguana.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'arguana'.",
      "lucene-index.beir-v1.0.0-arguana.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-arguana.multifield.20221116.505594.tar.gz" },
      "a8201952860d31c56ea8a54c31e88b51"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_MULTIFIELD("beir-v1.0.0-webis-touche2020.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'webis-touche2020'.",
      "lucene-index.beir-v1.0.0-webis-touche2020.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-webis-touche2020.multifield.20221116.505594.tar.gz" },
      "e160ea813990cff4dbdb9f50d509f8ea"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_MULTIFIELD("beir-v1.0.0-cqadupstack-android.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-android'.",
      "lucene-index.beir-v1.0.0-cqadupstack-android.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-android.multifield.20221116.505594.tar.gz" },
      "de85f92a018d83a7ea496d9ef955b8c5"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_MULTIFIELD("beir-v1.0.0-cqadupstack-english.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-english'.",
      "lucene-index.beir-v1.0.0-cqadupstack-english.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-english.multifield.20221116.505594.tar.gz" },
      "71c5d3db04586283772f6069668f5bfa"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_MULTIFIELD("beir-v1.0.0-cqadupstack-gaming.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gaming'.",
      "lucene-index.beir-v1.0.0-cqadupstack-gaming.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gaming.multifield.20221116.505594.tar.gz" },
      "ff7c628b568f916c3bc3f7bf2af831eb"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_MULTIFIELD("beir-v1.0.0-cqadupstack-gis.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gis'.",
      "lucene-index.beir-v1.0.0-cqadupstack-gis.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gis.multifield.20221116.505594.tar.gz" },
      "4083830da4922d1294b3fb38873ba5a2"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_MULTIFIELD("beir-v1.0.0-cqadupstack-mathematica.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-mathematica'.",
      "lucene-index.beir-v1.0.0-cqadupstack-mathematica.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-mathematica.multifield.20221116.505594.tar.gz" },
      "baa9414c385db88eaafffa95d5ec7d48"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_MULTIFIELD("beir-v1.0.0-cqadupstack-physics.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-physics'.",
      "lucene-index.beir-v1.0.0-cqadupstack-physics.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-physics.multifield.20221116.505594.tar.gz" },
      "342b105462067b87e78730921dd7288d"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_MULTIFIELD("beir-v1.0.0-cqadupstack-programmers.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-programmers'.",
      "lucene-index.beir-v1.0.0-cqadupstack-programmers.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-programmers.multifield.20221116.505594.tar.gz" },
      "2e95b82caf156d0f0b109c62e0011eab"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_MULTIFIELD("beir-v1.0.0-cqadupstack-stats.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-stats'.",
      "lucene-index.beir-v1.0.0-cqadupstack-stats.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-stats.multifield.20221116.505594.tar.gz" },
      "87c53df624baed7921672286beb94f9c"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_MULTIFIELD("beir-v1.0.0-cqadupstack-tex.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-tex'.",
      "lucene-index.beir-v1.0.0-cqadupstack-tex.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-tex.multifield.20221116.505594.tar.gz" },
      "86407171e4ff305ecb173afdd49eef7c"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_MULTIFIELD("beir-v1.0.0-cqadupstack-unix.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-unix'.",
      "lucene-index.beir-v1.0.0-cqadupstack-unix.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-unix.multifield.20221116.505594.tar.gz" },
      "acb0cc50cccb9e8dfca0ed599df0cfaa"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_MULTIFIELD("beir-v1.0.0-cqadupstack-webmasters.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-webmasters'.",
      "lucene-index.beir-v1.0.0-cqadupstack-webmasters.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-webmasters.multifield.20221116.505594.tar.gz" },
      "7701f016b6fc643c30630742f7712bbd"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_MULTIFIELD("beir-v1.0.0-cqadupstack-wordpress.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-wordpress'.",
      "lucene-index.beir-v1.0.0-cqadupstack-wordpress.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-wordpress.multifield.20221116.505594.tar.gz" },
      "d791cf8449a18ebe698d404f526375ee"),

  BEIR_V1_0_0_QUORA_MULTIFIELD("beir-v1.0.0-quora.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'quora'.",
      "lucene-index.beir-v1.0.0-quora.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-quora.multifield.20221116.505594.tar.gz" },
      "2d92b46f715df08ce146167ed1b12079"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_MULTIFIELD("beir-v1.0.0-dbpedia-entity.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'dbpedia-entity'.",
      "lucene-index.beir-v1.0.0-dbpedia-entity.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-dbpedia-entity.multifield.20221116.505594.tar.gz" },
      "b3f6b64bfd7903ff25ca2fa01a288392"),

  BEIR_V1_0_0_SCIDOCS_MULTIFIELD("beir-v1.0.0-scidocs.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'scidocs'.",
      "lucene-index.beir-v1.0.0-scidocs.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scidocs.multifield.20221116.505594.tar.gz" },
      "04c1e9aad3751dc552027d8bc3491323"),

  BEIR_V1_0_0_FEVER_MULTIFIELD("beir-v1.0.0-fever.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'fever'.",
      "lucene-index.beir-v1.0.0-fever.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fever.multifield.20221116.505594.tar.gz" },
      "28ea09308760235ea2ec72d6f9b2f432"),

  BEIR_V1_0_0_CLIMATE_FEVER_MULTIFIELD("beir-v1.0.0-climate-fever.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'climate-fever'.",
      "lucene-index.beir-v1.0.0-climate-fever.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-climate-fever.multifield.20221116.505594.tar.gz" },
      "827f2759cdfc45c47bbb67835cfcb1f2"),

  BEIR_V1_0_0_SCIFACT_MULTIFIELD("beir-v1.0.0-scifact.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'scifact'.",
      "lucene-index.beir-v1.0.0-scifact.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scifact.multifield.20221116.505594.tar.gz" },
      "efbafbc3e4909a026fe80bf8b1444b08"),

  // BEIR: SPLADE++ ED
  BEIR_V1_0_0_TREC_COVID_SPLADE_PP_ED("beir-v1.0.0-trec-covid.splade-pp-ed",
      "Lucene impact index of BEIR collection 'trec-covid' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-trec-covid.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-covid.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "09c7bc8500e8c70bfb2134556261e6e2"),

  BEIR_V1_0_0_BIOASQ_SPLADE_PP_ED("beir-v1.0.0-bioasq.splade-pp-ed",
      "Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-bioasq.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-bioasq.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "d153c06c23bcc6c1a1c9617d3defcef9"),

  BEIR_V1_0_0_NFCORPUS_SPLADE_PP_ED("beir-v1.0.0-nfcorpus.splade-pp-ed",
      "Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-nfcorpus.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nfcorpus.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f0d5659c4483ecb6fe8e32409ecd5002"),

  BEIR_V1_0_0_NQ_SPLADE_PP_ED("beir-v1.0.0-nq.splade-pp-ed",
      "Lucene impact index of BEIR collection 'nq' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-nq.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-nq.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "34ebea38ab05066f7a8dc45f72f88d57"),

  BEIR_V1_0_0_HOTPOTQA_SPLADE_PP_ED("beir-v1.0.0-hotpotqa.splade-pp-ed",
      "Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-hotpotqa.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-hotpotqa.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "415c855c411681dc43012f905d9826a3"),

  BEIR_V1_0_0_FIQA_SPLADE_PP_ED("beir-v1.0.0-fiqa.splade-pp-ed",
      "Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-fiqa.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fiqa.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "4dd93efc25f77afceb7d409211863b7b"),

  BEIR_V1_0_0_SIGNAL1M_SPLADE_PP_ED("beir-v1.0.0-signal1m.splade-pp-ed",
      "Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-signal1m.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-signal1m.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f12141cdbe242511f3dca72d03b87d0a"),

  BEIR_V1_0_0_TREC_NEWS_SPLADE_PP_ED("beir-v1.0.0-trec-news.splade-pp-ed",
      "Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-trec-news.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-trec-news.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "cc86753ff81ee0bcabde75b537d1bea6"),

  BEIR_V1_0_0_ROBUST04_SPLADE_PP_ED("beir-v1.0.0-robust04.splade-pp-ed",
      "Lucene impact index of BEIR collection 'robust04' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-robust04.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-robust04.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "a454bb33b6edb3b057f37c32d8712f4a"),

  BEIR_V1_0_0_ARGUANA_SPLADE_PP_ED("beir-v1.0.0-arguana.splade-pp-ed",
      "Lucene impact index of BEIR collection 'arguana' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-arguana.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-arguana.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "59be25716db84b574f503a1680824c6d"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_SPLADE_PP_ED("beir-v1.0.0-webis-touche2020.splade-pp-ed",
      "Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-webis-touche2020.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-webis-touche2020.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "c7ae4e8458e1ecec2e879beb6547d08f"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-android.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-android.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-android.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "e5179184bf85d2c18ae98be033674208"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-english.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-english.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-english.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "e99b9439465c8038794873fdef9478fa"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-gaming.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "cd1248b1ecaa3284f1b7fcad4e6afae6"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-gis.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "42a1c93fd7a012a34e7cd872c4b87528"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "3cb36e0043de37f47e1cb0fb5ea5d07c"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-physics.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "adf4d56e558cd2503a2b72214cc50950"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-programmers.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "042c2ef13a09b6da5a924b1db72a967b"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-stats.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f5fa111b03094cd6351f0a6a6ed9cb03"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-tex.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "aa2fa8df7e9dd834967519738f7b6666"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-unix.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "4fef94bad65d1374bce9532fd5bd1689"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "b883e6e3bb444689378d15af308280da"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "eda8eb8917514c64b43f5eaafde1a50b"),

  BEIR_V1_0_0_QUORA_SPLADE_PP_ED("beir-v1.0.0-quora.splade-pp-ed",
      "Lucene impact index of BEIR collection 'quora' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-quora.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-quora.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "7c0fea9ccae8db35fabc8a5f329ccb3c"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_SPLADE_PP_ED("beir-v1.0.0-dbpedia-entity.splade-pp-ed",
      "Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-dbpedia-entity.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-dbpedia-entity.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "2598e1588671d249c024ce7d44d2fec2"),

  BEIR_V1_0_0_SCIDOCS_SPLADE_PP_ED("beir-v1.0.0-scidocs.splade-pp-ed",
      "Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-scidocs.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scidocs.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "b3b643dc2c09d3d68660ab796ac96ac2"),

  BEIR_V1_0_0_FEVER_SPLADE_PP_ED("beir-v1.0.0-fever.splade-pp-ed",
      "Lucene impact index of BEIR collection 'fever' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-fever.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-fever.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "11f2e5c2259a55cc82052bed11a29039"),

  BEIR_V1_0_0_CLIMATE_FEVER_SPLADE_PP_ED("beir-v1.0.0-climate-fever.splade-pp-ed",
      "Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-climate-fever.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-climate-fever.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "32e7d4e30fa28c66db83722bf1ba7fd2"),

  BEIR_V1_0_0_SCIFACT_SPLADE_PP_ED("beir-v1.0.0-scifact.splade-pp-ed",
      "Lucene impact index of BEIR collection 'scifact' encoded by SPLADE++ EnsembleDistil",
      "lucene-index.beir-v1.0.0-scifact.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.beir-v1.0.0-scifact.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f8b03611fbb322a8f860a15e8ba52b14"),

  // BEIR: BGE
  BEIR_V1_0_0_TREC_COVID_BGE_BASE_EN_15("beir-v1.0.0-trec-covid.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-covid.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-trec-covid.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "2c8cba8525f8ec6920dbb4f0b4a2e0a6"),

  BEIR_V1_0_0_BIOASQ_BGE_BASE_EN_15("beir-v1.0.0-bioasq.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-bioasq.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-bioasq.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "2f4cde27ef5ec3be1193e06854fdaae6"),

  BEIR_V1_0_0_NFCORPUS_BGE_BASE_EN_15("beir-v1.0.0-nfcorpus.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d0aa34bf35b59466e7064c424dd82e2c"),

  BEIR_V1_0_0_NQ_BGE_BASE_EN_15("beir-v1.0.0-nq.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nq.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-nq.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "b0bbd85821c734125ffbc0f7ea8f75ae"),

  BEIR_V1_0_0_HOTPOTQA_BGE_BASE_EN_15("beir-v1.0.0-hotpotqa.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "83129157f2138a2240b69f8f5404e579"),

  BEIR_V1_0_0_FIQA_BGE_BASE_EN_15("beir-v1.0.0-fiqa.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fiqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-fiqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "f2e3191b9d047b88b4692ec3ac87acd0"),

  BEIR_V1_0_0_SIGNAL1M_BGE_BASE_EN_15("beir-v1.0.0-signal1m.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "86a5dc12806c5e2f5f1e7cf646ef9004"),

  BEIR_V1_0_0_TREC_NEWS_BGE_BASE_EN_15("beir-v1.0.0-trec-news.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-news.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-trec-news.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fcb8fae8c46c76931bde0ad51ecb86f8"),

  BEIR_V1_0_0_ROBUST04_BGE_BASE_EN_15("beir-v1.0.0-robust04.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-robust04.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-robust04.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "1b975602bf6b87e0a5815a254eb6e945"),

  BEIR_V1_0_0_ARGUANA_BGE_BASE_EN_15("beir-v1.0.0-arguana.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-arguana.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-arguana.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "468129157636526a3e96bc9427d62808"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_BGE_BASE_EN_15("beir-v1.0.0-webis-touche2020.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "4639db80366f755bb552ce4c736c4aea"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "f7e1f2e737756a84b0273794dcb1038f"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fcdb3fc633b2ca027111536ba422aaed"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d59b216b3df6eb1b724e2f20ceb14407"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "1dd42a28e388b30f42ede02565d445ca"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "cda37cb1893409c67908cf3aab1467fe"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "82f71e086930c7d8c5fe423173b9bc2e"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "a7a8e17dcef7b40fde2492436aab1458"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "7a304fa64332256976bed5049392605b"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "bc5b41b294528611982615c0fcb7ebc7"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "e42e7b6f46239211f9e9a3ed521d30eb"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "21987ab658ba062397095226eb62aaf1"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "4e80be8087e8f282c42c2b57e377bb65"),

  BEIR_V1_0_0_QUORA_BGE_BASE_EN_15("beir-v1.0.0-quora.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'quora' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-quora.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-quora.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "064d785db557b011649d5f8b07237eb4"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_BGE_BASE_EN_15("beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "323d47f84a54894ba5e6ca215999a533"),

  BEIR_V1_0_0_SCIDOCS_BGE_BASE_EN_15("beir-v1.0.0-scidocs.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scidocs.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-scidocs.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "50668564faa9723160b1dba37afbf6d9"),

  BEIR_V1_0_0_FEVER_BGE_BASE_EN_15("beir-v1.0.0-fever.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "33f67e73786a41b454bf88ac2a7c21c7"),

  BEIR_V1_0_0_CLIMATE_FEVER_BGE_BASE_EN_15("beir-v1.0.0-climate-fever.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-climate-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-climate-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "412337f9f8182e8ec6417bc3cd48288f"),

  BEIR_V1_0_0_SCIFACT_BGE_BASE_EN_15("beir-v1.0.0-scifact.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scifact.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-scifact.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "6de5a41a301575933fa9932f9ecb404d");

  public final String indexName;
  public final String description;
  public final String filename;
  public final String[] urls;
  public final String md5;

  IndexInfo(String indexName, String description, String filename, String[] urls, String md5) {
    this.indexName = indexName;
    this.description = description;
    this.filename = filename;
    this.urls = urls;
    this.md5 = md5;
  }

  public static boolean contains(String indexName) {
    for (IndexInfo indexInfo : IndexInfo.values()) {
      if (indexInfo.indexName.equals(indexName)) {
        return true;
      }
    }
    return false;
  }

  public static IndexInfo get(String indexName) {
    for (IndexInfo indexInfo : IndexInfo.values()) {
      if (indexInfo.indexName.equals(indexName)) {
        return indexInfo;
      }
    }
    throw new IllegalArgumentException("Index name " + indexName + " not found!");
  }

}
