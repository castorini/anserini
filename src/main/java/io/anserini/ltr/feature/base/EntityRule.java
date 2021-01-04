package io.anserini.ltr.feature.base;

import io.anserini.index.IndexArgs;
import io.anserini.ltr.feature.*;

import java.util.List;

public class EntityRule implements FeatureExtractor {
    private String field;
    private String qfield;

    public EntityRule() {
        this.field = "contents";
        this.qfield = "analyzed";
    }

    public EntityRule(String field, String qfield) {
        this.field = field;
        this.qfield = qfield;
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
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        float score = 0.0f;
        if (queryFieldContext.queryTokens.get(0).equals("how")){
            // how long, how many, how much
            if (queryFieldContext.queryTokens.get(1).equals("long")){
                score += entityCounts(documentContext, "DATE");
                score += entityCounts(documentContext, "TIME");
                score += entityCounts(documentContext, "CARDINAL");
            } else if (queryFieldContext.queryTokens.get(1).equals("much")){
                score += entityCounts(documentContext, "QUANTITY");
                score += entityCounts(documentContext, "MONEY");
            } else if (queryFieldContext.queryTokens.get(1).equals("mani")){
                if (queryContext.queryEntities.containsKey("DATE")) {
                    score += entityCounts(documentContext, "DATE");
                }
                score += entityCounts(documentContext, "QUANTITY");
                score += entityCounts(documentContext, "CARDINAL");
            }
        } else if (queryFieldContext.queryTokens.get(0).equals("where")){
            // where general counts and specific matching counts
            score += entityCounts(documentContext, "FAC");
            score += entityCounts(documentContext, "ORG");
            score += entityCounts(documentContext, "GPE");
            score += matchCounts(queryContext,documentContext,"FAC");
            score += matchCounts(queryContext,documentContext,"ORG");
            score += matchCounts(queryContext,documentContext,"GPE");

        } else if (queryFieldContext.queryTokens.get(0).equals("when")){
            score += entityCounts(documentContext, "DATE");
            score += entityCounts(documentContext, "TIME");
        } else if (queryFieldContext.queryTokens.get(0).equals("who")){
            score += entityCounts(documentContext, "PERSON");
            score += matchCounts(queryContext,documentContext,"PERSON");
        }

        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getName() {
        return String.format("%s_%s_EntityRule", field, qfield);
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
        return new EntityRule(field, qfield);
    }
}
