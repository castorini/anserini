/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import io.anserini.ltr.feature.NormalizedTfStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on NTF */
public class NormalizedTfTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new NormalizedTfStat(new SumPooler());
    /* termFreq == 0 -> docsize/0.5 */
    /* termFreq != 0 -> docsize/tf */
    /* sum of log() */
    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        // log(4/1)
        float[] expected = {1.39f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultiDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test", "terms tokens test test", "another document");
        String queryText = "test";
        //log(4/2)
        float[] expected = {0.69f};

        assertFeatureValues(expected, queryText, docs, EXTRACTOR, 2);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case multiple query tokens test";
        String queryText = "test query present";
        /*
        test: log(8/2)
        query: log(8/1)
        present:log(8/0.5)
         */

        float[] expected = {6.24f};
        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test", "terms tokens test test", "another document");
        String queryText = "test query tokens present";
        /*
        test: log(4/2)
        query: log(4/0.5)
        tokens:log(4/1)
        present: log(4/0.5)
         */

        float[] expected = {6.24f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,2);
    }

}

