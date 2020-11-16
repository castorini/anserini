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

import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexArgs;
import io.anserini.search.SearchArgs;

import java.util.Map;

public class TrecEndToEndPassageTest extends EndToEndTest {
  @Override
  protected IndexArgs getIndexArgs() {
    IndexArgs indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/trec/collection3";
    indexArgs.collectionClass = TrecCollection.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    documents.put("TREC_DOC_1.00001", Map.of(
        "contents", "This is head very simple text",
        "raw", "<HEAD>This is head</HEAD>\n" +
            "<TEXT>\n" +
            "very simple\n" +
            "text\n" +
            "</TEXT>"));
    documents.put("WSJ_1", Map.of(
        "contents", "head text 01/30/03 content",
        "raw", "<HL>\n" +
            "head text\n" +
            "</HL>\n" +
            "<DATE>\n" +
            "01/30/03\n" +
            "</DATE>\n" +
            "<LP>\n" +
            "content\n" +
            "</LP>\n" +
            "<TEXT>\n" +
            "</TEXT>"));
    documents.put("TREC_DOC_1.00002", Map.of(
        "contents", "HEAD simple enough text text text",
        "raw", "<HEAD>HEAD</HEAD>\n" +
            "<TEXT>\n" +
            "simple\n" +
            "enough\n" +
            "text\n" +
            "text\n" +
            "text\n" +
            "</TEXT>"));

    fieldNormStatusTotalFields = 1;  // text
    termIndexStatusTermCount = 12;   // Note that standard analyzer ignores stopwords; includes docids.
    termIndexStatusTotFreq = 17;
    storedFieldStatusTotalDocCounts = 3;
    // 16 positions for text fields, plus 1 for each document because of id
    termIndexStatusTotPos = 16 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 9;  // 3 docs * (1 id + 1 text + 1 raw)
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "Trec";
    topicFile = "src/test/resources/sample_topics/Trec";

    SearchArgs args;

    args = createDefaultSearchArgs().bm25();
    args.selectMaxPassage = true;
    testQueries.put("bm25v1", args);
    referenceRunOutput.put("bm25v1", new String[]{
        "1 Q0 TREC_DOC_1 1 0.343200 Anserini",
        "1 Q0 WSJ_1 2 0.068700 Anserini"});

    args = createDefaultSearchArgs().bm25();
    args.selectMaxPassage = true;
    args.selectMaxPassage_hits = 1;
    testQueries.put("bm25v2", args);
    referenceRunOutput.put("bm25v2", new String[]{
        "1 Q0 TREC_DOC_1 1 0.343200 Anserini"});

    args = createDefaultSearchArgs().qld();
    args.selectMaxPassage = true;
    testQueries.put("qld", args);
    referenceRunOutput.put("qld", new String[]{
        "1 Q0 TREC_DOC_1 1 0.002500 Anserini",
        "1 Q0 WSJ_1 2 0.000000 Anserini"});

    args = createDefaultSearchArgs().qljm();
    args.selectMaxPassage = true;
    testQueries.put("qljm", args);
    referenceRunOutput.put("qljm", new String[]{
        "1 Q0 TREC_DOC_1 1 4.872300 Anserini",
        "1 Q0 WSJ_1 2 1.658200 Anserini"});

    args = createDefaultSearchArgs().inl2();
    args.selectMaxPassage = true;
    testQueries.put("inl2", args);
    referenceRunOutput.put("inl2", new String[]{
        "1 Q0 TREC_DOC_1 1 0.133200 Anserini",
        "1 Q0 WSJ_1 2 0.021100 Anserini"});

    args = createDefaultSearchArgs().spl();
    args.selectMaxPassage = true;
    testQueries.put("spl", args);
    referenceRunOutput.put("spl", new String[]{
        "1 Q0 TREC_DOC_1 1 0.446100 Anserini",
        "1 Q0 WSJ_1 2 0.115900 Anserini"});

    args = createDefaultSearchArgs().f2exp();
    args.selectMaxPassage = true;
    testQueries.put("f2exp", args);
    referenceRunOutput.put("f2exp", new String[]{
        "1 Q0 TREC_DOC_1 1 1.434700 Anserini",
        "1 Q0 WSJ_1 2 0.536200 Anserini"});

    args = createDefaultSearchArgs().f2log();
    args.selectMaxPassage = true;
    testQueries.put("f2log", args);
    referenceRunOutput.put("f2log", new String[]{
        "1 Q0 TREC_DOC_1 1 0.548500 Anserini",
        "1 Q0 WSJ_1 2 0.139500 Anserini"});
  }
}
