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

import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PretokenizedIndexEndToEndTest extends EndToEndTest {
  @Override
  IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection_tokenized";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();
    indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
    indexArgs.pretokenized = true;
    indexArgs.storeRaw = true;

    return indexArgs;
    }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    referenceDocs.put("2000000", Map.of(
      "contents", "this was ##a simple pretokenized test",
      "raw","{\n" +
      "  \"id\" : \"2000000\",\n" +
      "  \"contents\" : \"this was ##a simple pretokenized test\"\n" +
      "}"));
    referenceDocs.put("2000001", Map.of(
      "contents", "some time extra ##vert ##ing and some time intro ##vert ##ing",
      "raw","{\n" +
      "  \"id\" : \"2000001\",\n" +
      "  \"contents\" : \"some time extra ##vert ##ing and some time intro ##vert ##ing\"\n" +
      "}"
    ));
    referenceDocTokens.put("2000000", Map.of(
      "contents", List.of("this", "was", "##a", "simple", "pretokenized", "test")));
    referenceDocTokens.put("2000001", Map.of(
      "contents", List.of("some", "time", "extra", "##vert", "##ing", "and", "some", "time", "intro", "##vert", "##ing")));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 15;
    termIndexStatusTotFreq = 15;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 17 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 6;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics.tsv";
    SearchArgs searchArg = createDefaultSearchArgs().bm25();
    searchArg.pretokenized = true;
    testQueries.put("bm25", searchArg);
    queryTokens.put("1", new ArrayList<>());
    queryTokens.get("1").add("##ing");
    queryTokens.get("1").add("##vert");
    referenceRunOutput.put("bm25", new String[]{
            "1 Q0 2000001 1 0.922400 Anserini"});
  }

}
