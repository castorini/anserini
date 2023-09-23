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

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

import java.io.IOException;
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

  private final BertFullTokenizer tokenizer;

  private final DefaultVocabulary vocab;

  private final OrtEnvironment environment;

  private final OrtSession session;

  public UniCoilEncoder() throws IOException, OrtException {
    super(5, 256);
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(VOCAB_NAME, VOCAB_URL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(MODEL_NAME, MODEL_URL).toString(), new OrtSession.SessionOptions());
  }

  @Override
  public String encode(String query) throws OrtException {
    String encodedQuery = "";
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    encodedQuery = generateEncodedQuery(tokenWeightMap);
    return encodedQuery;
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

  private Map<String, Float> getTokenWeightMap(List<String> tokens, float[] computedWeights) {
    Map<String, Float> tokenWeightMap = new LinkedHashMap<>();
    for (int i = 0; i < tokens.size(); ++i) {
      String token = tokens.get(i);
      float tokenWeight = computedWeights[i];
      if (token.equals("[CLS]")) {
        continue;
      } else if (token.equals("[PAD]")) {
        break;
      } else if (tokenWeightMap.containsKey(token)) {
        Float accumulatedWeight = tokenWeightMap.get(token);
        tokenWeightMap.put(token, accumulatedWeight + tokenWeight);
      } else {
        tokenWeightMap.put(token, tokenWeight);
      }
    }
    return tokenWeightMap;
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
    inputs.put("inputIds", OnnxTensor.createTensor(environment, inputTokenIds));

    Map<String, Float> tokenWeightMap = null;
    try (OrtSession.Result results = session.run(inputs)) {
      float[] computedWeights = flatten(results.get(0).getValue());
      tokenWeightMap = getTokenWeightMap(queryTokens, computedWeights);
    } catch (OrtException e) {
      e.printStackTrace();
    }
    return tokenWeightMap;
  }

}