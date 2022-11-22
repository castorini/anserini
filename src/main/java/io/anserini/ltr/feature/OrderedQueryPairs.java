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

package io.anserini.ltr.feature;

import io.anserini.index.Constants;
import io.anserini.ltr.DocumentContext;
import io.anserini.ltr.DocumentFieldContext;
import io.anserini.ltr.FeatureExtractor;
import io.anserini.ltr.QueryContext;
import io.anserini.ltr.QueryFieldContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Counts occurrences of all pairs of query tokens
 */
public class OrderedQueryPairs implements FeatureExtractor {
  private String field;
  private String qfield;

  private int gapSize = 8;

  public OrderedQueryPairs() {
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
  }

  public OrderedQueryPairs(int gapSize) {
    this.gapSize = gapSize;
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
  }

  public OrderedQueryPairs(int gapSize, String field, String qfield) {
    this.gapSize = gapSize;
    this.field = field;
    this.qfield = qfield;
  }

  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    float count = 0;
    List<Pair<String, String>> queryPairs= queryFieldContext.genQueryPair();
    for(Pair<String, String> pair: queryPairs){
      count += context.countBigram(pair.getLeft(),pair.getRight(),gapSize);
    }
    return count;
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    return queryFieldContext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    String name = this.getClass().getSimpleName();
    return String.format("%s_%s_%s_%d", field, qfield, name, this.gapSize);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField() {
    return qfield;
  }

  @Override
  public FeatureExtractor clone() {
    return new OrderedQueryPairs(gapSize, field, qfield);
  }
}
