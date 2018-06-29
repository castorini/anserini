/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.search;

import org.kohsuke.args4j.Option;

public class SearchArgs {
  // required arguments
  @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index")
  public String index;

  @Option(name = "-topics", metaVar = "[file]", required = true, usage = "topics file")
  public String topics;

  @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
  public String output;

  @Option(name = "-topicreader", required = true, usage = "define how to read the topic(query) file: one of [Trec|Webxml]")
  public String topicReader;

  // optional arguments
  @Option(name = "-topicfield", usage = "Which field of the query should be used, default \"title\"." +
      " For TREC ad hoc topics, description or narrative can be used.")
  public String topicfield = "title";

  @Option(name = "-searchtweets", usage = "Whether the search is against a tweet " +
      "index created by IndexCollection -collection TweetCollection")
  public Boolean searchtweets = false;

  @Option(name = "-keepstopwords", usage = "Boolean switch to keep stopwords in the query topics")
  public boolean keepstop = false;

  @Option(name = "-arbitraryScoreTieBreak", usage = "Break score ties arbitrarily (not recommended)")
  public boolean arbitraryScoreTieBreak = false;

  @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
  public int hits = 1000;

  @Option(name = "-rerankCutoff", metaVar = "[number]", required = false, usage = "max number of hits " +
    "for the initial round ranking. this is efficient since lots of reranking model only looks at " +
    "the top documents from the initial round ranking.")
  public int rerankcutoff = 50;

  @Option(name = "-runtag", metaVar = "[tag]", required = false, usage = "runtag")
  public String runtag = "Anserini";

  @Option(name = "-ql", usage = "use query likelihood scoring model")
  public boolean ql = false;

  @Option(name = "-mu", metaVar = "[value]", usage = "Dirichlet smoothing parameter")
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

  @Option(name = "-rm3.fbTerms", usage = "parameter to decide how many expansion terms to be picked")
  public int rm3_fbTerms = 20;

  @Option(name = "-rm3.fbDocs", usage = "parameter to decide how many documents to be used to find expansion terms")
  public int rm3_fbDocs = 50;

  @Option(name = "-rm3.originalQueryWeight", usage = "parameter to decide how many documents to be used to find expansion terms")
  public float rm3_originalQueryWeight = 0.6f;

  @Option(name = "-axiom", usage = "use Axiomatic query expansion model for the reranking")
  public boolean axiom = false;

  @Option(name = "-axiom.deterministic", usage = "make the expansion terms axiomatic reranking results deterministic")
  public boolean axiom_deterministic = false;

  @Option(name = "-axiom.seed", metaVar = "[number]", usage = "seed for the random generator in axiomatic reranking")
  public long axiom_seed = 42L;

  @Option(name = "-axiom.m", usage = "parameter M in axiomatic reranking")
  public int axiom_m = 20;

  @Option(name = "-axiom.r", usage = "parameter R in axiomatic reranking")
  public int axiom_r = 30;

  @Option(name = "-axiom.beta", usage = "parameter beta for Axiomatic query expansion model")
  public float axiom_beta = 0.4f;

  @Option(name = "-axiom.index", usage = "path to the external index for generating the reranking doucments pool")
  public String axiom_external_index = "";

  @Option(name = "-model", metaVar = "[file]", required = false, usage = "ranklib model file")
  public String model = "";
}
