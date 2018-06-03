/**
 * Anserini: An information retrieval toolkit built on Lucene
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


import io.anserini.eval.metric.AvgPrecision;
import io.anserini.eval.metric.NDCG;
import io.anserini.eval.metric.Precision;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class MetricsTest extends LuceneTestCase {
  protected List<ResultDoc> rankingList;
  protected Map<String, Integer> judgments1;
  protected Map<String, Integer> judgments2;

  protected static final double DELTA = 1e-6;

  /**
   * MUST call super
   * constructs the necessary ranking list and judgements
   * @throws Exception
   */
  @Override
  @Before
  public void setUp() throws Exception {
    super.setUp();
    // construct ranking list
    // d2 and d3 ties with score
    ResultDoc d1 = new ResultDoc("d1", 2.02, false, false);
    ResultDoc d2 = new ResultDoc("d2", 1.9998, false, false);
    ResultDoc d3 = new ResultDoc("d3", 1.9998, false, false);
    ResultDoc d4 = new ResultDoc("d4", 1.576, false, false);

    rankingList = new ArrayList<>();
    rankingList.add(d1);
    rankingList.add(d2);
    rankingList.add(d3);
    rankingList.add(d4);
    Collections.sort(rankingList);

    // construct the judgments
    judgments1 = new TreeMap<>();
    judgments1.put("d2", 1);
    judgments1.put("d4", 3);

    // this is empty judgments
    judgments2 = new TreeMap<>();
  }

  @Test
  public void testPrecision() throws IOException {
    double p1 = new Precision().evaluate(rankingList, judgments1);
    double p2 = new Precision(3).evaluate(rankingList, judgments1);
    double p3 = new Precision().evaluate(rankingList, judgments2);

    assertEquals(0.5, p1, DELTA);
    assertEquals(0.33333333, p2, DELTA);
    assertEquals(0.0, p3, DELTA);
  }

  @Test
  public void testAP() throws IOException {
    double ap1 = new AvgPrecision().evaluate(rankingList, judgments1);
    double ap2 = new AvgPrecision().evaluate(rankingList, judgments2);

    assertEquals(0.41666666666666, ap1, DELTA);
    assertEquals(0.0, ap2, DELTA);
  }

  @Test
  public void testNDCG() throws IOException {
    double ndcg1 = new NDCG().evaluate(rankingList, judgments1);
    double ndcg2 = new NDCG(3).evaluate(rankingList, judgments1);
    double ndcg3 = new NDCG().evaluate(rankingList, judgments2);

    assertEquals(0.46059078, ndcg1, DELTA);
    assertEquals(0.0655228, ndcg2, DELTA);
    assertEquals(0.0, ndcg3, DELTA);
  }
}
