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

import java.util.Arrays;

import uk.ac.gla.terrier.jtreceval.trec_eval;

public class TrecEval {

  private final trec_eval te;
  private final String qrels;
  private final String runfile;
  private final String[] args;

  public TrecEval(String[] args) {
    this.qrels = args[args.length-2];
    this.runfile = args[args.length-1];
    this.args =  args; //Arrays.copyOfRange(args, 0, args.length-2);
    this.te = new trec_eval();
  }

  public void run() {
    this.te.run(this.args);
  }

  public static void main(String[] args) {
    TrecEval te = new TrecEval(args);
    te.run();
  }
}
