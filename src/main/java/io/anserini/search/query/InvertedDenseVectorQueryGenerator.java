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
import com.fasterxml.jackson.databind.ObjectMapper;
import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.fw.FakeWordsEncoderAnalyzer;
import io.anserini.analysis.lexlsh.LexicalLshAnalyzer;
import io.anserini.index.Constants;
import io.anserini.index.IndexInvertedDenseVectors;
import io.anserini.search.InvertedDenseSearcher;
import io.anserini.search.SearchInvertedDenseVectors;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.Query;

import static io.anserini.index.IndexInvertedDenseVectors.FW;
import static io.anserini.index.IndexInvertedDenseVectors.LEXLSH;
import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class InvertedDenseVectorQueryGenerator {

  private final Analyzer vectorAnalyzer;
  private final boolean jsonConversion;

  public InvertedDenseVectorQueryGenerator(InvertedDenseSearcher.Args args, boolean jsonConversion) {
    this.jsonConversion = jsonConversion;
    if (args.encoding.equalsIgnoreCase(FW)) {
      vectorAnalyzer = new FakeWordsEncoderAnalyzer(args.q);
    } else if (args.encoding.equalsIgnoreCase(LEXLSH)) {
      vectorAnalyzer = new LexicalLshAnalyzer(args.decimals, args.ngrams, args.hashCount, args.bucketCount, args.hashSetSize);
    } else {
      throw new RuntimeException("unrecognized encoding " + args.encoding);
    }
  }

  private String convertJsonArray(String vectorString) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    ArrayList<Float> denseVector = mapper.readValue(vectorString, new TypeReference<>() {});
    StringBuilder sb = new StringBuilder();
    for (float fv : denseVector) {
      if (sb.length() > 0) {
        sb.append(' ');
      }
      sb.append(fv);
    }
    return sb.toString();
  }

  public Query buildQuery(String queryString) throws JsonProcessingException {
    String queryText;
    if (jsonConversion) {
      queryText = convertJsonArray(queryString);
    } else {
      queryText = queryString;
    }
    float cutoff = 0.999f;
    CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, cutoff);
    for (String token : AnalyzerUtils.analyze(vectorAnalyzer, queryText)) {
      simQuery.add(new Term(Constants.VECTOR, token));
    }
    return simQuery;
  }
}