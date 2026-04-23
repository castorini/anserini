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

import io.anserini.analysis.AnalyzerUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.IndexReader;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.anserini.index.Constants;
import io.anserini.index.IndexReaderUtils;

/*
 * Query-side BM25 query builder
 */
public class QuerySideBm25QueryGenerator extends QueryGenerator {
  private final float k1;
  private final float b;
  private final IndexReader indexReader;
  private final float avgLength;

  public QuerySideBm25QueryGenerator(float k1, float b, IndexReader indexReader) {
    this.k1 = k1;
    this.b = b;
    this.indexReader = indexReader;
    try{
      this.avgLength = indexReader.getSumTotalTermFreq(Constants.CONTENTS) / (float)indexReader.getDocCount(Constants.CONTENTS);
    }
    catch (Exception e) {
      throw new RuntimeException("Error getting BM25 statistics", e);
    }
  }

  // implement fields
  @Override
  public Query buildQuery(String field, Analyzer analyzer, String queryText) {
    List<String> tokens = AnalyzerUtils.analyze(analyzer, queryText);
    Map<String, Long> collect = tokens.stream()
        .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    try {
      int n = indexReader.getDocCount(Constants.CONTENTS);
      for (String t : collect.keySet()) {
        float termFrequency = collect.get(t);
        long docFreq = IndexReaderUtils.getDF(indexReader, t);
        float idf = (float) Math.log(1 + (n - docFreq + 0.5) / (docFreq + 0.5));
        float score = idf * (termFrequency / (termFrequency + k1 * (1 - b + b * (tokens.size() / avgLength))));
        builder.add(new BoostQuery(new TermQuery(new Term(field, t)), score),
            BooleanClause.Occur.SHOULD);
      }
      return builder.build();
    } catch (Exception e) {
      throw new RuntimeException("Error building BM25 query", e);
    }
  }

  @Override
  public Query buildQuery(Map<String, Float> fields, Analyzer analyzer, String queryText) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    for (Map.Entry<String, Float> entry : fields.entrySet()) {
      String field = entry.getKey();
      float boost = entry.getValue();

      Query clause = buildQuery(field, analyzer, queryText);
      builder.add(new BoostQuery(clause, boost), BooleanClause.Occur.SHOULD);
    }
    return builder.build();
  }
}

