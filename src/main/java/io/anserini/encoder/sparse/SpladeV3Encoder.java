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

import java.io.IOException;
import java.net.URISyntaxException;

public class SpladeV3Encoder extends SpladePlusPlusEncoder {
  static private final String MODEL_URL = "https://huggingface.co/castorini/splade-v3-onnx/blob/main/splade-v3-optimized.onnx";
  static private final String VOCAB_URL = "https://huggingface.co/castorini/splade-v3-onnx/blob/main/splade-v3-vocab.txt";

  static private final String MODEL_NAME = "splade-v3-optimized.onnx";
  static private final String VOCAB_NAME = "splade-v3-vocab.txt";

  public SpladeV3Encoder() throws IOException, OrtException, URISyntaxException {
    super(MODEL_NAME, MODEL_URL, VOCAB_NAME, VOCAB_URL);
  }
}