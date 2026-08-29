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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

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
    assertEquals(254, Qrels.names().size());
    assertFalse(Qrels.names().contains("this-qrels-name-does-not-exist"));
  }

  @Test
  public void testLocalBindings() throws IOException {
    // Canonical should be in names().
    assertTrue(Qrels.names().contains("dummy"));
    assertDummyQrels("dummy");

    // Aliases shouldn't be in names().
    assertFalse(Qrels.names().contains("dummy.1"));
    assertFalse(Qrels.names().contains("dummy.2"));

    // But they should be available via get.
    assertDummyQrels("dummy.1");
    assertDummyQrels("dummy.2");
  }

  @Test
  public void testLocalAliasesExtendExternalAliases() throws IOException {
    Qrels canonical = Qrels.get("msmarco-doc-dev");
    assertEquals(canonical.path(), Qrels.get("msmarco-doc.dev").path());
    assertEquals(canonical.path(), Qrels.get("dummy.msmarco-doc-dev").path());
  }

  @Test
  public void testAdhocAliases() throws IOException {
    Qrels canonical = Qrels.get("adhoc.351-400");
    assertTrue(Qrels.names().contains("adhoc.351-400"));
    assertFalse(Qrels.names().contains("trec7-adhoc"));
    assertEquals(canonical.path(), Qrels.get("trec7-adhoc").path());

    canonical = Qrels.get("adhoc.401-450");
    assertTrue(Qrels.names().contains("adhoc.401-450"));
    assertFalse(Qrels.names().contains("trec8-adhoc"));
    assertEquals(canonical.path(), Qrels.get("trec8-adhoc").path());
  }

  private void assertDummyQrels(String name) throws IOException {
    Qrels qrels = assertQrels(name, 2, 4);
    assertEquals(name, qrels.name());
    assertTrue(qrels.isDocJudged("1", "DOC1"));
    assertTrue(qrels.isDocJudged("1", "DOC2"));
    assertEquals(1, qrels.getRelevanceGrade("1", "DOC1"));
    assertEquals(0, qrels.getRelevanceGrade("1", "DOC2"));
    assertTrue(qrels.isDocJudged("2", "DOC3"));
    assertTrue(qrels.isDocJudged("2", "DOC4"));
    assertEquals(1, qrels.getRelevanceGrade("2", "DOC3"));
    assertEquals(0, qrels.getRelevanceGrade("2", "DOC4"));
  }

  @Test(expected = IOException.class)
  public void testFileNotFound() throws IOException {
    // Purposely read non-existent file.
    Qrels.loadFromFile("qrels.xxx.txt");
  }

  @Test(expected = ArrayIndexOutOfBoundsException.class)
  public void testNonvalidQrels() throws IOException {
    // Purposely read non-valid qrels.
    Qrels.loadFromFile("src/test/resources/sample_topics/Trec");
  }

  @Test
  public void testLoadFromFile() throws IOException {
    Qrels qrels = Qrels.loadFromFile("src/test/resources/sample_qrels/Trec");
    assertNotNull(qrels);
    assertEquals("Trec", qrels.name());
    assertEquals(Path.of("src/test/resources/sample_qrels/Trec"), qrels.path());
    assertEquals(1, qrels.getQids().size());
    assertEquals(3, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("1", "TREC_DOC_1"));
    assertEquals(1, qrels.getRelevanceGrade("1", "DOC222"));
    assertEquals(1, qrels.getRelevanceGrade("1", "WSJ_1"));
    assertTrue(qrels.isDocJudged("1", "TREC_DOC_1"));
    assertTrue(qrels.isDocJudged("1", "DOC222"));
    assertEquals(3, qrels.getDocMap("1").size());
    assertEquals(0, qrels.getRelevanceGrade("1", "DOC333"));
    assertEquals(0, qrels.getRelevanceGrade("2", "DOC222"));
    assertNull(qrels.getDocMap("2"));
    assertFalse(qrels.isDocJudged("1", "DOC333"));
    assertFalse(qrels.isDocJudged("2", "DOC222"));
  }

  @Test
  public void testLoadFromFileMicroblog() throws IOException {
    Qrels qrels = Qrels.loadFromFile("src/test/resources/sample_qrels/Microblog");
    assertNotNull(qrels);
    assertEquals(1, qrels.getQids().size());
    assertEquals(6, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("1", "1"));
    assertEquals(1, qrels.getRelevanceGrade("1", "3"));
    assertEquals(1, qrels.getRelevanceGrade("1", "8"));
    assertEquals(1, qrels.getRelevanceGrade("1", "10"));
    assertTrue(qrels.isDocJudged("1", "1"));
    assertTrue(qrels.isDocJudged("1", "3"));
    assertEquals(6, qrels.getDocMap("1").size());
    assertEquals(0, qrels.getRelevanceGrade("1", "2"));
    assertEquals(0, qrels.getRelevanceGrade("2", "3"));
    assertNull(qrels.getDocMap("2"));
    assertFalse(qrels.isDocJudged("1", "2"));
    assertFalse(qrels.isDocJudged("2", "3"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testUnknownQrelsName() throws IOException {
    Qrels.get("this-qrels-name-does-not-exist");
  }

  @Test
  public void testName() throws IOException {
    Qrels qrels = assertQrels("cacm", 52, 796);
    assertEquals("cacm", qrels.name());

    qrels = assertQrels("beir-v1.0.0-arguana-test", 1406, 1406);
    assertEquals("beir-v1.0.0-arguana-test", qrels.name());
  }

  @Test
  public void testCacm() throws IOException {
    Qrels qrels = assertQrels("cacm", 52, 796);
    assertEquals("cacm", qrels.name());
    assertEquals(1, qrels.getRelevanceGrade("1", "CACM-1410"));
    assertEquals(0, qrels.getRelevanceGrade("1", "CACM-1410x")); // non-existent docid
    assertEquals(0, qrels.getRelevanceGrade("xxx", "CACM-1410"));  // non-existent topic
    assertTrue(qrels.isDocJudged("1", "CACM-1410"));
    assertNull(qrels.getDocMap("xxx"));
  }

  @Test
  public void testFeverDev() throws IOException {
    Qrels qrels = assertQrels("fever.dev", 6666, 8079);
    assertEquals(2, qrels.getRelevanceGrade("137334", "Soul_Food_-LRB-film-RRB-"));
  }

  @Test
  public void testNanoKnowNqSupported() throws IOException {
    Qrels qrels = assertQrels("nanoknow-v1.0-nq.supported", 2389, 56958);
    assertEquals(1, qrels.getRelevanceGrade("0", "shard_01177_50695"));
  }

  @Test
  public void testNanoKnowSquadSupported() throws IOException {
    Qrels qrels = assertQrels("nanoknow-v1.0-squad.supported", 7490, 151675);
    assertEquals(1, qrels.getRelevanceGrade("0", "shard_01394_6521"));
  }

  @Test
  public void testWeb51To100() throws IOException {
    Qrels qrels = assertQrels("web.51-100", 48, 25329);
    assertEquals(1, qrels.getRelevanceGrade("51", "clueweb09-en0001-01-17957"));
  }

  @Test
  public void testWeb101To150() throws IOException {
    Qrels qrels = assertQrels("web.101-150", 50, 19381);
    assertEquals(2, qrels.getRelevanceGrade("101", "clueweb09-en0076-79-19134"));
  }

  @Test
  public void testWeb151To200() throws IOException {
    Qrels qrels = assertQrels("web.151-200", 50, 16055);
    assertEquals(1, qrels.getRelevanceGrade("151", "clueweb09-en0000-00-17600"));
  }

  @Test
  public void testWeb201To250() throws IOException {
    Qrels qrels = assertQrels("web.201-250", 50, 14474);
    assertEquals(1, qrels.getRelevanceGrade("201", "clueweb12-0000tw-05-12114"));
  }

  @Test
  public void testWeb251To300() throws IOException {
    Qrels qrels = assertQrels("web.251-300", 50, 14432);
    assertEquals(1, qrels.getRelevanceGrade("251", "clueweb12-0000tw-34-04382"));
  }

  @Test
  public void testRobust04() throws IOException {
    Qrels qrels = assertQrels("robust04", 249, 311410);
    assertEquals(1, qrels.getRelevanceGrade("301", "FBIS3-10082"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137"));
    assertEquals(0, qrels.getRelevanceGrade("700", "LA123090-0137x")); // non-existent docid
    assertEquals(0, qrels.getRelevanceGrade("xxx", "LA123090-0137"));  // non-existent topic
    assertTrue(qrels.isDocJudged("301", "FBIS3-10082"));
    assertNull(qrels.getDocMap("xxx"));
  }

  @Test
  public void testRobust05() throws IOException {
    Qrels qrels = assertQrels("robust05", 50, 37798);
    assertEquals(2, qrels.getRelevanceGrade("303", "APW19980609.1531"));
    assertEquals(0, qrels.getRelevanceGrade("689", "XIE20000925.0055"));
  }

  @Test
  public void testTrec19DLDoc() throws IOException {
    Qrels qrels = assertQrels("dl19-doc", 43, 16258);
    assertEquals(0, qrels.getRelevanceGrade("19335", "D1035833"));
    assertEquals(0, qrels.getRelevanceGrade("1133167", "D984590"));
  }

  @Test
  public void testTrec19DLPassage() throws IOException {
    Qrels qrels = assertQrels("dl19-passage", 43, 9260);
    assertEquals(0, qrels.getRelevanceGrade("19335", "1017759"));
    assertEquals(1, qrels.getRelevanceGrade("1133167", "8804478"));
  }

  @Test
  public void testTrec20DLDoc() throws IOException {
    Qrels qrels = assertQrels("dl20-doc", 45, 9098);
    assertEquals(3, qrels.getRelevanceGrade("42255", "D1884223"));
    assertEquals(3, qrels.getRelevanceGrade("1136962", "D96741"));
  }

  @Test
  public void testTrec20DLPassage() throws IOException {
    Qrels qrels = assertQrels("dl20-passage", 54, 11386);
    assertEquals(2, qrels.getRelevanceGrade("23849", "1020327"));
    assertEquals(1, qrels.getRelevanceGrade("1136962", "937258"));
  }

  @Test
  public void testTrec21DLDoc() throws IOException {
    Qrels qrels = assertQrels("dl21-doc", 57, 13058);
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_doc_01_1320020407"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_doc_59_863449044"));
  }

  @Test
  public void testTrec21DLPassage() throws IOException {
    Qrels qrels = assertQrels("dl21-passage", 53, 10828);
    assertEquals(3, qrels.getRelevanceGrade("2082", "msmarco_passage_02_179207466"));
    assertEquals(1, qrels.getRelevanceGrade("1129560", "msmarco_passage_67_937656589"));
  }

  @Test
  public void testTrec21DLDocMsMarcoV21() throws IOException {
    Qrels qrels = assertQrels("dl21-doc-msmarco-v2.1", 57, 10973);
    assertEquals(2, qrels.getRelevanceGrade("2082", "msmarco_v2.1_doc_01_1281570012"));
    assertEquals(2, qrels.getRelevanceGrade("1128632", "msmarco_v2.1_doc_17_481617788"));
  }

  @Test
  public void testTrec22DLDoc() throws IOException {
    Qrels qrels = assertQrels("dl22-doc", 76, 369638);
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_doc_00_928744703"));
    assertEquals(1, qrels.getRelevanceGrade("2056323", "msmarco_doc_59_419476385"));
  }

  @Test
  public void testTrec22DLPassage() throws IOException {
    Qrels qrels = assertQrels("dl22-passage", 76, 386416);
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_passage_00_491585864"));
    assertEquals(1, qrels.getRelevanceGrade("2056323", "msmarco_passage_68_715747739"));
  }

  @Test
  public void testTrec22DLDocMsMarcoV21() throws IOException {
    Qrels qrels = assertQrels("dl22-doc-msmarco-v2.1", 76, 349541);
    assertEquals(1, qrels.getRelevanceGrade("2000511", "msmarco_v2.1_doc_00_896525856"));
    assertEquals(2, qrels.getRelevanceGrade("2056158", "msmarco_v2.1_doc_06_934688453"));
  }

  @Test
  public void testTrec23DLDoc() throws IOException {
    Qrels qrels = assertQrels("dl23-doc", 82, 18034);
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_doc_00_1413652624"));
    assertEquals(3, qrels.getRelevanceGrade("3100922", "msmarco_doc_16_3928760942"));
  }

  @Test
  public void testTrec23DLPassage() throws IOException {
    Qrels qrels = assertQrels("dl23-passage", 82, 22327);
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_passage_00_729315698"));
    assertEquals(2, qrels.getRelevanceGrade("3100922", "msmarco_passage_22_487548813"));
  }

  @Test
  public void testTrec23DLDocMsMarcoV21() throws IOException {
    Qrels qrels = assertQrels("dl23-doc-msmarco-v2.1", 82, 15995);
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_v2.1_doc_00_1372241967"));
    assertEquals(2, qrels.getRelevanceGrade("3100922", "msmarco_v2.1_doc_19_1982402861"));
  }

  @Test
  public void testTREC24_RAG_RAGGY_DEV() throws IOException {
    Qrels qrels = assertQrels("rag24.raggy-dev", 120, 147328);
    assertEquals(1, qrels.getRelevanceGrade("2001010", "msmarco_v2.1_doc_00_1372241967"));
    assertEquals(1, qrels.getRelevanceGrade("253263", "msmarco_v2.1_doc_46_843492186"));
  }

  @Test
  public void testTREC24_RAG_UMBRELA() throws IOException {
    Qrels qrels = assertQrels("rag24.test-umbrela", 301, 108479);
    assertEquals(1, qrels.getRelevanceGrade("2024-145979", "msmarco_v2.1_doc_25_771726319#13_1477564195"));
    assertEquals(1, qrels.getRelevanceGrade("2024-216592", "msmarco_v2.1_doc_52_1092442741#3_2165187686"));
  }

  @Test
  public void testTREC24_RAG() throws IOException {
    Qrels qrels = assertQrels("rag24.test", 89, 20429);
    assertEquals(0, qrels.getRelevanceGrade("2024-145979", "msmarco_v2.1_doc_00_125364462#6_229054655"));
    assertEquals(1, qrels.getRelevanceGrade("2024-96359", "msmarco_v2.1_doc_54_724887112#1_1700994504"));
  }

  @Test
  public void testTREC25_RAG_UMBRELA() throws IOException {
    Qrels qrels = assertQrels("rag25.test-umbrela2", 22, 10284);
    assertEquals(1, qrels.getRelevanceGrade("200", "msmarco_v2.1_doc_10_1630045707#13_2668822206"));
    assertEquals(1, qrels.getRelevanceGrade("31", "msmarco_v2.1_doc_20_1589508824#12_3490034638"));
  }

  @Test
  public void testTREC25_RAG() throws IOException {
    Qrels qrels = assertQrels("rag25.test", 22, 10284);
    assertEquals(2, qrels.getRelevanceGrade("200", "msmarco_v2.1_doc_10_1630045707#13_2668822206"));
    assertEquals(0, qrels.getRelevanceGrade("31", "msmarco_v2.1_doc_20_1589508824#12_3490034638"));
  }

  @Test
  public void testMsmarcoDocDev() throws IOException {
    Qrels qrels = assertQrels("msmarco-doc-dev", 5193, 5193);
    assertEquals(1, qrels.getRelevanceGrade("2", "D1650436"));
    assertEquals(1, qrels.getRelevanceGrade("1102400", "D677570"));
  }

  @Test
  public void testMsmarcoDocDevAlias() throws IOException {
    Qrels qrels = assertQrels("msmarco-doc.dev", 5193, 5193);
    assertEquals("msmarco-doc.dev", qrels.name());
  }

  @Test
  public void testMsmarcoPassageDevSubset() throws IOException {
    Qrels qrels = assertQrels("msmarco-passage-dev", 6980, 7437);
    assertEquals(1, qrels.getRelevanceGrade("300674", "7067032"));
    assertEquals(1, qrels.getRelevanceGrade("195199", "8009377"));
  }

  @Test
  public void testMsmarcoV2DocDevMsMarcoV21() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2.1-doc.dev", 4552, 4702);
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_v2.1_doc_17_1968189952"));
    assertEquals(1, qrels.getRelevanceGrade("999897", "msmarco_v2.1_doc_46_191673440"));
  }

  @Test
  public void testMsmarcoV2DocDev2MsMarcoV21() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2.1-doc.dev2", 5000, 5177);
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_v2.1_doc_08_69146701"));
    assertEquals(1, qrels.getRelevanceGrade("999659", "msmarco_v2.1_doc_08_1247437925"));
  }

  @Test
  public void testMsmarcoV2DocPassage() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-passage-dev", 3903, 4009);
    assertEquals(1, qrels.getRelevanceGrade("763878", "msmarco_passage_33_459057644"));
    assertEquals(1, qrels.getRelevanceGrade("1091692", "msmarco_passage_23_330102695"));
  }

  @Test
  public void testMsmarcoV2DocPassageAlias() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-passage.dev", 3903, 4009);
    assertEquals("msmarco-v2-passage.dev", qrels.name());
  }

  @Test
  public void testMsmarcoV2DocPassage2() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-passage-dev2", 4281, 4411);
    assertEquals(1, qrels.getRelevanceGrade("419507", "msmarco_passage_04_254301507"));
    assertEquals(1, qrels.getRelevanceGrade("961297", "msmarco_passage_18_858458289"));
  }

  @Test
  public void testMsmarcoV2DocPassage2Alias() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-passage.dev2", 4281, 4411);
    assertEquals("msmarco-v2-passage.dev2", qrels.name());
  }

  @Test
  public void testMsmarcoV2DocDev() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-doc-dev", 4552, 4702);
    assertEquals(1, qrels.getRelevanceGrade("1000000", "msmarco_doc_17_2560009121"));
    assertEquals(1, qrels.getRelevanceGrade("999942", "msmarco_doc_06_956348348"));
  }

  @Test
  public void testMsmarcoV2DocDevAlias() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-doc.dev", 4552, 4702);
    assertEquals("msmarco-v2-doc.dev", qrels.name());
  }

  @Test
  public void testMsmarcoV2DocDev2() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-doc-dev2", 5000, 5178);
    assertEquals(1, qrels.getRelevanceGrade("1000202", "msmarco_doc_08_73026062"));
    assertEquals(1, qrels.getRelevanceGrade("999937", "msmarco_doc_05_319743607"));
  }

  @Test
  public void testMsmarcoV2DocDev2Alias() throws IOException {
    Qrels qrels = assertQrels("msmarco-v2-doc.dev2", 5000, 5178);
    assertEquals("msmarco-v2-doc.dev2", qrels.name());
  }

  @Test
  public void testCore17() throws IOException {
    Qrels qrels = assertQrels("core17", 50, 30030);
    assertEquals(1, qrels.getRelevanceGrade("307", "1001536"));
    assertEquals(0, qrels.getRelevanceGrade("690", "996059"));
  }

  @Test
  public void testCore18() throws IOException {
    Qrels qrels = assertQrels("core18", 50, 26233);
    assertEquals(0, qrels.getRelevanceGrade("321", "004c6120d0aa69da29cc045da0562168"));
    assertEquals(0, qrels.getRelevanceGrade("825", "ff3a25b0-0ba4-11e4-8341-b8072b1e7348"));
  }

  @Test
  public void testCar15() throws IOException {
    Qrels qrels = assertQrels("car17v1.5-benchmarkY1test", 2125, 5820);
    assertEquals(1, qrels.getRelevanceGrade("Aftertaste/Aftertaste%20processing%20in%20the%20cerebral%20cortex", "38c1bd25ddca2705164677a3f598c46df85afba7"));
    assertEquals(1, qrels.getRelevanceGrade("Yellowstone%20National%20Park/Recreation", "e80b5185da1493edde41bea19a389a3f62167369"));
  }

  @Test
  public void testCar20() throws IOException {
    Qrels qrels = assertQrels("car17v2.0-benchmarkY1test", 2254, 6192);
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Aftertaste", "327cca6c4d38953196fa6789f615546f03287b25"));
    assertEquals(1, qrels.getRelevanceGrade("enwiki:Yellowstone%20National%20Park/Recreation", "b812fca195f74f8c563db4262260554fe3ff3731"));
  }

  @Test
  public void testTrec2018BL() throws IOException {
    Qrels qrels = assertQrels("trec2018-bl", 50, 8508);
    assertEquals(16, qrels.getRelevanceGrade("321", "00f57310e5c8ec7833d6756ba637332e"));
    assertEquals(0, qrels.getRelevanceGrade("825", "f66b624ba8689d704872fa776fb52860"));
  }

  @Test
  public void testTrec2019BL() throws IOException {
    Qrels qrels = assertQrels("trec2019-bl", 57, 15655);
    assertEquals(2, qrels.getRelevanceGrade("826", "0154349511cd8c49ab862d6cb0d8f6a8"));
    assertEquals(0, qrels.getRelevanceGrade("885", "fde80cb0-b4f0-11e2-bbf2-a6f9e9d79e19"));
  }

  @Test
  public void testTrec2020BL() throws IOException {
    Qrels qrels = assertQrels("trec2020-bl", 49, 17764);
    assertEquals(0, qrels.getRelevanceGrade("886", "00183d98-741b-11e5-8248-98e0f5a2e830"));
    assertEquals(0, qrels.getRelevanceGrade("935", "ff0a760128ecdbcc096cafc8cd553255"));
  }

  @Test
  public void testCovidRound1() throws IOException {
    Qrels qrels = assertQrels("covid-round1", 30, 8691);
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("30", "zn87f1lk"));
  }

  @Test
  public void testCovidRound2() throws IOException {
    Qrels qrels = assertQrels("covid-round2", 35, 12037);
    assertEquals(0, qrels.getRelevanceGrade("1", "08efpohc"));
    assertEquals(0, qrels.getRelevanceGrade("35", "zzmfhr2s"));
  }

  @Test
  public void testCovidRound3() throws IOException {
    Qrels qrels = assertQrels("covid-round3", 40, 12713);
    assertEquals(1, qrels.getRelevanceGrade("1", "0194oljo"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4() throws IOException {
    Qrels qrels = assertQrels("covid-round4", 45, 13262);
    assertEquals(2, qrels.getRelevanceGrade("1", "1c47w4q5"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidRound5() throws IOException {
    Qrels qrels = assertQrels("covid-round5", 50, 23151);
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testCovidRound3Cumulative() throws IOException {
    Qrels qrels = assertQrels("covid-round3-cumulative", 40, 33068);
    assertEquals(2, qrels.getRelevanceGrade("1", "010vptx3"));
    assertEquals(1, qrels.getRelevanceGrade("40", "zsx7wfyj"));
  }

  @Test
  public void testCovidRound4Cumulative() throws IOException {
    Qrels qrels = assertQrels("covid-round4-cumulative", 45, 46203);
    assertEquals(1, qrels.getRelevanceGrade("1", "00fmeepz"));
    assertEquals(2, qrels.getRelevanceGrade("45", "zzrsk1ls"));
  }

  @Test
  public void testCovidComplete() throws IOException {
    Qrels qrels = assertQrels("covid-complete", 50, 69318);
    assertEquals(2, qrels.getRelevanceGrade("1", "005b2j4b"));
    assertEquals(1, qrels.getRelevanceGrade("50", "zz8wvos9"));
  }

  @Test
  public void testNtcir8Zh() throws IOException {
    Qrels qrels = assertQrels("ntcir8-zh", 100, 110213);
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020106.0118"));
    assertEquals(0, qrels.getRelevanceGrade("ACLIA2-CS-0001", "XIN_CMN_20020107.0140"));
  }

  @Test
  public void testClef2006Fr() throws IOException {
    Qrels qrels = assertQrels("clef2006-fr", 49, 17882);
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940106.0082"));
    assertEquals(0, qrels.getRelevanceGrade("301-AH", "ATS.940112.0089"));
  }

  @Test
  public void testTrec2002Ar() throws IOException {
    Qrels qrels = assertQrels("trec2002-ar", 50, 38432);
    assertEquals(0, qrels.getRelevanceGrade("26", "19940515_AFP_ARB.0115"));
    assertEquals(1, qrels.getRelevanceGrade("26", "19941213_AFP_ARB.0159"));
  }

  @Test
  public void testMrTyDiAr() throws IOException {
    assertQrels("mrtydi-v1.1-arabic-train", 12377, 12377);
    assertQrels("mrtydi-v1.1-arabic-dev", 3115, 3115);
    assertQrels("mrtydi-v1.1-arabic-test", 1081, 1257);
  }

  @Test
  public void testMrTyDiBn() throws IOException {
    assertQrels("mrtydi-v1.1-bengali-train", 1713, 1719);
    assertQrels("mrtydi-v1.1-bengali-dev", 440, 443);
    assertQrels("mrtydi-v1.1-bengali-test", 111, 130);
  }

  @Test
  public void testMrTyDiEn() throws IOException {
    assertQrels("mrtydi-v1.1-english-train", 3547, 3547);
    assertQrels("mrtydi-v1.1-english-dev", 878, 878);
    assertQrels("mrtydi-v1.1-english-test", 744, 935);
  }

  @Test
  public void testMrTyDiFi() throws IOException {
    assertQrels("mrtydi-v1.1-finnish-train", 6561, 6561);
    assertQrels("mrtydi-v1.1-finnish-dev", 1738, 1738);
    assertQrels("mrtydi-v1.1-finnish-test", 1254, 1451);
  }

  @Test
  public void testMrTyDiId() throws IOException {
    assertQrels("mrtydi-v1.1-indonesian-train", 4902, 4902);
    assertQrels("mrtydi-v1.1-indonesian-dev", 1224, 1224);
    assertQrels("mrtydi-v1.1-indonesian-test", 829, 961);
  }

  @Test
  public void testMrTyDiJa() throws IOException {
    assertQrels("mrtydi-v1.1-japanese-train", 3697, 3697);
    assertQrels("mrtydi-v1.1-japanese-dev", 928, 928);
    assertQrels("mrtydi-v1.1-japanese-test", 720, 923);
  }

  @Test
  public void testMrTyDiKo() throws IOException {
    assertQrels("mrtydi-v1.1-korean-train", 1295, 1317);
    assertQrels("mrtydi-v1.1-korean-dev", 303, 307);
    assertQrels("mrtydi-v1.1-korean-test", 421, 492);
  }

  @Test
  public void testMrTyDiRu() throws IOException {
    assertQrels("mrtydi-v1.1-russian-train", 5366, 5366);
    assertQrels("mrtydi-v1.1-russian-dev", 1375, 1375);
    assertQrels("mrtydi-v1.1-russian-test", 995, 1168);
  }

  @Test
  public void testMrTyDiSw() throws IOException {
    assertQrels("mrtydi-v1.1-swahili-train", 2072, 2401);
    assertQrels("mrtydi-v1.1-swahili-dev", 526, 623);
    assertQrels("mrtydi-v1.1-swahili-test", 670, 743);
  }

  @Test
  public void testMrTyDiTe() throws IOException {
    assertQrels("mrtydi-v1.1-telugu-train", 3880, 3880);
    assertQrels("mrtydi-v1.1-telugu-dev", 983, 983);
    assertQrels("mrtydi-v1.1-telugu-test", 646, 677);
    // The value 677 differs from Mr. TyDi paper.
    // The paper reported 664, which is the qrel size before fixing the document slicing bug.
    // 677 should be the correct number.
  }

  @Test
  public void testMrTyDiTh() throws IOException {
    assertQrels("mrtydi-v1.1-thai-train", 3319, 3360);
    assertQrels("mrtydi-v1.1-thai-dev", 807, 817);
    assertQrels("mrtydi-v1.1-thai-test", 1190, 1368);
  }

  @Test
  public void testBRIGHT() throws IOException {
    assertQrels("bright-biology", 103, 372);
    assertQrels("bright-earth-science", 116, 585);
    assertQrels("bright-economics", 103, 800);
    assertQrels("bright-psychology", 101, 692);
    assertQrels("bright-robotics", 101, 520);
    assertQrels("bright-stackoverflow", 117, 478);
    assertQrels("bright-sustainable-living", 108, 576);
    assertQrels("bright-pony", 112, 2219);
    assertQrels("bright-leetcode", 142, 262);
    assertQrels("bright-aops", 111, 524);
    assertQrels("bright-theoremqa-theorems", 76, 151);
    assertQrels("bright-theoremqa-questions", 194, 439);
  }

  @Test
  public void testBEIR() throws IOException {
    assertQrels("beir-v1.0.0-trec-covid-test", 50, 66334);
    assertQrels("beir-v1.0.0-bioasq-test", 500, 2359);
    assertQrels("beir-v1.0.0-nfcorpus-test", 323, 12334);
    assertQrels("beir-v1.0.0-nq-test", 3452, 4201);
    assertQrels("beir-v1.0.0-hotpotqa-test", 7405, 14810);
    assertQrels("beir-v1.0.0-fiqa-test", 648, 1706);
    assertQrels("beir-v1.0.0-signal1m-test", 97, 1899);
    assertQrels("beir-v1.0.0-trec-news-test", 57, 15655);
    assertQrels("beir-v1.0.0-robust04-test", 249, 311410);
    assertQrels("beir-v1.0.0-arguana-test", 1406, 1406);
    assertQrels("beir-v1.0.0-webis-touche2020-test", 49, 932);
    assertQrels("beir-v1.0.0-cqadupstack-android-test", 699, 1696);
    assertQrels("beir-v1.0.0-cqadupstack-english-test", 1570, 3765);
    assertQrels("beir-v1.0.0-cqadupstack-gaming-test", 1595, 2263);
    assertQrels("beir-v1.0.0-cqadupstack-gis-test", 885, 1114);
    assertQrels("beir-v1.0.0-cqadupstack-mathematica-test", 804, 1358);
    assertQrels("beir-v1.0.0-cqadupstack-physics-test", 1039, 1933);
    assertQrels("beir-v1.0.0-cqadupstack-programmers-test", 876, 1675);
    assertQrels("beir-v1.0.0-cqadupstack-stats-test", 652, 913);
    assertQrels("beir-v1.0.0-cqadupstack-tex-test", 2906, 5154);
    assertQrels("beir-v1.0.0-cqadupstack-unix-test", 1072, 1693);
    assertQrels("beir-v1.0.0-cqadupstack-webmasters-test", 506, 1395);
    assertQrels("beir-v1.0.0-cqadupstack-wordpress-test", 541, 744);
    assertQrels("beir-v1.0.0-quora-test", 10000, 15675);
    assertQrels("beir-v1.0.0-dbpedia-entity-test", 400, 43515);
    assertQrels("beir-v1.0.0-scidocs-test", 1000, 29928);
    assertQrels("beir-v1.0.0-fever-test", 6666, 7937);
    assertQrels("beir-v1.0.0-climate-fever-test", 1535, 4681);
    assertQrels("beir-v1.0.0-scifact-test", 300, 339);
  }

  @Test
  public void testHC4() throws IOException {
    assertQrels("hc4-v1.0-ru-dev", 4, 265);
    assertQrels("hc4-v1.0-ru-test", 50, 2970);
    assertQrels("hc4-v1.0-fa-dev", 10, 565);
    assertQrels("hc4-v1.0-fa-test", 50, 2522);
    assertQrels("hc4-v1.0-zh-dev", 10, 466);
    assertQrels("hc4-v1.0-zh-test", 50, 2751);
  }

  @Test
  public void testNeuClir2022() throws IOException {
    assertQrels("neuclir22-fa", 46, 34174);
    assertQrels("neuclir22-ru", 45, 33006);
    assertQrels("neuclir22-zh", 49, 36575);
  }

  @Test
  public void testHc4NeuClir2022() throws IOException {
    assertQrels("hc4-neuclir22-fa-test", 50, 2041);
    assertQrels("hc4-neuclir22-ru-test", 50, 625);
    assertQrels("hc4-neuclir22-zh-test", 60, 2573);
  }

  @Test
  public void testMIRACL() throws IOException {
    assertQrels("miracl-v1.0-ar-dev", 2896, 29197);
    assertQrels("miracl-v1.0-bn-dev", 411, 4206);
    assertQrels("miracl-v1.0-en-dev", 799, 8350);
    assertQrels("miracl-v1.0-es-dev", 648, 6443);
    assertQrels("miracl-v1.0-fa-dev", 632, 6571);
    assertQrels("miracl-v1.0-fi-dev", 1271, 12008);
    assertQrels("miracl-v1.0-fr-dev", 343, 3429);
    assertQrels("miracl-v1.0-hi-dev", 350, 3494);
    assertQrels("miracl-v1.0-id-dev", 960, 9668);
    assertQrels("miracl-v1.0-ja-dev", 860, 8354);
    assertQrels("miracl-v1.0-ko-dev", 213, 3057);
    assertQrels("miracl-v1.0-ru-dev", 1252, 13100);
    assertQrels("miracl-v1.0-sw-dev", 482, 5092);
    assertQrels("miracl-v1.0-te-dev", 828, 1606);
    assertQrels("miracl-v1.0-th-dev", 733, 7573);
    assertQrels("miracl-v1.0-zh-dev", 393, 3928);
  }

  @Test
  public void testCIRAL() throws IOException {
    assertQrels("ciral-v1.0-ha-dev", 10, 165);
    assertQrels("ciral-v1.0-so-dev", 10, 187);
    assertQrels("ciral-v1.0-sw-dev", 10, 196);
    assertQrels("ciral-v1.0-yo-dev", 10, 185);
    assertQrels("ciral-v1.0-ha-test-a", 80, 1447);
    assertQrels("ciral-v1.0-so-test-a", 99, 1798);
    assertQrels("ciral-v1.0-sw-test-a", 85, 1656);
    assertQrels("ciral-v1.0-yo-test-a", 100, 1921);
    assertQrels("ciral-v1.0-ha-test-a-pools", 80, 7288);
    assertQrels("ciral-v1.0-so-test-a-pools", 99, 9094);
    assertQrels("ciral-v1.0-sw-test-a-pools", 85, 8079);
    assertQrels("ciral-v1.0-yo-test-a-pools", 100, 8311);
    assertQrels("ciral-v1.0-ha-test-b", 312, 5930);
    assertQrels("ciral-v1.0-so-test-b", 239, 4324);
    assertQrels("ciral-v1.0-sw-test-b", 114, 2175);
    assertQrels("ciral-v1.0-yo-test-b", 554, 10569);
  }

  @Test
  public void testDseQrels() throws IOException {
    assertQrels("slidevqa", 2214, 2786);
  }

  @Test
  public void testMMEBVisDocQrels() throws IOException {
    assertQrels("mmeb-visdoc-ViDoRe_arxivqa-test", 500, 500);
    assertQrels("mmeb-visdoc-ViDoRe_docvqa-test", 451, 500);
    assertQrels("mmeb-visdoc-ViDoRe_infovqa-test", 494, 500);
    assertQrels("mmeb-visdoc-ViDoRe_shiftproject-test", 100, 100);
    assertQrels("mmeb-visdoc-ViDoRe_syntheticDocQA_artificial_intelligence-test", 100, 100);
    assertQrels("mmeb-visdoc-ViDoRe_syntheticDocQA_energy-test", 100, 100);
    assertQrels("mmeb-visdoc-ViDoRe_syntheticDocQA_government_reports-test", 100, 100);
    assertQrels("mmeb-visdoc-ViDoRe_syntheticDocQA_healthcare_industry-test", 100, 100);
    assertQrels("mmeb-visdoc-ViDoRe_tabfquad-test", 280, 280);
    assertQrels("mmeb-visdoc-ViDoRe_tatdqa-test", 1646, 1663);
    assertQrels("mmeb-visdoc-ViDoRe_biomedical_lectures_v2-test", 640, 2060);
    assertQrels("mmeb-visdoc-ViDoRe_biomedical_lectures_v2_multilingual-test", 640, 2060);
    assertQrels("mmeb-visdoc-ViDoRe_economics_reports_v2-test", 232, 3628);
    assertQrels("mmeb-visdoc-ViDoRe_economics_reports_v2_multilingual-test", 232, 3628);
    assertQrels("mmeb-visdoc-ViDoRe_esg_reports_human_labeled_v2-test", 52, 128);
    assertQrels("mmeb-visdoc-ViDoRe_esg_reports_v2-test", 228, 888);
    assertQrels("mmeb-visdoc-ViDoRe_esg_reports_v2_multilingual-test", 228, 888);
    assertQrels("mmeb-visdoc-VisRAG_ArxivQA-train", 816, 816);
    assertQrels("mmeb-visdoc-VisRAG_ChartQA-train", 63, 63);
    assertQrels("mmeb-visdoc-VisRAG_InfoVQA-train", 718, 718);
    assertQrels("mmeb-visdoc-VisRAG_MP-DocVQA-train", 591, 591);
    assertQrels("mmeb-visdoc-VisRAG_PlotQA-train", 863, 863);
    assertQrels("mmeb-visdoc-VisRAG_SlideVQA-train", 556, 702);
    assertQrels("mmeb-visdoc-ViDoSeek-doc-test", 1142, 21190);
    assertQrels("mmeb-visdoc-ViDoSeek-page-test", 1142, 1142);
    assertQrels("mmeb-visdoc-MMLongBench-doc-test", 838, 40850);
    assertQrels("mmeb-visdoc-MMLongBench-page-test", 838, 1574);
  }

  @Test
  public void testAdhocQrels() throws IOException {
    assertQrels("adhoc.51-100", 50, 89179);
    assertQrels("adhoc.101-150", 50, 62620);
    assertQrels("adhoc.151-200", 50, 97319);
    assertQrels("adhoc.451-550", 100, 140470);
  }

  @Test
  public void testAtomicValidationQrels() throws IOException {
    assertQrels("atomic.validation.i2t", 16131, 17801);
    assertQrels("atomic.validation.t2i", 17173, 17801);
  }

  @Test
  public void testFireQrels() throws IOException {
    assertQrels("fire12bn.176-225", 50, 44823);
    assertQrels("fire12en.176-225", 50, 36499);
    assertQrels("fire12hi.176-225", 50, 39827);
  }

  @Test
  public void testMBeirQrels() throws IOException {
    assertQrels("m-beir-cirr-task7", 4170, 4216);
    assertQrels("m-beir-edis-task2", 3241, 8341);
    assertQrels("m-beir-fashion200k-task0", 1719, 4847);
    assertQrels("m-beir-fashion200k-task3", 4889, 4889);
    assertQrels("m-beir-fashioniq-task7", 6003, 6014);
    assertQrels("m-beir-infoseek-task6", 11323, 73869);
    assertQrels("m-beir-infoseek-task8", 17593, 131376);
    assertQrels("m-beir-mscoco-task0", 24809, 24989);
    assertQrels("m-beir-mscoco-task3", 5000, 24989);
    assertQrels("m-beir-nights-task4", 2120, 2120);
    assertQrels("m-beir-oven-task6", 50004, 492654);
    assertQrels("m-beir-oven-task8", 14741, 261258);
    assertQrels("m-beir-visualnews-task0", 19995, 20000);
    assertQrels("m-beir-visualnews-task3", 20000, 20000);
    assertQrels("m-beir-webqa-task1", 2455, 5002);
    assertQrels("m-beir-webqa-task2", 2511, 3627);
  }

  @Test
  public void testMicroblogQrels() throws IOException {
    assertQrels("microblog2011", 49, 60129);
    assertQrels("microblog2012", 59, 73073);
    assertQrels("microblog2013", 60, 71279);
    assertQrels("microblog2014", 55, 57985);
  }

  @Test
  public void testAdditionalMiraclQrels() throws IOException {
    assertQrels("miracl-v1.0-ar-train", 3495, 25382);
    assertQrels("miracl-v1.0-bn-train", 1631, 16754);
    assertQrels("miracl-v1.0-de-dev", 305, 3144);
    assertQrels("miracl-v1.0-en-train", 2863, 29416);
    assertQrels("miracl-v1.0-es-train", 2162, 21531);
    assertQrels("miracl-v1.0-fa-train", 2107, 21844);
    assertQrels("miracl-v1.0-fi-train", 2897, 20350);
    assertQrels("miracl-v1.0-fr-train", 1143, 11426);
    assertQrels("miracl-v1.0-hi-train", 1169, 11668);
    assertQrels("miracl-v1.0-id-train", 4071, 41358);
    assertQrels("miracl-v1.0-ja-train", 3477, 34387);
    assertQrels("miracl-v1.0-ko-train", 868, 12767);
    assertQrels("miracl-v1.0-ru-train", 4683, 33921);
    assertQrels("miracl-v1.0-sw-train", 1901, 9359);
    assertQrels("miracl-v1.0-te-train", 3452, 18608);
    assertQrels("miracl-v1.0-th-train", 2972, 21293);
    assertQrels("miracl-v1.0-yo-dev", 119, 1188);
    assertQrels("miracl-v1.0-zh-train", 1312, 13113);
  }

  @Test
  public void testTerabyteQrels() throws IOException {
    assertQrels("terabyte04.701-750", 49, 58077);
    assertQrels("terabyte05.751-800", 50, 45291);
    assertQrels("terabyte06.801-850", 50, 31984);
  }

  private Qrels assertQrels(String name, int qids, int judgments) throws IOException {
    Qrels qrels = Qrels.get(name);
    assertNotNull(qrels);
    assertEquals(qids, qrels.getQids().size());
    assertEquals(judgments, getQrelsCount(qrels));
    return qrels;
  }

  @Test
  public void testPathResolution() throws IOException {
    Path expected;
    Path produced;

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.cacm.txt");
    produced = Qrels.resolveQrelsPath("cacm");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.robust04.txt");
    produced = Qrels.resolveQrelsPath("qrels.robust04.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.msmarco-passage.dev-subset.txt");
    produced = Qrels.resolveQrelsPath("qrels.msmarco-passage.dev-subset.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.msmarco-v2-passage.dev2.txt");
    produced = Qrels.resolveQrelsPath("qrels.msmarco-v2-passage.dev2.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.miracl-v1.0-en-dev.tsv");
    produced = Qrels.resolveQrelsPath("qrels.miracl-v1.0-en-dev.tsv");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.covid-round3.txt");
    produced = Qrels.resolveQrelsPath("qrels.covid-round3.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.ciral-v1.0-yo-test-a-pools.tsv");
    produced = Qrels.resolveQrelsPath("qrels.ciral-v1.0-yo-test-a-pools.tsv");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.adhoc.151-200.txt");
    produced = Qrels.resolveQrelsPath("qrels.adhoc.151-200.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.microblog2012.txt");
    produced = Qrels.resolveQrelsPath("qrels.microblog2012.txt");
    assertNotNull(produced);
    assertEquals(expected, produced);

    expected = CacheDirectoryResolver.getQrelsCachePath().resolve("qrels.terabyte04.701-750.txt");
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
