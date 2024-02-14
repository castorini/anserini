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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.io.FileUtils;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.Vocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;

public abstract class OnnxEncoder<T> {
  protected final BertFullTokenizer tokenizer;

  protected final DefaultVocabulary vocab;

  protected final OrtEnvironment environment;

  protected final OrtSession session;

  static private final String CACHE_DIR = Paths.get(System.getProperty("user.home"), "/.cache/anserini/encoders")
      .toString();

  static protected Path getVocabPath(String vocabName, String vocabURL) throws IOException {
    File vocabFile = new File(getCacheDir(), vocabName);
    if (!vocabFile.exists()) {
      FileUtils.copyURLToFile(new URL(vocabURL), vocabFile);
    }

    return vocabFile.toPath();
  }

  static protected String getCacheDir() {
    File cacheDir = new File(CACHE_DIR);
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  static protected Path getModelPath(String modelName, String modelURL) throws IOException {
    File modelFile = new File(getCacheDir(), modelName);
    if (!modelFile.exists()) {
      FileUtils.copyURLToFile(new URL(modelURL), modelFile);
    }

    return modelFile.toPath();
  }

  protected static long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens, Vocabulary vocab, int maxLen) {
    int numTokens = Math.min(tokens.size(), maxLen);
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  protected static long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens, Vocabulary vocab) {
    int numTokens = tokens.size();
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  /*
   * Normalize a vector using L2 norm
   */
  public static float[] normalize(float[] vector) {
    final float EPS = 1e-12f;
    float norm = 0;
    for (float v : vector) {
      norm += v * v;
    }
    norm = (float) Math.sqrt(norm);

    for (int i = 0; i < vector.length; i++) {
      vector[i] = vector[i] / (norm + EPS);
    }
    return vector;
  }

  public abstract T encode(String query) throws OrtException;

  public OnnxEncoder(String modelName, String modelURL, String vocabName, String vocabURL)
      throws IOException, OrtException {
    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath(vocabName, vocabURL))
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);
    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath(modelName, modelURL).toString(),
        new OrtSession.SessionOptions());
  }

}
