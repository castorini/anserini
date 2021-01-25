package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;

import java.util.List;
import java.util.regex.Pattern;

public class EntityWhoMatch implements FeatureExtractor  {
    private String qfield;
    private String field;

    public EntityWhoMatch() {
        this.qfield = null;
        this.field = null;
    }

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    public static double similarity(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 0.0; /* both strings are zero length */ }
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    public static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0) {
                    costs[j] = j;
                }
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1)) {
                            newValue = Math.min(Math.min(newValue, lastValue),
                                    costs[j]) + 1;
                        }
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0) {
                costs[s2.length()] = lastValue;
            }
        }
        return costs[s2.length()];
    }

    public float matchCounts(QueryContext queryContext, DocumentContext documentContext, String ent){
        float ret = 0.0f;
        if (queryContext.queryEntities.containsKey(ent)) {
            if (documentContext.entities.containsKey(ent)) {
                for (String text : queryContext.queryEntities.get(ent)) {
                    for (String docText: documentContext.entities.get(ent)) {
                        double sim = similarity(docText, text);
                        if (sim>=0.75){
                            ret += 1;
                        }
                    }
                }
            }
        }
        return ret;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = -1.0f;
        String raw = queryContext.raw.toLowerCase().trim();
        if (raw.contains("who")) {
            score = 0.0f;
            score += matchCounts(queryContext,documentContext,"PERSON");
        }

        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        //QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        //return queryFieldContext.getSelfLog(context.docId, getName());
        //discuss what if we don't have qfield
        return 0.0f;
    }

    @Override
    public String getName() {
        return String.format("EntityWhoMatch");
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
        return new EntityWhoMatch();
    }
}
