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

import io.anserini.TestUtils;
import io.anserini.index.AbstractIndexer;
import io.anserini.index.IndexFlatDenseVectors;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SearchFlatDenseVectors}
 */
public class SearchFlatDenseVectorsTest {
  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(AbstractIndexer.class.getName(), Level.ERROR);
    Configurator.setLevel(IndexFlatDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(SearchFlatDenseVectors.class.getName(), Level.ERROR);
    Configurator.setLevel(FlatDenseSearcher.class.getName(), Level.ERROR);
  }

  @Test
  public void testEmptyInvocation() throws Exception {
    redirectStderr();

    SearchFlatDenseVectors.main(new String[] {});
    assertTrue(err.toString().contains("Error"));
    assertTrue(err.toString().contains("is required"));

    restoreStderr();
  }

  @Test
  public void testAskForHelp() throws Exception {
    redirectStderr();

    SearchFlatDenseVectors.main(new String[] {"-options"});
    assertTrue(err.toString().contains("Options for"));

    restoreStderr();
  }

  @Test
  public void testInvalidIndex1() throws Exception {
    redirectStderr();

    // Fake path that doesn't exist.
    String[] searchArgs = new String[] {
        "-index", "/fake/path",
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", "target/run-" + System.currentTimeMillis(),
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: \"/fake/path\" does not appear to be a valid index.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void testInvalidIndex2() throws Exception {
    redirectStderr();

    // Path that does exist, but isn't an index.
    String[] searchArgs = new String[] {
        "-index", "/fake/path",
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", "target/run-" + System.currentTimeMillis(),
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: \"/fake/path\" does not appear to be a valid index.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidTopics() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "fake/topics/here",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};

    redirectStderr();
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: \"fake/topics/here\" does not refer to valid topics.\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidReader() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "FakeJsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};

    redirectStderr();
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load topic reader \"FakeJsonIntVector\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidTopicField() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "fake_field",
        "-hits", "5"};

    redirectStderr();
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to read topic field \"fake_field\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidGenerator() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "FakeVectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};

