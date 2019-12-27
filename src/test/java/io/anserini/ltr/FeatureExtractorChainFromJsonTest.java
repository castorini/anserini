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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import io.anserini.ltr.feature.FeatureExtractors;
import org.junit.Test;

/**
 * Will test that constructing a feature extractor chain works correctly
 */
public class FeatureExtractorChainFromJsonTest extends BaseFeatureExtractorTest{

  private JsonFactory jsonFactory = new JsonFactory();

  @Test
  public void testEmptyChain() throws Exception {
    String jsonString = "{extractors: []}";
    JsonParser jsonParser = jsonFactory.createParser(jsonString);

    FeatureExtractors emptyChain = FeatureExtractors.fromJson(jsonParser);
    assertNotNull(emptyChain);
  }

  @Test
  public void testChainSingleExtractorNoParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"AvgSCQ\"} ]}";
    JsonParser jsonParser = jsonFactory.createParser(jsonString);
    String testText = "test document";
    String testQuery = "document";
    //idf = 0.28768
    //tf =1
    float [] expected = {-0.24590f};
    FeatureExtractors chain = FeatureExtractors.fromJson(jsonParser);
    assertFeatureValues(expected, testQuery, testText, chain);
  }

  @Test
  public void testChainSingleExtractorParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"BM25Feature\", params: {k1:0.9, b:0.4}} ]}";
    JsonParser jsonParser = jsonFactory.createParser(jsonString);
    String docText = "single document test case";
    String queryText = "test";
    //df, tf =1, avgFL = 4, numDocs = 1
    //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

    // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
    // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
    float[] expected = {0.287682f};
    FeatureExtractors chain = FeatureExtractors.fromJson(jsonParser);
    assertFeatureValues(expected, queryText, docText, chain);
  }

  @Test
  public void testMultipleExtractorNoParam() throws Exception {
    String jsonString = "{extractors: [ {name: \"AvgIDF\"}, {name: \"SumTermFrequency\"} ]}";
    JsonParser jsonParser = jsonFactory.createParser(jsonString);
    String docText = "document missing token";
    String queryText = "document test";
    float[] expected = {0.836985f, 1f};

    FeatureExtractors chain = FeatureExtractors.fromJson(jsonParser);

    assertFeatureValues(expected, queryText, docText, chain);
  }

  @Test
  public void testMultipleExtractorMixed() throws Exception {
    String jsonString = "{extractors: [ {name: \"DocSize\"}, {name: \"QueryLength\"}," +
            "{name: \"OrderedSequentialPairs\", params:{gapSize: 2}}, {name: \"UnorderedSequentialPairs\", params:{gapSize : 2}}" +
            ", {name: \"OrderedSequentialPairs\", params:{gapSize: 5}} ]}";
    JsonParser jsonParser = jsonFactory.createParser(jsonString);
    String testText = "document test word word word  test bunch word document";
    String testQuery = "document test bunch";
    FeatureExtractors chain = FeatureExtractors.fromJson(jsonParser);

    // document test, test bunch, bunch document
    float[] expected = {9f, 3f, 2f, 2f, 4f};
    assertFeatureValues(expected, testQuery, testText,chain);


  }
}
