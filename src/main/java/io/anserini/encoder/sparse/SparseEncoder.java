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

import io.anserini.encoder.OnnxEncoder;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.onnxruntime.OrtException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class SparseEncoder extends OnnxEncoder<String> {

  protected int weightRange;

  protected int quantRange;

  public SparseEncoder(int weightRange, int quantRange, String vocabName, String vocabURL, String modelName,
      String modelURL) throws IOException, OrtException {
    super(vocabName, vocabURL, modelName, modelURL);
    this.weightRange = weightRange;
    this.quantRange = quantRange;
  }

  public String generateEncodedQuery(Map<String, Float> tokenWeightMap) {
    /*
     * This function generates the encoded query.
     */
    List<String> encodedQuery = new ArrayList<>();
    for (Map.Entry<String, Float> entry : tokenWeightMap.entrySet()) {
      String token = entry.getKey();
      Float tokenWeight = entry.getValue();
      int weightQuanted = Math.round(tokenWeight / weightRange * quantRange);
      for (int i = 0; i < weightQuanted; ++i) {
        encodedQuery.add(token);
      }
    }
    return String.join(" ", encodedQuery);
  }

  public Map<String, Integer> getEncodedQueryMap(Map<String, Float> tokenWeightMap) throws OrtException {
    Map<String, Integer> encodedQuery = new HashMap<>();
    for (Map.Entry<String, Float> entry : tokenWeightMap.entrySet()) {
      String token = entry.getKey();
      Float tokenWeight = entry.getValue();
      int weightQuanted = Math.round(tokenWeight / weightRange * quantRange);
      encodedQuery.put(token, weightQuanted);
    }
    return encodedQuery;
  }

  public Map<String, Integer> getEncodedQueryMap(String query) throws OrtException {
    Map<String, Float> tokenWeightMap = getTokenWeightMap(query);
    return getEncodedQueryMap(tokenWeightMap);
  }

  static protected Map<String, Float> getTokenWeightMap(long[] indexes, float[] computedWeights,
      DefaultVocabulary vocab) {
    /*
     * This function returns a map of token to its weight.
     */
    Map<String, Float> tokenWeightMap = new LinkedHashMap<>();

    for (int i = 0; i < indexes.length; i++) {
      if (indexes[i] == 101 || indexes[i] == 102 || indexes[i] == 0) {
        continue;
      }
      tokenWeightMap.put(vocab.getToken(indexes[i]), computedWeights[i]);
    }
    return tokenWeightMap;
  }

  protected abstract Map<String, Float> getTokenWeightMap(String query) throws OrtException;
}