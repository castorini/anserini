/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

import java.io.IOException;

import org.junit.Test;

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
  }

  @Test
  public void testRobust05() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.robust05.txt");
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
  }

  @Test
  public void testTrec19DLPassage() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.dl19-passage.txt");
    assertNotNull(qrels);
    assertEquals(43, qrels.getQids().size());
    assertEquals(9260, getQrelsCount(qrels));
    assertEquals(0, qrels.getRelevanceGrade("19335", "1017759"));
    assertEquals(1, qrels.getRelevanceGrade("1133167", "8804478"));
  }

  @Test
  public void testMsmarcoDocDev() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.msmarco-doc.dev.txt");
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
  }

  @Test
  public void testCore17() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.core17.txt");
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
  }

  @Test
  public void testTrec2018BL() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.backgroundlinking18.txt");
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
  }

  @Test
  public void testCovidRound1() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round1.txt");
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
  }

  @Test
  public void testCovidRound3() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round3.txt");
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
  }

  @Test
  public void testCovidRound5() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round5.txt");
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
  }

  @Test
  public void testCovidRound4Cumulative() {
    RelevanceJudgments qrels = new RelevanceJudgments("src/main/resources/topics-and-qrels/qrels.covid-round4-cumulative.txt");
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
  }

  @Test
  public void testGetQrelsResource() throws IOException {
      String qrels = RelevanceJudgments.getQrelsResource(Qrels.ROBUST04);
      assertNotNull(qrels);
      assertEquals("301 0 FBIS3-10082 1\n301 0", qrels.substring(0, 25));
    }
}
