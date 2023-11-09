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

package io.anserini.index;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IndexInvertedDenseVectors}
 */
public class IndexInvertedDenseVectorsTest {

  @Test
  public void indexFWTest() throws Exception {
    String output = createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
    System.out.println(output);
    assertTrue(output.contains("Indexing Complete! 4 documents indexed"));
  }

  @Test
  public void indexFWStoredTest() throws Exception {
    String output = createIndex("target/idx-sample-fw" + System.currentTimeMillis(), "fw", false);
    //System.out.println(output);
    assertTrue(output.contains("Indexing Complete! 4 documents indexed"));
  }

  @Test
  public void indexLLTest() throws Exception {
    String output = createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
    assertTrue(output.contains("Indexing Complete! 4 documents indexed"));
  }

  @Test
  public void indexLLStoredTest() throws Exception {
    String output = createIndex("target/idx-sample-ll" + System.currentTimeMillis(), "lexlsh", false);
    assertTrue(output.contains("Indexing Complete! 4 documents indexed"));
  }

  public static String createIndex(String path, String encoding, boolean stored) throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-encoding");
    args.add(encoding);
    args.add("-input");
    args.add("src/test/resources/mini-word-vectors.txt");
    args.add("-index");
    args.add(path);
    if (stored) {
      args.add("-stored");
    }

    final ByteArrayOutputStream redirectedStdout = new ByteArrayOutputStream();
    PrintStream savedStdout = System.out;
    redirectedStdout.reset();
    System.setOut(new PrintStream(redirectedStdout));

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));

    System.setOut(savedStdout);

    return redirectedStdout.toString();
  }

//  @Test
//  public void testLLCollection() throws Exception {
//    List<String> args = new LinkedList<>();
//    args.add("-collection");
//    args.add("JsonDenseVectorCollection");
//    args.add("-encoding");
//    args.add("lexlsh");
//    args.add("-input");
//    args.add("src/test/resources/sample_docs/json_vector/dense_collection1");
//    args.add("-index");
//    args.add("target/idx-sample-ll-vector" + System.currentTimeMillis());
//    args.add("-stored");
//
//    String output = wrapIndexerCall(args);
//    assertTrue(output.contains("Indexing Complete! 2 documents indexed"));
//  }

  @Test
  public void testFWCollection() throws Exception {
    List<String> args = new LinkedList<>();
    args.add("-collection");
    args.add("JsonDenseVectorCollection");
    args.add("-encoding");
    args.add("fw");
    args.add("-input");
    args.add("src/test/resources/sample_docs/json_vector/dense_collection1");
    args.add("-index");
    args.add("target/idx-sample-fw-vector" + System.currentTimeMillis());
    args.add("-stored");

    ByteArrayOutputStream redirectedStdout = new ByteArrayOutputStream();
    System.setOut(new PrintStream(redirectedStdout, true));
    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

    assertTrue(redirectedStdout.toString().contains("Indexing Complete! 2 documents indexed"));
  }

  public static String wrapIndexerCall(List<String> args) throws Exception {
    ByteArrayOutputStream redirectedStdout = new ByteArrayOutputStream();
    System.out.flush();

    //PrintStream savedStdout = System.out;
    //redirectedStdout.reset();
    System.setOut(new PrintStream(redirectedStdout, true));

    System.out.println("RUNNING COMMAND!");

    IndexInvertedDenseVectors.main(args.toArray(new String[0]));
    System.out.flush();

    System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

    System.out.println("!@#");
    return redirectedStdout.toString();
  }

}