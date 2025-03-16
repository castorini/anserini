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

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import io.anserini.encoder.EncoderInferenceTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

abstract class DenseEncoderInferenceTest extends EncoderInferenceTest {

  public DenseEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
    super(modelName, modelUrl, examples);
  }

  public DenseEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples, Object[][] longExamples) {
    super(modelName, modelUrl, examples, longExamples);
  }

  protected void basicTest() throws IOException, OrtException, URISyntaxException {
    String modelPath = getEncoderModelPath().toString();
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, options)) {

      for (Object[] example : examples) {
        long[] inputIds = (long[]) example[0];
        float[] expectedWeights = (float[]) example[1];

        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[][] tokenIds = new long[1][inputIds.length];
        tokenIds[0] = inputIds;
        inputs.put("input_ids", OnnxTensor.createTensor(env, tokenIds));

        try (Result results = session.run(inputs)) {
          float[] weights = ((float[][]) results.get("pooler_output").get().getValue())[0];
          assertArrayEquals(expectedWeights, weights, 1e-4f);
        }
      }
    }
  }
}