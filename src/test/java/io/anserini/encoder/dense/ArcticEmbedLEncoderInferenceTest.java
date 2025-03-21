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

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertTrue;

public class ArcticEmbedLEncoderInferenceTest extends DenseEncoderInferenceTest {
  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/arctic-embed-l-official.onnx";
  static private final String MODEL_NAME = "arctic-embed-l-official.onnx";

  private static final DenseExampleOutputPair[] EXAMPLES = {
      new DenseExampleOutputPair(
          "In the dawn of the 21st century, humanity stands on the brink of one " +
              "of the most transformative periods in history: the rise of artificial " +
              "intelligence (AI). This technological revolution promises to redefine " +
              "the way we live, work, and interact with the world around us. However, " +
              "as with any major technological advancement, the implications of AI for " +
              "society are complex, nuanced, and not entirely predictable. This essay " +
              "explores the potential impacts of AI on various aspects of human life, " +
              "including employment, ethics, personal privacy, and societal structures.",
          new float[] {0.028662f, -0.072173f, 0.013642f, -0.016277f, 0.025611f, 0.024395f, -0.013313f, 0.015624f, -0.029529f, -0.031812f, -0.002920f, 0.021264f, -0.029561f, 0.086086f, -0.023380f, -0.024218f, 0.043618f, 0.008462f, -0.015468f, 0.030845f, 0.036422f, 0.010022f, -0.045453f, 0.004422f, -0.025666f, 0.022788f, -0.039695f, -0.054332f, -0.059407f, 0.020087f, -0.012940f, 0.011038f, -0.033067f, -0.000805f, -0.019342f, -0.025109f, -0.037217f, -0.019784f, 0.005526f, -0.031904f, 0.026270f, 0.041281f, 0.027910f, 0.053008f, -0.002515f, -0.019099f, -0.035629f, 0.017943f, -0.051120f, 0.004236f, 0.028239f, -0.002816f, -0.062503f, -0.007217f, -0.029050f, -0.019619f, -0.008205f, -0.014517f, 0.022209f, -0.066957f, -0.009131f, 0.058094f, -0.038384f, -0.009051f, 0.001238f, 0.033840f, -0.028191f, 0.002178f, -0.005863f, -0.007354f, 0.019712f, 0.073143f, -0.076650f, 0.039337f, 0.092317f, 0.000566f, -0.009356f, 0.025793f, -0.033748f, 0.027637f, 0.019302f, -0.039390f, 0.013541f, -0.003421f, 0.009189f, 0.045791f, 0.036164f, 0.002350f, -0.001840f, -0.046000f, 0.045324f, 0.030724f, -0.008618f, 0.011436f, -0.017096f, 0.011963f, 0.025548f, -0.033445f, 0.043933f, -0.003626f, -0.012009f, 0.011415f, -0.016025f, -0.019208f, 0.014276f, -0.015682f, 0.022414f, -0.053741f, -0.028590f, -0.035825f, -0.048910f, -0.057389f, 0.048348f, 0.042945f, 0.003487f, 0.014819f, 0.040764f, -0.034954f, 0.010269f, 0.027979f, -0.038314f, -0.009079f, -0.031634f, -0.026983f, -0.036280f, 0.064722f, 0.003138f, 0.028974f, 0.031841f, 0.008287f, -0.043570f, -0.050897f, -0.065751f, -0.021143f, 0.028732f, -0.008679f, -0.034447f, -0.033803f, -0.012642f, 0.076527f, 0.028198f, -0.008754f, 0.005736f, 0.017474f, 0.043861f, 0.006540f, -0.004357f, 0.034931f, -0.017634f, -0.047323f, -0.064913f, 0.086174f, -0.010710f, 0.009420f, -0.026358f, -0.009668f, -0.024232f, 0.052014f, 0.011176f, 0.029115f, 0.006438f, 0.019044f, -0.017264f, 0.051567f, -0.038451f, -0.009206f, 0.019178f, 0.052758f, 0.037910f, -0.023638f, 0.033097f, 0.017241f, -0.046431f, 0.024619f, 0.013064f, -0.021784f, -0.010662f, -0.060487f, -0.053646f, -0.008597f, -0.046351f, -0.100811f, -0.002856f, -0.033346f, 0.060671f, 0.028288f, -0.031270f, -0.045548f, 0.033064f, -0.015448f, -0.021230f, 0.001320f, 0.042804f, -0.001496f, -0.014235f, -0.017687f, -0.036612f, 0.014573f, 0.006947f, 0.027269f, 0.021019f, 0.006149f, 0.037800f, 0.049890f, 0.012801f, 0.018560f, -0.079721f, 0.023481f, -0.030174f, -0.012010f, -0.023727f, 0.063843f, 0.013166f, 0.060601f, 0.009180f, 0.032113f, -0.018665f, 0.044044f, -0.029247f, 0.007642f, -0.005684f, 0.039901f, 0.026085f, -0.007755f, 0.038783f, 0.026113f, -0.015255f, -0.054023f, -0.027233f, 0.001904f, -0.032107f, 0.016166f, -0.038700f, -0.014786f, 0.049142f, 0.033683f, 0.011890f, 0.007836f, 0.008756f, -0.085006f, -0.041707f, -0.000871f, 0.040850f, 0.003390f, 0.008516f, -0.013701f, 0.018844f, -0.035166f, -0.002961f, 0.027703f, -0.003568f, 0.050732f, 0.009471f, -0.005039f, -0.042090f, -0.026369f, -0.050150f, -0.025689f, -0.012335f, 0.004938f, 0.010261f, -0.047069f, -0.025273f, 0.047470f, -0.027342f, 0.038412f, 0.032988f, -0.010909f, 0.050895f, 0.022040f, 0.001445f, 0.032813f, -0.017971f, -0.045337f, -0.024449f, 0.023729f, -0.016042f, -0.029678f, 0.018720f, 0.032744f, 0.013448f, 0.011150f, -0.047717f, 0.011838f, -0.017999f, -0.010761f, 0.024486f, -0.012443f, 0.043522f, 0.017245f, -0.007875f, -0.020626f, 0.017336f, -0.006868f, 0.017701f, 0.006223f, -0.062476f, -0.003740f, -0.041087f, 0.013650f, -0.031763f, -0.025744f, -0.001505f, -0.023904f, 0.006878f, -0.016659f, 0.003626f, -0.001727f, 0.059554f, 0.006449f, -0.004452f, 0.027882f, -0.014491f, -0.041657f, -0.095922f, -0.022895f, -0.012527f, -0.001754f, 0.022292f, 0.020926f, 0.004720f, 0.000607f, -0.052606f, -0.020877f, -0.034876f, 0.060328f, -0.019834f, -0.015507f, -0.001876f, 0.007068f, -0.034343f, -0.033476f, 0.063212f, -0.007230f, -0.018411f, -0.005125f, 0.027830f, -0.018073f, -0.029776f, -0.011521f, -0.000030f, 0.033963f, -0.015684f, -0.061807f, -0.020637f, 0.070888f, -0.020306f, 0.016839f, -0.040215f, -0.019754f, -0.037352f, -0.033052f, -0.031144f, -0.008811f, 0.001342f, -0.048529f, 0.025131f, -0.017051f, 0.014952f, 0.001391f, 0.010350f, -0.027528f, -0.022744f, -0.029556f, 0.031438f, 0.006321f, 0.040186f, 0.040778f, -0.008258f, -0.013096f, -0.016923f, 0.009654f, 0.006611f, -0.047742f, -0.005000f, 0.016325f, -0.048394f, 0.091617f, 0.019376f, -0.001578f, 0.026807f, 0.002535f, 0.001648f, 0.009078f, -0.025886f, 0.001915f, 0.013055f, 0.045042f, -0.006482f, 0.014705f, 0.034428f, -0.016082f, -0.004586f, 0.007931f, -0.010248f, -0.041472f, -0.001523f, -0.007928f, -0.001592f, 0.045641f, 0.033929f, 0.044523f, 0.025259f, 0.046082f, -0.034059f, -0.021428f, 0.003782f, 0.044223f, -0.004032f, -0.011197f, 0.031545f, 0.020819f, -0.056747f, 0.029746f, 0.018480f, 0.011872f, -0.064024f, -0.004296f, 0.045505f, 0.020635f, 0.030733f, 0.006400f, -0.009206f, 0.001701f, 0.028089f, 0.024451f, -0.007197f, 0.024095f, -0.006806f, -0.011451f, 0.022125f, -0.032235f, 0.021782f, 0.012920f, -0.008972f, -0.009784f, 0.008844f, -0.031876f, -0.018560f, -0.007170f, 0.023528f, 0.033968f, 0.024567f, -0.059129f, -0.023137f, -0.019699f, -0.004820f, -0.011913f, -0.011062f, -0.025891f, 0.003478f, 0.045663f, 0.000081f, 0.014830f, -0.038668f, -0.021153f, 0.004282f, 0.028133f, 0.011512f, 0.009209f, 0.003934f, -0.033638f, -0.009753f, 0.021022f, 0.012609f, 0.007198f, -0.041930f, -0.053681f, 0.011124f, -0.003615f, -0.033749f, -0.015354f, 0.002008f, 0.051187f, -0.022971f, 0.030443f, 0.039258f, -0.013610f, -0.009675f, 0.004952f, 0.015140f, 0.014261f, 0.003901f, 0.055460f, -0.004767f, 0.022593f, 0.015069f, 0.016139f, 0.017401f, 0.015070f, -0.009741f, -0.024359f, 0.042821f, -0.001511f, -0.020358f, -0.023503f, 0.022377f, -0.007187f, 0.002449f, -0.011224f, -0.009180f, -0.009102f, 0.025049f, 0.001238f, -0.017150f, 0.013589f, 0.039145f, 0.062643f, -0.028407f, -0.025439f, 0.047493f, -0.009870f, -0.023370f, -0.017590f, 0.032626f, -0.032406f, 0.005186f, -0.031217f, -0.025801f, -0.020323f, -0.004223f, -0.007607f, 0.025364f, 0.029053f, 0.001064f, 0.005480f, 0.007390f, -0.010935f, 0.018522f, 0.003972f, -0.031799f, 0.048851f, -0.064968f, 0.051006f, -0.018954f, -0.023718f, -0.010632f, 0.002297f, -0.019829f, -0.004521f, -0.004310f, 0.014941f, 0.021213f, -0.002507f, -0.015382f, -0.034529f, 0.015984f, 0.050223f, -0.037202f, -0.008812f, 0.058870f, 0.049337f, 0.001572f, -0.003008f, -0.038189f, 0.009844f, -0.042192f, -0.021732f, -0.018988f, -0.011341f, 0.010646f, 0.041783f, 0.001940f, -0.024522f, -0.004215f, -0.048193f, 0.027473f, -0.014837f, 0.010294f, -0.003770f, -0.044247f, 0.007809f, 0.027037f, -0.024672f, 0.011187f, -0.028192f, -0.002908f, -0.031922f, -0.038215f, -0.002363f, -0.028411f, -0.000201f, 0.024218f, -0.001417f, 0.029632f, 0.010703f, -0.028849f, 0.025791f, -0.030146f, -0.019108f, 0.028553f, 0.011963f, -0.010036f, 0.023044f, -0.008307f, -0.043976f, 0.090003f, -0.031048f, -0.012364f, 0.027057f, 0.005863f, 0.002500f, 0.021448f, -0.054002f, -0.016118f, 0.014588f, 0.003172f, 0.033061f, 0.013096f, -0.031914f, -0.004239f, -0.039593f, 0.024894f, 0.008950f, 0.008721f, -0.038773f, -0.023337f, -0.005635f, -0.002138f, 0.001355f, -0.055277f, -0.023385f, 0.034749f, 0.041025f, 0.000729f, -0.078366f, -0.005030f, 0.027301f, -0.012182f, -0.004969f, 0.016201f, 0.037732f, -0.015106f, 0.018452f, 0.040246f, -0.013612f, -0.010348f, -0.008480f, -0.010603f, 0.012227f, -0.019335f, -0.007332f, -0.063782f, 0.004037f, -0.059679f, 0.008528f, 0.004755f, 0.006247f, 0.005058f, -0.031401f, -0.004324f, 0.021471f, 0.038653f, 0.022483f, 0.050550f, -0.042157f, -0.000071f, -0.004196f, -0.012193f, -0.024432f, 0.021969f, -0.011790f, 0.041508f, -0.010719f, 0.022240f, 0.102043f, 0.040185f, -0.004466f, 0.030727f, 0.067838f, 0.013215f, -0.008203f, 0.006668f, -0.044647f, -0.009458f, -0.027874f, 0.020805f, -0.088589f, 0.023244f, -0.015085f, 0.000929f, -0.046407f, -0.017681f, -0.045308f, -0.021651f, -0.007394f, -0.015129f, -0.033457f, -0.037918f, -0.040281f, -0.033856f, 0.021109f, -0.033505f, 0.024811f, 0.050895f, -0.042535f, -0.015468f, 0.034259f, 0.037594f, 0.046439f, -0.015005f, -0.042127f, 0.024766f, -0.042649f, 0.055836f, 0.041836f, -0.041699f, -0.044720f, -0.040495f, 0.006886f, 0.012196f, -0.035462f, 0.013280f, -0.003946f, -0.009906f, -0.002588f, 0.042607f, -0.011424f, 0.029374f, 0.008802f, 0.027582f, 0.024879f, -0.018414f, -0.024806f, 0.091140f, 0.044383f, -0.073645f, -0.001905f, 0.007030f, -0.014695f, -0.065307f, -0.011076f, 0.026153f, -0.032155f, 0.042339f, -0.041379f, -0.018512f, 0.002293f, 0.014338f, -0.012671f, 0.030309f, -0.054456f, -0.006625f, 0.025860f, -0.051046f, 0.075630f, -0.001109f, 0.002079f, -0.015716f, -0.032158f, -0.013544f, 0.011710f, -0.021878f, -0.050607f, -0.045111f, 0.009962f, 0.010265f, -0.007610f, 0.066107f, -0.034829f, 0.052121f, -0.012651f, 0.007224f, 0.010081f, 0.022054f, -0.025462f, 0.005975f, 0.054561f, 0.044116f, -0.021845f, -0.008557f, 0.039787f, 0.044673f, 0.002299f, 0.015615f, 0.060853f, 0.013593f, 0.021073f, -0.021170f, -0.002039f, -0.007606f, -0.013238f, 0.015050f, 0.040908f, 0.026205f, 0.007134f, -0.062691f, 0.024492f, 0.004606f, 0.011553f, 0.041533f, -0.081843f, 0.011101f, 0.032250f, -0.020062f, -0.010585f, 0.010184f, -0.025758f, -0.049754f, 0.022829f, -0.002642f, -0.031810f, 0.022149f, -0.025634f, 0.009565f, -0.025900f, -0.008311f, 0.027165f, -0.035501f, -0.039164f, 0.016112f, -0.005410f, 0.047838f, 0.008695f, -0.022600f, 0.031282f, 0.039109f, -0.003496f, -0.013666f, -0.063432f, 0.046242f, 0.011999f, 0.011869f, -0.038924f, 0.010039f, -0.010353f, -0.013876f, 0.000492f, 0.020892f, -0.039051f, -0.064412f, -0.005380f, 0.010950f, 0.007073f, -0.016769f, 0.050948f, 0.006701f, 0.016997f, -0.001104f, -0.064401f, 0.028575f, 0.026822f, -0.002033f, -0.005572f, 0.006577f, -0.012737f, -0.003676f, -0.018102f, 0.079312f, 0.051727f, 0.069608f, 0.054609f, 0.022840f, -0.010330f, 0.017522f, -0.016860f, 0.034316f, 0.022739f, 0.016288f, -0.007741f, -0.050996f, 0.016353f, 0.022966f, -0.075980f, 0.047846f, 0.026050f, 0.001248f, 0.015603f, 0.046245f, 0.029058f, 0.032923f, 0.004917f, -0.003139f, 0.054877f, 0.011280f, -0.014855f, -0.005997f, 0.001545f, 0.020185f, 0.012821f, 0.038025f, 0.078309f, -0.001664f, -0.052530f, 0.023966f, 0.097405f, -0.003193f, 0.082989f, -0.036531f, -0.044326f, -0.006901f, -0.055838f, -0.018695f, 0.016632f, -0.016034f, 0.014092f, -0.029263f, 0.064952f, 0.020716f, -0.008260f, -0.041524f, 0.019431f, 0.035326f, 0.028611f, 0.006557f, -0.000926f, 0.011120f, 0.031773f, -0.037738f, 0.030204f, 0.048332f, 0.024286f, -0.004571f, -0.063324f, 0.025978f, -0.003727f, -0.003789f, 0.006005f, -0.027036f, 0.018806f, -0.014412f, 0.021265f, 0.002475f, 0.046636f, 0.011570f, 0.017745f, 0.028519f, -0.013392f, 0.034208f, -0.008917f, -0.038113f, -0.005413f, -0.000551f, 0.023113f, -0.005769f, -0.016479f, 0.020081f, -0.009568f, -0.048961f, 0.032688f, -0.043162f, 0.035471f, -0.031419f, 0.076636f, -0.017743f, -0.031860f, -0.025248f, 0.020741f, 0.009780f, 0.043909f, 0.019286f, -0.036416f, -0.041748f, 0.037254f, 0.025879f, 0.003261f, -0.050082f, -0.030416f, 0.006354f, 0.022122f, -0.033795f, -0.008135f, 0.026009f, -0.025028f, 0.038012f, 0.019606f, 0.011466f, 0.029574f, 0.017599f, -0.038550f, -0.036032f, 0.001309f, 0.001643f, -0.029219f, 0.036574f, -0.004309f, 0.037726f, 0.048682f, -0.004009f, -0.001195f, 0.013700f, -0.008675f, -0.030034f, 0.073559f, 0.012826f, 0.065922f, -0.007249f, 0.021073f, 0.015592f, 0.046689f, 0.003399f, 0.034028f, 0.025386f, -0.054016f, -0.009468f, -0.047228f, 0.007171f, 0.032295f, 0.072038f, -0.013838f, -0.020095f, 0.005218f, -0.024870f, -0.014504f, -0.007312f, -0.000285f, -0.023898f, 0.017025f, -0.020916f, 0.002547f, 0.007976f, 0.007563f, -0.041577f, 0.037773f, 0.012522f, -0.025697f, 0.027426f, 0.018368f, 0.022065f, 0.043167f, -0.023876f, -0.004219f, 0.036252f, -0.067209f})
  };

  @Test
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    try (ArcticEmbedLEncoder encoder = new ArcticEmbedLEncoder()) {
      assertTrue(encoder.getModelPath().endsWith("snowflake-arctic-embed-l-official.onnx"));
      super.testExamples(EXAMPLES, encoder);
    }
  }
}

