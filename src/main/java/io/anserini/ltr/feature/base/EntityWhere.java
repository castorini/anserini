package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityWhere implements FeatureExtractor {
    private String qfield;
    private String field;

    public EntityWhere() {
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

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = -1.0f;
        String raw = queryContext.raw.toLowerCase();
        if (Pattern.matches("^where.*$", raw)) {
            // where general counts and specific matching counts
            score = 0.0f;
            score += entityCounts(documentContext, "FAC");
            score += entityCounts(documentContext, "ORG");
            score += entityCounts(documentContext, "GPE");
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
        return String.format("EntityWhere");
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
        return new EntityWhere();
    }
}


