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

package io.anserini.encoder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.lucene.tests.util.LuceneTestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

@LuceneTestCase.SuppressSysoutChecks(bugUrl = "output comes from args4j and EncodeQuery stderr")
public class EncodeQueryTest extends StdOutStdErrRedirectableLuceneTestCase {

  private static final String QUERIES = "src/test/resources/sample_topics/encode-query-test-queries.jsonl";
  private static final ObjectMapper MAPPER = new ObjectMapper();

  @BeforeClass
  public static void setupClass() {
    suppressJvmLogging();
    Configurator.setLevel(EncodeQuery.class.getName(), Level.ERROR);
  }

  @Before
  public void setUp() throws Exception {
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdErr();
    super.tearDown();
  }

  /** Finds the JSONL line whose "id" or "qid" field matches the given id. */
  private static JsonNode findById(List<String> lines, String id) throws Exception {
    for (String line : lines) {
      JsonNode node = MAPPER.readTree(line);
      JsonNode idNode = node.has("id") ? node.get("id") : node.get("qid");
      if (idNode != null && idNode.asText().equals(id)) return node;
    }
    throw new AssertionError("No record with id=" + id + " in output");
  }

  /** Finds the TSV line whose first tab-separated field matches the given id. */
  private static String[] findTsvById(List<String> lines, String id) {
    for (String line : lines) {
      String[] parts = line.split("\t", 2);
      if (parts.length == 2 && parts[0].equals(id)) return parts;
    }
    throw new AssertionError("No TSV record with id=" + id + " in output");
  }

  // --- CLI argument validation ---

  @Test(expected = IllegalArgumentException.class)
  public void testBuildEncoderUnknownThrows() throws Exception {
    EncodeQuery.buildEncoder("UnknownEncoder");
  }

  @Test
  public void testMissingEncoder() throws Exception {
    EncodeQuery.main(new String[]{});
    assertTrue(err.toString().contains("Option \"-encoder\" is required"));
  }

  @Test
  public void testMissingQueries() throws Exception {
    EncodeQuery.main(new String[]{"-encoder", "SpladeV3", "-output", "out.jsonl"});
    assertTrue(err.toString().contains("Option \"-queries\" is required"));
  }

  @Test
  public void testMissingOutput() throws Exception {
    EncodeQuery.main(new String[]{"-encoder", "SpladeV3", "-queries", "topics.jsonl"});
    assertTrue(err.toString().contains("Option \"-output\" is required"));
  }

  @Test
  public void testUnknownEncoderPrintsError() throws Exception {
    File output = File.createTempFile("encode-query-test", ".jsonl");
    output.deleteOnExit();
    EncodeQuery.main(new String[]{
        "-encoder", "NoSuchEncoder",
        "-queries", QUERIES,
        "-output", output.getAbsolutePath()
    });
    assertTrue(err.toString().contains("Unknown encoder 'NoSuchEncoder'"));
  }

  @Test
  public void testNonExistentTopicsFilePrintsError() throws Exception {
    File output = File.createTempFile("encode-query-test", ".jsonl");
    output.deleteOnExit();
    EncodeQuery.main(new String[]{
        "-encoder", "SpladeV3",
        "-queries", "/nonexistent/path/to/topics.jsonl",
        "-output", output.getAbsolutePath()
    });
    assertTrue(err.toString().contains("No topics loaded"));
  }
  // --- Sparse encoding ---

  @Test
  public void testSparseJsonlOutput() throws Exception {
    File output = File.createTempFile("encode-query-test", ".jsonl");
    output.deleteOnExit();
    EncodeQuery.main(new String[]{
        "-encoder", "SpladeV3",
        "-queries", QUERIES,
        "-output", output.getAbsolutePath()
    });

    List<String> lines = Files.readAllLines(output.toPath());
    assertEquals(2, lines.size());
    for (String line : lines) {
      JsonNode node = MAPPER.readTree(line);
      assertNotNull(node.get("id"));
      assertFalse(node.get("title").asText().isEmpty());
    }

    // "what is paula deen's brother" → SpladeV3 expands to include "paula" and "brother"
    JsonNode paulaNode = findById(lines, "1048585");
    assertTrue(paulaNode.get("title").asText().contains("paula"));
    assertTrue(paulaNode.get("title").asText().contains("brother"));

    // "Androgen receptor define" → expands to include "receptor" and "hormone"
    JsonNode androgenNode = findById(lines, "2");
    assertTrue(androgenNode.get("title").asText().contains("receptor"));
    assertTrue(androgenNode.get("title").asText().contains("hormone"));
  }

  @Test
  public void testSparseCompressedOutput() throws Exception {
    File output = File.createTempFile("encode-query-test", ".jsonl.gz");
    output.deleteOnExit();
    EncodeQuery.main(new String[]{
        "-encoder", "SpladeV3",
        "-queries", QUERIES,
        "-compress",
        "-output", output.getAbsolutePath()
    });

    List<String> lines = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(
        new GZIPInputStream(new FileInputStream(output)), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) lines.add(line);
    }
    assertEquals(2, lines.size());

    JsonNode paulaNode = findById(lines, "1048585");
    assertTrue(paulaNode.get("title").asText().contains("paula"));
  }

  // --- Dense encoding ---

  @Test
  public void testDenseTsvOutput() throws Exception {
    File output = File.createTempFile("encode-query-test", ".tsv");
    output.deleteOnExit();
    EncodeQuery.main(new String[]{
        "-encoder", "CosDprDistil",
        "-queries", QUERIES,
        "-outputFormat", "tsv",
        "-output", output.getAbsolutePath()
    });

    List<String> lines = Files.readAllLines(output.toPath());
    assertEquals(2, lines.size());
    for (String line : lines) {
      String[] parts = line.split("\t", 2);
      assertEquals(2, parts.length);
      assertEquals(768, parts[1].split(" ").length);
    }

    String[] paulaParts = findTsvById(lines, "1048585");
    assertEquals(768, paulaParts[1].split(" ").length);
  }
}
