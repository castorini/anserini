package io.anserini.ltr;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.anserini.ltr.feature.FeatureExtractors;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Will test that constructing a feature extractor chain works correctly
 */
public class FeatureExtractorChainFromJsonTest extends BaseFeatureExtractorTest{

  private JsonParser parser = new JsonParser();

  @Test
  public void testEmptyChain() throws Exception {
    String jsonString = "{extractors: []}";
    JsonObject json = parser.parse(jsonString).getAsJsonObject();

    FeatureExtractors emptyChain = FeatureExtractors.fromJson(json);
    assertNotNull(emptyChain);
  }

  @Test
  public void testChainSingleExtractorNoParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"AvgSCQ\"} ]}";
    JsonObject json = parser.parse(jsonString).getAsJsonObject();
    String testText = "test document";
    String testQuery = "document";
    //idf = 0.28768
    //tf =1
    float [] expected = {-0.24590f};
    FeatureExtractors chain = FeatureExtractors.fromJson(json);
    assertFeatureValues(expected, testQuery, testText, chain);
  }

  @Test
  public void testChainSingleExtractorParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"BM25Feature\", params: {k1:0.9, b:0.4}} ]}";
    JsonObject json = parser.parse(jsonString).getAsJsonObject();
    String docText = "single document test case";
    String queryText = "test";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.287682f};
    FeatureExtractors chain = FeatureExtractors.fromJson(json);
    assertFeatureValues(expected, queryText, docText, chain);
  }

  @Test
  public void testMultipleExtractorNoParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"AvgIDF\"}, {name: \"SumTermFrequency\"} ]}";
    JsonObject json = parser.parse(jsonString).getAsJsonObject();
    String docText = "document missing token";
    String queryText = "document test";
    float[] expected = {0.836985f, 1f};

    FeatureExtractors chain = FeatureExtractors.fromJson(json);

    assertFeatureValues(expected, queryText, docText, chain);
  }

  @Test
  public void testMultipleExtractorMixed() throws Exception {
    String jsonString = "{extractors: [ {name: \"DocSize\"}, {name: \"QueryLength\"}," +
            "{name: \"OrderedSequentialPairs\", params:{gapSize: 2}}, {name: \"UnorderedSequentialPairs\", params:{gapSize : 2}}" +
            ", {name: \"OrderedSequentialPairs\", params:{gapSize: 5}} ]}";
    JsonObject json = parser.parse(jsonString).getAsJsonObject();
    String testText = "document test word word word  test bunch word document";
    String testQuery = "document test bunch";
    FeatureExtractors chain = FeatureExtractors.fromJson(json);

    // document test, test bunch, bunch document
    float[] expected = {9f, 3f, 2f, 2f, 4f};
    assertFeatureValues(expected, testQuery, testText,chain);


  }
}
