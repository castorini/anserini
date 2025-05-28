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

package io.anserini.encoder.sparse;

import ai.onnxruntime.OrtException;
import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;

public class SpladeV3EncoderTest extends BaseSparseEncoderInferenceTest {

  private static final SparseExampleOutputPair[] EXAMPLES = {
      new SparseExampleOutputPair("what is paula deen's brother",
          new HashMap<>() {{
            put("he", 32);
            put("she", 34);
            put("who", 53);
            put("##n", 56);
            put("family", 24);
            put("father", 29);
            put("son", 14);
            put("brother", 138);
            put("friend", 18);
            put("husband", 6);
            put("whom", 3);
            put("actor", 18);
            put("brothers", 113);
            put("sons", 7);
            put("cousin", 8);
            put("celebrity", 4);
            put("dee", 107);
            put("paula", 138);
          }}),

      new SparseExampleOutputPair("Androgen receptor define",
          new HashMap<>() {{
            put("and", 108);
            put("is", 25);
            put("##r", 22);
            put("alex", 16);
            put("plus", 86);
            put("gene", 3);
            put("alan", 28);
            put("emma", 4);
            put("definition", 98);
            put("lily", 30);
            put("biology", 42);
            put("pedro", 8);
            put("enzyme", 7);
            put("substance", 21);
            put("gage", 7);
            put("receptor", 123);
            put("receptors", 106);
            put("ser", 27);
            put("ant", 1);
            put("pill", 27);
            put("hormone", 83);
            put("##rogen", 98);
            put("gland", 8);
          }}),

      new SparseExampleOutputPair("treating tension headaches without medication",
          new HashMap<>() {{
            put("no", 9);
            put("without", 129);
            put("pain", 16);
            put("step", 37);
            put("treatment", 72);
            put("medicine", 13);
            put("relief", 13);
            put("avoid", 1);
            put("stress", 5);
            put("tension", 123);
            put("comfort", 14);
            put("therapy", 22);
            put("treat", 39);
            put("cure", 16);
            put("prevention", 53);
            put("anxiety", 3);
            put("heal", 23);
            put("treatments", 10);
            put("medication", 65);
            put("headache", 98);
            put("mig", 62);
            put("med", 3);
            put("medications", 24);
          }}),

      new SparseExampleOutputPair(
          "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). " +
              "This technological revolution promises to redefine the way we live, work, and interact with the world around us. " +
              "However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. " +
              "This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures.",
          new HashMap<>() {{
            put("e", 31);
            put("on", 0);
            put("we", 35);
            put("##i", 25);
            put("now", 16);
            put("most", 34);
            put("world", 28);
            put("will", 2);
            put("around", 32);
            put("did", 12);
            put("re", 14);
            put("because", 22);
            put("work", 30);
            put("life", 55);
            put("however", 8);
            put("our", 42);
            put("next", 6);
            put("age", 4);
            put("century", 82);
            put("20", 31);
            put("major", 54);
            put("history", 33);
            put("ever", 11);
            put("red", 40);
            put("live", 32);
            put("development", 18);
            put("story", 1);
            put("human", 74);
            put("role", 18);
            put("various", 10);
            put("21", 45);
            put("living", 38);
            put("working", 9);
            put("society", 87);
            put("period", 50);
            put("important", 40);
            put("social", 13);
            put("information", 5);
            put("moment", 0);
            put("today", 16);
            put("science", 0);
            put("race", 1);
            put("change", 4);
            put("person", 2);
            put("modern", 43);
            put("event", 1);
            put("currently", 27);
            put("institute", 16);
            put("possible", 29);
            put("changed", 4);
            put("future", 50);
            put("technology", 68);
            put("earth", 19);
            put("date", 28);
            put("industry", 23);
            put("job", 11);
            put("goal", 12);
            put("personal", 23);
            put("##ine", 29);
            put("culture", 7);
            put("stand", 20);
            put("hope", 3);
            put("lives", 23);
            put("computer", 27);
            put("significant", 13);
            put("issues", 3);
            put("edge", 3);
            put("complex", 45);
            put("historical", 56);
            put("effect", 35);
            put("digital", 2);
            put("extended", 3);
            put("era", 31);
            put("nations", 7);
            put("image", 21);
            put("influence", 5);
            put("global", 18);
            put("proposed", 23);
            put("contemporary", 11);
            put("effects", 50);
            put("advanced", 9);
            put("1970s", 23);
            put("20th", 4);
            put("potential", 71);
            put("alex", 45);
            put("analysis", 23);
            put("rise", 85);
            put("brain", 7);
            put("critical", 2);
            put("adam", 20);
            put("generation", 46);
            put("impact", 70);
            put("humans", 57);
            put("creation", 3);
            put("revolution", 96);
            put("independence", 12);
            put("interview", 14);
            put("eric", 17);
            put("intelligence", 113);
            put("environmental", 10);
            put("opportunity", 14);
            put("reality", 7);
            put("centuries", 40);
            put("dangerous", 10);
            put("rising", 68);
            put("electronic", 1);
            put("tomorrow", 28);
            put("employed", 37);
            put("promise", 61);
            put("threat", 37);
            put("progress", 12);
            put("advance", 16);
            put("structures", 32);
            put("employees", 28);
            put("anderson", 24);
            put("mental", 17);
            put("importance", 6);
            put("aware", 2);
            put("reform", 20);
            put("allen", 19);
            put("crisis", 17);
            put("affected", 2);
            put("danger", 4);
            put("decade", 19);
            put("strategy", 13);
            put("promised", 61);
            put("benefit", 7);
            put("jobs", 17);
            put("interesting", 6);
            put("aspects", 64);
            put("smart", 51);
            put("possibility", 24);
            put("employment", 55);
            put("alfred", 7);
            put("apple", 17);
            put("revolutionary", 43);
            put("achievement", 5);
            put("influential", 14);
            put("julian", 20);
            put("dawn", 100);
            put("alpha", 8);
            put("eve", 34);
            put("evolution", 32);
            put("tech", 67);
            put("##ance", 69);
            put("latest", 18);
            put("technologies", 41);
            put("automatic", 4);
            put("ted", 11);
            put("periods", 59);
            put("rev", 27);
            put("alien", 37);
            put("21st", 75);
            put("affect", 62);
            put("radical", 24);
            put("explosion", 0);
            put("significance", 8);
            put("aspect", 28);
            put("topics", 24);
            put("employee", 6);
            put("artificial", 116);
            put("##ative", 78);
            put("innovation", 27);
            put("interaction", 33);
            put("emerging", 11);
            put("societies", 74);
            put("autonomous", 37);
            put("happiness", 9);
            put("humanity", 75);
            put("consequences", 73);
            put("transformed", 7);
            put("transformation", 63);
            put("reforms", 5);
            put("invented", 49);
            put("explore", 11);
            put("malcolm", 1);
            put("essays", 58);
            put("robot", 62);
            put("twentieth", 4);
            put("ni", 13);
            put("nasa", 27);
            put("privacy", 50);
            put("intelligent", 38);
            put("thesis", 1);
            put("essay", 65);
            put("consequence", 34);
            put("phenomenon", 7);
            put("lifestyle", 20);
            put("ethics", 68);
            put("ally", 7);
            put("alec", 2);
            put("computing", 45);
            put("advances", 54);
            put("trend", 18);
            put("algorithm", 43);
            put("ai", 137);
            put("ibm", 31);
            put("promising", 22);
            put("achievements", 9);
            put("terrorism", 10);
            put("millennium", 24);
            put("interactions", 42);
            put("ari", 15);
            put("civilization", 42);
            put("explored", 8);
            put("promises", 61);
            put("technological", 65);
            put("transform", 85);
            put("invention", 31);
            put("embedded", 19);
            put("agenda", 18);
            put("analog", 7);
            put("##borg", 11);
            put("interact", 39);
            put("eli", 30);
            put("guaranteed", 25);
            put("insight", 0);
            put("advancement", 68);
            put("2020", 43);
            put("breakthrough", 22);
            put("instinct", 2);
            put("##ef", 71);
            put("ethical", 47);
            put("implications", 92);
            put("algorithms", 23);
            put("bias", 13);
            put("emergence", 31);
            put("impacts", 58);
            put("mankind", 23);
            put("##tech", 14);
            put("explores", 0);
            put("##vent", 3);
            put("innovations", 31);
            put("predict", 0);
            put("mans", 9);
            put("workplace", 37);
            put("acceleration", 4);
            put("nu", 51);
            put("imminent", 22);
            put("transforming", 3);
            put("vulnerability", 9);
            put("##kind", 14);
            put("impacted", 25);
            put("adaptive", 18);
            put("automation", 22);
            put("regeneration", 12);
            put("acronym", 40);
            put("robotic", 30);
            put("paradigm", 1);
            put("ir", 39);
            put("brink", 104);
            put("techno", 12);
            put("predictable", 84);
            put("unpredictable", 65);
            put("inventions", 19);
            put("transformations", 21);
            put("io", 32);
            put("disrupt", 22);
            put("societal", 47);
            put("looming", 10);
            put("erratic", 17);
            put("humanist", 15);
            put("civilizations", 14);
            put("revolutions", 77);
            put("implication", 59);
            put("iq", 50);
            put("##ances", 27);
            put("ina", 27);
            put("eras", 28);
          }})
  };

