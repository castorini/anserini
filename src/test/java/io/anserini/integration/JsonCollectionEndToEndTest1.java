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
import io.anserini.collection.JsonVectorCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchArgs;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonCollectionEndToEndTest1 extends EndToEndTest {
  @Override
  IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection3";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();

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
    topicFile = "src/test/resources/sample_topics/json_topics2.tsv";
    SearchArgs searchArg = createDefaultSearchArgs().bm25();

    testQueries.put("bm25", searchArg);
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 doc1 1 0.364800 Anserini",
        "2 Q0 doc2 1 0.364800 Anserini",
        "3 Q0 doc1 1 0.096000 Anserini",
        "3 Q0 doc2 2 0.095999 Anserini"});
  }

}
