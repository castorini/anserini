package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.AvgIDFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

public class AvgIDFFeatureExtractorTest extends BaseFeatureExtractorTest {

  private static FeatureExtractors EXTRACTOR = getChain(new AvgIDFFeatureExtractor());

  @Test
  public void testSingleDoc() throws IOException {
    float[] expected = {0.2876f};
    assertFeatureValues(expected, "document", "test document", EXTRACTOR);
  }

  @Test
  public void testSingleDocMissingToken() throws IOException {
    float[] expected = {0.836985f};
    assertFeatureValues(expected, "document test", "document missing token", EXTRACTOR);
  }

  @Test
  public void testMultipleDocMultipleTokens() throws IOException {
    // N = 7
    // N_document = 4   0.57536
    // N_token = 0      2.77258
    // N_test = 3       0.82667
    float[] expected = {1.391537f};
    assertFeatureValues(expected, "document token test",
            Lists.newArrayList("first document test",
                    "second document test",
                    "third document test ",
                    "unrelated entry",
                    "another entry",
                    "another document",
                    "one more"), EXTRACTOR, 0);
  }
}
