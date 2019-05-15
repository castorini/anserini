package io.anserini.search;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class RetrieveArgs {
  // required arguments
  @Option(name = "-qid_queries", metaVar = "[file]", required = true, usage="query id - query mapping file")
  public String qid_queries = "";

  @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
  public String output = "";

  @Option(name = "-index", metaVar = "[path]", required = true, usage = "index path")
  public String index = "";

  // optional arguments
  @Option(name = "-hits", metaVar = "[number]", usage = "number of hits to retrieve")
  public int hits = 10;

  @Option(name = "-k1", metaVar = "[value]", usage = "BM25 k1 parameter")
  public float k1 = 0.82f;

  @Option(name = "-b", metaVar = "[value]", usage = "BM25 b parameter")
  public float b = 0.72f;

  // See our MS MARCO documentation to understand how these parameter values were tuned.
  @Option(name = "-rm3", usage = "use RM3 query expansion model")
  public boolean rm3 = false;

  @Option(name = "-fbTerms", metaVar = "[number]", usage = "RM3 parameter: number of expansion terms")
  public int fbTerms = 10;

  @Option(name = "-fbDocs", metaVar = "[number]", usage = "RM3 parameter: number of documents")
  public int fbDocs = 10;

  @Option(name = "-originalQueryWeight", metaVar = "[value]", usage = "RM3 parameter: weight to assign to the original query")
  public float originalQueryWeight = 0.5f;
}
