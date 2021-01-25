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

import io.anserini.ltr.FeatureExtractorUtils;
import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import io.anserini.ltr.feature.QueryFieldContext;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 */
public class RunList implements FeatureExtractor {
    private String qfield = "analyzed";
    private ConcurrentHashMap<Pair<String, String>, Pair<Integer, Float>> lookup = new ConcurrentHashMap<>();
    private String tag;

    public RunList(String file, String tag) throws IOException {
        this.tag = tag;
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while(line != null) {
            String[] elements = line.split("\\s");
            String qid = elements[0];
            String docid = elements[2];
            int rank = Integer.parseInt(elements[3]);
            float score = Float.parseFloat(elements[4]);
            lookup.put(Pair.of(qid,docid),Pair.of(rank,score));
            line = reader.readLine();
        }
        reader.close();
    }

    public RunList(String tag, ConcurrentHashMap<Pair<String, String>, Pair<Integer, Float>> lookup) {
        this.lookup = lookup;
        this.tag = tag;
    }

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) throws IOException {
        if(lookup.containsKey(Pair.of(queryContext.qid, documentContext.docId)))
            return lookup.get(Pair.of(queryContext.qid, documentContext.docId)).getRight();
        else
            throw new IOException(String.format("Query Id %s Document Id %s expected but not found in file", queryContext.qid, documentContext.docId));
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        QueryFieldContext queryFieldContext = queryContext.fieldContexts.get(qfield);
        return queryFieldContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getField() {
        return null;
    }

    @Override
    public String getQField() {
        return qfield;
    }

    @Override
    public String getName() {
        return this.tag;
    }

    @Override
    public FeatureExtractor clone() {
        return new RunList(this.tag, this.lookup);
    }
}

