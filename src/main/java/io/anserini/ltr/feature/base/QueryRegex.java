package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryRegex implements FeatureExtractor {
    private String qfield;
    private String field;
    private String regex;

    public QueryRegex(String regex) {
        this.qfield = null;
        this.field = null;
        this.regex = regex;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = 0.0f;
        String raw = queryContext.raw.toLowerCase().trim();
        if (Pattern.matches(regex, raw)){
            score = 1.0f;
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
        return String.format("QueryRegex_%s", regex);
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
        return new QueryRegex(regex);
    }
}
