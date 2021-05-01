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

import io.anserini.ltr.feature.ProbalitySum;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ProbabilitySumTest extends BaseFeatureExtractorTest<Integer> {

    private FeatureExtractor EXTRACTOR = new ProbalitySum();
    /*
    sum of termFreq(each query token)/docSize
     */

    @Test
    public void testAllMissing() throws IOException, ExecutionException, InterruptedException {
        float[] expected = {0};
        assertFeatureValues(expected, "nothing", "document test missing all", EXTRACTOR);
    }

    @Test
    public void testSingleTermDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document document document another";
        String testQuery = "document";
        // 3/4
        float[] expected = {0.75f};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testMissingTermDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document test simple tokens";
        String testQuery = "simple missing";
        // 1/4
        float[] expected = {0.25f};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testMultipleTermsDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document multiple document term document multiple some missing";
        String testQuery = "document multiple missing";
        //3/8 + 2/8+ 1/8
        float[] expected = {0.75f};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testDoubleQueryTermsDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document multiple document term document multiple some missing";
        String testQuery = "document document double";
        //3/8 + 3/8+ 0
        float[] expected = {0.75f};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testTermFrequencyWithMultipleDocs() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document", "document with multiple terms",
                "document test case", "test terms tokens", "another test document");
        // We want to test that the expected value of count 1 is found for document
        // at index 2
        // 1/3
        String queryText = "document";
        float[] expected = {0.33f};

        assertFeatureValues(expected, queryText, docs, EXTRACTOR, 2);
    }
}

