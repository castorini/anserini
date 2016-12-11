package io.anserini.eval;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class EvalArgs {

  // required arguments
  @Option(name = "-run", metaVar = "[path]", required = true, usage = "The TREC formatted result file")
  public String runPath;

  @Option(name = "-qrels", metaVar = "[file]", required = true, usage = "Path to the qrels file")
  public String qrelPath;

  // optional arguments
  @Option(name = "-m", handler = StringArrayOptionHandler.class, usage = "The metric to be printed. Valid ones are: "
          +"[num_ret|num_rel|num_rel_ret|map|p[.cutoff]|ndcg[.cutoff]]")
  String[] reqMetrics;

  @Option(name = "-q", handler = BooleanOptionHandler.class, usage = "Print the internal document IDs of documents")
  boolean printPerQuery;
}