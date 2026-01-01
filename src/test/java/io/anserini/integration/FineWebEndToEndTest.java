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
    docCount = 8;
    docFieldCount = -1; // Variable field counts across documents

    // Documents from fineweb_standard.parquet
    referenceDocs.put("fineweb-doc-001", Map.of(
        "contents", "This is the first test document for FineWeb collection testing."));
    referenceDocs.put("fineweb-doc-002", Map.of(
        "contents", "Second document contains different content for verification."));
    referenceDocs.put("fineweb-doc-003", Map.of(
        "contents", "Third document with special characters: café, naïve, 日本語."));

    // Documents from fineweb_alternative_fields.parquet
    referenceDocs.put("alt-doc-001", Map.of(
        "contents", "Document using alternative field names for id and content."));
    referenceDocs.put("alt-doc-002", Map.of(
        "contents", "Another document with docid field instead of id."));

    // Documents from fineweb_no_id.parquet (auto-generated IDs)
    referenceDocs.put("fineweb_no_id_0", Map.of(
        "contents", "Document without an ID field - should auto-generate."));
    referenceDocs.put("fineweb_no_id_1", Map.of(
        "contents", "Another document that needs an auto-generated ID."));
    referenceDocs.put("fineweb_no_id_2", Map.of(
        "contents", "Third document also missing ID field."));

    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 41;
    termIndexStatusTotFreq = 60;
    storedFieldStatusTotalDocCounts = 8;
    termIndexStatusTotPos = 61;
    storedFieldStatusTotFields = 24;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/fineweb_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 fineweb-doc-001 1 3.201400 Anserini",
        "1 Q0 alt-doc-002 2 0.030600 Anserini",
        "1 Q0 fineweb-doc-002 3 0.030599 Anserini",
        "1 Q0 fineweb_no_id_1 4 0.030598 Anserini",
        "1 Q0 fineweb_no_id_2 5 0.030597 Anserini",
        "1 Q0 alt-doc-001 6 0.029800 Anserini",
        "1 Q0 fineweb_no_id_0 7 0.029799 Anserini",
        "1 Q0 fineweb-doc-003 8 0.028200 Anserini"});
  }
}
