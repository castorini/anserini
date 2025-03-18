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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CosDprDistilEncoder extends DenseEncoder {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cosdpr-distil-optimized.onnx";
  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/cosdpr-distil-vocab.txt";

  static private final String MODEL_NAME = "cosdpr-distil-optimized.onnx";
  static private final String VOCAB_NAME = "cosdpr-distil-vocab.txt";

  public CosDprDistilEncoder() throws IOException, OrtException, URISyntaxException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  public float[] encode(String query) throws OrtException {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(this.tokenizer.tokenize(query));
    queryTokens.add("[SEP]");
    
    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(queryTokens, this.vocab);
    long[][] inputTokenIds = new long[1][queryTokenIds.length];

    inputTokenIds[0] = queryTokenIds;
    inputs.put("input_ids", OnnxTensor.createTensor(this.environment, inputTokenIds));
    try (OrtSession.Result results = this.session.run(inputs)) {
      assert (results.get("pooler_output").isPresent());

      return ((float[][]) results.get("pooler_output").get().getValue())[0];
    }
  }
}
