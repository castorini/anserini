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

import io.anserini.search.SearchArgs;
import org.junit.After;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MultiThreadingSearchTest extends EndToEndTest {
  private List<File> cleanup = new ArrayList<>();
  private Map<String, Set<String>> runsForQuery = new HashMap<>();
  private Map<String, String[]> groundTruthRuns = new HashMap<>();

  @Override
  protected void init() {
    dataDirPath = "trec/collection2";
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

    SearchArgs searchArgs;

    searchArgs = createDefaultSearchArgs().bm25();
    searchArgs.bm25_b = new String[] {"0.2", "0.8"};
    testQueries.put("bm25", searchArgs);
    runsForQuery.put("bm25",
        Set.of("e2eTestSearchTrec_bm25(k1=0.9,b=0.2)_default", "e2eTestSearchTrec_bm25(k1=0.9,b=0.8)_default"));
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.2)_default", new String[] {
        "1 Q0 DOC222 1 0.346600 Anserini",
        "1 Q0 TREC_DOC_1 2 0.325400 Anserini",
        "1 Q0 WSJ_1 3 0.069500 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.8)_default", new String[] {
        "1 Q0 TREC_DOC_1 1 0.350900 Anserini",
        "1 Q0 DOC222 2 0.336600 Anserini",
        "1 Q0 WSJ_1 3 0.067100 Anserini"});

    searchArgs = createDefaultSearchArgs().bm25();
    searchArgs.bm25_b = new String[] {"0.2", "0.8"};
    searchArgs.rm3 = true;
    testQueries.put("bm25rm3-1", searchArgs);
    runsForQuery.put("bm25rm3-1", Set.of(
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.2)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.5)",
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.8)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.5)"));
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.2)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.5)",
        new String[] {
            "1 Q0 DOC222 1 0.086700 Anserini",
            "1 Q0 TREC_DOC_1 2 0.081300 Anserini",
            "1 Q0 WSJ_1 3 0.017400 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.8)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.5)",
        new String[] {
            "1 Q0 TREC_DOC_1 1 0.087700 Anserini",
            "1 Q0 DOC222 2 0.084100 Anserini",
            "1 Q0 WSJ_1 3 0.016800 Anserini"});

    searchArgs = createDefaultSearchArgs().bm25();
    searchArgs.bm25_b = new String[] {"0.4", "0.5"};
    searchArgs.rm3 = true;
    searchArgs.rm3_originalQueryWeight = new String[] {"0.2", "0.9"};
    testQueries.put("bm25rm3-2", searchArgs);
    runsForQuery.put("bm25rm3-2", Set.of(
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.4)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.2)",
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.4)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.9)",
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.5)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.2)",
        "e2eTestSearchTrec_bm25(k1=0.9,b=0.5)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.9)"));
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.4)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.2)",
        new String[] {
            "1 Q0 DOC222 1 0.034300 Anserini",
            "1 Q0 TREC_DOC_1 2 0.033300 Anserini",
            "1 Q0 WSJ_1 3 0.006900 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.4)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.9)",
        new String[] {
            "1 Q0 DOC222 1 0.154400 Anserini",
            "1 Q0 TREC_DOC_1 2 0.150100 Anserini",
            "1 Q0 WSJ_1 3 0.030900 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.5)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.2)",
        new String[] {
            "1 Q0 DOC222 1 0.034200 Anserini",
            "1 Q0 TREC_DOC_1 2 0.033800 Anserini",
            "1 Q0 WSJ_1 3 0.006800 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_bm25(k1=0.9,b=0.5)_rm3(fbTerms=10,fbDocs=10,originalQueryWeight=0.9)",
        new String[] {
            "1 Q0 DOC222 1 0.153700 Anserini",
            "1 Q0 TREC_DOC_1 2 0.151900 Anserini",
            "1 Q0 WSJ_1 3 0.030700 Anserini"});

    searchArgs = createDefaultSearchArgs().qld();
    searchArgs.qld_mu = new String[] {"1000", "2000"};
    testQueries.put("qld", searchArgs);
    runsForQuery.put("qld",
        Set.of("e2eTestSearchTrec_qld(mu=1000)_default", "e2eTestSearchTrec_qld(mu=2000)_default"));
    groundTruthRuns.put("e2eTestSearchTrec_qld(mu=1000)_default", new String[] {
        "1 Q0 DOC222 1 0.002500 Anserini",
        "1 Q0 TREC_DOC_1 2 0.001700 Anserini",
        "1 Q0 WSJ_1 3 0.000000 Anserini"});
    groundTruthRuns.put("e2eTestSearchTrec_qld(mu=2000)_default", new String[] {
        "1 Q0 DOC222 1 0.001200 Anserini",
        "1 Q0 TREC_DOC_1 2 0.000800 Anserini",
        "1 Q0 WSJ_1 3 0.000000 Anserini"});
  }

  protected void checkRankingResults(String key, String output) throws IOException {
    for (String run : runsForQuery.get(key)) {
      File runfile = new File(run);

      // First, check to see if all the expected runs exist:
      assertTrue(runfile.exists());

      // Check the contents of the runs:
      BufferedReader br = new BufferedReader(new FileReader(runfile));
      int cnt = 0;
      String s;
      while ((s = br.readLine()) != null) {
        assertEquals(groundTruthRuns.get(run)[cnt], s);
        cnt++;
      }
      assertEquals(cnt, groundTruthRuns.get(run).length);

      // Add the file to the cleanup list.
      cleanup.add(runfile);
    }
  }

  @After
  @Override
  public void tearDown() throws Exception {
    for (File file : cleanup) {
      file.delete();
    }
    super.tearDown();
  }
}
