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

import io.anserini.ltr.feature.DfrGl2Stat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


public class DfrGl2Test extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new DfrGl2Stat(new SumPooler());
    /*
      avgFL:totalTermFreq/numDocs;
      tfn:tf*log2(1+avgFL/docSize)
      (log(1+collectionFreq/numDocs) + tfn*log(1+numDocs/collectionFreq))/(tfn+1)
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        float[] expected = {0.69f};
        //avgFL:4
        //tfn:log2(2)
        //(log(1+1/1)+log2(2)*log(1+1))/(log2(2)+1)

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";
        //avgFL:4
        //tfn:log2(2)
        //(log(1+1/1)+log2(2)*log(1+1))/(log2(2)+1)

        //avgFL:4
        //tfn:log2(2)
        //(log(1+1/1)+log2(2)*log(1+1))/(log2(2)+1)

        //0

        float[] expected = {1.38f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";
        float[] expected = {0.84f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another document");
        String queryText = "test";
        float[] expected = {0.61f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another doc");
        String queryText = "test tfidf document";
        float[] expected = {1.27f};
        //assertFeatureValues(expected, queryText, docs, EXTRACTOR,0);
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,1);
    }

}


