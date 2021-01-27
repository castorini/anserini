package io.anserini.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.junit.Test;

import java.util.Arrays;

import static io.anserini.analysis.DefaultEnglishAnalyzer.fromArguments;
import static org.junit.Assert.assertEquals;

public class DefaultEnglishAnalyzerTest {

    @Test
    public void testKeepStopwords() throws Exception {
        DefaultEnglishAnalyzer defaultAnalyzer = fromArguments("porter", false, null);
        assertEquals(EnglishAnalyzer.getDefaultStopSet(), defaultAnalyzer.getStopwordSet());

        DefaultEnglishAnalyzer analyzer = fromArguments("porter", true, null);
        assertEquals(CharArraySet.EMPTY_SET, analyzer.getStopwordSet());
    }

    @Test
    public void testStopwordsLoading() throws Exception {
        DefaultEnglishAnalyzer analyzer = fromArguments("porter", false, "src/test/resources/test-stopwords.txt");
        CharArraySet expectedStopwords = new CharArraySet(Arrays.asList("some", "very", "common", "words"), false);
        assertEquals(expectedStopwords, analyzer.getStopwordSet());
    }
}
