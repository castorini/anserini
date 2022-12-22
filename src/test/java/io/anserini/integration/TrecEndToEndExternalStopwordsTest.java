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

import io.anserini.collection.TrecCollection;
import io.anserini.index.IndexCollection;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TrecEndToEndExternalStopwordsTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/trec/collection2";
    indexArgs.collectionClass = TrecCollection.class.getSimpleName();
    indexArgs.stopwords = "src/test/resources/sample_docs/trec/collection2/stopwords.txt";

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 3;
    docFieldCount = 3; // id, raw, contents

    referenceDocs.put("TREC_DOC_1", Map.of(
            "contents", "This is head very simple text",
            "raw", "<HEAD>This is head</HEAD>\n" +
                    "<TEXT>\n" +
                    "very simple\n" +
                    "text\n" +
                    "</TEXT>"));
    referenceDocs.put("WSJ_1", Map.of(
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
    referenceDocs.put("DOC222", Map.of(
            "contents", "HEAD simple enough text text text",
            "raw", "<HEAD>HEAD</HEAD>\n" +
                    "<TEXT>\n" +
                    "simple\n" +
                    "enough\n" +
                    "text\n" +
                    "text\n" +
                    "text\n" +
                    "</TEXT>"));

    referenceDocTokens.put("TREC_DOC_1", Map.of(
        "contents", Arrays.asList(new String[]{"thi", "is", "head", "veri", null, "text"})));
    referenceDocTokens.put("WSJ_1", Map.of(
        "contents", List.of("head", "text", "01", "30", "03", "content")));
    referenceDocTokens.put("DOC222", Map.of(
        "contents", Arrays.asList(new String[]{"head", null, null, "text", "text", "text"})));

    // Terms per document:
    // d1: TREC_DOC_1 this is head very simple text
    // d2: DOC222 head simple enough text
    // d3: WSJ_1 head text 01/30/03 content

    // before stopwords
    // total terms: TREC_DOC_1 this is head very simple text DOC222 enough WSJ_1 01 30 03 content
    // total term freqs: TREC_DOC_1|1 this|1 is|1 head|3 very|1 simple|2 text|3 DOC222|1 enough|1 content|1 wsj_1|1 01|1 30|1 03|1

    // after stopwords: enough, simple
    // total terms: TREC_DOC_1 this is head very text DOC222 WSJ_1 01 30 03 content => 12
    // total term freqs: TREC_DOC_1|1 this|1 is|1 head|3 very|1 text|3 DOC222|1 content|1 wsj_1|1 01|1 30|1 03|1 => 16

    fieldNormStatusTotalFields = 1;  // text
    termIndexStatusTermCount = 12;
    termIndexStatusTotFreq = 16;
    storedFieldStatusTotalDocCounts = 3;

    termIndexStatusTotPos = 15 + storedFieldStatusTotalDocCounts;
    storedFieldStatusTotFields = 9;  // 1 docs * (1 id + 1 text + 1 raw)
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "Trec";
    topicFile = "src/test/resources/sample_topics/Trec";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    referenceRunOutput.put("bm25", new String[]{
            "1 Q0 DOC222 1 0.104600 Anserini",
            "1 Q0 TREC_DOC_1 2 0.070300 Anserini",
            "1 Q0 WSJ_1 3 0.067700 Anserini"});

    testQueries.put("qld", createDefaultSearchArgs().qld());
    referenceRunOutput.put("qld", new String[]{
            "1 Q0 DOC222 1 0.004000 Anserini",
            "1 Q0 TREC_DOC_1 2 0.000000 Anserini",
            "1 Q0 WSJ_1 3 -0.000001 Anserini"});

    testQueries.put("qljm", createDefaultSearchArgs().qljm());
    referenceRunOutput.put("qljm", new String[]{
            "1 Q0 DOC222 1 2.944400 Anserini",
            "1 Q0 TREC_DOC_1 2 1.757900 Anserini",
            "1 Q0 WSJ_1 3 1.609400 Anserini"});

    testQueries.put("inl2", createDefaultSearchArgs().inl2());
    referenceRunOutput.put("inl2", new String[]{
            "1 Q0 DOC222 1 0.065000 Anserini",
            "1 Q0 TREC_DOC_1 2 0.023300 Anserini",
            "1 Q0 WSJ_1 3 0.019900 Anserini"});

    testQueries.put("spl", createDefaultSearchArgs().spl());
    referenceRunOutput.put("spl", new String[]{
            "1 Q0 DOC222 1 0.412000 Anserini",
            "1 Q0 TREC_DOC_1 2 0.128800 Anserini",
            "1 Q0 WSJ_1 3 0.109300 Anserini"});

    testQueries.put("f2exp", createDefaultSearchArgs().f2exp());
    referenceRunOutput.put("f2exp", new String[]{
            "1 Q0 DOC222 1 0.850700 Anserini",
            "1 Q0 TREC_DOC_1 2 0.553000 Anserini",
            "1 Q0 WSJ_1 3 0.526600 Anserini"});

    testQueries.put("f2log", createDefaultSearchArgs().f2log());
    referenceRunOutput.put("f2log", new String[]{
            "1 Q0 DOC222 1 0.221300 Anserini",
            "1 Q0 TREC_DOC_1 2 0.143800 Anserini",
            "1 Q0 WSJ_1 3 0.137000 Anserini"});
  }
}
