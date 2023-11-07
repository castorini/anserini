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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpladePlusPlusSelfDistilEncoder extends SparseEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/splade-pp-sd-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  static private final String MODEL_NAME = "splade-pp-sd-optimized.onnx";

  static private final String VOCAB_NAME = "splade-pp-sd-vocab.txt";

  public SpladePlusPlusSelfDistilEncoder() throws IOException, OrtException {
    super(5, 256, MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  public String encode(String query) throws OrtException {
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    return generateEncodedQuery(tokenWeightMap);
  }

  @Override
  protected Map<String, Float> getTokenWeightMap(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add("[SEP]");

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens, vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    long[][] attentionMask = new long[1][queryTokenIds.length];
    long[][] tokenTypeIds = new long[1][queryTokenIds.length];

    // initialize attention mask with all 1s
    Arrays.fill(attentionMask[0], 1);
    inputs.put("input_ids", OnnxTensor.createTensor(environment, inputTokenIds));
    inputs.put("token_type_ids", OnnxTensor.createTensor(environment, tokenTypeIds));
    inputs.put("attention_mask", OnnxTensor.createTensor(environment, attentionMask));
    Map<String, Float> tokenWeightMap = null;
    try (OrtSession.Result results = session.run(inputs)) {
      long[] indexes = (long[]) results.get("output_idx").get().getValue();
      float[] weights = (float[]) results.get("output_weights").get().getValue();
      tokenWeightMap = getTokenWeightMap(indexes, weights, vocab);
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return tokenWeightMap;
  }

}