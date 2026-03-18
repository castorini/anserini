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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;

public class CliUtilsTest extends StdOutStdErrRedirectableLuceneTestCase {
  static class DirectOptions {
    @Option(name = "--required", usage = "required option", required = true)
    String required;

    @Option(name = "--text", aliases = "-t", metaVar = "[text]", usage = "text option")
    String text = "hello";

    @Option(name = "--count", usage = "count option")
    int count = 3;

    @Option(name = "--items", metaVar = "[item]", usage = "items option")
    String[] items = new String[] {"one", "two"};

    @Option(name = "--flag", metaVar = "[boolean]", usage = "flag option")
    boolean flag;
  }

  static class NestedApplication {
    static class Args {
      @Option(name = "--nested", aliases = "-n", metaVar = "[value]", usage = "nested option")
      String nested = "inside";
    }
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

  @Test
  public void testPrintUsageFormatsDefaultsAndRequiredOptions() {
    CmdLineParser parser = new CmdLineParser(new DirectOptions());

    CliUtils.printUsage(parser, DirectOptions.class, new String[] {"-t", "--required", "--missing", "--required"});

    String output = err.toString();
    assertTrue(output.contains("Options for DirectOptions:"));
    assertTrue(output.contains("--text [text], -t [text]  text option (default: \"hello\")"));
    assertTrue(output.contains("--count                   count option (default: 3)"));
    assertTrue(output.contains("--items [item]            items option (default: [one, two])"));
    assertTrue(output.contains("--flag                    flag option"));
    assertTrue(output.contains("Required options: [--required]"));

    assertTrue(output.indexOf("--text [text], -t [text]") < output.indexOf("--required"));
    assertEquals(1, countOccurrences(output, "\n  --required"));
  }

  @Test
  public void testPrintUsageResolvesNestedOptionsClass() {
    CmdLineParser parser = new CmdLineParser(new NestedApplication.Args());

    CliUtils.printUsage(parser, NestedApplication.class, new String[] {"-n"});

    String output = err.toString();
    assertTrue(output.contains("Options for NestedApplication:"));
    assertTrue(output.contains("--nested [value], -n [value]  nested option (default: \"inside\")"));
  }

  private static int countOccurrences(String value, String needle) {
    int count = 0;
    int index = 0;
    while ((index = value.indexOf(needle, index)) >= 0) {
      count++;
      index += needle.length();
    }
    return count;
  }
}
