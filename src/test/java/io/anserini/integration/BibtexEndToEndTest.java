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

import io.anserini.collection.BibtexCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.generator.BibtexGenerator;

import java.util.Map;

public class BibtexEndToEndTest extends EndToEndTest {
  @Override
  protected IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/bib/acl";
    indexArgs.collectionClass = BibtexCollection.class.getSimpleName();
    indexArgs.generatorClass = BibtexGenerator.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    documents.put("article-id", Map.of(
        "contents", "this is the title. ",
        "raw", "this is the title. "));
    documents.put("inproceedings-id", Map.of(
        "contents", "this is the title. this is the abstract",
        "raw", "this is the title. this is the abstract"));
    documents.put("proceedings-id", Map.of(
        "contents", "this is the title. ",
        "raw", "this is the title. "));

    fieldNormStatusTotalFields = 12;
    termIndexStatusTermCount = 45;
    termIndexStatusTotFreq = 57;
    storedFieldStatusTotalDocCounts = 3;
    termIndexStatusTotPos = 57;
    storedFieldStatusTotFields = 37;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/bibtex_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 article-id 1 0.073800 Anserini",
        "1 Q0 proceedings-id 2 0.073799 Anserini",
        "1 Q0 inproceedings-id 3 0.064200 Anserini",
        "2 Q0 inproceedings-id 1 0.471600 Anserini"});
  }
}
