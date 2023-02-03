/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

package io.anserini.search.query;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.analysis.AnalyzerUtils;
import io.anserini.ann.IndexVectors;
import io.anserini.ann.fw.FakeWordsEncoderAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.Query;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class FWAnnVectorQueryGenerator {

  private final FakeWordsEncoderAnalyzer analyzer = new FakeWordsEncoderAnalyzer();;

  private float[] convertJsonArray(String vectorString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<>() {
    });
    int length = denseVector.size();
    float[] vector = new float[length];
    int i = 0;
    for (Float f : denseVector) {
      vector[i++] = f;
    }
    return vector;
  }
  
  public Query buildQuery(String queryString) throws JsonMappingException, JsonProcessingException{
    float[] queryVector;
    queryVector = convertJsonArray(queryString);
    StringBuilder sb = new StringBuilder();
    for (double fv : queryVector) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(fv);
    }
    String vectorString = sb.toString();
    float msm = 0;
    float cutoff = 0.999f;
    CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, cutoff);
    for (String token : AnalyzerUtils.analyze(analyzer, vectorString)) {
      simQuery.add(new Term(IndexVectors.FIELD_VECTOR, token));
    }
//    if (msm > 0) {
//      simQuery.setHighFreqMinimumNumberShouldMatch(msm);
//      simQuery.setLowFreqMinimumNumberShouldMatch(msm);
//    }
    return simQuery;
  }
}
