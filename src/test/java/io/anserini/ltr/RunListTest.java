package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.RunList;
import org.junit.Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class RunListTest  extends BaseFeatureExtractorTest<Integer>{
    @Test
    public void testSingleDoc() throws IOException, ExecutionException, InterruptedException {
        File tempFile = File.createTempFile("tempFile", ".txt");
        tempFile.deleteOnExit();
        BufferedWriter writer = new BufferedWriter(new FileWriter("tempFile.txt"));
        writer.write("1\tQ0\tdoc0\t1\t3.0\tanserini\n");
        writer.flush();
        writer.close();
        FeatureExtractor EXTRACTOR = new RunList("tempFile.txt", "BERT");
        float[] expected = {3};
        assertFeatureValues(expected, "1",  "define extreme", "document size independent of query document", EXTRACTOR);
    }

}

