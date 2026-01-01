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

import io.anserini.collection.SmolTalkCollection;
import io.anserini.index.IndexCollection;

import java.util.Map;

/**
 * End-to-end test for {@link SmolTalkCollection}.
 * Tests indexing and searching of SmolTalk conversation dataset.
 */
public class SmolTalkEndToEndTest extends EndToEndTest {
  @Override
  protected IndexCollection.Args getIndexArgs() {
    IndexCollection.Args indexArgs = createDefaultIndexArgs();

    indexArgs.input = "src/test/resources/sample_docs/smoltalk";
    indexArgs.collectionClass = SmolTalkCollection.class.getSimpleName();

    return indexArgs;
  }

  @Override
  protected void setCheckIndexGroundTruth() {
    docCount = 5;  // 5 Q&A pairs from the test parquet file
    docFieldCount = -1;  // Variable field counts

    // Documents from smoltalk_standard.parquet
    // Row 0: 1 Q&A pair about France
    referenceDocs.put("smoltalk_standard_0_0", Map.of(
        "contents", "Question: What is the capital of France?\n\nAnswer: The capital of France is Paris."));

    // Row 1: 3 Q&A pairs about Python learning
    referenceDocs.put("smoltalk_standard_1_0", Map.of(
        "contents", "Question: How do I learn Python?\n\nAnswer: Start with basic tutorials and practice coding daily."));
    referenceDocs.put("smoltalk_standard_1_1", Map.of(
        "contents", "Question: What resources do you recommend?\n\nAnswer: Try Codecademy, Python.org tutorials, or Automate the Boring Stuff."));
    referenceDocs.put("smoltalk_standard_1_2", Map.of(
        "contents", "Question: How long will it take to become proficient?\n\nAnswer: With consistent practice, you can become proficient in 3-6 months."));

    // Row 2: 1 Q&A pair about machine learning
    referenceDocs.put("smoltalk_standard_2_0", Map.of(
        "contents", "Question: Explain machine learning in simple terms.\n\nAnswer: Machine learning is a type of AI where computers learn patterns from data to make predictions."));

    // Index statistics
    fieldNormStatusTotalFields = 1;
    termIndexStatusTermCount = 53;
    termIndexStatusTotFreq = 68;
    storedFieldStatusTotalDocCounts = 5;
    termIndexStatusTotPos = 75;
    storedFieldStatusTotFields = 15;
  }

  @Override
  protected void setSearchGroundTruth() {
    topicReader = "TsvInt";
    topicFile = "src/test/resources/sample_topics/smoltalk_topics.tsv";

    testQueries.put("bm25", createDefaultSearchArgs().bm25());
    // The search for "capital France Paris" should return the France Q&A document first
    referenceRunOutput.put("bm25", new String[]{
        "1 Q0 smoltalk_standard_0_0 1 2.813700 Anserini"});
  }
}

