package io.anserini.trectools;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

public class TrecRunTest {
  private TrecRun trecRun;
  private Path sampleFilePath;

  @Before
  public void setUp() throws IOException {
    sampleFilePath = Paths.get("runs/testlong/run.neuclir22-zh-en-splade.splade.topics.neuclir22-en.splade.original-desc_title.txt");
    trecRun = new TrecRun(sampleFilePath, false);
  }

  @Test
  public void testReadRun() throws IOException {
    assertEquals(114, trecRun.getTopics().size());  // Assuming sample file has 3 topics
  }

  @Test
  public void testGetDocsByTopic() {
    List<Map<TrecRun.Column, Object>> docs = trecRun.getDocsByTopic("101", 0);
    // System.out.println(docs);
    assertNotNull(docs);
    assertEquals(1000, docs.size());  // Assuming there are at least 10 documents for topic 101
  }

  @Test
  public void testRescoreRRF() {
    trecRun.rescore(RescoreMethod.RRF, 60, 1.0);
    List<Map<TrecRun.Column, Object>> docs = trecRun.getDocsByTopic("101", 1);
    System.out.println(docs.get(0).get(TrecRun.Column.SCORE));
    assertEquals(1.0 / 61, docs.get(0).get(TrecRun.Column.SCORE));
  }

  @Test
  public void testNormalizeScores() {
    trecRun.rescore(RescoreMethod.NORMALIZE, 0, 0);
    List<Map<TrecRun.Column, Object>> docs = trecRun.getDocsByTopic("101", 0);
    double maxScore = (Double) docs.get(0).get(TrecRun.Column.SCORE);
    double minScore = (Double) docs.get(docs.size() - 1).get(TrecRun.Column.SCORE);
    assertEquals(1.0, maxScore, 0.01);
    assertEquals(0.0, minScore, 0.01);
  }

  @Test
  public void testMergeRuns() throws IOException {
    TrecRun trecRun1 = new TrecRun(sampleFilePath);
    TrecRun trecRun2 = new TrecRun(sampleFilePath);
    TrecRun mergedRun = TrecRun.merge(Arrays.asList(trecRun1, trecRun2), null, 10);
    Path outputPath = Paths.get("runs/testsrc/test/resources/output-merge.trec");
    mergedRun.saveToTxt(outputPath, "test_tag");

    // assertEquals(mergedRun.getDocsByTopic("101", 1).get(0).get(TrecRun.Column.SCORE), 2 * (double) trecRun1.getDocsByTopic("101", 1).get(0).get(TrecRun.Column.SCORE));
  }

  @Test
  public void testSaveToTxt() throws IOException {
    Path outputPath = Paths.get("runs/testsrc/test/resources/output.trec");
    // trecRun.rescore(RescoreMethod.SCALE, 0, 2.0);
    trecRun.saveToTxt(outputPath, "Anserini");
    // Re-load the saved run
    TrecRun savedRun = new TrecRun(outputPath);
    assertEquals(trecRun.getTopics().size(), savedRun.getTopics().size());
  }
}
