package io.anserini.search.query;

import ai.onnxruntime.*;
import ai.onnxruntime.OrtSession.Result;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

public class UniCoilEncoderQueryInferenceTest {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/unicoil.onnx";

  Object[][] examples = new Object[][] {
      { new long[] { 101, 2029, 18714, 7457, 13853, 3798, 1999, 1996, 2668, 1029, 102 },
          new float[] { 1.429669f, 0.36941767f, 2.2388394f, 1.39695f, 3.4305687f, 0.72433805f, 0.26170823f, 0.6414263f,
              2.0127833f, 0.9524705f, 0.0f } },

      { new long[] { 101, 2054, 2024, 1996, 2350, 2576, 4243, 1999, 2307, 3725, 1029, 7276, 2035, 2008, 6611, 1012,
          102 },
          new float[] { 1.6087996f, 0.34130257f, 0.46315512f, 0.76726866f, 1.7555796f, 1.5539078f, 2.5026379f,
              0.5871708f, 1.6406404f, 2.2059104f, 0.6693681f, 1.5679849f, 1.207441f, 0.1747964f, 1.383023f, 1.0706221f,
              0.0f } },

      { new long[] { 101, 2054, 2828, 1997, 4736, 2515, 8611, 2227, 1999, 1051, 1010, 2888, 1996, 5592, 1997, 1996,
          23848, 2072, 102 },
          new float[] { 1.3592957f, 0.2303419f, 0.9996596f, 0.25534713f, 2.13369f, 0.36542463f, 2.8693283f, 1.1475141f,
              0.6408561f, 1.0697018f, 0.04958488f, 1.4982458f, 0.30199072f, 1.6879205f, 0.38464934f, 0.7386115f,
              2.137267f, 0.705949f, 0.0f } },

      { new long[] { 101, 9375, 1024, 20248, 2078, 102 },
          new float[] { 2.1916094f, 2.321418f, 0.21508822f, 6.336946f, 4.0119653f, 0.0f } },

      { new long[] { 101, 2043, 2024, 1996, 2176, 2749, 2008, 2552, 2006, 2019, 13297, 1999, 14442, 1029, 102 },
          new float[] { 1.220808f, 1.7120245f, 0.0f, 0.35091612f, 2.1626499f, 1.3954347f, 0.0f, 1.075113f, 0.56012326f,
              0.6368706f, 2.6597006f, 0.8614292f, 2.1888595f, 0.7747703f, 0.0f } },

      { new long[] { 101, 2129, 2146, 2024, 2057, 9530, 15900, 6313, 2044, 2057, 4608, 1037, 3147, 1012, 1029, 102 },
          new float[] { 1.4291269f, 1.1623853f, 2.2530158f, 0.0f, 1.0501748f, 1.4107478f, 1.5672017f, 1.4331429f,
              1.3216976f, 1.1070791f, 1.0113696f, 0.19434278f, 2.7030065f, 0.62918174f, 0.35845983f, 0.0f } },

      { new long[] { 101, 1996, 7450, 2008, 21312, 1996, 13474, 2038, 1996, 2157, 2000, 2019, 4905, 2003, 1996, 1035,
          1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 7450, 1012, 102 },
          new float[] { 1.8520677f, 1.1448987f, 2.318459f, 0.7175932f, 2.0155277f, 0.784888f, 2.293599f, 0.77260935f,
              0.14165235f, 1.9080899f, 0.7096409f, 1.024202f, 2.9227517f, 0.9579353f, 1.1324929f, 0.0f, 0.0f, 0.0f,
              0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 2.4671867f, 1.5324136f, 0.0f } },

      { new long[] { 101, 2054, 2001, 11534, 1005, 1055, 4602, 6691, 2000, 12761, 3399, 1029, 102 },
          new float[] { 1.2704929f, 0.0f, 1.1832705f, 2.4083762f, 0.34482002f, 0.49629262f, 2.3794782f, 1.8941771f,
              0.42992824f, 1.5892929f, 0.47762313f, 0.95083314f, 0.0f } },

      { new long[] { 101, 2019, 5983, 8761, 2003, 7356, 2011, 1035, 1035, 1035, 1035, 1035, 1012, 102 },
          new float[] { 1.2431517f, 0.8008548f, 2.498167f, 1.27854f, 0.7832665f, 1.7345628f, 0.16658483f, 0.0f, 0.0f,
              0.0f, 0.0f, 0.0f, 0.2535707f, 0.0f } },

      { new long[] { 101, 2054, 8915, 8737, 2079, 2017, 5660, 15960, 24494, 2015, 2006, 1999, 1996, 17428, 1029, 1998,
          2005, 2129, 2146, 1029, 102 },
          new float[] { 1.1041864f, 0.0f, 0.47533986f, 1.669825f, 0.2934663f, 0.78933185f, 0.9406975f, 2.1501098f,
              2.362949f, 0.46458432f, 0.9171016f, 0.39884382f, 0.346544f, 1.7016646f, 0.33858445f, 0.32457188f,
              0.70062816f, 0.9733413f, 1.1381183f, 0.54110324f, 0.0f } },

      { new long[] { 101, 2029, 18672, 8844, 26450, 6740, 16896, 2006, 1996, 4942, 15782, 14289, 8017, 1042, 21842,
          1997, 1996, 8040, 9331, 7068, 1998, 19274, 2015, 2006, 1996, 8276, 7270, 21769, 1997, 1996, 20368, 7946, 1029,
          102 },
          new float[] { 1.2975391f, 0.41514271f, 1.5634528f, 1.0063248f, 1.7381638f, 1.0919806f, 1.5582242f,
              0.43482706f, 0.33785614f, 1.0224074f, 0.79580104f, 0.5206254f, 0.36112663f, 0.5333182f, 1.075079f,
              0.20146157f, 0.36403617f, 1.2272573f, 1.0263921f, 0.699825f, 0.297133f, 1.2347529f, 0.0f, 0.46168548f,
              0.3573556f, 1.3769448f, 1.0224242f, 1.0108802f, 0.11389083f, 0.41117048f, 1.5779594f, 0.4974613f,
              0.7945211f, 0.0f } },
  };

