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

import java.nio.file.Path;

import uk.ac.gla.terrier.jtreceval.trec_eval;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;


public class TrecEval {

  public static class TrecEvalArgs {
    @Option(name = "-qrels", metaVar = "[path]", required = true, usage = "Path to qrels index")
    public String qrels;
    @Option(name = "-runfile", metaVar = "[path]", required = true, usage = "Path to trec runfile")
    public String runfile;
  }

  private final TrecEvalArgs args;
  private final trec_eval te;

  public TrecEval(TrecEvalArgs args) {
    this.args = args;
    this.te = new trec_eval();
  }

  public void run() {
    Path qrelsPath = Path.of(this.args.qrels);
    Path runfilePath = Path.of(this.args.runfile);
    String[][] output = this.te.runAndGetOutput(new String[]{"-c", "-m", "ndcg_cut.10", qrelsPath.toString(), runfilePath.toString()});
    for (String[] line : output) {
      for (String s : line) {
        System.out.println(s);
      }
      System.out.println();
    }
  }

  public static void main(String[] args) {
    TrecEvalArgs trecEvalArgs = new TrecEvalArgs();
    CmdLineParser parser = new CmdLineParser(trecEvalArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
        return;
    }

    TrecEval te = new TrecEval(trecEvalArgs);
    te.run();
  }
}
