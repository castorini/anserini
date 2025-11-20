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

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableTestCase;
import io.anserini.TestUtils;

public class GenerateRerankerRequestsTest extends StdOutStdErrRedirectableTestCase {
  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
  }

  @After
  public void cleanUp() throws Exception {
    restoreStdOut();
    restoreStdErr();
  }

  @Test
  public void testPrebuilt() throws Exception {
    GenerateRerankerRequests.Args args = new GenerateRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run4";
    args.topics = "cacm";
    args.output = "test_reranker_requests.jsonl";

    GenerateRerankerRequests<String> outputRerankerRequests = new GenerateRerankerRequests<>(args);
    outputRerankerRequests.close();
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testParseTopics() throws Exception {
    GenerateRerankerRequests.Args args = new GenerateRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run4";
    args.topics = "cacm.bge-base-en-v1.5";
    args.output = "test_reranker_requests.jsonl";

    GenerateRerankerRequests<String> outputRerankerRequests = new GenerateRerankerRequests<>(args);
    outputRerankerRequests.close();
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testLocalIndex() throws Exception {
    GenerateRerankerRequests.Args args = new GenerateRerankerRequests.Args();
    args.index = "src/test/resources/prebuilt_indexes/raw-beir-collection1-index";
    args.run = "src/test/resources/sample_runs/run4";
    args.topics = "cacm";
    args.output = "test_reranker_requests.jsonl";

    GenerateRerankerRequests<String> outputRerankerRequests = new GenerateRerankerRequests<>(args);
    outputRerankerRequests.close();
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testLocalTopics() throws Exception {
    GenerateRerankerRequests.Args args = new GenerateRerankerRequests.Args();
    args.index = "cacm";
    args.run = "src/test/resources/sample_runs/run4";
    args.topics = "src/test/resources/sample_topics/acl_topics.tsv";
    args.output = "test_reranker_requests.jsonl";

    GenerateRerankerRequests<String> outputRerankerRequests = new GenerateRerankerRequests<>(args);
    outputRerankerRequests.close();
    assertTrue(!err.toString().contains("Error: "));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testBadIndex() throws Exception {
    String[] rerankArgs = new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "-run", "src/test/resources/sample_runs/run4",
        "-topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "-output", "test_reranker_requests.jsonl"
    };

    GenerateRerankerRequests.main(rerankArgs);
    assertTrue(err.toString().contains("Raw document with docid "));
    assertTrue(err.toString().contains("not found in index."));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testBadTopics() throws Exception {
    String[] rerankArgs = new String[] {
        "-index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "-run", "src/test/resources/sample_runs/run1",
        "-topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "-output", "test_reranker_requests.jsonl"
    };

    GenerateRerankerRequests.main(rerankArgs);
    assertTrue(err.toString().contains("Query ID not found in the list of topics:"));
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testGenerate() throws Exception {
    String[] rerankArgs = new String[] {
        "-index", "src/test/resources/prebuilt_indexes/raw-beir-collection1-index",
        "-run", "src/test/resources/sample_runs/run5",
        "-topics", "src/test/resources/sample_topics/acl_topics.tsv",
        "-output", "test_reranker_requests.jsonl"
    };

    GenerateRerankerRequests.main(rerankArgs);
    assertTrue(!err.toString().contains("Error: "));
    
    try {
      TestUtils.checkFile("test_reranker_requests.jsonl", new String[]{
        "{\"query\":{\"text\":\"model\",\"qid\":\"1\"},\"candidates\":[{\"docid\":\"doc1\",\"score\":7.0,\"doc\":{\"title\":\"doc1 title\",\"text\":\"doc1 text\"}},{\"docid\":\"doc2\",\"score\":6.0,\"doc\":{\"title\":\"doc2 title\",\"text\":\"doc2 text\"}}]}",
        "{\"query\":{\"text\":\"hpsg\",\"qid\":\"2\"},\"candidates\":[{\"docid\":\"doc3\",\"score\":20.0,\"doc\":{\"title\":\"doc3 title\",\"text\":\"doc3 text\"}},{\"docid\":\"doc1\",\"score\":14.0,\"doc\":{\"title\":\"doc1 title\",\"text\":\"doc1 text\"}}]}"
      });
    } catch (AssertionError e) {
      TestUtils.checkFile("test_reranker_requests.jsonl", new String[]{
        "{\"query\":{\"qid\":\"1\",\"text\":\"model\"},\"candidates\":[{\"docid\":\"doc1\",\"score\":7.0,\"doc\":{\"title\":\"doc1 title\",\"text\":\"doc1 text\"}},{\"docid\":\"doc2\",\"score\":6.0,\"doc\":{\"title\":\"doc2 title\",\"text\":\"doc2 text\"}}]}",
        "{\"query\":{\"qid\":\"2\",\"text\":\"hpsg\"},\"candidates\":[{\"docid\":\"doc3\",\"score\":20.0,\"doc\":{\"title\":\"doc3 title\",\"text\":\"doc3 text\"}},{\"docid\":\"doc1\",\"score\":14.0,\"doc\":{\"title\":\"doc1 title\",\"text\":\"doc1 text\"}}]}"
      });
    }
    assertTrue(new File("test_reranker_requests.jsonl").delete());
  }

  @Test
  public void testAmbiguousIndexLabelAndLocalPath() throws Exception {
    // Create a local directory that matches a known prebuilt label to force ambiguity.
    final String prebuiltLabel = "msmarco-v1-passage";
    File localDir = new File(prebuiltLabel);
    if (localDir.exists()) {
      // If it already exists, fail to avoid interfering with other tests.
      throw new IllegalStateException("Unexpected pre-existing directory: " + localDir.getAbsolutePath());
    }

    try {
      if (!localDir.mkdir()) {
        throw new IllegalStateException("Failed to create test directory: " + localDir.getAbsolutePath());
      }

      GenerateRerankerRequests.Args args = new GenerateRerankerRequests.Args();
      args.index = prebuiltLabel; // Ambiguous: label exists and local dir exists
      args.run = "src/test/resources/sample_runs/run4";
      args.topics = "cacm";
      args.output = "test_reranker_requests.jsonl";

      try (GenerateRerankerRequests<?> ignored = new GenerateRerankerRequests<>(args)) {
        assertTrue("Expected IllegalArgumentException due to ambiguous index reference", false);
      } catch (IllegalArgumentException e) {
        assertTrue(e.getMessage().contains("Ambiguous index reference"));
      }
    } finally {
      if (localDir.exists()) {
        assertTrue(localDir.delete());
      }
      // Ensure we don't leave the output file around if it was accidentally created
      new File("test_reranker_requests.jsonl").delete();
    }
  }
}
