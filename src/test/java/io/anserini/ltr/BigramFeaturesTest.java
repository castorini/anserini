/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.ltr;

import io.anserini.ltr.feature.*;
import io.anserini.ltr.feature.base.OrderedQueryPairs;
import io.anserini.ltr.feature.base.OrderedSequentialPairs;
import io.anserini.ltr.feature.base.UnorderedQueryPairs;
import io.anserini.ltr.feature.base.UnorderedSequentialPairs;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Create some temporary documents and test the correctness of ordered and unordered
 * window phrase extractors
 */
public class BigramFeaturesTest extends BaseFeatureExtractorTest<Integer> {

  private List<FeatureExtractor> getUnorderedChain() {
    return getChain(
            new UnorderedSequentialPairs(2),
            new UnorderedSequentialPairs(4),
            new UnorderedSequentialPairs(6)
    );
  }

  private List<FeatureExtractor> getOrderedChain() {
    return getChain(
            new OrderedSequentialPairs(2),
            new OrderedSequentialPairs(4),
            new OrderedSequentialPairs(6)
    );
  }

  private List<FeatureExtractor> getMixedChain() {
    return getChain(
            new OrderedSequentialPairs(2),
            new OrderedSequentialPairs(4),
            new OrderedSequentialPairs(6),
            new UnorderedSequentialPairs(2),
            new UnorderedSequentialPairs(4),
            new UnorderedSequentialPairs(6)
    );
  }

  private List<FeatureExtractor> getAllPairsOrdered() {
    return getChain(
            new OrderedQueryPairs(2),
            new OrderedQueryPairs(4),
            new OrderedQueryPairs(6)
    );
  }

  private List<FeatureExtractor> getAllPairsUnOrdered() {
    return getChain(
            new UnorderedQueryPairs(2),
            new UnorderedQueryPairs(4),
            new UnorderedQueryPairs(6)
    );
  }

  private List<FeatureExtractor> getMixedSequentialAllPairs() {
    return getChain(
            new OrderedSequentialPairs(2),
            new UnorderedSequentialPairs(2),
            new OrderedQueryPairs(2),
            new UnorderedQueryPairs(2)
    );
  }

  private static FeatureExtractor bigram = new OrderedSequentialPairs(1);

  @Test
  public void testSimpleQuery () throws IOException, ExecutionException, InterruptedException {
    String testText = "a simple document";
    String testQuery = "simple document";
    float[] expected = {1,1,1};
    assertFeatureValues(expected, testQuery, testText, getUnorderedChain());
    assertFeatureValues(expected, testQuery, testText, getOrderedChain());
  }

  @Test
  public void testMultipleUnorderedQuery() throws IOException, ExecutionException, InterruptedException {
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
  public void testMixedMultipleQuery() throws IOException, ExecutionException, InterruptedException {
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
  public void testSimpleCountOrderedAllPairs() throws IOException, ExecutionException, InterruptedException {
    String testText = "bunch words document test simple case large text length size";
    String testQuery = "bunch words test";

    // bunch words, bunch test for 4,6, words test
    float[] expected = {2, 3,3};
    assertFeatureValues(expected, testQuery, testText, getAllPairsOrdered());
  }

  @Test
  public void testSimpleCountUnorderedAllPairs() throws IOException, ExecutionException, InterruptedException {
    String testText =  "bunch words document test simple case large text length size";
    String testQuery = "test document text";

    // test document, test text, document text
    float[] expected = {1, 2, 3};
    assertFeatureValues(expected, testQuery, testText, getAllPairsUnOrdered());
  }

  @Test
  public void testDuplicateStartingTokens() throws IOException, ExecutionException, InterruptedException {
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
  public void testDuplicateAllPairs() throws IOException, ExecutionException, InterruptedException {
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
  public void testMixedSequentialAllPairs() throws IOException, ExecutionException, InterruptedException {
    String testText = "document test word word word test case word document";
    String testQuery = "document test case";

    // document test, test bunch, bunch document
    float[] expected = {2,2,2,3};
    assertFeatureValues(expected, testQuery, testText, getMixedSequentialAllPairs());
  }

  @Test
  public void testSimpleBigramCount() throws IOException, ExecutionException, InterruptedException {
    String testText = "document test document test";
    String testQuery = "missing phrase";
    float[] expected = {0.0f};
    assertFeatureValues(expected, testQuery, testText, bigram);
  }

  @Test
  public void testSimpleBigramCount2() throws IOException, ExecutionException, InterruptedException {
    String testText = "document test document test";
    String testQuery = "document tests";
    float[] expected = {2f};
    assertFeatureValues(expected, testQuery, testText, bigram);
  }

  @Test
  public void testBigramCountMultiple() throws IOException, ExecutionException, InterruptedException {
    String testText = "test document test document multiple tokens multiple phrase";
    String testQuery = "test document multiple";
    //test document x 2 + document multiple
    float[] expected = {3f};
    assertFeatureValues(expected, testQuery, testText, bigram);
  }

}
