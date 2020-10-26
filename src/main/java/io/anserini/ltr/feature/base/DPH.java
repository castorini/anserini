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
import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import io.anserini.rerank.RerankerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiTerms;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.Terms;
import org.apache.lucene.index.TermsEnum;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This feature computes collection query similarity, DPH defined as
 * tf*log(p/P(t))+0.5log(2pi*tf*(1-p)) found on page 18 of Giambattista Amati, Frequentist and
 * Bayesian Approach to Information Retrieval
 * p is the relative term-frequency in the collection, P(t) = TF/TFC where
 * TF is the number of tokens of that term in the collection
 * TFC is the overall number of tokens in the collection
 */
 
public class DPH implements FeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(DPH.class);


    @Override
    public float extract(ContentContext context, QueryContext queryContext) {
        long numDocs = context.numDocs;
        long docSize = context.docSize;
        long totalTermFreq = context.totalTermFreq;
        float score = 0;

        for (String queryToken : queryContext.queryTokens) {
            int docFreq = context.getDocFreq(queryToken);
            long termFreq = context.getTermFreq(queryToken);
            double collectionFreqs = context.getCollectionFreq(queryToken);
            double relativeFreq = termFreq/docSize;
            double norm = (1d-relativeFreq) * (1d -relativeFreq)/(termFreq+1d);
            double Pt = collectionFreqs/totalTermFreq
            score += norm * (termFreq* Math.log((relativeFreq/Pt)) +
                    0.5d * Math.log(2.0 * Math.PI * termFreq * (1d - relativeFreq)));
            termFreq/docSize / (collectionFreqs/totalTermFreq)
        }
        return score;
    }

    @Override
    public String getName() {
        return String.format("DPH");
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public FeatureExtractor clone() {
        return new DPH();
    }
}
