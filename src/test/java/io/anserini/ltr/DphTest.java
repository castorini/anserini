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

import io.anserini.ltr.feature.DphStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DphTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new DphStat(new SumPooler());
    /*
       (1-tf/docSize) * (1-tf/docSize)/(tf+1) * (termFreq* log((tf/docSize/(collectionFreqs/totalTermFreq)))
       + 0.5 * log(2.0 * pi * tf * (1- tf/docSize))
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        float[] expected = {0.22f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";

        float[] expected = {0.44f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";
        float[] expected = {0.4f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another document");
        String queryText = "test";
        /*
        log(14/7)
         */
        float[] expected = {0.04f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        float[] expected = {0.16f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}


