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

import io.anserini.ltr.feature.LmDirStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on LMDir */
public class LmDirTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new LmDirStat(new SumPooler());
    /*
      log((tf+1000*collectionFreq/totalTermFreq)/(1000+docSize));
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        /*
        log(251/1004)
         */
        float[] expected = {-1.39f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";
        /*
        log(251/1004)+log(251/1004)

         */
        float[] expected = {-2.78f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";
        /*
        log((1+1000/9)/1003)
         */
        float[] expected = {-2.2f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens test", "another document");
        String queryText = "test";
        /*
        log((6+1000*6/15)/(1000+8));
         */
        float[] expected = {-0.76f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        /*
           log((6+1000*7/15)/(1000+8))
          +log((6)/(1000+8));
          +log((6+1000*1/15)/(1000+8))
         */
        float[] expected = {-2.39f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}



