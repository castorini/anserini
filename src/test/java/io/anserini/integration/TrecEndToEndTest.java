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

package io.anserini.integration;

public class TrecEndToEndTest extends EndToEndTest {

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
    termIndexStatusTotPos = 16;   // only "text" fields are indexed with position so we have 16
    storedFieldStatusTotalDocCounts = 3;
    storedFieldStatusTotFields = 9;  // 3 docs * (1 id + 1 text + 1 raw)

    evalMetricValue = (float)(0.0/1+1.0/2+2.0/3)/2.0f; // 3 retrieved docs in total:
                               // 1st retrieved doc is non-rel, 2nd and 3rd are rel
                               // and there are in total 3 rel docs in qrels
  }
}
