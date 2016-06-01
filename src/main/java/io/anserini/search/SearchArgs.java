package io.anserini.search;

import io.anserini.document.Collection;
import org.kohsuke.args4j.Option;

public class SearchArgs {

  // required arguments
  @Option(name = "-index", metaVar = "[path]", required = true, usage = "Lucene index")
  public String index;

  @Option(name = "-topics", metaVar = "[file]", required = true, usage = "topics file")
  public String topics;

  @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
  public String output;

  @Option(name = "-collection", required = true, usage = "Collection")
  protected Collection collection;

  // optional arguments

  @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
  public int hits = 1000;

  @Option(name = "-runtag", metaVar = "[tag]", required = false, usage = "runtag")
  public String runtag = "Lucene";

  @Option(name = "-inmem", usage = "load index completely in memory")
  public boolean inmem = false;

  @Option(name = "-ql", usage = "use query likelihood scoring model")
  public boolean ql = false;

  @Option(name = "-mu", metaVar = "[value]", required = false, usage = "Dirichlet smoothing parameter")
  public float mu = 1000.0f;
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
  public boolean bm25 = false;

  @Option(name = "-k1", metaVar = "[value]", required = false, usage = "BM25 k1 parameter")
  public float k1 = 0.9f;

  @Option(name = "-b", metaVar = "[value]", required = false, usage = "BM25 b parameter")
  public float b = 0.4f;

  @Option(name = "-rm3", usage = "use RM3 query expansion model (implies using query likelihood)")
  public boolean rm3 = false;

  @Option(name = "-model", metaVar = "[file]", required = false, usage = "ranklib model file")
  public String model = "";

  @Option(name = "-dump", required = false, usage = "dump out feature vectors")
  public boolean dumpFeatures = false;

  @Option(name = "-featureFile", metaVar = "[file]", required = false, usage = "output for the feature vector file")
  public String featureFile = "";

  @Option(name = "-qrels", metaVar = "[file]", required = false, usage = "patht to the qrels file, needed for feature vectors")
  public String qrels= "";

  @Option(name = "-extractors", metaVar = "[file]", required = false, usage = "Optional definition to feature extractors")
  public String extractors = null;

}