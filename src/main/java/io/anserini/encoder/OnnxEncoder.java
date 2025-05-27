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
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

public abstract class OnnxEncoder<T> implements AutoCloseable {
  private static final Logger LOG = LogManager.getLogger(OnnxEncoder.class);

  private static final String CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "pyserini", "encoders").toString();

  private static final String BASE_CONFIG_NAME = "config.json";

  protected static final String CLS = "[CLS]";
  protected static final String SEP = "[SEP]";
  protected static final String PAD = "[PAD]";

  private final String modelName;
  private final String modelUrl;
  private final String vocabName;
  private final String vocabUrl;
  private final String configUrl;

  protected final BertFullTokenizer tokenizer;
  protected final DefaultVocabulary vocab;

  protected final OrtEnvironment environment;
  protected final OrtSession session;

  public OnnxEncoder(@NotNull String modelName, @NotNull String modelUrl, @NotNull String vocabName, @NotNull String vocabUrl, String configUrl)
      throws IOException, OrtException, URISyntaxException {
    this.vocabName = vocabName;
    this.vocabUrl = vocabUrl;
    this.modelName = modelName;
    this.modelUrl = modelUrl;
    this.configUrl = configUrl;

    this.vocab = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath())
        .optUnknownToken("[UNK]")
        .build();
    this.tokenizer = new BertFullTokenizer(vocab, true);

    this.environment = OrtEnvironment.getEnvironment();
    this.session = environment.createSession(getModelPath().toString(), new OrtSession.SessionOptions());
  }

  public OnnxEncoder(@NotNull String modelName, @NotNull String modelUrl, @NotNull String vocabName, @NotNull String vocabUrl) 
      throws IOException, OrtException, URISyntaxException{
    this(modelName, modelUrl, vocabName, vocabUrl, null);
  }

  public Path getVocabPath() throws URISyntaxException, IOException {
    File vocabFile = new File(getCacheDir(), vocabName);
    if (!vocabFile.exists()) {
      FileUtils.copyURLToFile(new URI(vocabUrl).toURL(), vocabFile);
    }

    return vocabFile.toPath();
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public String getCacheDir() {
    File cacheDir = new File(CACHE_DIR);

    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  public Path getModelPath() throws IOException, URISyntaxException {
    File modelFile = new File(getCacheDir(), modelName);
    if (!modelFile.exists()) {
      FileUtils.copyURLToFile(new URI(modelUrl).toURL(), modelFile);
      if (configUrl != null) {
        File configFile = new File(getCacheDir(), this.getClass().getSimpleName() + '-' + BASE_CONFIG_NAME);
        FileUtils.copyURLToFile(new URI(configUrl).toURL(), configFile);
      }
    }

    return modelFile.toPath();
  }

  protected long[] convertTokensToIds(List<String> tokens, int maxLen) {
    int numTokens = Math.min(tokens.size(), maxLen);
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; i++) {
      tokenIds[i] = vocab.getIndex(tokens.get(i));
    }
    return tokenIds;
  }

  protected long[] convertTokensToIds(List<String> tokens) {
    return convertTokensToIds(tokens, Integer.MAX_VALUE);
  }

  public abstract T encode(@NotNull String query) throws OrtException;

  public void close() {
    try {
      this.session.close();
      // Note that we don't need to close the environment: according to docs, it's a no-op.
    } catch (OrtException e) {
      // Nothing we can do at this point, so log and move on.
      LOG.error("Error closing session: {}", e.getMessage());
    }
  }
}