  private static final Object[][] TOKENIZATION_EXAMPLE = new Object[][] {
      { "which hormone increases calcium levels in the blood?",
          new long[] { 101, 2029, 18714, 7457, 13853, 3798, 1999, 1996, 2668, 1029, 102 } },

      { "what are the major political parties in great britain? select all that apply.",
          new long[] { 101, 2054, 2024, 1996, 2350, 2576, 4243, 1999, 2307, 3725, 1029, 7276, 2035, 2008, 6611, 1012, 102 } },

      { "what type of conflict does della face in o, henry the gift of the magi",
          new long[] { 101, 2054, 2828, 1997, 4736, 2515, 8611, 2227, 1999, 1051, 1010, 2888, 1996, 5592, 1997, 1996, 23848, 2072, 102 } },

      { "define: geon",
          new long[] { 101, 9375, 1024, 20248, 2078, 102 } },

      { "when are the four forces that act on an airplane in equilibrium?",
          new long[] { 101, 2043, 2024, 1996, 2176, 2749, 2008, 2552, 2006, 2019, 13297, 1999, 14442, 1029, 102 } },

      { "how long are we contagious after we catch a cold.?",
          new long[] { 101, 2129, 2146, 2024, 2057, 9530, 15900, 6313, 2044, 2057, 4608, 1037, 3147, 1012, 1029, 102 } },

      { "the amendment that ensures the defendant has the right to an attorney is the ____________ amendment.",
          new long[] { 101, 1996, 7450, 2008, 21312, 1996, 13474, 2038, 1996, 2157, 2000, 2019, 4905, 2003, 1996, 1035,
              1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 1035, 7450, 1012, 102 } },

      { "what was darwin's greatest contribution to evolutionary theory?",
          new long[] { 101, 2054, 2001, 11534, 1005, 1055, 4602, 6691, 2000, 12761, 3399, 1029, 102 } },

      { "an eating disorder is characterized by _____.",
          new long[] { 101, 2019, 5983, 8761, 2003, 7356, 2011, 1035, 1035, 1035, 1035, 1035, 1012, 102 } },

      { "what temp do you cook pork chops on in the oven? and for how long?",
          new long[] { 101, 2054, 8915, 8737, 2079, 2017, 5660, 15960, 24494, 2015, 2006, 1999, 1996, 17428, 1029, 1998,
              2005, 2129, 2146, 1029, 102 } },

      { "which rotator cuff muscle originates on the subscapular fossa of the scapula and inserts on the lesser tubercle of the humerus?",
          new long[] { 101, 2029, 18672, 8844, 26450, 6740, 16896, 2006, 1996, 4942, 15782, 14289, 8017, 1042, 21842, 1997,
              1996, 8040, 9331, 7068, 1998, 19274, 2015, 2006, 1996, 8276, 7270, 21769, 1997, 1996, 20368, 7946, 1029, 102 } },
  };

  @Test
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    try(SpladeV3Encoder encoder = new SpladeV3Encoder()) {
      assertTrue(encoder.getModelPath().endsWith("splade-v3-optimized.onnx"));
      super.testExamples(EXAMPLES, encoder);
    }
  }

  @Test
  public void testTokenization() throws IOException, URISyntaxException, OrtException {
    try(SpladeV3Encoder encoder = new SpladeV3Encoder()) {
      for (Object[] example : TOKENIZATION_EXAMPLE) {
        String query = (String) example[0];
        long[] expectedTokenIds = (long[]) example[1];

        assertArrayEquals(expectedTokenIds, encoder.tokenizeToIds(query));
      }
    }
  }
}