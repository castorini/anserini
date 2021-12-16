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

import io.anserini.ltr.feature.TfStat;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;


/** test on TF */
public class TfStatTest extends BaseFeatureExtractorTest<Integer> {
    private static final FeatureExtractor EXTRACTOR = new TfStat(new SumPooler());
    /*termFreq*/

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test";
        float[] expected = {1f};

        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultiDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test", "terms tokens test test", "another document");
        String queryText = "test";
        float[] expected = {2f};

        assertFeatureValues(expected, queryText, docs, EXTRACTOR, 2);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case for multiple query tokens test";
        String queryText = "test query not present";

        float[] expected = {3f};
        assertFeatureValues(expected, queryText, docText, EXTRACTOR);
    }

    @Test
    public void testMultiDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document to test", "terms tokens test test", "another document");
        String queryText = "test query tokens not present";

        float[] expected = {3f};
        assertFeatureValues(expected, queryText, docs, EXTRACTOR,2);
    }

}
