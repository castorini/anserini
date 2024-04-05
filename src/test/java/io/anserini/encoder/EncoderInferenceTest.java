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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public abstract class EncoderInferenceTest {
  protected String modelName;
  protected String modelUrl;
  protected Object[][] examples;
  protected Object[][] longExamples;

  protected String getCacheDir() {
    File cacheDir = new File(System.getProperty("user.home") + "/.cache/anserini/encoders");
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  protected Path getEncoderModelPath() throws IOException, URISyntaxException {
    File modelFile = new File(getCacheDir(), modelName);
    FileUtils.copyURLToFile(new URI(modelUrl).toURL(), modelFile);
    return modelFile.toPath();
  }

  public EncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
    this.modelName = modelName;
    this.modelUrl = modelUrl;
    this.examples = examples;
  }

  public EncoderInferenceTest(String modelName, String modelUrl, Object[][] examples, Object[][] longExamples) {
    this.modelName = modelName;
    this.modelUrl = modelUrl;
    this.examples = examples;
    this.longExamples = longExamples;
  }
}
