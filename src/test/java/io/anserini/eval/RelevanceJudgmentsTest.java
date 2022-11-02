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

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RelevanceJudgmentsTest {

  public int getQrelsCount(RelevanceJudgments qrels) {
    int count = 0;
    for (String qid : qrels.getQids()) {
      count += qrels.getDocMap(qid).size();
    }
    return count;
  }

  @Test
  public void testRobust04() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.robust04.txt");
    assertNotNull(qrels);
    assertEquals(249, qrels.getQids().size());
    assertEquals(311410, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("301", "FBIS3-10082"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137"));

    qrels = RelevanceJudgments.fromQrels(Qrels.ROBUST04);
    assertNotNull(qrels);
    assertEquals(249, qrels.getQids().size());
    assertEquals(311410, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("301", "FBIS3-10082"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137"));
  }

  @Test
  public void testRobust05() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.robust05.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(37798, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("303", "APW19980609.1531"));
    assertEquals(0, qrels.getRelevanceGrade("689", "XIE20000925.0055"));

    qrels = RelevanceJudgments.fromQrels(Qrels.ROBUST05);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(37798, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("303", "APW19980609.1531"));
    assertEquals(0, qrels.getRelevanceGrade("689", "XIE20000925.0055"));
  }

  @Test
  public void testTrec19DLDoc() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl19-doc.txt");
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(16258, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "D1035833"));
    assertEquals(0, qrels.getRelevanceGrade("1133167", "D984590"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2019_DL_DOC);
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(16258, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "D1035833"));
    assertEquals(0, qrels.getRelevanceGrade("1133167", "D984590"));
  }

  @Test
  public void testTrec19DLPassage() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl19-passage.txt");
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(9260, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "1017759"));
    assertEquals(1, qrels.getRelevanceGrade("1133167", "8804478"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2019_DL_PASSAGE);
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(9260, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "1017759"));
    assertEquals(1, qrels.getRelevanceGrade("1133167", "8804478"));
  }

  @Test
  public void testTrec20DLDoc() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl20-doc.txt");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(9098, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("42255", "D1884223"));
    assertEquals(3, qrels.getRelevanceGrade("1136962", "D96741"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2020_DL_DOC);
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(9098, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("42255", "D1884223"));
    assertEquals(3, qrels.getRelevanceGrade("1136962", "D96741"));
  }

  @Test
  public void testTrec20DLPassage() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl20-passage.txt");
    assertNotNull(qrels);
    assertEquals(54, qrels.getQids().size());
    assertEquals(11386, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("23849", "1020327"));
    assertEquals(1, qrels.getRelevanceGrade("1136962", "937258"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2020_DL_PASSAGE);
    assertNotNull(qrels);
    assertEquals(54, qrels.getQids().size());
    assertEquals(11386, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("23849", "1020327"));
    assertEquals(1, qrels.getRelevanceGrade("1136962", "937258"));
  }

  @Test
  public void testTrec21DLDoc() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl21-doc.txt");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(13058, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_doc_01_1320020407"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_doc_59_863449044"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2021_DL_DOC);
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(13058, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_doc_01_1320020407"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_doc_59_863449044"));
  }

  @Test
  public void testTrec21DLPassage() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl21-passage.txt");
    assertNotNull(qrels);
    assertEquals(53, qrels.getQids().size());
    assertEquals(10828, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("2082", "msmarco_passage_02_179207466"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_passage_67_937656589"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2021_DL_PASSAGE);
    assertNotNull(qrels);
    assertEquals(53, qrels.getQids().size());
    assertEquals(10828, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("2082", "msmarco_passage_02_179207466"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_passage_67_937656589"));
  }

  @Test
  public void testMsmarcoDocDev() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt");
    assertNotNull(qrels);
    assertEquals(5193, qrels.getQids().size());
    assertEquals(5193, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2", "D1650436"));
    assertEquals(1, qrels.getRelevanceGrade("1102400", "D677570"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_DOC_DEV);
    assertNotNull(qrels);
    assertEquals(5193, qrels.getQids().size());
    assertEquals(5193, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2", "D1650436"));
    assertEquals(1, qrels.getRelevanceGrade("1102400", "D677570"));
  }

  @Test
  public void testMsmarcoPassageDevSubset() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-passage.dev-subset.txt");
    assertNotNull(qrels);
    assertEquals(6980, qrels.getQids().size());
    assertEquals(7437, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("300674", "7067032"));
    assertEquals(1, qrels.getRelevanceGrade("195199", "8009377"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_PASSAGE_DEV_SUBSET);
    assertNotNull(qrels);
    assertEquals(6980, qrels.getQids().size());
    assertEquals(7437, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("300674", "7067032"));
    assertEquals(1, qrels.getRelevanceGrade("195199", "8009377"));
  }

  @Test
  public void testMsmarcoV2DocDev() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev.txt");
    assertNotNull(qrels);
    assertEquals(4552, qrels.getQids().size());
    assertEquals(4702, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_doc_17_2560009121"));
    assertEquals(1, qrels.getRelevanceGrade("999942", "msmarco_doc_06_956348348"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_V2_DOC_DEV);
    assertNotNull(qrels);
    assertEquals(4552, qrels.getQids().size());
    assertEquals(4702, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_doc_17_2560009121"));
    assertEquals(1, qrels.getRelevanceGrade("999942", "msmarco_doc_06_956348348"));
  }

  @Test
  public void testMsmarcoV2DocDev2() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-v2-doc.dev2.txt");
    assertNotNull(qrels);
    assertEquals(5000, qrels.getQids().size());
    assertEquals(5178, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_doc_08_73026062"));
    assertEquals(1, qrels.getRelevanceGrade("999937", "msmarco_doc_05_319743607"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_V2_DOC_DEV2);
    assertNotNull(qrels);
    assertEquals(5000, qrels.getQids().size());
    assertEquals(5178, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_doc_08_73026062"));
    assertEquals(1, qrels.getRelevanceGrade("999937", "msmarco_doc_05_319743607"));
  }

  @Test
  public void testMsmarcoV2DocPassage() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev.txt");
    assertNotNull(qrels);
    assertEquals(3903, qrels.getQids().size());
    assertEquals(4009, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("763878", "msmarco_passage_33_459057644"));
    assertEquals(1, qrels.getRelevanceGrade("1091692", "msmarco_passage_23_330102695"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_V2_PASSAGE_DEV);
    assertNotNull(qrels);
    assertEquals(3903, qrels.getQids().size());
    assertEquals(4009, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("763878", "msmarco_passage_33_459057644"));
    assertEquals(1, qrels.getRelevanceGrade("1091692", "msmarco_passage_23_330102695"));
  }

  @Test
  public void testMsmarcoV2DocPassage2() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-v2-passage.dev2.txt");
    assertNotNull(qrels);
    assertEquals(4281, qrels.getQids().size());
    assertEquals(4411, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("419507", "msmarco_passage_04_254301507"));
    assertEquals(1, qrels.getRelevanceGrade("961297", "msmarco_passage_18_858458289"));

    qrels = RelevanceJudgments.fromQrels(Qrels.MSMARCO_V2_PASSAGE_DEV2);
    assertNotNull(qrels);
    assertEquals(4281, qrels.getQids().size());
    assertEquals(4411, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("419507", "msmarco_passage_04_254301507"));
    assertEquals(1, qrels.getRelevanceGrade("961297", "msmarco_passage_18_858458289"));
  }

  @Test
  public void testCore17() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.core17.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(30030, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("307", "1001536"));
    assertEquals(0, qrels.getRelevanceGrade("690", "996059"));

    qrels = RelevanceJudgments.fromQrels(Qrels.CORE17);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(30030, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("307", "1001536"));
    assertEquals(0, qrels.getRelevanceGrade("690", "996059"));
  }

  @Test
  public void testCore18() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.core18.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(26233, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("321", "004c6120d0aa69da29cc045da0562168"));
    assertEquals(0, qrels.getRelevanceGrade("825", "ff3a25b0-0ba4-11e4-8341-b8072b1e7348"));

    qrels = RelevanceJudgments.fromQrels(Qrels.CORE18);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(26233, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("321", "004c6120d0aa69da29cc045da0562168"));
    assertEquals(0, qrels.getRelevanceGrade("825", "ff3a25b0-0ba4-11e4-8341-b8072b1e7348"));
  }

  @Test
  public void testCar15() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.car17v1.5.benchmarkY1test.txt");
    assertNotNull(qrels);
    assertEquals(2125, qrels.getQids().size());
    assertEquals(5820, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex",
        "38c1bd25ddca2705164677a3f598c46df85afba7"));
    assertEquals(1, qrels.getRelevanceGrade("Yellowstone%20National%20Park/Recreation",
        "e80b5185da1493edde41bea19a389a3f62167369"));

    qrels = RelevanceJudgments.fromQrels(Qrels.CAR17V15_BENCHMARK_Y1_TEST);
    assertNotNull(qrels);
    assertEquals(2125, qrels.getQids().size());
    assertEquals(5820, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex",
            "38c1bd25ddca2705164677a3f598c46df85afba7"));
    assertEquals(1, qrels.getRelevanceGrade("Yellowstone%20National%20Park/Recreation",
            "e80b5185da1493edde41bea19a389a3f62167369"));
  }

  @Test
  public void testCar20() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.car17v2.0.benchmarkY1test.txt");
    assertNotNull(qrels);
    assertEquals(2254, qrels.getQids().size());
    assertEquals(6192, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Aftertaste", "327cca6c4d38953196fa6789f615546f03287b25"));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Yellowstone%20National%20Park/Recreation",
        "b812fca195f74f8c563db4262260554fe3ff3731"));

    qrels = RelevanceJudgments.fromQrels(Qrels.CAR17V20_BENCHMARK_Y1_TEST);
    assertNotNull(qrels);
    assertEquals(2254, qrels.getQids().size());
    assertEquals(6192, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Aftertaste", "327cca6c4d38953196fa6789f615546f03287b25"));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Yellowstone%20National%20Park/Recreation",
            "b812fca195f74f8c563db4262260554fe3ff3731"));
  }

  @Test
  public void testTrec2018BL() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(8508, getQrelsCount(qrels));
    assertEquals(16, qrels.getRelevanceGrade("321", "00f57310e5c8ec7833d6756ba637332e"));
    assertEquals(0, qrels.getRelevanceGrade("825", "f66b624ba8689d704872fa776fb52860"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2018_BL);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(8508, getQrelsCount(qrels));
    assertEquals(16, qrels.getRelevanceGrade("321", "00f57310e5c8ec7833d6756ba637332e"));
    assertEquals(0, qrels.getRelevanceGrade("825", "f66b624ba8689d704872fa776fb52860"));
  }

  @Test
  public void testTrec2019BL() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.backgroundlinking19.txt");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(15655, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("826", "0154349511cd8c49ab862d6cb0d8f6a8"));
    assertEquals(0, qrels.getRelevanceGrade("885", "fde80cb0-b4f0-11e2-bbf2-a6f9e9d79e19"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2019_BL);
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(15655, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("826", "0154349511cd8c49ab862d6cb0d8f6a8"));
    assertEquals(0, qrels.getRelevanceGrade("885", "fde80cb0-b4f0-11e2-bbf2-a6f9e9d79e19"));
  }

  @Test
  public void testTrec2020BL() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.backgroundlinking20.txt");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17764, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("886", "00183d98-741b-11e5-8248-98e0f5a2e830"));
    assertEquals(0, qrels.getRelevanceGrade("935", "ff0a760128ecdbcc096cafc8cd553255"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2020_BL);
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17764, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("886", "00183d98-741b-11e5-8248-98e0f5a2e830"));
    assertEquals(0, qrels.getRelevanceGrade("935", "ff0a760128ecdbcc096cafc8cd553255"));
  }

  @Test
  public void testCovidRound1() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round1.txt");
    assertNotNull(qrels);
    assertEquals(30, qrels.getQids().size());
    assertEquals(8691, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("30", "zn87f1lk"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND1);
    assertNotNull(qrels);
    assertEquals(30, qrels.getQids().size());
    assertEquals(8691, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("30", "zn87f1lk"));
  }

  @Test
  public void testCovidRound2() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round2.txt");
    assertNotNull(qrels);
    assertEquals(35, qrels.getQids().size());
    assertEquals(12037, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("1", "08efpohc"));
    assertEquals(0, qrels.getRelevanceGrade("35", "zzmfhr2s"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND2);
    assertNotNull(qrels);
    assertEquals(35, qrels.getQids().size());
    assertEquals(12037, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("1", "08efpohc"));
    assertEquals(0, qrels.getRelevanceGrade("35", "zzmfhr2s"));
  }

  @Test
  public void testCovidRound3() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round3.txt");
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(12713, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "0194oljo"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND3);
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(12713, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "0194oljo"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round4.txt");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(13262, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "1c47w4q5"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND4);
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(13262, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "1c47w4q5"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidRound5() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round5.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(23151, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND5);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(23151, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testCovidRound3Cumulative() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round3-cumulative.txt");
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(33068, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND3_CUMULATIVE);
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(33068, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4Cumulative() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(46203, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "00fmeepz"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_ROUND4_CUMULATIVE);
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(46203, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "00fmeepz"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidComplete() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-complete.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(69318, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));

    qrels = RelevanceJudgments.fromQrels(Qrels.COVID_COMPLETE);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(69318, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testNtcir8Zh() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.ntcir8.eval.txt");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(110213, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020106.0118"));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020107.0140"));

    qrels = RelevanceJudgments.fromQrels(Qrels.NTCIR8_ZH);
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(110213, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020106.0118"));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020107.0140"));
  }

  @Test
  public void testClef2006Fr() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.clef06fr.txt");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17882, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940106.0082"));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940112.0089"));

    qrels = RelevanceJudgments.fromQrels(Qrels.CLEF2006_FR);
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17882, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940106.0082"));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940112.0089"));
  }

  @Test
  public void testTrec2002Ar() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.trec02ar.txt");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(38432, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("26", "19940515_AFP_ARB.0115"));
    assertEquals(1, qrels.getRelevanceGrade("26", "19941213_AFP_ARB.0159"));

    qrels = RelevanceJudgments.fromQrels(Qrels.TREC2002_AR);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(38432, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("26", "19940515_AFP_ARB.0115"));
    assertEquals(1, qrels.getRelevanceGrade("26", "19941213_AFP_ARB.0159"));
  }

  @Test
  public void testMrTyDiAr() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ar.train.txt");
    assertNotNull(qrels);
    assertEquals(12377, qrels.getQids().size());
    assertEquals(12377, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_AR_TRAIN);
    assertNotNull(qrels);
    assertEquals(12377, qrels.getQids().size());
    assertEquals(12377, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ar.dev.txt");
    assertNotNull(qrels);
    assertEquals(3115, qrels.getQids().size());
    assertEquals(3115, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_AR_DEV);
    assertNotNull(qrels);
    assertEquals(3115, qrels.getQids().size());
    assertEquals(3115, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ar.test.txt");
    assertNotNull(qrels);
    assertEquals(1081, qrels.getQids().size());
    assertEquals(1257, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_AR_TEST);
    assertNotNull(qrels);
    assertEquals(1081, qrels.getQids().size());
    assertEquals(1257, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiBn() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.train.txt");
    assertNotNull(qrels);
    assertEquals(1713, qrels.getQids().size());
    assertEquals(1719, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_BN_TRAIN);
    assertNotNull(qrels);
    assertEquals(1713, qrels.getQids().size());
    assertEquals(1719, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.dev.txt");
    assertNotNull(qrels);
    assertEquals(440, qrels.getQids().size());
    assertEquals(443, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_BN_DEV);
    assertNotNull(qrels);
    assertEquals(440, qrels.getQids().size());
    assertEquals(443, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-bn.test.txt");
    assertNotNull(qrels);
    assertEquals(111, qrels.getQids().size());
    assertEquals(130, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_BN_TEST);
    assertNotNull(qrels);
    assertEquals(111, qrels.getQids().size());
    assertEquals(130, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiEn() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-en.train.txt");
    assertNotNull(qrels);
    assertEquals(3547, qrels.getQids().size());
    assertEquals(3547, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_EN_TRAIN);
    assertNotNull(qrels);
    assertEquals(3547, qrels.getQids().size());
    assertEquals(3547, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-en.dev.txt");
    assertNotNull(qrels);
    assertEquals(878, qrels.getQids().size());
    assertEquals(878, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_EN_DEV);
    assertNotNull(qrels);
    assertEquals(878, qrels.getQids().size());
    assertEquals(878, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-en.test.txt");
    assertNotNull(qrels);
    assertEquals(744, qrels.getQids().size());
    assertEquals(935, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_EN_TEST);
    assertNotNull(qrels);
    assertEquals(744, qrels.getQids().size());
    assertEquals(935, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiFi() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.train.txt");
    assertNotNull(qrels);
    assertEquals(6561, qrels.getQids().size());
    assertEquals(6561, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_FI_TRAIN);
    assertNotNull(qrels);
    assertEquals(6561, qrels.getQids().size());
    assertEquals(6561, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.dev.txt");
    assertNotNull(qrels);
    assertEquals(1738, qrels.getQids().size());
    assertEquals(1738, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_FI_DEV);
    assertNotNull(qrels);
    assertEquals(1738, qrels.getQids().size());
    assertEquals(1738, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-fi.test.txt");
    assertNotNull(qrels);
    assertEquals(1254, qrels.getQids().size());
    assertEquals(1451, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_FI_TEST);
    assertNotNull(qrels);
    assertEquals(1254, qrels.getQids().size());
    assertEquals(1451, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiId() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-id.train.txt");
    assertNotNull(qrels);
    assertEquals(4902, qrels.getQids().size());
    assertEquals(4902, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_ID_TRAIN);
    assertNotNull(qrels);
    assertEquals(4902, qrels.getQids().size());
    assertEquals(4902, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-id.dev.txt");
    assertNotNull(qrels);
    assertEquals(1224, qrels.getQids().size());
    assertEquals(1224, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_ID_DEV);
    assertNotNull(qrels);
    assertEquals(1224, qrels.getQids().size());
    assertEquals(1224, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-id.test.txt");
    assertNotNull(qrels);
    assertEquals(829, qrels.getQids().size());
    assertEquals(961, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_ID_TEST);
    assertNotNull(qrels);
    assertEquals(829, qrels.getQids().size());
    assertEquals(961, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiJa() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.train.txt");
    assertNotNull(qrels);
    assertEquals(3697, qrels.getQids().size());
    assertEquals(3697, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_JA_TRAIN);
    assertNotNull(qrels);
    assertEquals(3697, qrels.getQids().size());
    assertEquals(3697, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.dev.txt");
    assertNotNull(qrels);
    assertEquals(928, qrels.getQids().size());
    assertEquals(928, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_JA_DEV);
    assertNotNull(qrels);
    assertEquals(928, qrels.getQids().size());
    assertEquals(928, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ja.test.txt");
    assertNotNull(qrels);
    assertEquals(720, qrels.getQids().size());
    assertEquals(923, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_JA_TEST);
    assertNotNull(qrels);
    assertEquals(720, qrels.getQids().size());
    assertEquals(923, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiKo() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ko.train.txt");
    assertNotNull(qrels);
    assertEquals(1295, qrels.getQids().size());
    assertEquals(1317, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_KO_TRAIN);
    assertNotNull(qrels);
    assertEquals(1295, qrels.getQids().size());
    assertEquals(1317, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ko.dev.txt");
    assertNotNull(qrels);
    assertEquals(303, qrels.getQids().size());
    assertEquals(307, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_KO_DEV);
    assertNotNull(qrels);
    assertEquals(303, qrels.getQids().size());
    assertEquals(307, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ko.test.txt");
    assertNotNull(qrels);
    assertEquals(421, qrels.getQids().size());
    assertEquals(492, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_KO_TEST);
    assertNotNull(qrels);
    assertEquals(421, qrels.getQids().size());
    assertEquals(492, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiRu() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.train.txt");
    assertNotNull(qrels);
    assertEquals(5366, qrels.getQids().size());
    assertEquals(5366, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_RU_TRAIN);
    assertNotNull(qrels);
    assertEquals(5366, qrels.getQids().size());
    assertEquals(5366, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.dev.txt");
    assertNotNull(qrels);
    assertEquals(1375, qrels.getQids().size());
    assertEquals(1375, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_RU_DEV);
    assertNotNull(qrels);
    assertEquals(1375, qrels.getQids().size());
    assertEquals(1375, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-ru.test.txt");
    assertNotNull(qrels);
    assertEquals(995, qrels.getQids().size());
    assertEquals(1168, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_RU_TEST);
    assertNotNull(qrels);
    assertEquals(995, qrels.getQids().size());
    assertEquals(1168, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiSw() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.train.txt");
    assertNotNull(qrels);
    assertEquals(2072, qrels.getQids().size());
    assertEquals(2401, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_SW_TRAIN);
    assertNotNull(qrels);
    assertEquals(2072, qrels.getQids().size());
    assertEquals(2401, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.dev.txt");
    assertNotNull(qrels);
    assertEquals(526, qrels.getQids().size());
    assertEquals(623, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_SW_DEV);
    assertNotNull(qrels);
    assertEquals(526, qrels.getQids().size());
    assertEquals(623, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-sw.test.txt");
    assertNotNull(qrels);
    assertEquals(670, qrels.getQids().size());
    assertEquals(743, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_SW_TEST);
    assertNotNull(qrels);
    assertEquals(670, qrels.getQids().size());
    assertEquals(743, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiTe() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.train.txt");
    assertNotNull(qrels);
    assertEquals(3880, qrels.getQids().size());
    assertEquals(3880, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TE_TRAIN);
    assertNotNull(qrels);
    assertEquals(3880, qrels.getQids().size());
    assertEquals(3880, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.dev.txt");
    assertNotNull(qrels);
    assertEquals(983, qrels.getQids().size());
    assertEquals(983, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TE_DEV);
    assertNotNull(qrels);
    assertEquals(983, qrels.getQids().size());
    assertEquals(983, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-te.test.txt");
    assertNotNull(qrels);
    assertEquals(646, qrels.getQids().size());
    assertEquals(677, getQrelsCount(qrels));
    // The value 677 differs from Mr. TyDi paper.
    // The paper reported 664, which is the qrel size before fixing the document slicing bug.
    // 677 should be the correct number.

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TE_TEST);
    assertNotNull(qrels);
    assertEquals(646, qrels.getQids().size());
    assertEquals(677, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiTh() {
    RelevanceJudgments qrels;

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-th.train.txt");
    assertNotNull(qrels);
    assertEquals(3319, qrels.getQids().size());
    assertEquals(3360, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TH_TRAIN);
    assertNotNull(qrels);
    assertEquals(3319, qrels.getQids().size());
    assertEquals(3360, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-th.dev.txt");
    assertNotNull(qrels);
    assertEquals(807, qrels.getQids().size());
    assertEquals(817, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TH_DEV);
    assertNotNull(qrels);
    assertEquals(807, qrels.getQids().size());
    assertEquals(817, getQrelsCount(qrels));

    qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.mrtydi-v1.1-th.test.txt");
    assertNotNull(qrels);
    assertEquals(1190, qrels.getQids().size());
    assertEquals(1368, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MRTYDI_V11_TH_TEST);
    assertNotNull(qrels);
    assertEquals(1190, qrels.getQids().size());
    assertEquals(1368, getQrelsCount(qrels));
  }

  @Test
  public void testBEIR() {
    RelevanceJudgments qrels;

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_TREC_COVID_TEST);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(66334, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_BIOASQ_TEST);
    assertNotNull(qrels);
    assertEquals(500, qrels.getQids().size());
    assertEquals(2359, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_NFCORPUS_TEST);
    assertNotNull(qrels);
    assertEquals(323, qrels.getQids().size());
    assertEquals(12334, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_NQ_TEST);
    assertNotNull(qrels);
    assertEquals(3452, qrels.getQids().size());
    assertEquals(4201, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_HOTPOTQA_TEST);
    assertNotNull(qrels);
    assertEquals(7405, qrels.getQids().size());
    assertEquals(14810, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_FIQA_TEST);
    assertNotNull(qrels);
    assertEquals(648, qrels.getQids().size());
    assertEquals(1706, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_SIGNAL1M_TEST);
    assertNotNull(qrels);
    assertEquals(97, qrels.getQids().size());
    assertEquals(1899, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_TREC_NEWS_TEST);
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(15655, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_ROBUST04_TEST);
    assertNotNull(qrels);
    assertEquals(249, qrels.getQids().size());
    assertEquals(311410, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_ARGUANA_TEST);
    assertNotNull(qrels);
    assertEquals(1406, qrels.getQids().size());
    assertEquals(1406, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_WEBIS_TOUCHE2020_TEST);
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(932, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_ANDROID_TEST);
    assertNotNull(qrels);
    assertEquals(699, qrels.getQids().size());
    assertEquals(1696, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_ENGLISH_TEST);
    assertNotNull(qrels);
    assertEquals(1570, qrels.getQids().size());
    assertEquals(3765, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_GAMING_TEST);
    assertNotNull(qrels);
    assertEquals(1595, qrels.getQids().size());
    assertEquals(2263, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_GIS_TEST);
    assertNotNull(qrels);
    assertEquals(885, qrels.getQids().size());
    assertEquals(1114, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_MATHEMATICA_TEST);
    assertNotNull(qrels);
    assertEquals(804, qrels.getQids().size());
    assertEquals(1358, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_PHYSICS_TEST);
    assertNotNull(qrels);
    assertEquals(1039, qrels.getQids().size());
    assertEquals(1933, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_PROGRAMMERS_TEST);
    assertNotNull(qrels);
    assertEquals(876, qrels.getQids().size());
    assertEquals(1675, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_STATS_TEST);
    assertNotNull(qrels);
    assertEquals(652, qrels.getQids().size());
    assertEquals(913, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_TEX_TEST);
    assertNotNull(qrels);
    assertEquals(2906, qrels.getQids().size());
    assertEquals(5154, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_UNIX_TEST);
    assertNotNull(qrels);
    assertEquals(1072, qrels.getQids().size());
    assertEquals(1693, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_WEBMASTERS_TEST);
    assertNotNull(qrels);
    assertEquals(506, qrels.getQids().size());
    assertEquals(1395, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CQADUPSTACK_WORDPRESS_TEST);
    assertNotNull(qrels);
    assertEquals(541, qrels.getQids().size());
    assertEquals(744, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_QUORA_TEST);
    assertNotNull(qrels);
    assertEquals(10000, qrels.getQids().size());
    assertEquals(15675, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_DBPEDIA_ENTITY_TEST);
    assertNotNull(qrels);
    assertEquals(400, qrels.getQids().size());
    assertEquals(43515, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_SCIDOCS_TEST);
    assertNotNull(qrels);
    assertEquals(1000, qrels.getQids().size());
    assertEquals(29928, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_FEVER_TEST);
    assertNotNull(qrels);
    assertEquals(6666, qrels.getQids().size());
    assertEquals(7937, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_CLIMATE_FEVER_TEST);
    assertNotNull(qrels);
    assertEquals(1535, qrels.getQids().size());
    assertEquals(4681, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.BEIR_V1_0_0_SCIFACT_TEST);
    assertNotNull(qrels);
    assertEquals(300, qrels.getQids().size());
    assertEquals(339, getQrelsCount(qrels));
  }
  
  @Test
  public void testHC4() {
    RelevanceJudgments qrels;
    
    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_RU_DEV);
    assertNotNull(qrels);
    assertEquals(4, qrels.getQids().size());
    assertEquals(265, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_RU_TEST);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2970, getQrelsCount(qrels));
  
    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_FA_DEV);
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(565, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_FA_TEST);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2522, getQrelsCount(qrels));
  
    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_ZH_DEV);
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(466, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.HC4_V1_0_ZH_TEST);
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2751, getQrelsCount(qrels));
  }

  @Test
  public void testMIRACL() {
    RelevanceJudgments qrels;

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_AR_DEV);
    assertNotNull(qrels);
    assertEquals(2896, qrels.getQids().size());
    assertEquals(29197, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_BN_DEV);
    assertNotNull(qrels);
    assertEquals(411, qrels.getQids().size());
    assertEquals(4206, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_EN_DEV);
    assertNotNull(qrels);
    assertEquals(799, qrels.getQids().size());
    assertEquals(8350, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_ES_DEV);
    assertNotNull(qrels);
    assertEquals(648, qrels.getQids().size());
    assertEquals(6443, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_FA_DEV);
    assertNotNull(qrels);
    assertEquals(632, qrels.getQids().size());
    assertEquals(6571, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_FI_DEV);
    assertNotNull(qrels);
    assertEquals(1271, qrels.getQids().size());
    assertEquals(12008, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_FR_DEV);
    assertNotNull(qrels);
    assertEquals(343, qrels.getQids().size());
    assertEquals(3429, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_HI_DEV);
    assertNotNull(qrels);
    assertEquals(350, qrels.getQids().size());
    assertEquals(3494, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_ID_DEV);
    assertNotNull(qrels);
    assertEquals(960, qrels.getQids().size());
    assertEquals(9668, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_JA_DEV);
    assertNotNull(qrels);
    assertEquals(860, qrels.getQids().size());
    assertEquals(8354, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_KO_DEV);
    assertNotNull(qrels);
    assertEquals(213, qrels.getQids().size());
    assertEquals(3057, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_RU_DEV);
    assertNotNull(qrels);
    assertEquals(1252, qrels.getQids().size());
    assertEquals(13100, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_SW_DEV);
    assertNotNull(qrels);
    assertEquals(482, qrels.getQids().size());
    assertEquals(5092, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_TE_DEV);
    assertNotNull(qrels);
    assertEquals(828, qrels.getQids().size());
    assertEquals(1606, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_TH_DEV);
    assertNotNull(qrels);
    assertEquals(733, qrels.getQids().size());
    assertEquals(7573, getQrelsCount(qrels));

    qrels = RelevanceJudgments.fromQrels(Qrels.MIRACL_V10_ZH_DEV);
    assertNotNull(qrels);
    assertEquals(393, qrels.getQids().size());
    assertEquals(3928, getQrelsCount(qrels));


  }
}
