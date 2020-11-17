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

import io.anserini.ltr.StopWords;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FieldContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

import java.util.List;
import java.util.Map;

/**
 * Entropy
 * Compute the entropy which is the sum of log(ratio of each term) * ratio of each term
 */
public class Entropy implements FeatureExtractor {
    private String field;

    public Entropy() { }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        FieldContext context = documentContext.fieldContexts.get(field);
        float score = 0.0f;
        for (Map.Entry<String, Long> entry : context.termFreqs.entrySet()) {
            String term = entry.getKey();
            Long freq = entry.getValue();
            float p = freq / context.docSize;
            score += p * Math.log(p);
        }
        if (score >0) {
            score = -score;
        }
        return score;
    }

    @Override
    public String getName() {
        return "Entropy";
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public FeatureExtractor clone() {
        return new Entropy();
    }
}
