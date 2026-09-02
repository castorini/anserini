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

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.eval.Qrels;

public class QrelsRegistryTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final TypeReference<List<String>> NAME_LIST_TYPE =
      new TypeReference<List<String>>() {};
  private static final TypeReference<Map<String, Object>> METADATA_TYPE =
      new TypeReference<Map<String, Object>>() {};

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
  public void testInvalidOption() {
    QrelsRegistry.main(new String[] {"--invalid"});
    assertTrue(err.toString().contains("Error:"));
    assertTrue(err.toString().contains("--invalid"));
    assertTrue(err.toString().contains("Options for QrelsRegistry:"));
  }

  @Test
  public void testHelp() {
    QrelsRegistry.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for QrelsRegistry:"));
    assertTrue(err.toString().contains("--help"));
    assertFalse(err.toString().contains("Error:"));
  }

  @Test
  public void testFilterRequiresList() {
    QrelsRegistry.main(new String[] {"--get", "dummy", "--filter", "dummy"});
    assertTrue(err.toString().contains("Error: --filter only works with --list"));
  }

  @Test
  public void testListWithFilter() throws Exception {
    QrelsRegistry.main(new String[] {"--list", "--filter", "^dummy$"});
    List<String> names = MAPPER.readValue(out.toString(), NAME_LIST_TYPE);
    assertEquals(List.of("dummy"), names);
  }

  @Test
  public void testListWithInvalidFilterRegex() {
    QrelsRegistry.main(new String[] {"--list", "--filter", "["});
    assertTrue(err.toString().contains("Error: invalid regular expression \"[\""));
    assertEquals("", out.toString());
  }

  @Test
  public void testMissingRequiredOption() {
    QrelsRegistry.main(new String[] {});
    assertTrue(err.toString().contains("Error: exactly one of --list, --get, or --metadata must be specified"));
  }

  @Test
  public void testList() throws Exception {
    QrelsRegistry.main(new String[] {"--list"});
    List<String> names = MAPPER.readValue(out.toString(), NAME_LIST_TYPE);
    Set<String> expectedNames = new TreeSet<>(Qrels.names());
    assertEquals(expectedNames, new TreeSet<>(names));
    assertTrue(names.contains("dummy"));
    assertFalse(names.contains("dummy.1"));
  }

  @Test
  public void testGetRawContents() throws Exception {
    Path qrelsFile = Path.of("qrels.dummy.txt");
    String contents = "1 0 DOC1 1\n1 0 DOC2 0\n2 0 DOC3 1\n2 0 DOC4 0\n";
    Files.writeString(qrelsFile, contents, StandardCharsets.UTF_8);

    try {
      QrelsRegistry.main(new String[] {"--get", "dummy"});
      assertEquals(contents, out.toString());
      assertEquals("", err.toString());
    } finally {
      Files.deleteIfExists(qrelsFile);
    }
  }

  @Test
  public void testGetInvalidQrels() {
    QrelsRegistry.main(new String[] {"--get", "NOT_A_QRELS"});
    assertTrue(err.toString().contains("Error: unknown qrels \"NOT_A_QRELS\""));
    assertEquals("", out.toString());
  }

  @Test
  public void testMetadata() throws Exception {
    Path qrelsFile = Path.of("qrels.dummy.txt");
    Files.writeString(qrelsFile, "1 0 DOC1 1\n", StandardCharsets.UTF_8);

    try {
      QrelsRegistry.main(new String[] {"--metadata", "dummy"});
      Map<String, Object> metadata = MAPPER.readValue(out.toString(), METADATA_TYPE);
      assertEquals("dummy", metadata.get("name"));
      assertEquals("qrels.dummy.txt", metadata.get("path"));
      assertEquals(qrelsFile.toAbsolutePath().toString(), metadata.get("local_path"));
      assertEquals(List.of("dummy.1", "dummy.2"), metadata.get("aliases"));
    } finally {
      Files.deleteIfExists(qrelsFile);
    }
  }

  @Test
  public void testMetadataInvalidQrels() {
    QrelsRegistry.main(new String[] {"--metadata", "NOT_A_QRELS"});
    assertTrue(err.toString().contains("Error: unknown qrels \"NOT_A_QRELS\""));
  }
}
