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

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class OnnxEncoderTest {

  private ai.djl.modality.nlp.DefaultVocabulary getVocab(OnnxEncoder<?> encoder) {
    return ((OnnxEncoder<?>) encoder).vocab;
  }

  // Tests the convertTokensToIds method with empty query.
  @Test
  public void testConvertTokensToIdsEmptyQuery() throws Exception {
    try (io.anserini.encoder.dense.BgeBaseEn15Encoder encoder = new io.anserini.encoder.dense.BgeBaseEn15Encoder()) {
      String query = "";
      List<String> queryTokens = new ArrayList<>();
      queryTokens.add("[CLS]");
      queryTokens.addAll(encoder.tokenizer.tokenize(query));
      queryTokens.add("[SEP]");
      long[] result = ((OnnxEncoder<?>) encoder).convertTokensToIds(queryTokens, 512);
      assertTrue(result.length == 2);
      assertEquals(getVocab(encoder).getIndex("[CLS]"), result[0]);
      assertEquals(getVocab(encoder).getIndex("[SEP]"), result[result.length - 1]);
    }
  }

  // Tests the convertTokensToIds method with normal term query.
  @Test
  public void testConvertTokensToIdsNormalQuery() throws Exception {
    try (io.anserini.encoder.dense.BgeBaseEn15Encoder encoder = new io.anserini.encoder.dense.BgeBaseEn15Encoder()) {
      String query = "What is the capital of France?";
      String INSTRUCTION = "Represent this sentence for searching relevant passages: ";
      List<String> queryTokens = new ArrayList<>();
      queryTokens.add("[CLS]");
      queryTokens.addAll(encoder.tokenizer.tokenize(INSTRUCTION + query));
      queryTokens.add("[SEP]");
      long[] result = ((OnnxEncoder<?>) encoder).convertTokensToIds(queryTokens, 512);
      assertEquals(17, result.length);
      assertEquals(getVocab(encoder).getIndex("[CLS]"), result[0]);
      assertEquals(getVocab(encoder).getIndex("[SEP]"), result[result.length - 1]);
    }
  }

  // Tests the convertTokensToIds method with 512 term query.
  @Test
  public void testConvertTokensToIds512Terms() throws Exception {
    try (io.anserini.encoder.dense.BgeBaseEn15Encoder encoder = new io.anserini.encoder.dense.BgeBaseEn15Encoder()) {
      String testQuery = String.join(" ", Collections.nCopies(510, "hello"));
      List<String> queryTokens = new ArrayList<>();
      queryTokens.add("[CLS]");
      queryTokens.addAll(encoder.tokenizer.tokenize(testQuery));
      queryTokens.add("[SEP]");
      long[] result = ((OnnxEncoder<?>) encoder).convertTokensToIds(queryTokens, 512);
      assertEquals(getVocab(encoder).getIndex("[CLS]"), result[0]);
      assertEquals(getVocab(encoder).getIndex("[SEP]"), result[result.length - 1]);
    }
  }

  // Tests the convertTokensToIds method with 513 term query.
  @Test
  public void testConvertTokensToIds513Terms() throws Exception {
    try (io.anserini.encoder.dense.BgeBaseEn15Encoder encoder = new io.anserini.encoder.dense.BgeBaseEn15Encoder()) {
      String testQuery = String.join(" ", Collections.nCopies(511, "hello"));
      List<String> queryTokens = new ArrayList<>();
      queryTokens.add("[CLS]");
      queryTokens.addAll(encoder.tokenizer.tokenize(testQuery));
      queryTokens.add("[SEP]");
      long[] result = ((OnnxEncoder<?>) encoder).convertTokensToIds(queryTokens, 512);
      assertEquals(512, result.length);
      assertEquals(getVocab(encoder).getIndex("[CLS]"), result[0]);
      assertEquals(getVocab(encoder).getIndex("[SEP]"), result[result.length - 1]);
    }
  }

  // Tests the convertTokensToIds method with a real long query.
  @Test
  public void testConvertTokensToIdsReal512Query() throws Exception {
    try (io.anserini.encoder.dense.BgeBaseEn15Encoder encoder = new io.anserini.encoder.dense.BgeBaseEn15Encoder()) {
      String INSTRUCTION = "Represent this sentence for searching relevant passages: ";
      String testQuery = "Query: Collisions are dangerous and lead to injury.  Ray Fosse and Buster Posey (mentioned above in the Introduction) are just two examples of players who suffered major injuries in crashes at home plate. " +
          "Texas Rangers star Josh Hamilton, reigning Most Valuable Player of the American League, broke his arm when he collided with a catcher in 2011. " +
          "In August 2010, Cleveland Indians catcher Carlos Santana suffered a season-ending knee injury when he was hit by Red Sox runner Ryan Kalish. " +
          "To go back a few more seasons, Braves catcher Greg Olson was having a career year in 1992 until Ken Caminiti broke his leg in a collision. " +
          "There have been literally dozens of severe injuries suffered in bang-bang plays at the plate.  This high rate of injury should come as no surprise, given the physics involved in this type of play. " +
          "A simulation with a crash-test dummy wired with sensors showed that a catcher can get hit by a runner travelling 18 miles per hour, resulting in 3,200 pounds of force—much worse than an American football hit, with much less padding. [1]  " +
          "Teams make heavy investments in their players, paying them millions of dollars a year. Thus, serious injuries are very expensive, both because of the treatment required and because the player is missing many games. " +
          "This is why the Oakland Athletics instructed their top catcher, Kurt Suzuki, to avoid blocking the plate—because their investment in him is worth more than whatever runs he allows by failing to stop the runner from scoring. [2]  " +
          "When players are injured in these plays, it's also bad for fans, who will lose the opportunity to see their favourite athletes on the field. " +
          "As Bruce Bochy, Busty Posey's manager with the Giants, told the media after he lost his star catcher to injury: \"And here's a guy that's very popular in baseball. Fans want to see him play, and now he's out for a while.\" [3]  " +
          "[1] Joel Siegel, Barbara Pinto, and Tahman Bradley, \"Catcher Collision Ignites Baseball Rules Debate,\" ABC News, May 28, 2011,  .  " +
          "[2] Buster Olney, \"Billy Beane issues home plate directive,\" ESPN The Magazine, June 1, 2011,  .  " +
          "[3] Tim Kawakami, \"Bochy on Posey's injury: 'Hopefully the guys are not happy—I'm certainly not happy,'\" MercuryNews.com (Talking Points blog), May 26, 2011,  .";
      List<String> queryTokens = new ArrayList<>();
      queryTokens.add("[CLS]");
      queryTokens.addAll(encoder.tokenizer.tokenize(INSTRUCTION + testQuery));
      queryTokens.add("[SEP]");
      long[] result = ((OnnxEncoder<?>) encoder).convertTokensToIds(queryTokens, 512);
      assertEquals(512, result.length);
      assertEquals(getVocab(encoder).getIndex("[CLS]"), result[0]);
      assertEquals(getVocab(encoder).getIndex("[SEP]"), result[result.length - 1]);
    }
  }
}