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

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.StdOutStdErrRedirectableLuceneTestCase;
import io.anserini.util.CacheDirectoryResolver;
import io.anserini.util.PrebuiltIndexHandler;

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
  public void testShowConfig() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "cacm", "--show"});

    assertTrue(out.toString().startsWith("conditions:"));
    assertTrue(out.toString().contains("name: bm25"));
    assertTrue(out.toString().contains("index cacm"));
  }

  @Test
  public void testBeirDryRun() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir", "--dry-run"});

    assertTrue(out.toString().startsWith("Indexes referenced by this run"));
    assertTrue(out.toString().contains("Total size across"));
    assertTrue(out.toString().contains("# Running condition"));
    assertTrue(out.toString().contains("Retrieval command"));
    assertTrue(out.toString().contains("Eval command"));
  }

  @Test
  public void testDryRunOmitsPathForMissingPrebuiltIndex() throws Exception {
    Path cacheDirectory = createTempDir("pyserini-cache");
    String previousCacheDirectory = System.getProperty(CacheDirectoryResolver.CACHE_PROPERTY);

    try {
      System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, cacheDirectory.toString());

      ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "cacm", "--dry-run"});
    } finally {
      if (previousCacheDirectory == null) {
        System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
      } else {
        System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, previousCacheDirectory);
      }
    }

    String output = out.toString();
    assertTrue(output, output.contains("Indexes referenced by this run (1 total):"));
    assertTrue(output, output.matches("(?s).*\\ncacm\\s+[0-9.]+ [A-Z]+B\\s+-\\s+-\\s*\\n.*"));
    assertFalse(output, output.contains(cacheDirectory.resolve("indexes").toString()));
  }

  @Test
  public void testBeirDryRunReportsPlainTarIndexSize() throws Exception {
    Path cacheDirectory = createTempDir("pyserini-cache");
    String previousCacheDirectory = System.getProperty(CacheDirectoryResolver.CACHE_PROPERTY);
    PrebuiltIndexHandler handler = PrebuiltIndexHandler.get("beir-v1.0.0-trec-covid.bge-base-en-v1.5.flat");
    assertNotNull(handler);
    assertTrue(handler.getFilename().endsWith(".tar"));

    String filenameBase = handler.getFilename().substring(0, handler.getFilename().length() - ".tar".length());
    Path indexDirectory = cacheDirectory.resolve("indexes").resolve(filenameBase + "." + handler.getMD5());

    try {
      System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, cacheDirectory.toString());
      Files.createDirectories(indexDirectory);
      Files.writeString(indexDirectory.resolve("marker"), "marker");

      ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir", "--dry-run"});
    } finally {
      if (previousCacheDirectory == null) {
        System.clearProperty(CacheDirectoryResolver.CACHE_PROPERTY);
      } else {
        System.setProperty(CacheDirectoryResolver.CACHE_PROPERTY, previousCacheDirectory);
      }
    }

    String output = out.toString();
    assertTrue(output, output.contains("beir-v1.0.0-trec-covid.bge-base-en-v1.5.flat"));
    assertTrue(output, output.matches("(?s).*beir-v1\\.0\\.0-trec-covid\\.bge-base-en-v1\\.5\\.flat\\s+506\\.5 MB\\s+6\\.0 B.*"));
    assertTrue(output, output.contains(indexDirectory.toAbsolutePath().toString()));
  }

  @Test
  public void testCacmEndToEnd() throws Exception {
    Path runsDirectory = createTempDir("runs");
    Locale previousLocale = Locale.getDefault();

    try {
      Locale.setDefault(Locale.forLanguageTag("ar-LB"));
      ReproduceFromPrebuiltIndexes.main(new String[] {
          "--config", "cacm",
          "--runs-directory", runsDirectory.toString()
      });
    } finally {
      Locale.setDefault(previousLocale);
    }

    String output = out.toString();
    assertTrue(output, output.contains("Run successfully completed!"));
    assertTrue(output, output.contains("Indexes referenced by this run (1 total):"));
    assertTrue(output, output.matches("(?s).*Total size across [01] of 1 indexes:.*"));
    assertTrue(output, output.contains("MAP: 0.3123"));
    assertTrue(output, output.contains("P30: 0.1942"));
    assertTrue(output, output.matches("(?s).*Duration:\\s+[0-9]{2}:[0-9]{2}:[0-9]{2}.*"));
    assertFalse(output.contains("NumberFormatException"));
    assertTrue(Files.exists(runsDirectory.resolve("run.cacm.bm25.cacm.txt")));
    assertTrue(Files.size(runsDirectory.resolve("run.cacm.bm25.cacm.txt")) > 0);
  }

  @Test
  public void testFaultyConfigSkipsEvaluation() throws Exception {
    Path runsDirectory = createTempDir("runs");

    ReproduceFromPrebuiltIndexes.main(new String[] {
        "--config", "faulty",
        "--runs-directory", runsDirectory.toString()
    });

    String output = out.toString();
    assertTrue(output, output.contains("# Running condition \"retrieval-fails\""));
    assertTrue(output, output.contains("Run failed!"));
    assertTrue(output, output.contains("Skipping evaluation because retrieval failed."));
    assertTrue(output, output.contains("# Running condition \"missing-run-file\""));
    assertTrue(output, output.contains("Run successfully completed!"));
    assertTrue(output, output.contains("Skipping evaluation because run file was not created: " + runsDirectory.resolve("run.faulty.missing-run-file.cacm.txt")));
    assertFalse(output.contains("NumberFormatException"));
    assertFalse(Files.exists(runsDirectory.resolve("run.faulty.retrieval-fails.cacm.txt")));
    assertFalse(Files.exists(runsDirectory.resolve("run.faulty.missing-run-file.cacm.txt")));
  }

  @Test
  public void testRenderSummaryTable() {
    ReproduceFromPrebuiltIndexes.Config config = new ReproduceFromPrebuiltIndexes.Config();

    ReproduceFromPrebuiltIndexes.Condition firstCondition = new ReproduceFromPrebuiltIndexes.Condition();
    firstCondition.name = "cond-a";
    ReproduceFromPrebuiltIndexes.Topic firstTopic = new ReproduceFromPrebuiltIndexes.Topic();
    firstTopic.topic_key = "topic-a";
    firstTopic.expected_scores = new LinkedHashMap<>();
    firstTopic.expected_scores.put("MRR@10", 0.1234);
    firstTopic.expected_scores.put("R@1K", 0.5678);
    firstCondition.topics = Arrays.asList(firstTopic);

    ReproduceFromPrebuiltIndexes.Condition secondCondition = new ReproduceFromPrebuiltIndexes.Condition();
    secondCondition.name = "cond-b";
    ReproduceFromPrebuiltIndexes.Topic secondTopic = new ReproduceFromPrebuiltIndexes.Topic();
    secondTopic.topic_key = "topic-b";
    secondTopic.expected_scores = new LinkedHashMap<>();
    secondTopic.expected_scores.put("MAP", 0.9876);
    secondCondition.topics = Arrays.asList(secondTopic);

    config.conditions = Arrays.asList(firstCondition, secondCondition);

    String summary = ReproduceFromPrebuiltIndexes.renderSummaryTable(config);
    assertTrue(summary.startsWith("Summary"));
    assertTrue(summary.contains("condition"));
    assertTrue(summary.contains("topic"));
    assertTrue(summary.contains("metric"));
    assertTrue(summary.contains("expected"));
    assertTrue(summary.contains("cond-a"));
    assertTrue(summary.contains("topic-a"));
    assertTrue(summary.contains("MRR@10"));
    assertTrue(summary.contains("0.1234"));
    assertTrue(summary.contains("R@1K"));
    assertTrue(summary.contains("0.5678"));
    List<String> lines = summary.lines().collect(Collectors.toList());
    int firstConditionLastRow = -1;
    int secondConditionFirstRow = -1;
    for (int i = 0; i < lines.size(); i++) {
      String line = lines.get(i);
      if (line.startsWith("cond-a")) {
        firstConditionLastRow = i;
      } else if (line.startsWith("cond-b")) {
        secondConditionFirstRow = i;
        break;
      }
    }
    assertTrue(firstConditionLastRow >= 0);
    assertTrue(secondConditionFirstRow > firstConditionLastRow + 1);
    assertEquals("", lines.get(secondConditionFirstRow - 1));
    assertTrue(summary.contains("MAP"));
    assertTrue(summary.contains("0.9876"));
  }
}
