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

import io.anserini.ltr.feature.RunList;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

public class RunListTest  extends BaseFeatureExtractorTest<Integer>{
    @Test
    public void testSingleDoc() throws IOException, ExecutionException, InterruptedException {
        File tempFile = Files.createTempFile("tempFile", ".txt").toFile();
        tempFile.deleteOnExit();
        BufferedWriter writer = new BufferedWriter(new FileWriter("tempFile.txt"));
        writer.write("1\tQ0\tdoc0\t1\t3.0\tanserini\n");
        writer.flush();
        writer.close();
        FeatureExtractor EXTRACTOR = new RunList("tempFile.txt", "BERT");
        float[] expected = {3};
        assertFeatureValues(expected, "1",  "define extreme", "document size independent of query document", EXTRACTOR);
    }

    @After
    @Override
    public void tearDown() throws Exception {
        super.tearDown();
        File tempFile = new File("tempFile.txt");
        if(tempFile.exists()) {
            tempFile.delete();
        }
    }

}

