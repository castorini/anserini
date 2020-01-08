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

public class TrecEndToEndWhitelistTest extends EndToEndTest {

  @Override
  protected void init() throws Exception {
    dataDirPath = "trec";
    collectionClass = "Trec";
    generator = "Jsoup";
    topicReader = "Trec";

    docCount = 1;

    counterIndexed = 1;
    counterEmpty = 0;
    counterUnindexable = 0;
    counterSkipped = 2;
    counterErrors = 0;

    fieldNormStatusTotalFields = 1;  // text
    termIndexStatusTermCount = 5;   // Note that standard analyzer ignores stopwords; includes docids.
    termIndexStatusTotFreq = 5;
    storedFieldStatusTotalDocCounts = 1;
    termIndexStatusTotPos = 7;
    storedFieldStatusTotFields = 3;

    referenceRunOutput = new String[] {
        "1 Q0 DOC222 1 0.372700 Anserini"
    };
  }

  @Override
  protected void setIndexingArgs() {
    super.setIndexingArgs();
    indexCollectionArgs.whitelist = "src/test/resources/sample_docs/trec/whitelist.txt";
    // With a whitelist, we're only indexing DOC222
  }
}
