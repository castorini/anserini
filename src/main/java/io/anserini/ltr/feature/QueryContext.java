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
    public Map<String, List<String>> queryEntities;

    public QueryContext(String qid, Set<String> fieldsToLoad, JsonNode root) throws JsonProcessingException {
        this.qid = qid;
        this.fieldsToLoad = fieldsToLoad;
        fieldContexts = new HashMap<>();
        queryEntities = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for(String fieldName: fieldsToLoad)
            fieldContexts.put(fieldName, new QueryFieldContext(fieldName, root));
        String entityJson = root.get("entity").asText();
        JsonNode node = mapper.readValue(entityJson, JsonNode.class);
        Iterator<Map.Entry<String, JsonNode>> ents = node.fields();
        while(ents.hasNext()) {
            Map.Entry<String, JsonNode> ent = ents.next();
            String entText = ent.getKey();
            String nameEnt = ent.getValue().asText();
            List<String> temp;
            if (queryEntities.containsKey(nameEnt)) {
                temp = queryEntities.get(nameEnt);
            } else {
                temp = new ArrayList<>();
            }
            temp.add(entText);
            queryEntities.put(nameEnt, temp);
        }
    }
}
