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

package io.anserini.rerank;

import org.junit.Test;

public class OutputRerankerRequestsTest {
  @Test
  public void testPrebuilt() throws Exception {
    OutputRerankerRequests.Args args = new OutputRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run1";
    args.topics = "cacm";
    args.output = "test_reranker_requests.jsonl";

    OutputRerankerRequests<String> outputRerankerRequests = new OutputRerankerRequests<>(args);
    outputRerankerRequests.close();
  }

  @Test
  public void testParseTopics() throws Exception {
    OutputRerankerRequests.Args args = new OutputRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run1";
    args.topics = "cacm.bge-base-en-v1.5";
    args.output = "test_reranker_requests.jsonl";

    OutputRerankerRequests<String> outputRerankerRequests = new OutputRerankerRequests<>(args);
    outputRerankerRequests.close();
  }

  @Test
  public void testLocalIndex() throws Exception {
    OutputRerankerRequests.Args args = new OutputRerankerRequests.Args();
    args.index = "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_json_collection_tokenized/";
    args.run = "src/test/resources/sample_runs/run1";
    args.topics = "cacm";
    args.output = "test_reranker_requests.jsonl";

    OutputRerankerRequests<String> outputRerankerRequests = new OutputRerankerRequests<>(args);
    outputRerankerRequests.close();
  }

  @Test
  public void testLocalTopics() throws Exception {
    OutputRerankerRequests.Args args = new OutputRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run1";
    args.topics = "src/test/resources/sample_topics/acl_topics.tsv";
    args.output = "test_reranker_requests.jsonl";

    OutputRerankerRequests<String> outputRerankerRequests = new OutputRerankerRequests<>(args);
    outputRerankerRequests.close();
  }
}
