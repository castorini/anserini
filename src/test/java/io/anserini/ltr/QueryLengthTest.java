package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.QueryLength;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

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
