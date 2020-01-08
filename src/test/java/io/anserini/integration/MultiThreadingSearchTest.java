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

package io.anserini.integration;

import org.junit.After;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MultiThreadingSearchTest extends EndToEndTest {

  @Override
  protected void init() {
    dataDirPath = "trec";
    collectionClass = "Trec";
    generator = "Jsoup";
    topicReader = "Trec";

    docCount = 3;

    counterIndexed = 3;
    counterEmpty = 0;
    counterUnindexable = 0;
    counterSkipped = 0;
    counterErrors = 0;

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

  protected void checkRankingResults(String output) throws IOException {
    String[][] multiReferenceOutput = new String[][] {
      {"1 Q0 DOC222 1 0.346600 Anserini",
       "1 Q0 TREC_DOC_1 2 0.325400 Anserini",
       "1 Q0 WSJ_1 3 0.069500 Anserini"},
      {"1 Q0 TREC_DOC_1 1 0.350900 Anserini",
       "1 Q0 DOC222 2 0.336600 Anserini",
       "1 Q0 WSJ_1 3 0.067100 Anserini"}
    };

    for (int i = 0; i < searchArgs.b.length; i++) {
      String fname = output + "_k1=" + searchArgs.k1[0] + ",b=" + searchArgs.b[i];

      BufferedReader br = new BufferedReader(new FileReader(fname));
      int cnt = 0;
      String s;
      while ((s = br.readLine()) != null) {
        assertEquals(multiReferenceOutput[i][cnt], s);
        cnt++;
      }
    }
  }

  @After
  @Override
  public void tearDown() throws Exception {
    for (String b : searchArgs.b) {
      new File(searchArgs.output+"_k1="+searchArgs.k1[0]+",b="+b).delete();
    }
    super.tearDown();
  }
}
