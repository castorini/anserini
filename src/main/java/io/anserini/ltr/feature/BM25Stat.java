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
import io.anserini.ltr.Pooler;
import io.anserini.ltr.QueryContext;
import io.anserini.ltr.QueryFieldContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * This feature extractor will compute BM25 score as according to Lucene 5.3 documentation
 * The formula is the same, but the computation of docSize is slightly different,
 * Lucene uses the norm value encoded in the index, we are calculating it as is
 * also we do not have any boosting, the field norm is also not available
 */
public class BM25Stat implements FeatureExtractor {
  private static final Logger LOG = LogManager.getLogger(BM25Stat.class);

  // Default values, could be changed
  private double k1 = 0.9;
  private double b = 0.4;
  private String field;
  private String qfield;
  Pooler collectFun;

  public BM25Stat(Pooler collectFun) {
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public BM25Stat(Pooler collectFun, double k, double b) {
    this.k1 = k;
    this.b = b;
    this.field = Constants.CONTENTS;
    this.qfield = "analyzed";
    this.collectFun = collectFun;
  }

  public BM25Stat(Pooler collectFun, double k, double b, String field, String qfield) {
    this.k1 = k;
    this.b = b;
    this.field = field;
    this.qfield = qfield;
    this.collectFun = collectFun;
  }

  /**
   * We will implement this according to the Lucene specification
   * the formula used:
   * sum ( IDF(qi) * (df(qi,D) * (k+1)) / (df(qi,D) + k * (1-b + b*|D| / avgFL))
   * IDF and avgFL computation are described above.
   */
  @Override
  public float extract(DocumentContext documentContext, QueryContext queryContext) {
    DocumentFieldContext context = documentContext.fieldContexts.get(field);
    QueryFieldContext qcontext = queryContext.fieldContexts.get(qfield);
    long numDocs = context.numDocs;
    long docSize = context.docSize;
    long totalTermFreq = context.totalTermFreq;
    double avgFL = (double)totalTermFreq/numDocs;
    List<Float> score = new ArrayList<>();


    for (String queryToken : qcontext.queryTokens) {
        int docFreq = context.getDocFreq(queryToken);
        long termFreq = context.getTermFreq(queryToken);
        double numerator = (this.k1 + 1) * termFreq;
        double docLengthFactor = this.b * (docSize / avgFL);
        double denominator = termFreq + (this.k1) * (1 - this.b + docLengthFactor);
        double idf = Math.log(1 + (numDocs - docFreq + 0.5d) / (docFreq + 0.5d));
        score.add((float)(idf * numerator / denominator));
    }
    return collectFun.pool(score);
  }

  @Override
  public float postEdit(DocumentContext context, QueryContext queryContext) {
    QueryFieldContext qcontext = queryContext.fieldContexts.get(qfield);
    return qcontext.getSelfLog(context.docId, getName());
  }

  @Override
  public String getName() {
    String name = this.getClass().getSimpleName();
    return String.format("%s_%s_%s_%s_k1_%.2f_b_%.2f",field, qfield, name, collectFun.getName(), k1, b);
  }

  @Override
  public String getField() {
    return field;
  }

  @Override
  public String getQField(){return qfield;}

  public double getK1() {
    return k1;
  }

  public double getB() {
    return b;
  }

  @Override
  public FeatureExtractor clone() {
    Pooler newFun = collectFun.clone();
    return new BM25Stat(newFun, k1, b, field, qfield);
  }
}
