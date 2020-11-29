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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Map;

public class BM25MeanMean implements FeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(BM25MeanMean.class);
    // Default values, could be changed
    private double k1 = 0.9;
    private double b = 0.4;
    private String field;

    public BM25MeanMean() { this.field = IndexArgs.CONTENTS; }

    public BM25MeanMean(double k, double b) {
        this.k1 = k;
        this.b = b;
        this.field = IndexArgs.CONTENTS;
    }

    public BM25MeanMean(double k, double b, String field) {
        this.k1 = k;
        this.b = b;
        this.field = field;
    }

    /**
     * We will implement this according to the Lucene specification
     * the formula used:
     * sum ( IDF(qi) * (df(qi,D) * (k+1)) / (df(qi,D) + k * (1-b + b*|D| / avgFL))
     * IDF and avgFL computation are described above.
     */
    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = 0.0f;
        FieldContext context = documentContext.fieldContexts.get(field);
        List<Float> scores = context.generateBM25Mean(queryContext.queryTokens,k1,b);
        for (int i = 0; i <scores.size(); ++i) {
            score += scores.get(i);
        }
        score = score / queryContext.querySize;
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return queryContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_BM25_Mean_Mean_k1_%.2f_b_%.2f",field, k1, b);
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
        return new BM25MeanMean(k1, b, field);
    }

}
