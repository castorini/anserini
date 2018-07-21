package io.anserini.ltr;

import io.anserini.ltr.feature.base.DocSizeFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

/**
 * Test we get the doc size correctly
 */
public class DocSizeFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {

  @Test
  public void testSingleDoc() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text can't be empty", "document size independent of query document",
            new DocSizeFeatureExtractor());
  }

  @Test
  public void testMultipleDocs() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text", Arrays.asList("first document",
                                      "second document", "test document document document test"),
            getChain(new DocSizeFeatureExtractor()), 2);
  }

}
