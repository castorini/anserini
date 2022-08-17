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

import org.apache.lucene.search.KnnVectorQuery;

public class VectorQueryGenerator {

  private float[] convertJsonArray(String vectorString) throws JsonMappingException, JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<ArrayList<Float>>(){});
    int length = denseVector.size();
    float[] vector = new float[length];
    int i = 0;
    for (Float f : denseVector) {
      vector[i++] = f;
    }
    return vector;
  }
  
  public KnnVectorQuery buildQuery(String field, String queryString, Integer topK) throws JsonMappingException, JsonProcessingException{
    float[] queryVector;
    queryVector = convertJsonArray(queryString);
    KnnVectorQuery knnQuery = new KnnVectorQuery(field, queryVector, topK);
    return knnQuery;
  }
}
