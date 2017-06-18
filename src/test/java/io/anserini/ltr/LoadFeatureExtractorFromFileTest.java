package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractors;
import org.junit.Test;

/**
 * Test loading feature extractors from files
 */
public class LoadFeatureExtractorFromFileTest extends BaseFeatureExtractorTest{

  @Test
  public void testMultipleExtractorNoParam() throws Exception {
    String jsonFile = "src/test/resources/MixedFeatureExtractor.txt";
    String docText = "document missing token";
    String queryText = "document test";
    float[] expected = {0.836985f, 1f};

    FeatureExtractors chain = FeatureExtractors.loadExtractor(jsonFile);

    assertFeatureValues(expected, queryText, docText, chain);
  }
}
