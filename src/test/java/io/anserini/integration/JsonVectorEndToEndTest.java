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

import io.anserini.collection.JsonVectorCollection;
import io.anserini.index.IndexCollection;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchCollection;

import java.util.ArrayList;
import java.util.Map;

public class JsonVectorEndToEndTest extends EndToEndTest {
  @Override
  IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json_vector/collection3";
    indexArgs.collectionClass = JsonVectorCollection.class.getSimpleName();
    indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
    indexArgs.pretokenized = true;
    indexArgs.storeRaw = true;
    indexArgs.impact = true;

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    docFieldCount = 3; // id, raw, contents

    referenceDocs.put("doc1", Map.of("contents", "f1 f2 f2 f3 f4 f4 f4 f4 f5 "));
    referenceDocs.put("doc2", Map.of("contents", "f4 f4 f4 f5 f9 f9 f22 f22 f22 f22 f22 f22 f35 f35 f35 f35 f35 f35 f35 f35 "));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 10;
    termIndexStatusTotFreq = 12;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 31;
    storedFieldStatusTotFields = 6;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_vector_topics.tsv";
    SearchCollection.Args searchArg = createDefaultSearchArgs().impact();
    searchArg.pretokenized = true;

    testQueries.put("impact", searchArg);
    queryTokens.put("1", new ArrayList<>());
    queryTokens.get("1").add("f35");
    queryTokens.put("2", new ArrayList<>());
    queryTokens.get("2").add("f3");
    queryTokens.put("3", new ArrayList<>());
    queryTokens.get("3").add("f4");

    referenceRunOutput.put("impact", new String[]{
        "1 Q0 doc2 1 8.000000 Anserini",
        "2 Q0 doc1 1 1.000000 Anserini",
        "3 Q0 doc1 1 4.000000 Anserini",
        "3 Q0 doc2 2 3.000000 Anserini"});
  }

}
