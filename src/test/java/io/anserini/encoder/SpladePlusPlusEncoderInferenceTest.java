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

package io.anserini.encoder;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

abstract class SpladePlusPlusEncoderInferenceTest extends EncoderInferenceTest {

  public SpladePlusPlusEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
    super(modelName, modelUrl, examples);
  }

  public SpladePlusPlusEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples, Object[][] longExamples) {
    super(modelName, modelUrl, examples, longExamples);
  }


  protected void basicTest() throws IOException, OrtException {
    String modelPath = getEncoderModelPath().toString();
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, options)) {

      for (Object[] example : examples) {
        long[] inputIds = (long[]) example[0];
        long[] expectedIdx = (long[]) example[1];
        float[] expectedWeights = (float[]) example[2];

        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[][] tokenIds = new long[1][inputIds.length];
        long[][] tokenTypeIdsTensor = new long[1][inputIds.length];
        long[][] attentionMaskTensor = new long[1][inputIds.length];
        Arrays.fill(attentionMaskTensor[0], 1);
        tokenIds[0] = inputIds;
        inputs.put("input_ids", OnnxTensor.createTensor(env, tokenIds));
        inputs.put("token_type_ids", OnnxTensor.createTensor(env, tokenTypeIdsTensor));
        inputs.put("attention_mask", OnnxTensor.createTensor(env, attentionMaskTensor));
        try (Result results = session.run(inputs)) {
          long[] indexes = (long[]) results.get("output_idx").get().getValue();
          float[] weights = (float[]) results.get("output_weights").get().getValue();
          assertArrayEquals(expectedIdx, indexes);
          assertArrayEquals(expectedWeights, weights, 1e-4f);
        }
      }
    }
  }

}
