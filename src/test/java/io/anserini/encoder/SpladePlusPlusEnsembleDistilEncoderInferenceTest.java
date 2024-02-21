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

import ai.onnxruntime.OrtException;
import io.anserini.encoder.sparse.SparseEncoder;
import io.anserini.encoder.sparse.SpladePlusPlusEnsembleDistilEncoder;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SpladePlusPlusEnsembleDistilEncoderInferenceTest extends SpladePlusPlusEncoderInferenceTest {

  static private final String MODEL_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/splade-pp-ed-optimized.onnx";
  static private final String MODEL_NAME = "splade-pp-ed-optimized.onnx";

  static private final Object[][] EXAMPLES = new Object[][] {
      { new long[] { 101, 2029, 18714, 7457, 13853, 3798, 1999, 1996, 2668, 1029, 102 },
          new long[] { 2504, 2668, 3445, 3466, 3623, 3798, 4319, 4962, 5333, 5547, 5783,
              6693, 6845, 9007, 9885, 11460, 12448, 13853, 15799, 17357,
              17663, 18714, 18888, 20752, 22991, 24054, 29610 },
          new float[] { 0.7955833f, 1.6830623f, 1.2895458f, 0.17283697f, 1.8474298f,
              0.47887012f, 0.5696925f, 0.03657739f, 1.0409304f, 0.09523272f,
              0.023714364f, 0.33500367f, 0.030933367f, 0.5053173f,
              0.38656655f, 0.13753895f, 0.029621502f, 2.7265992f, 1.7740643f,
              0.058388956f, 0.5208645f, 2.5654476f, 0.09836552f, 2.0933836f,
              0.22953975f, 0.72711116f, 0.34796622f } },
      { new long[] { 101, 2054, 2024, 1996, 2350, 2576, 4243, 1999, 2307, 3725, 1029, 7276, 2035,
          2008, 6611, 1012, 102 },
          new long[] { 2020, 2024, 2035, 2045, 2093, 2177, 2231, 2283, 2307, 2329, 2343,
              2350, 2364, 2406, 2523, 2536, 2563, 2576, 2602, 2679, 2759,
              2837, 2862, 2866, 2922, 3003, 3032, 3163, 3323, 3479, 3519,
              3537, 3607, 3725, 3789, 3945, 3951, 3970, 4018, 4069, 4127,
              4162, 4243, 4314, 4331, 4646, 4696, 4707, 5347, 5454, 5981,
              6056, 6611, 7072, 7141, 7276, 7515, 9782, 10233, 13815, 15523,
              15984, 21127, 21277 },
          new float[] { 0.14769758f, 0.16949965f, 0.23474836f, 0.10592967f, 0.44418612f,
              0.0772488f, 0.19157234f, 1.5968046f, 1.2841316f, 1.2554955f,
              0.09333664f, 1.3755296f, 0.37944564f, 0.26306355f, 0.12348456f,
              0.13497308f, 0.46576625f, 1.3067871f, 0.20111214f, 0.03347309f,
              0.17012452f, 0.013637469f, 0.36164427f, 1.3408387f, 0.32971704f,
              0.034206152f, 0.051039167f, 0.0485385f, 0.5148035f, 0.61843747f,
              0.17119622f, 0.48878193f, 0.13298064f, 1.1947479f, 0.38021207f,
              0.29290885f, 0.33372706f, 0.010118263f, 0.03673287f,
              0.31103662f, 0.3289621f, 0.6645488f, 1.5809797f, 0.12412179f,
              1.0443845f, 0.03413783f, 0.10446505f, 0.55190694f, 0.23820217f,
              0.23240799f, 0.09202352f, 0.24222925f, 0.93797374f, 0.29282433f,
              0.45334193f, 0.83602285f, 0.4799106f, 0.105660066f, 0.49844545f,
              0.8225963f, 0.34430104f, 0.07948579f, 0.13844188f,
              0.04854645f } },
      { new long[] { 101, 2054, 2828, 1997, 4736, 2515, 8611, 2227, 1999, 1051, 1010, 2888, 1996,
          5592, 1997, 1996, 23848, 2072, 102 },
          new long[] { 1051, 1996, 1997, 1998, 1999, 2014, 2016, 2056, 2072, 2143, 2162,
              2217, 2227, 2359, 2377, 2442, 2466, 2645, 2828, 2839, 2888,
              2954, 3276, 4028, 4320, 4323, 4706, 4736, 4963, 5344, 5592,
              6157, 6907, 6965, 8611, 9604, 9755, 14225, 16006, 23848 },
          new float[] { 0.7404208f, 0.40673906f, 0.41499114f, 0.044480685f, 0.2184895f,
              0.055102326f, 0.5336643f, 0.2649181f, 0.27262175f, 0.09129781f,
              0.16264816f, 0.29880422f, 1.2537857f, 0.0134364795f,
              0.060805354f, 0.51008195f, 0.68728083f, 0.40286416f,
              0.74806935f, 0.9838408f, 1.528713f, 0.4006751f, 0.5933437f,
              0.45380116f, 0.13934335f, 0.40032873f, 0.23415788f, 1.7422137f,
              0.08391227f, 0.077517785f, 1.3425684f, 0.0880909f, 0.37026376f,
              0.5701287f, 2.335375f, 0.7543898f, 1.3447703f, 0.20946254f,
              0.03607917f, 1.5865924f } },
      { new long[] { 101, 9375, 1024, 20248, 2078, 102 }, new long[] { 1037, 1043, 2003, 2078, 2577,
          2600, 2643, 2671, 2962, 2969, 2974, 3267, 3330, 3533, 3574, 3660, 3956, 3958,
          4079, 4172, 4205, 4482, 4775, 4913, 5074, 5146, 5207, 5253, 5529, 5624, 5708,
          5726, 5965, 6119, 6127, 6156, 6210, 6330, 6384, 6425, 6426, 6647, 6769, 6969,
          7010, 7150, 7366, 7441, 7530, 7733, 7779, 8475, 8709, 8785, 9036, 9183, 9274,
          9553, 9843, 9894, 10012, 10267, 10505, 10923, 11046, 11902, 12062, 12799, 13404,
          17514, 20248, 22498 },
          new float[] { 0.020789338f, 0.086227484f, 0.16900828f, 2.6266768f, 0.13668232f,
              0.075457804f, 0.3001061f, 0.5761215f, 0.4010797f, 0.4179172f,
              0.27982244f, 0.31147987f, 0.100691f, 0.15033147f, 0.7773396f,
              0.033296112f, 0.14087088f, 0.049636032f, 0.16929466f,
              0.23112203f, 0.24504773f, 0.350704f, 0.5284954f, 0.08418079f,
              0.09223975f, 0.017717898f, 0.38285294f, 0.063870594f,
              0.19538477f, 0.021579351f, 0.2012375f, 0.5946761f, 0.20478511f,
              0.10862623f, 0.29174587f, 0.14790128f, 1.2746162f, 0.47783032f,
              0.0015136463f, 0.21308789f, 0.07221613f, 0.010703417f,
              0.62254757f, 0.18786988f, 0.026747737f, 0.0074720667f,
              0.87107444f, 0.03483909f, 0.055796593f, 0.39843732f,
              0.32331735f, 0.18513167f, 0.24772924f, 0.45369202f, 0.438072f,
              0.06990133f, 0.30506533f, 0.21396045f, 0.45597702f, 0.2835654f,
              0.18362549f, 0.2847969f, 1.1077826f, 0.004802597f, 0.34058252f,
              0.05172494f, 0.33340135f, 0.24490096f, 0.75574744f, 0.1281388f,
              3.1922574f, 0.50601137f } },
      { new long[] { 101, 2043, 2024, 1996, 2176, 2749, 2008, 2552, 2006, 2019, 13297, 1999, 14442,
          1029, 102 },
          new long[] { 1018, 1999, 2006, 2043, 2097, 2176, 2486, 2552, 2558, 2597, 2749,
              2754, 2895, 2929, 2948, 3058, 3177, 3257, 3462, 3466, 3612,
              3778, 3909, 4019, 4195, 4254, 4400, 4405, 4668, 4742, 4894,
              4946, 5584, 5703, 5823, 5876, 7337, 7504, 7551, 8123, 8372,
              8582, 8992, 9963, 10763, 13297, 13428, 13561, 14442, 14446,
              15699, 16264, 18355, 18440, 19744 },
          new float[] { 1.2173709f, 0.024255317f, 0.29948083f, 0.7706239f, 0.066343635f,
              1.8404659f, 1.5258001f, 1.3097733f, 0.20956685f, 0.30950302f,
              1.9214535f, 0.030493725f, 0.12871124f, 0.5071046f, 1.1631463f,
              1.0646278f, 0.17271982f, 0.34310877f, 0.84579825f, 0.11880887f,
              0.13473693f, 0.18046707f, 0.2629173f, 0.25861228f, 0.08944735f,
              0.04224642f, 0.2752902f, 0.60962415f, 0.13806875f, 0.19720376f,
              0.17015187f, 1.2812724f, 0.16543852f, 1.0472775f, 0.6125703f,
              0.31289068f, 0.23293461f, 0.090607084f, 0.08654644f, 0.2119892f,
              0.13188535f, 1.445544f, 0.65577286f, 0.16424927f, 0.4222374f,
              1.1019951f, 0.39814636f, 0.25714558f, 2.1812627f, 0.25580817f,
              0.026262011f, 0.12745133f, 0.01295707f, 0.19258021f,
              0.42181653f } },
      { new long[] { 101, 2129, 2146, 2024, 2057, 9530, 15900, 6313, 2044, 2057, 4608, 1037, 3147,
          1012, 1029, 102 },
          new long[] { 2017, 2044, 2051, 2057, 2065, 2086, 2131, 2146, 2161, 2166, 2320,
              2558, 2574, 2733, 2754, 2994, 3147, 3236, 3413, 3467, 3659,
              4608, 5776, 6313, 7355, 7691, 8985, 9016, 9530, 11005, 13004,
              15403, 15900, 17404, 19340, 19857 },
          new float[] { 0.17626774f, 1.1717949f, 0.6671376f, 0.32015017f, 0.030807031f,
              1.1661142f, 0.22229384f, 2.1373727f, 0.06375182f, 0.108807154f,
              0.0004567057f, 0.95257604f, 0.1515848f, 0.028029643f,
              0.31305963f, 0.15716438f, 1.8053889f, 0.2937335f, 0.022223715f,
              0.31371045f, 0.82218015f, 1.0034947f, 0.0706095f, 1.108423f,
              0.12933268f, 0.5750698f, 0.63685495f, 0.11042738f, 1.0530826f,
              0.4276444f, 0.3927912f, 0.39421225f, 1.5852892f, 0.121254526f,
              0.7007512f, 0.35716093f } },
      { new long[] { 101, 1996, 7450, 2008, 21312, 1996, 13474, 2038, 1996, 2157, 2000, 2019, 4905,
          2003, 1996, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035,
          1035, 7450, 1012, 102 },
          new long[] { 1000, 1035, 2000, 2019, 2031, 2157, 2375, 2457, 2610, 2916, 3423,
              3425, 3648, 3827, 3860, 3979, 4905, 5160, 5416, 5496, 5676,
              6254, 6531, 6543, 7450, 7816, 9870, 11075, 11302, 13474, 14306,
              15359, 15403, 16214, 16362, 20454 },
          new float[] { 0.24155189f, 0.8787399f, 0.55347806f, 0.28090963f, 0.1575947f,
              0.9651886f, 0.5808611f, 0.008838933f, 0.0916666f, 1.1471356f,
              0.6166421f, 0.08061039f, 0.58263975f, 0.20274553f, 0.23114198f,
              0.41225404f, 2.065178f, 1.486106f, 0.18748145f, 0.27171862f,
              1.0481129f, 0.19139485f, 0.52559435f, 0.5120809f, 2.3180618f,
              0.4157903f, 0.56877136f, 0.42205873f, 0.84510523f, 1.5393679f,
              0.7722863f, 0.0955318f, 0.027158633f, 0.8649349f, 1.0209972f,
              0.12163016f } },
      { new long[] { 101, 2054, 2001, 11534, 1005, 1055, 4602, 6691, 2000, 12761, 3399, 1029, 102 },
          new long[] { 2001, 2002, 2010, 2042, 2056, 2087, 2307, 2318, 2338, 2350, 2364,
              2370, 2470, 2590, 2798, 2817, 3044, 3112, 3271, 3278, 3399,
              3603, 3755, 3818, 3910, 4045, 4205, 4602, 5105, 5201, 5221,
              5456, 5857, 5875, 6622, 6691, 6801, 7084, 7155, 7156, 7403,
              7551, 8106, 8144, 8826, 9002, 9667, 11046, 11534, 12761 },
          new float[] { 0.8389583f, 0.11172191f, 0.550236f, 0.031661473f, 0.32405102f,
              0.42506588f, 0.0017889224f, 0.16259679f, 0.07521137f,
              0.0360257f, 0.40673906f, 0.11693868f, 0.1098166f, 0.26455966f,
              0.02741047f, 0.15234789f, 0.056823578f, 0.021588918f,
              0.3671742f, 0.76061124f, 1.1409152f, 0.49590045f, 0.1675643f,
              0.03891848f, 0.5103464f, 0.22035818f, 0.051648982f, 1.7097777f,
              0.32602373f, 1.1873171f, 1.2933531f, 0.48717725f, 0.82523644f,
              0.18502508f, 1.5867937f, 1.9396286f, 0.09652886f, 0.012634943f,
              0.1788058f, 0.07448122f, 0.21466503f, 0.49183512f, 0.9057477f,
              0.10847982f, 0.7362495f, 0.46277112f, 0.42374682f, 0.11414654f,
              2.7350166f, 1.7402309f } },
      { new long[] { 101, 2019, 5983, 8761, 2003, 7356, 2011, 1035, 1035, 1035, 1035, 1035, 1012,
          102 },
          new long[] { 1035, 2011, 2019, 2108, 2124, 2367, 2649, 2711, 2833, 2957, 2966,
              3291, 3303, 3571, 3605, 4205, 4225, 4295, 4340, 4521, 4696,
              4963, 5177, 5182, 5248, 5845, 5983, 6180, 6330, 6754, 7150,
              7355, 7356, 7664, 7870, 8281, 8738, 8761, 9012, 9895, 10000,
              10089, 10786, 10840, 12712, 13449, 13899, 14266, 18224, 18888,
              19470, 24552 },
          new float[] { 1.0285411f, 0.90925366f, 0.2738699f, 0.016006544f, 0.7345002f,
              0.028891094f, 1.4609993f, 0.001113629f, 0.6116325f, 0.18104564f,
              0.16142921f, 0.19465119f, 0.5779254f, 0.15054694f, 1.1459386f,
              0.02806743f, 1.106488f, 0.53266853f, 1.4385542f, 1.9874896f,
              0.30492115f, 0.44486946f, 0.54460955f, 0.20955418f, 0.11552399f,
              0.664972f, 2.5249176f, 0.006653418f, 0.011459717f, 0.111965366f,
              0.0093409885f, 0.48631912f, 2.7532082f, 0.047228962f,
              0.18865791f, 1.716179f, 0.26227555f, 1.814656f, 0.82145154f,
              0.35474685f, 0.044287965f, 0.4293066f, 0.12792756f, 1.733067f,
              0.19900683f, 0.19109683f, 0.39726734f, 0.17623577f, 0.5166739f,
              0.21554603f, 0.090716936f, 0.31856054f } },
      { new long[] { 101, 2054, 8915, 8737, 2079, 2017, 5660, 15960, 24494, 2015, 2006, 1999, 1996,
          17428, 1029, 1998, 2005, 2129, 2146, 1029, 102 },
          new long[] { 1999, 2006, 2146, 3014, 3684, 4860, 5660, 6243, 8434, 8670, 8915,
              10369, 12984, 14744, 15960, 16247, 16716, 17428, 17974, 18302,
              18651, 18740, 24494, 25043 },
          new float[] { 0.124326564f, 0.5262765f, 0.92574155f, 0.78734446f, 0.8214859f,
              1.3936423f, 1.4402342f, 0.55416036f, 0.37437603f, 0.93983847f,
              1.1362213f, 1.0401926f, 0.055462487f, 0.13190645f, 1.8458554f,
              0.25235546f, 0.45627415f, 1.3504475f, 0.18828492f, 0.16853502f,
              0.19503458f, 0.09277542f, 1.914891f, 0.0031683268f } },
      { new long[] { 101, 2029, 18672, 8844, 26450, 6740, 16896, 2006, 1996, 4942, 15782, 14289, 8017,
          1042, 21842, 1997, 1996, 8040, 9331, 7068, 1998, 19274, 2015, 2006, 1996, 8276,
          7270, 21769, 1997, 1996, 20368, 7946, 1029, 102 },
          new long[] { 1042, 1996, 2006, 2112, 2181, 2284, 2597, 2872, 2906, 3244, 3252,
              3295, 4544, 4574, 4761, 4942, 5098, 5923, 5970, 6028, 6462,
              6466, 6650, 6740, 7068, 7270, 7709, 7940, 7946, 8017, 8040,
              8276, 8844, 9331, 10764, 11192, 11457, 12818, 12889, 13656,
              15782, 16749, 18672, 19085, 19274, 20368, 21754, 21769, 21842,
              23851, 25641, 26450, 27159, 27245 },
          new float[] { 0.2064935f, 0.2274995f, 0.14492846f, 0.12306682f, 0.22290778f,
              0.4029314f, 0.9576951f, 0.1466466f, 0.12397226f, 0.86204875f,
              0.09439642f, 0.34498304f, 0.35121176f, 0.485138f, 0.94655895f,
              0.94441867f, 0.21513493f, 0.36443436f, 0.029959261f,
              0.06648597f, 0.06872166f, 0.29403588f, 0.91531867f, 1.3573264f,
              0.44397908f, 0.7961949f, 0.006857551f, 0.9054035f, 0.6489772f,
              0.6140058f, 0.59923446f, 0.549771f, 0.6660374f, 0.7933998f,
              0.19412619f, 0.13392304f, 0.029118529f, 0.7014239f, 0.93853056f,
              0.054955762f, 0.08857642f, 0.10813644f, 1.1521642f, 0.1924095f,
              0.4375875f, 1.010762f, 0.19736788f, 0.3473662f, 0.7764083f,
              0.56429875f, 0.028293198f, 1.2583573f, 0.27887684f,
              0.060074817f } },
  };

  static private final Object[][] LONG_EXAMPLES = new Object[][] {
      { new String[] {
          "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). "
              +
              "This technological revolution promises to redefine the way we live, work, and interact with the world around us. "
              +
              "However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. "
              +
              "This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures." },
          new HashMap<String, Integer>() {
            {
              put("achievements", 26);
              put("promised", 44);
              put("brink", 93);
              put("advance", 14);
              put("crash", 18);
              put("robot", 27);
              put("revolutions", 55);
              put("predictable", 58);
              put("complex", 43);
              put("##ances", 19);
              put("because", 11);
              put("societies", 35);
              put("promises", 28);
              put("released", 1);
              put("events", 2);
              put("invented", 30);
              put("inevitable", 7);
              put("mankind", 3);
              put("past", 5);
              put("expected", 1);
              put("impact", 54);
              put("ai", 143);
              put("benefit", 1);
              put("innovations", 23);
              put("alex", 3);
              put("advancement", 58);
              put("science", 5);
              put("institute", 1);
              put("dawn", 95);
              put("20", 27);
              put("21", 47);
              put("trans", 4);
              put("affect", 23);
              put("robotics", 12);
              put("industry", 6);
              put("global", 16);
              put("turn", 4);
              put("current", 13);
              put("extinction", 31);
              put("aspect", 21);
              put("computers", 3);
              put("emerging", 2);
              put("humanity", 58);
              put("innovation", 28);
              put("impacted", 1);
              put("e", 18);
              put("emergence", 4);
              put("structures", 5);
              put("started", 19);
              put("possible", 14);
              put("standing", 12);
              put("ibm", 20);
              put("complicated", 24);
              put("progress", 2);
              put("timeline", 1);
              put("##ance", 60);
              put("technological", 60);
              put("##ine", 45);
              put("historical", 35);
              put("promise", 48);
              put("ethics", 44);
              put("robotic", 9);
              put("red", 48);
              put("assessment", 1);
              put("acceleration", 5);
              put("##eem", 8);
              put("helped", 28);
              put("us", 16);
              put("predicted", 6);
              put("tech", 50);
              put("peoples", 7);
              put("image", 7);
              put("rev", 13);
              put("unpredictable", 17);
              put("significant", 13);
              put("engineering", 7);
              put("technologies", 21);
              put("disrupt", 1);
              put("success", 22);
              put("worst", 22);
              put("cia", 6);
              put("harm", 18);
              put("breakthrough", 8);
              put("importance", 3);
              put("aspects", 50);
              put("privacy", 56);
              put("we", 37);
              put("life", 48);
              put("weapon", 3);
              put("apple", 10);
              put("society", 60);
              put("happiness", 5);
              put("human", 52);
              put("early", 22);
              put("live", 16);
              put("digital", 6);
              put("##tron", 3);
              put("living", 17);
              put("historic", 3);
              put("mars", 6);
              put("gm", 11);
              put("was", 5);
              put("war", 10);
              put("technology", 29);
              put("evolution", 17);
              put("way", 27);
              put("2019", 16);
              put("artificial", 103);
              put("israel", 15);
              put("21st", 73);
              put("rise", 73);
              put("humans", 34);
              put("decade", 11);
              put("achievement", 9);
              put("helping", 5);
              put("revolution", 80);
              put("##ef", 76);
              put("russia", 8);
              put("slavery", 14);
              put("controversial", 6);
              put("algorithm", 2);
              put("promising", 30);
              put("surrounding", 2);
              put("generation", 12);
              put("period", 22);
              put("goal", 5);
              put("20th", 31);
              put("work", 29);
              put("iq", 62);
              put("impacts", 46);
              put("##tech", 14);
              put("intelligence", 93);
              put("revolutionary", 16);
              put("societal", 35);
              put("theory", 3);
              put("our", 28);
              put("alien", 19);
              put("anti", 5);
              put("computer", 22);
              put("world", 32);
              put("modern", 9);
              put("era", 25);
              put("kyle", 3);
              put("electronic", 11);
              put("steve", 4);
              put("periods", 47);
              put("power", 3);
              put("event", 21);
              put("stand", 39);
              put("consequence", 27);
              put("implications", 67);
              put("explore", 15);
              put("brain", 2);
              put("transformation", 47);
              put("ethical", 35);
              put("century", 71);
              put("essays", 36);
              put("future", 25);
              put("discovery", 13);
              put("foster", 7);
              put("improvements", 1);
              put("date", 26);
              put("political", 2);
              put("employee", 9);
              put("employed", 8);
              put("##bility", 1);
              put("edge", 29);
              put("brandon", 16);
              put("major", 33);
              put("biggest", 23);
              put("consequences", 39);
              put("potential", 53);
              put("ali", 2);
              put("centuries", 5);
              put("development", 6);
              put("adam", 5);
              put("##ative", 74);
              put("created", 15);
              put("survival", 11);
              put("history", 54);
              put("most", 50);
              put("important", 23);
              put("eras", 17);
              put("effects", 37);
              put("proposed", 5);
              put("effect", 29);
              put("essay", 58);
              put("job", 6);
              put("rising", 46);
              put("critical", 1);
              put("purpose", 6);
              put("nu", 47);
              put("interact", 32);
              put("around", 41);
              put("interactions", 27);
              put("transform", 90);
              put("of", 4);
              put("today", 4);
              put("working", 19);
              put("predict", 29);
              put("possibility", 8);
              put("fuzzy", 3);
              put("on", 11);
              put("radical", 5);
              put("will", 8);
              put("social", 12);
              put("interesting", 7);
              put("employment", 47);
              put("intelligent", 8);
              put("earth", 12);
              put("interaction", 13);
              put("threat", 29);
              put("strategy", 6);
              put("invention", 7);

            }
          }
      }
  };

  public SpladePlusPlusEnsembleDistilEncoderInferenceTest() {
    super(MODEL_NAME, MODEL_URL, EXAMPLES, LONG_EXAMPLES);
  }

  @Test
  public void basic() throws OrtException, IOException {
    super.basicTest();
  }

  @SuppressWarnings("unchecked")
  @Test
  public void maxlen() throws OrtException, IOException {
    SparseEncoder encoder = null;
    String[] inputStrings = (String[]) longExamples[0][0];
    Map<String, Integer> expectedMap = (Map<String, Integer>) longExamples[0][1];

    try {
      encoder = new SpladePlusPlusEnsembleDistilEncoder();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    try {
      Map<String, Integer> outputs = encoder.getEncodedQueryMap(inputStrings[0]);
      for (Map.Entry<String, Integer> entry : outputs.entrySet()) {
        String key = entry.getKey();
        Integer value = entry.getValue();
        Integer expectedValue = expectedMap.get(key);
        assertEquals(expectedValue, value);
      }
    } catch (OrtException e) {
      throw new OrtException("Error in encoding: " + e.getMessage());
    }
  }
}