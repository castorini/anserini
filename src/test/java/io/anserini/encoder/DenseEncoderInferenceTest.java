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

abstract class DenseEncoderInferenceTest extends EncoderInferenceTest {

  public DenseEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples) {
    super(modelName, modelUrl, examples);
  }

  public DenseEncoderInferenceTest(String modelName, String modelUrl, Object[][] examples, Object[][] longExamples) {
    super(modelName, modelUrl, examples, longExamples);
  }

  protected void basicTest() throws IOException, OrtException {
    String modelPath = getEncoderModelPath().toString();
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, options)) {

      for (Object[] example : examples) {
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