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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class BM25HMean implements FeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(BM25HMean.class);
    // Default values, could be changed
    private double k1 = 0.9;
    private double b = 0.4;
    private String field;
    Pooler collectFun;
    public BM25HMean(Pooler collectFun) {
        this.field = IndexArgs.CONTENTS;
        this.collectFun = collectFun;
    }

    public BM25HMean(double k, double b, Pooler collectFun) {
        this.k1 = k;
        this.b = b;
        this.field = IndexArgs.CONTENTS;
        this.collectFun = collectFun;
    }

    public BM25HMean(double k, double b, String field, Pooler collectFun) {
        this.k1 = k;
        this.b = b;
        this.field = field;
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
        FieldContext context = documentContext.fieldContexts.get(field);
        List<Float> scores = context.generateBM25HMean(queryContext.queryTokens,k1,b);
        return collectFun.pool(scores);
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return queryContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_BM25_HMean_k1_%.2f_b_%.2f_%s",field, k1, b, collectFun.getName());
    }

    @Override
    public String getField() {
        return field;
    }

    public double getK1() {
        return k1;
    }

    public double getB() {
        return b;
    }

    @Override
    public FeatureExtractor clone() {
        Pooler newFun = collectFun.clone();
        return new BM25HMean(k1, b, field, newFun);
    }

}