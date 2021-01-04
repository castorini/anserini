package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityHowRule implements FeatureExtractor {
    private String qfield;
    private String field;

    public EntityHowRule() {
        this.qfield = null;
        this.field = null;
    }

    public float entityCounts(DocumentContext documentContext, String ent) {
        float ret = 0.0f;
        if (documentContext.entities.containsKey(ent)) {
            ret += documentContext.entities.get(ent).size();
        }
        return ret;
    }
    public float matchCounts(QueryContext queryContext, DocumentContext documentContext, String ent){
        float ret = 0.0f;
        if (queryContext.queryEntities.containsKey(ent)) {
            if (documentContext.entities.containsKey(ent)) {
                List<String> docEnts = documentContext.entities.get(ent);
                for (String text : queryContext.queryEntities.get(ent)) {
                    for (String docText:docEnts) {
                        if (docText.equals(text)){
                            ret += 1;
                        } } } } }
        return ret;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = 0.0f;
        String raw = queryContext.raw;
        if (Pattern.matches("^How long.*$", raw)){
            score += entityCounts(documentContext, "DATE");
            score += entityCounts(documentContext, "TIME");
            score += entityCounts(documentContext, "CARDINAL");
        } else if (Pattern.matches("^How much.*$", raw)) {
                score += entityCounts(documentContext, "QUANTITY");
                score += entityCounts(documentContext, "MONEY");
        } else if (Pattern.matches("^How many.*$", raw)){
            if (queryContext.queryEntities.containsKey("DATE")) {
                // how many days
                score += entityCounts(documentContext, "DATE");
            }
            score += entityCounts(documentContext, "QUANTITY");
            score += entityCounts(documentContext, "CARDINAL");
        }

        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return 0.0f;
        //todo discuss what if we don't have qfield
    }

    @Override
    public String getName() {
        return String.format("EntityHowRule");
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public String getQField() {
        return null;
    }

    @Override
    public FeatureExtractor clone() {
        return new EntityHowRule();
    }
}
