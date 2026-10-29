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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.Test;

import ai.onnxruntime.OrtException;

/**
 * Matches the SPLADE inference test structure and example queries.
 * Expected integer vectors and token IDs come from the matching HF tokenizer and
 * Python ONNX Runtime 1.17.0, using slideon-optimized.onnx and scale 100.
 * This regression test does not assert raw-vector equivalence with PyTorch.
 */
public class SlideOnEncoderInferenceTest extends BaseSparseEncoderInferenceTest {

  private static final SparseExampleOutputPair[] EXAMPLES = {
      new SparseExampleOutputPair("what is paula deen's brother",
          new HashMap<>() {{
            put("86", 12);
            put("129", 1);
            put("261", 2);
            put("281", 2);
            put("346", 2);
            put("365", 4);
            put("398", 1);
            put("522", 3);
            put("723", 3);
            put("1411", 1);
            put("1920", 2);
            put("2377", 27);
            put("2524", 1);
            put("2901", 6);
            put("3120", 1);
            put("3329", 2);
            put("3489", 2);
            put("3700", 5);
            put("4119", 1);
            put("4192", 1);
            put("4311", 2);
            put("4386", 2);
            put("4546", 15);
            put("4741", 1);
            put("4898", 2);
            put("5556", 2);
            put("6788", 15);
            put("7051", 1);
            put("7162", 2);
            put("7299", 2);
            put("7584", 1);
            put("7887", 1);
            put("9170", 1);
            put("9827", 2);
            put("10299", 2);
            put("10477", 3);
            put("10535", 6);
            put("10682", 10);
            put("10735", 1);
            put("10928", 4);
            put("11309", 2);
            put("11793", 2);
            put("11900", 1);
            put("12064", 20);
            put("13158", 1);
            put("13453", 2);
            put("13777", 3);
            put("14016", 1);
            put("14210", 1);
            put("14484", 2);
            put("14941", 3);
            put("15081", 1);
            put("15259", 1);
            put("15308", 1);
            put("15341", 2);
            put("15359", 3);
            put("15614", 1);
            put("15960", 4);
            put("15985", 3);
            put("16118", 11);
            put("16211", 2);
            put("16325", 1);
            put("16703", 7);
            put("16846", 3);
            put("16925", 8);
            put("17091", 3);
            put("17212", 6);
            put("17911", 3);
            put("18223", 16);
            put("18236", 9);
            put("18242", 3);
            put("18623", 1);
            put("19006", 2);
            put("19166", 1);
            put("19345", 3);
            put("19382", 3);
            put("19614", 1);
            put("20172", 1);
            put("20387", 3);
            put("20412", 1);
            put("20429", 1);
            put("20597", 5);
            put("20866", 2);
            put("21106", 2);
            put("21262", 3);
            put("21618", 2);
            put("21764", 2);
            put("21864", 1);
            put("22004", 2);
            put("22278", 1);
            put("22347", 12);
            put("23043", 1);
            put("23044", 1);
            put("23413", 5);
            put("23423", 3);
            put("23577", 4);
            put("23883", 2);
            put("24113", 1);
            put("24637", 1);
            put("25187", 12);
            put("25285", 1);
            put("25453", 12);
            put("25827", 2);
            put("25844", 4);
            put("25873", 6);
            put("27000", 1);
            put("27232", 6);
            put("27462", 1);
            put("27534", 1);
            put("27737", 9);
            put("27835", 1);
            put("27843", 5);
            put("28044", 2);
            put("28229", 2);
            put("28805", 1);
            put("30007", 3);
            put("30221", 1);
            put("30237", 1);
            put("30353", 1);
            put("30460", 1);
            put("30544", 1);
            put("30565", 3);
            put("30738", 3);
            put("30840", 1);
            put("31874", 2);
            put("31926", 2);
            put("32039", 4);
            put("32606", 2);
            put("32683", 1);
          }}),

      new SparseExampleOutputPair("Androgen receptor define",
          new HashMap<>() {{
            put("86", 3);
            put("926", 4);
            put("1200", 1);
            put("1315", 1);
            put("1560", 4);
            put("2132", 9);
            put("3120", 2);
            put("3163", 2);
            put("3489", 4);
            put("3561", 3);
            put("3886", 2);
            put("4399", 3);
            put("4634", 4);
            put("4652", 6);
            put("4717", 5);
            put("4741", 2);
            put("4744", 2);
            put("5247", 1);
            put("5289", 4);
            put("5353", 2);
            put("5486", 11);
            put("6518", 19);
            put("7299", 6);
            put("7977", 1);
            put("8388", 4);
            put("8418", 1);
            put("8643", 6);
            put("8646", 3);
            put("8979", 3);
            put("9129", 6);
            put("9418", 5);
            put("9453", 2);
            put("9633", 9);
            put("10297", 3);
            put("10863", 1);
            put("11298", 3);
            put("11793", 2);
            put("11800", 4);
            put("13079", 2);
            put("13245", 2);
            put("13424", 17);
            put("14828", 3);
            put("15182", 3);
            put("15333", 2);
            put("15536", 10);
            put("15752", 4);
            put("15824", 1);
            put("15843", 2);
            put("15985", 7);
            put("18019", 3);
            put("18130", 1);
            put("18142", 3);
            put("18703", 1);
            put("19269", 3);
            put("19412", 4);
            put("19627", 14);
            put("19632", 8);
            put("19715", 3);
            put("20918", 7);
            put("21135", 3);
            put("21165", 1);
            put("21490", 3);
            put("22390", 2);
            put("22565", 24);
            put("22625", 1);
            put("23026", 2);
            put("23043", 1);
            put("23044", 1);
            put("23371", 2);
            put("23438", 1);
            put("24421", 10);
            put("24565", 5);
            put("24715", 2);
            put("25827", 11);
            put("25830", 2);
            put("26491", 1);
            put("27232", 3);
            put("27269", 1);
            put("27500", 10);
            put("27557", 3);
            put("28116", 2);
            put("28198", 1);
            put("28406", 3);
            put("28455", 1);
            put("28818", 2);
            put("28960", 4);
            put("29198", 2);
            put("29966", 2);
            put("30020", 12);
            put("30028", 13);
            put("30237", 1);
            put("30307", 4);
            put("30652", 1);
            put("31420", 7);
            put("32403", 2);
          }}),

      new SparseExampleOutputPair("treating tension headaches without medication",
          new HashMap<>() {{
            put("114", 3);
            put("784", 1);
            put("962", 3);
            put("1471", 2);
            put("1549", 7);
            put("1724", 1);
            put("1754", 3);
            put("1777", 10);
            put("2311", 2);
            put("2429", 25);
            put("2711", 4);
            put("3120", 1);
            put("3168", 3);
            put("3346", 13);
            put("3522", 6);
            put("3830", 5);
            put("4086", 1);
            put("4196", 4);
            put("4634", 1);
            put("5073", 3);
            put("5623", 16);
            put("5727", 1);
            put("5733", 5);
            put("5793", 1);
            put("5832", 4);
            put("5936", 2);
            put("6115", 2);
            put("6828", 1);
            put("7709", 4);
            put("7950", 3);
            put("8254", 4);
            put("8561", 1);
            put("8924", 4);
            put("9418", 12);
            put("9785", 14);
            put("10243", 5);
            put("10913", 10);
            put("11793", 2);
            put("12014", 4);
            put("12662", 3);
            put("12916", 2);
            put("13134", 2);
            put("13291", 2);
            put("14069", 1);
            put("14086", 16);
            put("14821", 2);
            put("15433", 3);
            put("15435", 6);
            put("15705", 1);
            put("15749", 3);
            put("15796", 2);
            put("16340", 2);
            put("16802", 4);
            put("16823", 1);
            put("17099", 4);
            put("17507", 2);
            put("17509", 4);
            put("17715", 2);
            put("17835", 1);
            put("17859", 2);
            put("18260", 1);
            put("18334", 3);
            put("18369", 13);
            put("18392", 1);
            put("19386", 1);
            put("19476", 1);
            put("19754", 3);
            put("20684", 9);
            put("20745", 6);
            put("21259", 3);
            put("21388", 2);
            put("22278", 1);
            put("22286", 1);
            put("22343", 3);
            put("22561", 5);
            put("22585", 5);
            put("22667", 3);
            put("23044", 1);
            put("23481", 5);
            put("23660", 1);
            put("23673", 3);
            put("24416", 1);
            put("24733", 3);
            put("24999", 1);
            put("25076", 3);
            put("25413", 3);
            put("25648", 15);
            put("25796", 1);
            put("26377", 3);
            put("26680", 2);
            put("27138", 1);
            put("27729", 2);
            put("27964", 3);
            put("29081", 1);
            put("29414", 5);
            put("29466", 1);
            put("30237", 3);
            put("30731", 8);
            put("30753", 1);
            put("30876", 1);
            put("31061", 3);
            put("31274", 1);
            put("31822", 2);
            put("32669", 2);
          }}),

      new SparseExampleOutputPair("In the dawn of the 21st century, humanity stands on the brink of one of the most transformative periods in history: the rise of artificial intelligence (AI). This technological revolution promises to redefine the way we live, work, and interact with the world around us. However, as with any major technological advancement, the implications of AI for society are complex, nuanced, and not entirely predictable. This essay explores the potential impacts of AI on various aspects of human life, including employment, ethics, personal privacy, and societal structures.",
          new HashMap<>() {{
            put("60", 1);
            put("183", 8);
            put("307", 1);
            put("738", 9);
            put("768", 3);
            put("774", 6);
            put("859", 6);
            put("1072", 2);
            put("1141", 2);
            put("1322", 1);
            put("1458", 2);
            put("1472", 5);
            put("1724", 1);
            put("1841", 12);
            put("1942", 4);
            put("2007", 1);
            put("2032", 4);
            put("2135", 1);
            put("2267", 7);
            put("2271", 2);
            put("2300", 1);
            put("2319", 6);
            put("2361", 6);
            put("2711", 1);
            put("2791", 1);
            put("2800", 5);
            put("3053", 5);
            put("3120", 2);
            put("3232", 5);
            put("3246", 1);
            put("3250", 4);
            put("3414", 2);
            put("3485", 4);
            put("3636", 6);
            put("3702", 1);
            put("3810", 1);
            put("4051", 2);
            put("4181", 2);
            put("4221", 1);
            put("4618", 9);
            put("4638", 3);
            put("4759", 1);
            put("4818", 4);
            put("4970", 4);
            put("5019", 4);
            put("5081", 2);
            put("5226", 1);
            put("5385", 4);
            put("5442", 6);
            put("5486", 3);
            put("5548", 7);
            put("5656", 4);
            put("5670", 1);
            put("5951", 4);
            put("6003", 1);
            put("6029", 2);
            put("6069", 2);
            put("6078", 4);
            put("6119", 1);
            put("6126", 2);
            put("6135", 2);
            put("6243", 3);
            put("6269", 7);
            put("6301", 1);
            put("6397", 1);
            put("6399", 3);
            put("6646", 6);
            put("6704", 2);
            put("6766", 2);
            put("6828", 1);
            put("6859", 1);
            put("6893", 2);
            put("7191", 1);
            put("7420", 3);
            put("7570", 6);
            put("7617", 2);
            put("7626", 4);
            put("7917", 1);
            put("8081", 2);
            put("8194", 1);
            put("8398", 1);
            put("8401", 3);
            put("8417", 4);
            put("8498", 1);
            put("8646", 8);
            put("9237", 1);
            put("9334", 3);
            put("9438", 1);
            put("9490", 18);
            put("9494", 1);
            put("9557", 7);
            put("10041", 11);
            put("10121", 4);
            put("10222", 4);
            put("10260", 4);
            put("10297", 4);
            put("10423", 2);
            put("10863", 2);
            put("10944", 2);
            put("11096", 2);
            put("11194", 4);
            put("11228", 5);
            put("11463", 1);
            put("11734", 1);
            put("11793", 1);
            put("12148", 2);
            put("12470", 5);
            put("12676", 1);
            put("12678", 2);
            put("12698", 3);
            put("12715", 10);
            put("12887", 5);
            put("13074", 5);
            put("13127", 18);
            put("13322", 1);
            put("13342", 2);
            put("13424", 7);
            put("13684", 3);
            put("13734", 2);
            put("13836", 1);
            put("13852", 1);
            put("14886", 6);
            put("15066", 8);
            put("15095", 1);
            put("15157", 7);
            put("15219", 2);
            put("15346", 11);
            put("15543", 9);
            put("15724", 9);
            put("15799", 1);
            put("15864", 6);
            put("15922", 13);
            put("16002", 1);
            put("16090", 9);
            put("16204", 3);
            put("16261", 3);
            put("16569", 4);
            put("16612", 1);
            put("16648", 9);
            put("16920", 3);
            put("17033", 5);
            put("17154", 2);
            put("17198", 2);
            put("17393", 5);
            put("17443", 1);
            put("17500", 7);
            put("17517", 4);
            put("17668", 3);
            put("17830", 10);
            put("17897", 4);
            put("17921", 1);
            put("17959", 1);
            put("18186", 3);
            put("18270", 1);
            put("18378", 1);
            put("18446", 9);
            put("18523", 3);
            put("18526", 1);
            put("18731", 1);
            put("18756", 3);
            put("18801", 1);
            put("19040", 1);
            put("19072", 9);
            put("19079", 2);
            put("19101", 3);
            put("19112", 1);
            put("19253", 1);
            put("19353", 3);
            put("19661", 1);
            put("19944", 1);
            put("20184", 2);
            put("20215", 2);
            put("20230", 4);
            put("20677", 1);
            put("20742", 11);
            put("20794", 3);
            put("20866", 1);
            put("20960", 4);
            put("21010", 3);
            put("21049", 8);
            put("21202", 3);
            put("21255", 1);
            put("21453", 1);
            put("21464", 1);
            put("21718", 1);
            put("21842", 2);
            put("22140", 1);
            put("22358", 1);
            put("22384", 1);
            put("22562", 7);
            put("22683", 1);
            put("22760", 3);
            put("22805", 1);
            put("22969", 3);
            put("23008", 5);
            put("23044", 1);
            put("23164", 1);
            put("23304", 1);
            put("23427", 1);
            put("23448", 4);
            put("23826", 2);
            put("24314", 4);
            put("24330", 1);
            put("24428", 1);
            put("24484", 17);
            put("24572", 1);
            put("24649", 1);
            put("24834", 3);
            put("24845", 4);
            put("24847", 3);
            put("24909", 1);
            put("24926", 2);
            put("24958", 22);
            put("24967", 1);
            put("25034", 1);
            put("25124", 1);
            put("25137", 11);
            put("25253", 3);
            put("25471", 2);
            put("25518", 2);
            put("25665", 1);
            put("25680", 1);
            put("25747", 11);
            put("25766", 1);
            put("25946", 1);
            put("26133", 2);
            put("26246", 6);
            put("26317", 3);
            put("26358", 4);
            put("26446", 1);
            put("26701", 1);
            put("26708", 1);
            put("26709", 1);
            put("26995", 9);
            put("27027", 4);
            put("27090", 2);
            put("27405", 9);
            put("27487", 1);
            put("27761", 3);
            put("27893", 15);
            put("27932", 2);
            put("27966", 1);
            put("28004", 1);
            put("28114", 5);
            put("28337", 1);
            put("28453", 1);
            put("28643", 5);
            put("28650", 1);
            put("28817", 1);
            put("28844", 2);
            put("28854", 8);
            put("28890", 1);
            put("28919", 5);
            put("29045", 4);
            put("29126", 9);
            put("29421", 2);
            put("29546", 1);
            put("29723", 4);
            put("29779", 3);
            put("29881", 5);
            put("30010", 4);
            put("30028", 5);
            put("30129", 2);
            put("30237", 1);
            put("30249", 6);
            put("30276", 1);
            put("30325", 5);
            put("30342", 1);
            put("30377", 3);
            put("30379", 1);
            put("30417", 1);
            put("30426", 10);
            put("30535", 3);
            put("30843", 2);
            put("30924", 2);
            put("30967", 1);
            put("31020", 2);
            put("31040", 2);
            put("31097", 1);
            put("31131", 1);
            put("31420", 1);
            put("31471", 7);
            put("31580", 2);
            put("31581", 2);
            put("31770", 5);
            put("31938", 8);
            put("31986", 1);
            put("32006", 3);
            put("32084", 3);
            put("32304", 1);
            put("32387", 1);
            put("32549", 5);
            put("32583", 1);
            put("32681", 2);
          }}),

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
  public void testExamples() throws OrtException, IOException, URISyntaxException {
    try (SlideOnEncoder encoder = new SlideOnEncoder()) {
      assertTrue(encoder.getModelPath().endsWith("slideon-optimized.onnx"));
      super.testExamples(EXAMPLES, encoder);
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
