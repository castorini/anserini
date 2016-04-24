package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.SCQFeatureExtractor;
import org.junit.Test;

import java.io.IOException;

public class SCQFeatureExtractorTest extends BaseFeatureExtractorTest {

  private static FeatureExtractors EXTRACTOR = getChain(new SCQFeatureExtractor());

  @Test
  public void testSimpleSingleDocument() throws IOException {
    String testText = "test document";
    String testQuery = "document";
    //idf = 0.28768
    //tf =1
    float [] expected = {-0.24590f};
    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testSingleDocumentMultipleQueryToken() throws IOException {
    String testText = "test document more tokens than just two document ";
    String testQuery = "document missing";

    float[] expected = {0.22362f};
    assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
  }

  @Test
  public void testSimpleMultiDocument() throws IOException {
    String testQuery = "test document";
    // idf = 0.47
    // tf = 3
    // document: 1.34359
    // test : 1 : 0.98064
    float[] expected = {1.162115f};

    assertFeatureValues(expected, testQuery,
            Lists.newArrayList("test document multiple tokens document",
                    "another document for doc freq count",
                    "yet another"), EXTRACTOR, 0);
  }
}
