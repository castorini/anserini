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

public class JsonEndToEndMultifieldTest extends EndToEndTest {
  @Override
  IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection3";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();
    // The difference between JsonCollectionEndToEndMultifieldTest and JsonCollectionEndToEndBasicTest is that
    // here we are indexing additional fields.
    indexArgs.fields = new String[] {"field1",  "field2"};

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    docFieldCount = 5; // id, raw, contents + two extra fields

    referenceDocs.put("doc1", Map.of("contents", "this is the contents 1."));
    referenceDocs.put("doc2", Map.of("contents", "this is the contents 2."));

    fieldNormStatusTotalFields = 3;
    termIndexStatusTermCount = 13;
    termIndexStatusTotFreq = 18;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 18;
    storedFieldStatusTotFields = 10;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics4.tsv";
    SearchCollection.Args searchArg1 = createDefaultSearchArgs().bm25();
    testQueries.put("bm25-1", searchArg1);
    referenceRunOutput.put("bm25-1", new String[]{
        "1 Q0 doc1 1 0.096000 Anserini",
        "1 Q0 doc2 2 0.095999 Anserini",
        "2 Q0 doc1 1 0.096000 Anserini",
        "2 Q0 doc2 2 0.095999 Anserini",
        "3 Q0 doc1 1 0.096000 Anserini",
        "3 Q0 doc2 2 0.095999 Anserini"});

    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics4.tsv";
    SearchCollection.Args searchArg2 = createDefaultSearchArgs().bm25();
    searchArg2.fields = new String[]{"contents=1.0", "field1=1.0"};
    testQueries.put("bm25-2", searchArg2);
    referenceRunOutput.put("bm25-2", new String[]{
        "1 Q0 doc1 1 0.191900 Anserini",
        "1 Q0 doc2 2 0.191899 Anserini",
        "2 Q0 doc1 1 0.652700 Anserini",
        "2 Q0 doc2 2 0.287900 Anserini",
        "3 Q0 doc2 1 0.652700 Anserini",
        "3 Q0 doc1 2 0.287900 Anserini"});

    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics4.tsv";
    SearchCollection.Args searchArg3 = createDefaultSearchArgs().bm25();
    searchArg3.fields = new String[]{"contents=1.0", "field1=0.5"};
    testQueries.put("bm25-3", searchArg3);
    referenceRunOutput.put("bm25-3", new String[]{
        "1 Q0 doc1 1 0.143900 Anserini",
        "1 Q0 doc2 2 0.143899 Anserini",
        "2 Q0 doc1 1 0.374300 Anserini",
        "2 Q0 doc2 2 0.191900 Anserini",
        "3 Q0 doc2 1 0.374300 Anserini",
        "3 Q0 doc1 2 0.191900 Anserini"});

    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics4.tsv";
    SearchCollection.Args searchArg4 = createDefaultSearchArgs().bm25();
    searchArg4.fields = new String[]{"contents=1.0", "field1=0.5", "field2=0.5"};
    testQueries.put("bm25-4", searchArg4);
    referenceRunOutput.put("bm25-4", new String[]{
        "1 Q0 doc1 1 0.191900 Anserini",
        "1 Q0 doc2 2 0.191899 Anserini",
        "2 Q0 doc1 1 0.652700 Anserini",
        "2 Q0 doc2 2 0.287900 Anserini",
        "3 Q0 doc2 1 0.652700 Anserini",
        "3 Q0 doc1 2 0.287900 Anserini"});
  }

}
