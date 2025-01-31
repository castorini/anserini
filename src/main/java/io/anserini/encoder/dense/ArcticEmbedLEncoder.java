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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

/**
 * ArcticEmbedL
 */
public class ArcticEmbedLEncoder extends DenseEncoder {
  static private final String MODEL_URL = "https://huggingface.co/Snowflake/snowflake-arctic-embed-l/blob/main/onnx/model.onnx";
  static private final String VOCAB_URL = "https://huggingface.co/Snowflake/snowflake-arctic-embed-l/blob/main/vocab.txt";

  static private final String MODEL_NAME = "snowflake-arctic-embed-l-official.onnx";
  static private final String VOCAB_NAME = "snowflake-arctic-embed-l-vocab.txt";

  static private final String INSTRUCTION = "Represent this sentence for searching relevant passages: ";
  static private final int MAX_SEQ_LEN = 512;

  public ArcticEmbedLEncoder() throws IOException, OrtException, URISyntaxException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  public float[] encode(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(tokenizer.tokenize(INSTRUCTION + query));
    queryTokens.add("[SEP]");

    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(tokenizer, queryTokens, vocab, MAX_SEQ_LEN);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];
    inputTokenIds[0] = queryTokenIds;

    long[][] attentionMask = new long[1][queryTokenIds.length];
    long[][] tokenTypeIds = new long[1][queryTokenIds.length];
    Arrays.fill(attentionMask[0], 1);

    inputs.put("input_ids", OnnxTensor.createTensor(environment, inputTokenIds));
    inputs.put("token_type_ids", OnnxTensor.createTensor(environment, tokenTypeIds));
    inputs.put("attention_mask", OnnxTensor.createTensor(environment, attentionMask));

    float[] weights = null;
    try (OrtSession.Result results = session.run(inputs)) {
      float[][][] tensorData = (float[][][]) ((OnnxTensor) results.get(0)).getValue();

      weights = new float[1024];
      System.arraycopy(tensorData[0][0], 0, weights, 0, 1024);

      return normalize(weights);
    } catch (OrtException e) {
      throw e;
    }
  }
}