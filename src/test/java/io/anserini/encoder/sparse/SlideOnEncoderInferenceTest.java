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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.junit.Test;

import ai.onnxruntime.OrtException;

/**
 * On-the-fly query inference smoke tests, tokenizer regression tests, and
 * synthetic checks of top-64 selection and quantization. No precomputed query
 * representations are required, and raw-vector equivalence with PyTorch is not asserted.
 */
public class SlideOnEncoderInferenceTest {

  private static final String[] EXAMPLE_QUERIES = {
      "what is paula deen's brother",
      "Androgen receptor define",
      "treating tension headaches without medication",
      "In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: "
          + "the rise of artificial intelligence (AI). This technological revolution promises to redefine the way we live, "
          + "work, and interact with the world around us. However, as with any major technological advancement, the "
          + "implications of AI for society are complex, nuanced, and not entirely predictable. This essay explores the "
          + "potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, "
          + "and societal structures."
  };

  private static final Object[][] TOKENIZATION_EXAMPLE = new Object[][] {
      { "which hormone increases calcium levels in the blood?",
          new long[] { 50281, 7267, 27, 534, 14689, 5459, 11672, 2308, 275, 253, 2614, 32, 50282 } },

      { "what are the major political parties in great britain? select all that apply.",
          new long[] { 50281, 7267, 27, 752, 403, 253, 2201, 3569, 4676, 275, 1270, 270, 902, 404, 32, 3609, 512, 326, 4647, 15, 50282 } },

      { "what type of conflict does della face in o, henry the gift of the magi",
          new long[] { 50281, 7267, 27, 752, 1511, 273, 7344, 1057, 14804, 2454, 275, 258, 13, 23394, 610, 253, 9140, 273, 253, 4231, 74, 50282 } },

      { "define: geon",
          new long[] { 50281, 7267, 27, 4853, 27, 3471, 251, 50282 } },

      { "when are the four forces that act on an airplane in equilibrium?",
          new long[] { 50281, 7267, 27, 672, 403, 253, 1740, 5621, 326, 769, 327, 271, 33667, 275, 12902, 32, 50282 } },

      { "how long are we contagious after we catch a cold.?",
          new long[] { 50281, 7267, 27, 849, 1048, 403, 359, 41142, 784, 846, 359, 5834, 247, 5412, 50057, 50282 } },

      { "the amendment that ensures the defendant has the right to an attorney is the ____________ amendment.",
          new long[] { 50281, 7267, 27, 253, 15129, 326, 20096, 253, 3257, 556, 253, 987, 281, 271, 6834, 310, 253, 38834, 15362, 15129, 15, 50282 } },

      { "what was darwin's greatest contribution to evolutionary theory?",
          new long[] { 50281, 7267, 27, 752, 369, 13681, 6481, 434, 6459, 7680, 281, 16483, 3762, 32, 50282 } },

      { "an eating disorder is characterized by _____.",
          new long[] { 50281, 7267, 27, 271, 9123, 8984, 310, 7943, 407, 37866, 15, 50282 } },

      { "what temp do you cook pork chops on in the oven? and for how long?",
          new long[] { 50281, 7267, 27, 752, 14712, 513, 368, 4417, 23914, 448, 2695, 327, 275, 253, 14195, 32, 285, 323, 849, 1048, 32, 50282 } },

      { "which rotator cuff muscle originates on the subscapular fossa of the scapula and inserts on the lesser tubercle of the humerus?",
          new long[] { 50281, 7267, 27, 534, 4000, 1080, 39020, 6616, 42799, 327, 253, 749, 1026, 522, 792, 17122, 66, 273, 253, 660, 522, 3627, 285, 37034, 327, 253, 16277, 6985, 254, 2148, 273, 253, 1547, 254, 316, 32, 50282 } },

  };

  @Test
  public void testEmptyWhitespaceAndNullQueries() throws Exception {
    try (SlideOnEncoder encoder = new SlideOnEncoder()) {
      assertArrayEquals(new long[] {50281, 7267, 27, 50282}, encoder.tokenizeToIds(""));
      assertArrayEquals(encoder.tokenizeToIds(""), encoder.tokenizeToIds(" \t\n\u00a0\u0085"));
      assertEquals("query", assertThrows(NullPointerException.class, () -> encoder.tokenizeToIds(null)).getMessage());
      assertThrows(NullPointerException.class, () -> encoder.encode(null));
    }
  }

  @Test
  public void testMaxLengthOverrideTruncatesAndKeepsSpecialTokens() throws Exception {
    String property = "anserini.slideon.maxLength";
    String previous = System.getProperty(property);
    try {
      System.setProperty(property, "8");
      try (SlideOnEncoder encoder = new SlideOnEncoder()) {
        long[] ids = encoder.tokenizeToIds("hello world ".repeat(20));
        assertEquals(8, ids.length);
        assertEquals(50281, ids[0]);
        assertEquals(50282, ids[ids.length - 1]);
      }
    } finally {
      restoreProperty(property, previous);
    }
  }

