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

package io.anserini.search;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertTrue;

public class SearchCollectionTest {
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

    SearchCollection.main(new String[] {});
    assertTrue(err.toString().contains("Option \"-index\" is required"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo"});
    assertTrue(err.toString().contains("Option \"-output\" is required"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar"});
    assertTrue(err.toString().contains("Option \"-topicreader\" is required"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz"});
    assertTrue(err.toString().contains("Option \"-topics\" is required"));

    restoreStderr();
  }

  @Test
  public void testOptionErrors() throws Exception {
    redirectStderr();

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",});
    assertTrue(err.toString().contains("Index path 'foo' does not exist or is not a directory."));

    restoreStderr();
  }

  @Test
  public void testMutallyExclusive() throws Exception {
    redirectStderr();

    // We can't exhaustively test all combinations, so we just sample a few combinations.

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",
        "-bm25", "-qld"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",
        "-bm25", "-qljm"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",
        "-qljm", "-spl"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",
        "-inl2", "-f2exp"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    err.reset();
    SearchCollection.main(new String[] {"-index", "foo", "-output", "bar", "-topicreader", "baz", "-topics", "topic",
        "-f2log", "-f2exp"});
    assertTrue(err.toString().contains("cannot be used with the option"));

    restoreStderr();
  }
}
