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

package io.anserini.search.topicreader;

import java.util.HashMap;
import java.util.Map;

/**
 * An enumeration comprising standard sets of topics from various evaluations.
 */
public enum Topics {
  TREC1_ADHOC(TrecTopicReader.class, "topics.adhoc.51-100.txt"),
  TREC2_ADHOC(TrecTopicReader.class, "topics.adhoc.101-150.txt"),
  TREC3_ADHOC(TrecTopicReader.class, "topics.adhoc.151-200.txt"),
  ROBUST04(TrecTopicReader.class, "topics.robust04.txt"),
  ROBUST05(TrecTopicReader.class, "topics.robust05.txt"),
  CORE17(TrecTopicReader.class, "topics.core17.txt"),
  CORE18(TrecTopicReader.class, "topics.core18.txt"),
  WT10G(TrecTopicReader.class, "topics.adhoc.451-550.txt"),
  TREC2004_TERABYTE(TrecTopicReader.class, "topics.terabyte04.701-750.txt"),
  TREC2005_TERABYTE(TrecTopicReader.class, "topics.terabyte05.751-800.txt"),
  TREC2006_TERABYTE(TrecTopicReader.class, "topics.terabyte06.801-850.txt"),
  TREC2007_MILLION_QUERY(WebTopicReader.class, "topics.mq.1-10000.txt"),
  TREC2008_MILLION_QUERY(WebTopicReader.class, "topics.mq.10001-20000.txt"),
  TREC2009_MILLION_QUERY(PrioritizedWebTopicReader.class, "topics.mq.20001-60000.txt"),
  TREC2009_WEB(WebxmlTopicReader.class, "topics.web.1-50.txt"),
  TREC2010_WEB(WebxmlTopicReader.class, "topics.web.51-100.txt"),
  TREC2011_WEB(WebxmlTopicReader.class, "topics.web.101-150.txt"),
  TREC2012_WEB(WebxmlTopicReader.class, "topics.web.151-200.txt"),
  TREC2013_WEB(WebxmlTopicReader.class, "topics.web.201-250.txt"),
  TREC2014_WEB(WebxmlTopicReader.class, "topics.web.251-300.txt"),
  MB11(MicroblogTopicReader.class, "topics.microblog2011.txt"),
  MB12(MicroblogTopicReader.class, "topics.microblog2012.txt"),
  MB13(MicroblogTopicReader.class, "topics.microblog2013.txt"),
  MB14(MicroblogTopicReader.class, "topics.microblog2014.txt"),
  CAR17V15_BENCHMARK_Y1_TEST(CarTopicReader.class, "topics.car17v1.5.benchmarkY1test.txt"),
  CAR17V20_BENCHMARK_Y1_TEST(CarTopicReader.class, "topics.car17v2.0.benchmarkY1test.txt"),

