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
 * This is a feature extractor that will calculate the
 * unordered count of phrases in the window specified
 */
public class UnorderedSequentialPairs implements FeatureExtractor {
  private String field;
  private String qfield;

  private int gapSize = 8;

  public UnorderedSequentialPairs() {
    this.qfield = "analyzed";
    this.field = IndexArgs.CONTENTS;
  }

  public UnorderedSequentialPairs(int gapSize) {
    this.gapSize = gapSize;
    this.field = IndexArgs.CONTENTS;
    this.qfield = "analyzed";
  }

  public UnorderedSequentialPairs(int gapSize, String field, String qfield) {
    this.gapSize = gapSize;
    this.field = field;
    this.qfield = qfield;
  }

  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
    float count = 0;
    List<Pair<String, String>> queryPairs= queryFieldContext.genQueryBigram();
    for(Pair<String, String> pair: queryPairs){
      count += context.countBigram(pair.getLeft(),pair.getRight(),gapSize);
      count += context.countBigram(pair.getRight(),pair.getLeft(),gapSize);
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
    return String.format("%s_%s_UnorderedSequentialPairs_%d", field, qfield, this.gapSize);
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
    return new UnorderedSequentialPairs(gapSize, field, qfield);
  }
}
