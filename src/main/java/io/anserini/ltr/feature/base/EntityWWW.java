package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityWWW implements FeatureExtractor {

    public EntityWWW() {}

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
        if (Pattern.matches("^Where.*$", raw)) {
            // where general counts and specific matching counts
            score += entityCounts(documentContext, "FAC");
            score += entityCounts(documentContext, "ORG");
            score += entityCounts(documentContext, "GPE");
            score += matchCounts(queryContext,documentContext,"FAC");
            score += matchCounts(queryContext,documentContext,"ORG");
            score += matchCounts(queryContext,documentContext,"GPE");
        } else if (Pattern.matches("^When.*$", raw)) {
            score += entityCounts(documentContext, "DATE");
            score += entityCounts(documentContext, "TIME");
        } else if (Pattern.matches("^Who.*$", raw)) {
            score += entityCounts(documentContext, "PERSON");
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
        return String.format("EntityWWWRule");
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
        return new EntityWWW();
    }
}


