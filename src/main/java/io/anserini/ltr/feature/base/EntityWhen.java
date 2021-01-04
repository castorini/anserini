package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityWhen implements FeatureExtractor {
    private String qfield;
    private String field;

    public EntityWhen() {
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
        if (Pattern.matches("^when.*$", raw)) {
            score = 0.0f;
            score += entityCounts(documentContext, "DATE");
            score += entityCounts(documentContext, "TIME");
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
        return String.format("EntityWhen");
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
        return new EntityWhen();
    }
}


