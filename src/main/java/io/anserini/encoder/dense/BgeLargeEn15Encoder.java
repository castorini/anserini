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
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BgeLargeEn15Encoder extends DenseEncoder {
  static private final String MODEL_URL = "https://huggingface.co/castorini/bge-large-en-v1.5-onnx/resolve/main/bge-large-en-v1.5-optimized.onnx";
  static private final String VOCAB_URL = "https://huggingface.co/castorini/bge-large-en-v1.5-onnx/resolve/main/bge-large-en-v1.5-vocab.txt";
  static private final String CONFIG_URL = "https://huggingface.co/castorini/bge-large-en-v1.5-onnx/resolve/main/config.json";

  static private final String MODEL_NAME = "bge-large-en-v1.5-optimized.onnx";
  static private final String VOCAB_NAME = "bge-large-en-v1.5-vocab.txt";

  static private final int MAX_SEQ_LEN = 512;

  static private final String INSTRUCTION = "Represent this sentence for searching relevant passages: ";
  static private final String MODEL_INPUT_IDS = "input_ids";
  static private final String MODEL_LAST_HIDDEN_STATE = "last_hidden_state";

  public BgeLargeEn15Encoder() throws IOException, OrtException, URISyntaxException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL, CONFIG_URL);
  }

  @Override
  public float[] encode(@NotNull String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add(CLS);
    queryTokens.addAll(this.tokenizer.tokenize(INSTRUCTION + query));
    queryTokens = (queryTokens.size() > MAX_SEQ_LEN - 2) ? queryTokens.subList(0, MAX_SEQ_LEN - 2) : queryTokens;
    queryTokens.add(SEP);
    
    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(queryTokens, MAX_SEQ_LEN);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];
    inputTokenIds[0] = queryTokenIds;
    inputs.put(MODEL_INPUT_IDS, OnnxTensor.createTensor(this.environment, inputTokenIds));

    float[] weights;
    try (OrtSession.Result results = this.session.run(inputs)) {
      assert (results.get(MODEL_LAST_HIDDEN_STATE).isPresent());
      weights = ((float[][][]) results.get(MODEL_LAST_HIDDEN_STATE).get().getValue())[0][0];

      return normalize(weights);
    }
  }
}