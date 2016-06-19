package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.PMIFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Test implementation of PMI
 */
public class PMIFeatureExtractorTest extends BaseFeatureExtractorTest {

  private static FeatureExtractors EXTRACTOR = getChain(new PMIFeatureExtractor());

  @Test
  public void testSingleDocSimpleQuery() throws IOException {
    String testText = "test document multiple tokens";
    String testQuery = "test document";
    float[] expected = {0f};

    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testMultipleDocSimpleQuery() throws IOException {
    float[] expected = {-1.43916f};
    String testQuery = "test document token";
    // 3 query pairs: test document, document token, test token
    // docfreqs: test: 2
    //           document: 5
    //           token: 3
    // intersects:  test document: 1
    //              document token: 2
    //              test token: 0
    // avgPMI = 1/3 * (Math.log(1/(2*5)) + Math.log(2/(5*3))) = -1.43916
    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("test document",
                    "document token",
                    "document no match",
                    "test",
                    "no match token",
                    "no match document token",
                    "just another document"), EXTRACTOR, 0);
  }

  @Test
  public void testBadQueries() throws IOException {
    float[] expected = {0.0f};
    String testQuery  = "missing tokens";
    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("document",
            "another document",
            "more documents",
            "just don't have the query pieces",
            "one more"), EXTRACTOR, 0);
  }

  @Test
  public void testNoIntersect() throws IOException {
    float[] expected = {0.0f};
    String testQuery = "test document";
    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("document", "test entry",
                    "another document"), EXTRACTOR, 0);
  }
}
