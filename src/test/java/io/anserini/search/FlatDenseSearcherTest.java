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

import io.anserini.index.IndexFlatDenseVectors;
import io.anserini.search.topicreader.JsonIntVectorTopicReader;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.TsvIntTopicReader;
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

public class FlatDenseSearcherTest {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(IndexFlatDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(FlatDenseSearcher.class.getName(), Level.ERROR);
  }

  @Test
  public void testAda2() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    FlatDenseSearcher.Args args = new FlatDenseSearcher.Args();
    args.index = indexPath;

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();
    Iterator<Integer> iterator = topics.keySet().iterator();

    try(FlatDenseSearcher<Integer> searcher = new FlatDenseSearcher<>(args)) {
      int qid = iterator.next();
      ScoredDoc[] results;

      assertEquals(160885, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("40", results[2].docid);
      assertEquals("48", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(0.863064f, results[0].score, 10e-6);
      assertEquals(0.861596f, results[1].score, 10e-6);
      assertEquals(0.858651f, results[2].score, 10e-6);
      assertEquals(0.858514f, results[3].score, 10e-6);
      assertEquals(0.856264f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("40", results[2].docid);
      assertEquals("48", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(0.863064f, results[0].score, 10e-6);
      assertEquals(0.861596f, results[1].score, 10e-6);
      assertEquals(0.858651f, results[2].score, 10e-6);
      assertEquals(0.858514f, results[3].score, 10e-6);
      assertEquals(0.856264f, results[4].score, 10e-6);

      qid = iterator.next();
      assertEquals(867490, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("10", results[0].docid);
      assertEquals("45", results[1].docid);
      assertEquals("44", results[2].docid);
      assertEquals("95", results[3].docid);
      assertEquals("97", results[4].docid);

      assertEquals(0.850332f, results[0].score, 10e-6);
      assertEquals(0.846281f, results[1].score, 10e-6);
      assertEquals(0.845236f, results[2].score, 10e-6);
      assertEquals(0.845013f, results[3].score, 10e-6);
      assertEquals(0.844905f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("10", results[0].docid);
      assertEquals("45", results[1].docid);
      assertEquals("44", results[2].docid);
      assertEquals("95", results[3].docid);
      assertEquals("97", results[4].docid);

      assertEquals(0.850332f, results[0].score, 10e-6);
      assertEquals(0.846281f, results[1].score, 10e-6);
      assertEquals(0.845236f, results[2].score, 10e-6);
      assertEquals(0.845013f, results[3].score, 10e-6);
      assertEquals(0.844905f, results[4].score, 10e-6);
    }
  }

  @Test
  public void testAda2Batch() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    FlatDenseSearcher.Args args = new FlatDenseSearcher.Args();
    args.index = indexPath;

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

    try(FlatDenseSearcher<Integer> searcher = new FlatDenseSearcher<>(args)) {
      SortedMap<Integer, ScoredDoc[]> allResults = searcher.batch_search(queries, qids, 5, 2);

      ScoredDoc[] results = allResults.get(160885);
      assertEquals(5, results.length);
      assertEquals("45", results[0].docid);
      assertEquals("44", results[1].docid);
      assertEquals("40", results[2].docid);
      assertEquals("48", results[3].docid);
      assertEquals("41", results[4].docid);

      assertEquals(0.863064f, results[0].score, 10e-6);
      assertEquals(0.861596f, results[1].score, 10e-6);
      assertEquals(0.858651f, results[2].score, 10e-6);
      assertEquals(0.858514f, results[3].score, 10e-6);
      assertEquals(0.856264f, results[4].score, 10e-6);

      results = allResults.get(867490);
      assertEquals(5, results.length);
      assertEquals("10", results[0].docid);
      assertEquals("45", results[1].docid);
      assertEquals("44", results[2].docid);
      assertEquals("95", results[3].docid);
      assertEquals("97", results[4].docid);

      assertEquals(0.850332f, results[0].score, 10e-6);
      assertEquals(0.846281f, results[1].score, 10e-6);
      assertEquals(0.845236f, results[2].score, 10e-6);
      assertEquals(0.845013f, results[3].score, 10e-6);
      assertEquals(0.844905f, results[4].score, 10e-6);
    }
  }

  @Test
  public void testCosDpr() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cos-dpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    FlatDenseSearcher.Args args = new FlatDenseSearcher.Args();
    args.index = indexPath;

    TopicReader<Integer> topicReader = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cos-dpr-distil.jsonl"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();
    Iterator<Integer> iterator = topics.keySet().iterator();

    try(FlatDenseSearcher<Integer> searcher = new FlatDenseSearcher<>(args)) {
      int qid = iterator.next();
      ScoredDoc[] results;

      assertEquals(2, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("208", results[0].docid);
      assertEquals("224", results[1].docid);
      assertEquals("384", results[2].docid);
      assertEquals("136", results[3].docid);
      assertEquals("720", results[4].docid);

      assertEquals(0.578725f, results[0].score, 10e-6);
      assertEquals(0.578704f, results[1].score, 10e-6);
      assertEquals(0.573909f, results[2].score, 10e-6);
      assertEquals(0.573040f, results[3].score, 10e-6);
      assertEquals(0.571078f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("208", results[0].docid);
      assertEquals("224", results[1].docid);
      assertEquals("384", results[2].docid);
      assertEquals("136", results[3].docid);
      assertEquals("720", results[4].docid);

      assertEquals(0.578725f, results[0].score, 10e-6);
      assertEquals(0.578704f, results[1].score, 10e-6);
      assertEquals(0.573909f, results[2].score, 10e-6);
      assertEquals(0.573040f, results[3].score, 10e-6);
      assertEquals(0.571078f, results[4].score, 10e-6);

      qid = iterator.next();
      assertEquals(1048585, qid);
      results = searcher.search(qid, topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("624", results[0].docid);
      assertEquals("120", results[1].docid);
      assertEquals("320", results[2].docid);
      assertEquals("232", results[3].docid);
      assertEquals("328", results[4].docid);

      assertEquals(0.568415f, results[0].score, 10e-6);
      assertEquals(0.563448f, results[1].score, 10e-6);
      assertEquals(0.558943f, results[2].score, 10e-6);
      assertEquals(0.550981f, results[3].score, 10e-6);
      assertEquals(0.550971f, results[4].score, 10e-6);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("vector"), 5);
      assertEquals(5, results.length);
      assertEquals("624", results[0].docid);
      assertEquals("120", results[1].docid);
      assertEquals("320", results[2].docid);
      assertEquals("232", results[3].docid);
      assertEquals("328", results[4].docid);

      assertEquals(0.568415f, results[0].score, 10e-6);
      assertEquals(0.563448f, results[1].score, 10e-6);
      assertEquals(0.558943f, results[2].score, 10e-6);
      assertEquals(0.550981f, results[3].score, 10e-6);
      assertEquals(0.550971f, results[4].score, 10e-6);
    }
  }

  @Test
  public void testCosDprWithOnnx() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cos-dpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    FlatDenseSearcher.Args args = new FlatDenseSearcher.Args();
    args.index = indexPath;
    args.encoder = "CosDprDistil";

    TopicReader<Integer> topicReader = new TsvIntTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cos-dpr-distil.tsv"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();
    Iterator<Integer> iterator = topics.keySet().iterator();

    try(FlatDenseSearcher<Integer> searcher = new FlatDenseSearcher<>(args)) {
      int qid = iterator.next();
      ScoredDoc[] results;

      assertEquals(2, qid);
      results = searcher.search(qid, topics.get(qid).get("title"), 5);
      assertEquals(5, results.length);
      assertEquals("208", results[0].docid);
      assertEquals("224", results[1].docid);
      assertEquals("384", results[2].docid);
      assertEquals("136", results[3].docid);
      assertEquals("720", results[4].docid);

      assertEquals(0.578723f, results[0].score, 10e-4);
      assertEquals(0.578716f, results[1].score, 10e-4);
      assertEquals(0.573913f, results[2].score, 10e-4);
      assertEquals(0.573051f, results[3].score, 10e-4);
      assertEquals(0.571061f, results[4].score, 10e-4);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("title"), 5);
      assertEquals(5, results.length);
      assertEquals("208", results[0].docid);
      assertEquals("224", results[1].docid);
      assertEquals("384", results[2].docid);
      assertEquals("136", results[3].docid);
      assertEquals("720", results[4].docid);

      assertEquals(0.578723f, results[0].score, 10e-4);
      assertEquals(0.578716f, results[1].score, 10e-4);
      assertEquals(0.573913f, results[2].score, 10e-4);
      assertEquals(0.573051f, results[3].score, 10e-4);
      assertEquals(0.571061f, results[4].score, 10e-4);

      qid = iterator.next();
      assertEquals(1048585, qid);
      results = searcher.search(qid, topics.get(qid).get("title"), 5);
      assertEquals(5, results.length);
      assertEquals("624", results[0].docid);
      assertEquals("120", results[1].docid);
      assertEquals("320", results[2].docid);
      assertEquals("328", results[3].docid);
      assertEquals("232", results[4].docid);

      assertEquals(0.568417f, results[0].score, 10e-4);
      assertEquals(0.563483f, results[1].score, 10e-4);
      assertEquals(0.558932f, results[2].score, 10e-4);
      assertEquals(0.550985f, results[3].score, 10e-4);
      assertEquals(0.550977f, results[4].score, 10e-4);

      // Now test without qid
      results = searcher.search(topics.get(qid).get("title"), 5);
      assertEquals(5, results.length);
      assertEquals("624", results[0].docid);
      assertEquals("120", results[1].docid);
      assertEquals("320", results[2].docid);
      assertEquals("328", results[3].docid);
      assertEquals("232", results[4].docid);

      assertEquals(0.568417f, results[0].score, 10e-4);
      assertEquals(0.563483f, results[1].score, 10e-4);
      assertEquals(0.558932f, results[2].score, 10e-4);
      assertEquals(0.550985f, results[3].score, 10e-4);
      assertEquals(0.550977f, results[4].score, 10e-4);
    }
  }

  @Test
  public void testCosDprBatchWithOnnx() throws Exception {
    String indexPath = "target/idx-sample-hnsw" + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cos-dpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    FlatDenseSearcher.Args args = new FlatDenseSearcher.Args();
    args.index = indexPath;
    args.encoder = "CosDprDistil";

    TopicReader<Integer> topicReader = new TsvIntTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cos-dpr-distil.tsv"));

    SortedMap<Integer, Map<String, String>> topics = topicReader.read();

    List<Integer> qids= new ArrayList<>();
    List<String> queries = new ArrayList<>();

    topics.forEach((qid, topic) -> {
      String query = topic.get("title");
      assert query != null;
      qids.add(qid);
      queries.add(query);
    });

    try(FlatDenseSearcher<Integer> searcher = new FlatDenseSearcher<>(args)) {
      SortedMap<Integer, ScoredDoc[]> allResults = searcher.batch_search(queries, qids, 5, 2);

      ScoredDoc[] results = allResults.get(2);
      assertEquals(5, results.length);
      assertEquals("208", results[0].docid);
      assertEquals("224", results[1].docid);
      assertEquals("384", results[2].docid);
      assertEquals("136", results[3].docid);
      assertEquals("720", results[4].docid);

      assertEquals(0.578723f, results[0].score, 10e-4);
      assertEquals(0.578716f, results[1].score, 10e-4);
      assertEquals(0.573913f, results[2].score, 10e-4);
      assertEquals(0.573051f, results[3].score, 10e-4);
      assertEquals(0.571061f, results[4].score, 10e-4);

      results = allResults.get(1048585);
      assertEquals(5, results.length);
      assertEquals("624", results[0].docid);
      assertEquals("120", results[1].docid);
      assertEquals("320", results[2].docid);
      assertEquals("328", results[3].docid);
      assertEquals("232", results[4].docid);

      assertEquals(0.568417f, results[0].score, 10e-4);
      assertEquals(0.563483f, results[1].score, 10e-4);
      assertEquals(0.558932f, results[2].score, 10e-4);
      assertEquals(0.550985f, results[3].score, 10e-4);
      assertEquals(0.550977f, results[4].score, 10e-4);
    }
  }
}
