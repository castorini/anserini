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

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GenerateRegressionDocsTest {
  @Test
  public void main() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    URL templatesRoot = GenerateRegressionDocsTest.class.getResource("/docgen/templates/");

    for (final File fileEntry : new File(templatesRoot.toURI()).listFiles()) {
      String fileName = fileEntry.getName();
      // This is the name of the test, which can be different from the name of the collection,
      // e.g., multiple topics run on the same collection.
      String testName = fileEntry.getName().replaceAll(".template", "");

      URL yaml = GenerateRegressionDocsTest.class.getResource(String.format("/regression/%s.yaml", testName));
      DataModel data = mapper.readValue(new File(yaml.toURI()), DataModel.class);
      String corpus = data.getCorpus();

      Map<String, String> valuesMap = new HashMap<>();
      valuesMap.put("yaml", String.format("../src/main/resources/regression/%s.yaml", testName));
      valuesMap.put("template", String.format("../src/main/resources/docgen/templates/%s.template", testName));
      valuesMap.put("test_name", testName);
      valuesMap.put("corpus", corpus);
      valuesMap.put("index_cmds", data.generateIndexingCommand(corpus));
      valuesMap.put("ranking_cmds", data.generateRankingCommand(corpus));
      valuesMap.put("converting_cmds", data.generateConvertingCommand(corpus));
      valuesMap.put("eval_cmds", data.generateEvalCommand(corpus));
      valuesMap.put("effectiveness", data.generateEffectiveness(corpus));

      StringSubstitutor sub = new StringSubstitutor(valuesMap);
      URL template = GenerateRegressionDocsTest.class.getResource(String.format("/docgen/templates/%s.template", testName));
      Scanner scanner = new Scanner(new File(template.toURI()), "UTF-8");
      String text = scanner.useDelimiter("\\A").next();
      scanner.close();
      String resolvedString = sub.replace(text);

      FileUtils.writeStringToFile(new File(String.format("docs/regressions-%s.md", testName)),
        resolvedString, "UTF-8");
    }
  }
}
