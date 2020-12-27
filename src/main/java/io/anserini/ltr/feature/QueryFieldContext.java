package io.anserini.ltr.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;
public class QueryFieldContext {
    private String fieldName;
    public List<String> queryTokens;
    public Map<String,Integer> queryFreqs;
    public int querySize;
    public Map<String, Float> cache;

    private Map<String, Map<String, Float>> featureLog;
    public QueryFieldContext(String fieldName, JsonNode root){
        this.fieldName = fieldName;
        ObjectMapper mapper = new ObjectMapper();
        this.queryTokens = mapper.convertValue(root.get(fieldName), ArrayList.class);
        this.querySize = queryTokens.size();
        this.queryFreqs = new HashMap<>();
        for (String token : queryTokens)
            queryFreqs.put(token, queryFreqs.getOrDefault(token,0)+1);
        this.cache = new HashMap<>();
        this.featureLog = new HashMap<>();
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

    public void logExtract(String docId, List<Float> features, List<String> featureName){
        assert featureName.size() == features.size();
        Map<String, Float> docFeature = new HashMap<>();
        for(int i=0; i<featureName.size(); i++){
            docFeature.put(featureName.get(i),features.get(i));
        }
        featureLog.put(docId, docFeature);
    }

    public float getSelfLog(String docId, String featureName) {
        return featureLog.get(docId).get(featureName);
    }

    public List<Float> getOthersLog(String docId, String featureName) {
        List<Float> others = new ArrayList<>();
        for(String otherDid: featureLog.keySet()){
            others.add(featureLog.get(otherDid).get(featureName));
        }
        return others;
    }
}
