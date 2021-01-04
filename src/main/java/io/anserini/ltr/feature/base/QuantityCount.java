package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

public class QuantityCount implements FeatureExtractor {
    private String field;
    private String qfield;

    public QuantityCount() {
        this.field = "entity";
        this.qfield = "raw";
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
//        //call raw
//        String entityJson = documentContext.entityJson;
//        //FieldContext context = documentContext.get("entity");
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
//        QueryFieldContext entityQuery = queryContext.fieldContexts.get("entity");
        float score = 0.0f;
//        //position check
//        if (queryFieldContext.queryTokens.get(1) == "how"){
//            if (queryFieldContext.queryTokens.get(2) == "long"){
//
//            } else if (queryFieldContext.queryTokens.get(2) == "much"){
//
//            }
//        }

        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_%s_QAHow", field, qfield);
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
        return new QuantityCount();
    }
}
