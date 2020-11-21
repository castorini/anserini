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

import io.anserini.ltr.feature.DocumentContext;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.QueryContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 *
 */
public class BERT implements FeatureExtractor {
    private static final Logger LOG = LogManager.getLogger(BERT.class);

    private String field;

    private String fileDir = "src/main/java/io/anserini/ltr/feature/base/run.monobert.LTR_entire.dev.trec";

    public BERT() {}

    @Override
    public float extract(DocumentContext documentContext, QueryContext queryContext) throws FileNotFoundException {
        float score = 0.0f;
        String qid = queryContext.qid;
        String docid = documentContext.docId;
        File bert = new File(fileDir);
        Scanner reader = new Scanner(bert);
        while (reader.hasNextLine()) {
            List<String> rank = Arrays.asList(reader.nextLine().split(" "));
            String fqid = rank.get(0);
            String fdocid = rank.get(2);
            if ((fqid == qid) && (fdocid == docid)) {
                score = Float.parseFloat(rank.get(4));
                break;
            }
        }
        reader.close();

        return score;
    }

    @Override
    public float postEdit(DocumentContext context, QueryContext queryContext) {
        return queryContext.getSelfLog(context.docId, getName());
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getName() { return "BERT";}

    @Override
    public FeatureExtractor clone() {
        return new BERT();
    }
}

