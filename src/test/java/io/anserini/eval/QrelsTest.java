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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;

import org.junit.Test;

import io.anserini.util.CacheDirectoryResolver;

public class QrelsTest{

  public int getQrelsCount(Qrels qrels) throws IOException {
    int count = 0;
    for (String qid : qrels.getQids()) {
      count += qrels.getDocMap(qid).size();
    }
    return count;
  }

  @Test
  public void testTotalCount() {
    assertEquals(233, Qrels.registry().size());
    assertEquals(233, new HashSet<>(Qrels.registry().values()).size());
  }

  @Test(expected = IOException.class)
  public void testFileNotFound() throws IOException {
    // Purposely read non-existent file.
    Qrels.loadFromFile("tools/topics-and-qrels/qrels.xxx.txt");
  }

  @Test(expected = IOException.class)
  public void testNonvalidQrels() throws IOException {
    // Purposely read non-valid qrels.
    Qrels.loadFromFile("tools/topics-and-qrels/topics.robust04.txt ");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownQrelsName() throws IOException {
    Qrels.get("this-qrels-name-does-not-exist");
  }

  @Test
  public void testName() throws IOException {
    Qrels qrels = Qrels.get("cacm");
    assertEquals("cacm", qrels.name());

    qrels = Qrels.get("beir-v1.0.0-arguana-test");
    assertEquals("beir-v1.0.0-arguana", qrels.name());
  }

  @Test
  public void testCacm() throws IOException {
    Qrels qrels = Qrels.get("cacm");
    assertNotNull(qrels);
    assertEquals("cacm", qrels.name());
    assertEquals(52, qrels.getQids().size());
    assertEquals(796, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "CACM-1410"));
    assertEquals(0, qrels.getRelevanceGrade("1", "CACM-1410x")); // non-existent docid
    assertEquals(0, qrels.getRelevanceGrade("xxx", "CACM-1410"));  // non-existent topic
    assertTrue(qrels.isDocJudged("1", "CACM-1410"));
    assertNull(qrels.getDocMap("xxx"));
  }

  @Test
  public void testRobust04() throws IOException {
    Qrels qrels = Qrels.get("robust04");
    assertNotNull(qrels);
    assertEquals(249, qrels.getQids().size());
    assertEquals(311410, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("301", "FBIS3-10082"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137x")); // non-existent docid
    assertEquals(0, qrels.getRelevanceGrade("xxx", "LA123090-0137"));  // non-existent topic
    assertTrue(qrels.isDocJudged("301", "FBIS3-10082"));
    assertNull(qrels.getDocMap("xxx"));
  }

  @Test
  public void testRobust05() throws IOException {
    Qrels qrels = Qrels.get("robust05");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(37798, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("303", "APW19980609.1531"));
    assertEquals(0, qrels.getRelevanceGrade("689", "XIE20000925.0055"));
  }

  @Test
  public void testTrec19DLDoc() throws IOException {
    Qrels qrels = Qrels.get("dl19-doc");
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(16258, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "D1035833"));
    assertEquals(0, qrels.getRelevanceGrade("1133167", "D984590"));
  }

  @Test
  public void testTrec19DLPassage() throws IOException {
    Qrels qrels = Qrels.get("dl19-passage");
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(9260, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "1017759"));
    assertEquals(1, qrels.getRelevanceGrade("1133167", "8804478"));
  }

  @Test
  public void testTrec20DLDoc() throws IOException {
    Qrels qrels = Qrels.get("dl20-doc");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(9098, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("42255", "D1884223"));
    assertEquals(3, qrels.getRelevanceGrade("1136962", "D96741"));
  }

  @Test
  public void testTrec20DLPassage() throws IOException {
    Qrels qrels = Qrels.get("dl20-passage");
    assertNotNull(qrels);
    assertEquals(54, qrels.getQids().size());
    assertEquals(11386, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("23849", "1020327"));
    assertEquals(1, qrels.getRelevanceGrade("1136962", "937258"));
  }

