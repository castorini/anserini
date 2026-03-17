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

package io.anserini.reproduce;

import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class ReproduceFromPrebuiltIndexesTest extends StdOutStdErrRedirectableLuceneTestCase {
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
  public void testInvalidOption() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--invalid"});

    assertTrue(err.toString().startsWith("Error: \"--invalid\" is not a valid option"));
    assertTrue(err.toString().contains("Options for ReproduceFromPrebuiltIndexes:"));
  }

  @Test
  public void testConfigRequiredUnlessListSpecified() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[0]);

    assertTrue(err.toString().contains("Error: Option \"--config\" is required unless \"--list\" is specified."));
    assertTrue(err.toString().contains("Options for ReproduceFromPrebuiltIndexes:"));
  }

  @Test
  public void testHelp() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--help"});

    assertTrue(err.toString().contains("Options for ReproduceFromPrebuiltIndexes:"));
    assertTrue(err.toString().contains("--help"));
  }

  @Test
  public void testListConfigs() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--list"});

    List<?> outputConfigs = new ObjectMapper().readValue(out.toString(), List.class);
    List<String> expectedConfigs = ReproductionUtils.listYamlConfigs(
        ReproduceFromPrebuiltIndexes.class, "reproduce/from-prebuilt-indexes/configs");
    assertEquals(expectedConfigs.size(), outputConfigs.size());
  }

  @Test
  public void testBeirCoreDryRun() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir.core", "--dry-run"});

    assertTrue(out.toString().startsWith("# Running condition"));
  }

  @Test
  public void testBeirCorePrintCommandsDryRun() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir.core", "--dry-run", "--print-commands"});

    assertTrue(out.toString().startsWith("# Running condition"));
    assertTrue(out.toString().contains("Retrieval command"));
    assertTrue(out.toString().contains("Eval command"));
  }

  @Test
  public void testComputeIndexSize() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir.core", "--dry-run", "--compute-index-size"});

    String s = out.toString();
    assertTrue(s.contains("Indexes referenced by this run"));
    assertTrue(s.contains("Total size across"));
  }
}
