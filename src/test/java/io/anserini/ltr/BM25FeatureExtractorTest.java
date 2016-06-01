package io.anserini.ltr;

import com.google.common.collect.Lists;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.BM25FeatureExtractor;
import org.junit.Test;

import java.io.IOException;

/**
 * Tests that BM25 score is computed according to our forumla
 */
public class BM25FeatureExtractorTest extends BaseFeatureExtractorTest {
  // Test the BM25 extractor with 2 settings of k and b
  private static final FeatureExtractor EXTRACTOR = new BM25FeatureExtractor(0.9,0.4);
  // 1.25,0.75
  private static final FeatureExtractor EXTRACTOR2 = new BM25FeatureExtractor();

  @Test
  public void testSingleDocSingleQuery() throws IOException {
    String docText = "single document test case";
    String queryText = "test";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.287682f,0.287682f};

    assertFeatureValues(expected, queryText, docText, getChain(EXTRACTOR, EXTRACTOR2));

  }

  @Test
  public void testSingleDocMultiQuery() throws IOException {
    String docText = "single document test case";
    String queryText = "test document";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.575364f,0.575364f};

    assertFeatureValues(expected, queryText, docText, getChain(EXTRACTOR, EXTRACTOR2));

  }

  @Test
  public void testMultiDocSingleQuery() throws IOException {
    String queryText = "test";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082

    // 0.98082* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.92255
    // 0.98082* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.8612
    float[] expected = {0.92255f,0.8612f};

    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "another document", "yet another document"), getChain(EXTRACTOR, EXTRACTOR2),0);

  }

  @Test
  public void testMultiDocMultiQuery() throws IOException {
    String queryText = "test document";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082
    //idf = log(1 + (3 - 3 + 0.5 / (3 + 0.5)) = 0.13353

    // 0.98082* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.92255
    // 0.98082* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.8612

    // 0.13353* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/3))) = 0.12559
    // 0.13353* 2.25 / (1 + 1.25 *(0.25 + 0.75* 4/3)) = 0.1172
    float[] expected = {1.04814f,0.97844f};

    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case",
            "another document", "yet another document"), getChain(EXTRACTOR, EXTRACTOR2),0);

  }
  @Test
  public void testMultiDocMultiQuery2() throws IOException {
    String queryText = "test document";
    //df , tf =1, avgFL = 3, numDocs = 3
    //idf = log(1 + (3- 1 + 0.5 / 1 + 0.5)) = 0.98082
    //idf = log(1 + (3 - 3 + 0.5 / (3 + 0.5)) = 0.13353

    // 0.98082* (2*1.9) / (2 + 0.9 * (0.6 + 0.4 * (5/3))) = 1.1870
    // 0.98082* (2*2.25) / (2 + 1.25 *(0.25 + 0.75* 5/3)) = 1.1390

    // 0.13353* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (5/3))) = 0.11855
    // 0.13353* 2.25 / (1 + 1.25 *(0.25 + 0.75* 5/3)) = 0.1045
    float[] expected = {1.30555f,1.2435f};

    assertFeatureValues(expected, queryText, Lists.newArrayList("single document test case test",
            "another document", "more document"), getChain(EXTRACTOR, EXTRACTOR2),0);

  }

}
