/**
 * Anserini: A Lucene toolkit for replicable information retrieval research
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

package io.anserini.ltr;

import ciir.umass.edu.learning.DataPoint;
import ciir.umass.edu.learning.Ranker;
import ciir.umass.edu.learning.RankerFactory;
import io.anserini.util.Qrels;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * This file will consume a feature file and
 * ranklib model to score the feature vectors
 * then output in a trec_eval friend format
 */
public class RankLibScorer {
  // We need a model file
  // We need a feature file
  // We need a qrel file
  private static class ParseArgs {

    @Option(name = "-model", metaVar = "[file]", required = true, usage = "ranklib model file")
    public String model = "";

    @Option(name = "-featureFile", metaVar = "[file]", required = true, usage = "feature vector file")
    public String featureFile = "";

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "output for the feature vector file")
    public String output = "";

    @Option(name = "-qrels", metaVar = "[file]", required = true, usage = "patht to the qrels file, needed for feature vectors")
    public String qrels= "";
  }

  public static void main(String[] args) throws IOException {
    ParseArgs parsedArgs= new ParseArgs();
    CmdLineParser parser = new CmdLineParser(parsedArgs , ParserProperties.defaults().withUsageWidth(90));
    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchTweets" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }
    Qrels qrels = new Qrels(parsedArgs.qrels);
    BufferedReader reader = new BufferedReader(new FileReader(parsedArgs.featureFile));
    // Map of qid:docId -> datapoint
    Map<String, DataPoint> featureMap = new HashMap<>();
    Ranker ranker = new RankerFactory().loadRanker(parsedArgs.model);

    String line= reader.readLine();
    // We are expecting a line of the form:
    // qrel qid featureVector # docid
    while (line != null) {
      DataPoint dp = new DataPoint(line);
      String[] pieces = line.split(" ");
      String key = pieces[1] + " " + pieces[pieces.length-1];
      featureMap.put(key, dp);
      line = reader.readLine();
    }

    BufferedWriter writer = new BufferedWriter(new FileWriter(parsedArgs.output));
    // Now we want it of the form:
    //qid, Q0, docid, 0(rank), score, LUCENE
    for (String key : featureMap.keySet()) {
      StringBuilder sb = new StringBuilder();
      String pieces[] = key.split(" ");
      sb.append(pieces[0]);
      sb.append(" Q0 ");
      sb.append(pieces[1]);
      sb.append(" 0 ");
      double score = ranker.eval(featureMap.get(key));
      sb.append(score);
      sb.append(" LUCENE");
      writer.write(sb.toString());
      writer.newLine();
    }
    writer.flush();
    writer.close();
  }
}
