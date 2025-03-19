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

package io.anserini.encoder.sparse;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UniCoilEncoder extends SparseEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/unicoil.onnx";
  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  static private final String MODEL_NAME = "unicoil.onnx";
  static private final String VOCAB_NAME = "unicoil-vocab.txt";

  static private final String MODEL_INPUT_IDS = "inputIds";

  public UniCoilEncoder() throws IOException, OrtException, URISyntaxException {
    super(5, 256, MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  protected Map<String, Float> computeFloatWeights(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add(CLS);
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add(SEP);

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(queryTokens);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];
    inputTokenIds[0] = queryTokenIds;
    inputs.put(MODEL_INPUT_IDS, OnnxTensor.createTensor(environment, inputTokenIds));

    try (OrtSession.Result results = session.run(inputs)) {
      float[] computedWeights = flattenResults(results.get(0).getValue());

      Map<String, Float> tokenWeightMap = new LinkedHashMap<>();
      for (int i = 0; i < queryTokens.size(); ++i) {
        String token = queryTokens.get(i);
        if (token.equals(CLS) || token.equals(PAD)) {
          continue;
        }

        tokenWeightMap.put(token,
            tokenWeightMap.containsKey(token) ? tokenWeightMap.get(token) + computedWeights[i] : computedWeights[i]);
      }

      return tokenWeightMap;
    }
  }

  private float[] flattenResults(Object obj) {
    List<Float> weightsList = new ArrayList<>();
    Object[] inputs = (Object[]) obj;
    for (Object input : inputs) {
      float[][] weights = (float[][]) input;
      for (float[] weight : weights) {
        weightsList.add(weight[0]);
      }
    }

    Float[] floatObjects = new Float[weightsList.size()];
    floatObjects = weightsList.toArray(floatObjects);
    return ArrayUtils.toPrimitive(floatObjects);
  }
}