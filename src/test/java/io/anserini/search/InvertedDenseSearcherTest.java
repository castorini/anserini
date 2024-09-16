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

package io.anserini.search;

import io.anserini.index.IndexInvertedDenseVectors;
import io.anserini.search.topicreader.JsonIntVectorTopicReader;
import io.anserini.search.topicreader.TopicReader;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import static org.junit.Assert.assertEquals;

public class InvertedDenseSearcherTest {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(IndexInvertedDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(InvertedDenseSearcher.class.getName(), Level.ERROR);
  }

  @Test
  public void searchAda2FWTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "fw"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    InvertedDenseSearcher.Args args = new InvertedDenseSearcher.Args();
    args.index = indexPath;
    args.encoding = "fw";

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();
    Iterator<Integer> iterator = topics.keySet().iterator();

    try(InvertedDenseSearcher<Integer> searcher = new InvertedDenseSearcher<>(args)) {
      int qid = iterator.next();
      ScoredDoc[] results;

      assertEquals(160885, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("40", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("48", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(32.355999f, results[0].score, 10e-6);
      assertEquals(31.581369f, results[1].score, 10e-6);
      assertEquals(30.734432f, results[2].score, 10e-6);
      assertEquals(30.215816f, results[3].score, 10e-6);
      assertEquals(30.153873f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("40", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("48", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(32.355999f, results[0].score, 10e-6);
      assertEquals(31.581369f, results[1].score, 10e-6);
      assertEquals(30.734432f, results[2].score, 10e-6);
      assertEquals(30.215816f, results[3].score, 10e-6);
      assertEquals(30.153873f, results[4].score, 10e-6);

      qid = iterator.next();
      assertEquals(867490, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("95", results[1].docid);
      assertEquals("43", results[2].docid);
      assertEquals("10", results[3].docid);
      assertEquals("45", results[4].docid);

      assertEquals(33.122585f, results[0].score, 10e-6);
      assertEquals(32.564468f, results[1].score, 10e-6);
      assertEquals(31.937614f, results[2].score, 10e-6);
      assertEquals(31.408100f, results[3].score, 10e-6);
      assertEquals(30.429819f, results[4].score, 10e-6);

      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("95", results[1].docid);
      assertEquals("43", results[2].docid);
      assertEquals("10", results[3].docid);
      assertEquals("45", results[4].docid);

      assertEquals(33.122585f, results[0].score, 10e-6);
      assertEquals(32.564468f, results[1].score, 10e-6);
      assertEquals(31.937614f, results[2].score, 10e-6);
      assertEquals(31.408100f, results[3].score, 10e-6);
      assertEquals(30.429819f, results[4].score, 10e-6);
    }
  }

  @Test
  public void searchAda2FWBatchTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "fw"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    InvertedDenseSearcher.Args args = new InvertedDenseSearcher.Args();
    args.index = indexPath;
    args.encoding = "fw";

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();

    List<Integer> qids= new ArrayList<>();
    List<String> queries = new ArrayList<>();

    topics.forEach((qid, topic) -> {
      String query = topic.get("vector");
      assert query != null;
      qids.add(qid);
      queries.add(query);
    });

    try(InvertedDenseSearcher<Integer> searcher = new InvertedDenseSearcher<>(args)) {
      SortedMap<Integer, ScoredDoc[]> allResults = searcher.batch_search(queries, qids, 5, 2);

      ScoredDoc[] results = allResults.get(160885);
      assertEquals(5, results.length);
      assertEquals("40", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("48", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(32.355999f, results[0].score, 10e-6);
      assertEquals(31.581369f, results[1].score, 10e-6);
      assertEquals(30.734432f, results[2].score, 10e-6);
      assertEquals(30.215816f, results[3].score, 10e-6);
      assertEquals(30.153873f, results[4].score, 10e-6);

      results = allResults.get(867490);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("95", results[1].docid);
      assertEquals("43", results[2].docid);
      assertEquals("10", results[3].docid);
      assertEquals("45", results[4].docid);

      assertEquals(33.122585f, results[0].score, 10e-6);
      assertEquals(32.564468f, results[1].score, 10e-6);
      assertEquals(31.937614f, results[2].score, 10e-6);
      assertEquals(31.408100f, results[3].score, 10e-6);
      assertEquals(30.429819f, results[4].score, 10e-6);
    }
  }

  @Test
  public void searchAda2LLTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "lexlsh"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    InvertedDenseSearcher.Args args = new InvertedDenseSearcher.Args();
    args.index = indexPath;
    args.encoding = "lexlsh";

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();
    Iterator<Integer> iterator = topics.keySet().iterator();

    try(InvertedDenseSearcher<Integer> searcher = new InvertedDenseSearcher<>(args)) {
      int qid = iterator.next();
      ScoredDoc[] results;

      assertEquals(160885, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("4", results[1].docid);
      assertEquals("118", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("65", results[4].docid);

      assertEquals(82.128540f, results[0].score, 10e-6);
      assertEquals(79.793037f, results[1].score, 10e-6);
      assertEquals(77.931618f, results[2].score, 10e-6);
      assertEquals(75.614052f, results[3].score, 10e-6);
      assertEquals(74.778358f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("4", results[1].docid);
      assertEquals("118", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("65", results[4].docid);

      assertEquals(82.128540f, results[0].score, 10e-6);
      assertEquals(79.793037f, results[1].score, 10e-6);
      assertEquals(77.931618f, results[2].score, 10e-6);
      assertEquals(75.614052f, results[3].score, 10e-6);
      assertEquals(74.778358f, results[4].score, 10e-6);

      qid = iterator.next();
      assertEquals(867490, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("13", results[1].docid);
      assertEquals("10", results[2].docid);
      assertEquals("44", results[3].docid);
      assertEquals("67", results[4].docid);

      assertEquals(84.916107f, results[0].score, 10e-6);
      assertEquals(82.500229f, results[1].score, 10e-6);
      assertEquals(82.364830f, results[2].score, 10e-6);
      assertEquals(79.369530f, results[3].score, 10e-6);
      assertEquals(78.378647f, results[4].score, 10e-6);

      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("13", results[1].docid);
      assertEquals("10", results[2].docid);
      assertEquals("44", results[3].docid);
      assertEquals("67", results[4].docid);

      assertEquals(84.916107f, results[0].score, 10e-6);
      assertEquals(82.500229f, results[1].score, 10e-6);
      assertEquals(82.364830f, results[2].score, 10e-6);
      assertEquals(79.369530f, results[3].score, 10e-6);
      assertEquals(78.378647f, results[4].score, 10e-6);
    }
  }

  @Test
  public void searchAda2LLBatchTest() throws Exception {
    String indexPath = "target/idx-sample-fw-vector-" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath,
        "-encoding", "lexlsh"
    };
    IndexInvertedDenseVectors.main(indexArgs);

    InvertedDenseSearcher.Args args = new InvertedDenseSearcher.Args();
    args.index = indexPath;
    args.encoding = "lexlsh";

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();

    List<Integer> qids= new ArrayList<>();
    List<String> queries = new ArrayList<>();

    topics.forEach((qid, topic) -> {
      String query = topic.get("vector");
      assert query != null;
      qids.add(qid);
      queries.add(query);
    });

    try(InvertedDenseSearcher<Integer> searcher = new InvertedDenseSearcher<>(args)) {
      SortedMap<Integer, ScoredDoc[]> allResults = searcher.batch_search(queries, qids, 5, 2);

      ScoredDoc[] results = allResults.get(160885);
      assertEquals(5, results.length);
      assertEquals("97", results[0].docid);
      assertEquals("4", results[1].docid);
      assertEquals("118", results[2].docid);
      assertEquals("43", results[3].docid);
      assertEquals("65", results[4].docid);

      assertEquals(82.128540f, results[0].score, 10e-6);
      assertEquals(79.793037f, results[1].score, 10e-6);
      assertEquals(77.931618f, results[2].score, 10e-6);
      assertEquals(75.614052f, results[3].score, 10e-6);
      assertEquals(74.778358f, results[4].score, 10e-6);

      results = allResults.get(867490);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("13", results[1].docid);
      assertEquals("10", results[2].docid);
      assertEquals("44", results[3].docid);
      assertEquals("67", results[4].docid);

      assertEquals(84.916107f, results[0].score, 10e-6);
      assertEquals(82.500229f, results[1].score, 10e-6);
      assertEquals(82.364830f, results[2].score, 10e-6);
      assertEquals(79.369530f, results[3].score, 10e-6);
      assertEquals(78.378647f, results[4].score, 10e-6);
    }
  }
}
