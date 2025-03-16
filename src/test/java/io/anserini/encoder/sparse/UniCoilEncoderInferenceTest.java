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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

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

  @Test
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    try(SparseEncoder encoder = new UniCoilEncoder()) {
      super.testExamples(EXAMPLES, encoder);
    }
  }
}