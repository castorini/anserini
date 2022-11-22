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

import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexCollection;
import io.anserini.search.SearchCollection;

import java.util.Map;

public class JsonEndToEndBasicTest extends EndToEndTest {
  @Override
  IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection3";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();
    // The difference between JsonCollectionEndToEndMultifieldTest and JsonCollectionEndToEndBasicTest is that
    // here we are *not* indexing additional fields.
    indexArgs.fields = null;

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    docFieldCount = 3; // id, raw, contents

    referenceDocs.put("doc1", Map.of("contents", "this is the contents 1."));
    referenceDocs.put("doc2", Map.of("contents", "this is the contents 2."));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 5;
    termIndexStatusTotFreq = 6;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 6;
    storedFieldStatusTotFields = 6;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics2.tsv";
    SearchCollection.Args searchArg1 = createDefaultSearchArgs().bm25();
    testQueries.put("bm25", searchArg1);
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 doc1 1 0.364800 Anserini",
        "2 Q0 doc2 1 0.364800 Anserini",
        "3 Q0 doc1 1 0.096000 Anserini",
        "3 Q0 doc2 2 0.095999 Anserini"});

    topicReader = "TsvString";
    topicFile = "src/test/resources/sample_topics/json_topics3.tsv";
    SearchCollection.Args searchArg2 = createDefaultSearchArgs().bm25();
    searchArg2.removeQuery = true;
    testQueries.put("bm25-rq", searchArg2);
    referenceRunOutput.put("bm25-rq", new String[]{
        "doc1 Q0 doc2 1 0.095999 Anserini",
        "doc2 Q0 doc1 1 0.096000 Anserini"});
  }

}
