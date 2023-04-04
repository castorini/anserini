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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class IOUtils {

  private IOUtils() {
  }

  public static Map<String, List<float[]>> readGloVe(File input) throws IOException {
    Map<String, List<float[]>> vectors = new HashMap<>();
    for (String line : org.apache.commons.io.IOUtils.readLines(new FileReader(input))) {
      String[] s = line.split("\\s+");
      if (s.length > 2) {
        String key = s[0];
        float[] vector = new float[s.length - 1];
        float norm = 0f;
        for (int i = 1; i < s.length; i++) {
          float f = Float.parseFloat(s[i]);
          vector[i - 1] = f;
          norm += Math.pow(f, 2);
        }
        norm = (float) Math.sqrt(norm);
        for (int i = 0; i < vector.length; i++) {
          vector[i] = vector[i] / norm;
        }
        if (vectors.containsKey(key)) {
          List<float[]> floats = new LinkedList<>(vectors.get(key));
          floats.add(vector);
          vectors.put(key, floats);
        } else {
          vectors.put(key, List.of(vector));
        }
      }
    }
    return vectors;
  }
}
