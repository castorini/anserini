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

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;
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
  public void testShowConfig() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "cacm", "--show"});

    assertTrue(out.toString().startsWith("conditions:"));
    assertTrue(out.toString().contains("name: bm25"));
    assertTrue(out.toString().contains("index cacm"));
  }

  @Test
  public void testBeirCoreDryRun() throws Exception {
    ReproduceFromPrebuiltIndexes.main(new String[] {"--config", "beir.core", "--dry-run"});

    assertTrue(out.toString().startsWith("Indexes referenced by this run"));
    assertTrue(out.toString().contains("Total size across"));
    assertTrue(out.toString().contains("# Running condition"));
    assertTrue(out.toString().contains("Retrieval command"));
    assertTrue(out.toString().contains("Eval command"));
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
