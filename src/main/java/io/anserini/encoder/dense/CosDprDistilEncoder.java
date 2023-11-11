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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

/**
 * CosDPRDistil
 */
public class CosDprDistilEncoder extends DenseEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cos-dpr-distil-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cos-dpr-distil-vocab.txt";

  static private final String MODEL_NAME = "cos-dpr-distil-optimized.onnx";

  static private final String VOCAB_NAME = "cos-dpr-distil-vocab.txt";

  public CosDprDistilEncoder() throws IOException, OrtException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  public float[] encode(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(this.tokenizer.tokenize(query));
    queryTokens.add("[SEP]");
    
    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(this.tokenizer, queryTokens, this.vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    inputs.put("input_ids", OnnxTensor.createTensor(this.environment, inputTokenIds));
    float[] weights = null;
    try (OrtSession.Result results = this.session.run(inputs)) {
      weights = ((float[][]) results.get("pooler_output").get().getValue())[0];
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return weights;
  }

}