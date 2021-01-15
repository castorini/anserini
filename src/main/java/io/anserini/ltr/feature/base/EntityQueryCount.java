package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityQueryCount implements FeatureExtractor {
    private String qfield;
    private String field;
    private String type;

    public EntityQueryCount(String type) {
        this.qfield = null;
        this.field = null;
        this.type = type;
    }

    public float entityCounts(QueryContext queryContext, String ent) {
        float ret = 0.0f;
        if (queryContext.queryEntities.containsKey(ent)) {
            ret += queryContext.queryEntities.get(ent).size();
        }
        return ret;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = 0.0f;
        score += entityCounts(queryContext, this.type);
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return 0.0f;
        //todo discuss what if we don't have qfield
    }

    @Override
    public String getName() {
        return String.format("EntityQueryCount_%s", type);
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
        return new EntityQueryCount(type);
    }
}
