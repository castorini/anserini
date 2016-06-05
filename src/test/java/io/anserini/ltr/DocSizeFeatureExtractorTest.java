package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.base.DocSizeFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Test we get the doc size correctly
 */
public class DocSizeFeatureExtractorTest extends BaseFeatureExtractorTest{

  @Test
  public void testSingleDoc() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text can't be empty", "document size independent of query document",
            new DocSizeFeatureExtractor());
  }

  @Test
  public void testMultipleDocs() throws IOException {
    float[] expected = {5};
    assertFeatureValues(expected, "query text", Lists.newArrayList("first document",
                                      "second document", "test document document document test"),
            getChain(new DocSizeFeatureExtractor()), 2);
  }

}