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

  // BEIR: BGE
  BEIR_V1_0_0_TREC_COVID_BGE_BASE_EN_15("beir-v1.0.0-trec-covid-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-covid-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-trec-covid-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "c391e9c6841e3521355eb2ac837fe248"),

  BEIR_V1_0_0_BIOASQ_BGE_BASE_EN_15("beir-v1.0.0-bioasq-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-bioasq-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-bioasq-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "79844df82809e4daa5eca3ceebf2b935"),

  BEIR_V1_0_0_NFCORPUS_BGE_BASE_EN_15("beir-v1.0.0-nfcorpus-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nfcorpus-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-nfcorpus-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "a5be3e39e5922ad742deff6ba9d53266"),

  BEIR_V1_0_0_NQ_BGE_BASE_EN_15("beir-v1.0.0-nq-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nq-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-nq-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "caa451b4a46126659cbf4ffdaeae335b"),

  BEIR_V1_0_0_HOTPOTQA_BGE_BASE_EN_15("beir-v1.0.0-hotpotqa-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-hotpotqa-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-hotpotqa-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "00ba2207aeacb86bc975d150633ecb09"),

  BEIR_V1_0_0_FIQA_BGE_BASE_EN_15("beir-v1.0.0-fiqa-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fiqa-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-fiqa-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "aabb1a185ff1eba74db655e0a7e3bb08"),

  BEIR_V1_0_0_SIGNAL1M_BGE_BASE_EN_15("beir-v1.0.0-signal1m-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-signal1m-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-signal1m-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "e427da1a196f624644a428b7f4fe5065"),

  BEIR_V1_0_0_TREC_NEWS_BGE_BASE_EN_15("beir-v1.0.0-trec-news-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-news-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-trec-news-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "13afaccb1981824490cf3ab4694eb45c"),

  BEIR_V1_0_0_ROBUST04_BGE_BASE_EN_15("beir-v1.0.0-robust04-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-robust04-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-robust04-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "f27e12a03545933f44fff674b24cc311"),

  BEIR_V1_0_0_ARGUANA_BGE_BASE_EN_15("beir-v1.0.0-arguana-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-arguana-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-arguana-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "a5c3dde9409a7f8bbab651a0b1dca169"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_BGE_BASE_EN_15("beir-v1.0.0-webis-touche2020-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-webis-touche2020-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "76ad8c91f37654a4f34e20f9aa9bb67b"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-android-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "26e5f2b3e76c029a4dc9d6c0782bf6fa"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-english-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "22358a8571b8c9b9483c056c5588d474"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-gaming-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fc6dfc94eca7bd635e93ae41ad3da6db"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-gis-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fb6977d2b2568e3b2ee33a033a63b25d"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "6e20741b2322bcda8e808f1ea0c66d26"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-physics-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d8f618d161681ad9918249c1fec4de80"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-programmers-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "8daf4afd332be000c7dd508d46f019af"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-stats-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "95d513a64cbeecf956e0ff093354a1bc"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-tex-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d1a770ffb3dd02be9fd19a73b7ac6878"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-unix-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "70d366f4f3a735dfdd0f47053679e5c9"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "e236cfbd1662112e8ac02ad590544d10"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "e6e90dcb387b769a0c27b7e282326d94"),

  BEIR_V1_0_0_QUORA_BGE_BASE_EN_15("beir-v1.0.0-quora-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'quora' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-quora-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-quora-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "91bec208040d08caeffafb400ea220b2"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_BGE_BASE_EN_15("beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-dbpedia-entity-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d9fe11b3033f378ad43773bc11e9b9af"),

  BEIR_V1_0_0_SCIDOCS_BGE_BASE_EN_15("beir-v1.0.0-scidocs-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scidocs-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-scidocs-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "b7bfe2ae6b0df37b14655c16faeb409b"),

  BEIR_V1_0_0_FEVER_BGE_BASE_EN_15("beir-v1.0.0-fever-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fever-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-fever-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "480ce0b18ab73ccdecc782eaa820d0e9"),

  BEIR_V1_0_0_CLIMATE_FEVER_BGE_BASE_EN_15("beir-v1.0.0-climate-fever-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-climate-fever-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-climate-fever-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fa3814f8f20ef2642934bdaad6b12d5a"),

  BEIR_V1_0_0_SCIFACT_BGE_BASE_EN_15("beir-v1.0.0-scifact-bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scifact-bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-hnsw.beir-v1.0.0-scifact-bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "379b2f45873b0df722c63189c485ac29");

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
