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

package io.anserini.util;

import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class DumpAnalyzedQueriesTest {

  @Rule
  public TemporaryFolder tempFolder = new TemporaryFolder();

  @Test
  public void testLanguageSpecificAnalyzerSelection() {
    DumpAnalyzedQueries.Args args = new DumpAnalyzedQueries.Args();
    args.language = "fr";

    Analyzer analyzer = DumpAnalyzedQueries.getAnalyzer(args);
    assertTrue(analyzer instanceof FrenchAnalyzer);
  }

  @Test
  public void testWhitespaceAnalyzerSelection() {
    DumpAnalyzedQueries.Args args = new DumpAnalyzedQueries.Args();
    args.language = "sw";

    Analyzer analyzer = DumpAnalyzedQueries.getAnalyzer(args);
    assertTrue(analyzer instanceof WhitespaceAnalyzer);
  }

  @Test
  public void testDefaultAnalyzerSelection() {
    DumpAnalyzedQueries.Args args = new DumpAnalyzedQueries.Args();
    args.language = "en";

    Analyzer analyzer = DumpAnalyzedQueries.getAnalyzer(args);
    assertSame(IndexCollection.DEFAULT_ANALYZER, analyzer);
  }

  @Test
  public void testDumpAnalyzedQueriesWithWhitespaceAnalyzer() throws Exception {
    File topics = tempFolder.newFile("topics.tsv");
    Files.write(topics.toPath(), Arrays.asList(
        "topic1\tanserini analyzer",
        "topic2\tstable whitespace tokens"
    ), StandardCharsets.UTF_8);

    File output = tempFolder.newFile("analyzed.tsv");

    DumpAnalyzedQueries.main(new String[]{
        "-topicreader", "TsvString",
        "-topics", topics.getAbsolutePath(),
        "-output", output.getAbsolutePath(),
        "-language", "en"
    });

    List<String> lines = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8);
    assertEquals(2, lines.size());
    assertEquals("topic1\tanserini analyz", lines.get(0));
    assertEquals("topic2\tstabl whitespac token", lines.get(1));
  }

  @Test
  public void testDumpAnalyzedQueriesWithTrecTopics() throws Exception {
    File output = tempFolder.newFile("trec.tsv");

    DumpAnalyzedQueries.main(new String[]{
        "-topicreader", "Trec",
        "-topics", new File("src/test/resources/sample_topics/Trec").getAbsolutePath(),
        "-output", output.getAbsolutePath(),
        "-language", "en"
    });

    List<String> lines = Files.readAllLines(output.toPath(), StandardCharsets.UTF_8);
    assertEquals(1, lines.size());
    assertEquals("1\tsimpl text", lines.get(0));
  }
}
