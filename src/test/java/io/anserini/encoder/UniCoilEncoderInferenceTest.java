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

 import ai.onnxruntime.OnnxTensor;
 import ai.onnxruntime.OrtEnvironment;
 import ai.onnxruntime.OrtException;
 import ai.onnxruntime.OrtSession;
 import ai.onnxruntime.OrtSession.Result;
 import org.junit.Test;

 import java.io.IOException;
 import java.net.URISyntaxException;
 import java.util.ArrayList;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;

 import static org.junit.Assert.assertArrayEquals;

 public class UniCoilEncoderInferenceTest extends EncoderInferenceTest {

   static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/unicoil.onnx";
   static private final String MODEL_NAME = "unicoil.onnx";

   // TODO(v): Leave a comment about github runners etc.......... !!!!
   static private final Object[][] EXAMPLES = new Object[][] {
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

//       { new long[] { 101, 9375, 1024, 20248, 2078, 102 },
//           new float[] { 2.1916094f, 2.321418f, 0.21508822f, 6.336946f, 4.0119653f, 0.0f } },
//
//       { new long[] { 101, 2043, 2024, 1996, 2176, 2749, 2008, 2552, 2006, 2019, 13297, 1999, 14442, 1029, 102 },
//           new float[] { 1.220808f, 1.7120245f, 0.0f, 0.35091612f, 2.1626499f, 1.3954347f, 0.0f, 1.075113f, 0.56012326f,
//               0.6368706f, 2.6597006f, 0.8614292f, 2.1888595f, 0.7747703f, 0.0f } },




   };

   public UniCoilEncoderInferenceTest() {
     super(MODEL_NAME, MODEL_URL, EXAMPLES);
   }

   @Test
   public void basic() throws OrtException, IOException, URISyntaxException {
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