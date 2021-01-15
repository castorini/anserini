/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.ltr.feature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;

import java.util.*;
import java.util.regex.Pattern;

public class QueryContext {
    public String qid;
    public Map<String, QueryFieldContext> fieldContexts;
    private Set<String> fieldsToLoad; // analyzed, text, text_unlemm, text_bert_tok
    public Map<String, List<String>> queryEntities;
    public String raw;

    public QueryContext(String qid, Set<String> fieldsToLoad, JsonNode root) throws JsonProcessingException {
        this.qid = qid;
        this.fieldsToLoad = fieldsToLoad;
        fieldContexts = new HashMap<>();
        queryEntities = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        for(String fieldName: fieldsToLoad)
            fieldContexts.put(fieldName, new QueryFieldContext(fieldName, root));
        if (root.has("entity")) {
            String entityJson = root.get("entity").asText();
            JsonNode node = mapper.readValue(entityJson, JsonNode.class);
            Iterator<Map.Entry<String, JsonNode>> ents = node.fields();
            while (ents.hasNext()) {
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
        if (root.has("raw")) {
            raw = root.get("raw").asText();
        } else {
            raw = "";
        }
    }
}
