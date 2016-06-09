package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.UnigramFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests the unigram count feature
 */
public class UnigramFeaturesTest extends BaseFeatureExtractorTest {

  @Test
  public void testSingleQueryTermCounts() throws IOException {
    String testText = "document document simple test case";
    String testQuery = "document";
    float [] expected = {2};
    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testNonMatchQuery() throws IOException {
    String testText = "document document simple";
    String testQuery = "case";
    float[] expected = {0};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testPartialMatches() throws IOException {
    String testText = "simple test case document";
    String testQuery = "simple document unigram";
    float[] expected = {2};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }

  @Test
  public void testMultipleMatches() throws IOException {
    String testText = "simple simple document test case document";
    String testQuery = "document simple case nonexistent query";
    float[] expected = {5};

    assertFeatureValues(expected, testQuery, testText, new UnigramFeatureExtractor());
  }


}
