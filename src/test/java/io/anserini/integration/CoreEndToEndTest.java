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

package io.anserini.integration;

import io.anserini.collection.CoreCollection;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.CoreGenerator;

import java.util.Map;

public class CoreEndToEndTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/core";
    indexArgs.collectionClass = CoreCollection.class.getSimpleName();
    indexArgs.generatorClass = CoreGenerator.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    docFieldCount = -1; // Documents may have variable number of fields, so don't bother checking.

    referenceDocs.put("fullCoreDoc", Map.of(
        "contents", "Full CORE doc ",
        "raw", "Full CORE doc "));
    referenceDocs.put("coreDoc1", Map.of(
        "contents", "this is the title 1 this is the abstract 1",
        "raw", "this is the title 1 this is the abstract 1"));
    referenceDocs.put("doi2", Map.of(
        "contents", "this is the title 2 this is the abstract 2",
        "raw", "this is the title 2 this is the abstract 2"));

    fieldNormStatusTotalFields = 15;
    termIndexStatusTermCount = 36;
    termIndexStatusTotFreq = 43;
    storedFieldStatusTotalDocCounts = 3;
    termIndexStatusTotPos = 45;
    storedFieldStatusTotFields = 46;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/core_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 coreDoc1 1 0.243200 Anserini",
        "1 Q0 doi2 2 0.243199 Anserini",
        "2 Q0 coreDoc1 1 0.243200 Anserini",
        "2 Q0 doi2 2 0.243199 Anserini",
        "3 Q0 fullCoreDoc 1 0.534600 Anserini"});
  }
}
