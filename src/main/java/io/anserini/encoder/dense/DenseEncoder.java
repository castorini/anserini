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

import io.anserini.encoder.OnnxEncoder;

import java.io.IOException;

import ai.onnxruntime.OrtException;

/**
 * DenseEncoder
 */
public abstract class DenseEncoder extends OnnxEncoder<float[]> {

  public DenseEncoder(String modelName, String modelURL, String vocabName, String vocabURL)
      throws IOException, OrtException {
    super(modelName, modelURL, vocabName, vocabURL);
  }

}