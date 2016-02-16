package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.AvgICTFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Test ICTF feature extractor is implemented according to
 * the  Carmel, Yom-Tov synthesis series book
 */
public class ICTFFeatureExtractorTest extends BaseFeatureExtractorTest {

  private static FeatureExtractors EXTRACTOR = getChain(new AvgICTFFeatureExtractor());
  @Test
  public void testSingleQueryPhrase() throws IOException {
    float[] expected = {0};
    assertFeatureValues(expected, "document", "document", EXTRACTOR);
  }

  @Test
  public void testSingleQuery2() throws IOException {
    float[] expected = {1.38629f};
    assertFeatureValues(expected, "document", "document multiple tokens more", EXTRACTOR);
  }

  @Test
  public void testSingleQuery3() throws IOException {
    float[] expected = {0.693147f};
    assertFeatureValues(expected, "document", "document document test more tokens document", EXTRACTOR);
  }

  @Test
  public void testMultiQuery() throws IOException {
    float[] expected = {0.20273f};

    assertFeatureValues(expected, "document test", "document document missing", EXTRACTOR);
  }

  @Test
  public void testMultiQuery2() throws IOException {
    // log(8/3)*0.5 + log(8/2) * 0.5
    float[] expected = {1.18356f};
    assertFeatureValues(expected, "document test", "document document test test more tokens document tokens", EXTRACTOR);
  }
}
