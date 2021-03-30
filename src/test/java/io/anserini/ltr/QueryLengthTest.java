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

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.QueryLength;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
// get length of query
public class QueryLengthTest  extends BaseFeatureExtractorTest<Integer>{
    private static FeatureExtractor EXTRACTOR = new QueryLength();

    @Test
    public void testSingleDoc() throws IOException, ExecutionException, InterruptedException {
        float[] expected = {3};
        assertFeatureValues(expected, "simple test query", "document size independent of query document",
                EXTRACTOR);
    }

    @Test
    public void testMultipleDocs() throws IOException, ExecutionException, InterruptedException {
        float[] expected = {2};
        assertFeatureValues(expected, "just test", Arrays.asList("first document",
                "second document", "test document document document test"),  EXTRACTOR, 2);
    }
}
