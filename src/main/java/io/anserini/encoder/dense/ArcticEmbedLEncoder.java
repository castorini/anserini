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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 
 public class ArcticEmbedLEncoder extends DenseEncoder {
   static private final String MODEL_URL = "https://huggingface.co/castorini/arctic-embed-l-onnx/resolve/main/arctic-embed-l-official.onnx";
   static private final String VOCAB_URL = "https://huggingface.co/castorini/arctic-embed-l-onnx/resolve/main/arctic-embed-l-official-vocab.txt";
   static private final String CONFIG_URL = "https://huggingface.co/castorini/arctic-embed-l-onnx/resolve/main/config.json";
 
   static private final String MODEL_NAME = "snowflake-arctic-embed-l-official.onnx";
   static private final String VOCAB_NAME = "snowflake-arctic-embed-l-vocab.txt";

   static private final int MAX_SEQ_LEN = 512;
   static private final int EMBEDDING_DIM = 1024;

   static private final String INSTRUCTION = "Represent this sentence for searching relevant passages: ";
   static private final String MODEL_INPUT_IDS = "input_ids";
   static private final String MODEL_TOKEN_TYPE_IDS = "token_type_ids";
   static private final String MODEL_ATTENTION_MASK = "attention_mask";

   public ArcticEmbedLEncoder() throws IOException, OrtException, URISyntaxException {
     super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL, CONFIG_URL);
   }
 
   @Override
   public float[] encode(@NotNull String query) throws OrtException {
     List<String> queryTokens = new ArrayList<>();
     queryTokens.add(CLS);
     queryTokens.addAll(tokenizer.tokenize(INSTRUCTION + query));
     queryTokens.add(SEP);
 
     Map<String, OnnxTensor> inputs = new HashMap<>();
     long[] queryTokenIds = convertTokensToIds(queryTokens, MAX_SEQ_LEN);
     long[][] inputTokenIds = new long[1][queryTokenIds.length];
     inputTokenIds[0] = queryTokenIds;
     long[][] tokenTypeIds = new long[1][queryTokenIds.length];
     long[][] attentionMask = new long[1][queryTokenIds.length];
     Arrays.fill(attentionMask[0], 1);
 
     inputs.put(MODEL_INPUT_IDS, OnnxTensor.createTensor(environment, inputTokenIds));
     inputs.put(MODEL_TOKEN_TYPE_IDS, OnnxTensor.createTensor(environment, tokenTypeIds));
     inputs.put(MODEL_ATTENTION_MASK, OnnxTensor.createTensor(environment, attentionMask));
 
     float[] embeddings = new float[EMBEDDING_DIM];
     try (OrtSession.Result results = session.run(inputs)) {
       float[][][] tensorData = (float[][][]) results.get(0).getValue();
       System.arraycopy(tensorData[0][0], 0, embeddings, 0, EMBEDDING_DIM);

       return normalize(embeddings);
     }
   }
 }