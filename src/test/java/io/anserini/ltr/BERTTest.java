package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.BERT;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class BERTTest  extends BaseFeatureExtractorTest<Integer>{
    private static FeatureExtractor EXTRACTOR = new BERT();

    @Test
    public void testSingleDoc() throws IOException, ExecutionException, InterruptedException {
        float[] expected = {3};
        assertFeatureValues(expected, "121352",  "define extreme", "document size independent of query document", EXTRACTOR);
    }

}

