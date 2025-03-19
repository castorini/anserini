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

import ai.onnxruntime.OrtException;
import io.anserini.encoder.OnnxEncoder;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SparseEncoder extends OnnxEncoder<Map<String, Integer>> {

  protected int weightRange;
  protected int quantRange;

  public static String flatten(Map<String, Integer> intWeights) {
    List<String> tokens = new ArrayList<>();
    for (Map.Entry<String, Integer> entry : intWeights.entrySet()) {
      String token = entry.getKey();
      int weight = entry.getValue();
      for (int i = 0; i < weight; i++) {
        tokens.add(token);
      }
    }

    return String.join(" ", tokens);
  }

  public SparseEncoder(int weightRange, int quantRange,
                       @NotNull String vocabName, @NotNull String vocabUrl,
                       @NotNull String modelName, @NotNull String modelUrl)
      throws IOException, OrtException, URISyntaxException {
    super(vocabName, vocabUrl, modelName, modelUrl);
    this.weightRange = weightRange;
    this.quantRange = quantRange;
  }

  public Map<String, Integer> quantizeFloatWeights(Map<String, Float> tokenFloatWeights) {
    Map<String, Integer> tokenIntWeights = new HashMap<>();
    tokenFloatWeights.forEach((token, weight) -> tokenIntWeights.put(token, Math.round(weight / weightRange * quantRange)));

    return tokenIntWeights;
  }

  @Override
  public Map<String, Integer> encode(@NotNull String query) throws OrtException {
    return quantizeFloatWeights(computeFloatWeights(query));
  }

  public long[] tokenizeToIds(String query) {
    List<String> queryTokens = new ArrayList<>();
    queryTokens.add(CLS);
    queryTokens.addAll(tokenizer.tokenize(query));
    queryTokens.add(SEP);

    return convertTokensToIds(queryTokens);
  }

  protected abstract Map<String, Float> computeFloatWeights(String query) throws OrtException;
}