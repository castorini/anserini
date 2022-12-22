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
import io.anserini.index.generator.DefaultLuceneDocumentGenerator;
import io.anserini.search.SearchCollection;

import java.util.Map;

public class HuggingFaceTokenizerEndToEndTest extends EndToEndTest {
  @Override
  IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();
    indexArgs.input = "src/test/resources/sample_docs/json/collection_huggingfacetokenizer";
    indexArgs.analyzeWithHuggingFaceTokenizer = "bert-base-uncased";
    indexArgs.collectionClass = JsonCollection.class.getSimpleName();
    indexArgs.generatorClass = DefaultLuceneDocumentGenerator.class.getSimpleName();
    indexArgs.storeRaw = true;
    
    return indexArgs;
  }
  
  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 2;
    docFieldCount = 3; // id, raw, contents
    
    referenceDocs.put("7187163", Map.of(
        "contents", "Racial scandals aren't always bad for business. Just ask Paula Deen's brother.",
        "raw","{\n" +
            "  \"id\" : \"7187163\",\n" +
            "  \"contents\" : \"Racial scandals aren't always bad for business. Just ask Paula Deen's brother.\"\n" +
            "}"));
    referenceDocs.put("7546327", Map.of(
        "contents", "What happened to Paula Deen's first husband? kgb answers Arts & Entertainment",
        "raw","{\n" +
            "  \"id\" : \"7546327\",\n" +
            "  \"contents\" : \"What happened to Paula Deen's first husband? kgb answers Arts & Entertainment\"\n" +
            "}"
    ));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 21;
    termIndexStatusTotFreq = 23;
    storedFieldStatusTotalDocCounts = 2;
    termIndexStatusTotPos = 21 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 6;
  }
  
  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/json_topics5.tsv";
    SearchCollection.Args searchArg = createDefaultSearchArgs().bm25();
    searchArg.analyzeWithHuggingFaceTokenizer = "bert-base-uncased";
    
    testQueries.put("bm25", searchArg);
    referenceRunOutput.put("bm25", new String[]{
        "1048585 Q0 7546327 1 0.465000 Anserini",
        "1048585 Q0 7187163 2 0.456700 Anserini"
    });
  }
  
}
