package io.anserini.search.query;

import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import ai.onnxruntime.OrtEnvironment;
import ai.onnxruntime.OrtException;
import ai.onnxruntime.OrtSession;
import ai.onnxruntime.OrtSession.Result;
import ai.onnxruntime.OnnxTensor;


abstract class SpladePlusPlusEncoderQueryInferenceTest {
  private final String MODEL_NAME;
  private final String MODEL_URL;
  private final Object[][] EXAMPLES;

  public SpladePlusPlusEncoderQueryInferenceTest(String modelName, String modelUrl, Object[][] examples) {
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
        long[] expectedIdx = (long[]) example[1];
        float[] expectedWeights = (float[]) example[2];

        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[][] tokenIds = new long[1][inputIds.length];
        long[][] tokenTypeIdsTensor = new long[1][inputIds.length];
        long[][] attentionMaskTensor = new long[1][inputIds.length];
        Arrays.fill(attentionMaskTensor[0], 1);
        tokenIds[0] = inputIds;
        inputs.put("input_ids", OnnxTensor.createTensor(env, tokenIds));
        inputs.put("token_type_ids", OnnxTensor.createTensor(env, tokenTypeIdsTensor));
        inputs.put("attention_mask", OnnxTensor.createTensor(env, attentionMaskTensor));
        try (Result results = session.run(inputs)) {
          long[] indexes = (long[]) results.get("output_idx").get().getValue();
          float[] weights = (float[]) results.get("output_weights").get().getValue();
          assertArrayEquals(expectedIdx, indexes);
          assertArrayEquals(expectedWeights, weights, 1e-4f);
        }
      }
    }
  }

}
