package io.anserini.analysis;

import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class TokenizeOnlyAnalyzerTest {
  @Test
  public void test1() {
    Analyzer analyzer = DefaultEnglishAnalyzer.newNonStemmingInstance();
    List<String> tokens = AnalyzerUtils.analyze(analyzer, "city buses are running on schedule");

    assertEquals(6, tokens.size());
    assertEquals("city", tokens.get(0));
    assertEquals("buses", tokens.get(1));
    assertEquals("are", tokens.get(2));
    assertEquals("running", tokens.get(3));
    assertEquals("on", tokens.get(4));
    assertEquals("schedule", tokens.get(5));
  }
}