  @Test
  public void testTrec21DLDoc() throws IOException {
    Qrels qrels = Qrels.get("dl21-doc");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(13058, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_doc_01_1320020407"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_doc_59_863449044"));
  }

  @Test
  public void testTrec21DLPassage() throws IOException {
    Qrels qrels = Qrels.get("dl21-passage");
    assertNotNull(qrels);
    assertEquals(53, qrels.getQids().size());
    assertEquals(10828, getQrelsCount(qrels));
    assertEquals(3, qrels.getRelevanceGrade("2082", "msmarco_passage_02_179207466"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_passage_67_937656589"));
  }

  @Test
  public void testTrec21DLDocMsMarcoV21() throws IOException {
    Qrels qrels = Qrels.get("dl21-doc-msmarco-v2.1");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(10973, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_v2.1_doc_01_1281570012"));
    assertEquals(2, qrels.getRelevanceGrade("1128632", "msmarco_v2.1_doc_17_481617788"));
  }

  @Test
  public void testTrec22DLDoc() throws IOException {
    Qrels qrels = Qrels.get("dl22-doc");
    assertNotNull(qrels);
    assertEquals(76, qrels.getQids().size());
    assertEquals(369638, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_doc_00_928744703"));
    assertEquals(1, qrels.getRelevanceGrade("2056323", "msmarco_doc_59_419476385"));
  }

  @Test
  public void testTrec22DLPassage() throws IOException {
    Qrels qrels = Qrels.get("dl22-passage");
    assertNotNull(qrels);
    assertEquals(76, qrels.getQids().size());
    assertEquals(386416, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_passage_00_491585864"));
    assertEquals(1, qrels.getRelevanceGrade("2056323", "msmarco_passage_68_715747739"));
  }

  @Test
  public void testTrec22DLDocMsMarcoV21() throws IOException {
    Qrels qrels = Qrels.get("dl22-doc-msmarco-v2.1");
    assertNotNull(qrels);
    assertEquals(76, qrels.getQids().size());
    assertEquals(349541, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_v2.1_doc_00_896525856"));
    assertEquals(2, qrels.getRelevanceGrade("2056158", "msmarco_v2.1_doc_06_934688453"));
  }

  @Test
  public void testTrec23DLDoc() throws IOException {
    Qrels qrels = Qrels.get("dl23-doc");
    assertNotNull(qrels);
    assertEquals(82, qrels.getQids().size());
    assertEquals(18034, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_doc_00_1413652624"));
    assertEquals(3, qrels.getRelevanceGrade("3100922", "msmarco_doc_16_3928760942"));
  }

  @Test
  public void testTrec23DLPassage() throws IOException {
    Qrels qrels = Qrels.get("dl23-passage");
    assertNotNull(qrels);
    assertEquals(82, qrels.getQids().size());
    assertEquals(22327, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_passage_00_729315698"));
    assertEquals(2, qrels.getRelevanceGrade("3100922", "msmarco_passage_22_487548813"));
  }

  @Test
  public void testTrec23DLDocMsMarcoV21() throws IOException {
    Qrels qrels = Qrels.get("dl23-doc-msmarco-v2.1");
    assertNotNull(qrels);
    assertEquals(82, qrels.getQids().size());
    assertEquals(15995, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_v2.1_doc_00_1372241967"));
    assertEquals(2, qrels.getRelevanceGrade("3100922", "msmarco_v2.1_doc_19_1982402861"));
  }

  @Test
  public void testTREC24_RAG_RAGGY_DEV() throws IOException {
    Qrels qrels = Qrels.get("rag24.raggy-dev");
    assertNotNull(qrels);
    assertEquals(120, qrels.getQids().size());
    assertEquals(147328, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_v2.1_doc_00_1372241967"));
    assertEquals(1, qrels.getRelevanceGrade("253263", "msmarco_v2.1_doc_46_843492186"));
  }

  @Test
  public void testTREC24_RAG_UMBRELA() throws IOException {
    Qrels qrels = Qrels.get("rag24.test-umbrela");
    assertNotNull(qrels);
    assertEquals(301, qrels.getQids().size());
    assertEquals(108479, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2024-145979", "msmarco_v2.1_doc_25_771726319#13_1477564195"));
    assertEquals(1, qrels.getRelevanceGrade("2024-216592", "msmarco_v2.1_doc_52_1092442741#3_2165187686"));
  }

  @Test
  public void testTREC24_RAG() throws IOException {
    Qrels qrels = Qrels.get("rag24.test");
    assertNotNull(qrels);
    assertEquals(89, qrels.getQids().size());
    assertEquals(20429, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("2024-145979", "msmarco_v2.1_doc_00_125364462#6_229054655"));
    assertEquals(1, qrels.getRelevanceGrade("2024-96359", "msmarco_v2.1_doc_54_724887112#1_1700994504"));
  }

  @Test
  public void testTREC25_RAG_UMBRELA() throws IOException {
    Qrels qrels = Qrels.get("rag25.test-umbrela2");
    assertNotNull(qrels);
    assertEquals(22, qrels.getQids().size());
    assertEquals(10284, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("200", "msmarco_v2.1_doc_10_1630045707#13_2668822206"));
    assertEquals(1, qrels.getRelevanceGrade("31", "msmarco_v2.1_doc_20_1589508824#12_3490034638"));
  }

  @Test
  public void testTREC25_RAG() throws IOException {
    Qrels qrels = Qrels.get("rag25.test");
    assertNotNull(qrels);
    assertEquals(22, qrels.getQids().size());
    assertEquals(10284, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("200", "msmarco_v2.1_doc_10_1630045707#13_2668822206"));
    assertEquals(0, qrels.getRelevanceGrade("31", "msmarco_v2.1_doc_20_1589508824#12_3490034638"));
  }

  @Test
  public void testMsmarcoDocDev() throws IOException {
    Qrels qrels = Qrels.get("msmarco-doc-dev");
    assertNotNull(qrels);
    assertEquals(5193, qrels.getQids().size());
    assertEquals(5193, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("2", "D1650436"));
    assertEquals(1, qrels.getRelevanceGrade("1102400", "D677570"));
  }

  @Test
  public void testMsmarcoPassageDevSubset() throws IOException {
    Qrels qrels = Qrels.get("msmarco-passage-dev");
    assertNotNull(qrels);
    assertEquals(6980, qrels.getQids().size());
    assertEquals(7437, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("300674", "7067032"));
    assertEquals(1, qrels.getRelevanceGrade("195199", "8009377"));
  }

  @Test
  public void testMsmarcoV2DocDevMsMarcoV21() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2.1-doc.dev");
    assertNotNull(qrels);
    assertEquals(4552, qrels.getQids().size());
    assertEquals(4702, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_v2.1_doc_17_1968189952"));
    assertEquals(1, qrels.getRelevanceGrade("999897", "msmarco_v2.1_doc_46_191673440"));
  }

  @Test
  public void testMsmarcoV2DocDev2MsMarcoV21() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2.1-doc.dev2");
    assertNotNull(qrels);
    assertEquals(5000, qrels.getQids().size());
    assertEquals(5177, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_v2.1_doc_08_69146701"));
    assertEquals(1, qrels.getRelevanceGrade("999659", "msmarco_v2.1_doc_08_1247437925"));
  }

  @Test
  public void testMsmarcoV2DocPassage() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2-passage-dev");
    assertNotNull(qrels);
    assertEquals(3903, qrels.getQids().size());
    assertEquals(4009, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("763878", "msmarco_passage_33_459057644"));
    assertEquals(1, qrels.getRelevanceGrade("1091692", "msmarco_passage_23_330102695"));
  }

  @Test
  public void testMsmarcoV2DocPassage2() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2-passage-dev2");
    assertNotNull(qrels);
    assertEquals(4281, qrels.getQids().size());
    assertEquals(4411, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("419507", "msmarco_passage_04_254301507"));
    assertEquals(1, qrels.getRelevanceGrade("961297", "msmarco_passage_18_858458289"));
  }

  @Test
  public void testMsmarcoV2DocDev() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2-doc-dev");
    assertNotNull(qrels);
    assertEquals(4552, qrels.getQids().size());
    assertEquals(4702, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_doc_17_2560009121"));
    assertEquals(1, qrels.getRelevanceGrade("999942", "msmarco_doc_06_956348348"));
  }

  @Test
  public void testMsmarcoV2DocDev2() throws IOException {
    Qrels qrels = Qrels.get("msmarco-v2-doc-dev2");
    assertNotNull(qrels);
    assertEquals(5000, qrels.getQids().size());
    assertEquals(5178, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_doc_08_73026062"));
    assertEquals(1, qrels.getRelevanceGrade("999937", "msmarco_doc_05_319743607"));
  }

  @Test
  public void testCore17() throws IOException {
    Qrels qrels = Qrels.get("core17");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(30030, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("307", "1001536"));
    assertEquals(0, qrels.getRelevanceGrade("690", "996059"));
  }

  @Test
  public void testCore18() throws IOException {
    Qrels qrels = Qrels.get("core18");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(26233, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("321", "004c6120d0aa69da29cc045da0562168"));
    assertEquals(0, qrels.getRelevanceGrade("825", "ff3a25b0-0ba4-11e4-8341-b8072b1e7348"));
  }

  @Test
  public void testCar15() throws IOException {
    Qrels qrels = Qrels.get("car17v1.5-benchmarkY1test");
    assertNotNull(qrels);
    assertEquals(2125, qrels.getQids().size());
    assertEquals(5820, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex", "38c1bd25ddca2705164677a3f598c46df85afba7"));
    assertEquals(1, qrels.getRelevanceGrade("Yellowstone%20National%20Park/Recreation", "e80b5185da1493edde41bea19a389a3f62167369"));
  }

  @Test
  public void testCar20() throws IOException {
    Qrels qrels = Qrels.get("car17v2.0-benchmarkY1test");
    assertNotNull(qrels);
    assertEquals(2254, qrels.getQids().size());
    assertEquals(6192, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Aftertaste", "327cca6c4d38953196fa6789f615546f03287b25"));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Yellowstone%20National%20Park/Recreation", "b812fca195f74f8c563db4262260554fe3ff3731"));
  }

  @Test
  public void testTrec2018BL() throws IOException {
    Qrels qrels = Qrels.get("trec2018-bl");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(8508, getQrelsCount(qrels));
    assertEquals(16, qrels.getRelevanceGrade("321", "00f57310e5c8ec7833d6756ba637332e"));
    assertEquals(0, qrels.getRelevanceGrade("825", "f66b624ba8689d704872fa776fb52860"));
  }

  @Test
  public void testTrec2019BL() throws IOException {
    Qrels qrels = Qrels.get("trec2019-bl");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(15655, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("826", "0154349511cd8c49ab862d6cb0d8f6a8"));
    assertEquals(0, qrels.getRelevanceGrade("885", "fde80cb0-b4f0-11e2-bbf2-a6f9e9d79e19"));
  }

  @Test
  public void testTrec2020BL() throws IOException {
    Qrels qrels = Qrels.get("trec2020-bl");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17764, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("886", "00183d98-741b-11e5-8248-98e0f5a2e830"));
    assertEquals(0, qrels.getRelevanceGrade("935", "ff0a760128ecdbcc096cafc8cd553255"));
  }

  @Test
  public void testCovidRound1() throws IOException {
    Qrels qrels = Qrels.get("covid-round1");
    assertNotNull(qrels);
    assertEquals(30, qrels.getQids().size());
    assertEquals(8691, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("30", "zn87f1lk"));
  }

  @Test
  public void testCovidRound2() throws IOException {
    Qrels qrels = Qrels.get("covid-round2");
    assertNotNull(qrels);
    assertEquals(35, qrels.getQids().size());
    assertEquals(12037, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("1", "08efpohc"));
    assertEquals(0, qrels.getRelevanceGrade("35", "zzmfhr2s"));
  }

  @Test
  public void testCovidRound3() throws IOException {
    Qrels qrels = Qrels.get("covid-round3");
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(12713, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "0194oljo"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4() throws IOException {
    Qrels qrels = Qrels.get("covid-round4");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(13262, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "1c47w4q5"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidRound5() throws IOException {
    Qrels qrels = Qrels.get("covid-round5");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(23151, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testCovidRound3Cumulative() throws IOException {
    Qrels qrels = Qrels.get("covid-round3-cumulative");
    assertNotNull(qrels);
    assertEquals(40, qrels.getQids().size());
    assertEquals(33068, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4Cumulative() throws IOException {
    Qrels qrels = Qrels.get("covid-round4-cumulative");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(46203, getQrelsCount(qrels));
    assertEquals(1, qrels.getRelevanceGrade("1", "00fmeepz"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidComplete() throws IOException {
    Qrels qrels = Qrels.get("covid-complete");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(69318, getQrelsCount(qrels));
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testNtcir8Zh() throws IOException {
    Qrels qrels = Qrels.get("ntcir8-zh");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(110213, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020106.0118"));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020107.0140"));
  }

  @Test
  public void testClef2006Fr() throws IOException {
    Qrels qrels = Qrels.get("clef2006-fr");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(17882, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940106.0082"));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940112.0089"));
  }

  @Test
  public void testTrec2002Ar() throws IOException {
    Qrels qrels = Qrels.get("trec2002-ar");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(38432, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("26", "19940515_AFP_ARB.0115"));
    assertEquals(1, qrels.getRelevanceGrade("26", "19941213_AFP_ARB.0159"));
  }

  @Test
  public void testMrTyDiAr() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-arabic-train");
    assertNotNull(qrels);
    assertEquals(12377, qrels.getQids().size());
    assertEquals(12377, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-arabic-dev");
    assertNotNull(qrels);
    assertEquals(3115, qrels.getQids().size());
    assertEquals(3115, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-arabic-test");
    assertNotNull(qrels);
    assertEquals(1081, qrels.getQids().size());
    assertEquals(1257, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiBn() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-bengali-train");
    assertNotNull(qrels);
    assertEquals(1713, qrels.getQids().size());
    assertEquals(1719, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-bengali-dev");
    assertNotNull(qrels);
    assertEquals(440, qrels.getQids().size());
    assertEquals(443, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-bengali-test");
    assertNotNull(qrels);
    assertEquals(111, qrels.getQids().size());
    assertEquals(130, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiEn() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-english-train");
    assertNotNull(qrels);
    assertEquals(3547, qrels.getQids().size());
    assertEquals(3547, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-english-dev");
    assertNotNull(qrels);
    assertEquals(878, qrels.getQids().size());
    assertEquals(878, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-english-test");
    assertNotNull(qrels);
    assertEquals(744, qrels.getQids().size());
    assertEquals(935, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiFi() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-finnish-train");
    assertNotNull(qrels);
    assertEquals(6561, qrels.getQids().size());
    assertEquals(6561, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-finnish-dev");
    assertNotNull(qrels);
    assertEquals(1738, qrels.getQids().size());
    assertEquals(1738, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-finnish-test");
    assertNotNull(qrels);
    assertEquals(1254, qrels.getQids().size());
    assertEquals(1451, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiId() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-indonesian-train");
    assertNotNull(qrels);
    assertEquals(4902, qrels.getQids().size());
    assertEquals(4902, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-indonesian-dev");
    assertNotNull(qrels);
    assertEquals(1224, qrels.getQids().size());
    assertEquals(1224, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-indonesian-test");
    assertNotNull(qrels);
    assertEquals(829, qrels.getQids().size());
    assertEquals(961, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiJa() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-japanese-train");
    assertNotNull(qrels);
    assertEquals(3697, qrels.getQids().size());
    assertEquals(3697, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-japanese-dev");
    assertNotNull(qrels);
    assertEquals(928, qrels.getQids().size());
    assertEquals(928, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-japanese-test");
    assertNotNull(qrels);
    assertEquals(720, qrels.getQids().size());
    assertEquals(923, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiKo() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-korean-train");
    assertNotNull(qrels);
    assertEquals(1295, qrels.getQids().size());
    assertEquals(1317, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-korean-dev");
    assertNotNull(qrels);
    assertEquals(303, qrels.getQids().size());
    assertEquals(307, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-korean-test");
    assertNotNull(qrels);
    assertEquals(421, qrels.getQids().size());
    assertEquals(492, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiRu() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-russian-train");
    assertNotNull(qrels);
    assertEquals(5366, qrels.getQids().size());
    assertEquals(5366, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-russian-dev");
    assertNotNull(qrels);
    assertEquals(1375, qrels.getQids().size());
    assertEquals(1375, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-russian-test");
    assertNotNull(qrels);
    assertEquals(995, qrels.getQids().size());
    assertEquals(1168, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiSw() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-swahili-train");
    assertNotNull(qrels);
    assertEquals(2072, qrels.getQids().size());
    assertEquals(2401, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-swahili-dev");
    assertNotNull(qrels);
    assertEquals(526, qrels.getQids().size());
    assertEquals(623, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-swahili-test");
    assertNotNull(qrels);
    assertEquals(670, qrels.getQids().size());
    assertEquals(743, getQrelsCount(qrels));
  }

  @Test
  public void testMrTyDiTe() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-telugu-train");
    assertNotNull(qrels);
    assertEquals(3880, qrels.getQids().size());
    assertEquals(3880, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-telugu-dev");
    assertNotNull(qrels);
    assertEquals(983, qrels.getQids().size());
    assertEquals(983, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-telugu-test");
    assertNotNull(qrels);
    assertEquals(646, qrels.getQids().size());
    assertEquals(677, getQrelsCount(qrels));
    // The value 677 differs from Mr. TyDi paper.
    // The paper reported 664, which is the qrel size before fixing the document slicing bug.
    // 677 should be the correct number.
  }

  @Test
  public void testMrTyDiTh() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("mrtydi-v1.1-thai-train");
    assertNotNull(qrels);
    assertEquals(3319, qrels.getQids().size());
    assertEquals(3360, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-thai-dev");
    assertNotNull(qrels);
    assertEquals(807, qrels.getQids().size());
    assertEquals(817, getQrelsCount(qrels));

    qrels = Qrels.get("mrtydi-v1.1-thai-test");
    assertNotNull(qrels);
    assertEquals(1190, qrels.getQids().size());
    assertEquals(1368, getQrelsCount(qrels));
  }

  @Test
  public void testBRIGHT() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("bright-biology");
    assertNotNull(qrels);
    assertEquals(103, qrels.getQids().size());
    assertEquals(372, getQrelsCount(qrels));

    qrels = Qrels.get("bright-earth-science");
    assertNotNull(qrels);
    assertEquals(116, qrels.getQids().size());
    assertEquals(585, getQrelsCount(qrels));

    qrels = Qrels.get("bright-economics");
    assertNotNull(qrels);
    assertEquals(103, qrels.getQids().size());
    assertEquals(800, getQrelsCount(qrels));

    qrels = Qrels.get("bright-psychology");
    assertNotNull(qrels);
    assertEquals(101, qrels.getQids().size());
    assertEquals(692, getQrelsCount(qrels));

    qrels = Qrels.get("bright-robotics");
    assertNotNull(qrels);
    assertEquals(101, qrels.getQids().size());
    assertEquals(520, getQrelsCount(qrels));

    qrels = Qrels.get("bright-stackoverflow");
    assertNotNull(qrels);
    assertEquals(117, qrels.getQids().size());
    assertEquals(478, getQrelsCount(qrels));

    qrels = Qrels.get("bright-sustainable-living");
    assertNotNull(qrels);
    assertEquals(108, qrels.getQids().size());
    assertEquals(576, getQrelsCount(qrels));

    qrels = Qrels.get("bright-pony");
    assertNotNull(qrels);
    assertEquals(112, qrels.getQids().size());
    assertEquals(2219, getQrelsCount(qrels));

    qrels = Qrels.get("bright-leetcode");
    assertNotNull(qrels);
    assertEquals(142, qrels.getQids().size());
    assertEquals(262, getQrelsCount(qrels));

    qrels = Qrels.get("bright-aops");
    assertNotNull(qrels);
    assertEquals(111, qrels.getQids().size());
    assertEquals(524, getQrelsCount(qrels));

    qrels = Qrels.get("bright-theoremqa-theorems");
    assertNotNull(qrels);
    assertEquals(76, qrels.getQids().size());
    assertEquals(151, getQrelsCount(qrels));

    qrels = Qrels.get("bright-theoremqa-questions");
    assertNotNull(qrels);
    assertEquals(194, qrels.getQids().size());
    assertEquals(439, getQrelsCount(qrels));
  }

  @Test
  public void testBEIR() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("beir-v1.0.0-trec-covid-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(66334, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-bioasq-test");
    assertNotNull(qrels);
    assertEquals(500, qrels.getQids().size());
    assertEquals(2359, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-nfcorpus-test");
    assertNotNull(qrels);
    assertEquals(323, qrels.getQids().size());
    assertEquals(12334, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-nq-test");
    assertNotNull(qrels);
    assertEquals(3452, qrels.getQids().size());
    assertEquals(4201, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-hotpotqa-test");
    assertNotNull(qrels);
    assertEquals(7405, qrels.getQids().size());
    assertEquals(14810, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-fiqa-test");
    assertNotNull(qrels);
    assertEquals(648, qrels.getQids().size());
    assertEquals(1706, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-signal1m-test");
    assertNotNull(qrels);
    assertEquals(97, qrels.getQids().size());
    assertEquals(1899, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-trec-news-test");
    assertNotNull(qrels);
    assertEquals(57, qrels.getQids().size());
    assertEquals(15655, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-robust04-test");
    assertNotNull(qrels);
    assertEquals(249, qrels.getQids().size());
    assertEquals(311410, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-arguana-test");
    assertNotNull(qrels);
    assertEquals(1406, qrels.getQids().size());
    assertEquals(1406, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-webis-touche2020-test");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(932, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-android-test");
    assertNotNull(qrels);
    assertEquals(699, qrels.getQids().size());
    assertEquals(1696, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-english-test");
    assertNotNull(qrels);
    assertEquals(1570, qrels.getQids().size());
    assertEquals(3765, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-gaming-test");
    assertNotNull(qrels);
    assertEquals(1595, qrels.getQids().size());
    assertEquals(2263, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-gis-test");
    assertNotNull(qrels);
    assertEquals(885, qrels.getQids().size());
    assertEquals(1114, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-mathematica-test");
    assertNotNull(qrels);
    assertEquals(804, qrels.getQids().size());
    assertEquals(1358, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-physics-test");
    assertNotNull(qrels);
    assertEquals(1039, qrels.getQids().size());
    assertEquals(1933, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-programmers-test");
    assertNotNull(qrels);
    assertEquals(876, qrels.getQids().size());
    assertEquals(1675, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-stats-test");
    assertNotNull(qrels);
    assertEquals(652, qrels.getQids().size());
    assertEquals(913, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-tex-test");
    assertNotNull(qrels);
    assertEquals(2906, qrels.getQids().size());
    assertEquals(5154, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-unix-test");
    assertNotNull(qrels);
    assertEquals(1072, qrels.getQids().size());
    assertEquals(1693, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-webmasters-test");
    assertNotNull(qrels);
    assertEquals(506, qrels.getQids().size());
    assertEquals(1395, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-cqadupstack-wordpress-test");
    assertNotNull(qrels);
    assertEquals(541, qrels.getQids().size());
    assertEquals(744, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-quora-test");
    assertNotNull(qrels);
    assertEquals(10000, qrels.getQids().size());
    assertEquals(15675, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-dbpedia-entity-test");
    assertNotNull(qrels);
    assertEquals(400, qrels.getQids().size());
    assertEquals(43515, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-scidocs-test");
    assertNotNull(qrels);
    assertEquals(1000, qrels.getQids().size());
    assertEquals(29928, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-fever-test");
    assertNotNull(qrels);
    assertEquals(6666, qrels.getQids().size());
    assertEquals(7937, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-climate-fever-test");
    assertNotNull(qrels);
    assertEquals(1535, qrels.getQids().size());
    assertEquals(4681, getQrelsCount(qrels));

    qrels = Qrels.get("beir-v1.0.0-scifact-test");
    assertNotNull(qrels);
    assertEquals(300, qrels.getQids().size());
    assertEquals(339, getQrelsCount(qrels));
  }
  
  @Test
  public void testHC4() throws IOException {
    Qrels qrels;
    
    qrels = Qrels.get("hc4-v1.0-ru-dev");
    assertNotNull(qrels);
    assertEquals(4, qrels.getQids().size());
    assertEquals(265, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-v1.0-ru-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2970, getQrelsCount(qrels));
  
    qrels = Qrels.get("hc4-v1.0-fa-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(565, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-v1.0-fa-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2522, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-v1.0-zh-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(466, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-v1.0-zh-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2751, getQrelsCount(qrels));
  }

  @Test
  public void testNeuClir2022() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("neuclir22-fa");
    assertNotNull(qrels);
    assertEquals(46, qrels.getQids().size());
    assertEquals(34174, getQrelsCount(qrels));

    qrels = Qrels.get("neuclir22-ru");
    assertNotNull(qrels);
    assertEquals(45, qrels.getQids().size());
    assertEquals(33006, getQrelsCount(qrels));

    qrels = Qrels.get("neuclir22-zh");
    assertNotNull(qrels);
    assertEquals(49, qrels.getQids().size());
    assertEquals(36575, getQrelsCount(qrels));
  }

  @Test
  public void testHc4NeuClir2022() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("hc4-neuclir22-fa-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(2041, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-neuclir22-ru-test");
    assertNotNull(qrels);
    assertEquals(50, qrels.getQids().size());
    assertEquals(625, getQrelsCount(qrels));

    qrels = Qrels.get("hc4-neuclir22-zh-test");
    assertNotNull(qrels);
    assertEquals(60, qrels.getQids().size());
    assertEquals(2573, getQrelsCount(qrels));
  }

  @Test
  public void testMIRACL() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("miracl-v1.0-ar-dev");
    assertNotNull(qrels);
    assertEquals(2896, qrels.getQids().size());
    assertEquals(29197, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-bn-dev");
    assertNotNull(qrels);
    assertEquals(411, qrels.getQids().size());
    assertEquals(4206, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-en-dev");
    assertNotNull(qrels);
    assertEquals(799, qrels.getQids().size());
    assertEquals(8350, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-es-dev");
    assertNotNull(qrels);
    assertEquals(648, qrels.getQids().size());
    assertEquals(6443, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-fa-dev");
    assertNotNull(qrels);
    assertEquals(632, qrels.getQids().size());
    assertEquals(6571, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-fi-dev");
    assertNotNull(qrels);
    assertEquals(1271, qrels.getQids().size());
    assertEquals(12008, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-fr-dev");
    assertNotNull(qrels);
    assertEquals(343, qrels.getQids().size());
    assertEquals(3429, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-hi-dev");
    assertNotNull(qrels);
    assertEquals(350, qrels.getQids().size());
    assertEquals(3494, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-id-dev");
    assertNotNull(qrels);
    assertEquals(960, qrels.getQids().size());
    assertEquals(9668, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-ja-dev");
    assertNotNull(qrels);
    assertEquals(860, qrels.getQids().size());
    assertEquals(8354, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-ko-dev");
    assertNotNull(qrels);
    assertEquals(213, qrels.getQids().size());
    assertEquals(3057, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-ru-dev");
    assertNotNull(qrels);
    assertEquals(1252, qrels.getQids().size());
    assertEquals(13100, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-sw-dev");
    assertNotNull(qrels);
    assertEquals(482, qrels.getQids().size());
    assertEquals(5092, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-te-dev");
    assertNotNull(qrels);
    assertEquals(828, qrels.getQids().size());
    assertEquals(1606, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-th-dev");
    assertNotNull(qrels);
    assertEquals(733, qrels.getQids().size());
    assertEquals(7573, getQrelsCount(qrels));

    qrels = Qrels.get("miracl-v1.0-zh-dev");
    assertNotNull(qrels);
    assertEquals(393, qrels.getQids().size());
    assertEquals(3928, getQrelsCount(qrels));
  }

  @Test
  public void testCIRAL() throws IOException {
    Qrels qrels;

    qrels = Qrels.get("ciral-v1.0-ha-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(165, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-so-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(187, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-sw-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(196, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-yo-dev");
    assertNotNull(qrels);
    assertEquals(10, qrels.getQids().size());
    assertEquals(185, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-ha-test-a");
    assertNotNull(qrels);
    assertEquals(80, qrels.getQids().size());
    assertEquals(1447, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-so-test-a");
    assertNotNull(qrels);
    assertEquals(99, qrels.getQids().size());
    assertEquals(1798, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-sw-test-a");
    assertNotNull(qrels);
    assertEquals(85, qrels.getQids().size());
    assertEquals(1656, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-yo-test-a");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(1921, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-ha-test-a-pools");
    assertNotNull(qrels);
    assertEquals(80, qrels.getQids().size());
    assertEquals(7288, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-so-test-a-pools");
    assertNotNull(qrels);
    assertEquals(99, qrels.getQids().size());
    assertEquals(9094, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-sw-test-a-pools");
    assertNotNull(qrels);
    assertEquals(85, qrels.getQids().size());
    assertEquals(8079, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-yo-test-a-pools");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(8311, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-ha-test-b");
    assertNotNull(qrels);
    assertEquals(312, qrels.getQids().size());
    assertEquals(5930, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-so-test-b");
    assertNotNull(qrels);
    assertEquals(239, qrels.getQids().size());
    assertEquals(4324, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-sw-test-b");
    assertNotNull(qrels);
    assertEquals(114, qrels.getQids().size());
    assertEquals(2175, getQrelsCount(qrels));

    qrels = Qrels.get("ciral-v1.0-yo-test-b");
    assertNotNull(qrels);
    assertEquals(554, qrels.getQids().size());
    assertEquals(10569, getQrelsCount(qrels));
  }

  @Test
  public void testDseQrels() throws IOException {
    Qrels qrels = Qrels.get("slidevqa");
    assertNotNull(qrels);
    assertEquals(2214, qrels.getQids().size());
    assertEquals(2786, getQrelsCount(qrels));
  }

  @Test
  public void testMMEBVisDocQrels() throws IOException {
    Qrels qrels = Qrels.get("mmeb-visdoc-ViDoRe_arxivqa-test");
    assertNotNull(qrels);
    assertEquals(500, qrels.getQids().size());
    assertEquals(500, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_docvqa-test");
    assertNotNull(qrels);
    assertEquals(451, qrels.getQids().size());
    assertEquals(500, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_infovqa-test");
    assertNotNull(qrels);
    assertEquals(494, qrels.getQids().size());
    assertEquals(500, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_shiftproject-test");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(100, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_syntheticDocQA_artificial_intelligence-test");
      assertNotNull(qrels);
      assertEquals(100, qrels.getQids().size());
      assertEquals(100, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_syntheticDocQA_energy-test");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(100, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_syntheticDocQA_government_reports-test");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(100, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_syntheticDocQA_healthcare_industry-test");
    assertNotNull(qrels);
    assertEquals(100, qrels.getQids().size());
    assertEquals(100, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_tabfquad-test");
    assertNotNull(qrels);
    assertEquals(280, qrels.getQids().size());
    assertEquals(280, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_tatdqa-test");
    assertNotNull(qrels);
    assertEquals(1646, qrels.getQids().size());
    assertEquals(1663, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_biomedical_lectures_v2-test");
    assertNotNull(qrels);
    assertEquals(640, qrels.getQids().size());
    assertEquals(2060, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_biomedical_lectures_v2_multilingual-test");
    assertNotNull(qrels);
    assertEquals(640, qrels.getQids().size());
    assertEquals(2060, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_economics_reports_v2-test");
    assertNotNull(qrels);
    assertEquals(232, qrels.getQids().size());
    assertEquals(3628, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_economics_reports_v2_multilingual-test");
    assertNotNull(qrels);
    assertEquals(232, qrels.getQids().size());
    assertEquals(3628, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_esg_reports_human_labeled_v2-test");
    assertNotNull(qrels);
    assertEquals(52, qrels.getQids().size());
    assertEquals(128, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_esg_reports_v2-test");
    assertNotNull(qrels);
    assertEquals(228, qrels.getQids().size());
    assertEquals(888, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoRe_esg_reports_v2_multilingual-test");
    assertNotNull(qrels);
    assertEquals(228, qrels.getQids().size());
    assertEquals(888, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_ArxivQA-train");
    assertNotNull(qrels);
    assertEquals(816, qrels.getQids().size());
    assertEquals(816, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_ChartQA-train");
    assertNotNull(qrels);
    assertEquals(63, qrels.getQids().size());
    assertEquals(63, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_InfoVQA-train");
    assertNotNull(qrels);
    assertEquals(718, qrels.getQids().size());
    assertEquals(718, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_MP-DocVQA-train");
    assertNotNull(qrels);
    assertEquals(591, qrels.getQids().size());
    assertEquals(591, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_PlotQA-train");
    assertNotNull(qrels);
    assertEquals(863, qrels.getQids().size());
    assertEquals(863, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-VisRAG_SlideVQA-train");
    assertNotNull(qrels);
    assertEquals(556, qrels.getQids().size());
    assertEquals(702, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoSeek-doc-test");
    assertNotNull(qrels);
    assertEquals(1142, qrels.getQids().size());
    assertEquals(21190, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-ViDoSeek-page-test");
    assertNotNull(qrels);
    assertEquals(1142, qrels.getQids().size());
    assertEquals(1142, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-MMLongBench-doc-test");
    assertNotNull(qrels);
    assertEquals(838, qrels.getQids().size());
    assertEquals(40850, getQrelsCount(qrels));

    qrels = Qrels.get("mmeb-visdoc-MMLongBench-page-test");
    assertNotNull(qrels);
    assertEquals(838, qrels.getQids().size());
    assertEquals(1574, getQrelsCount(qrels));
  }

  @Test
  public void testPathResolution() throws IOException {
    Path expected;
    Path produced;

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.cacm.txt");
    produced = Qrels.resolveQrelsPath("cacm");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.robust04.txt");
    produced = Qrels.resolveQrelsPath("qrels.robust04.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.msmarco-passage.dev-subset.txt");
    produced = Qrels.resolveQrelsPath("qrels.msmarco-passage.dev-subset.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.msmarco-v2-passage.dev2.txt");
    produced = Qrels.resolveQrelsPath("qrels.msmarco-v2-passage.dev2.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.miracl-v1.0-en-dev.tsv");
    produced = Qrels.resolveQrelsPath("qrels.miracl-v1.0-en-dev.tsv");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.covid-round3.txt");
    produced = Qrels.resolveQrelsPath("qrels.covid-round3.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);
    
    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.ciral-v1.0-yo-test-a-pools.tsv");
    produced = Qrels.resolveQrelsPath("qrels.ciral-v1.0-yo-test-a-pools.tsv");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.adhoc.151-200.txt");
    produced = Qrels.resolveQrelsPath("qrels.adhoc.151-200.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.microblog2012.txt");
    produced = Qrels.resolveQrelsPath("qrels.microblog2012.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getTopicsAndQrelsCachePath().resolve("qrels.terabyte04.701-750.txt");
    produced = Qrels.resolveQrelsPath("qrels.terabyte04.701-750.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    // Test for non valid paths
    expected = Path.of("thisdoesnotexist");
    produced = Qrels.resolveQrelsPath("thisdoesnotexist");
    assertNotNull(produced);
    assertEquals(expected, produced);
  }
}
