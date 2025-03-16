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

public class SpladePlusPlusSelfDistilEncoderInferenceTest extends BaseSparseEncoderInferenceTest {

  private static final SparseExampleOutputPair[] EXAMPLES = {
      new SparseExampleOutputPair("what is paula deen's brother",
          new HashMap<>() {
            {
              put("##e", 2);
              put("##n", 62);
              put("actor", 22);
              put("biography", 3);
              put("brother", 119);
              put("brothers", 101);
              put("carlos", 0);
              put("celebrity", 34);
              put("character", 11);
              put("cousin", 17);
              put("dad", 22);
              put("dee", 115);
              put("family", 23);
              put("father", 2);
              put("friend", 14);
              put("girlfriend", 6);
              put("gun", 4);
              put("harry", 9);
              put("he", 36);
              put("her", 32);
              put("his", 9);
              put("husband", 9);
              put("landon", 9);
              put("lover", 18);
              put("marriage", 6);
              put("partner", 14);
              put("paula", 145);
              put("presley", 18);
              put("relationship", 1);
              put("ruth", 0);
              put("s", 33);
              put("she", 55);
              put("son", 26);
              put("sons", 24);
              put("who", 49);
              put("whom", 27);
              put("wife", 8);
            }}),

      new SparseExampleOutputPair("Androgen receptor define",
          new HashMap<>() {
            {
              put("##rogen", 114);
              put("acronym", 26);
              put("alan", 21);
              put("albert", 11);
              put("alex", 4);
              put("alexander", 4);
              put("alpha", 15);
              put("and", 99);
              put("antigen", 20);
              put("barrett", 6);
              put("beck", 18);
              put("biology", 30);
              put("brain", 12);
              put("brooke", 5);
              put("cell", 18);
              put("chemical", 9);
              put("definition", 56);
              put("drug", 4);
              put("earl", 6);
              put("eli", 6);
              put("emma", 12);
              put("fisher", 8);
              put("gage", 36);
              put("gay", 2);
              put("gene", 32);
              put("gland", 30);
              put("hart", 4);
              put("hc", 2);
              put("henry", 15);
              put("hormone", 88);
              put("ion", 13);
              put("jude", 25);
              put("ken", 7);
              put("lilly", 27);
              put("lily", 15);
              put("lucy", 0);
              put("marcus", 1);
              put("marshall", 5);
              put("medical", 19);
              put("michael", 22);
              put("one", 11);
              put("pill", 38);
              put("plus", 77);
              put("rat", 1);
              put("receptor", 118);
              put("receptors", 102);
              put("rod", 1);
              put("russell", 6);
              put("sensor", 23);
              put("ser", 15);
              put("signal", 19);
              put("spencer", 10);
              put("steven", 3);
              put("stud", 8);
              put("substance", 19);
              put("test", 4);
              put("victor", 3);
            }}),

      new SparseExampleOutputPair("treating tension headaches without medication",
          new HashMap<>() {
            {
              put("##s", 17);
              put("alternative", 2);
              put("anxiety", 13);
              put("avoid", 25);
              put("calming", 5);
              put("cold", 1);
              put("combat", 2);
              put("comfort", 0);
              put("cope", 7);
              put("cure", 28);
              put("depression", 14);
              put("distraction", 27);
              put("dose", 5);
              put("dressing", 24);
              put("drug", 14);
              put("exercise", 9);
              put("fix", 3);
              put("for", 10);
              put("headache", 110);
              put("heal", 24);
              put("help", 13);
              put("injection", 8);
              put("intervention", 10);
              put("med", 28);
              put("medication", 100);
              put("medications", 62);
              put("medicine", 47);
              put("mig", 81);
              put("minimal", 54);
              put("monk", 0);
              put("no", 15);
              put("pain", 36);
              put("pill", 10);
              put("prescription", 1);
              put("prevention", 44);
              put("relief", 17);
              put("relieve", 11);
              put("remedy", 22);
              put("remove", 3);
              put("sans", 8);
              put("solution", 5);
              put("spray", 18);
              put("stress", 11);
              put("supplement", 5);
              put("surgery", 0);
              put("tension", 119);
              put("therapy", 36);
              put("treat", 49);
              put("treating", 17);
              put("treatment", 76);
              put("treatments", 9);
              put("warning", 9);
              put("without", 115);
            }}),

      new SparseExampleOutputPair(
          "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). " +
              "This technological revolution promises to redefine the way we live, work, and interact with the world around us. " +
              "However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. " +
              "This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures.",
          new HashMap<>() {
            {
              put("##ance", 59);
              put("##ances", 7);
              put("##ative", 78);
              put("##bari", 1);
              put("##ef", 70);
              put("##ine", 45);
              put("##ism", 2);
              put("##tech", 16);
              put("20", 12);
              put("2020", 14);
              put("21", 9);
              put("21st", 57);
              put("abraham", 1);
              put("acceleration", 2);
              put("achievement", 4);
              put("achievements", 11);
              put("acronym", 16);
              put("adam", 12);
              put("adaptation", 8);
              put("advance", 12);
              put("advancement", 49);
              put("advances", 17);
              put("affect", 41);
              put("affected", 8);
              put("agenda", 4);
              put("ai", 135);
              put("alec", 0);
              put("alex", 17);
              put("alfred", 6);
              put("algorithm", 19);
              put("alien", 23);
              put("alvin", 3);
              put("analog", 1);
              put("analysis", 9);
              put("ancient", 7);
              put("anderson", 0);
              put("around", 38);
              put("artificial", 104);
              put("aspect", 24);
              put("aspects", 48);
              put("automation", 5);
              put("because", 8);
              put("bias", 15);
              put("brain", 6);
              put("breakthrough", 14);
              put("brink", 93);
              put("century", 66);
              put("change", 11);
              put("chaos", 8);
              put("cia", 17);
              put("civilization", 24);
              put("complex", 31);
              put("complicated", 2);
              put("computer", 13);
              put("computing", 34);
              put("consequence", 9);
              put("consequences", 54);
              put("controversial", 3);
              put("created", 5);
              put("creation", 1);
              put("crisis", 7);
              put("critical", 12);
              put("current", 6);
              put("dawn", 96);
              put("decade", 11);
              put("decisive", 5);
              put("destiny", 5);
              put("development", 8);
              put("did", 13);
              put("digital", 8);
              put("discovery", 0);
              put("disrupt", 17);
              put("edge", 1);
              put("edward", 8);
              put("effect", 23);
              put("effects", 40);
              put("electronic", 6);
              put("eli", 19);
              put("emergence", 24);
              put("emerging", 2);
              put("employed", 15);
              put("employees", 9);
              put("employment", 43);
              put("environmental", 3);
              put("epidemic", 11);
              put("era", 22);
              put("eras", 18);
              put("erratic", 21);
              put("essay", 49);
              put("essays", 48);
              put("ethical", 30);
              put("ethics", 59);
              put("eve", 23);
              put("event", 13);
              put("events", 3);
              put("evidence", 8);
              put("evolution", 20);
              put("explore", 19);
              put("explored", 14);
              put("explores", 6);
              put("future", 16);
              put("generation", 14);
              put("global", 10);
              put("guaranteed", 1);
              put("historic", 11);
              put("historical", 46);
              put("history", 36);
              put("hope", 10);
              put("how", 15);
              put("human", 50);
              put("humanist", 1);
              put("humanity", 68);
              put("humans", 38);
              put("ibm", 26);
              put("image", 8);
              put("imminent", 0);
              put("impact", 48);
              put("impacted", 22);
              put("impacts", 38);
              put("implication", 18);
              put("implications", 65);
              put("important", 22);
              put("improvements", 4);
              put("independence", 5);
              put("industry", 9);
              put("inevitable", 3);
              put("influence", 4);
              put("innovation", 23);
              put("innovations", 22);
              put("instinct", 13);
              put("institute", 19);
              put("intel", 8);
              put("intelligence", 98);
              put("intelligent", 11);
              put("interact", 34);
              put("interaction", 25);
              put("interactions", 37);
              put("interesting", 9);
              put("invented", 27);
              put("invention", 24);
              put("inventions", 0);
              put("iq", 23);
              put("israel", 8);
              put("issue", 3);
              put("job", 12);
              put("jobs", 12);
              put("julian", 19);
              put("led", 8);
              put("legacy", 4);
              put("life", 42);
              put("lifestyle", 8);
              put("live", 28);
              put("lives", 2);
              put("living", 31);
              put("looming", 7);
              put("major", 38);
              put("mans", 8);
              put("modern", 25);
              put("moment", 6);
              put("most", 36);
              put("movement", 3);
              put("nasa", 13);
              put("novel", 6);
              put("now", 0);
              put("nu", 37);
              put("of", 8);
              put("opportunity", 13);
              put("our", 35);
              put("paradigm", 4);
              put("past", 5);
              put("period", 39);
              put("periods", 45);
              put("personal", 8);
              put("phenomenon", 21);
              put("planet", 2);
              put("potential", 52);
              put("predict", 9);
              put("predictable", 72);
              put("privacy", 48);
              put("private", 15);
              put("problem", 1);
              put("progress", 6);
              put("promise", 49);
              put("promised", 41);
              put("promises", 44);
              put("promising", 8);
              put("proposed", 14);
              put("psychology", 6);
              put("radical", 5);
              put("reality", 2);
              put("red", 36);
              put("reform", 6);
              put("res", 19);
              put("rev", 18);
              put("revolution", 72);
              put("revolutionary", 11);
              put("revolutions", 40);
              put("rise", 60);
              put("rising", 35);
              put("robot", 25);
              put("role", 8);
              put("security", 8);
              put("shift", 5);
              put("significance", 9);
              put("significant", 11);
              put("smart", 11);
              put("social", 15);
              put("societal", 31);
              put("societies", 44);
              put("society", 58);
              put("stand", 25);
              put("standing", 8);
              put("stark", 8);
              put("steve", 9);
              put("story", 1);
              put("strategy", 2);
              put("structures", 15);
              put("sunrise", 6);
              put("surrounding", 12);
              put("tech", 48);
              put("technological", 40);
              put("technologies", 22);
              put("technology", 43);
              put("terrorism", 20);
              put("theory", 5);
              put("threat", 27);
              put("threats", 1);
              put("timeline", 7);
              put("today", 11);
              put("transform", 79);
              put("transformation", 57);
              put("transforming", 6);
              put("trend", 18);
              put("turn", 5);
              put("unemployment", 7);
              put("unpredictable", 57);
              put("us", 12);
              put("vision", 5);
              put("war", 5);
              put("wave", 2);
              put("way", 27);
              put("ways", 2);
              put("we", 35);
              put("work", 33);
              put("working", 16);
              put("workplace", 9);
              put("works", 2);
              put("world", 37);
              put("worst", 10);
            }
          })
  };

  @Test
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    super.testExamples(EXAMPLES, new SpladePlusPlusSelfDistilEncoder());
  }
}