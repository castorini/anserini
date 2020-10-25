package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.ContentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Proximity implements FeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(Proximity.class);

    @Override
    public float extract(ContentContext context, QueryContext queryContext) {
        float score = 0.0f;
        /* Store condensed direct file */
        List<Pair<String, Integer>> cdf = new ArrayList<>();
        int s = 0;
        for (String queryToken : queryContext.queryTokens) {
            if (context.termFreqs.containsKey(queryToken)) {
                ++s;
                List<Integer> termPos = context.termPositions.get(queryToken);
                for (Integer pos : termPos) {
                    cdf.add(Pair.of(queryToken, pos));
                }
            }
        }
        if (s<2) return score;
        /* sort cdf by positions in the doc*/
        Collections.sort(cdf, new Comparator<>() {
            @Override
            public int compare(final Pair<String, Integer> lhs, final Pair<String, Integer> rhs) {
                return lhs.getValue() - rhs.getValue();
            }
        });

        int window = 8;
        int k1 = 90;
        int b = 40;
        double epsilon = 1e-6;
        /* bigram scan */
        for (int i=0; i< cdf.size()-1; ++i){
            for (int j = i+1; j < cdf.size(); i=j++){
                Pair<String, Integer> lhs = cdf.get(i);
                Pair<String, Integer> rhs = cdf.get(j);
                int dist = rhs.getValue() - lhs.getValue();
                /* skip if same term for bigram */
                if (rhs.getKey() == lhs.getKey()) continue;
                if(dist > 0 && dist <= window){
                    int queryFreq = queryContext.queryFreqs.get(lhs.getKey());
                    float idf = (float) Math.max(epsilon,Math.log(context.numDocs/context.getDocFreq(lhs.getKey()))*queryFreq);
                    float avgDocLen = context.totalTermFreq / context.numDocs;
                    float tf = k1*((1-b) + (b*(context.docSize / avgDocLen)));
                    score += ((k1+1)*context.getTermFreq(lhs.getKey()) / (tf + context.getTermFreq(lhs.getKey()))) * idf;

                    float queryFreqB = queryContext.queryFreqs.get(rhs.getKey());
                    float idfB = (float)Math.max(epsilon, Math.log(context.numDocs/context.getDocFreq(rhs.getKey()))*queryFreqB);
                    score += ((k1+1)*context.getTermFreq(rhs.getKey()) / (tf + context.getTermFreq(rhs.getKey()))) * idfB;
                }
            }
        }

        return score;
    }

    @Override
    public String getName() {
        return "Proximity";
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public FeatureExtractor clone() {
        return new Proximity();
    }
}