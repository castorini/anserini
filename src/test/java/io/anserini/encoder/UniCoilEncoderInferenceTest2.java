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
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

public class UniCoilEncoderInferenceTest2 extends EncoderInferenceTest {

  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/unicoil.onnx";
  static private final String MODEL_NAME = "unicoil.onnx";

  // Second half of the examples from the original test
  static private final Object[][] EXAMPLES = new Object[][] {
      { new long[] { 101, 2054, 2001, 11534, 1005, 1055, 4602, 6691, 2000, 12761, 3399, 1029, 102 },
          new float[] { 1.2704929f, 0.0f, 1.1832705f, 2.4083762f, 0.34482002f, 0.49629262f, 2.3794782f, 1.8941771f,
        0.42992824f, 1.5892929f, 0.47762313f, 0.95083314f, 0.0f } },

      { new long[] { 101, 2019, 5983, 8761, 2003, 7356, 2011, 1035, 1035, 1035, 1035, 1035, 1012, 102 },
          new float[] { 1.2431517f, 0.8008548f, 2.498167f, 1.27854f, 0.7832665f, 1.7345628f, 0.16658483f, 0.0f, 0.0f,
        0.0f, 0.0f, 0.0f, 0.2535707f, 0.0f } },
      { new long[] { 101, 2054, 8915, 8737, 2079, 2017, 5660, 15960, 24494, 2015, 2006, 1999, 1996, 17428, 1029, 1998,
          2005, 2129, 2146, 1029, 102 },
          new float[] { 1.1041864f, 0.0f, 0.47533986f, 1.669825f, 0.2934663f, 0.78933185f, 0.9406975f, 2.1501098f,
              2.362949f, 0.46458432f, 0.9171016f, 0.39884382f, 0.346544f, 1.7016646f, 0.33858445f, 0.32457188f,
              0.70062816f, 0.9733413f, 1.1381183f, 0.54110324f, 0.0f } },

      { new long[] { 101, 2029, 18672, 8844, 26450, 6740, 16896, 2006, 1996, 4942, 15782, 14289, 8017, 1042, 21842,
          1997, 1996, 8040, 9331, 7068, 1998, 19274, 2015, 2006, 1996, 8276, 7270, 21769, 1997, 1996, 20368, 7946, 1029,
          102 },
          new float[] { 1.2975391f, 0.41514271f, 1.5634528f, 1.0063248f, 1.7381638f, 1.0919806f, 1.5582242f,
              0.43482706f, 0.33785614f, 1.0224074f, 0.79580104f, 0.5206254f, 0.36112663f, 0.5333182f, 1.075079f,
              0.20146157f, 0.36403617f, 1.2272573f, 1.0263921f, 0.699825f, 0.297133f, 1.2347529f, 0.0f, 0.46168548f,
              0.3573556f, 1.3769448f, 1.0224242f, 1.0108802f, 0.11389083f, 0.41117048f, 1.5779594f, 0.4974613f,
              0.7945211f, 0.0f } },
  };

  public UniCoilEncoderInferenceTest2() {
    super(MODEL_NAME, MODEL_URL, EXAMPLES);
  }

  @Test
  public void basic() throws OrtException, IOException, URISyntaxException {
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
        inputs.put("inputIds", OnnxTensor.createTensor(env, tokenIds));
        try (Result results = session.run(inputs)) {
          float[] computedWeights = flatten(results.get(0).getValue());
          assertArrayEquals(expectedWeights, computedWeights, 1e-5f);
        }
      }
    }
  }

  private float[] flatten(Object obj) {
    List<Float> weightsList = new ArrayList<>();
    Object[] inputs = (Object[]) obj;
    for (Object input : inputs) {
      float[][] weights = (float[][]) input;
      for (float[] weight : weights) {
        weightsList.add(weight[0]);
      }
    }
    return toArray(weightsList);
  }

  private float[] toArray(List<Float> input) {
    float[] output = new float[input.size()];
    for (int i = 0; i < output.length; i++) {
      output[i] = input.get(i);
    }
    return output;
  }
} 