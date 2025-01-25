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

public class SnowflakeEmbedLEncoder extends DenseEncoder {
  //TODO: These URLs are not correct because they live on the SSH Orca machine- Temporary fix
    static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/snowflake-embed-l-optimized.onnx";

  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/snowflake-embed-l-vocab.txt";

  static private final String MODEL_NAME = "snowflake-embed-l-optimized.onnx";

  static private final String VOCAB_NAME = "snowflake-embed-l-vocab.txt";

  static private final String INSTRUCTION = "Represent this sentence for searching relevant passages: ";

  static private final int MAX_SEQ_LEN = 512;

  public SnowflakeEmbedLEncoder() throws IOException, OrtException, URISyntaxException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }

  @Override
  public float[] encode(String query) throws OrtException {
    // Keep basic tokenization for now since we know we need tokens (SPLADE does this)
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add("[CLS]");
    queryTokens.addAll(this.tokenizer.tokenize(INSTRUCTION + query));  // INSTRUCTION needs verification
    queryTokens.add("[SEP]");
    
    Map<String, OnnxTensor> inputs = new HashMap<>();
    long[] queryTokenIds = convertTokensToIds(this.tokenizer, queryTokens, this.vocab, MAX_SEQ_LEN);
    
    // TODO: Verify exact input names and formats required by Arctic model
    // TODO: Verify exact output names and how to process them
    // TODO: Add proper error handling like SPLADE
    
    float[] weights = null;
    try (OrtSession.Result results = this.session.run(inputs)) {
        // TODO: Replace with actual output handling once we know the names/format
    }
    return weights;
  }
}
