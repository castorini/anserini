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

package io.anserini.encoder.dense;

import ai.onnxruntime.OrtException;
import io.anserini.encoder.OnnxEncoder;

import java.io.IOException;
import java.net.URISyntaxException;

public abstract class DenseEncoder extends OnnxEncoder<float[]> {
  public DenseEncoder(String modelName, String modelURL, String vocabName, String vocabURL, String configURL)
      throws IOException, OrtException, URISyntaxException {
    super(modelName, modelURL, vocabName, vocabURL, configURL);
  }

  public static float[] normalize(float[] vector) {
    final float EPS = 1e-12f;
    float norm = 0;
    for (float v : vector) {
      norm += v * v;
    }
    norm = (float) Math.sqrt(norm);

    for (int i = 0; i < vector.length; i++) {
      vector[i] = vector[i] / (norm + EPS);
    }
    return vector;
  }
}