package io.anserini.ltr.feature;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.util.*;

public class QueryContext {
    public String qid;
    public Map<String, QueryFieldContext> fieldContexts;
    private Set<String> fieldsToLoad; // analyzed, text, text_unlemm, text_bert_tok

    public QueryContext(String qid, Set<String> fieldsToLoad, JsonNode root){
        this.qid = qid;
        this.fieldsToLoad = fieldsToLoad;
        fieldContexts = new HashMap<>();
        for(String fieldName: fieldsToLoad)
            fieldContexts.put(fieldName, new QueryFieldContext(fieldName, root));
    }
}
