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

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.TestUtils;

public class SearchCollectionTest extends StdOutStdErrRedirectableLuceneTestCase {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(SearchCollection.class.getName(), Level.ERROR);
  }

  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testAskForOptions() throws Exception {
    SearchCollection.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for SearchCollection"));
  }

  @Test
  public void testIncompleteOptions() throws Exception {
    SearchCollection.main(new String[] {});
    assertTrue(err.toString().contains("Option \"-index\" is required"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo"});
    assertTrue(err.toString().contains("Option \"-output\" is required"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz"});
    assertTrue(err.toString().contains("Option \"-topics\" is required"));
  }

  @Test
  public void testOptionErrors() throws Exception {
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",});
    assertTrue(err.toString().contains("\"foo\" does not appear to be a valid index."));
  }

  @Test
  public void testMutallyExclusive() throws Exception {
    // We can't exhaustively test all combinations, so we just sample a few combinations.
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-bm25", "-qld"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-bm25", "-bm25.accurate"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-bm25", "-qljm"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-qljm", "-spl"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-inl2", "-f2exp"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicReader", "baz", "-topics", "topic",
        "-f2log", "-f2exp"});
    assertTrue(err.toString().contains("cannot be used with the option"));
  }

  @Test
  public void testInvalidTopicReader() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "-topics", "src/test/resources/sample_topics/Trec",
        "-topicReader", "FakeTrec",
        "-output", "run.test", "-bm25"});
    assertTrue(err.toString().contains("Unable to load topic reader"));
  }

  @Test
  public void testInvalidFields() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "-topics", "src/test/resources/sample_topics/Trec",
        "-topicReader", "Trec",
        "-fields", "field1=a",
        "-output", "run.test", "-bm25"});
    assertTrue(err.toString().contains("Error parsing -fields"));
  }

  @Test
  public void testSearchLucene9() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "-topics", "src/test/resources/sample_topics/Trec",
        "-topicReader", "Trec",
        "-output", "run.test", "-bm25"});

    TestUtils.checkFile("run.test", new String[]{
        "1 Q0 DOC222 1 0.343200 Anserini",
        "1 Q0 TREC_DOC_1 2 0.333400 Anserini",
        "1 Q0 WSJ_1 3 0.068700 Anserini"});
    assertTrue(new File("run.test").delete());

    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_json_collection_tokenized/",
        "-topics", "src/test/resources/sample_topics/json_topics1.tsv",
        "-topicReader", "TsvInt",
        "-output", "run.test",
        "-pretokenized", "-impact"});

    TestUtils.checkFile("run.test", new String[]{
        "1 Q0 2000001 1 4.000000 Anserini",});
    assertTrue(new File("run.test").delete());
  }

  @Test
  public void testSearchLucene8() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene8-index.sample_docs_trec_collection2/",
        "-topics", "src/test/resources/sample_topics/Trec",
        "-topicReader", "Trec",
        "-output", "run.test", "-bm25"});

    TestUtils.checkFile("run.test", new String[]{
        "1 Q0 DOC222 1 0.343192 Anserini",
        "1 Q0 TREC_DOC_1 2 0.333445 Anserini",
        "1 Q0 WSJ_1 3 0.068654 Anserini"});
    assertTrue(new File("run.test").delete());

    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene8-index.sample_docs_json_collection_tokenized/",
        "-topics", "src/test/resources/sample_topics/json_topics1.tsv",
        "-topicReader", "TsvInt", "-output",
        "run.test", "-pretokenized", "-impact"});

    TestUtils.checkFile("run.test", new String[]{
        "1 Q0 2000001 1 4.000000 Anserini",});
    assertTrue(new File("run.test").delete());
  }

  @Test
  public void testSpecifyTopicsAsSymbol() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_trec_collection2/",
        "-topics", "TREC2019_DL_PASSAGE",
        "-output", "run.test", "-bm25"});

    // Not checking content, just checking if the topics were loaded successfully.
    File f = new File("run.test");
    assertTrue(f.exists());
    f.delete();
  }

  @Test
  public void testSearchBackgroundLinkingBm25_1() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.no-raw_no-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with no raw, no docvectors - should get an error.
    assertTrue(err.toString().contains("java.lang.RuntimeException: Raw documents not stored!"));
  }

  @Test
  public void testSearchBackgroundLinkingBm25_2() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.no-raw_with-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with no raw, no docvectors - should get an error.
    assertTrue(err.toString().contains("java.lang.RuntimeException: Raw documents not stored!"));
  }

  @Test
  public void testSearchBackgroundLinkingBm25_3() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.with-raw_with-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with raw, with docvectors - should run fine.
    TestUtils.checkFile("run.test", new String[]{
        "321 Q0 eacd327b20aa77a2aa909596ae336497 1 7.792500 Anserini",
        "321 Q0 dafe3110-4a9e-11e6-acbc-4d4870a079da 2 5.247200 Anserini"});
    assertTrue(new File("run.test").delete());
  }

  @Test
  public void testSearchBackgroundLinkingBm25_4() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.with-raw_no-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25",
        "-backgroundLinking", "-backgroundLinking.k", "100",
        "-collection", "WashingtonPostCollection"});

    // Running on index with raw, no docvectors - needs -collection WashingtonPostCollection
    TestUtils.checkFile("run.test", new String[]{
        "321 Q0 eacd327b20aa77a2aa909596ae336497 1 7.792500 Anserini",
        "321 Q0 dafe3110-4a9e-11e6-acbc-4d4870a079da 2 5.247200 Anserini"});
    assertTrue(new File("run.test").delete());
  }

  @Test
  public void testSearchBackgroundLinkingBm25Rm3_1() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.no-raw_no-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25", "-rm3",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with no raw, no docvectors - should get an error.
    assertTrue(err.toString().contains("java.lang.RuntimeException: Raw documents not stored!"));
  }

  @Test
  public void testSearchBackgroundLinkingBm25Rm3_2() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.no-raw_with-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25", "-rm3",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with no raw, no docvectors - should get an error.
    assertTrue(err.toString().contains("java.lang.RuntimeException: Raw documents not stored!"));
  }

  @Test
  public void testSearchBackgroundLinkingBm25Rm3_3() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.with-raw_with-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25", "-rm3",
        "-backgroundLinking", "-backgroundLinking.k", "100"});

    // Running on index with raw, with docvectors - should run fine.
    TestUtils.checkFile("run.test", new String[]{
        "321 Q0 eacd327b20aa77a2aa909596ae336497 1 0.039000 Anserini",
        "321 Q0 dafe3110-4a9e-11e6-acbc-4d4870a079da 2 0.026200 Anserini",
        "321 Q0 03049850-58e3-11e6-8b48-0cb344221131 3 0.011300 Anserini"});
    assertTrue(new File("run.test").delete());
  }

  @Test
  public void testSearchBackgroundLinkingBm25Rm3_4() throws Exception {
    SearchCollection.main(new String[] {
        "-index", "src/test/resources/prebuilt_indexes/lucene-inverted.sample-wapo.with-raw_no-docvectors/",
        "-topics", "src/test/resources/sample_topics/bglinking.txt",
        "-topicReader", "BackgroundLinking",
        "-output", "run.test", "-bm25", "-rm3",
        "-backgroundLinking", "-backgroundLinking.k", "100",
        "-collection", "WashingtonPostCollection"});

    // Running on index with raw, no docvectors - needs -collection WashingtonPostCollection
    TestUtils.checkFile("run.test", new String[]{
        "321 Q0 eacd327b20aa77a2aa909596ae336497 1 0.039000 Anserini",
        "321 Q0 dafe3110-4a9e-11e6-acbc-4d4870a079da 2 0.026200 Anserini",
        "321 Q0 03049850-58e3-11e6-8b48-0cb344221131 3 0.011300 Anserini"});
    assertTrue(new File("run.test").delete());
  }
}
