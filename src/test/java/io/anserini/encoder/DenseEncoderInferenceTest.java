package io.anserini.encoder;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import ai.onnxruntime.OnnxTensor;
import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;

abstract class DenseEncoderInferenceTest {
  private final String MODEL_NAME;
  private final String MODEL_URL;
  private final Object[][] EXAMPLES;

  public DenseEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
    this.MODEL_NAME = modelName;
    this.MODEL_URL = modelUrl;
    this.EXAMPLES = examples;
  }

  protected String getCacheDir() {
    File cacheDir = new File(System.getProperty("user.home") + "/.cache/anserini/test");
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  protected Path getEncoderModelPath() throws IOException {
    File modelFile = new File(getCacheDir(), MODEL_NAME);
    FileUtils.copyURLToFile(new URL(MODEL_URL), modelFile);
    return modelFile.toPath();
  }

  protected void basicTest() throws IOException, OrtException {
    String modelPath = getEncoderModelPath().toString();
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, options)) {

      for (Object[] example : EXAMPLES) {
        long[] inputIds = (long[]) example[0];
        float[] expectedWeights = (float[]) example[1];

        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[][] tokenIds = new long[1][inputIds.length];
        tokenIds[0] = inputIds;
        inputs.put("input_ids", OnnxTensor.createTensor(env, tokenIds));

        try (Result results = session.run(inputs)) {
          float[] weights = ((float[][]) results.get("pooler_output").get().getValue())[0];
          assertArrayEquals(expectedWeights, weights, 1e-4f);
        }
      }
    }
  }
}