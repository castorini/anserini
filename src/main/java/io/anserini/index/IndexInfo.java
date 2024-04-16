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
      "lucene-index.cacm.20221005.252b5e.tar.gz",
      new String[] {
          "https://github.com/castorini/anserini-data/raw/master/CACM/lucene-index.cacm.20221005.252b5e.tar.gz" },
      "cfe14d543c6a27f4d742fb2d0099b8e0"),

  // MS MARCO V1
  MSMARCO_V1_PASSAGE("msmarco-v1-passage",
      "Lucene index of the MS MARCO V1 passage corpus.",
      "lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.msmarco-v1-passage.20221004.252b5e.tar.gz" },
      "678876e8c99a89933d553609a0fd8793"),

  MSMARCO_V1_PASSAGE_SPLADE_PP_ED("msmarco-v1-passage.splade-pp-ed",
      "Lucene impact index of the MS MARCO V1 passage corpus encoded by SPLADE++ CoCondenser-EnsembleDistil.",
      "lucene-inverted.msmarco-v1-passage.splade-pp-ed.20230524.a59610.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.msmarco-v1-passage.splade-pp-ed.20230524.a59610.tar.gz" },
      "2c008fc36131e27966a72292932358e6"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL("msmarco-v1-passage.cos-dpr-distil",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw.msmarco-v1-passage.cos-dpr-distil.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.msmarco-v1-passage.cos-dpr-distil.20240108.825148.tar.gz" },
      "d0d602c46fb8b93511f2ab1214bcb86a"),

  MSMARCO_V1_PASSAGE_COS_DPR_DISTIL_QUANTIZED("msmarco-v1-passage.cos-dpr-distil.quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by cos-DPR Distil.",
      "lucene-hnsw-int8.msmarco-v1-passage.cos-dpr-distil.20240108.825148.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw-int8.msmarco-v1-passage.cos-dpr-distil.20240108.825148.tar.gz" },
      "24ed94669c9caf52704e0ceccee3732b"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN_15("msmarco-v1-passage.bge-base-en-v1.5",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.tar.gz" },
      "00a577f689d90f95e6c5611438b0af3d"),

  MSMARCO_V1_PASSAGE_BGE_BASE_EN_15_QUANTIZED("msmarco-v1-passage.bge-base-en-v1.5.quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by BGE-base-en-v1.5.",
      "lucene-hnsw-int8.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw-int8.msmarco-v1-passage.bge-base-en-v1.5.20240117.53514b.tar.gz" },
      "7830712459cf124c96fd058bb0a405b7"),

  MSMARCO_V1_PASSAGE_COHERE_EMBED_ENGLISH_30("msmarco-v1-passage.cohere-embed-english-v3.0",
      "Lucene HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0.",
      "lucene-hnsw.msmarco-v1-passage.cohere-embed-english-v3.0.20240228.eacd13.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.msmarco-v1-passage.cohere-embed-english-v3.0.20240228.eacd13.tar.gz" },
      "c7294ca988ae1b812d427362ffca1ee2"),

  MSMARCO_V1_PASSAGE_COHERE_EMBED_ENGLISH_30_QUANTIZED("msmarco-v1-passage.cohere-embed-english-v3.0.quantized",
      "Lucene quantized HNSW index of the MS MARCO V1 passage corpus encoded by Cohere embed-english-v3.0.",
      "lucene-hnsw-int8.msmarco-v1-passage.cohere-embed-english-v3.0.20240228.eacd13.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw-int8.msmarco-v1-passage.cohere-embed-english-v3.0.20240228.eacd13.tar.gz" },
      "dbaca578cc8495f504cdd0a7187f4c36"),

  // MS MARCO V2
  MSMARCO_V2_PASSAGE("msmarco-v2-passage",
      "Lucene index of the MS MARCO V2 passage corpus.",
      "lucene-index.msmarco-v2-passage.20220808.4d6d2a.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v2-passage.20220808.4d6d2a.tar.gz" },
      "eacd8556dd416ccad517b5e7dc97bceb"),

  MSMARCO_V2_DOC("msmarco-v2-doc",
      "Lucene index of the MS MARCO V2 document corpus.",
      "lucene-index.msmarco-v2-doc.20220808.4d6d2a.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v2-doc.20220808.4d6d2a.tar.gz" },
      "0599bd6ed5ee28390b279eb398ef0267"),

  MSMARCO_V2_DOC_SEGMENTED("msmarco-v2-doc-segmented",
      "Lucene index of the MS MARCO V2 segmented document corpus.",
      "lucene-index.msmarco-v2-doc-segmented.20220808.4d6d2a.tar.gz",
      new String[] {
          "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene-index.msmarco-v2-doc-segmented.20220808.4d6d2a.tar.gz" },
      "8a5f444fa5a63cc5d4ddc3e6dd15faa0"),

  // BEIR: flat
  BEIR_V1_0_0_TREC_COVID_FLAT("beir-v1.0.0-trec-covid.flat",
      "Lucene inverted 'flat' index of BEIR collection 'trec-covid'.",
      "lucene-inverted.beir-v1.0.0-trec-covid.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-covid.flat.20221116.505594.tar.gz" },
      "1aaf107b0787aa349deac92cb67d4230"),

  BEIR_V1_0_0_BIOASQ_FLAT("beir-v1.0.0-bioasq.flat",
      "Lucene inverted 'flat' index of BEIR collection 'bioasq'.",
      "lucene-inverted.beir-v1.0.0-bioasq.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-bioasq.flat.20221116.505594.tar.gz" },
      "12728b3629817d352322f18b0cb6199b"),

  BEIR_V1_0_0_NFCORPUS_FLAT("beir-v1.0.0-nfcorpus.flat",
      "Lucene inverted 'flat' index of BEIR collection 'nfcorpus'.",
      "lucene-inverted.beir-v1.0.0-nfcorpus.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nfcorpus.flat.20221116.505594.tar.gz" },
      "eb7a6f1bb15071c2940bc50752d86626"),

  BEIR_V1_0_0_NQ_FLAT("beir-v1.0.0-nq.flat",
      "Lucene inverted 'flat' index of BEIR collection 'nq'.",
      "lucene-inverted.beir-v1.0.0-nq.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nq.flat.20221116.505594.tar.gz" },
      "0ba1ef0412d8a0fb56b4a04ecb13ef0b"),

  BEIR_V1_0_0_HOTPOTQA_FLAT("beir-v1.0.0-hotpotqa.flat",
      "Lucene inverted 'flat' index of BEIR collection 'hotpotqa'.",
      "lucene-inverted.beir-v1.0.0-hotpotqa.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-hotpotqa.flat.20221116.505594.tar.gz" },
      "3f41d640a8ebbcad4f598140750c24f8"),

  BEIR_V1_0_0_FIQA_FLAT("beir-v1.0.0-fiqa.flat",
      "Lucene inverted 'flat' index of BEIR collection 'fiqa'.",
      "lucene-inverted.beir-v1.0.0-fiqa.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fiqa.flat.20221116.505594.tar.gz" },
      "d98ee6ebfc234657ecbd04226e8a7849"),

  BEIR_V1_0_0_SIGNAL1M_FLAT("beir-v1.0.0-signal1m.flat",
      "Lucene inverted 'flat' index of BEIR collection 'signal1m'.",
      "lucene-inverted.beir-v1.0.0-signal1m.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-signal1m.flat.20221116.505594.tar.gz" },
      "93d901916b473351fbc04fdf12c5ba4f"),

  BEIR_V1_0_0_TREC_NEWS_FLAT("beir-v1.0.0-trec-news.flat",
      "Lucene inverted 'flat' index of BEIR collection 'trec-news'.",
      "lucene-inverted.beir-v1.0.0-trec-news.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-news.flat.20221116.505594.tar.gz" },
      "22e7752c3d0122c28013b33e5e2134ae"),

  BEIR_V1_0_0_ROBUST04_FLAT("beir-v1.0.0-robust04.flat",
      "Lucene inverted 'flat' index of BEIR collection 'robust04'.",
      "lucene-inverted.beir-v1.0.0-robust04.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-robust04.flat.20221116.505594.tar.gz" },
      "d508fc770002a99a5dc3da3d0fa001b7"),

  BEIR_V1_0_0_ARGUANA_FLAT("beir-v1.0.0-arguana.flat",
      "Lucene inverted 'flat' index of BEIR collection 'arguana'.",
      "lucene-inverted.beir-v1.0.0-arguana.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-arguana.flat.20221116.505594.tar.gz" },
      "db59ef0cb74e9cfeac0ac735827381df"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_FLAT("beir-v1.0.0-webis-touche2020.flat",
      "Lucene inverted 'flat' index of BEIR collection 'webis-touche2020'.",
      "lucene-inverted.beir-v1.0.0-webis-touche2020.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-webis-touche2020.flat.20221116.505594.tar.gz" },
      "f6419ddfd53c0bf1d76ea132b1c0c352"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_FLAT("beir-v1.0.0-cqadupstack-android.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-android'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-android.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-android.flat.20221116.505594.tar.gz" },
      "443e413b49c39de43a6cece96a7513c0"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_FLAT("beir-v1.0.0-cqadupstack-english.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-english'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-english.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-english.flat.20221116.505594.tar.gz" },
      "f7db543f5bb56fa98c3c14224c6b96f2"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_FLAT("beir-v1.0.0-cqadupstack-gaming.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gaming'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gaming.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gaming.flat.20221116.505594.tar.gz" },
      "775169fd863d3e91076e1905799456ea"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_FLAT("beir-v1.0.0-cqadupstack-gis.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-gis'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gis.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gis.flat.20221116.505594.tar.gz" },
      "4c5be1c7026a61ca7866b4f28cac91fe"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_FLAT("beir-v1.0.0-cqadupstack-mathematica.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-mathematica'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.flat.20221116.505594.tar.gz" },
      "43e2b33db7ecadc041165005aa5d4b6f"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_FLAT("beir-v1.0.0-cqadupstack-physics.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-physics'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-physics.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-physics.flat.20221116.505594.tar.gz" },
      "765b8013595962e01600f4f851e8f16d"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_FLAT("beir-v1.0.0-cqadupstack-programmers.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-programmers'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-programmers.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-programmers.flat.20221116.505594.tar.gz" },
      "aa4fc9f29a0436a6e0942656274ceaf5"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_FLAT("beir-v1.0.0-cqadupstack-stats.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-stats'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-stats.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-stats.flat.20221116.505594.tar.gz" },
      "d56538f56d982ce09961d4b680bd4dc5"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_FLAT("beir-v1.0.0-cqadupstack-tex.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-tex'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-tex.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-tex.flat.20221116.505594.tar.gz" },
      "36825b8428aa34fdaad7e420e120c101"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_FLAT("beir-v1.0.0-cqadupstack-unix.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-unix'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-unix.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-unix.flat.20221116.505594.tar.gz" },
      "961e386016c7eb7afa2bc26feb96902c"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_FLAT("beir-v1.0.0-cqadupstack-webmasters.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-webmasters'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.flat.20221116.505594.tar.gz" },
      "f31625436dc6efc24b9c2ae1b0f2364e"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_FLAT("beir-v1.0.0-cqadupstack-wordpress.flat",
      "Lucene inverted 'flat' index of BEIR collection 'cqadupstack-wordpress'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.flat.20221116.505594.tar.gz" },
      "5a0035fbb6ccabd20fe0eed742dce0d0"),

  BEIR_V1_0_0_QUORA_FLAT("beir-v1.0.0-quora.flat",
      "Lucene inverted 'flat' index of BEIR collection 'quora'.",
      "lucene-inverted.beir-v1.0.0-quora.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-quora.flat.20221116.505594.tar.gz" },
      "48c95c2da43e24cc603695d3e6bfd779"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_FLAT("beir-v1.0.0-dbpedia-entity.flat",
      "Lucene inverted 'flat' index of BEIR collection 'dbpedia-entity'.",
      "lucene-inverted.beir-v1.0.0-dbpedia-entity.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-dbpedia-entity.flat.20221116.505594.tar.gz" },
      "8ac66272fde08ff10491dc0ec52f17e2"),

  BEIR_V1_0_0_SCIDOCS_FLAT("beir-v1.0.0-scidocs.flat",
      "Lucene inverted 'flat' index of BEIR collection 'scidocs'.",
      "lucene-inverted.beir-v1.0.0-scidocs.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scidocs.flat.20221116.505594.tar.gz" },
      "9555ecc5da399a73956d9302a98420fc"),

  BEIR_V1_0_0_FEVER_FLAT("beir-v1.0.0-fever.flat",
      "Lucene inverted 'flat' index of BEIR collection 'fever'.",
      "lucene-inverted.beir-v1.0.0-fever.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fever.flat.20221116.505594.tar.gz" },
      "30b5a338f9f16669ed3dae3bae4e7b32"),

  BEIR_V1_0_0_CLIMATE_FEVER_FLAT("beir-v1.0.0-climate-fever.flat",
      "Lucene inverted 'flat' index of BEIR collection 'climate-fever'.",
      "lucene-inverted.beir-v1.0.0-climate-fever.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-climate-fever.flat.20221116.505594.tar.gz" },
      "6e7101f4a5c241ba263bb6a826049826"),

  BEIR_V1_0_0_SCIFACT_FLAT("beir-v1.0.0-scifact.flat",
      "Lucene inverted 'flat' index of BEIR collection 'scifact'.",
      "lucene-inverted.beir-v1.0.0-scifact.flat.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scifact.flat.20221116.505594.tar.gz" },
      "59777038fe0539e600658591e322ea57"),

  // BEIR: multifield
  BEIR_V1_0_0_TREC_COVID_MULTIFIELD("beir-v1.0.0-trec-covid.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'trec-covid'.",
      "lucene-inverted.beir-v1.0.0-trec-covid.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-covid.multifield.20221116.505594.tar.gz" },
      "0439617a927a33727c7b592bd436d8d6"),

  BEIR_V1_0_0_BIOASQ_MULTIFIELD("beir-v1.0.0-bioasq.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'bioasq'.",
      "lucene-inverted.beir-v1.0.0-bioasq.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-bioasq.multifield.20221116.505594.tar.gz" },
      "b2f4fed18b04414193f8368b6891e19c"),

  BEIR_V1_0_0_NFCORPUS_MULTIFIELD("beir-v1.0.0-nfcorpus.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'nfcorpus'.",
      "lucene-inverted.beir-v1.0.0-nfcorpus.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nfcorpus.multifield.20221116.505594.tar.gz" },
      "85cdcceaf06c482ab6a60c34c06c0448"),

  BEIR_V1_0_0_NQ_MULTIFIELD("beir-v1.0.0-nq.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'nq'.",
      "lucene-inverted.beir-v1.0.0-nq.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nq.multifield.20221116.505594.tar.gz" },
      "73b3e3c49c2d79a2851c1ba85f8fbbdf"),

  BEIR_V1_0_0_HOTPOTQA_MULTIFIELD("beir-v1.0.0-hotpotqa.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'hotpotqa'.",
      "lucene-inverted.beir-v1.0.0-hotpotqa.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-hotpotqa.multifield.20221116.505594.tar.gz" },
      "1d9f75122d4b50cb33cccaa125640a38"),

  BEIR_V1_0_0_FIQA_MULTIFIELD("beir-v1.0.0-fiqa.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'fiqa'.",
      "lucene-inverted.beir-v1.0.0-fiqa.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fiqa.multifield.20221116.505594.tar.gz" },
      "1c9330baf3d9004ae46778d4d9e039f6"),

  BEIR_V1_0_0_SIGNAL1M_MULTIFIELD("beir-v1.0.0-signal1m.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'signal1m'.",
      "lucene-inverted.beir-v1.0.0-signal1m.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-signal1m.multifield.20221116.505594.tar.gz" },
      "0735de4f103330975d206285ea85aaf5"),

  BEIR_V1_0_0_TREC_NEWS_MULTIFIELD("beir-v1.0.0-trec-news.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'trec-news'.",
      "lucene-inverted.beir-v1.0.0-trec-news.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-news.multifield.20221116.505594.tar.gz" },
      "a7b5bd79d22d3631dffcad2ffa8afd0a"),

  BEIR_V1_0_0_ROBUST04_MULTIFIELD("beir-v1.0.0-robust04.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'robust04'.",
      "lucene-inverted.beir-v1.0.0-robust04.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-robust04.multifield.20221116.505594.tar.gz" },
      "49db6bf123b6224d0e0973a16ff9c243"),

  BEIR_V1_0_0_ARGUANA_MULTIFIELD("beir-v1.0.0-arguana.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'arguana'.",
      "lucene-inverted.beir-v1.0.0-arguana.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-arguana.multifield.20221116.505594.tar.gz" },
      "895b0d78a1cc40222aaebcff10b6b929"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_MULTIFIELD("beir-v1.0.0-webis-touche2020.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'webis-touche2020'.",
      "lucene-inverted.beir-v1.0.0-webis-touche2020.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-webis-touche2020.multifield.20221116.505594.tar.gz" },
      "390552c8b93dc95bf2f58808d1c8a37d"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_MULTIFIELD("beir-v1.0.0-cqadupstack-android.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-android'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-android.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-android.multifield.20221116.505594.tar.gz" },
      "299fc8b542dabc241320db571b8f8ff0"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_MULTIFIELD("beir-v1.0.0-cqadupstack-english.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-english'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-english.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-english.multifield.20221116.505594.tar.gz" },
      "5bb26ad0ba9184592b5ed935e65b5f17"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_MULTIFIELD("beir-v1.0.0-cqadupstack-gaming.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gaming'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gaming.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gaming.multifield.20221116.505594.tar.gz" },
      "90d1ae9a1862b8b96871b9b94cc46b4e"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_MULTIFIELD("beir-v1.0.0-cqadupstack-gis.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-gis'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gis.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gis.multifield.20221116.505594.tar.gz" },
      "62869b2b6cf569424fed659adf1e5ea7"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_MULTIFIELD("beir-v1.0.0-cqadupstack-mathematica.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-mathematica'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.multifield.20221116.505594.tar.gz" },
      "a78c9d2e29a4b727fbeb38e825629df5"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_MULTIFIELD("beir-v1.0.0-cqadupstack-physics.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-physics'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-physics.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-physics.multifield.20221116.505594.tar.gz" },
      "d6e60e2665c1b6f2bac021dc6c767393"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_MULTIFIELD("beir-v1.0.0-cqadupstack-programmers.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-programmers'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-programmers.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-programmers.multifield.20221116.505594.tar.gz" },
      "77b54cd7613b555d80998b9744eef85c"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_MULTIFIELD("beir-v1.0.0-cqadupstack-stats.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-stats'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-stats.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-stats.multifield.20221116.505594.tar.gz" },
      "8469917c70c767ea398ec2b93aaf04ca"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_MULTIFIELD("beir-v1.0.0-cqadupstack-tex.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-tex'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-tex.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-tex.multifield.20221116.505594.tar.gz" },
      "4d0b0efb2579e0fd73b9156921580a00"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_MULTIFIELD("beir-v1.0.0-cqadupstack-unix.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-unix'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-unix.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-unix.multifield.20221116.505594.tar.gz" },
      "33e2510bb1414ca106766ae787e28670"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_MULTIFIELD("beir-v1.0.0-cqadupstack-webmasters.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-webmasters'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.multifield.20221116.505594.tar.gz" },
      "cb16d3da34b6705747ec07ce89913457"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_MULTIFIELD("beir-v1.0.0-cqadupstack-wordpress.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'cqadupstack-wordpress'.",
      "lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.multifield.20221116.505594.tar.gz" },
      "f619c003e2d0cf84794cc672e18e0437"),

  BEIR_V1_0_0_QUORA_MULTIFIELD("beir-v1.0.0-quora.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'quora'.",
      "lucene-inverted.beir-v1.0.0-quora.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-quora.multifield.20221116.505594.tar.gz" },
      "9248de265c88afc105231659d8c8be09"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_MULTIFIELD("beir-v1.0.0-dbpedia-entity.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'dbpedia-entity'.",
      "lucene-inverted.beir-v1.0.0-dbpedia-entity.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-dbpedia-entity.multifield.20221116.505594.tar.gz" },
      "b7f0ae30f045188a608cc87553cade37"),

  BEIR_V1_0_0_SCIDOCS_MULTIFIELD("beir-v1.0.0-scidocs.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'scidocs'.",
      "lucene-inverted.beir-v1.0.0-scidocs.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scidocs.multifield.20221116.505594.tar.gz" },
      "6409f5ec569530fc3240590dab59bc4c"),

  BEIR_V1_0_0_FEVER_MULTIFIELD("beir-v1.0.0-fever.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'fever'.",
      "lucene-inverted.beir-v1.0.0-fever.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fever.multifield.20221116.505594.tar.gz" },
      "841908da91e7e5eaa0d122faf1a486d8"),

  BEIR_V1_0_0_CLIMATE_FEVER_MULTIFIELD("beir-v1.0.0-climate-fever.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'climate-fever'.",
      "lucene-inverted.beir-v1.0.0-climate-fever.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-climate-fever.multifield.20221116.505594.tar.gz" },
      "2901ac443ca4f0df424a35d068905829"),

  BEIR_V1_0_0_SCIFACT_MULTIFIELD("beir-v1.0.0-scifact.multifield",
      "Lucene inverted 'multifield' index of BEIR collection 'scifact'.",
      "lucene-inverted.beir-v1.0.0-scifact.multifield.20221116.505594.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scifact.multifield.20221116.505594.tar.gz" },
      "b40b26f44f68ab9aa4b573aafea27e2e"),

  // BEIR: SPLADE++ ED
  BEIR_V1_0_0_TREC_COVID_SPLADE_PP_ED("beir-v1.0.0-trec-covid.splade-pp-ed",
      "Lucene impact index of BEIR collection 'trec-covid' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-trec-covid.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-covid.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "e808ff9d4a1f45de9f0bc292900302b4"),

  BEIR_V1_0_0_BIOASQ_SPLADE_PP_ED("beir-v1.0.0-bioasq.splade-pp-ed",
      "Lucene impact index of BEIR collection 'bioasq' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-bioasq.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-bioasq.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "fc661b2c2fa59e24f37c6dfa6de8e682"),

  BEIR_V1_0_0_NFCORPUS_SPLADE_PP_ED("beir-v1.0.0-nfcorpus.splade-pp-ed",
      "Lucene impact index of BEIR collection 'nfcorpus' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-nfcorpus.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nfcorpus.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "7d6e66cca9d2db8bb7caa3bdf330cdd8"),

  BEIR_V1_0_0_NQ_SPLADE_PP_ED("beir-v1.0.0-nq.splade-pp-ed",
      "Lucene impact index of BEIR collection 'nq' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-nq.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-nq.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "a785d6636df60c861829507c3d806ee6"),

  BEIR_V1_0_0_HOTPOTQA_SPLADE_PP_ED("beir-v1.0.0-hotpotqa.splade-pp-ed",
      "Lucene impact index of BEIR collection 'hotpotqa' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-hotpotqa.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-hotpotqa.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "b280ed3f7b12034c0cc4b302f92801b9"),

  BEIR_V1_0_0_FIQA_SPLADE_PP_ED("beir-v1.0.0-fiqa.splade-pp-ed",
      "Lucene impact index of BEIR collection 'fiqa' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-fiqa.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fiqa.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "ea53103c695c0da6cea5b1c8353371b0"),

  BEIR_V1_0_0_SIGNAL1M_SPLADE_PP_ED("beir-v1.0.0-signal1m.splade-pp-ed",
      "Lucene impact index of BEIR collection 'signal1m' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-signal1m.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-signal1m.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "0b46d71c97eabe9ca424f3ab9b2ddc64"),

  BEIR_V1_0_0_TREC_NEWS_SPLADE_PP_ED("beir-v1.0.0-trec-news.splade-pp-ed",
      "Lucene impact index of BEIR collection 'trec-news' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-trec-news.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-trec-news.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "ef4fb032b632b80355db46549f08a026"),

  BEIR_V1_0_0_ROBUST04_SPLADE_PP_ED("beir-v1.0.0-robust04.splade-pp-ed",
      "Lucene impact index of BEIR collection 'robust04' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-robust04.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-robust04.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "c1a6fd094bb9e34e69e10040d9b0ad2a"),

  BEIR_V1_0_0_ARGUANA_SPLADE_PP_ED("beir-v1.0.0-arguana.splade-pp-ed",
      "Lucene impact index of BEIR collection 'arguana' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-arguana.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-arguana.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "c2725b375ca53ff031ee8b4ba8501eb6"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_SPLADE_PP_ED("beir-v1.0.0-webis-touche2020.splade-pp-ed",
      "Lucene impact index of BEIR collection 'webis-touche2020' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-webis-touche2020.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-webis-touche2020.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "1abec77feeb741edfb3c9b7565b42964"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-android.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-android' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-android.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-android.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "0b6b36417df9095e9ed32e4127bdd2fd"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-english.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-english' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-english.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-english.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f2a5f68523117638f957bcc353c956c1"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-gaming.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-gaming' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gaming.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "804851ed2ca5c38464f28263fb664615"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-gis.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-gis' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-gis.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "ee53ba7f26e678f39c3db8997785169a"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-mathematica' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-mathematica.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "c3dd33ddfd364a0665450691963f9036"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-physics.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-physics' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-physics.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "155a130b556072ec0b84788417361228"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-programmers.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-programmers' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-programmers.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f0923dd88b7d4f050d54ff6f6efcc7f5"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-stats.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-stats' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-stats.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "78e62040ed6d44e232e9381e96a56cc7"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-tex.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-tex' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-tex.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "402088c62cbffeba3d710fec408226ed"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-unix.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-unix' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-unix.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "66e884e446ff183e07973c65ccf32625"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-webmasters' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-webmasters.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "17be129cbe65b4e4e64a181f95a56972"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_SPLADE_PP_ED("beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed",
      "Lucene impact index of BEIR collection 'cqadupstack-wordpress' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-cqadupstack-wordpress.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "f20bacfe92f21bc75360a9978278e690"),

  BEIR_V1_0_0_QUORA_SPLADE_PP_ED("beir-v1.0.0-quora.splade-pp-ed",
      "Lucene impact index of BEIR collection 'quora' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-quora.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-quora.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "ce6dbaacf3b7b0e8282020565d324ea5"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_SPLADE_PP_ED("beir-v1.0.0-dbpedia-entity.splade-pp-ed",
      "Lucene impact index of BEIR collection 'dbpedia-entity' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-dbpedia-entity.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-dbpedia-entity.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "fc9ac8329b6e2c054290791e68e0a0e4"),

  BEIR_V1_0_0_SCIDOCS_SPLADE_PP_ED("beir-v1.0.0-scidocs.splade-pp-ed",
      "Lucene impact index of BEIR collection 'scidocs' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-scidocs.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scidocs.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "3285b17da7cd88d2e6e62a3bfc465039"),

  BEIR_V1_0_0_FEVER_SPLADE_PP_ED("beir-v1.0.0-fever.splade-pp-ed",
      "Lucene impact index of BEIR collection 'fever' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-fever.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-fever.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "22e67800879422840f20c7d0008795a9"),

  BEIR_V1_0_0_CLIMATE_FEVER_SPLADE_PP_ED("beir-v1.0.0-climate-fever.splade-pp-ed",
      "Lucene impact index of BEIR collection 'climate-fever' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-climate-fever.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-climate-fever.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "bd5f3c804874ca18f99590037873a1bc"),

  BEIR_V1_0_0_SCIFACT_SPLADE_PP_ED("beir-v1.0.0-scifact.splade-pp-ed",
      "Lucene impact index of BEIR collection 'scifact' encoded by SPLADE++ EnsembleDistil",
      "lucene-inverted.beir-v1.0.0-scifact.splade-pp-ed.20231124.a66f86f.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-inverted.beir-v1.0.0-scifact.splade-pp-ed.20231124.a66f86f.tar.gz" },
      "3abe52209fcd04f411da438a37254e3a"),

  // BEIR: BGE
  BEIR_V1_0_0_TREC_COVID_BGE_BASE_EN_15("beir-v1.0.0-trec-covid.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-covid' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-covid.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-trec-covid.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "2c8cba8525f8ec6920dbb4f0b4a2e0a6"),

  BEIR_V1_0_0_BIOASQ_BGE_BASE_EN_15("beir-v1.0.0-bioasq.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'bioasq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-bioasq.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-bioasq.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "2f4cde27ef5ec3be1193e06854fdaae6"),

  BEIR_V1_0_0_NFCORPUS_BGE_BASE_EN_15("beir-v1.0.0-nfcorpus.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nfcorpus' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-nfcorpus.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d0aa34bf35b59466e7064c424dd82e2c"),

  BEIR_V1_0_0_NQ_BGE_BASE_EN_15("beir-v1.0.0-nq.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'nq' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-nq.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-nq.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "b0bbd85821c734125ffbc0f7ea8f75ae"),

  BEIR_V1_0_0_HOTPOTQA_BGE_BASE_EN_15("beir-v1.0.0-hotpotqa.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'hotpotqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-hotpotqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "83129157f2138a2240b69f8f5404e579"),

  BEIR_V1_0_0_FIQA_BGE_BASE_EN_15("beir-v1.0.0-fiqa.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fiqa' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fiqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-fiqa.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "f2e3191b9d047b88b4692ec3ac87acd0"),

  BEIR_V1_0_0_SIGNAL1M_BGE_BASE_EN_15("beir-v1.0.0-signal1m.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'signal1m' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-signal1m.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "86a5dc12806c5e2f5f1e7cf646ef9004"),

  BEIR_V1_0_0_TREC_NEWS_BGE_BASE_EN_15("beir-v1.0.0-trec-news.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'trec-news' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-trec-news.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-trec-news.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fcb8fae8c46c76931bde0ad51ecb86f8"),

  BEIR_V1_0_0_ROBUST04_BGE_BASE_EN_15("beir-v1.0.0-robust04.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'robust04' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-robust04.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-robust04.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "1b975602bf6b87e0a5815a254eb6e945"),

  BEIR_V1_0_0_ARGUANA_BGE_BASE_EN_15("beir-v1.0.0-arguana.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'arguana' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-arguana.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-arguana.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "468129157636526a3e96bc9427d62808"),

  BEIR_V1_0_0_WEBIS_TOUCHE2020_BGE_BASE_EN_15("beir-v1.0.0-webis-touche2020.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'webis-touche2020' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-webis-touche2020.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "4639db80366f755bb552ce4c736c4aea"),

  BEIR_V1_0_0_CQADUPSTACK_ANDROID_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-android' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-android.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "f7e1f2e737756a84b0273794dcb1038f"),

  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-english' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-english.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "fcdb3fc633b2ca027111536ba422aaed"),

  BEIR_V1_0_0_CQADUPSTACK_GAMING_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gaming' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-gaming.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "d59b216b3df6eb1b724e2f20ceb14407"),

  BEIR_V1_0_0_CQADUPSTACK_GIS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-gis' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-gis.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "1dd42a28e388b30f42ede02565d445ca"),

  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-mathematica' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-mathematica.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "cda37cb1893409c67908cf3aab1467fe"),

  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-physics' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-physics.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "82f71e086930c7d8c5fe423173b9bc2e"),

  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-programmers' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-programmers.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "a7a8e17dcef7b40fde2492436aab1458"),

  BEIR_V1_0_0_CQADUPSTACK_STATS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-stats' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-stats.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "7a304fa64332256976bed5049392605b"),

  BEIR_V1_0_0_CQADUPSTACK_TEX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-tex' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-tex.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "bc5b41b294528611982615c0fcb7ebc7"),

  BEIR_V1_0_0_CQADUPSTACK_UNIX_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-unix' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-unix.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "e42e7b6f46239211f9e9a3ed521d30eb"),

  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-webmasters' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-webmasters.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "21987ab658ba062397095226eb62aaf1"),

  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_BGE_BASE_EN_15("beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'cqadupstack-wordpress' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-cqadupstack-wordpress.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "4e80be8087e8f282c42c2b57e377bb65"),

  BEIR_V1_0_0_QUORA_BGE_BASE_EN_15("beir-v1.0.0-quora.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'quora' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-quora.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-quora.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "064d785db557b011649d5f8b07237eb4"),

  BEIR_V1_0_0_DBPEDIA_ENTITY_BGE_BASE_EN_15("beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'dbpedia-entity' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-dbpedia-entity.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "323d47f84a54894ba5e6ca215999a533"),

  BEIR_V1_0_0_SCIDOCS_BGE_BASE_EN_15("beir-v1.0.0-scidocs.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scidocs' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scidocs.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-scidocs.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "50668564faa9723160b1dba37afbf6d9"),

  BEIR_V1_0_0_FEVER_BGE_BASE_EN_15("beir-v1.0.0-fever.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "33f67e73786a41b454bf88ac2a7c21c7"),

  BEIR_V1_0_0_CLIMATE_FEVER_BGE_BASE_EN_15("beir-v1.0.0-climate-fever.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'climate-fever' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-climate-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-climate-fever.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
      "412337f9f8182e8ec6417bc3cd48288f"),

  BEIR_V1_0_0_SCIFACT_BGE_BASE_EN_15("beir-v1.0.0-scifact.bge-base-en-v1.5",
      "Lucene HNSW index of BEIR collection 'scifact' encoded by BGE-base-en-v1.5.",
      "lucene-hnsw.beir-v1.0.0-scifact.bge-base-en-v1.5.20240223.43c9ec.tar.gz",
      new String[] { "https://rgw.cs.uwaterloo.ca/pyserini/indexes/lucene/lucene-hnsw.beir-v1.0.0-scifact.bge-base-en-v1.5.20240223.43c9ec.tar.gz" },
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
