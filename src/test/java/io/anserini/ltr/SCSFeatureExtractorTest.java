package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.SimplifiedClarityFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests the simplified clarity feature
 */
public class SCSFeatureExtractorTest extends BaseFeatureExtractorTest {
  private FeatureExtractors EXTRACTOR = getChain(new SimplifiedClarityFeatureExtractor());

  @Test
  public void testBadQuery() throws IOException {
    String testQuery = "test";
    // P[t|q] = 1
    // P[t|D] = 0
    float[] expected = {0f};
    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("document",
                    "another document"), EXTRACTOR, 0);
  }

  @Test
  public void testSimpleQuery() throws IOException {
    String testQuery = "test";

    // P[t|q] = 1
    // P[t|D] = 5 / 10 = 0.5
    // 1 * log(2)
    float[] expected = {0.6931f};

    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("test document",
                    "test test",
                    "another document",
                    "more test document",
                    "test"), EXTRACTOR, 0);

  }

  @Test
  public void testMultipleTokensQuery() throws IOException {
    String testQuery = "test document";

    // P[t|q] = 1/2
    // P[t|D] = 5 / 10 = 0.5
    // 1/2 * log(0.5/0.5)
    // 1/2 * log(0.5 / 3/10)
    float[] expected = {0.25541f};

    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("test document",
                    "test test",
                    "another document",
                    "more test document",
                    "test"), EXTRACTOR, 0);

  }
}
