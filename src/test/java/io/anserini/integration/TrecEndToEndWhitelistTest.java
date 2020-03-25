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

import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.JsoupGenerator;

public class TrecEndToEndWhitelistTest extends EndToEndTest {
  @Override
  protected IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/trec/collection2";
    indexArgs.collectionClass = TrecCollection.class.getSimpleName();
    indexArgs.generatorClass = JsoupGenerator.class.getSimpleName();
    indexArgs.whitelist = "src/test/resources/sample_docs/trec/collection2/whitelist.txt";
    // With a whitelist, we're only indexing DOC222

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 1;

    fieldNormStatusTotalFields = 1;  // text
    termIndexStatusTermCount = 5;   // Note that standard analyzer ignores stopwords; includes docids.
    termIndexStatusTotFreq = 5;
    storedFieldStatusTotalDocCounts = 1;
    termIndexStatusTotPos = 7;
    storedFieldStatusTotFields = 3;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "Trec";
    topicFile = "src/test/resources/sample_topics/Trec";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 DOC222 1 0.372700 Anserini"
    });
  }
}
