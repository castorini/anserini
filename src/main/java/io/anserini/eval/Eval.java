package io.anserini.eval;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Eval App
 */
public final class Eval {

  private static final Logger LOG = LogManager.getLogger(Eval.class);

  private static String[] allMetrics;
  private static String[] allQueries;

  public static class EvalBundle {
    public String format;
    public Map<String, Double> evals;
    public double aggregated;

    EvalBundle(String format, Map<String, Double> evals, double aggregated) {
      this.format = format;
      this.evals = evals;
      this.aggregated = aggregated;
    }
  }

  private static Map<String, EvalBundle> allEvals;

  public static void setAllMetrics(String[] metrics) {
    allMetrics = metrics;
  }

  public static Map<String, EvalBundle> getAllEvals() {
    return allEvals;
  }

  public static void print(boolean printPerQuery, PrintStream output) {
    String format = "%1$-22s\t%2$s\t%3$";
    if (printPerQuery) {
      String anyMetric = allMetrics[0];
      Set<String> querySet = allEvals.get(anyMetric).evals.keySet();
      String[] queries = querySet.toArray(new String[querySet.size()]);
      for (String query : queries) {
        for (String metric : allMetrics) {
          String formattedOutput = format + allEvals.get(metric).format + "\n";
          output.format(Locale.US, formattedOutput, metric, query, allEvals.get(metric).evals.get(query));
        }
      }
    }
    for (String metric : allMetrics) {
      String formattedOutput = format + allEvals.get(metric).format + "\n";
      output.format(Locale.US, formattedOutput, metric, "all", allEvals.get(metric).aggregated);
    }
  }

  public static void eval(String runFile, String qrelFile, boolean long_docids,
                          boolean docid_desc) throws IOException {
    RankingResults rr = new RankingResults(runFile, long_docids, docid_desc);
    QueryJudgments qj = new QueryJudgments(qrelFile);
    allEvals = new TreeMap<>();
    for (String metric : allMetrics) {
      BatchEval bm = new BatchEval(metric);
      Map<String, Double> evals = bm.evaluate(rr, qj);
      double aggregated = bm.getAggregated();
      String format = bm.getFormat();
      allEvals.put(metric, new EvalBundle(format, evals, aggregated));
    }
  }

  public static void main(String[] args) throws Exception {

    EvalArgs evalArgs = new EvalArgs();
    CmdLineParser parser = new CmdLineParser(evalArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    allMetrics = evalArgs.reqMetrics;
    if (allMetrics.length == 0) {
      System.err.println("No metric provided...exit");
      return;
    }
    eval(evalArgs.runPath, evalArgs.qrelPath, evalArgs.longDocids, evalArgs.asc);
    print(evalArgs.printPerQuery, System.out);
  }
}
