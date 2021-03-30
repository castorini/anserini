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

import io.anserini.ltr.BaseFeatureExtractorTest;
import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.SumPooler;
import io.anserini.ltr.feature.base.ictfStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on ictf */
public class ictfTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new ictfStat(new SumPooler());
    /*
        log(collectionSize / cf+1)
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        /*
        log(4/2)
         */
        float[] expected = {0.69f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";
        /*
        log(2)+ log(2)+log(4)

         */
        float[] expected = {2.77f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";
        /*
        log(9/2)
         */
        float[] expected = {1.5f};
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
        float[] expected = {0.69f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        /*
           log(14/7) + log(14/1) + log(14/4)
         */
        float[] expected = {4.58f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}

