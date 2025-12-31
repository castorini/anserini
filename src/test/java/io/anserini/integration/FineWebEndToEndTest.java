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

import io.anserini.collection.FineWebCollection;
import io.anserini.index.IndexCollection;

import java.util.Map;

public class FineWebEndToEndTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/fineweb";
    indexArgs.collectionClass = FineWebCollection.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 4;
    docFieldCount = -1; // Variable field counts across documents

    // Documents from fineweb_standard.parquet
    referenceDocs.put("fineweb-doc-001", Map.of(
        "contents", "This is the first test document for FineWeb collection testing."));
    referenceDocs.put("fineweb-doc-003", Map.of(
        "contents", "Third document with special characters: café, naïve, 日本語."));

    // Documents from fineweb_alternative_fields.parquet
    referenceDocs.put("alt-doc-001", Map.of(
        "contents", "Document using alternative field names for id and content."));
    referenceDocs.put("alt-doc-002", Map.of(
        "contents", "Another document with docid field instead of id."));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 26;
    termIndexStatusTotFreq = 31;
    storedFieldStatusTotalDocCounts = 4;
    termIndexStatusTotPos = 32;
    storedFieldStatusTotFields = 12;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/fineweb_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 fineweb-doc-001 1 2.204911 Anserini",
        "1 Q0 alt-doc-002 2 0.056996 Anserini",
        "1 Q0 alt-doc-001 3 0.055453 Anserini",
        "1 Q0 fineweb-doc-003 4 0.052605 Anserini"});
  }
}
