package io.anserini.search;

import org.kohsuke.args4j.Option;

public class SearchArgs {

  // required arguments
  @Option(name = "-index", metaVar = "[Path]", required = true, usage = "Lucene index")
  String index;

  @Option(name = "-topics", metaVar = "[File]", required = true, usage = "topics file")
  String topics;

  @Option(name = "-output", metaVar = "[File]", required = true, usage = "output file")
  String output;

  // optional arguments

  @Option(name = "-inmem", usage = "load index completely in memory")
  boolean inmem = false;

  @Option(name = "-ql", usage = "use query likelihood scoring model")
  boolean ql = false;

  @Option(name = "-mu", metaVar = "[value]", required = false, usage = "Dirichlet smoothing parameter")
  float mu = 1000.0f;
  /*
   * Why this value? We want to pick a value that corresponds to what the community generally
   * considers to "good". Zhai and Lafferty (SIGIR 2001) write "the optimal value of mu appears to
   * have a wide range (500-10000) and usually is around 2,000. A large value is 'safer,' especially
   * for long verbose queries." We might consider additional evidence from TREC papers: the UMass
   * TREC overview papers from 2002 and 2003 don't specifically mention query-likelihood as a
   * retrieval model. The UMass overview paper from TREC 2004 mentions setting mu to 1000;
   * incidentally, this is the first mention of what the community would later call RM3. So, this
   * setting seems reasonable and does not contradict Zhai and Lafferty.
   */

  @Option(name = "-bm25", usage = "use BM25 scoring model")
  boolean bm25 = false;

  @Option(name = "-k1", metaVar = "[value]", required = false, usage = "BM25 k1 parameter")
  float k1 = 0.9f;

  @Option(name = "-b", metaVar = "[value]", required = false, usage = "BM25 b parameter")
  float b = 0.4f;

  @Option(name = "-rm3", usage = "use RM3 query expansion model (implies using query likelihood)")
  boolean rm3 = false;
  
}