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
import io.anserini.ltr.Stopwords;
import io.anserini.ltr.feature.*;

import java.util.List;

/**
 * StopCover
 * Compute the ratio of stop/non-stop.
 */
public class StopRatio implements FeatureExtractor {
    private String field;
    private String qfield = "analyzed";

    public StopRatio(String field) {
        this.field = field;
    }

    public StopRatio() {
        this.field = IndexArgs.CONTENTS;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        DocumentFieldContext context = documentContext.fieldContexts.get(field);
        long cov = 0l;
        float score = 0.0f;
        List<String> stopWords = Stopwords.getStopWordList();
        for (String stopWord : stopWords) {
            if (context.termFreqs.containsKey(stopWord)) {
                cov += context.termFreqs.get(stopWord);
            }
        }
        long nonStop = context.docSize - cov;
        if(nonStop != 0) {
            score = cov / nonStop;
        }
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_%s_StopRatio", field, qfield);
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
        return new StopRatio();
    }
}

