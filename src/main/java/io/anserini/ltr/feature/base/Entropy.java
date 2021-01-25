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

import java.util.Map;

/**
 * Entropy
 * Compute the entropy which is the sum of log(ratio of each term) * ratio of each term
 */
public class Entropy implements FeatureExtractor {
    private String field;
    private String qfield = "analyzed";

    public Entropy(String field) { this.field = field; }

    public Entropy() {
        this.field = IndexArgs.CONTENTS;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        DocumentFieldContext context = documentContext.fieldContexts.get(field);
        float score = 0.0f;
        for (Map.Entry<String, Long> entry : context.termFreqs.entrySet()) {
            Long freq = entry.getValue();
            //todo discuss if freq == 0, how to handle (special actions?)
            if(freq == 0) continue;
            float p = (1.0f*freq) / context.docSize;
            score += p * Math.log(p);
        }
        if (score >0) {
            score = -score;
        }
        assert score == score;
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_%s_Entropy", field, qfield);
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
        return new Entropy();
    }
}
