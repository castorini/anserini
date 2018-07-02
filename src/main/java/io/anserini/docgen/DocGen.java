/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.docgen;

import java.io.File;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.text.StrSubstitutor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class DocGen {
  public static void main(String[] args) throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    URL resource = DocGen.class.getResource("/regression/all.yaml");
    Model data = mapper.readValue(Paths.get(resource.toURI()).toFile(), Model.class);
    //System.out.println(ReflectionToStringBuilder.toString(data, ToStringStyle.MULTI_LINE_STYLE));

    for (String collection : data.getCollections().keySet()) {
      Map<String, String> valuesMap = new HashMap<>();
      valuesMap.put("index_cmds", data.genIndexingCmd(collection));
      valuesMap.put("ranking_cmds", data.genRankingCmd(collection));
      valuesMap.put("eval_cmds", data.genEvalCmd(collection));
      valuesMap.put("effectiveness", data.genEffectiveness(collection));
      StrSubstitutor sub = new StrSubstitutor(valuesMap);
      URL template = DocGen.class.getResource(String.format("/docgen/templates/%s.template", collection));
      Scanner scanner = new Scanner(Paths.get(template.toURI()).toFile(), "UTF-8");
      String text = scanner.useDelimiter("\\A").next();
      scanner.close();
      String resolvedString = sub.replace(text);

      FileUtils.writeStringToFile(new File(String.format("docs/experiments-%s.md", collection)),
        resolvedString, "UTF-8");
    }
  }
}
