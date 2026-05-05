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

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.index.IndexCollection;

public class GetDocumentTest extends StdOutStdErrRedirectableLuceneTestCase {
  private static Path cacmIndexPath;
  private static Path cacmIndexWithoutRawPath;

  @BeforeClass
  public static void setupClass() throws Exception {
    cacmIndexPath = Files.createTempDirectory("anserini-get-document-test-cacm");
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

    cacmIndexWithoutRawPath = Files.createTempDirectory("anserini-get-document-test-cacm-no-raw");
    IndexCollection.main(new String[] {
        "-collection", "HtmlCollection",
        "-generator", "DefaultLuceneDocumentGenerator",
        "-threads", "1",
        "-input", "src/main/resources/cacm",
        "-index", cacmIndexWithoutRawPath.toString(),
        "-storePositions",
        "-storeDocvectors",
        "-storeContents",
        "-quiet"
    });
  }

  @AfterClass
  public static void tearDownClass() throws Exception {
    if (cacmIndexPath != null) {
      FileUtils.deleteDirectory(cacmIndexPath.toFile());
    }
    if (cacmIndexWithoutRawPath != null) {
      FileUtils.deleteDirectory(cacmIndexWithoutRawPath.toFile());
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
    restoreStdOut();
    restoreStdErr();
    super.tearDown();
  }

  @Test
  public void testHelpOption() {
    GetDocument.main(new String[] {"--help"});
    assertTrue(err.toString().contains("Options for GetDocument:"));
    assertTrue(err.toString().contains("--help"));
    assertFalse(err.toString().contains("Option \"--index\" is required"));
  }

  @Test
  public void testMissingRequiredIndexOption() {
    GetDocument.main(new String[] {});
    assertTrue(err.toString().contains("Option \"--index\" is required"));
  }

  @Test
  public void testMissingDocidInNonInteractiveMode() {
    GetDocument.main(new String[] {"--index", cacmIndexPath.toString()});
    assertTrue(err.toString().contains("Error: --docid is required when not running in interactive mode"));
  }

  @Test
  public void testMutuallyExclusiveInteractiveAndDocid() {
    GetDocument.main(new String[] {"--index", cacmIndexPath.toString(), "--docid", "CACM-0001", "--interactive"});
    assertTrue(err.toString().contains("Error: --interactive and --docid are mutually exclusive"));
  }

  @Test
  public void testGetDocumentWithCacm() {
    GetDocument.main(new String[] {"--index", cacmIndexPath.toString(), "--docid", "CACM-0001"});

    String output = out.toString();
    assertTrue(output.contains("<html>"));
    assertTrue(output.contains("Preliminary Report-International Algebraic Language"));
    assertTrue(output.contains("CACM December, 1958"));
    assertTrue(err.toString().isEmpty());
  }

  @Test
  public void testGetDocumentWithPrebuiltCacm() {
    GetDocument.main(new String[] {"--index", "cacm", "--docid", "CACM-0001"});

    String output = out.toString();
    assertTrue(output.contains("<html>"));
    assertTrue(output.contains("Preliminary Report-International Algebraic Language"));
    assertTrue(output.contains("CACM December, 1958"));
    assertTrue(err.toString().isEmpty());
  }

  @Test
  public void testInteractiveGetDocumentWithCacm() {
    String stdin = "CACM-0001\n\nCACM-0002\n";
    System.setIn(new ByteArrayInputStream(stdin.getBytes(StandardCharsets.UTF_8)));

    GetDocument.main(new String[] {"--index", cacmIndexPath.toString(), "--interactive"});

    String output = out.toString();
    assertTrue(output.contains("Preliminary Report-International Algebraic Language"));
    assertTrue(output.contains("Extraction of Roots by Repeated Subtractions"));
    assertTrue(err.toString().isEmpty());
  }

  @Test
  public void testInteractiveInvalidIndex() {
    Path missingIndexPath = cacmIndexPath.resolveSibling("anserini-get-document-test-missing-index");
    GetDocument.main(new String[] {"--index", missingIndexPath.toString(), "--interactive"});

    assertTrue(out.toString().isEmpty());
    assertTrue(err.toString().contains("Error:"));
  }

  @Test
  public void testDocumentNotFound() {
    GetDocument.main(new String[] {"--index", cacmIndexPath.toString(), "--docid", "CACM-9999"});
    assertTrue(out.toString().isEmpty());
    assertTrue(err.toString().contains("Error: Document not found: CACM-9999"));
  }

  @Test
  public void testDocumentWithoutRawField() {
    GetDocument.main(new String[] {"--index", cacmIndexWithoutRawPath.toString(), "--docid", "CACM-0001"});
    assertTrue(out.toString().isEmpty());
    assertTrue(err.toString().contains("Error: Document does not have stored raw field: CACM-0001"));
  }
}
