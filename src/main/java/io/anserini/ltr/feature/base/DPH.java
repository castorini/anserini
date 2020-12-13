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

/**
 * This feature computes collection query similarity, DPH defined as
 * tf*log(p/P(t))+0.5log(2pi*tf*(1-p)) found on page 18 of Giambattista Amati, Frequentist and
 * Bayesian Approach to Information Retrieval
 * p is the relative term-frequency in the collection, P(t) = TF/TFC where
 * TF is the number of tokens of that term in the collection
 * TFC is the overall number of tokens in the collection
 */
 
public class DPH implements FeatureExtractor {
    private String field;

    public DPH() { this.field = IndexArgs.CONTENTS; }

    public DPH(String field) { this.field = field; }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        FieldContext context = documentContext.fieldContexts.get(field);
        long docSize = context.docSize;
        long totalTermFreq = context.totalTermFreq;
        float score = 0;

        for (String queryToken : queryContext.queryTokens) {
            long termFreq = context.getTermFreq(queryToken);
            //todo need discuss this
            if(termFreq==0) continue;
            double collectionFreqs = context.getCollectionFreq(queryToken);
            double relativeFreq = (double)termFreq/docSize;
            double norm = (1d-relativeFreq) * (1d -relativeFreq)/(termFreq+1d);
            double Pt = collectionFreqs/totalTermFreq;
            score += norm * (termFreq* Math.log((relativeFreq/Pt)) +
                    0.5d * Math.log(2.0 * Math.PI * termFreq * (1d - relativeFreq)));
        }
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return queryContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_DPH", field);
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public FeatureExtractor clone() {
        return new DPH(field);
    }
}