  @Test
  public void testRejectsInvalidConfigurationOverrides() throws Exception {
    String maxLength = "anserini.slideon.maxLength";
    String scale = "anserini.slideon.scale";
    String previousLength = System.getProperty(maxLength);
    String previousScale = System.getProperty(scale);
    try {
      System.clearProperty(scale);
      System.setProperty(maxLength, "1");
      assertTrue(assertThrows(IOException.class, SlideOnEncoder::new).getMessage().contains("maximum length"));
      System.clearProperty(maxLength);
      for (String invalidScale : new String[] {"0", "NaN"}) {
        System.setProperty(scale, invalidScale);
        assertTrue(assertThrows(IOException.class, SlideOnEncoder::new).getMessage().contains("scale"));
      }
    } finally {
      restoreProperty(maxLength, previousLength);
      restoreProperty(scale, previousScale);
    }
  }

  private static void restoreProperty(String name, String value) {
    if (value == null) {
      System.clearProperty(name);
    } else {
      System.setProperty(name, value);
    }
  }

  @Test
  public void testQuantizationUsesFloat32AndTiesToEven() {
    assertEquals(12, SlideOnEncoder.quantizeWeight(.125f, 100));
    assertEquals(38, SlideOnEncoder.quantizeWeight(.375f, 100));
    // Promoting to double before multiplying would incorrectly round this to 3.
    assertEquals(2, SlideOnEncoder.quantizeWeight(.025f, 100));
    assertEquals(0, SlideOnEncoder.quantizeWeight(.005f, 100));
    assertEquals(0, SlideOnEncoder.quantizeWeight(Math.nextDown(.5f), 1));
    assertEquals(1, SlideOnEncoder.quantizeWeight(Math.nextUp(.5f), 1));
    assertEquals(0, SlideOnEncoder.quantizeWeight(0, 100));
    assertEquals(0, SlideOnEncoder.quantizeWeight(-0.0f, 100));
  }

  @Test
  public void testQuantizationRejectsInvalidArguments() {
    for (float weight : new float[] {-1, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
      assertThrows(IllegalArgumentException.class, () -> SlideOnEncoder.quantizeWeight(weight, 100));
    }
    for (float scale : new float[] {0, -1, Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY}) {
      assertThrows(IllegalArgumentException.class, () -> SlideOnEncoder.quantizeWeight(1, scale));
    }
  }

  @Test
  public void testQuantizationIntegerBoundaryAndOverflow() {
    assertEquals(2147483520, SlideOnEncoder.quantizeWeight(Math.nextDown((float) Integer.MAX_VALUE), 1));
    assertTrue(assertThrows(IllegalArgumentException.class,
        () -> SlideOnEncoder.quantizeWeight((float) Integer.MAX_VALUE, 1)).getMessage().contains("Java int range"));
    assertTrue(assertThrows(IllegalArgumentException.class,
        () -> SlideOnEncoder.quantizeWeight(Float.MAX_VALUE, 2)).getMessage().contains("Java int range"));
  }

  @Test
  public void testTop64BeforeQuantizationAndNumericTieBreaking() throws Exception {
    try (SlideOnEncoder encoder = new SlideOnEncoder()) {
      var weights = new LinkedHashMap<String, Float>();
      for (int id = 64; id >= 0; id--) {
        weights.put(Integer.toString(id), .006f);
      }
      // All weights round to 1, but the largest float must survive before ties are broken.
      weights.put("1000", .009f);
      var actual = encoder.quantizeFloatWeights(weights);
      var expected = new HashMap<String, Integer>();
      for (int id = 0; id < 63; id++) {
        expected.put(Integer.toString(id), 1);
      }
      expected.put("1000", 1);
      assertEquals(expected, actual);
      assertEquals(66, weights.size());
      assertTrue(encoder.quantizeFloatWeights(java.util.Map.of("101", .001f)).isEmpty());
    }
  }

  @Test
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    try (SlideOnEncoder encoder = new SlideOnEncoder()) {
      assertTrue(encoder.getModelPath().endsWith("slideon-optimized.onnx"));
      for (String query : EXAMPLE_QUERIES) {
        var vector = encoder.encode(query);
        assertTrue("Empty query representation: " + query, !vector.isEmpty());
        assertTrue("Query exceeds top-64: " + query, vector.size() <= 64);
        vector.forEach((term, weight) -> {
          int latentId = Integer.parseInt(term);
          assertTrue("Invalid latent ID: " + term, latentId >= 0 && latentId < 32768);
          assertTrue("Nonpositive impact: " + term, weight > 0);
        });
      }
    }
  }

  @Test
  public void testTokenization() throws IOException, URISyntaxException, OrtException {
    try (SlideOnEncoder encoder = new SlideOnEncoder()) {
      for (Object[] example : TOKENIZATION_EXAMPLE) {
        String query = (String) example[0];
        long[] expectedTokenIds = (long[]) example[1];
        assertArrayEquals(expectedTokenIds, encoder.tokenizeToIds(query));
      }
    }
  }
}
