package io.anserini.ltr.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private String entityJson;
    public Map<String, String> entities;

    public QueryContext(String qid, Set<String> fieldsToLoad, JsonNode root) throws JsonProcessingException {
        this.qid = qid;
        this.fieldsToLoad = fieldsToLoad;
        fieldContexts = new HashMap<>();
        for(String fieldName: fieldsToLoad)
            fieldContexts.put(fieldName, new QueryFieldContext(fieldName, root));

        this.entityJson = null;
        this.entities = new HashMap<>();

        entityJson = root.get("entity").asText();
        ObjectMapper mapper = new ObjectMapper();
        JsonNode entityRoot = mapper.readValue(entityJson, JsonNode.class);
        for (Iterator<Map.Entry<String, JsonNode>> it = entityRoot.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> entity = it.next();
            String text = entity.getKey();
            String type = entity.getValue().asText();
            entities.put(text, type);
        }
    }
}
