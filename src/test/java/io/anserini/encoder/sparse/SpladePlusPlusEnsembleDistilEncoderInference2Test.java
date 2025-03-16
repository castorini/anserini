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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class SpladePlusPlusEnsembleDistilEncoderInference2Test {
  // We're running into this issue on GitHub Java CI:
  // > Error: The operation was canceled.
  // Can't reproduce locally, but separating test cases into separate files seems to fix it...
  @SuppressWarnings("unchecked")
  @Test
  public void maxlen() throws OrtException, IOException, URISyntaxException {
    SparseEncoder encoder = new SpladePlusPlusEnsembleDistilEncoder();
    String[] inputStrings = (String[]) SpladePlusPlusEnsembleDistilEncoderInferenceTest.LONG_EXAMPLES[0][0];
    Map<String, Integer> expectedMap = (Map<String, Integer>) SpladePlusPlusEnsembleDistilEncoderInferenceTest.LONG_EXAMPLES[0][1];

    Map<String, Integer> outputs = encoder.getEncodedQueryMap(inputStrings[0]);
    for (Map.Entry<String, Integer> entry : outputs.entrySet()) {
      String key = entry.getKey();
      Integer value = entry.getValue();
      Integer expectedValue = expectedMap.get(key);
      assertEquals(expectedValue, value);
    }
  }
}