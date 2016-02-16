package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.OrderedQueryPairsFeatureExtractor;
import io.anserini.ltr.feature.OrderedSequentialPairsFeatureExtractor;
import io.anserini.ltr.feature.UnorderedQueryPairsFeatureExtractor;
import io.anserini.ltr.feature.UnorderedSequentialPairsFeatureExtractor;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Create some temporary documents and test the correctness of ordered and unordered
 * window phrase extractors
 */
public class BigramFeaturesTest extends BaseFeatureExtractorTest {

  private FeatureExtractors getUnorderedChain() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new UnorderedSequentialPairsFeatureExtractor(2));
    chain.add(new UnorderedSequentialPairsFeatureExtractor(4));
    chain.add(new UnorderedSequentialPairsFeatureExtractor(6));
    return chain;
  }

  private FeatureExtractors getOrderedChain() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new OrderedSequentialPairsFeatureExtractor(2));
    chain.add(new OrderedSequentialPairsFeatureExtractor(4));
    chain.add(new OrderedSequentialPairsFeatureExtractor(6));
    return chain;
  }

  private FeatureExtractors getMixedChain() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new OrderedSequentialPairsFeatureExtractor(2));
    chain.add(new OrderedSequentialPairsFeatureExtractor(4));
    chain.add(new OrderedSequentialPairsFeatureExtractor(6));
    chain.add(new UnorderedSequentialPairsFeatureExtractor(2));
    chain.add(new UnorderedSequentialPairsFeatureExtractor(4));
    chain.add(new UnorderedSequentialPairsFeatureExtractor(6));
    return chain;
  }

  private FeatureExtractors getAllPairsOrdered() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new OrderedQueryPairsFeatureExtractor(2));
    chain.add(new OrderedQueryPairsFeatureExtractor(4));
    chain.add(new OrderedQueryPairsFeatureExtractor(6));
    return chain;
  }

  private FeatureExtractors getAllPairsUnOrdered() {
    FeatureExtractors chain = new FeatureExtractors();
    chain.add(new UnorderedQueryPairsFeatureExtractor(2));
    chain.add(new UnorderedQueryPairsFeatureExtractor(4));
    chain.add(new UnorderedQueryPairsFeatureExtractor(6));
    return chain;
  }

  @Test
  public void testSimpleQuery () throws IOException {
    String testText = "a simple document";
    String testQuery = "simple document";
    float[] expected = {1,1,1};
    assertFeatureValues(expected, testQuery, testText, getUnorderedChain());
    assertFeatureValues(expected, testQuery, testText, getOrderedChain());
  }

  @Test
  public void testMultipleUnorderedQuery() throws IOException {
    String testText = "document more token simple test case";
    String testQuery = "simple document test case";

    // there are 2, document ... test, simple test, test case
    // the first window size 2 has too small a window
    float[] orderedExpected = {1,2,2};
    // There are simple document, document test, test case
    float[] unordedExpected = {1,3,3};

    assertFeatureValues(orderedExpected,testQuery, testText, getOrderedChain());
    assertFeatureValues(unordedExpected,testQuery, testText, getUnorderedChain());
  }

  @Test
  public void testMixedMultipleQuery() throws IOException {
    String testText = "bunch words document test simple case document test case simple, test document";
    String testQuery = "document test";

    // there are 2 instances, but the second and 3rd instances are counted twice each in 6 window
    float[] orderedExpected = {2,3,4};

    float[] unorderedExpected = {3, 6,7};
    float[] mixedExpected = {2,3,4,3,6,7};
    assertFeatureValues(orderedExpected, testQuery, testText, getOrderedChain());
    assertFeatureValues(unorderedExpected, testQuery, testText, getUnorderedChain());
    assertFeatureValues(mixedExpected, testQuery, testText, getMixedChain());
  }

  @Test
  public void testSimpleCountOrderedAllPairs() throws IOException {
    String testText = "bunch words document test simple case large text length size";
    String testQuery = "bunch words test";

    // bunch words, bunch test for 4,6, words test
    float[] expected = {2, 3,3};
    assertFeatureValues(expected, testQuery, testText, getAllPairsOrdered());
  }

  @Test
  public void testSimpleCountUnorderedAllPairs() throws IOException {
    String testText =  "bunch words document test simple case large text length size";
    String testQuery = "test document text";

    // test document, test text, document text
    float[] expected = {1, 2, 3};
    assertFeatureValues(expected, testQuery, testText, getAllPairsUnOrdered());
  }

  @Test
  public void testDuplicateStartingTokens() throws IOException {
    String testText = "document test document bunch";
    String testQuery = "document test document bunch";

    // we have document test, test document, document bunch
    float[] orderedExpected = {3,4,4};
    // We have document test, test document, document bunchx2, test document (a second time)
    float[] unorderedExpected = {5, 6,6};

    assertFeatureValues(orderedExpected, testQuery, testText, getOrderedChain());
    assertFeatureValues(unorderedExpected, testQuery, testText, getUnorderedChain());
  }

  @Test
  public void testDuplicateAllPairs() throws IOException {
    String testText = "document case document test bunch";
    String testQuery = "document case test";

    //document case, document test x2, case test
    float[] orderedExpected = {3,4,4};
    assertFeatureValues(orderedExpected, testQuery, testText, getAllPairsOrdered());

    // document case, document test, case test
    // case document, test document, test case
    float[] unorderedExpected = {4,5,5};
    assertFeatureValues(unorderedExpected, testQuery, testText, getAllPairsUnOrdered());
  }

  @Test
  public void testMixedSequentialAllPairs() throws IOException {
    String testText = "document test word word word test case word document";
    String testQuery = "document test case";

    // document test, test bunch, bunch document
    float[] expected = {2,2,2,3};
    assertFeatureValues(expected, testQuery, testText,
            getChain(new OrderedSequentialPairsFeatureExtractor(2),
                    new UnorderedSequentialPairsFeatureExtractor(2),
                    new OrderedQueryPairsFeatureExtractor(2),
                    new UnorderedQueryPairsFeatureExtractor(2)));
  }

  @Test
  public void testSimpleBigramCount() throws IOException {
    String testText = "document test document test";
    String testQuery = "missing phrase";
    float[] expected = {0.0f};
    assertFeatureValues(expected, testQuery, testText,
            getChain(new OrderedSequentialPairsFeatureExtractor(1)));
  }

  @Test
  public void testSimpleBigramCount2() throws IOException {
    String testText = "document test document test";
    String testQuery = "document tests";
    float[] expected = {2f};
    assertFeatureValues(expected, testQuery, testText,
            getChain(new OrderedSequentialPairsFeatureExtractor(1)));
  }

  @Test
  public void testBigramCountMultiple() throws IOException {
    String testText = "test document test document multiple tokens multiple phrase";
    String testQuery = "test document multiple";
    //test document x 2 + document multiple
    float[] expected = {3f};
    assertFeatureValues(expected, testQuery, testText,
            getChain(new OrderedSequentialPairsFeatureExtractor(1)));
  }

}
