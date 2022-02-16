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

import io.anserini.ltr.feature.TfIdfStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on TFIDF */
public class TfIdfStatTest extends BaseFeatureExtractorTest<Integer>{
    private static final FeatureExtractor EXTRACTOR = new TfIdfStat(new SumPooler());
    /*
        (1+log(termFreq)) * log(numDocs/docFreq)
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        /*
        (1+log(1)) * log(1)
         */
        float[] expected = {0.0f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";
        /*
        (1+log(1)) * log(1) + 0 + 0 because only one doc, docFreq is 0

         */
        float[] expected = {0.0f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test", "terms tokens", "another document");
        String queryText = "test";
        /*
        tf : (1+log(1))
        idf : log(4/1)
         */
        float[] expected = {1.38f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test test test test test test", "terms tokens", "another document");
        String queryText = "test";
        /*
        tf : (1+log(6))
        idf : log(4/1)
         */
        float[] expected = {3.87f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test test test test test test", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        /*
           tf : (1+log(6)) * idf : log(4/1)
        +  tf : 0
        +  tf : (1+log(1)) * idf : log(4/2)
         */
        float[] expected = {4.56f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}
