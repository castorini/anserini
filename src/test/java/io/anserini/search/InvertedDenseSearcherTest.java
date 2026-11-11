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

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.CustomAppender;
import io.anserini.LocaleTestUtils;
import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexInvertedDenseVectors;
import io.anserini.search.topicreader.JsonIntVectorTopicReader;
import io.anserini.search.topicreader.TopicReader;

public class InvertedDenseSearcherTest extends StdOutStdErrRedirectableLuceneTestCase {
  private final List<String> indexPaths = new ArrayList<>();

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.ERROR);
    Configurator.setLevel(IndexInvertedDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(InvertedDenseSearcher.class.getName(), Level.ERROR);
  }

  @After
  public void tearDown() throws Exception {
    for (String indexPath : indexPaths) {
      FileUtils.deleteDirectory(new File(indexPath));
    }
    indexPaths.clear();
    super.tearDown();
  }

  private String newIndexPath(String prefix) {
    String indexPath = prefix + System.currentTimeMillis();
    indexPaths.add(indexPath);
    return indexPath;
  }

  @Test
  public void searchAda2FWTest() throws Exception {
    String indexPath = newIndexPath("target/idx-sample-fw-vector-");
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
    String indexPath = newIndexPath("target/idx-sample-fw-vector-");
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
    String indexPath = newIndexPath("target/idx-sample-fw-vector-");
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
    String indexPath = newIndexPath("target/idx-sample-fw-vector-");
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

  @Test
  public void testBatchSearchProgressAcrossLocales() throws Exception {
    String indexPath = newIndexPath("target/idx-locale-inverted-dense-");
    IndexInvertedDenseVectors.main(new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-generator", "InvertedDenseVectorDocumentGenerator",
        "-index", indexPath, "-encoding", "fw"});
    InvertedDenseSearcher.Args args = new InvertedDenseSearcher.Args();
    args.index = indexPath;
    args.encoding = "fw";
    SortedMap<Integer, Map<String, String>> topics = new JsonIntVectorTopicReader(
        Path.of("src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl")).read();
    List<String> queries = Collections.nCopies(100, topics.get(topics.firstKey()).get("vector"));
    List<Integer> qids = java.util.stream.IntStream.range(0, 100).boxed().toList();
    List<String> messages = Collections.synchronizedList(new ArrayList<>());
    CustomAppender appender = new CustomAppender("locale-progress") {
      @Override
      public void append(LogEvent event) {
        messages.add(event.getMessage().getFormattedMessage());
      }
    };
    Logger logger = (Logger) LogManager.getLogger(InvertedDenseSearcher.class);
    Level previousLevel = logger.getLevel();
    boolean previousAdditivity = logger.isAdditive();
    Locale previous = Locale.getDefault();
    appender.start();
    logger.addAppender(appender);
    logger.setAdditive(false);
    logger.setLevel(Level.INFO);
    try (InvertedDenseSearcher<Integer> searcher = new InvertedDenseSearcher<>(args)) {
      for (Locale locale : LocaleTestUtils.LOCALES) {
        Locale.setDefault(locale);
        messages.clear();
        assertEquals(100, searcher.batch_search(queries, qids, 5, 2).size());
        assertEquals(messages.toString(), 2, messages.size());
        assertEquals("100 queries processed", messages.get(0));
        assertTrue(messages.get(1), messages.get(1).matches(
            "100 queries processed in .* = ~(?:[0-9]+\\.[0-9]{2}|Infinity|NaN) q/s"));
      }
    } finally {
      Locale.setDefault(previous);
      logger.setLevel(previousLevel);
      logger.setAdditive(previousAdditivity);
      logger.removeAppender(appender);
      appender.stop();
    }
  }
}
