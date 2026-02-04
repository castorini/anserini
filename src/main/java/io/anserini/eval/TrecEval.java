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
 * The following code is adapted from https://github.com/terrierteam/jtreceval
 * by Craig Macdonald
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
    final String resName = getExecName();
    if (TrecEval.class.getClassLoader().getResource(resName) == null)
      throw new UnsupportedOperationException("Unsupported os/arch: " + resName);

    File tempExec = null;
    try {
      Path tempExecDir = Files.createTempDirectory("trec_eval");
      tempExecDir.toFile().deleteOnExit();

      tempExec = File.createTempFile("trec_eval", "", tempExecDir.toFile());
      InputStream in = TrecEval.class.getClassLoader().getResourceAsStream(resName);
      OutputStream out = new BufferedOutputStream(new FileOutputStream(tempExec));
      IOUtils.copy(in, out);
      in.close();
      out.close();
      tempExec.setExecutable(true);
      tempExec.deleteOnExit();
    } catch (Exception e) {
      throw new UnsupportedOperationException(e);
    }

    assert tempExec.exists() : "Exe file " + tempExec.toString() + " does not exist after creation";
    return tempExec;
  }

  protected static String getExecName() {
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
   * Obtain the output from a trec_eval invocation
   *
   * @param args trec_eval commandline arguments
   * @return first dimension is for each line, second dimension is for each component
   */
  public String[][] runAndGetOutput(String[] args) {
    List<String[]> output = new ArrayList<String[]>();
    try {
      ProcessBuilder pb = getBuilder(args);
      pb.redirectError(Redirect.INHERIT);
      Process p = pb.start();
      InputStream in = p.getInputStream();
      LineIterator it = IOUtils.lineIterator(new InputStreamReader(in));
      while (it.hasNext()) {
        output.add(it.next().split("\\s+"));
      }
      p.waitFor();
      exit = p.exitValue();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    if (exit != 0)
      throw new RuntimeException("trec_eval ended with non-zero exit code (" + exit + ")");
    return output.toArray(new String[output.size()][]);
  }

  /**
   * @return Exit code of last invocation of trec_eval
   */
  public int getLastExitCode() {
    return exit;
  }

  /**
   * Invokes trec_eval and displays the output to this process's STDOUT stream.
   *
   * @param args trec_eval commandline arguments
   * @return exit code of trec_eval
   */
  public int run(String[] args) {
    try {
      ProcessBuilder pb = getBuilder(args);

      Process p = pb.start();
      p.waitFor();

      exit = p.exitValue();
    } catch (Exception e) {
      System.err.println(e);
      e.printStackTrace();
      exit = -1;
    }

    return exit;
  }

  /**
   * Directly invokes trec_eval
   *
   * @param args trec_eval commandline arguments
   */
  public static void main(String[] args) {
    System.exit(new TrecEval().run(args));
  }
}
