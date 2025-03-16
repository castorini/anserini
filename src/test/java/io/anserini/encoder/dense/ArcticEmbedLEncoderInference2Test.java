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

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertArrayEquals;

public class ArcticEmbedLEncoderInference2Test {
  // We're running into this issue on GitHub Java CI:
  // > Error: The operation was canceled.
  // Can't reproduce locally, but separating test cases into separate files seems to fix it...
  @Test
  public void testMaxLength() throws OrtException, IOException, URISyntaxException {
    ArcticEmbedLEncoder encoder = new ArcticEmbedLEncoder();
    float[] expectedWeights = (float[]) ArcticEmbedLEncoderInferenceTest.LONG_EXAMPLES[0][1];
    String[] inputStrings = (String[]) ArcticEmbedLEncoderInferenceTest.LONG_EXAMPLES[0][0];

    float[] outputs = encoder.encode(inputStrings[0]);
    assertArrayEquals(expectedWeights, outputs, 1e-4f);
  }
}
