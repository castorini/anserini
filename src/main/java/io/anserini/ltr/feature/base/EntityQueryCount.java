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

package io.anserini.ltr.feature.base;

import io.anserini.ltr.feature.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityQueryCount implements FeatureExtractor {
    private String qfield;
    private String field;
    private String type;

    public EntityQueryCount(String type) {
        this.qfield = null;
        this.field = null;
        this.type = type;
    }

    public float entityCounts(QueryContext queryContext, String ent) {
        float ret = 0.0f;
        if (queryContext.queryEntities.containsKey(ent)) {
            ret += queryContext.queryEntities.get(ent).size();
        }
        return ret;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) {
        float score = 0.0f;
        score += entityCounts(queryContext, this.type);
        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return 0.0f;
        //todo discuss what if we don't have qfield
    }

    @Override
    public String getName() {
        return String.format("EntityQueryCount_%s", type);
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
        return new EntityQueryCount(type);
    }
}
