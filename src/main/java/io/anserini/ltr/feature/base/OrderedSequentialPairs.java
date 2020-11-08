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
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * This feature extractor will return the number of phrases
 * in a specified gap size
 */
public class OrderedSequentialPairs implements FeatureExtractor {

  private String field;

  private int gapSize = 8;

  public OrderedSequentialPairs() {
    this.field = IndexArgs.CONTENTS;
  }

  public OrderedSequentialPairs(int gapSize) {
    this.gapSize = gapSize;
    this.field = IndexArgs.CONTENTS;
  }

  public OrderedSequentialPairs(int gapSize, String field) {
    this.gapSize = gapSize;
    this.field = field;
  }

  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    FieldContext context = documentContext.fieldContexts.get(field);
    float count = 0;
    List<Pair<String, String>> queryPairs= queryContext.genQueryBigram();
    for(Pair<String, String> pair: queryPairs){
      count += context.countBigram(pair.getLeft(),pair.getRight(),gapSize);
    }
    return count;
  }

  @Override
  public String getName() {
    return String.format("%s_OrderedSequentialPairs_%d", field, this.gapSize);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public FeatureExtractor clone() {
    return new OrderedSequentialPairs(gapSize, field);
  }
}
