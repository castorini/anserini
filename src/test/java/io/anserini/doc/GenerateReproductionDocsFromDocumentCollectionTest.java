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

package io.anserini.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.text.StringSubstitutor;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GenerateReproductionDocsFromDocumentCollectionTest {
  @Test
  public void generateDocs() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    URL templatesRoot = GenerateReproductionDocsFromDocumentCollectionTest.class.getResource("/reproduce/from-document-collection/docgen");

    assert templatesRoot != null;
    for (final File fileEntry : Objects.requireNonNull(new File(templatesRoot.toURI()).listFiles())) {
      // This is the name of the test, which can be different from the name of the collection,
      // e.g., multiple topics run on the same collection.
      String testName = fileEntry.getName().replaceAll(".template", "");

      URL yaml = GenerateReproductionDocsFromDocumentCollectionTest.class.getResource(String.format("/reproduce/from-document-collection/configs/%s.yaml", testName));
      DataModel data = mapper.readValue(new File(yaml.toURI()), DataModel.class);
      String corpus = data.getCorpus();
      String download_corpus = data.getDownload_corpus();

      Map<String, String> valuesMap = new HashMap<>();
      valuesMap.put("root_path", "../../..");
      valuesMap.put("yaml", String.format("../../../src/main/resources/reproduce/from-document-collection/configs/%s.yaml", testName));
      valuesMap.put("template", String.format("../../../src/main/resources/reproduce/from-document-collection/docgen/%s.template", testName));
      valuesMap.put("test_name", testName);
      valuesMap.put("corpus", corpus);
      valuesMap.put("download_url", data.getDownload_url());
      valuesMap.put("download_checksum", data.getDownload_checksum());
      valuesMap.put("download_corpus", download_corpus);
      valuesMap.put("index_cmds", data.generateIndexingCommand(corpus));
      valuesMap.put("ranking_cmds", data.generateRankingCommand(corpus));
      valuesMap.put("converting_cmds", data.generateConvertingCommand(corpus));
      valuesMap.put("eval_cmds", data.generateEvalCommand(corpus));
      valuesMap.put("effectiveness", data.generateEffectiveness(corpus));

      StringSubstitutor sub = new StringSubstitutor(valuesMap);
      URL template = GenerateReproductionDocsFromDocumentCollectionTest.class.getResource(String.format("/reproduce/from-document-collection/docgen/%s.template", testName));
      assertNotNull(template);

      Scanner scanner = new Scanner(new File(template.toURI()), StandardCharsets.UTF_8);
      String text = scanner.useDelimiter("\\A").next();
      scanner.close();
      String resolvedString = sub.replace(text);

      FileUtils.writeStringToFile(new File(String.format("docs/reproduce/from-document-collection/%s.md", testName)),
        resolvedString, "UTF-8");
    }
  }

  @Test
  public void generateOverviewDoc() throws Exception {
    Path configsPath = Path.of("src/main/resources/reproduce/from-document-collection/configs");
    List<String> configs = Files.list(configsPath)
        .filter(path -> path.getFileName().toString().endsWith(".yaml"))
        .map(path -> path.getFileName().toString().replaceAll("\\.yaml$", ""))
        .sorted()
        .toList();

    Map<String, List<String>> ordering = extractOrderingFromRegressions();
    StringBuilder invocations = new StringBuilder();
    Set<String> emitted = new LinkedHashSet<>();

    for (Map.Entry<String, List<String>> entry : ordering.entrySet()) {
      List<String> groupConfigs = new ArrayList<>();
      for (String regex : entry.getValue()) {
        Pattern pattern = Pattern.compile(regex);
        for (String config : configs) {
          if (!emitted.contains(config) && pattern.matcher(config).matches()) {
            groupConfigs.add(config);
            emitted.add(config);
          }
        }
      }

      if (groupConfigs.isEmpty()) {
        continue;
      }

      invocations.append("<details>\n");
      invocations.append("<summary>").append(entry.getKey()).append("</summary>\n\n");
      for (String config : groupConfigs) {
        invocations.append("+ [").append(config).append("](reproduce/from-document-collection/")
            .append(config).append(".md)\n");
      }
      invocations.append("\n</details>\n");
    }

    List<String> remainingConfigs = configs.stream()
        .filter(config -> !emitted.contains(config))
        .sorted(Comparator.naturalOrder())
        .toList();
    if (!remainingConfigs.isEmpty()) {
      invocations.append("<details>\n");
      invocations.append("<summary>Other regressions</summary>\n\n");
      for (String config : remainingConfigs) {
        invocations.append("+ [").append(config).append("](reproduce/from-document-collection/")
            .append(config).append(".md)\n");
      }
      invocations.append("\n</details>\n");
    }

    Map<String, String> valuesMap = new HashMap<>();
    valuesMap.put("invocations", invocations.toString().trim());

    StringSubstitutor sub = new StringSubstitutor(valuesMap);
    String text = Files.readString(Path.of("docs/reproduce-from-document-collection.template"), StandardCharsets.UTF_8);
    String resolvedString = sub.replace(text);

    FileUtils.writeStringToFile(new File("docs/reproduce-from-document-collection.md"), resolvedString, "UTF-8");
  }

  private static Map<String, List<String>> extractOrderingFromRegressions() throws Exception {
    String regressions = Files.readString(Path.of("docs/regressions.md"), StandardCharsets.UTF_8);
    Map<String, List<String>> ordering = new LinkedHashMap<>();

    Pattern sectionPattern = Pattern.compile(
        "<summary>(.*?)</summary>(.*?)(?=<summary>|$)",
        Pattern.DOTALL);
    Pattern regressionPattern = Pattern.compile("--regression\\s+([^\\s]+)");
    Matcher sectionMatcher = sectionPattern.matcher(regressions);

    while (sectionMatcher.find()) {
      String group = sectionMatcher.group(1).trim();
      String body = sectionMatcher.group(2);
      Matcher regressionMatcher = regressionPattern.matcher(body);
      List<String> patterns = new ArrayList<>();
      while (regressionMatcher.find()) {
        patterns.add(Pattern.quote(regressionMatcher.group(1)));
      }
      ordering.put(group, patterns);
    }

    return ordering;
  }

}
