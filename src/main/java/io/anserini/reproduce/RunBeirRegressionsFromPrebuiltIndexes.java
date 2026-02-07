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

package io.anserini.reproduce;

import io.anserini.reproduce.RunRegressionsFromPrebuiltIndexes.Args;
import io.anserini.reproduce.RunRegressionsFromPrebuiltIndexes.TrecEvalMetricDefinitions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.ParserProperties;

public class RunBeirRegressionsFromPrebuiltIndexes {

  public static void main(String[] args) throws Exception {
    Args beirArgs = new RunRegressionsFromPrebuiltIndexes.Args();
    CmdLineParser parser = new CmdLineParser(beirArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException exception) {
      System.err.println(exception.getMessage());
      return;
    }

    RunRegressionsFromPrebuiltIndexes repro = new RunRegressionsFromPrebuiltIndexes("beir", new TrecEvalMetricDefinitions(), beirArgs.printCommands, beirArgs.dryRun, beirArgs.computeIndexSize);
    repro.run();
  }
}
