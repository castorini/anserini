package io.anserini.search;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class SearchDenseVectorsTest {

  private final ByteArrayOutputStream err = new ByteArrayOutputStream();
  private PrintStream save;

  private void redirectStderr() {
    save = System.err;
    err.reset();
    System.setErr(new PrintStream(err));
  }

  private void restoreStderr() {
    System.setErr(save);
  }

  @Test
  public void testIncompleteOptions() throws Exception {
    redirectStderr();

    SearchDenseVectors.main(new String[] {});
    assertTrue(err.toString().contains("Option \"-index\" is required"));

    err.reset();
    SearchDenseVectors.main(new String[] {"-index", "foo"});
    assertTrue(err.toString().contains("Option \"-output\" is required"));

    err.reset();
    SearchDenseVectors.main(new String[] {"-index", "foo", "-output", "bar"});
    assertTrue(err.toString().contains("Option \"-topicreader\" is required"));

    err.reset();
    SearchDenseVectors.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz"});
    assertTrue(err.toString().contains("Option \"-topics\" is required"));

    restoreStderr();
  }

  @Test
  public void testOptionErrors() throws Exception {
    redirectStderr();

    err.reset();
    SearchDenseVectors.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",});
    assertTrue(err.toString().contains("Index path 'foo' does not exist or is not a directory."));

    restoreStderr();
  }

}