    redirectStderr();
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load QueryGenerator \"FakeVectorQueryGenerator\".\n", err.toString());
    restoreStderr();
  }

  @Test
  public void searchInvalidEncoder() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-encoder", "FakeEncoder",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};

    redirectStderr();
    SearchFlatDenseVectors.main(searchArgs);

    assertEquals("Error: Unable to load Encoder \"FakeEncoder\".\n", err.toString());
    restoreStderr();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicAda2() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "160885 Q0 45 1 0.863064 Anserini",
        "160885 Q0 44 2 0.861596 Anserini",
        "160885 Q0 40 3 0.858651 Anserini",
        "160885 Q0 48 4 0.858514 Anserini",
        "160885 Q0 41 5 0.856265 Anserini",
        "867490 Q0 10 1 0.850331 Anserini",
        "867490 Q0 45 2 0.846281 Anserini",
        "867490 Q0 44 3 0.845236 Anserini",
        "867490 Q0 95 4 0.845013 Anserini",
        "867490 Q0 97 5 0.844905 Anserini"
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicCosDpr() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cosdpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cosdpr-distil.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "2 Q0 208 1 0.578725 Anserini",
        "2 Q0 224 2 0.578704 Anserini",
        "2 Q0 384 3 0.573909 Anserini",
        "2 Q0 136 4 0.573040 Anserini",
        "2 Q0 720 5 0.571078 Anserini",
        "1048585 Q0 624 1 0.568415 Anserini",
        "1048585 Q0 120 2 0.563448 Anserini",
        "1048585 Q0 320 3 0.558943 Anserini",
        "1048585 Q0 232 4 0.550981 Anserini",
        "1048585 Q0 328 5 0.550971 Anserini"
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicCosDprQuantized() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cosdpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1", "-quantize.int8"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cosdpr-distil.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "2 Q0 224 1 0.579050 Anserini",
        "2 Q0 208 2 0.577672 Anserini",
        "2 Q0 384 3 0.572705 Anserini",
        "2 Q0 136 4 0.572389 Anserini",
        "2 Q0 720 5 0.568491 Anserini",
        "1048585 Q0 624 1 0.569788 Anserini",
        "1048585 Q0 120 2 0.564118 Anserini",
        "1048585 Q0 320 3 0.559633 Anserini",
        "1048585 Q0 328 4 0.550906 Anserini",
        "1048585 Q0 232 5 0.550473 Anserini"
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicCosDprSpecifyTopicsAsSymbol() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cosdpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "TREC2019_DL_PASSAGE_COSDPR_DISTIL",
        "-output", runfile,
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    // Not checking content, just checking if the topics were loaded successfully.
    File f = new File(runfile);
    assertTrue(f.exists());
    f.delete();

    runfile = "target/run-" + System.currentTimeMillis();
    searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "TREC2019_DL_PASSAGE",
        "-encoder", "CosDprDistil",
        "-output", runfile,
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    // Not checking content, just checking if the topics were loaded successfully.
    f = new File(runfile);
    assertTrue(f.exists());
    f.delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testBasicWithOnnx() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/cosdpr-distil/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile;
    String[] searchArgs;

    runfile = "target/run-" + System.currentTimeMillis();
    searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cosdpr-distil.tsv",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-encoder", "CosDprDistil",
        "-topicReader", "TsvInt",
        "-topicField", "title",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    // Note output is slightly different from pre-encoded query vectors.
    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "2 Q0 208 1 0.578723 Anserini",
        "2 Q0 224 2 0.578716 Anserini",
        "2 Q0 384 3 0.573913 Anserini",
        "2 Q0 136 4 0.573051 Anserini",
        "2 Q0 720 5 0.571061 Anserini",
        "1048585 Q0 624 1 0.568417 Anserini",
        "1048585 Q0 120 2 0.563483 Anserini",
        "1048585 Q0 320 3 0.558932 Anserini",
        "1048585 Q0 328 4 0.550985 Anserini",
        "1048585 Q0 232 5 0.550977 Anserini"
    });

    new File(runfile).delete();

    runfile = "target/run-" + System.currentTimeMillis();
    searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-cosdpr-distil.tsv",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        // This works regardless of whether we specify the suffix (Encoder) or not.
        "-encoder", "CosDprDistilEncoder",
        "-topicReader", "TsvInt",
        "-topicField", "title",
        "-hits", "5"};
    SearchFlatDenseVectors.main(searchArgs);

    // Note output is slightly different from pre-encoded query vectors.
    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "2 Q0 208 1 0.578723 Anserini",
        "2 Q0 224 2 0.578716 Anserini",
        "2 Q0 384 3 0.573913 Anserini",
        "2 Q0 136 4 0.573051 Anserini",
        "2 Q0 720 5 0.571061 Anserini",
        "1048585 Q0 624 1 0.568417 Anserini",
        "1048585 Q0 120 2 0.563483 Anserini",
        "1048585 Q0 320 3 0.558932 Anserini",
        "1048585 Q0 328 4 0.550985 Anserini",
        "1048585 Q0 232 5 0.550977 Anserini"
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testRemoveQuery() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector/",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2a.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonStringVector",
        "-topicField", "vector",
        "-hits", "5",
        "-removeQuery"};
    SearchFlatDenseVectors.main(searchArgs);

    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "10 Q0 45 1 0.846281 Anserini",
        "10 Q0 44 2 0.845236 Anserini",
        "10 Q0 95 3 0.845013 Anserini",
        "10 Q0 97 4 0.844905 Anserini",
        "45 Q0 44 1 0.861596 Anserini",
        "45 Q0 40 2 0.858651 Anserini",
        "45 Q0 48 3 0.858514 Anserini",
        "45 Q0 41 4 0.856264 Anserini",
    });

    new File(runfile).delete();
  }

  @Test
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public void testPassage() throws Exception {
    String indexPath = "target/lucene-test-index.flat." + System.currentTimeMillis();
    String[] indexArgs = new String[] {
        "-collection", "JsonDenseVectorCollection",
        "-input", "src/test/resources/sample_docs/openai_ada2/json_vector2",
        "-index", indexPath,
        "-generator", "DenseVectorDocumentGenerator",
        "-threads", "1"
    };

    IndexFlatDenseVectors.main(indexArgs);

    String runfile = "target/run-" + System.currentTimeMillis();
    String[] searchArgs = new String[] {
        "-index", indexPath,
        "-topics", "src/test/resources/sample_topics/sample-topics.msmarco-passage-dev-openai-ada2.jsonl",
        "-output", runfile,
        "-generator", "VectorQueryGenerator",
        "-topicReader", "JsonIntVector",
        "-topicField", "vector",
        "-selectMaxPassage",
        "-selectMaxPassage.hits", "5",
        "-hits", "10"};
    SearchFlatDenseVectors.main(searchArgs);

    TestUtils.checkRunFileApproximate(runfile, new String[] {
        "160885 Q0 44 1 0.863064 Anserini",
        "160885 Q0 40 2 0.858651 Anserini",
        "160885 Q0 48 3 0.858514 Anserini",
        "160885 Q0 a 4 0.856264 Anserini",
        "160885 Q0 46 5 0.849332 Anserini",
        "867490 Q0 10 1 0.850332 Anserini",
        "867490 Q0 44 2 0.846281 Anserini",
        "867490 Q0 b 3 0.845013 Anserini",
        "867490 Q0 40 4 0.837815 Anserini",
        "867490 Q0 46 5 0.837050 Anserini"
    });

    new File(runfile).delete();
  }
}
