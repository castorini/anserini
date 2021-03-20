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

package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.SumPooler;
import io.anserini.ltr.feature.base.tfIdfStat;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on TFIDF */
public class TFIDFFeatureExtractorTest extends BaseFeatureExtractorTest<Integer>{
    private static final FeatureExtractor EXTRACTOR = new tfIdfStat(new SumPooler());

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        //df, tf =1, avgFL = 4, numDocs = 1
        //idf = log(1 + (0.5 / 1 + 0.5)) = 0.287682

        // 0.287682* 1.9 / (1 + 0.9 * (0.6 + 0.4 * (4/4))) = 1 * 0.287682
        // 0.287682 * 2.25 / (1 + 1.25 *(0.25 + 0.75)) = 0.287682
        float[] expected = {0.287682f,0.287682f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);

    }
}
