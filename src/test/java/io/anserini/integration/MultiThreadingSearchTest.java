/**
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

package io.anserini.integration;

import io.anserini.eval.Eval;
import org.junit.After;

import java.io.File;

public class MultiThreadingSearchTest extends EndToEndTest {

  @Override
  protected void init() {
    dataDirPath = "trec";
    collectionClass = "Trec";
    generator = "Jsoup";
    topicReader = "Trec";

    fieldNormStatusTotalFields = 1; // text
    termIndexStatusTermCount = 12; // Please note that standard analyzer ignores stopwords.
                                   // Also, this includes docids
    termIndexStatusTotFreq = 17;  //
    storedFieldStatusTotalDocCounts = 3;
    // 16 positions for text fields, plus 1 for each document because of id
    termIndexStatusTotPos = 16 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 9;  // 3 docs * (1 id + 1 text + 1 raw)
  }
  
  protected void setSearchArgs() {
    super.setSearchArgs();
    searchArgs.bm25 = true;
    searchArgs.b = new String[] {"0.2", "0.8"};
  }
  
  protected void testEval() throws Exception {
    setEvalArgs();
    float[] res = new float[]{0.8333f, 0.5833f};
    try {
      Eval.setAllMetrics(this.evalMetrics);
      for (int i = 0; i < searchArgs.b.length; i++) {
        System.out.println(evalArgs.runPath+"_k1="+searchArgs.k1[0]+",b="+searchArgs.b[i]);
        Eval.eval(evalArgs.runPath+"_k1="+searchArgs.k1[0]+",b="+searchArgs.b[i], evalArgs.qrelPath, evalArgs.longDocids, evalArgs.asc);
        assertEquals(Eval.getAllEvals().get(this.evalMetrics[0]).aggregated,
            res[i], 0.001);
      }
    } catch (Exception e) {
      System.out.println("Test Eval failed");
      e.printStackTrace();
      fail();
    }
  }
  
  @After
  @Override
  public void tearDown() throws Exception {
    for (String b : searchArgs.b) {
      new File(evalArgs.runPath+"_k1="+searchArgs.k1[0]+",b="+b).delete();
    }
    super.tearDown();
  }
}
