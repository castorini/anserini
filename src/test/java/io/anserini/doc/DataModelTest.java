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

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.junit.Test;

import io.anserini.LocaleTestUtils;

public class DataModelTest {
  @Test
  public void testEffectivenessAcrossLocales() {
    DataModel data = new DataModel();
    DataModel.Metric metric = new DataModel.Metric();
    metric.setMetric("map");
    DataModel.Topic topic = new DataModel.Topic();
    topic.setName("test");
    DataModel.Model model = new DataModel.Model();
    model.setName("BM25");
    model.setResults(Map.of("map", List.of(0.3123f)));
    data.setMetrics(List.of(metric));
    data.setTopics(List.of(topic));
    data.setModels(List.of(model));
    Locale previous = Locale.getDefault();
    try {
      for (Locale locale : LocaleTestUtils.LOCALES) {
        Locale.setDefault(locale);
        for (String type : List.of("inverted", "hnsw")) {
          data.setIndex_type(type);
          String row = data.generateEffectiveness("test").lines().reduce((a, b) -> b).orElseThrow();
          assertEquals("| test    | " + (type.equals("hnsw") ? "0.312      " : "0.3123     ") + "|", row);
        }
      }
    } finally {
      Locale.setDefault(previous);
    }
  }
}
