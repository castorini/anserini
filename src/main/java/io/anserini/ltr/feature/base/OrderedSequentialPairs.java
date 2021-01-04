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

package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * This feature extractor will return the number of phrases
 * in a specified gap size
 */
public class OrderedSequentialPairs implements FeatureExtractor {

  private String field;
  private String qfield;

  private int gapSize = 8;

  public OrderedSequentialPairs() {
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public OrderedSequentialPairs(int gapSize) {
    this.gapSize = gapSize;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public OrderedSequentialPairs(int gapSize, String field, String qfield) {
    this.gapSize = gapSize;
    this.field = field;
    this.qfield = qfield;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    float count = 0;
    List<Pair<String, String>> queryPairs= queryFieldContext.genQueryBigram();
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
    return String.format("%s_%s_OrderedSequentialPairs_%d", field, qfield, this.gapSize);
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
    return new OrderedSequentialPairs(gapSize, field, qfield);
  }
}
