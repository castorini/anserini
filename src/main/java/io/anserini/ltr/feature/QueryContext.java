package io.anserini.ltr.feature;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class QueryContext {
    public String queryText;
    public List<String> queryTokens;
    public int querySize;

    //maybe put pre-retrieval feature here
    public Map<String, Float> cache;
    public QueryContext(List<String> queryTokens){
        this.queryTokens = queryTokens;
        this.queryText = String.join(",", queryTokens);
        this.querySize = queryTokens.size();
    }

    public List<Pair<String, String>> genQueryPair() {
        List<Pair<String, String>> queryPairs = new ArrayList<>();
        for (int i = 0; i < queryTokens.size() - 1; i++) {
            for (int j = i +1; j < queryTokens.size(); j++) {
                queryPairs.add(Pair.of(queryTokens.get(i),queryTokens.get(j)));
            }
        }
        return queryPairs;
    }

    public List<Pair<String, String>> genQueryBigram() {
        List<Pair<String, String>> queryBigram = new ArrayList<>();
        for (int i = 0; i < queryTokens.size() - 1; i++) {
            queryBigram.add(Pair.of(queryTokens.get(i),queryTokens.get(i+1)));
        }
        return queryBigram;
    }



}
