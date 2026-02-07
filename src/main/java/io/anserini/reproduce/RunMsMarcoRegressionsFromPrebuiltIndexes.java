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

import io.anserini.reproduce.RunRegressionsFromPrebuiltIndexes.TrecEvalMetricDefinitions;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import java.util.*;

public class RunMsMarcoRegressionsFromPrebuiltIndexes {
  public static class Args extends RunRegressionsFromPrebuiltIndexes.Args {
    @Option(name = "-collection", usage = "MS MARCO version {'msmarco-v1-passage' (default), 'msmarco-v1-doc', 'msmarco-v2-doc', 'msmarco-v2-passage', 'msmarco-v2.1-doc', 'msmarco-v2.1-doc-segmented'}.")
    public String MsMarcoVersion = "msmarco-v1-passage.core";
  }

  public static void main(String[] args) throws Exception {
    // check for cmd option
    Args msmarcoArgs = new Args();
    CmdLineParser parser = new CmdLineParser(msmarcoArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (msmarcoArgs.options) {
        System.err.printf("Options for %s:\n\n", RunMsMarcoRegressionsFromPrebuiltIndexes.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }

    Set<String> allowedVersions = new HashSet<>(
            Arrays.asList("msmarco-v1-doc", "msmarco-v1-passage.core", "msmarco-v1-passage.optional", "msmarco-v2-doc", "msmarco-v2-passage", "msmarco-v2.1-doc", "msmarco-v2.1-doc-segmented"));
    if (!allowedVersions.contains(msmarcoArgs.MsMarcoVersion)) {
        System.err.println("Invalid MS MARCO version: " + msmarcoArgs.MsMarcoVersion);
        System.exit(1);
    }

    RunRegressionsFromPrebuiltIndexes repro = new RunRegressionsFromPrebuiltIndexes(msmarcoArgs.MsMarcoVersion, new TrecEvalMetricDefinitions(),
            msmarcoArgs.printCommands, msmarcoArgs.dryRun, msmarcoArgs.computeIndexSize);
    repro.run();
  }
}
