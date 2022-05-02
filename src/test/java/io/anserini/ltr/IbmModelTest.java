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

import io.anserini.ltr.feature.IbmModel1;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class IbmModelTest extends BaseFeatureExtractorTest<Integer> {
    private IbmModel1 IBMModel1;
    /*
       (1-tf/docSize) * (1-tf/docSize)/(tf+1) * (termFreq* log((tf/docSize/(collectionFreqs/totalTermFreq)))
       + 0.5 * log(2.0 * pi * tf * (1- tf/docSize))
    */

    @Test
    public void testSingleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        String queryText = "test";
        String docText = "single document test case";

        double lambda =0.6;
        Long dosize = 1L;
        double tranProb = 0.5;
        float totTranProb = 0;
        double colProb = 1;
        Map<String, Long> map = Map.of("single", 0L,"document", 0L,"test", 1L,"case", 0L);
        for (String docTerm : map.keySet()) {
            totTranProb += tranProb * ((1.0*map.get(docTerm)) / dosize);
        }
        /*
        log(0.4 * 0.5 + 0.6 * 0) - log(0)*/
        float expected = 0.2876821f;
        assertEquals(expected, IBMModel1.calculate_score(colProb,totTranProb,lambda),0.01f);
    }

    @Test
    public void testSingleDocMultiQuery() throws IOException, ExecutionException, InterruptedException {
        String docText = "single document test case";
        String queryText = "test document irrelevant";

        double lambda =0.6;
        Long dosize = 1L;
        double tranProb = 0.5;
        float totTranProb = 0;
        double colProb = 0.67;
        Map<String, Long> map = Map.of("single", 0L,"document", 1L,"test", 1L,"case", 0L);
        for (String docTerm : map.keySet()) {
            totTranProb += tranProb * ((1.0*map.get(docTerm)) / dosize);
        }
        /*totTranProb = 1
        log(0.4 * 1+ 0.6 * 0.67) - log(0.6 * 0.67)*/
        float expected = 0.6906f;
        assertEquals(expected, IBMModel1.calculate_score(colProb,totTranProb,lambda),0.01f);
    }

    @Test
    public void testMultipleDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test case", "terms tokens", "another document");
        String queryText = "test";

        double lambda =0.6;
        Long dosize = 4L;
        double tranProb = 0.5;
        float totTranProb = 0;
        double colProb = 1;
        Map<String, Long> map = Map.of("tokens", 0L,"document", 4L,"test", 1L,"case", 0L,"another",0L);
        for (String docTerm : map.keySet()) {
            totTranProb += tranProb * ((1.0*map.get(docTerm)) / dosize);
        }
        /*totTranProb = 0.625
        log(0.4 * 0.625 + 0.6 * 1) - log(0.6 * 1)*/
        float expected = 0.3483f;
        assertEquals(expected, IBMModel1.calculate_score(colProb,totTranProb,lambda),0.01f);
    }

    @Test
    public void testManyQTermsDocSingleQuery() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document",
                "document test test test test test test case", "terms tokens", "another document");
        String queryText = "test";

        double lambda =0.6;
        Long dosize = 4L;
        double tranProb = 0.5;
        float totTranProb = 0;
        double colProb = 1;
        Map<String, Long> map = Map.of("tokens", 0L,"document", 4L,"test", 7L,"case", 1L,"another",1L);
        for (String docTerm : map.keySet()) {
            totTranProb += tranProb * ((1.0*map.get(docTerm)) / dosize);
        }
        /*totTranProb = 1.625
        log(0.4 * 1.625 + 0.6 * 1) - log(0.6 * 1)*/
        float expected = 0.7340f;
        assertEquals(expected, IBMModel1.calculate_score(colProb,totTranProb,lambda),0.01f);
    }

}