  // TREC DL topics
  TREC2019_DL_DOC(TsvIntTopicReader.class,"topics.dl19-doc.txt"),
  TREC2019_DL_DOC_WP(TsvIntTopicReader.class,"topics.dl19-doc.wp.tsv.gz"),
  TREC2019_DL_DOC_UNICOIL(TsvIntTopicReader.class,"topics.dl19-doc.unicoil.0shot.tsv.gz"),
  TREC2019_DL_DOC_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl19-doc.unicoil-noexp.0shot.tsv.gz"),
  TREC2019_DL_PASSAGE(TsvIntTopicReader.class,"topics.dl19-passage.txt"),
  TREC2019_DL_PASSAGE_WP(TsvIntTopicReader.class,"topics.dl19-passage.wp.tsv.gz"),
  TREC2019_DL_PASSAGE_UNICOIL(TsvIntTopicReader.class,"topics.dl19-passage.unicoil.0shot.tsv.gz"),
  TREC2019_DL_PASSAGE_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl19-passage.unicoil-noexp.0shot.tsv.gz"),
  TREC2019_DL_PASSAGE_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvIntTopicReader.class,"topics.dl19-passage.splade_distil_cocodenser_medium.tsv.gz"),
  TREC2019_DL_PASSAGE_SPLADE_PP_ED(TsvIntTopicReader.class,"topics.dl19-passage.splade-pp-ed.tsv.gz"),
  TREC2019_DL_PASSAGE_SPLADE_PP_SD(TsvIntTopicReader.class,"topics.dl19-passage.splade-pp-sd.tsv.gz"),
  TREC2019_DL_PASSAGE_COS_DPR_DISTIL(JsonIntVectorTopicReader.class, "topics.dl19-passage.cos-dpr-distil.jsonl.gz"),
  TREC2019_DL_PASSAGE_BGE_BASE_EN_15(JsonIntVectorTopicReader.class, "topics.dl19-passage.bge-base-en-v1.5.jsonl.gz"),
  TREC2019_DL_PASSAGE_COHERE_EMBED_ENGLISH_30(JsonIntVectorTopicReader.class, "topics.dl19-passage.cohere-embed-english-v3.0.jsonl.gz"),
  TREC2020_DL(TsvIntTopicReader.class,"topics.dl20.txt"),
  TREC2020_DL_WP(TsvIntTopicReader.class,"topics.dl20.wp.tsv.gz"),
  TREC2020_DL_UNICOIL(TsvIntTopicReader.class,"topics.dl20.unicoil.0shot.tsv.gz"),
  TREC2020_DL_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl20.unicoil-noexp.0shot.tsv.gz"),
  TREC2020_DL_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvIntTopicReader.class,"topics.dl20.splade_distil_cocodenser_medium.tsv.gz"),
  TREC2020_DL_SPLADE_PP_ED(TsvIntTopicReader.class,"topics.dl20.splade-pp-ed.tsv.gz"),
  TREC2020_DL_SPLADE_PP_SD(TsvIntTopicReader.class,"topics.dl20.splade-pp-sd.tsv.gz"),
  TREC2020_DL_COS_DPR_DISTIL(JsonIntVectorTopicReader.class, "topics.dl20.cos-dpr-distil.jsonl.gz"),
  TREC2020_DL_BGE_BASE_EN_15(JsonIntVectorTopicReader.class, "topics.dl20.bge-base-en-v1.5.jsonl.gz"),
  TREC2020_DL_COHERE_EMBED_ENGLISH_30(JsonIntVectorTopicReader.class, "topics.dl20.cohere-embed-english-v3.0.jsonl.gz"),
  TREC2021_DL(TsvIntTopicReader.class,"topics.dl21.txt"),
  TREC2021_DL_UNICOIL(TsvIntTopicReader.class,"topics.dl21.unicoil.0shot.tsv.gz"),
  TREC2021_DL_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl21.unicoil-noexp.0shot.tsv.gz"),
  TREC2021_DL_SPLADE_PP_ED(TsvIntTopicReader.class,"topics.dl21.splade-pp-ed.tsv.gz"),
  TREC2021_DL_SPLADE_PP_SD(TsvIntTopicReader.class,"topics.dl21.splade-pp-sd.tsv.gz"),
  TREC2022_DL(TsvIntTopicReader.class,"topics.dl22.txt"),
  TREC2022_DL_UNICOIL(TsvIntTopicReader.class,"topics.dl22.unicoil.0shot.tsv.gz"),
  TREC2022_DL_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl22.unicoil-noexp.0shot.tsv.gz"),
  TREC2022_DL_SPLADE_PP_ED(TsvIntTopicReader.class,"topics.dl22.splade-pp-ed.tsv.gz"),
  TREC2022_DL_SPLADE_PP_SD(TsvIntTopicReader.class,"topics.dl22.splade-pp-sd.tsv.gz"),
  TREC2023_DL(TsvIntTopicReader.class, "topics.dl23.txt"),
  TREC2023_DL_UNICOIL(TsvIntTopicReader.class,"topics.dl23.unicoil.0shot.tsv.gz"),
  TREC2023_DL_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.dl23.unicoil-noexp.0shot.tsv.gz"),
  TREC2023_DL_SPLADE_PP_ED(TsvIntTopicReader.class,"topics.dl23.splade-pp-ed.tsv.gz"),
  TREC2023_DL_SPLADE_PP_SD(TsvIntTopicReader.class,"topics.dl23.splade-pp-sd.tsv.gz"),

  TREC2024_RAG_RAGGY_DEV(TsvIntTopicReader.class, "topics.rag24.raggy-dev.txt"),
  TREC2024_RAG_RESEARCHY_DEV(TsvIntTopicReader.class, "topics.rag24.researchy-dev.txt"),

  // MS MARCO V1 topics
  MSMARCO_DOC_DEV(TsvIntTopicReader.class,"topics.msmarco-doc.dev.txt"),
  MSMARCO_DOC_DEV_WP(TsvIntTopicReader.class,"topics.msmarco-doc.dev.wp.tsv.gz"),
  MSMARCO_DOC_DEV_UNICOIL(TsvIntTopicReader.class,"topics.msmarco-doc.dev.unicoil.tsv.gz"),
  MSMARCO_DOC_DEV_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.msmarco-doc.dev.unicoil-noexp.tsv.gz"),
  MSMARCO_DOC_TEST(TsvIntTopicReader.class,"topics.msmarco-doc.test.txt"),
  MSMARCO_PASSAGE_DEV_SUBSET(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.txt"),
  MSMARCO_PASSAGE_DEV_SUBSET_WP(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.wp.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_DEEPIMPACT(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.deepimpact.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.unicoil.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL_NOEXP(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.unicoil-noexp.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_UNICOIL_TILDE(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.unicoil-tilde-expansion.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_DISTILL_SPLADE_MAX(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.distill-splade-max.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.splade_distil_cocodenser_medium.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.splade-pp-ed.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_SD(TsvIntTopicReader.class, "topics.msmarco-passage.dev-subset.splade-pp-sd.tsv.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL(JsonIntVectorTopicReader.class, "topics.msmarco-passage.dev-subset.cos-dpr-distil.jsonl.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15(JsonIntVectorTopicReader.class, "topics.msmarco-passage.dev-subset.bge-base-en-v1.5.jsonl.gz"),
  MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30(JsonIntVectorTopicReader.class, "topics.msmarco-passage.dev-subset.cohere-embed-english-v3.0.jsonl.gz"),
  MSMARCO_PASSAGE_TEST_SUBSET(TsvIntTopicReader.class, "topics.msmarco-passage.test-subset.txt"),

  // MS MARCO V2 topics
  MSMARCO_V2_DOC_DEV(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev.txt"),
  MSMARCO_V2_DOC_DEV_UNICOIL(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev.unicoil.0shot.tsv.gz"),
  MSMARCO_V2_DOC_DEV_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev.unicoil-noexp.0shot.tsv.gz"),
  MSMARCO_V2_DOC_DEV2(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev2.txt"),
  MSMARCO_V2_DOC_DEV2_UNICOIL(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev2.unicoil.0shot.tsv.gz"),
  MSMARCO_V2_DOC_DEV2_UNICOIL_NOEXP(TsvIntTopicReader.class,"topics.msmarco-v2-doc.dev2.unicoil-noexp.0shot.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev.txt"),
  MSMARCO_V2_PASSAGE_DEV_UNICOIL(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev.unicoil.0shot.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV_UNICOIL_NOEXP(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev.unicoil-noexp.0shot.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV_SPLADE_PP_ED(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev.splade-pp-ed.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV_SPLADE_PP_SD(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev.splade-pp-sd.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV2(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev2.txt"),
  MSMARCO_V2_PASSAGE_DEV2_UNICOIL(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev2.unicoil.0shot.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV2_UNICOIL_NOEXP(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev2.unicoil-noexp.0shot.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV2_SPLADE_PP_ED(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev2.splade-pp-ed.tsv.gz"),
  MSMARCO_V2_PASSAGE_DEV2_SPLADE_PP_SD(TsvIntTopicReader.class, "topics.msmarco-v2-passage.dev2.splade-pp-sd.tsv.gz"),

  NTCIR8_ZH(TsvStringTopicReader.class, "topics.ntcir8zh.eval.txt"),
  CLEF2006_FR(TsvStringTopicReader.class, "topics.clef06fr.mono.fr.txt"),
  TREC2002_AR(TrecTopicReader.class, "topics.trec02ar-ar.txt"),
  FIRE2012_BN(TrecTopicReader.class, "topics.fire12bn.176-225.txt"),
  FIRE2012_HI(TrecTopicReader.class, "topics.fire12hi.176-225.txt"),
  FIRE2012_EN(TrecTopicReader.class, "topics.fire12en.176-225.txt"),
  COVID_ROUND1(CovidTopicReader.class, "topics.covid-round1.xml"),
  COVID_ROUND1_UDEL(CovidTopicReader.class, "topics.covid-round1-udel.xml"),
  COVID_ROUND2(CovidTopicReader.class, "topics.covid-round2.xml"),
  COVID_ROUND2_UDEL(CovidTopicReader.class, "topics.covid-round2-udel.xml"),
  COVID_ROUND3(CovidTopicReader.class, "topics.covid-round3.xml"),
  COVID_ROUND3_UDEL(CovidTopicReader.class, "topics.covid-round3-udel.xml"),
  COVID_ROUND4(CovidTopicReader.class, "topics.covid-round4.xml"),
  COVID_ROUND4_UDEL(CovidTopicReader.class, "topics.covid-round4-udel.xml"),
  COVID_ROUND5(CovidTopicReader.class, "topics.covid-round5.xml"),
  COVID_ROUND5_UDEL(CovidTopicReader.class, "topics.covid-round5-udel.xml"),
  TREC2018_BL(BackgroundLinkingTopicReader.class, "topics.backgroundlinking18.txt"),
  TREC2019_BL(BackgroundLinkingTopicReader.class, "topics.backgroundlinking19.txt"),
  TREC2020_BL(BackgroundLinkingTopicReader.class, "topics.backgroundlinking20.txt"),
  EPIDEMIC_QA_EXPERT_PRELIM(EpidemicQATopicReader.class, "topics.epidemic-qa.expert.prelim.json"),
  EPIDEMIC_QA_CONSUMER_PRELIM(EpidemicQATopicReader.class, "topics.epidemic-qa.consumer.prelim.json"),
  DPR_NQ_DEV(DprNqTopicReader.class, "topics.dpr.nq.dev.txt"),
  DPR_NQ_TEST(DprNqTopicReader.class, "topics.dpr.nq.test.txt"),
  DPR_TRIVIA_DEV(DprNqTopicReader.class, "topics.dpr.trivia.dev.txt"),
  DPR_TRIVIA_TEST(DprNqTopicReader.class, "topics.dpr.trivia.test.txt"),
  DPR_WQ_TEST(DprJsonlTopicReader.class, "topics.dpr.wq.test.txt"),
  DPR_CURATED_TEST(DprJsonlTopicReader.class, "topics.dpr.curated.test.txt"),
  DPR_SQUAD_TEST(DprJsonlTopicReader.class, "topics.dpr.squad.test.txt"),
  NQ_DEV(DprNqTopicReader.class, "topics.nq.dev.txt"),
  NQ_TEST(DprNqTopicReader.class, "topics.nq.test.txt"),
  NQ_TEST_GART5_ANSWERS(TsvIntTopicReader.class, "topics.nq.test.gar-t5.answers.tsv"),
  NQ_TEST_GART5_TITLES(TsvIntTopicReader.class, "topics.nq.test.gar-t5.titles.tsv"),
  NQ_TEST_GART5_SENTENCES(TsvIntTopicReader.class, "topics.nq.test.gar-t5.sentences.tsv"),
  NQ_TEST_GART5_ALL(TsvIntTopicReader.class, "topics.nq.test.gar-t5.all.tsv"),
  DPR_TRIVIA_TEST_GART5_ANSWERS(TsvIntTopicReader.class, "topics.dpr.trivia.test.gar-t5.answers.tsv"),
  DPR_TRIVIA_TEST_GART5_TITLES(TsvIntTopicReader.class, "topics.dpr.trivia.test.gar-t5.titles.tsv"),
  DPR_TRIVIA_TEST_GART5_SENTENCES(TsvIntTopicReader.class, "topics.dpr.trivia.test.gar-t5.sentences.tsv"),
  DPR_TRIVIA_TEST_GART5_ALL(TsvIntTopicReader.class, "topics.dpr.trivia.test.gar-t5.all.tsv"),

  // Mr.TyDi queries
  MRTYDI_V11_AR_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ar.train.txt.gz"),
  MRTYDI_V11_AR_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ar.dev.txt.gz"),
  MRTYDI_V11_AR_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ar.test.txt.gz"),
  MRTYDI_V11_BN_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-bn.train.txt.gz"),
  MRTYDI_V11_BN_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-bn.dev.txt.gz"),
  MRTYDI_V11_BN_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-bn.test.txt.gz"),
  MRTYDI_V11_EN_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-en.train.txt.gz"),
  MRTYDI_V11_EN_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-en.dev.txt.gz"),
  MRTYDI_V11_EN_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-en.test.txt.gz"),
  MRTYDI_V11_FI_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-fi.train.txt.gz"),
  MRTYDI_V11_FI_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-fi.dev.txt.gz"),
  MRTYDI_V11_FI_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-fi.test.txt.gz"),
  MRTYDI_V11_ID_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-id.train.txt.gz"),
  MRTYDI_V11_ID_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-id.dev.txt.gz"),
  MRTYDI_V11_ID_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-id.test.txt.gz"),
  MRTYDI_V11_JA_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ja.train.txt.gz"),
  MRTYDI_V11_JA_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ja.dev.txt.gz"),
  MRTYDI_V11_JA_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ja.test.txt.gz"),
  MRTYDI_V11_KO_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ko.train.txt.gz"),
  MRTYDI_V11_KO_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ko.dev.txt.gz"),
  MRTYDI_V11_KO_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ko.test.txt.gz"),
  MRTYDI_V11_RU_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ru.train.txt.gz"),
  MRTYDI_V11_RU_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ru.dev.txt.gz"),
  MRTYDI_V11_RU_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-ru.test.txt.gz"),
  MRTYDI_V11_SW_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-sw.train.txt.gz"),
  MRTYDI_V11_SW_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-sw.dev.txt.gz"),
  MRTYDI_V11_SW_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-sw.test.txt.gz"),
  MRTYDI_V11_TE_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-te.train.txt.gz"),
  MRTYDI_V11_TE_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-te.dev.txt.gz"),
  MRTYDI_V11_TE_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-te.test.txt.gz"),
  MRTYDI_V11_TH_TRAIN(TsvIntTopicReader.class, "topics.mrtydi-v1.1-th.train.txt.gz"),
  MRTYDI_V11_TH_DEV(TsvIntTopicReader.class, "topics.mrtydi-v1.1-th.dev.txt.gz"),
  MRTYDI_V11_TH_TEST(TsvIntTopicReader.class, "topics.mrtydi-v1.1-th.test.txt.gz"),

  // BEIR (v1.0.0): original queries
  BEIR_V1_0_0_TREC_COVID_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.tsv.gz"),
  BEIR_V1_0_0_BIOASQ_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-bioasq.test.tsv.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.tsv.gz"),
  BEIR_V1_0_0_NQ_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-nq.test.tsv.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.tsv.gz"),
  BEIR_V1_0_0_FIQA_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-fiqa.test.tsv.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-signal1m.test.tsv.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-news.test.tsv.gz"),
  BEIR_V1_0_0_ROBUST04_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-robust04.test.tsv.gz"),
  BEIR_V1_0_0_ARGUANA_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-arguana.test.tsv.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.tsv.gz"),
  BEIR_V1_0_0_QUORA_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-quora.test.tsv.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.tsv.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-scidocs.test.tsv.gz"),
  BEIR_V1_0_0_FEVER_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-fever.test.tsv.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.tsv.gz"),
  BEIR_V1_0_0_SCIFACT_TEST(TsvStringTopicReader.class, "topics.beir-v1.0.0-scifact.test.tsv.gz"),

  // BEIR (v1.0.0): word piece queries
  BEIR_V1_0_0_TREC_COVID_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.wp.tsv.gz"),
  BEIR_V1_0_0_BIOASQ_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-bioasq.test.wp.tsv.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.wp.tsv.gz"),
  BEIR_V1_0_0_NQ_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-nq.test.wp.tsv.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.wp.tsv.gz"),
  BEIR_V1_0_0_FIQA_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-fiqa.test.wp.tsv.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-signal1m.test.wp.tsv.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-news.test.wp.tsv.gz"),
  BEIR_V1_0_0_ROBUST04_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-robust04.test.wp.tsv.gz"),
  BEIR_V1_0_0_ARGUANA_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-arguana.test.wp.tsv.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.wp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.wp.tsv.gz"),
  BEIR_V1_0_0_QUORA_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-quora.test.wp.tsv.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.wp.tsv.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-scidocs.test.wp.tsv.gz"),
  BEIR_V1_0_0_FEVER_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-fever.test.wp.tsv.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.wp.tsv.gz"),
  BEIR_V1_0_0_SCIFACT_TEST_WP(TsvStringTopicReader.class, "topics.beir-v1.0.0-scifact.test.wp.tsv.gz"),

  // BEIR (v1.0.0): pre-encoded queries for SPLADE-distill CoCodenser-medium
  BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_BIOASQ_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-bioasq.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_NQ_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-nq.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_FIQA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-fiqa.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-signal1m.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-news.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_ROBUST04_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-robust04.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_ARGUANA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-arguana.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_QUORA_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-quora.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-scidocs.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_FEVER_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-fever.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.splade_distil_cocodenser_medium.tsv.gz"),
  BEIR_V1_0_0_SCIFACT_TEST_SPLADE_DISTILL_COCODENSER_MEDIUM(TsvStringTopicReader.class, "topics.beir-v1.0.0-scifact.test.splade_distil_cocodenser_medium.tsv.gz"),

  // BEIR (v1.0.0): pre-encoded queries for SPLADE++ (CoCondenser-EnsembleDistil)
  BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_BIOASQ_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-bioasq.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_NQ_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-nq.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_FIQA_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-fiqa.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-signal1m.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-news.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_ROBUST04_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-robust04.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_ARGUANA_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-arguana.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_QUORA_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-quora.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-scidocs.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_FEVER_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-fever.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.splade-pp-ed.tsv.gz"),
  BEIR_V1_0_0_SCIFACT_TEST_SPLADE_PP_ED(TsvStringTopicReader.class, "topics.beir-v1.0.0-scifact.test.splade-pp-ed.tsv.gz"),

  // BEIR (v1.0.0): pre-encoded queries for BGE-base-en-v1.5
  BEIR_V1_0_0_TREC_COVID_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_BIOASQ_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-bioasq.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_NQ_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-nq.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_FIQA_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-fiqa.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-signal1m.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-trec-news.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_ROBUST04_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-robust04.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_ARGUANA_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-arguana.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_QUORA_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-quora.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-scidocs.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_FEVER_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-fever.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.bge-base-en-v1.5.jsonl.gz"),
  BEIR_V1_0_0_SCIFACT_TEST_BGE_BASE_EN_15(JsonStringVectorTopicReader.class, "topics.beir-v1.0.0-scifact.test.bge-base-en-v1.5.jsonl.gz"),

  // BEIR (v1.0.0): pre-encoded queries for uniCOIL-noexp
  BEIR_V1_0_0_TREC_COVID_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-covid.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_BIOASQ_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-bioasq.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_NFCORPUS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-nfcorpus.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_NQ_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-nq.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_HOTPOTQA_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-hotpotqa.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_FIQA_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-fiqa.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_SIGNAL1M_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-signal1m.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_TREC_NEWS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-trec-news.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_ROBUST04_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-robust04.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_ARGUANA_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-arguana.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-webis-touche2020.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-android.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-english.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gaming.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-gis.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-mathematica.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-physics.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-programmers.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-stats.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-tex.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-unix.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-webmasters.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-cqadupstack-wordpress.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_QUORA_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-quora.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-dbpedia-entity.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_SCIDOCS_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-scidocs.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_FEVER_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-fever.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_CLIMATE_FEVER_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-climate-fever.test.unicoil-noexp.tsv.gz"),
  BEIR_V1_0_0_SCIFACT_TEST_UNCOIL_NOEXP(TsvStringTopicReader.class, "topics.beir-v1.0.0-scifact.test.unicoil-noexp.tsv.gz"),

  // HC4 V1.0 Topics
  HC4_V1_0_FA_DEV_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.dev.title.tsv"),
  HC4_V1_0_FA_DEV_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.dev.desc.tsv"),
  HC4_V1_0_FA_DEV_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.dev.desc.title.tsv"),
  HC4_V1_0_FA_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.test.title.tsv"),
  HC4_V1_0_FA_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.test.desc.tsv"),
  HC4_V1_0_FA_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.test.desc.title.tsv"),
  HC4_V1_0_FA_EN_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.en.test.title.tsv"),
  HC4_V1_0_FA_EN_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.en.test.desc.tsv"),
  HC4_V1_0_FA_EN_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-fa.en.test.desc.title.tsv"),
  HC4_V1_0_RU_DEV_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.dev.title.tsv"),
  HC4_V1_0_RU_DEV_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.dev.desc.tsv"),
  HC4_V1_0_RU_DEV_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.dev.desc.title.tsv"),
  HC4_V1_0_RU_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.test.title.tsv"),
  HC4_V1_0_RU_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.test.desc.tsv"),
  HC4_V1_0_RU_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.test.desc.title.tsv"),
  HC4_V1_0_RU_EN_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.en.test.title.tsv"),
  HC4_V1_0_RU_EN_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.en.test.desc.tsv"),
  HC4_V1_0_RU_EN_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-ru.en.test.desc.title.tsv"),
  HC4_V1_0_ZH_DEV_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.dev.title.tsv"),
  HC4_V1_0_ZH_DEV_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.dev.desc.tsv"),
  HC4_V1_0_ZH_DEV_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.dev.desc.title.tsv"),
  HC4_V1_0_ZH_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.test.title.tsv"),
  HC4_V1_0_ZH_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.test.desc.tsv"),
  HC4_V1_0_ZH_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.test.desc.title.tsv"),
  HC4_V1_0_ZH_EN_TEST_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.en.test.title.tsv"),
  HC4_V1_0_ZH_EN_TEST_DESC(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.en.test.desc.tsv"),
  HC4_V1_0_ZH_EN_TEST_DESC_TITLE(TsvIntTopicReader.class, "topics.hc4-v1.0-zh.en.test.desc.title.tsv"),

  // TREC NeuCLIR 2022 Topics
  NEUCLIR22_EN_TITLE(TsvIntTopicReader.class,         "topics.neuclir22-en.original-title.txt"),
  NEUCLIR22_EN_DESC(TsvIntTopicReader.class,          "topics.neuclir22-en.original-desc.txt"),
  NEUCLIR22_EN_DESC_TITLE(TsvIntTopicReader.class,    "topics.neuclir22-en.original-desc_title.txt"),
  NEUCLIR22_FA_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-fa.ht-title.txt"),
  NEUCLIR22_FA_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-fa.ht-desc.txt"),
  NEUCLIR22_FA_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-fa.ht-desc_title.txt"),
  NEUCLIR22_FA_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-fa.mt-title.txt"),
  NEUCLIR22_FA_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-fa.mt-desc.txt"),
  NEUCLIR22_FA_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-fa.mt-desc_title.txt"),
  NEUCLIR22_RU_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-ru.ht-title.txt"),
  NEUCLIR22_RU_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-ru.ht-desc.txt"),
  NEUCLIR22_RU_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-ru.ht-desc_title.txt"),
  NEUCLIR22_RU_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-ru.mt-title.txt"),
  NEUCLIR22_RU_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-ru.mt-desc.txt"),
  NEUCLIR22_RU_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-ru.mt-desc_title.txt"),
  NEUCLIR22_ZH_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-zh.ht-title.txt"),
  NEUCLIR22_ZH_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-zh.ht-desc.txt"),
  NEUCLIR22_ZH_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-zh.ht-desc_title.txt"),
  NEUCLIR22_ZH_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-zh.mt-title.txt"),
  NEUCLIR22_ZH_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-zh.mt-desc.txt"),
  NEUCLIR22_ZH_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-zh.mt-desc_title.txt"),

  // TREC NeuCLIR 2022 Topics, SPLADE
  NEUCLIR22_FA_SPLADE_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-fa.splade.ht-title.txt.gz"),
  NEUCLIR22_FA_SPLADE_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-fa.splade.ht-desc.txt.gz"),
  NEUCLIR22_FA_SPLADE_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-fa.splade.ht-desc_title.txt.gz"),
  NEUCLIR22_FA_SPLADE_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-fa.splade.mt-title.txt.gz"),
  NEUCLIR22_FA_SPLADE_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-fa.splade.mt-desc.txt.gz"),
  NEUCLIR22_FA_SPLADE_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-fa.splade.mt-desc_title.txt.gz"),
  NEUCLIR22_RU_SPLADE_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-ru.splade.ht-title.txt.gz"),
  NEUCLIR22_RU_SPLADE_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-ru.splade.ht-desc.txt.gz"),
  NEUCLIR22_RU_SPLADE_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-ru.splade.ht-desc_title.txt.gz"),
  NEUCLIR22_RU_SPLADE_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-ru.splade.mt-title.txt.gz"),
  NEUCLIR22_RU_SPLADE_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-ru.splade.mt-desc.txt.gz"),
  NEUCLIR22_RU_SPLADE_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-ru.splade.mt-desc_title.txt.gz"),
  NEUCLIR22_ZH_SPLADE_HT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-zh.splade.ht-title.txt.gz"),
  NEUCLIR22_ZH_SPLADE_HT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-zh.splade.ht-desc.txt.gz"),
  NEUCLIR22_ZH_SPLADE_HT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-zh.splade.ht-desc_title.txt.gz"),
  NEUCLIR22_ZH_SPLADE_MT_TITLE(TsvIntTopicReader.class,      "topics.neuclir22-zh.splade.mt-title.txt.gz"),
  NEUCLIR22_ZH_SPLADE_MT_DESC(TsvIntTopicReader.class,       "topics.neuclir22-zh.splade.mt-desc.txt.gz"),
  NEUCLIR22_ZH_SPLADE_MT_DESC_TITLE(TsvIntTopicReader.class, "topics.neuclir22-zh.splade.mt-desc_title.txt.gz"),

  // MIRACL (v1.0.0): original queries
  MIRACL_V10_AR_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-ar-dev.tsv"),
  MIRACL_V10_BN_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-bn-dev.tsv"),
  MIRACL_V10_EN_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-en-dev.tsv"),
  MIRACL_V10_ES_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-es-dev.tsv"),
  MIRACL_V10_FA_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-fa-dev.tsv"),
  MIRACL_V10_FI_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-fi-dev.tsv"),
  MIRACL_V10_FR_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-fr-dev.tsv"),
  MIRACL_V10_HI_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-hi-dev.tsv"),
  MIRACL_V10_ID_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-id-dev.tsv"),
  MIRACL_V10_JA_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-ja-dev.tsv"),
  MIRACL_V10_KO_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-ko-dev.tsv"),
  MIRACL_V10_RU_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-ru-dev.tsv"),
  MIRACL_V10_SW_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-sw-dev.tsv"),
  MIRACL_V10_TE_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-te-dev.tsv"),
  MIRACL_V10_TH_DEV(TsvIntTopicReader.class, "topics.miracl-v1.0-th-dev.tsv"),
  MIRACL_V10_ZH_DEV(TsvStringTopicReader.class,"topics.miracl-v1.0-zh-dev.tsv"),
  MIRACL_V10_DE_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-de-dev.tsv"),
  MIRACL_V10_YO_DEV(TsvStringTopicReader.class, "topics.miracl-v1.0-yo-dev.tsv"),

  // AToMiC topics
  ATOMIC_V021_VIT_L_14_LAION2B_S32B_B82K_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.ViT-L-14.laion2b_s32b_b82k.jsonl"),
  ATOMIC_V021_VIT_L_14_LAION2B_S32B_B82K_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.ViT-L-14.laion2b_s32b_b82k.jsonl"),
  ATOMIC_V021_VIT_B_32_LAION2B_E16_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.ViT-B-32.laion2b_e16.jsonl"),
  ATOMIC_V021_VIT_B_32_LAION2B_E16_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.ViT-B-32.laion2b_e16.jsonl"),
  ATOMIC_V021_VIT_BIGG_14_LAION2B_S39B_B160K_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.ViT-bigG-14.laion2b_s39b_b160k.jsonl"),
  ATOMIC_V021_VIT_BIGG_14_LAION2B_S39B_B160K_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.ViT-bigG-14.laion2b_s39b_b160k.jsonl"),
  ATOMIC_V021_VIT_H_14_LAION2B_S32B_B79K_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.ViT-H-14.laion2b_s32b_b79k.jsonl"),
  ATOMIC_V021_VIT_H_14_LAION2B_S32B_B79K_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.ViT-H-14.laion2b_s32b_b79k.jsonl"),
  ATOMIC_V021_VIT_B_32_LAION400M_E32_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.ViT-B-32.laion400m_e32.jsonl"),
  ATOMIC_V021_VIT_B_32_LAION400M_E32_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.ViT-B-32.laion400m_e32.jsonl"),
  ATOMIC_V021_SALESFORCE_BLIP_ITM_LARGE_COCO_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.Salesforce.blip-itm-large-coco.jsonl"),
  ATOMIC_V021_SALESFORCE_BLIP_ITM_LARGE_COCO_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.Salesforce.blip-itm-large-coco.jsonl"),
  ATOMIC_V021_SALESFORCE_BLIP_ITM_BASE_COCO_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.Salesforce.blip-itm-base-coco.jsonl"),
  ATOMIC_V021_SALESFORCE_BLIP_ITM_BASE_COCO_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.Salesforce.blip-itm-base-coco.jsonl"),
  ATOMIC_V021_OPENAI_CLIP_VIT_BASE_PATCH32_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.openai.clip-vit-base-patch32.jsonl"),
  ATOMIC_V021_OPENAI_CLIP_VIT_BASE_PATCH32_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.openai.clip-vit-base-patch32.jsonl"),
  ATOMIC_V021_OPENAI_CLIP_VIT_LARGE_PATCH14_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.openai.clip-vit-large-patch14.jsonl"),
  ATOMIC_V021_OPENAI_CLIP_VIT_LARGE_PATCH14_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.openai.clip-vit-large-patch14.jsonl"),
  ATOMIC_V021_FACEBOOK_FLAVA_FULL_TEXT_VAL(JsonStringTopicReader.class, "topics.atomic.validation.text.facebook.flava-full.jsonl"),
  ATOMIC_V021_FACEBOOK_FLAVA_FULL_IMAGE_VAL(JsonStringTopicReader.class, "topics.atomic.validation.image.facebook.flava-full.jsonl"),

  // CIRAL Queries
  CIRAL_V10_HA_TEST_A(TsvIntTopicReader.class, "topics.ciral-v1.0-ha-test-a.tsv"),
  CIRAL_V10_SO_TEST_A(TsvIntTopicReader.class, "topics.ciral-v1.0-so-test-a.tsv"),
  CIRAL_V10_SW_TEST_A(TsvIntTopicReader.class, "topics.ciral-v1.0-sw-test-a.tsv"),
  CIRAL_V10_YO_TEST_A(TsvIntTopicReader.class, "topics.ciral-v1.0-yo-test-a.tsv"),
  CIRAL_V10_HA_TEST_B(TsvIntTopicReader.class, "topics.ciral-v1.0-ha-test-b.tsv"),
  CIRAL_V10_SO_TEST_B(TsvIntTopicReader.class, "topics.ciral-v1.0-so-test-b.tsv"),
  CIRAL_V10_SW_TEST_B(TsvIntTopicReader.class, "topics.ciral-v1.0-sw-test-b.tsv"),
  CIRAL_V10_YO_TEST_B(TsvIntTopicReader.class, "topics.ciral-v1.0-yo-test-b.tsv"),
  CIRAL_V10_HA_TEST_A_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-ha-test-a-native.tsv"),
  CIRAL_V10_SO_TEST_A_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-so-test-a-native.tsv"),
  CIRAL_V10_SW_TEST_A_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-sw-test-a-native.tsv"),
  CIRAL_V10_YO_TEST_A_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-yo-test-a-native.tsv"),
  CIRAL_V10_HA_TEST_B_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-ha-test-b-native.tsv"),
  CIRAL_V10_SO_TEST_B_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-so-test-b-native.tsv"),
  CIRAL_V10_SW_TEST_B_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-sw-test-b-native.tsv"),
  CIRAL_V10_YO_TEST_B_NATIVE(TsvIntTopicReader.class, "topics.ciral-v1.0-yo-test-b-native.tsv"),
  CIRAL_V10_HA_DEV_MONO(TsvIntTopicReader.class, "topics.ciral-v1.0-ha-dev-native.tsv"),
  CIRAL_V10_SO_DEV_MONO(TsvIntTopicReader.class, "topics.ciral-v1.0-so-dev-native.tsv"),
  CIRAL_V10_SW_DEV_MONO(TsvIntTopicReader.class, "topics.ciral-v1.0-sw-dev-native.tsv"),
  CIRAL_V10_YO_DEV_MONO(TsvIntTopicReader.class, "topics.ciral-v1.0-yo-dev-native.tsv"),

  // unused topics
  CACM(CacmTopicReader.class,                   "topics.cacm.txt"),
  NTCIR_EN_1(NtcirTopicReader.class,            "topics.www1.english.txt"),
  NTCIR_EN_2(NtcirTopicReader.class,            "topics.www2.english.txt"),
  TERABYTE_05_EFFICIENCY(WebTopicReader.class,  "topics.terabyte05.efficiency.txt"),
  NTCIR_8_EN_EVAL(TsvStringTopicReader.class,   "topics.ntcir8en.eval.txt");

  public final String path;
  public final Class<? extends TopicReader> readerClass;

  Topics(Class<? extends TopicReader> c, String path) {
    this.readerClass = c;
    this.path = path;
  }

  private static Map<String, Topics> SYMBOL_DICTIONARY = generateSymbolDictionary();

  private static Map<String, Topics> generateSymbolDictionary() {
    Map<String, Topics> m = new HashMap<>();
    for (Topics t : Topics.values()) {
      String sym = t.path.replaceFirst("^topics\\.", "");
      sym = sym.replaceFirst("(\\.tsv|\\.txt|\\.txt\\.gz|\\.jsonl|\\.jsonl\\.gz|\\.tsv\\.gz)$", "");
      m.put(sym, t);
    }

    // Additional aliases
    m.put("msmarco-passage-dev", MSMARCO_PASSAGE_DEV_SUBSET);
    m.put("msmarco-passage-dev-splade-pp-ed", MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED);
    m.put("msmarco-passage-dev-cos-dpr-distil", MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL);
    m.put("msmarco-passage-dev-bge-base-en-v1.5", MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15);
    m.put("msmarco-passage-dev-cohere-embed-english-v3.0", MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30);

    m.put("msmarco-passage.dev", MSMARCO_PASSAGE_DEV_SUBSET);
    m.put("msmarco-passage.dev.splade-pp-ed", MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED);
    m.put("msmarco-passage.dev.cos-dpr-distil", MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL);
    m.put("msmarco-passage.dev.bge-base-en-v1.5", MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15);
    m.put("msmarco-passage.dev.cohere-embed-english-v3.0", MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30);

    m.put("msmarco-v1-passage-dev", MSMARCO_PASSAGE_DEV_SUBSET);
    m.put("msmarco-v1-passage-dev-splade-pp-ed", MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED);
    m.put("msmarco-v1-passage-dev-cos-dpr-distil", MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL);
    m.put("msmarco-v1-passage-dev-bge-base-en-v1.5", MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15);
    m.put("msmarco-v1-passage-dev-cohere-embed-english-v3.0", MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30);

    m.put("msmarco-v1-passage.dev", MSMARCO_PASSAGE_DEV_SUBSET);
    m.put("msmarco-v1-passage.dev.splade-pp-ed", MSMARCO_PASSAGE_DEV_SUBSET_SPLADE_PP_ED);
    m.put("msmarco-v1-passage.dev.cos-dpr-distil", MSMARCO_PASSAGE_DEV_SUBSET_COS_DPR_DISTIL);
    m.put("msmarco-v1-passage.dev.bge-base-en-v1.5", MSMARCO_PASSAGE_DEV_SUBSET_BGE_BASE_EN_15);
    m.put("msmarco-v1-passage.dev.cohere-embed-english-v3.0", MSMARCO_PASSAGE_DEV_SUBSET_COHERE_EMBED_ENGLISH_30);

    m.put("dl20-passage", TREC2020_DL);
    m.put("dl20-doc", TREC2020_DL);

    m.put("dl20-passage.splade-pp-ed", TREC2020_DL_SPLADE_PP_ED);
    m.put("dl20-passage.cos-dpr-distil", TREC2020_DL_COS_DPR_DISTIL);
    m.put("dl20-passage.bge-base-en-v1.5", TREC2020_DL_BGE_BASE_EN_15);
    m.put("dl20-passage.cohere-embed-english-v3.0", TREC2020_DL_COHERE_EMBED_ENGLISH_30);

    m.put("dl21-passage", TREC2021_DL);
    m.put("dl21-doc", TREC2021_DL);
    m.put("dl22-passage", TREC2022_DL);
    m.put("dl22-doc", TREC2022_DL);
    m.put("dl23-passage", TREC2023_DL);
    m.put("dl23-doc", TREC2023_DL);

    m.put("beir-trec-covid", BEIR_V1_0_0_TREC_COVID_TEST);
    m.put("beir-bioasq", BEIR_V1_0_0_BIOASQ_TEST);
    m.put("beir-nfcorpus", BEIR_V1_0_0_NFCORPUS_TEST);
    m.put("beir-nq", BEIR_V1_0_0_NQ_TEST);
    m.put("beir-hotpotqa", BEIR_V1_0_0_HOTPOTQA_TEST);
    m.put("beir-fiqa", BEIR_V1_0_0_FIQA_TEST);
    m.put("beir-signal1m", BEIR_V1_0_0_SIGNAL1M_TEST);
    m.put("beir-trec-news", BEIR_V1_0_0_TREC_NEWS_TEST);
    m.put("beir-robust04", BEIR_V1_0_0_ROBUST04_TEST);
    m.put("beir-arguana", BEIR_V1_0_0_ARGUANA_TEST);
    m.put("beir-webis-touche2020", BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST);
    m.put("beir-cqadupstack-android", BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST);
    m.put("beir-cqadupstack-english", BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST);
    m.put("beir-cqadupstack-gaming", BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST);
    m.put("beir-cqadupstack-gis", BEIR_V1_0_0_CQADUPSTACK_GIS_TEST);
    m.put("beir-cqadupstack-mathematica", BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST);
    m.put("beir-cqadupstack-physics", BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST);
    m.put("beir-cqadupstack-programmers", BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST);
    m.put("beir-cqadupstack-stats", BEIR_V1_0_0_CQADUPSTACK_STATS_TEST);
    m.put("beir-cqadupstack-tex", BEIR_V1_0_0_CQADUPSTACK_TEX_TEST);
    m.put("beir-cqadupstack-unix", BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST);
    m.put("beir-cqadupstack-webmasters", BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST);
    m.put("beir-cqadupstack-wordpress", BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST);
    m.put("beir-quora", BEIR_V1_0_0_QUORA_TEST);
    m.put("beir-dbpedia-entity", BEIR_V1_0_0_DBPEDIA_ENTITY_TEST);
    m.put("beir-scidocs", BEIR_V1_0_0_SCIDOCS_TEST);
    m.put("beir-fever", BEIR_V1_0_0_FEVER_TEST);
    m.put("beir-climate-fever", BEIR_V1_0_0_CLIMATE_FEVER_TEST);
    m.put("beir-scifact", BEIR_V1_0_0_SCIFACT_TEST);

    m.put("beir-trec-covid-splade-pp-ed", BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_PP_ED);
    m.put("beir-bioasq-splade-pp-ed", BEIR_V1_0_0_BIOASQ_TEST_SPLADE_PP_ED);
    m.put("beir-nfcorpus-splade-pp-ed", BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_PP_ED);
    m.put("beir-nq-splade-pp-ed", BEIR_V1_0_0_NQ_TEST_SPLADE_PP_ED);
    m.put("beir-hotpotqa-splade-pp-ed", BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_PP_ED);
    m.put("beir-fiqa-splade-pp-ed", BEIR_V1_0_0_FIQA_TEST_SPLADE_PP_ED);
    m.put("beir-signal1m-splade-pp-ed", BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_PP_ED);
    m.put("beir-trec-news-splade-pp-ed", BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_PP_ED);
    m.put("beir-robust04-splade-pp-ed", BEIR_V1_0_0_ROBUST04_TEST_SPLADE_PP_ED);
    m.put("beir-arguana-splade-pp-ed", BEIR_V1_0_0_ARGUANA_TEST_SPLADE_PP_ED);
    m.put("beir-webis-touche2020-splade-pp-ed", BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-android-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-english-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-gaming-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-gis-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-mathematica-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-physics-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-programmers-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-stats-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-tex-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-unix-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-webmasters-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-wordpress-splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_PP_ED);
    m.put("beir-quora-splade-pp-ed", BEIR_V1_0_0_QUORA_TEST_SPLADE_PP_ED);
    m.put("beir-dbpedia-entity-splade-pp-ed", BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_PP_ED);
    m.put("beir-scidocs-splade-pp-ed", BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_PP_ED);
    m.put("beir-fever-splade-pp-ed", BEIR_V1_0_0_FEVER_TEST_SPLADE_PP_ED);
    m.put("beir-climate-fever-splade-pp-ed", BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_PP_ED);
    m.put("beir-scifact-splade-pp-ed", BEIR_V1_0_0_SCIFACT_TEST_SPLADE_PP_ED);

    m.put("beir-trec-covid.splade-pp-ed", BEIR_V1_0_0_TREC_COVID_TEST_SPLADE_PP_ED);
    m.put("beir-bioasq.splade-pp-ed", BEIR_V1_0_0_BIOASQ_TEST_SPLADE_PP_ED);
    m.put("beir-nfcorpus.splade-pp-ed", BEIR_V1_0_0_NFCORPUS_TEST_SPLADE_PP_ED);
    m.put("beir-nq.splade-pp-ed", BEIR_V1_0_0_NQ_TEST_SPLADE_PP_ED);
    m.put("beir-hotpotqa.splade-pp-ed", BEIR_V1_0_0_HOTPOTQA_TEST_SPLADE_PP_ED);
    m.put("beir-fiqa.splade-pp-ed", BEIR_V1_0_0_FIQA_TEST_SPLADE_PP_ED);
    m.put("beir-signal1m.splade-pp-ed", BEIR_V1_0_0_SIGNAL1M_TEST_SPLADE_PP_ED);
    m.put("beir-trec-news.splade-pp-ed", BEIR_V1_0_0_TREC_NEWS_TEST_SPLADE_PP_ED);
    m.put("beir-robust04.splade-pp-ed", BEIR_V1_0_0_ROBUST04_TEST_SPLADE_PP_ED);
    m.put("beir-arguana.splade-pp-ed", BEIR_V1_0_0_ARGUANA_TEST_SPLADE_PP_ED);
    m.put("beir-webis-touche2020.splade-pp-ed", BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-android.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-english.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-gaming.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-gis.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-mathematica.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-physics.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-programmers.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-stats.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-tex.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-unix.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-webmasters.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_SPLADE_PP_ED);
    m.put("beir-cqadupstack-wordpress.splade-pp-ed", BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_SPLADE_PP_ED);
    m.put("beir-quora.splade-pp-ed", BEIR_V1_0_0_QUORA_TEST_SPLADE_PP_ED);
    m.put("beir-dbpedia-entity.splade-pp-ed", BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_SPLADE_PP_ED);
    m.put("beir-scidocs.splade-pp-ed", BEIR_V1_0_0_SCIDOCS_TEST_SPLADE_PP_ED);
    m.put("beir-fever.splade-pp-ed", BEIR_V1_0_0_FEVER_TEST_SPLADE_PP_ED);
    m.put("beir-climate-fever.splade-pp-ed", BEIR_V1_0_0_CLIMATE_FEVER_TEST_SPLADE_PP_ED);
    m.put("beir-scifact.splade-pp-ed", BEIR_V1_0_0_SCIFACT_TEST_SPLADE_PP_ED);

    m.put("beir-trec-covid-bge-base-en-v1.5", BEIR_V1_0_0_TREC_COVID_TEST_BGE_BASE_EN_15);
    m.put("beir-bioasq-bge-base-en-v1.5", BEIR_V1_0_0_BIOASQ_TEST_BGE_BASE_EN_15);
    m.put("beir-nfcorpus-bge-base-en-v1.5", BEIR_V1_0_0_NFCORPUS_TEST_BGE_BASE_EN_15);
    m.put("beir-nq-bge-base-en-v1.5", BEIR_V1_0_0_NQ_TEST_BGE_BASE_EN_15);
    m.put("beir-hotpotqa-bge-base-en-v1.5", BEIR_V1_0_0_HOTPOTQA_TEST_BGE_BASE_EN_15);
    m.put("beir-fiqa-bge-base-en-v1.5", BEIR_V1_0_0_FIQA_TEST_BGE_BASE_EN_15);
    m.put("beir-signal1m-bge-base-en-v1.5", BEIR_V1_0_0_SIGNAL1M_TEST_BGE_BASE_EN_15);
    m.put("beir-trec-news-bge-base-en-v1.5", BEIR_V1_0_0_TREC_NEWS_TEST_BGE_BASE_EN_15);
    m.put("beir-robust04-bge-base-en-v1.5", BEIR_V1_0_0_ROBUST04_TEST_BGE_BASE_EN_15);
    m.put("beir-arguana-bge-base-en-v1.5", BEIR_V1_0_0_ARGUANA_TEST_BGE_BASE_EN_15);
    m.put("beir-webis-touche2020-bge-base-en-v1.5", BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-android-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-english-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-gaming-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-gis-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-mathematica-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-physics-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-programmers-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-stats-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-tex-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-unix-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-webmasters-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-wordpress-bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_BGE_BASE_EN_15);
    m.put("beir-quora-bge-base-en-v1.5", BEIR_V1_0_0_QUORA_TEST_BGE_BASE_EN_15);
    m.put("beir-dbpedia-entity-bge-base-en-v1.5", BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_BGE_BASE_EN_15);
    m.put("beir-scidocs-bge-base-en-v1.5", BEIR_V1_0_0_SCIDOCS_TEST_BGE_BASE_EN_15);
    m.put("beir-fever-bge-base-en-v1.5", BEIR_V1_0_0_FEVER_TEST_BGE_BASE_EN_15);
    m.put("beir-climate-fever-bge-base-en-v1.5", BEIR_V1_0_0_CLIMATE_FEVER_TEST_BGE_BASE_EN_15);
    m.put("beir-scifact-bge-base-en-v1.5", BEIR_V1_0_0_SCIFACT_TEST_BGE_BASE_EN_15);

    m.put("beir-trec-covid.bge-base-en-v1.5", BEIR_V1_0_0_TREC_COVID_TEST_BGE_BASE_EN_15);
    m.put("beir-bioasq.bge-base-en-v1.5", BEIR_V1_0_0_BIOASQ_TEST_BGE_BASE_EN_15);
    m.put("beir-nfcorpus.bge-base-en-v1.5", BEIR_V1_0_0_NFCORPUS_TEST_BGE_BASE_EN_15);
    m.put("beir-nq.bge-base-en-v1.5", BEIR_V1_0_0_NQ_TEST_BGE_BASE_EN_15);
    m.put("beir-hotpotqa.bge-base-en-v1.5", BEIR_V1_0_0_HOTPOTQA_TEST_BGE_BASE_EN_15);
    m.put("beir-fiqa.bge-base-en-v1.5", BEIR_V1_0_0_FIQA_TEST_BGE_BASE_EN_15);
    m.put("beir-signal1m.bge-base-en-v1.5", BEIR_V1_0_0_SIGNAL1M_TEST_BGE_BASE_EN_15);
    m.put("beir-trec-news.bge-base-en-v1.5", BEIR_V1_0_0_TREC_NEWS_TEST_BGE_BASE_EN_15);
    m.put("beir-robust04.bge-base-en-v1.5", BEIR_V1_0_0_ROBUST04_TEST_BGE_BASE_EN_15);
    m.put("beir-arguana.bge-base-en-v1.5", BEIR_V1_0_0_ARGUANA_TEST_BGE_BASE_EN_15);
    m.put("beir-webis-touche2020.bge-base-en-v1.5", BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-android.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-english.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-gaming.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-gis.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_GIS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-mathematica.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-physics.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-programmers.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-stats.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_STATS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-tex.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_TEX_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-unix.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-webmasters.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST_BGE_BASE_EN_15);
    m.put("beir-cqadupstack-wordpress.bge-base-en-v1.5", BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST_BGE_BASE_EN_15);
    m.put("beir-quora.bge-base-en-v1.5", BEIR_V1_0_0_QUORA_TEST_BGE_BASE_EN_15);
    m.put("beir-dbpedia-entity.bge-base-en-v1.5", BEIR_V1_0_0_DBPEDIA_ENTITY_TEST_BGE_BASE_EN_15);
    m.put("beir-scidocs.bge-base-en-v1.5", BEIR_V1_0_0_SCIDOCS_TEST_BGE_BASE_EN_15);
    m.put("beir-fever.bge-base-en-v1.5", BEIR_V1_0_0_FEVER_TEST_BGE_BASE_EN_15);
    m.put("beir-climate-fever.bge-base-en-v1.5", BEIR_V1_0_0_CLIMATE_FEVER_TEST_BGE_BASE_EN_15);
    m.put("beir-scifact.bge-base-en-v1.5", BEIR_V1_0_0_SCIFACT_TEST_BGE_BASE_EN_15);

    return m;
  }

  static public Topics getByName(String name) {
    try {
      return Topics.valueOf(name);
    } catch (IllegalArgumentException e) {
      if (SYMBOL_DICTIONARY.containsKey(name)) {
        return SYMBOL_DICTIONARY.get(name);
      }

      return null;
    }
  }
}
