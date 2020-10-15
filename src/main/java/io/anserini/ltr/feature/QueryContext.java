package io.anserini.ltr.feature;

import java.util.List;
import java.util.Map;

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
}
