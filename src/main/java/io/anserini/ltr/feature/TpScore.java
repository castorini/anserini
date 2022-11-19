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
import io.anserini.ltr.QueryContext;
import io.anserini.ltr.QueryFieldContext;
import io.anserini.ltr.SumPooler;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * This feature extractor will compute TPscore as according to INEX 2008
 * The score for a document d is computed by a variant of [2] that uses a linear combination of a standard
 * BM25-based score and a proximity score, which is itself computed by plugging
 * the accumulated proximity scores into a BM25-like scoring function:
 * To compute acc(d, t), we consider every query term occurrence.
 */
public class TpScore implements FeatureExtractor {
    private String field;
    private String qfield;

    public TpScore() {
        this.field = Constants.CONTENTS;
        this.qfield = "analyzed";
    }

    public TpScore(String field, String qfield) {
        this.field = field;
        this.qfield = qfield;
    }

    public static class BCTP {
        String id;
        double weight = 0.0;
        double accumulator = 0.0;
        double doc_count = 0;
     }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        DocumentFieldContext context = documentContext.fieldContexts.get(field);
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        //parameters for BM25
        double k1 = 0.9;
        double b = 0.4;
        long numDocs = context.numDocs;
        long docSize = context.docSize;
        long totalTermFreq = context.totalTermFreq;
        double avgFL = (double)totalTermFreq/numDocs;
        //firstly get the score from BM25
        BM25Stat bm25_score = new BM25Stat(new SumPooler(), k1, b, field, qfield);
        float score = bm25_score.extract(documentContext, queryContext);

        List<Pair<Integer, BCTP>> bctp_query = new ArrayList<>();
        //generate bctp_query which contains the position of specific term and some details of it
        for (String queryToken : queryFieldContext.queryTokens) {
            double collectionFreqs = context.getCollectionFreq(queryToken);
            BCTP t = new BCTP();
            t.id = queryToken;
            t.doc_count = collectionFreqs;
            if (context.termFreqs.containsKey(queryToken)) {
                List<Integer> termPos = context.termPositions.get(queryToken);
                for (Integer pos : termPos) {
                    bctp_query.add(Pair.of(pos,t));
                }
            }
        }
        /* sort bctp_query by positions in the doc*/
        bctp_query.sort(Comparator.comparingInt(Pair::getKey));

        if (bctp_query.size() <3|| docSize<bctp_query.size()) {
            return score;
        }
        //update the accumulation function of each BCTP
        score_terms(bctp_query, context);
        //calculate the tp score
        for (Pair<Integer,BCTP> pair: bctp_query ){
            BCTP value = pair.getValue();
            double weight = Math.min(1.0f, value.weight);
            double K = k1 * ((1 - b) + (b * (docSize / avgFL)));
            double x = value.accumulator * (1 + k1);
            double y = value.accumulator + K;

            score += weight * (x / y);
        }

        return score;
    }

    public void score_terms(List<Pair<Integer, BCTP>> bctp_query, DocumentFieldContext context) {
        long numDocs = context.numDocs;
//        long docSize = context.docSize;
        BCTP  curr_term;
        BCTP  prev_term;

        prev_term = bctp_query.get(0).getValue();
        int pos;
        int prev_pos;

        for (Pair<Integer,BCTP> pair: bctp_query ){
            pair.getValue().weight = rw_idf_weight(numDocs,pair.getValue().doc_count);
        }

        for (int i = 1; i < bctp_query.size(); i++) {
            curr_term = bctp_query.get(i).getValue();
            if (prev_term != null && !prev_term.id.equals(curr_term.id)) {
                    pos = bctp_query.get(i).getKey();
                    prev_pos = bctp_query.get(i-1).getKey();
                    curr_term.accumulator += prev_term.weight * Math.pow(pos - prev_pos, -2);
                    prev_term.accumulator += curr_term.weight * Math.pow(pos - prev_pos, -2);
            }
            prev_term = curr_term;
            }
    }

    public double rw_idf_weight(long numDocs,double num_doc_term) {
        if (num_doc_term == 0) {
            return 0;
        }
        return Math.log(numDocs / num_doc_term);
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        String name = this.getClass().getSimpleName();
        return String.format("%s_%s_%s", field, qfield,name);
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
        return new TpScore(field, qfield);
    }
}