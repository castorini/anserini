/*
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

package io.anserini.search;

import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

public class SearchArgs {
  // required arguments
  @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index")
  public String index;

  @Option(name = "-topics", metaVar = "[file]", handler = StringArrayOptionHandler.class, required = true, usage = "topics file")
  public String[] topics;

  @Option(name = "-output", metaVar = "[file]", required = true, usage = "output file")
  public String output;

  @Option(name = "-topicreader", required = true, usage = "TopicReader to use.")
  public String topicReader;

  // optional arguments
  @Option(name = "-querygenerator", usage = "QueryGenerator to use.")
  public String queryGenerator = "BagOfWordsQueryGenerator";

  @Option(name = "-threads", metaVar = "[Number]", usage = "Number of Threads")
  public int threads = 1;

  @Option(name = "-language", usage = "Analyzer Language")
  public String language = "en";

  @Option(name = "-inmem", usage = "Boolean switch to read index in memory")
  public Boolean inmem = false;

  @Option(name = "-topicfield", usage = "Which field of the query should be used, default \"title\"." +
      " For TREC ad hoc topics, description or narrative can be used.")
  public String topicfield = "title";

  // Note that this option is set to false by default because duplicate documents usually indicate some underlying
  // indexing issues, and we don't want to just eat errors silently.
  @Option(name = "-removedups", usage = "Remove duplicate docids when writing final run output.")
  public Boolean removedups = false;

  @Option(name = "-skipexists", usage = "When enabled, will skip if the run file exists")
  public Boolean skipexists = false;

  @Option(name = "-searchtweets", usage = "Whether the search is against a tweet " +
      "index created by IndexCollection -collection TweetCollection")
  public Boolean searchtweets = false;

  @Option(name = "-backgroundlinking", forbids = {"-sdm", "-rf.qrels"},
      usage = "performs the background linking task as part of the TREC News Track")
  public Boolean backgroundlinking = false;

  @Option(name = "-backgroundlinking.k", usage = "extract top k terms from the query document for TREC News Track Background " +
      "Linking task. The terms are ranked by their tf-idf score from the query document")
  public int backgroundlinking_k = 10;

  @Option(name = "-backgroundlinking.datefilter", usage = "Boolean switch to filter out articles published after topic article " +
      "for the TREC News Track Background Linking task.")
  public boolean backgroundlinking_datefilter = false;

  @Option(name = "-stemmer", usage = "Stemmer: one of the following porter,krovetz,none. Default porter")
  public String stemmer = "porter";

  @Option(name = "-keepstopwords", usage = "Boolean switch to keep stopwords in the query topics")
  public boolean keepstop = false;

  @Option(name = "-stopwords", metaVar = "[file]", forbids = "-keepStopwords",
          usage = "Path to file with stopwords.")
  public String stopwords = null;

  @Option(name = "-arbitraryScoreTieBreak", usage = "Break score ties arbitrarily (not recommended)")
  public boolean arbitraryScoreTieBreak = false;

  @Option(name = "-hits", metaVar = "[number]", required = false, usage = "max number of hits to return")
  public int hits = 1000;

  @Option(name = "-rerankCutoff", metaVar = "[number]", required = false, usage = "max number of hits " +
      "for the initial round ranking. this is efficient since lots of reranking model only looks at " +
      "the top documents from the initial round ranking.")
  public int rerankcutoff = 50;

  @Option(name = "-rf.qrels", metaVar = "[file]", usage = "qrels file used for relevance feedback")
  public String rf_qrels = null;

  @Option(name = "-runtag", metaVar = "[tag]", usage = "runtag")
  public String runtag = null;

  // ---------------------------------------------
  // Simple built-in support for passage retrieval
  // ---------------------------------------------

  // A simple approach to passage retrieval is to pre-segment documents in the corpus into passages and index those
  // passages. At retrieval time, we retain only the max scoring passage from each document; this is often called MaxP,
  // from Dai and Callan (SIGIR 2019) in the context of BERT, although the general approach dates back to Callan
  // (SIGIR 1994), Hearst and Plaunt (SIGIR 1993), and lots of other papers from the 1990s and even earlier.
  //
  // One common convention is to label the passages of a docid as "docid.00000", "docid.00001", "docid.00002", ...
  // We use this convention in CORD-19. Alternatively, in document expansion for the MS MARCO document corpus, we use
  // '#' as the delimiter.
  //
  // The options below control various aspects of this behavior.

  @Option(name = "-selectMaxPassage", usage = "Select and retain only the max scoring segment from each document.")
  public Boolean selectMaxPassage = false;

  @Option(name = "-selectMaxPassage.delimiter", metaVar = "[regexp]",
      usage = "The delimiter (as a regular regression) for splitting the segment id from the doc id.")
  public String selectMaxPassage_delimiter = "\\.";

  @Option(name = "-selectMaxPassage.hits", metaVar = "[int]",
      usage = "Maximum number of hits to return per topic after segment id removal. " +
              "Note that this is different from '-hits', which specifies the number of hits including the segment id. ")
  public int selectMaxPassage_hits = Integer.MAX_VALUE;
  // Note that by default here we explicitly *don't* restrict the final number of hits returned per topic.

  // -------------------
  // ranking model: bm25
  // -------------------

  @Option(name = "-bm25",
      forbids = {"-qld", "-qljm", "-inl2", "-spl", "-f2exp", "-f2log"},
      usage = "ranking model: BM25")
  public boolean bm25 = false;

  @Option(name = "-bm25.accurate", usage = "BM25: use accurate document lengths")
  public boolean bm25Accurate = false;

  // BM25 parameters: Robertson et al. (TREC 4) propose the range of 1.0-2.0 for k1 and 0.6-0.75 for b, with k1 = 1.2
  // and b = 0.75 being a very common setting. Empirically, these values don't work very well for modern collections.
  // Here, we adopt the defaults recommended by Trotman et al. (SIGIR 2012 OSIR Workshop) of k1 = 0.9 and b = 0.4.
  // These values come from tuning on the INEX 2008 Wikipedia collection, which is less commonly used, so there isn't
  // the danger of (inadvertently) training on test data. These settings are used in the ATIRE system and also in
  // Lin et al. (ECIR 2016).

  @Option(name = "-bm25.k1", handler = StringArrayOptionHandler.class, usage = "BM25: k1 parameter")
  public String[] bm25_k1 = new String[]{"0.9"};

  @Option(name = "-bm25.b", handler = StringArrayOptionHandler.class, usage = "BM25: b parameter")
  public String[] bm25_b = new String[]{"0.4"};

  // --------------------------------------------------------
  // ranking model: query likelihood with Dirichlet smoothing
  // --------------------------------------------------------

  @Option(name = "-qld",
      forbids = {"-bm25", "-qljm", "-inl2", "-spl", "-f2exp", "-f2log"},
      usage = "ranking model: query likelihood with Dirichlet smoothing")
  public boolean qld = false;

  // Why this value? We want to pick a value that corresponds to what the community generally considers to be "good".
  // Zhai and Lafferty (SIGIR 2001) write "the optimal value of mu appears to have a wide range (500-10000) and
  // usually is around 2,000. A large value is 'safer', especially for long verbose queries." We might consider
  // additional evidence from TREC papers: the UMass TREC overview papers from 2002 and 2003 don't specifically
  // mention query-likelihood as a retrieval model. The UMass overview paper from TREC 2004 mentions setting mu
  // to 1000; incidentally, this is the first mention of what the community would later call RM3. So, this setting
  // seems reasonable and does not contradict Zhai and Lafferty.

  @Option(name = "-qld.mu", handler = StringArrayOptionHandler.class, usage = "qld: mu smoothing parameter")
  public String[] qld_mu = new String[]{"1000"};

  // -------------------------------------------------------------
  // ranking model: query likelihood with Jelinek-Mercer smoothing
  // -------------------------------------------------------------

  @Option(name = "-qljm",
      forbids = {"-bm25", "-qld", "-inl2", "-spl", "-f2exp", "-f2log"},
      usage = "ranking model: query likelihood with Jelinek-Mercer smoothing")
  public boolean qljm = false;

  @Option(name = "-qljm.lambda", handler = StringArrayOptionHandler.class, usage = "qljm: lambda smoothing parameter")
  public String[] qljm_lambda = new String[]{"0.1"};

  @Option(name = "-inl2",
      forbids = {"bm25", "-qld", "-qljm", "-spl", "-f2exp", "-f2log"},
      usage = "use I(n)L2 scoring model")
  public boolean inl2 = false;

  @Option(name = "-inl2.c", metaVar = "[value]", usage = "I(n)L2 c parameter")
  public String[] inl2_c = new String[]{"0.1"};

  @Option(name = "-spl",
      forbids = {"bm25", "-qld", "-qljm", "-inl2", "-f2exp", "-f2log"},
      usage = "use SPL scoring model")
  public boolean spl = false;

  @Option(name = "-spl.c", metaVar = "[value]", usage = "SPL c parameter")
  public String[] spl_c = new String[]{"0.1"};

  @Option(name = "-f2exp",
      forbids = {"bm25", "-qld", "-qljm", "-inl2", "-spl", "-f2log"},
      usage = "use F2Exp scoring model")
  public boolean f2exp = false;

  @Option(name = "-f2exp.s", metaVar = "[value]", usage = "F2Exp s parameter")
  public String[] f2exp_s = new String[]{"0.5"};

  @Option(name = "-f2log",
      forbids = {"bm25", "-qld", "-qljm", "-inl2", "-spl", "-f2exp"},
      usage = "use F2Log scoring model")
  public boolean f2log = false;

  @Option(name = "-f2log.s", metaVar = "[value]", usage = "F2Log s parameter")
  public String[] f2log_s = new String[]{"0.5"};

  @Option(name = "-sdm", usage = "boolean switch to use Sequential Dependence Model query")
  public boolean sdm = false;

  @Option(name = "-sdm.tw", metaVar = "[value]", usage = "SDM term weight")
  public float sdm_tw = 0.85f;

  @Option(name = "-sdm.ow", metaVar = "[value]", usage = "ordered window weight in sdm")
  public float sdm_ow = 0.1f;

  @Option(name = "-sdm.uw", metaVar = "[value]", usage = "unordered window weight in sdm")
  public float sdm_uw = 0.05f;

  // --------------------------
  // query expansion model: rm3
  // --------------------------

  // Anserini uses the same default options as in Indri.
  // As of v5.13, the defaults in Indri are, from src/RMExpander.cpp:
  //
  //   int fbDocs = _param.get( "fbDocs" , 10 );
  //   int fbTerms = _param.get( "fbTerms" , 10 );
  //   double fbOrigWt = _param.get( "fbOrigWeight", 0.5 );
  //   double mu = _param.get( "fbMu", 0 );

  @Option(name = "-rm3", usage = "use RM3 query expansion model")
  public boolean rm3 = false;

  @Option(name = "-rm3.fbTerms", handler = StringArrayOptionHandler.class,
      usage = "RM3 parameter: number of expansion terms")
  public String[] rm3_fbTerms = new String[]{"10"};

  @Option(name = "-rm3.fbDocs", handler = StringArrayOptionHandler.class,
      usage = "RM3 parameter: number of expansion documents")
  public String[] rm3_fbDocs = new String[]{"10"};

  @Option(name = "-rm3.originalQueryWeight", handler = StringArrayOptionHandler.class,
      usage = "RM3 parameter: weight to assign to the original query")
  public String[] rm3_originalQueryWeight = new String[]{"0.5"};

  @Option(name = "-rm3.outputQuery",
      usage = "RM3 parameter: flag to print original and expanded queries")
  public boolean rm3_outputQuery = false;

  // ------------------------------
  // query expansion model: bm25prf
  // ------------------------------

  @Option(name = "-bm25prf", usage = "use bm25PRF query expansion model")
  public boolean bm25prf = false;

  @Option(name = "-bm25prf.fbTerms", handler = StringArrayOptionHandler.class,
      usage = "bm25PRF parameter: number of expansion terms")
  public String[] bm25prf_fbTerms = new String[]{"20"};

  @Option(name = "-bm25prf.fbDocs", handler = StringArrayOptionHandler.class,
      usage = "bm25PRF parameter: number of documents")
  public String[] bm25prf_fbDocs = new String[]{"10"};

  @Option(name = "-bm25prf.k1", handler = StringArrayOptionHandler.class,
      usage = "bm25PRF parameter: k1")
  public String[] bm25prf_k1 = new String[]{"0.9"};

  @Option(name = "-bm25prf.b", handler = StringArrayOptionHandler.class,
      usage = "bm25PRF parameter: b")
  public String[] bm25prf_b = new String[]{"0.4"};

  @Option(name = "-bm25prf.newTermWeight", handler = StringArrayOptionHandler.class,
      usage = "bm25PRF parameter: weight to assign to the expansion terms")
  public String[] bm25prf_newTermWeight = new String[]{"0.2"};

  @Option(name = "-bm25prf.outputQuery",
      usage = "bm25PRF parameter: print original and expanded queries")
  public boolean bm25prf_outputQuery = false;

  // --------------------------------------------------
  // query expansion model: axiomatic semantic matching
  // --------------------------------------------------

  @Option(name = "-axiom", usage = "use Axiomatic query expansion model for the reranking")
  public boolean axiom = false;

  @Option(name = "-axiom.outputQuery", usage = "output original and expanded query")
  public boolean axiom_outputQuery = false;

  @Option(name = "-axiom.deterministic", usage = "make the expansion terms axiomatic reranking results deterministic")
  public boolean axiom_deterministic = false;

  @Option(name = "-axiom.seed", handler = StringArrayOptionHandler.class, usage = "seed for the random generator in axiomatic reranking")
  public String[] axiom_seed = new String[]{"42"};

  @Option(name = "-axiom.docids", usage = "sorted docids file that for deterministic reranking. this file can be obtained " +
      "by running CLI command `IndexUtils -index /path/to/index -dumpAllDocids GZ`")
  public String axiom_docids = null;

  @Option(name = "-axiom.r", handler = StringArrayOptionHandler.class, usage = "parameter R in axiomatic reranking")
  public String[] axiom_r = new String[]{"20"};

  @Option(name = "-axiom.n", handler = StringArrayOptionHandler.class, usage = "parameter N in axiomatic reranking")
  public String[] axiom_n = new String[]{"30"};

  @Option(name = "-axiom.beta", handler = StringArrayOptionHandler.class, usage = "parameter beta for Axiomatic query expansion model")
  public String[] axiom_beta = new String[]{"0.4"};

  @Option(name = "-axiom.top", handler = StringArrayOptionHandler.class, usage = "select top M terms from the expansion terms pool")
  public String[] axiom_top = new String[]{"20"};

  @Option(name = "-axiom.index", usage = "path to the external index for generating the reranking doucments pool")
  public String axiom_index = null;

  @Option(name = "-qid_queries", metaVar = "[file]", usage = "query id - query mapping file")
  public String qid_queries = "";

  // These are convenience methods to support a fluent, method-chaining style of programming.
  public SearchArgs bm25() {
    this.bm25 = true;
    this.qld = false;
    this.qljm = false;
    this.inl2 = false;
    this.spl = false;
    this.f2exp = false;
    this.f2log = false;

    return this;
  }

  public SearchArgs qld() {
    this.bm25 = false;
    this.qld = true;
    this.qljm = false;
    this.inl2 = false;
    this.spl = false;
    this.f2exp = false;
    this.f2log = false;

    return this;
  }

  public SearchArgs qljm() {
    this.bm25 = false;
    this.qld = false;
    this.qljm = true;
    this.inl2 = false;
    this.spl = false;
    this.f2exp = false;
    this.f2log = false;

    return this;
  }

  public SearchArgs inl2() {
    this.bm25 = false;
    this.qld = false;
    this.qljm = false;
    this.inl2 = true;
    this.spl = false;
    this.f2exp = false;
    this.f2log = false;

    return this;
  }

  public SearchArgs spl() {
    this.bm25 = false;
    this.qld = false;
    this.qljm = false;
    this.inl2 = false;
    this.spl = true;
    this.f2exp = false;
    this.f2log = false;

    return this;
  }

  public SearchArgs f2exp() {
    this.bm25 = false;
    this.qld = false;
    this.qljm = false;
    this.inl2 = false;
    this.spl = false;
    this.f2exp = true;
    this.f2log = false;

    return this;
  }

  public SearchArgs f2log() {
    this.bm25 = false;
    this.qld = false;
    this.qljm = false;
    this.inl2 = false;
    this.spl = false;
    this.f2exp = false;
    this.f2log = true;

    return this;
  }

  public SearchArgs searchTweets() {
    this.searchtweets = true;
    return this;
  }

}
