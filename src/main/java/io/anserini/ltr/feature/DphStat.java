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

import java.util.ArrayList;
import java.util.List;

/**
 * This feature computes collection query similarity, DPH defined as
 * tf*log(p/P(t))+0.5log(2pi*tf*(1-p)) found on page 18 of Giambattista Amati, Frequentist and
 * Bayesian Approach to Information Retrieval
 * p is the relative term-frequency in the collection, P(t) = TF/TFC where
 * TF is the number of tokens of that term in the collection
 * TFC is the overall number of tokens in the collection
 */
 
public class DphStat implements FeatureExtractor {
    private String field;
    private String qfield;
    Pooler collectFun;

    public DphStat(Pooler collectFun) {
        this.field = Constants.CONTENTS;
        this.qfield = "analyzed";
        this.collectFun = collectFun;
    }

    public DphStat(Pooler collectFun, String field, String qfield) {
        this.field = field;
        this.qfield = qfield;
        this.collectFun = collectFun;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        DocumentFieldContext context = documentContext.fieldContexts.get(field);
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        long docSize = context.docSize;
        long totalTermFreq = context.totalTermFreq;
        List<Float> score = new ArrayList<>();

        for (String queryToken : queryFieldContext.queryTokens) {
            long termFreq = context.getTermFreq(queryToken);
            if(termFreq==0) continue;
            double collectionFreqs = context.getCollectionFreq(queryToken);
            double relativeFreq = (double)termFreq/docSize;
            if (relativeFreq == 1d) { // to fix bug if relativeFreq is 1, score is NaN
                relativeFreq = 0.99;
            }
            double norm = (1d-relativeFreq) * (1d -relativeFreq)/(termFreq+1d);
            double Pt = collectionFreqs/totalTermFreq;
            score.add((float) (norm * (termFreq* Math.log((relativeFreq/Pt)) +
                                0.5d * Math.log(2.0 * Math.PI * termFreq * (1d - relativeFreq)))));
        }
        return collectFun.pool(score);
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        return String.format("%s_%s_%s_%s", field, qfield, name, collectFun.getName());
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
        Pooler newFun = collectFun.clone();
        return new DphStat(newFun, field, qfield);
    }
}