  static private String getCacheDir() {
    File cacheDir = new File(System.getProperty("user.home") + "/.cache/anserini/test");
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  static private Path getUnicoilEncoderModelPath() throws IOException {
    File modelFile = new File(getCacheDir(), "unicoil.onnx");
    FileUtils.copyURLToFile(new URL(MODEL_URL), modelFile);
    return modelFile.toPath();
  }

  @Test
  public void basic() throws OrtException, IOException {
    String modelPath = getUnicoilEncoderModelPath().toString();
    try (OrtEnvironment env = OrtEnvironment.getEnvironment();
        OrtSession.SessionOptions options = new OrtSession.SessionOptions();
        OrtSession session = env.createSession(modelPath, options)) {

      for (Object[] example : examples) {
        long[] inputIds = (long[]) example[0];
        float[] expectedWeights = (float[]) example[1];
        Map<String, OnnxTensor> inputs = new HashMap<>();
        long[][] tokenIds = new long[1][inputIds.length];
        tokenIds[0] = inputIds;
        inputs.put("inputIds", OnnxTensor.createTensor(env, tokenIds));
        try (Result results = session.run(inputs)) {
          float[] computedWeights = flatten(results.get(0).getValue());
          assertArrayEquals(expectedWeights, computedWeights, 1e-5f);
        }
      }
    }
  }

  private float[] flatten(Object obj) {
    List<Float> weightsList = new ArrayList<>();
    Object[] inputs = (Object[]) obj;
    for (Object input : inputs) {
      float[][] weights = (float[][]) input;
      for (float[] weight : weights) {
        weightsList.add(weight[0]);
      }
    }
    return toArray(weightsList);
  }

  private float[] toArray(List<Float> input) {
    float[] output = new float[input.size()];
    for (int i = 0; i < output.length; i++) {
      output[i] = input.get(i);
    }
    return output;
  }
}