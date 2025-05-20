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
      // new SparseExampleOutputPair("what is paula deen's brother",
      //     new HashMap<>() {
      //       {
      //         put("he", 32);
      //         put("she", 34);
      //         put("who", 53);
      //         put("##n", 56);
      //         put("family", 24);
      //         put("father", 29);
      //         put("son", 14);
      //         put("brother", 138);
      //         put("friend", 18);
      //         put("husband", 6);
      //         put("whom", 3);
      //         put("actor", 18);
      //         put("brothers", 113);
      //         put("sons", 7);
      //         put("cousin", 8);
      //         put("celebrity", 4);
      //         put("dee", 107);
      //         put("paula", 138);
      //       }}),

      new SparseExampleOutputPair("Androgen receptor define",
          new HashMap<>() {
            {
              put("##rogen", 109);
              put("abbreviation", 32);
              put("alexander", 1);
              put("and", 94);
              put("barrett", 20);
              put("beck", 24);
              put("biological", 2);
              put("biology", 56);
              put("bp", 0);
              put("brain", 10);
              put("brian", 1);
              put("burke", 4);
              put("buzz", 1);
              put("cell", 4);
              put("chemical", 20);
              put("davis", 21);
              put("definition", 52);
              put("detection", 7);
              put("disorder", 15);
              put("drug", 1);
              put("fisher", 2);
              put("function", 11);
              put("gage", 42);
              put("gene", 50);
              put("gland", 14);
              put("hart", 0);
              put("helen", 11);
              put("henry", 15);
              put("hormone", 93);
              put("hormones", 2);
              put("ian", 16);
              put("ion", 8);
              put("is", 2);
              put("leslie", 4);
              put("lilly", 23);
              put("milan", 3);
              put("plus", 76);
              put("receptor", 114);
              put("receptors", 104);
              put("regulation", 4);
              put("sensor", 6);
              put("sex", 15);
              put("signal", 8);
              put("spencer", 6);
              put("stimulation", 5);
              put("wilson", 13);
            }}),

      // new SparseExampleOutputPair("treating tension headaches without medication",
      //     new HashMap<>() {
      //       {
      //         put("##raine", 1);
      //         put("addiction", 18);
      //         put("alternative", 4);
      //         put("anxiety", 8);
      //         put("avoid", 33);
      //         put("brace", 4);
      //         put("clinic", 0);
      //         put("comfort", 6);
      //         put("counseling", 17);
      //         put("cure", 48);
      //         put("dose", 2);
      //         put("drug", 43);
      //         put("emergency", 2);
      //         put("fix", 14);
      //         put("for", 1);
      //         put("headache", 109);
      //         put("heal", 24);
      //         put("help", 34);
      //         put("lack", 33);
      //         put("massage", 24);
      //         put("med", 24);
      //         put("medical", 0);
      //         put("medication", 91);
      //         put("medicine", 34);
      //         put("mig", 86);
      //         put("no", 17);
      //         put("not", 7);
      //         put("outside", 5);
      //         put("pain", 24);
      //         put("prescribed", 12);
      //         put("prevention", 4);
      //         put("relief", 13);
      //         put("remedy", 51);
      //         put("solution", 5);
      //         put("spray", 17);
      //         put("tense", 46);
      //         put("tension", 104);
      //         put("therapy", 40);
      //         put("treat", 46);
      //         put("treated", 33);
      //         put("treating", 7);
      //         put("treatment", 81);
      //         put("warning", 3);
      //         put("with", 11);
      //         put("withdrawal", 2);
      //         put("without", 120);
      //       }}),

      // new SparseExampleOutputPair(
      //     "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). " +
      //         "This technological revolution promises to redefine the way we live, work, and interact with the world around us. " +
      //         "However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. " +
      //         "This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures.",
      //     new HashMap<>() {
      //       {
      //         put("##ance", 60);
      //         put("##ances", 19);
      //         put("##ative", 74);
      //         put("##bility", 1);
      //         put("##eem", 8);
      //         put("##ef", 76);
      //         put("##ine", 45);
      //         put("##tech", 14);
      //         put("##tron", 3);
      //         put("20", 27);
      //         put("2019", 16);
      //         put("20th", 31);
      //         put("21", 47);
      //         put("21st", 73);
      //         put("acceleration", 5);
      //         put("achievement", 9);
      //         put("achievements", 26);
      //         put("adam", 5);
      //         put("advance", 14);
      //         put("advancement", 58);
      //         put("affect", 23);
      //         put("ai", 143);
      //         put("alex", 3);
      //         put("algorithm", 2);
      //         put("ali", 2);
      //         put("alien", 19);
      //         put("anti", 5);
      //         put("apple", 10);
      //         put("around", 41);
      //         put("artificial", 103);
      //         put("aspect", 21);
      //         put("aspects", 50);
      //         put("assessment", 1);
      //         put("because", 11);
      //         put("benefit", 1);
      //         put("biggest", 23);
      //         put("brain", 2);
      //         put("brandon", 16);
      //         put("breakthrough", 8);
      //         put("brink", 93);
      //         put("centuries", 5);
      //         put("century", 71);
      //         put("cia", 6);
      //         put("complex", 43);
      //         put("complicated", 24);
      //         put("computer", 22);
      //         put("computers", 3);
      //         put("consequence", 27);
      //         put("consequences", 39);
      //         put("controversial", 6);
      //         put("crash", 18);
      //         put("created", 15);
      //         put("critical", 1);
      //         put("current", 13);
      //         put("date", 26);
      //         put("dawn", 95);
      //         put("decade", 11);
      //         put("development", 6);
      //         put("digital", 6);
      //         put("discovery", 13);
      //         put("disrupt", 1);
      //         put("e", 18);
      //         put("early", 22);
      //         put("earth", 12);
      //         put("edge", 29);
      //         put("effect", 29);
      //         put("effects", 37);
      //         put("electronic", 11);
      //         put("emergence", 4);
      //         put("emerging", 2);
      //         put("employed", 8);
      //         put("employee", 9);
      //         put("employment", 47);
      //         put("engineering", 7);
      //         put("era", 25);
      //         put("eras", 17);
      //         put("essay", 58);
      //         put("essays", 36);
      //         put("ethical", 35);
      //         put("ethics", 44);
      //         put("event", 21);
      //         put("events", 2);
      //         put("evolution", 17);
      //         put("expected", 1);
      //         put("explore", 15);
      //         put("extinction", 31);
      //         put("foster", 7);
      //         put("future", 25);
      //         put("fuzzy", 3);
      //         put("generation", 12);
      //         put("global", 16);
      //         put("gm", 11);
      //         put("goal", 5);
      //         put("happiness", 5);
      //         put("harm", 18);
      //         put("helped", 28);
      //         put("helping", 5);
      //         put("historic", 3);
      //         put("historical", 35);
      //         put("history", 54);
      //         put("human", 52);
      //         put("humanity", 58);
      //         put("humans", 34);
      //         put("ibm", 20);
      //         put("image", 7);
      //         put("impact", 54);
      //         put("impacted", 1);
      //         put("impacts", 46);
      //         put("implications", 67);
      //         put("importance", 3);
      //         put("important", 23);
      //         put("improvements", 1);
      //         put("industry", 6);
      //         put("inevitable", 7);
      //         put("innovation", 28);
      //         put("innovations", 23);
      //         put("institute", 1);
      //         put("intelligence", 93);
      //         put("intelligent", 8);
      //         put("interact", 32);
      //         put("interaction", 13);
      //         put("interactions", 27);
      //         put("interesting", 7);
      //         put("invented", 30);
      //         put("invention", 7);
      //         put("iq", 62);
      //         put("israel", 15);
      //         put("job", 6);
      //         put("kyle", 3);
      //         put("life", 48);
      //         put("live", 16);
      //         put("living", 17);
      //         put("major", 33);
      //         put("mankind", 3);
      //         put("mars", 6);
      //         put("modern", 9);
      //         put("most", 50);
      //         put("nu", 47);
      //         put("of", 4);
      //         put("on", 11);
      //         put("our", 28);
      //         put("past", 5);
      //         put("peoples", 7);
      //         put("period", 22);
      //         put("periods", 47);
      //         put("political", 2);
      //         put("possibility", 8);
      //         put("possible", 14);
      //         put("potential", 53);
      //         put("power", 3);
      //         put("predict", 29);
      //         put("predictable", 58);
      //         put("predicted", 6);
      //         put("privacy", 56);
      //         put("progress", 2);
      //         put("promise", 48);
      //         put("promised", 44);
      //         put("promises", 28);
      //         put("promising", 30);
      //         put("proposed", 5);
      //         put("purpose", 6);
      //         put("radical", 5);
      //         put("red", 48);
      //         put("released", 1);
      //         put("rev", 13);
      //         put("revolution", 80);
      //         put("revolutionary", 16);
      //         put("revolutions", 55);
      //         put("rise", 73);
      //         put("rising", 46);
      //         put("robot", 27);
      //         put("robotic", 9);
      //         put("robotics", 12);
      //         put("russia", 8);
      //         put("science", 5);
      //         put("significant", 13);
      //         put("slavery", 14);
      //         put("social", 12);
      //         put("societal", 35);
      //         put("societies", 35);
      //         put("society", 60);
      //         put("stand", 39);
      //         put("standing", 12);
      //         put("started", 19);
      //         put("steve", 4);
      //         put("strategy", 6);
      //         put("structures", 5);
      //         put("success", 22);
      //         put("surrounding", 2);
      //         put("survival", 11);
      //         put("tech", 50);
      //         put("technological", 60);
      //         put("technologies", 21);
      //         put("technology", 29);
      //         put("theory", 3);
      //         put("threat", 29);
      //         put("timeline", 1);
      //         put("today", 4);
      //         put("trans", 4);
      //         put("transform", 90);
      //         put("transformation", 47);
      //         put("turn", 4);
      //         put("unpredictable", 17);
      //         put("us", 16);
      //         put("war", 10);
      //         put("was", 5);
      //         put("way", 27);
      //         put("we", 37);
      //         put("weapon", 3);
      //         put("will", 8);
      //         put("work", 29);
      //         put("working", 19);
      //         put("world", 32);
      //         put("worst", 22);
      //       }
      //     })
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