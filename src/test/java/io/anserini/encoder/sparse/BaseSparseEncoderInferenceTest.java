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

import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BaseSparseEncoderInferenceTest {
  public void testExamples(SparseExampleOutputPair[] examples, SparseEncoder encoder) throws OrtException {
    for (SparseExampleOutputPair pair : examples) {
      Map<String, Integer> outputs = encoder.encode(pair.example());
      Map<String, Integer> expectedWeights = pair.output();

      assertEquals(expectedWeights.size(), outputs.size());
      outputs.forEach((token, weight) -> assertEquals(expectedWeights.get(token), weight));
    }
  }
}
