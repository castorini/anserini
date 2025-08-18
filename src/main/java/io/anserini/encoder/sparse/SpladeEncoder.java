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
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SpladeEncoder extends SparseEncoder {
  static private final int WEIGHT_RANGE = 5;
  static private final int QUANT_RANGE = 256;
  static private final int MAX_SEQ_LEN = 512;

  static private final String MODEL_INPUT_IDS = "input_ids";
  static private final String MODEL_TOKEN_TYPE_IDS = "token_type_ids";
  static private final String MODEL_ATTENTION_MASK = "attention_mask";
  static private final String MODEL_OUTPUT_IDX = "output_idx";
  static private final String MODEL_OUTPUT_WEIGHTS = "output_weights";

  protected SpladeEncoder(@NotNull String modelName, @NotNull String modelUrl,
                          @NotNull String vocabName, @NotNull String vocabUrl, String configUrl)
      throws IOException, OrtException, URISyntaxException {
    super(WEIGHT_RANGE, QUANT_RANGE, modelName, modelUrl, vocabName, vocabUrl, configUrl);
  }

  @Override
  protected Map<String, Float> computeFloatWeights(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add(CLS);
    queryTokens.addAll(tokenizer.tokenize(query));
    if (queryTokens.size() > MAX_SEQ_LEN - 2) {
      queryTokens = queryTokens.subList(0, MAX_SEQ_LEN - 2);
    }
    queryTokens.add(SEP);

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(queryTokens, MAX_SEQ_LEN);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];
    inputTokenIds[0] = queryTokenIds;
    long[][] attentionMask = new long[1][queryTokenIds.length];
    Arrays.fill(attentionMask[0], 1);
    long[][] tokenTypeIds = new long[1][queryTokenIds.length];

    inputs.put(MODEL_INPUT_IDS, OnnxTensor.createTensor(environment, inputTokenIds));
    inputs.put(MODEL_TOKEN_TYPE_IDS, OnnxTensor.createTensor(environment, tokenTypeIds));
    inputs.put(MODEL_ATTENTION_MASK, OnnxTensor.createTensor(environment, attentionMask));

    try (OrtSession.Result results = session.run(inputs)) {
      assert (results.get(MODEL_OUTPUT_IDX).isPresent());
      assert (results.get(MODEL_OUTPUT_WEIGHTS).isPresent());

      long[] indexes = (long[]) results.get(MODEL_OUTPUT_IDX).get().getValue();
      float[] weights = (float[]) results.get(MODEL_OUTPUT_WEIGHTS).get().getValue();

      Map<String, Float> tokenFloatWeights = new LinkedHashMap<>();
      for (int i = 0; i < indexes.length; i++) {
        if (indexes[i] == 101 || indexes[i] == 102 || indexes[i] == 0) {
          continue;
        }
        tokenFloatWeights.put(vocab.getToken(indexes[i]), weights[i]);
      }

      return tokenFloatWeights;
    }
  }
}