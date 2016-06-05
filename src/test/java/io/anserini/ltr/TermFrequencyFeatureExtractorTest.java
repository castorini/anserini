package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.TermFrequencyFeatureExtractor;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

/**
 * Test the term frequency feature extractor is correct
 */
public class TermFrequencyFeatureExtractorTest extends BaseFeatureExtractorTest{

  private FeatureExtractors getChain() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new TermFrequencyFeatureExtractor());
    return chain;
  }

  @Test
  public void testAllMissing() throws IOException {
    float[] expected = {0};
    assertFeatureValues(expected, "nothing", "document test missing all", getChain());
  }

  @Test
  public void testSingleTermDoc() throws IOException {
    String testText = "document document document another";
    String testQuery = "document";
    float[] expected = {3};

    assertFeatureValues(expected, testQuery, testText, getChain());
  }

  @Test
  public void testMissingTermDoc() throws IOException {
    String testText = "document test simple tokens";
    String testQuery = "simple missing";
    float[] expected = {1};

    assertFeatureValues(expected, testQuery, testText, getChain());
  }

  @Test
  public void testMultipleTermsDoc() throws IOException {
    String testText = "document with multiple document term document multiple some missing";
    String testQuery = "document multiple missing";
    float[] expected = {6};

    assertFeatureValues(expected, testQuery, testText, getChain());
  }

  @Test
  public void testTermFrequencyWithMultipleDocs() throws IOException {
    List<String> docs = Lists.newArrayList("document document", "document with multiple terms",
            "document to test", "test terms tokens", "another test document");
    // We want to test that the expected value of count 1 is found for document
    // at index 2
    String queryText = "document";
    float[] expected = {1};

    assertFeatureValues(expected, queryText, docs, getChain(), 2);
  }
}
