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

import io.anserini.ltr.feature.NormalizedTfIdf;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on normalized TFIDF */
public class NormalizedTfIdfTest extends BaseFeatureExtractorTest<Integer>{
    private static final FeatureExtractor EXTRACTOR = new NormalizedTfIdf();
    /*
       idf : Math.log(1+numDocs/docFreq)
       tf:(1+Math.log(termFreq))/docSize)
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        /*
        idf : Math.log(2/1)
        tf:(1+Math.log(1))/4)
         */
        float[] expected = {0.17f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";
        /*
        test:
        idf : Math.log(2/1)
        tf:(1+Math.log(1))/4)
        document:
        idf : Math.log(2/1)
        tf:(1+Math.log(1))/4)
        irrelevant: 0

         */
        float[] expected = {0.34f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";
        /*
        test:
        idf : Math.log(5/1)
        tf:(1+Math.log(1))/3)
         */
        float[] expected = {0.54f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another document");
        String queryText = "test";
        /*
        test:
        idf : Math.log(5/1)
        tf:(1+Math.log(6))/8)
         */
        float[] expected = {0.56f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test test test test test test", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        /*
        test:
        idf : Math.log(5/1)
        tf:(1+Math.log(6))/8)

        tfidf: 0

        document:
        idf : Math.log(5/2)
        tf:(1+Math.log(1))/8)
         */
        float[] expected = {0.8f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}
