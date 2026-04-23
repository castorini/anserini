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

public class UniCoilEncoderInferenceTest extends BaseSparseEncoderInferenceTest {

  private static final SparseExampleOutputPair[] EXAMPLES = {
      new SparseExampleOutputPair("what is paula deen's brother",
          new HashMap<>() {
            {
              put("##n", 109);
              put("'", 9);
              put("[SEP]", 0);
              put("brother", 209);
              put("dee", 129);
              put("is", 55);
              put("paula", 166);
              put("s", 30);
              put("what", 46);
            }}),

      new SparseExampleOutputPair("Androgen receptor define",
          new HashMap<>() {
            {
              put("##rogen", 193);
              put("[SEP]", 0);
              put("and", 145);
              put("define", 98);
              put("receptor", 181);
            }}),

      new SparseExampleOutputPair("treating tension headaches without medication",
          new HashMap<>() {
            {
              put("##s", 0);
              put("[SEP]", 0);
              put("headache", 133);
              put("medication", 72);
              put("tension", 148);
              put("treating", 76);
              put("without", 135);
            }}),

      new SparseExampleOutputPair(
          "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). " +
              "This technological revolution promises to redefine the way we live, work, and interact with the world around us. " +
              "However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. " +
              "This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures.",
          new HashMap<>() {
            {
              put("##ance", 48);
              put("##ative", 92);
              put("##d", 36);
              put("##ef", 74);
              put("##ine", 91);
              put("(", 45);
              put(")", 11);
              put(",", 416);
              put(".", 315);
              put("21st", 167);
              put(":", 63);
              put("[SEP]", 16);
              put("advancement", 75);
              put("ai", 593);
              put("and", 104);
              put("any", 55);
              put("are", 41);
              put("around", 66);
              put("artificial", 155);
              put("as", 33);
              put("aspects", 76);
              put("brink", 128);
              put("century", 95);
              put("complex", 54);
              put("dawn", 141);
              put("employment", 38);
              put("entirely", 68);
              put("essay", 115);
              put("ethics", 35);
              put("explores", 70);
              put("for", 57);
              put("history", 75);
              put("however", 85);
              put("human", 113);
              put("humanity", 159);
              put("impacts", 105);
              put("implications", 108);
              put("in", 89);
              put("including", 45);
              put("intelligence", 145);
              put("interact", 89);
              put("life", 61);
              put("live", 80);
              put("major", 57);
              put("most", 108);
              put("not", 56);
              put("nu", 50);
              put("of", 350);
              put("on", 111);
              put("one", 73);
              put("periods", 87);
              put("personal", 43);
              put("potential", 65);
              put("predictable", 74);
              put("privacy", 53);
              put("promises", 102);
              put("red", 93);
              put("revolution", 114);
              put("rise", 125);
              put("societal", 68);
              put("society", 103);
              put("stands", 111);
              put("structures", 42);
              put("technological", 237);
              put("the", 546);
              put("this", 143);
              put("to", 43);
              put("transform", 137);
              put("us", 78);
              put("various", 47);
              put("way", 74);
              put("we", 79);
              put("with", 98);
              put("work", 71);
              put("world", 103);
            }
          })
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
    try(UniCoilEncoder encoder = new UniCoilEncoder()) {
      assertTrue(encoder.getModelPath().endsWith("unicoil.onnx"));
      super.testExamples(EXAMPLES, encoder);
    }
  }

  @Test
  public void testTokenization() throws IOException, URISyntaxException, OrtException {
    try(UniCoilEncoder encoder = new UniCoilEncoder()) {
      for (Object[] example : TOKENIZATION_EXAMPLE) {
        String query = (String) example[0];
        long[] expectedTokenIds = (long[]) example[1];

        assertArrayEquals(expectedTokenIds, encoder.tokenizeToIds(query));
      }
    }
  }
}