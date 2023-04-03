package io.anserini.search.query;

import ai.djl.modality.nlp.DefaultVocabulary;
import ai.djl.modality.nlp.bert.BertFullTokenizer;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class SpladeEncoderQueryTokenizationTest {
  static private final String VOCAB_URL = "https://rgw.cs.uwaterloo.ca/pyserini/data/wordpiece-vocab.txt";

  Object[][] examples = new Object[][] {
      { "which hormone increases calcium levels in the blood?",
          new long[] { 101, 2029, 18714, 7457, 13853, 3798, 1999, 1996, 2668, 1029, 102 } },

      { "what are the major political parties in great britain? select all that apply.",
          new long[] { 101, 2054, 2024, 1996, 2350, 2576, 4243, 1999, 2307, 3725, 1029, 7276, 2035, 2008, 6611, 1012,
              102 } },

      { "what type of conflict does della face in o, henry the gift of the magi",
          new long[] { 101, 2054, 2828, 1997, 4736, 2515, 8611, 2227, 1999, 1051, 1010, 2888, 1996, 5592, 1997, 1996,
              23848, 2072, 102 } },

      { "define: geon",
          new long[] { 101, 9375, 1024, 20248, 2078, 102 } },

      { "when are the four forces that act on an airplane in equilibrium?",
          new long[] { 101, 2043, 2024, 1996, 2176, 2749, 2008, 2552, 2006, 2019, 13297, 1999, 14442, 1029, 102 } },

      { "how long are we contagious after we catch a cold.?",
          new long[] { 101, 2129, 2146, 2024, 2057, 9530, 15900, 6313, 2044, 2057, 4608, 1037, 3147, 1012, 1029,
              102 } },

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
          new long[] { 101, 2029, 18672, 8844, 26450, 6740, 16896, 2006, 1996, 4942, 15782, 14289, 8017, 1042, 21842,
              1997, 1996, 8040, 9331, 7068, 1998, 19274, 2015, 2006, 1996, 8276, 7270, 21769, 1997, 1996, 20368, 7946,
              1029, 102 } },
  };

  static private String getCacheDir() {
    File cacheDir = new File("~/.cache/anserini/test");
    if (!cacheDir.exists()) {
      cacheDir.mkdir();
    }
    return cacheDir.getPath();
  }

  static private Path getVocabPath() throws IOException {
    File vocabFile = new File(getCacheDir(), "UnicoilVocab.txt");
    FileUtils.copyURLToFile(new URL(VOCAB_URL), vocabFile);
    return vocabFile.toPath();
  }

  @Test
  public void basic() throws Exception {
    DefaultVocabulary vocabulary = DefaultVocabulary.builder()
        .addFromTextFile(getVocabPath())
        .optUnknownToken("[UNK]")
        .build();
    BertFullTokenizer tokenizer = new BertFullTokenizer(vocabulary, true);

    for (Object[] example : examples) {
      String query = (String) example[0];
      long[] expectedTokenIds = (long[]) example[1];
      List<String> tokens = new ArrayList<>();
      tokens.add("[CLS]");
      tokens.addAll(tokenizer.tokenize(query));
      tokens.add("[SEP]");
      assertArrayEquals(expectedTokenIds, convertTokensToIds(tokenizer, tokens));
    }
  }

  private long[] convertTokensToIds(BertFullTokenizer tokenizer, List<String> tokens) {
    int numTokens = tokens.size();
    long[] tokenIds = new long[numTokens];
    for (int i = 0; i < numTokens; ++i) {
      tokenIds[i] = tokenizer.getVocabulary().getIndex(tokens.get(i));
    }
    return tokenIds;
  }
}