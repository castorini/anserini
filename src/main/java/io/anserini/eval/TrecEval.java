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

package io.anserini.eval;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

/**
 * This class provides a Java wrapper around {@code trec_eval} binaries.
 * Adapted from https://github.com/terrierteam/jtreceval by Craig Macdonald.
 */
public class TrecEval {
  private final File binary;
  private int exit = Integer.MAX_VALUE;

  public TrecEval() {
    binary = getTrecEvalBinary();
  }

  private static String getOsShort() {
    String osName = System.getProperty("os.name");

    if (osName.startsWith("Windows")) {
      return "win";
    }

    if (osName.startsWith("Linux")) {
      return "linux";
    }

    if (osName.equals("Mac OS X")) {
      return "macosx";
    }

    throw new UnsupportedOperationException("Unsupported os: " + osName);
  }

  private static File getTrecEvalBinary() {
    final String resName = getExecutableName();
    if (TrecEval.class.getClassLoader().getResource(resName) == null) {
      throw new UnsupportedOperationException("Unsupported os/arch: " + resName);
    }

    File tempExec = null;
    try {
      Path tempExecDir = Files.createTempDirectory("trec_eval");
      tempExecDir.toFile().deleteOnExit();

      tempExec = File.createTempFile("trec_eval", "", tempExecDir.toFile());
      try (InputStream in = TrecEval.class.getClassLoader().getResourceAsStream(resName);
           OutputStream out = new BufferedOutputStream(new FileOutputStream(tempExec))) {
        if (in == null) {
          throw new RuntimeException();
        }
        IOUtils.copy(in, out);
      }
      tempExec.setExecutable(true);
      tempExec.deleteOnExit();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return tempExec;
  }

  protected static String getExecutableName() {
    return "trec_eval/trec_eval-" + getOsShort() + "-" + System.getProperty("os.arch");
  }

  private String potentiallyExpandSymbol(String sym) {
    File f = new File(sym);
    // Check for exact match
    if (f.exists()) {
      return f.toString();
    }

    // If no exact match is found, we are expecting a symbol
    Path filePath;
    try {
      filePath = RelevanceJudgments.getQrelsPath(Path.of(sym));
    } catch (IOException e) {
      filePath = Path.of(sym);
    }

    return filePath.toString();
  }

  private ProcessBuilder getBuilder(String[] args) {
    List<String> cmd = new ArrayList<String>();
    cmd.add(binary.getAbsolutePath().toString());
    for (int i = 0; i < args.length; i++) {
      // Special case for symbol expansion
      if (i == args.length - 2) {
        cmd.add(potentiallyExpandSymbol(args[i]));
      } else {
        cmd.add(args[i]);
      }
    }

    return new ProcessBuilder(cmd);
  }

  /**
   * Returns the output from a {@code trec_eval} invocation as a string array.
   * Each row represents the result for a particular metric.
   * The fields for each row are the metric, the qid (or "all"), and the metric value.
   *
   * @param args {@code trec_eval} command-line arguments
   * @return the output from a {@code trec_eval} invocation as a string array
   */
  public String[][] runAndGetOutput(String[] args) {
    List<String[]> output = new ArrayList<String[]>();
    try {
      ProcessBuilder pb = getBuilder(args);
      pb.redirectError(Redirect.INHERIT);
      Process p = pb.start();
      try (InputStream in = p.getInputStream();
           InputStreamReader reader = new InputStreamReader(in);
           LineIterator it = IOUtils.lineIterator(reader)) {
        while (it.hasNext()) {
          output.add(it.next().split("\\s+"));
        }
      }
      p.waitFor();
      exit = p.exitValue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    if (exit != 0) {
      throw new RuntimeException(String.format("trec_eval ended with non-zero exit code (%d)", exit));
    }

    return output.toArray(new String[output.size()][]);
  }

  /**
   * Returns the exit code of the last invocation of {@code trec_eval}.
   *
   * @return the exit code of the last invocation of {@code trec_eval}
   */
  public int getLastExitCode() {
    return exit;
  }

  /**
   * Invokes {@code trec_eval} and displays the output to this process's {@code stdout}.
   *
   * @param args {@code trec_eval} command-line arguments
   * @return exit code of {@code trec_eval}
   */
  public int run(String[] args) {
    try {
      ProcessBuilder pb = getBuilder(args);
      pb.inheritIO();

      Process p = pb.start();
      exit = p.waitFor();
    } catch (Exception e) {
      e.printStackTrace();
      exit = -1;
    }

    return exit;
  }

  /**
   * Invokes {@code trec_eval}.
   *
   * @param args {@code trec_eval} command-line arguments
   */
  public static void main(String[] args) {
    new TrecEval().run(args);
  }
}
