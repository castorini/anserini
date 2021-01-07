package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityHowMany implements FeatureExtractor {
    private String qfield;
    private String field;

    public EntityHowMany() {
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
        String raw = queryContext.raw.toLowerCase().trim();
        if (raw.contains("how many")){
            score = 0.0f;
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
        return String.format("EntityHowMany");
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
        return new EntityHowMany();
    }
}
