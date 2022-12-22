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

import io.anserini.collection.C4Collection;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.C4Generator;

import java.util.Map;

public class C4EndToEndTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/c4";
    indexArgs.collectionClass = C4Collection.class.getSimpleName();
    indexArgs.generatorClass = C4Generator.class.getSimpleName();
    indexArgs.shardCount = 2;
    indexArgs.shardCurrent = 1;

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    docFieldCount = 5; // id, raw, contents, url, timestamp

    referenceDocs.put("c4-0001-000000", Map.of(
            "contents", "test text",
            "raw", "{\n" +
                    "  \"text\" : \"test text\",\n" +
                    "  \"timestamp\" : \"2019-04-23T08:26:47Z\",\n" +
                    "  \"url\" : \"http://www.test.com\"\n" +
                    "}"));
    referenceDocs.put("c4-0001-000001", Map.of(
            "contents", "test text2",
            "raw", "{\n" +
                    "  \"text\" : \"test text2\",\n" +
                    "  \"timestamp\" : \"2020-04-23T08:26:47Z\",\n" +
                    "  \"url\" : \"http://www.test2.com\"\n" +
                    "}"));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 7;
    termIndexStatusTotFreq = 8;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 8;
    storedFieldStatusTotFields = 10;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/c4_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
            "1 Q0 c4-0001-000000 1 0.364800 Anserini"});
  }
}
