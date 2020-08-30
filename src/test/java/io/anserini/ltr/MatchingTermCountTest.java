package io.anserini.ltr;

import io.anserini.ltr.feature.FeatureExtractor;
import io.anserini.ltr.feature.base.MatchingTermCount;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MatchingTermCountTest extends BaseFeatureExtractorTest<Integer> {

    private FeatureExtractor EXTRACTOR = new MatchingTermCount();

    @Test
    public void testAllMissing() throws IOException, ExecutionException, InterruptedException {
        float[] expected = {0};
        assertFeatureValues(expected, "nothing", "document test missing all", EXTRACTOR);
    }

    @Test
    public void testSingleTermDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document document document another";
        String testQuery = "document";
        float[] expected = {1};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testMissingTermDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document test simple tokens";
        String testQuery = "simple missing";
        float[] expected = {1};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testMultipleTermsDoc() throws IOException, ExecutionException, InterruptedException {
        String testText = "document with multiple document term document multiple some missing";
        String testQuery = "document multiple missing";
        float[] expected = {3};

        assertFeatureValues(expected, testQuery, testText, EXTRACTOR);
    }

    @Test
    public void testTermFrequencyWithMultipleDocs() throws IOException, ExecutionException, InterruptedException {
        List<String> docs = Arrays.asList("document document", "document with multiple terms",
                "document to test", "test terms tokens", "another test document");
        // We want to test that the expected value of count 1 is found for document
        // at index 2
        String queryText = "document";
        float[] expected = {1};

        assertFeatureValues(expected, queryText, docs, EXTRACTOR, 2);
    }
}
