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

package io.anserini.eval;

import java.nio.file.Path;

public enum Qrels {
  TREC1_ADHOC("qrels.adhoc.51-100.txt"),
  TREC2_ADHOC("qrels.adhoc.101-150.txt"),
  TREC3_ADHOC("qrels.adhoc.151-200.txt"),
  ROBUST04("qrels.robust04.txt"),
  ROBUST05("qrels.robust05.txt"),
  CORE17("qrels.core17.txt"),
  CORE18("qrels.core18.txt"),
  WT10G("qrels.adhoc.451-550.txt"),
  TREC2004_TERABYTE("qrels.terabyte04.701-750.txt"),
  TREC2005_TERABYTE("qrels.terabyte05.751-800.txt"),
  TREC2006_TERABYTE("qrels.terabyte06.801-850.txt"),
  TREC2011_WEB("qrels.web.101-150.txt"),
  TREC2012_WEB("qrels.web.151-200.txt"),
  TREC2013_WEB("qrels.web.201-250.txt"),
  TREC2014_WEB("qrels.web.251-300.txt"),
  MB11("qrels.microblog2011.txt"),
  MB12("qrels.microblog2012.txt"),
  MB13("qrels.microblog2013.txt"),
  MB14("qrels.microblog2014.txt"),
  CAR17V15_BENCHMARK_Y1_TEST("qrels.car17v1.5.benchmarkY1test.txt"),
  CAR17V20_BENCHMARK_Y1_TEST("qrels.car17v2.0.benchmarkY1test.txt"),
  TREC2019_DL_DOC("qrels.dl19-doc.txt"),
  TREC2019_DL_PASSAGE("qrels.dl19-passage.txt"),
  TREC2020_DL_DOC("qrels.dl20-doc.txt"),
  TREC2020_DL_PASSAGE("qrels.dl20-passage.txt"),
  TREC2021_DL_DOC("qrels.dl21-doc.txt"),
  TREC2021_DL_PASSAGE("qrels.dl21-passage.txt"),
  TREC2022_DL_PASSAGE("qrels.dl22-passage.txt"),
  MSMARCO_DOC_DEV("qrels.msmarco-doc.dev.txt"),
  MSMARCO_PASSAGE_DEV_SUBSET("qrels.msmarco-passage.dev-subset.txt"),
  MSMARCO_V2_DOC_DEV("qrels.msmarco-v2-doc.dev.txt"),
  MSMARCO_V2_DOC_DEV2("qrels.msmarco-v2-doc.dev2.txt"),
  MSMARCO_V2_PASSAGE_DEV("qrels.msmarco-v2-passage.dev.txt"),
  MSMARCO_V2_PASSAGE_DEV2("qrels.msmarco-v2-passage.dev2.txt"),
  NTCIR8_ZH("qrels.ntcir8.eval.txt"),
  CLEF2006_FR("qrels.clef06fr.txt"),
  TREC2002_AR("qrels.trec02ar.txt"),
  FIRE2012_BN("qrels.fire12bn.176-225.txt"),
  FIRE2012_HI("qrels.fire12hi.176-225.txt"),
  FIRE2012_EN("qrels.fire12en.176-225.txt"),
  COVID_COMPLETE("qrels.covid-complete.txt"),
  COVID_ROUND1("qrels.covid-round1.txt"),
  COVID_ROUND2("qrels.covid-round2.txt"),
  COVID_ROUND3("qrels.covid-round3.txt"),
  COVID_ROUND3_CUMULATIVE("qrels.covid-round3-cumulative.txt"),
  COVID_ROUND4("qrels.covid-round4.txt"),
  COVID_ROUND4_CUMULATIVE("qrels.covid-round4-cumulative.txt"),
  COVID_ROUND5("qrels.covid-round5.txt"),
  TREC2018_BL("qrels.backgroundlinking18.txt"),
  TREC2019_BL("qrels.backgroundlinking19.txt"),
  TREC2020_BL("qrels.backgroundlinking20.txt"),
  MRTYDI_V11_AR_TRAIN("qrels.mrtydi-v1.1-ar.train.txt"),
  MRTYDI_V11_AR_DEV("qrels.mrtydi-v1.1-ar.dev.txt"),
  MRTYDI_V11_AR_TEST("qrels.mrtydi-v1.1-ar.test.txt"),
  MRTYDI_V11_BN_TRAIN("qrels.mrtydi-v1.1-bn.train.txt"),
  MRTYDI_V11_BN_DEV("qrels.mrtydi-v1.1-bn.dev.txt"),
  MRTYDI_V11_BN_TEST("qrels.mrtydi-v1.1-bn.test.txt"),
  MRTYDI_V11_EN_TRAIN("qrels.mrtydi-v1.1-en.train.txt"),
  MRTYDI_V11_EN_DEV("qrels.mrtydi-v1.1-en.dev.txt"),
  MRTYDI_V11_EN_TEST("qrels.mrtydi-v1.1-en.test.txt"),
  MRTYDI_V11_FI_TRAIN("qrels.mrtydi-v1.1-fi.train.txt"),
  MRTYDI_V11_FI_DEV("qrels.mrtydi-v1.1-fi.dev.txt"),
  MRTYDI_V11_FI_TEST("qrels.mrtydi-v1.1-fi.test.txt"),
  MRTYDI_V11_ID_TRAIN("qrels.mrtydi-v1.1-id.train.txt"),
  MRTYDI_V11_ID_DEV("qrels.mrtydi-v1.1-id.dev.txt"),
  MRTYDI_V11_ID_TEST("qrels.mrtydi-v1.1-id.test.txt"),
  MRTYDI_V11_JA_TRAIN("qrels.mrtydi-v1.1-ja.train.txt"),
  MRTYDI_V11_JA_DEV("qrels.mrtydi-v1.1-ja.dev.txt"),
  MRTYDI_V11_JA_TEST("qrels.mrtydi-v1.1-ja.test.txt"),
  MRTYDI_V11_KO_TRAIN("qrels.mrtydi-v1.1-ko.train.txt"),
  MRTYDI_V11_KO_DEV("qrels.mrtydi-v1.1-ko.dev.txt"),
  MRTYDI_V11_KO_TEST("qrels.mrtydi-v1.1-ko.test.txt"),
  MRTYDI_V11_RU_TRAIN("qrels.mrtydi-v1.1-ru.train.txt"),
  MRTYDI_V11_RU_DEV("qrels.mrtydi-v1.1-ru.dev.txt"),
  MRTYDI_V11_RU_TEST("qrels.mrtydi-v1.1-ru.test.txt"),
  MRTYDI_V11_SW_TRAIN("qrels.mrtydi-v1.1-sw.train.txt"),
  MRTYDI_V11_SW_DEV("qrels.mrtydi-v1.1-sw.dev.txt"),
  MRTYDI_V11_SW_TEST("qrels.mrtydi-v1.1-sw.test.txt"),
  MRTYDI_V11_TE_TRAIN("qrels.mrtydi-v1.1-te.train.txt"),
  MRTYDI_V11_TE_DEV("qrels.mrtydi-v1.1-te.dev.txt"),
  MRTYDI_V11_TE_TEST("qrels.mrtydi-v1.1-te.test.txt"),
  MRTYDI_V11_TH_TRAIN("qrels.mrtydi-v1.1-th.train.txt"),
  MRTYDI_V11_TH_DEV("qrels.mrtydi-v1.1-th.dev.txt"),
  MRTYDI_V11_TH_TEST("qrels.mrtydi-v1.1-th.test.txt"),
  BEIR_V1_0_0_TREC_COVID_TEST("qrels.beir-v1.0.0-trec-covid.test.txt"),
  BEIR_V1_0_0_BIOASQ_TEST("qrels.beir-v1.0.0-bioasq.test.txt"),
  BEIR_V1_0_0_NFCORPUS_TEST("qrels.beir-v1.0.0-nfcorpus.test.txt"),
  BEIR_V1_0_0_NQ_TEST("qrels.beir-v1.0.0-nq.test.txt"),
  BEIR_V1_0_0_HOTPOTQA_TEST("qrels.beir-v1.0.0-hotpotqa.test.txt"),
  BEIR_V1_0_0_FIQA_TEST("qrels.beir-v1.0.0-fiqa.test.txt"),
  BEIR_V1_0_0_SIGNAL1M_TEST("qrels.beir-v1.0.0-signal1m.test.txt"),
  BEIR_V1_0_0_TREC_NEWS_TEST("qrels.beir-v1.0.0-trec-news.test.txt"),
  BEIR_V1_0_0_ROBUST04_TEST("qrels.beir-v1.0.0-robust04.test.txt"),
  BEIR_V1_0_0_ARGUANA_TEST("qrels.beir-v1.0.0-arguana.test.txt"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST("qrels.beir-v1.0.0-webis-touche2020.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST("qrels.beir-v1.0.0-cqadupstack-android.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST("qrels.beir-v1.0.0-cqadupstack-english.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST("qrels.beir-v1.0.0-cqadupstack-gaming.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST("qrels.beir-v1.0.0-cqadupstack-gis.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST("qrels.beir-v1.0.0-cqadupstack-mathematica.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST("qrels.beir-v1.0.0-cqadupstack-physics.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST("qrels.beir-v1.0.0-cqadupstack-programmers.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST("qrels.beir-v1.0.0-cqadupstack-stats.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST("qrels.beir-v1.0.0-cqadupstack-tex.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST("qrels.beir-v1.0.0-cqadupstack-unix.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST("qrels.beir-v1.0.0-cqadupstack-webmasters.test.txt"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST("qrels.beir-v1.0.0-cqadupstack-wordpress.test.txt"),
  BEIR_V1_0_0_QUORA_TEST("qrels.beir-v1.0.0-quora.test.txt"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST("qrels.beir-v1.0.0-dbpedia-entity.test.txt"),
  BEIR_V1_0_0_SCIDOCS_TEST("qrels.beir-v1.0.0-scidocs.test.txt"),
  BEIR_V1_0_0_FEVER_TEST("qrels.beir-v1.0.0-fever.test.txt"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST("qrels.beir-v1.0.0-climate-fever.test.txt"),
  BEIR_V1_0_0_SCIFACT_TEST("qrels.beir-v1.0.0-scifact.test.txt"),
  HC4_V1_0_FA_DEV("qrels.hc4-v1.0-fa.dev.txt"),
  HC4_V1_0_FA_TEST("qrels.hc4-v1.0-fa.test.txt"),
  HC4_V1_0_RU_DEV("qrels.hc4-v1.0-ru.dev.txt"),
  HC4_V1_0_RU_TEST("qrels.hc4-v1.0-ru.test.txt"),
  HC4_V1_0_ZH_DEV("qrels.hc4-v1.0-zh.dev.txt"),
  HC4_V1_0_ZH_TEST("qrels.hc4-v1.0-zh.test.txt"),
  HC4_NEUCLIR22_FA_TEST("qrels.hc4-neuclir22-fa.test.txt"),
  HC4_NEUCLIR22_RU_TEST("qrels.hc4-neuclir22-ru.test.txt"),
  HC4_NEUCLIR22_ZH_TEST("qrels.hc4-neuclir22-zh.test.txt"),
  NEUCLIR22_FA("qrels.neuclir22-fa.txt"),
  NEUCLIR22_RU("qrels.neuclir22-ru.txt"),
  NEUCLIR22_ZH("qrels.neuclir22-zh.txt"),
  MIRACL_V10_AR_DEV("qrels.miracl-v1.0-ar-dev.tsv"),
  MIRACL_V10_BN_DEV("qrels.miracl-v1.0-bn-dev.tsv"),
  MIRACL_V10_EN_DEV("qrels.miracl-v1.0-en-dev.tsv"),
  MIRACL_V10_ES_DEV("qrels.miracl-v1.0-es-dev.tsv"),
  MIRACL_V10_FA_DEV("qrels.miracl-v1.0-fa-dev.tsv"),
  MIRACL_V10_FI_DEV("qrels.miracl-v1.0-fi-dev.tsv"),
  MIRACL_V10_FR_DEV("qrels.miracl-v1.0-fr-dev.tsv"),
  MIRACL_V10_HI_DEV("qrels.miracl-v1.0-hi-dev.tsv"),
  MIRACL_V10_ID_DEV("qrels.miracl-v1.0-id-dev.tsv"),
  MIRACL_V10_JA_DEV("qrels.miracl-v1.0-ja-dev.tsv"),
  MIRACL_V10_KO_DEV("qrels.miracl-v1.0-ko-dev.tsv"),
  MIRACL_V10_RU_DEV("qrels.miracl-v1.0-ru-dev.tsv"),
  MIRACL_V10_SW_DEV("qrels.miracl-v1.0-sw-dev.tsv"),
  MIRACL_V10_TE_DEV("qrels.miracl-v1.0-te-dev.tsv"),
  MIRACL_V10_TH_DEV("qrels.miracl-v1.0-th-dev.tsv"),
  MIRACL_V10_ZH_DEV("qrels.miracl-v1.0-zh-dev.tsv"),
  MIRACL_V10_DE_DEV("qrels.miracl-v1.0-de-dev.tsv"),
  MIRACL_V10_YO_DEV("qrels.miracl-v1.0-yo-dev.tsv");

  public final String path;

  Qrels(String path) {
    this.path = path;
  }

  public static boolean contains(Path topicPath) {
    for (Qrels c : Qrels.values()) {
      if (c.path.equals(topicPath.getFileName().toString())) {
        return true;
      }
    }
    return false;
  }
}
