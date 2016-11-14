package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.base.TFIDFFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Make sure that TFIDF feature extractor gives the scores as caculated by the formula
 * in the feature extractor, does not have all components of the lucene formula
 */
public class TFIDFFeatureExtractorTest extends BaseFeatureExtractorTest {

  @Test
  public void testTFIDFOnSingleDocSingleQuery() throws IOException {
    float[] expected = {0.094158f};
    assertFeatureValues(expected, "document", "single document test case",
            new TFIDFFeatureExtractor() );
  }

  @Test
  public void testTFIDFOnSingleDocMultiQuery() throws IOException {
    float[] expected = {0.188316f};
    assertFeatureValues(expected, "document test", "single document test case",
            new TFIDFFeatureExtractor() );
  }

  @Test
  public void testTFIDFOnMultiDocSingleQuery() throws IOException {
    String queryText = "document";

    float[] expected = {0.35345f};
    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "another document test"),getChain(new TFIDFFeatureExtractor()), 0 );
  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery() throws IOException {
    String queryText = "document test";

    float[] expected = {0.7069f};
    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "another document test"),getChain(new TFIDFFeatureExtractor()), 0 );
  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery2() throws IOException {
    String queryText = "document test";

    float[] expected = {1.35345f};
    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "another document"),getChain(new TFIDFFeatureExtractor()), 0 );

  }

  @Test
  public void testTFIDFOnMultiDocMultiQuery3() throws IOException {
    String queryText = "document test";

    float[] expected = {2.4827f};
    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "new document", "another document"),getChain(new TFIDFFeatureExtractor()), 0 );
  }
}
