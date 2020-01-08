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

public class TrecEndToEndTest extends EndToEndTest {

  @Override
  protected void init() throws Exception {
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

    fieldNormStatusTotalFields = 1;  // text
    termIndexStatusTermCount = 12;   // Note that standard analyzer ignores stopwords; includes docids.
    termIndexStatusTotFreq = 17;
    storedFieldStatusTotalDocCounts = 3;
    // 16 positions for text fields, plus 1 for each document because of id
    termIndexStatusTotPos = 16 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 9;  // 3 docs * (1 id + 1 text + 1 raw)

    referenceRunOutput = new String[] {
      "1 Q0 DOC222 1 0.343200 Anserini",
      "1 Q0 TREC_DOC_1 2 0.333400 Anserini",
      "1 Q0 WSJ_1 3 0.068700 Anserini" };
  }
}
