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

package io.anserini.cli;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.index.IndexCollection;

public class SearchTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static Path cacmIndexPath;

  @BeforeClass
  public static void setupClass() throws Exception {
    cacmIndexPath = Files.createTempDirectory("anserini-search-test-cacm");
    IndexCollection.main(new String[] {
        "-collection", "HtmlCollection",
        "-generator", "DefaultLuceneDocumentGenerator",
        "-threads", "1",
        "-input", "src/main/resources/cacm",
        "-index", cacmIndexPath.toString(),
        "-storePositions",
        "-storeDocvectors",
        "-storeContents",
        "-storeRaw",
        "-quiet"
    });
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if (cacmIndexPath != null) {
      FileUtils.deleteDirectory(cacmIndexPath.toFile());
    }
  }

  @Before
  public void setUp() throws Exception {
    redirectStdOut();
    redirectStdErr();
    super.setUp();
  }

  @After
  public void tearDown() throws Exception {
    restoreStdIn();
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testHelpOption() {
    Search.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for Search:"));
    assertTrue(err.toString().contains("--help"));
    assertFalse(err.toString().contains("Option \"--index\" is required"));
  }

  @Test
  public void testMissingRequiredIndexOption() {
    Search.main(new String[] {});
    assertTrue(err.toString().contains("Option \"--index\" is required"));
  }

  @Test
  public void testMissingQueryInNonInteractiveMode() {
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--trec"});
    assertTrue(err.toString().contains("Error: --query is required when not running in interactive mode"));
  }

  @Test
  public void testMutuallyExclusiveInteractiveAndQuery() {
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", "information retrieval", "--interactive", "--trec"});
    assertTrue(err.toString().contains("Error: --interactive and --query are mutually exclusive"));
  }

  @Test
  public void testInvalidHitsOption() {
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", "information retrieval", "--trec", "--hits", "0"});
    assertTrue(err.toString().contains("Error: --hits must be positive"));
  }

  @Test
  public void testRequiresExactlyOneOutputMode() {
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", "information retrieval", "--json", "--trec"});
    assertTrue(err.toString().contains("Error: exactly one of --json or --trec must be specified"));

    out.reset();
    err.reset();

    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", "information retrieval"});
    assertTrue(err.toString().contains("Error: exactly one of --json or --trec must be specified"));
  }

  @Test
  public void testSearchTrecOutputWithCacm() {
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", "information retrieval", "--trec", "--hits", "2"});

    List<String> trecLines = extractTrecLines(out.toString());
    assertEquals(2, trecLines.size());
    assertTrue(isValidTrecLine(trecLines.get(0)));
    assertTrue(isValidTrecLine(trecLines.get(1)));
  }

  @Test
  public void testSearchJsonOutputWithCacm() throws Exception {
    String query = "information retrieval";
    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--query", query, "--json", "--hits", "2"});

    String jsonLine = extractLastNonEmptyLine(out.toString());
    JsonNode root = MAPPER.readTree(jsonLine);
    assertEquals(query, root.get("query").get("text").asText());
    assertEquals(2, root.get("candidates").size());
    assertTrue(root.get("candidates").get(0).has("docid"));
    assertTrue(root.get("candidates").get(0).has("score"));
    assertTrue(root.get("candidates").get(0).has("doc"));
  }

  @Test
  public void testInteractiveSearchTrecOutpuWithCacm() {
    String stdin = "information retrieval\n";
    redirectStdIn(new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)));

    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--interactive", "--trec", "--hits", "1"});

    List<String> trecLines = extractTrecLines(out.toString());
    assertEquals(1, trecLines.size());
    assertTrue(isValidTrecLine(trecLines.get(0)));
  }

  @Test
  public void testInteractiveSearchJsonOutputWithCacm() throws Exception {
    String query = "information retrieval";
    String stdin = query + "\n";
    redirectStdIn(new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)));

    Search.main(new String[] {"--index", cacmIndexPath.toString(), "--interactive", "--json", "--hits", "1"});

    String jsonLine = extractLastNonEmptyLine(out.toString());
    JsonNode root = MAPPER.readTree(jsonLine);
    assertEquals(query, root.get("query").get("text").asText());
    assertEquals(1, root.get("candidates").size());
    assertTrue(root.get("candidates").get(0).has("docid"));
    assertTrue(root.get("candidates").get(0).has("score"));
    assertTrue(root.get("candidates").get(0).has("doc"));
  }

  @Test
  public void testSearchOutputWithCacm() throws Exception {
    assertSearchJsonOutput("src/test/resources/prebuilt_indexes/lucene-inverted.sample_cacm.store_raw", "preliminary");
  }

  @Test
  public void testSearchOutputWithMsmarcoV1Passage() throws Exception {
    assertSearchJsonOutput("src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v1-passage.store_raw", "obliterated");
  }

  @Test
  public void testSearchOutputWithMsmarcoV21DocSegmented() throws Exception {
    assertSearchJsonOutput("src/test/resources/prebuilt_indexes/lucene-inverted.sample_msmarco-v2.1-doc-segmented.store_raw", "demerara");
  }

  @Test
  public void testSearchOutputWithBeirNfcorpus() throws Exception {
    assertSearchJsonOutput("src/test/resources/prebuilt_indexes/lucene-inverted.sample_beir-nfcorpus.flat.store_raw", "statin");
  }

  private void assertSearchJsonOutput(String index, String query) throws Exception {
    for (boolean noParse : new boolean[] {false, true}) {
      out.reset();
      err.reset();

      if (noParse) {
        Search.main(new String[] {"--index", index, "--query", query, "--hits", "1", "--json", "--no-parse"});
      } else {
        Search.main(new String[] {"--index", index, "--query", query, "--hits", "1", "--json"});
      }

      String jsonLine = extractLastNonEmptyLine(out.toString());
      JsonNode root = MAPPER.readTree(jsonLine);
      JsonNode candidate = root.get("candidates").get(0);

      assertEquals(query, root.get("query").get("text").asText());
      assertEquals(1, root.get("candidates").size());
      assertTrue(candidate.has("docid"));
      assertTrue(candidate.has("score"));
      assertTrue(candidate.has("doc"));
      if (noParse) {
        assertTrue(candidate.get("doc").isTextual());
      }
    }
  }

  private static List<String> extractTrecLines(String output) {
    return Arrays.stream(output.split("\\R"))
        .map(String::trim)
        .filter(line -> !line.isEmpty())
        .filter(line -> line.contains(" Q0 "))
        .filter(line -> line.endsWith(" anserini"))
        .collect(Collectors.toList());
  }

  private static boolean isValidTrecLine(String line) {
    String[] fields = line.split("\\s+");
    return fields.length == 6 && "Q0".equals(fields[1]) && "anserini".equals(fields[5]);
  }

  private static String extractLastNonEmptyLine(String output) {
    String[] lines = output.split("\\R");
    for (int i = lines.length - 1; i >= 0; i--) {
      if (!lines[i].isBlank()) {
        return lines[i];
      }
    }
    return "";
  }
}
