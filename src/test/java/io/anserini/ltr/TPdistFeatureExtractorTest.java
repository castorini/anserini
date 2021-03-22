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
import io.anserini.ltr.feature.base.tpDist;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;


/** test on TFIDF */
public class TPdistFeatureExtractorTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new tpDist();
    /*
        (1+log(termFreq)) * log(numDocs/docFreq)
    */
    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        float[] expected = {0.0f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    public void testDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case this is to test tp score";
        String queryText = "document case";
        float[] expected = {0.0f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }
}
