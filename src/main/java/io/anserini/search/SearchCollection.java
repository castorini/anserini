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

package io.anserini.search;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.analysis.HuggingFaceTokenizerAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.Constants;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.index.generator.WashingtonPostGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.AxiomReranker;
import io.anserini.rerank.lib.BM25PrfReranker;
import io.anserini.rerank.lib.NewsBackgroundLinkingReranker;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.RocchioReranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.QueryGenerator;
import io.anserini.search.query.SdmQueryGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.ImpactSimilarity;
import io.anserini.search.similarity.TaggedSimilarity;
import io.anserini.search.topicreader.BackgroundLinkingTopicReader;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.da.DanishAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fa.PersianAnalyzer;
import org.apache.lucene.analysis.fi.FinnishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.analysis.hu.HungarianAnalyzer;
import org.apache.lucene.analysis.id.IndonesianAnalyzer;
import org.apache.lucene.analysis.it.ItalianAnalyzer;
import org.apache.lucene.analysis.ja.JapaneseAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.nl.DutchAnalyzer;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.apache.lucene.analysis.pt.PortugueseAnalyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.analysis.sv.SwedishAnalyzer;
import org.apache.lucene.analysis.te.TeluguAnalyzer;
import org.apache.lucene.analysis.th.ThaiAnalyzer;
import org.apache.lucene.analysis.tr.TurkishAnalyzer;
import org.apache.lucene.analysis.uk.UkrainianMorfologikAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TermInSetQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.similarities.AfterEffectL;
import org.apache.lucene.search.similarities.AxiomaticF2EXP;
import org.apache.lucene.search.similarities.AxiomaticF2LOG;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.BasicModelIn;
import org.apache.lucene.search.similarities.DFRSimilarity;
import org.apache.lucene.search.similarities.DistributionSPL;
import org.apache.lucene.search.similarities.IBSimilarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.LMJelinekMercerSimilarity;
import org.apache.lucene.search.similarities.LambdaDF;
import org.apache.lucene.search.similarities.NormalizationH2;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.apache.lucene.util.BytesRef;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.StringArrayOptionHandler;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Main entry point for search.
 */
public final class SearchCollection implements Closeable {
  // These are the default tie-breaking rules for documents that end up with the same score with respect to a query.
  // For most collections, docids are strings, and we break ties by lexicographic sort order. For tweets, docids are
  // longs, and we break ties by reverse numerical sort order (i.e., most recent tweet first). This means that searching
  // tweets requires a slightly different code path, which is enabled by the -searchtweets option in SearchArgs.
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(Constants.ID, SortField.Type.STRING_VAL));
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.TweetField.ID_LONG.name, SortField.Type.LONG, true));

  private static final Logger LOG = LogManager.getLogger(SearchCollection.class);

  public static class Args {
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
    @Option(name = "-collection", metaVar = "[class]",
        usage = "If doc vector is not stored in the index, this need to be provided as collection class in package 'io.anserini.collection'.")
    public String collectionClass;

    @Option(name = "-querygenerator", usage = "QueryGenerator to use.")
    public String queryGenerator = "BagOfWordsQueryGenerator";

    @Option(name = "-fields", metaVar = "[file]", handler = StringArrayOptionHandler.class, usage = "Fields")
    public String[] fields = new String[]{};
    public Map<String, Float> fieldsMap = new HashMap<>();

    @Option(name = "-threads", metaVar = "[int]", usage = "Number of threads to use for running different parameter configurations.")
    public int threads = 1;

    @Option(name = "-parallelism", metaVar = "[int]", usage = "Number of threads to use for each individual parameter configuration.")
    public int parallelism = 8;

    @Option(name = "-language", usage = "Analyzer Language")
    public String language = "en";

    @Option(name = "-analyzeWithHuggingFaceTokenizer",
        usage = "search a collection by tokenizing query with pretrained mbert tokenizer")
    public String analyzeWithHuggingFaceTokenizer = null;

    @Option(name = "-inmem", usage = "Boolean switch to read index in memory")
    public Boolean inmem = false;

    @Option(name = "-topicfield", usage = "Which field of the query should be used, default \"title\"." +
        " For TREC ad hoc topics, description or narrative can be used.")
    public String topicfield = "title";

    @Option(name = "-removeQuery", usage = "Remove docids that have the query id when writing final run output.")
    public Boolean removeQuery = false;

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

    @Option(name = "-pretokenized", usage = "Boolean switch to accept pre tokenized jsonl.")
    public boolean pretokenized = false;

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

    @Option(name = "-format", metaVar = "[output format]", usage = "Output format, default \"trec\", alternative \"msmarco\".")
    public String format = "trec";

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

    // ----------------------------------------------------------
    // ranking model: impact scores (basically, just sum of tf's)
    // ----------------------------------------------------------

    @Option(name = "-impact",
        forbids = {"-bm25", "-qld", "-qljm", "-inl2", "-spl", "-f2exp", "-f2log"},
        usage = "ranking model: BM25")
    public boolean impact = false;

    // -------------------
    // ranking model: bm25
    // -------------------

    @Option(name = "-bm25",
        forbids = {"-impact", "-qld", "-qljm", "-inl2", "-spl", "-f2exp", "-f2log"},
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
        forbids = {"-impact", "-bm25", "-qljm", "-inl2", "-spl", "-f2exp", "-f2log"},
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
        forbids = {"-impact", "-bm25", "-qld", "-inl2", "-spl", "-f2exp", "-f2log"},
        usage = "ranking model: query likelihood with Jelinek-Mercer smoothing")
    public boolean qljm = false;

    @Option(name = "-qljm.lambda", handler = StringArrayOptionHandler.class, usage = "qljm: lambda smoothing parameter")
    public String[] qljm_lambda = new String[]{"0.1"};

    // -----------------------------------------
    // other ranking models (less commonly used)
    // -----------------------------------------

    @Option(name = "-inl2",
        forbids = {"-impact", "bm25", "-qld", "-qljm", "-spl", "-f2exp", "-f2log"},
        usage = "use I(n)L2 scoring model")
    public boolean inl2 = false;

    @Option(name = "-inl2.c", metaVar = "[value]", usage = "I(n)L2 c parameter")
    public String[] inl2_c = new String[]{"0.1"};

    @Option(name = "-spl",
        forbids = {"-impact", "bm25", "-qld", "-qljm", "-inl2", "-f2exp", "-f2log"},
        usage = "use SPL scoring model")
    public boolean spl = false;

    @Option(name = "-spl.c", metaVar = "[value]", usage = "SPL c parameter")
    public String[] spl_c = new String[]{"0.1"};

    @Option(name = "-f2exp",
        forbids = {"-impact", "bm25", "-qld", "-qljm", "-inl2", "-spl", "-f2log"},
        usage = "use F2Exp scoring model")
    public boolean f2exp = false;

    @Option(name = "-f2exp.s", metaVar = "[value]", usage = "F2Exp s parameter")
    public String[] f2exp_s = new String[]{"0.5"};

    @Option(name = "-f2log",
        forbids = {"-impact", "bm25", "-qld", "-qljm", "-inl2", "-spl", "-f2exp"},
        usage = "use F2Log scoring model")
    public boolean f2log = false;

    @Option(name = "-f2log.s", metaVar = "[value]", usage = "F2Log s parameter")
    public String[] f2log_s = new String[]{"0.5"};

    // -------------------------------------------
    // options for the sequential dependence model
    // -------------------------------------------

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

    @Option(name = "-rm3.noTermFilter",
        usage = "RM3 parameter: turn off English term filter")
    public boolean rm3_noTermFilter = false;

    // ------------------------------
    // query expansion model: rocchio
    // ------------------------------

    // Anserini uses as defaults the same topFbTerms, topFbDocs, bottomFbTerms and bottomFbDocs settings as RM3.
    // For alpha/beta/gamma weights, we use the setting referenced in the Manning et al. textbook:
    // https://nlp.stanford.edu/IR-book/html/htmledition/the-rocchio71-algorithm-1.html

    @Option(name = "-rocchio", usage = "use rocchio query expansion model")
    public boolean rocchio = false;

    @Option(name = "-rocchio.topFbTerms", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: number of expansion relevant terms")
    public String[] rocchio_topFbTerms = new String[]{"10"};

    @Option(name = "-rocchio.topFbDocs", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: number of expansion relevant documents")
    public String[] rocchio_topFbDocs = new String[]{"10"};

    @Option(name = "-rocchio.bottomFbTerms", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: number of expansion nonrelevant terms")
    public String[] rocchio_bottomFbTerms = new String[]{"10"};

    @Option(name = "-rocchio.bottomFbDocs", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: number of expansion nonrelevant documents")
    public String[] rocchio_bottomFbDocs = new String[]{"10"};

    @Option(name = "-rocchio.alpha", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: weight to assign to the original query")
    public String[] rocchio_alpha = new String[]{"1"};

    @Option(name = "-rocchio.beta", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: weight to assign to the relevant document vectors")
    public String[] rocchio_beta = new String[]{"0.75"};

    @Option(name = "-rocchio.gamma", handler = StringArrayOptionHandler.class,
        usage = "Rocchio parameter: weight to assign to the nonrelevant document vectors")
    public String[] rocchio_gamma = new String[]{"0.15"};

    @Option(name = "-rocchio.useNegative",
        usage = "Rocchio parameter: flag to use nonrelevant document vectors")
    public boolean rocchio_useNegative = false;

    @Option(name = "-rocchio.outputQuery",
        usage = "Rocchio parameter: flag to print original and expanded queries")
    public boolean rocchio_outputQuery = false;

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
    public Args impact() {
      this.impact = true;
      this.bm25 = false;
      this.qld = false;
      this.qljm = false;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args bm25() {
      this.impact = false;
      this.bm25 = true;
      this.qld = false;
      this.qljm = false;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args qld() {
      this.impact = false;
      this.bm25 = false;
      this.qld = true;
      this.qljm = false;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args qljm() {
      this.impact = false;
      this.bm25 = false;
      this.qld = false;
      this.qljm = true;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args inl2() {
      this.impact = false;
      this.bm25 = false;
      this.qld = false;
      this.qljm = false;
      this.inl2 = true;
      this.spl = false;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args spl() {
      this.impact = false;
      this.bm25 = false;
      this.qld = false;
      this.qljm = false;
      this.inl2 = false;
      this.spl = true;
      this.f2exp = false;
      this.f2log = false;

      return this;
    }

    public Args f2exp() {
      this.impact = false;
      this.bm25 = false;
      this.qld = false;
      this.qljm = false;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = true;
      this.f2log = false;

      return this;
    }

    public Args f2log() {
      this.impact = false;
      this.bm25 = false;
      this.qld = false;
      this.qljm = false;
      this.inl2 = false;
      this.spl = false;
      this.f2exp = false;
      this.f2log = true;

      return this;
    }

    public Args searchTweets() {
      this.searchtweets = true;
      return this;
    }

  }

  private final Args args;
  private final IndexReader reader;
  private final Analyzer analyzer;
  private final Class collectionClass;
  private List<TaggedSimilarity> similarities;
  private List<RerankerCascade> cascades;
  private final boolean isRerank;
  private Map<String, ScoredDocuments> qrels;
  private Set<String> queriesWithRel;

  private final class SearcherThread<K> extends Thread {
    final private IndexReader reader;
    final private IndexSearcher searcher;
    final private SortedMap<K, Map<String, String>> topics;
    final private TaggedSimilarity taggedSimilarity;
    final private RerankerCascade cascade;
    final private String outputPath;
    final private String runTag;

    private SearcherThread(IndexReader reader, SortedMap<K, Map<String, String>> topics, TaggedSimilarity taggedSimilarity,
                           RerankerCascade cascade, Map<String, ScoredDocuments> qrels, String outputPath, String runTag) {
      this.reader = reader;
      this.topics = topics;
      this.taggedSimilarity = taggedSimilarity;
      this.cascade = cascade;
      this.runTag = runTag;
      this.outputPath = outputPath;
      this.searcher = new IndexSearcher(this.reader);
      this.searcher.setSimilarity(this.taggedSimilarity.getSimilarity());
      setName(outputPath);
    }

    @Override
    public void run() {
      try {
        // A short descriptor of the ranking setup.
        final String desc = String.format("ranker: %s, reranker: %s", taggedSimilarity.getTag(), cascade.getTag());

        // This is the number of threads that we're going to devote to running the queries in parallel.
        int parallelism = args.parallelism;

        // ThreadPool for parallelizing the execution of individual queries:
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(parallelism);
        // Data structure for holding the per-query results, with the qid as the key and the results (the lines that
        // will go into the final run file) as the value.
        ConcurrentSkipListMap<K, String> results = new ConcurrentSkipListMap<>();
        AtomicInteger cnt = new AtomicInteger();

        final long start = System.nanoTime();
        for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
          K qid = entry.getKey();

          // This is the per-query execution, in parallel.
          executor.execute(() -> {
            // This is for holding the results.
            StringBuilder out = new StringBuilder();

            String queryString = "";
            if (args.topicfield.contains("+")) {
              for (String field : args.topicfield.split("\\+")) {
                queryString += " " + entry.getValue().get(field);
              }
            } else {
              queryString = entry.getValue().get(args.topicfield);
            }

            ScoredDocuments queryQrels = null;
            boolean hasRelDocs = false;
            String qidString = qid.toString();
            if (qrels != null) {
              queryQrels = qrels.get(qidString);
              if (queriesWithRel.contains(qidString)) {
                hasRelDocs = true;
              }
            }
            ScoredDocuments docs;
            try {
              if (args.searchtweets) {
                docs = searchTweets(this.searcher, qid, queryString,
                    Long.parseLong(entry.getValue().get("time")), cascade, queryQrels, hasRelDocs);
              } else if (args.backgroundlinking) {
                docs = searchBackgroundLinking(this.searcher, qid, queryString, cascade);
              } else {
                docs = search(this.searcher, qid, queryString, cascade, queryQrels, hasRelDocs);
              }
            } catch (IOException e) {
              throw new CompletionException(e);
            }

            // For removing duplicate docids.
            Set<String> docids = new HashSet<>();

            int rank = 1;
            for (int i = 0; i < docs.documents.length; i++) {
              String docid = docs.documents[i].get(Constants.ID);

              if (args.selectMaxPassage) {
                docid = docid.split(args.selectMaxPassage_delimiter)[0];
              }

              if (docids.contains(docid))
                continue;

              // Remove docids that are identical to the query id if flag is set.
              if (args.removeQuery && docid.equals(qid))
                continue;

              if ("msmarco".equals(args.format)) {
                // MS MARCO output format:
                out.append(String.format(Locale.US, "%s\t%s\t%d\n", qid, docid, rank));
              } else {
                // Standard TREC format:
                // + the first column is the topic number.
                // + the second column is currently unused and should always be "Q0".
                // + the third column is the official document identifier of the retrieved document.
                // + the fourth column is the rank the document is retrieved.
                // + the fifth column shows the score (integer or floating point) that generated the ranking.
                // + the sixth column is called the "run tag" and should be a unique identifier for your
                out.append(String.format(Locale.US, "%s Q0 %s %d %f %s\n",
                    qid, docid, rank, docs.scores[i], runTag));
              }

              // Note that this option is set to false by default because duplicate documents usually indicate some
              // underlying indexing issues, and we don't want to just eat errors silently.
              //
              // However, we we're performing passage retrieval, i.e., with "selectMaxSegment", we *do* want to remove
              // duplicates.
              if (args.removedups || args.selectMaxPassage) {
                docids.add(docid);
              }

              rank++;

              if (args.selectMaxPassage && rank > args.selectMaxPassage_hits) {
                break;
              }
            }

            results.put(qid, out.toString());
            int n = cnt.incrementAndGet();
            if (n % 100 == 0) {
              LOG.info(String.format("%s: %d queries processed", desc, n));
            }
          });
        }

        executor.shutdown();

        try {
          // Wait for existing tasks to terminate.
          while (!executor.awaitTermination(1, TimeUnit.MINUTES)) ;
        } catch (InterruptedException ie) {
          // (Re-)Cancel if current thread also interrupted.
          executor.shutdownNow();
          // Preserve interrupt status.
          Thread.currentThread().interrupt();
        }
        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);

        LOG.info(desc + ": " + topics.size() + " queries processed in " +
            DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss") +
            String.format(" = ~%.2f q/s", topics.size() / (durationMillis / 1000.0)));

        // Now we write the results to a run file.
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8));

        // Here's a really screwy corner case that we have to manually hack around: for MS MARCO V1, the query file is not
        // sorted by qid, but the topic representation internally is (i.e., K is a comparable). The original query runner
        // SearchMsmarco retained the order of the queries; however, this class does not. Thus, the run files list the
        // results in different orders. Due to the way that the MS MARCO V1 eval scripts are written (they report MRR to
        // an excessive number of significant digits), different orders yield slightly different metric values (due to
        // floating point precision issues). Just to retain exactly the same output as SearchMsmarco (which was used to,
        // for example, generate Anserini leaderboard runs), we add an ugly hack here to dump the results in the order
        // of the qids in the query files.
        boolean isMSMARCOv1_passage = topics.firstKey().equals(2) &&
            topics.get(2).get("title").equals("Androgen receptor define") &&
            topics.keySet().size() == 6980;
        boolean isMAMARCOv1_doc = topics.firstKey().equals(2) &&
            topics.get(2).get("title").equals("androgen receptor define") &&
            topics.keySet().size() == 5193;

        if (isMSMARCOv1_passage || isMAMARCOv1_doc) {
          String raw = "";
          try {
            InputStream inputStream = null;
            if (isMSMARCOv1_passage) {
              inputStream = TopicReader.class.getClassLoader().getResourceAsStream(Topics.MSMARCO_PASSAGE_DEV_SUBSET.path);
            } else {
              inputStream = TopicReader.class.getClassLoader().getResourceAsStream(Topics.MSMARCO_DOC_DEV.path);
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
              line = line.trim();
              String[] arr = line.split("\\t");
              out.print(results.get(Integer.parseInt(arr[0])));
            }

            inputStream.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        } else {
          // This is the default case: just dump out the qids by their natural order.
          for (K qid : results.keySet()) {
            out.print(results.get(qid));
          }
        }
        out.flush();
        out.close();

      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception: ", e);
      }
    }
  }

  public SearchCollection(Args args) throws IOException {
    this.args = args;
    Path indexPath = Paths.get(args.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(String.format("Index path '%s' does not exist or is not a directory.", args.index));
    }

    LOG.info("============ Initializing Searcher ============");
    LOG.info("Index: " + indexPath);
    this.reader = args.inmem ? DirectoryReader.open(MMapDirectory.open(indexPath)) :
        DirectoryReader.open(FSDirectory.open(indexPath));

    LOG.info("Fields: " + Arrays.toString(args.fields));
    if (args.fields.length != 0) {
      // The -fields argument should be in the form of "field1=weight1 field2=weight2...".
      // Try to parse, and throw exception if anything goes wrong.
      try {
        for (String part : args.fields) {
          String[] tok = part.split("=");
          args.fieldsMap.put(tok[0], Float.parseFloat(tok[1]));
        }
      } catch (Exception e) {
        throw new IllegalArgumentException("Error parsing -fields parameter: " + Arrays.toString(args.fields));
      }
    }

    // get collection class if available
    if (args.collectionClass != null) {
      Class getCollectionClass;
      try {
        getCollectionClass = Class.forName("io.anserini.collection." + args.collectionClass);
      } catch (ClassNotFoundException e) {
        getCollectionClass = null;
        System.out.println("collectionClass: " + args.collectionClass + " NOT FOUND!");
      }
      this.collectionClass = getCollectionClass;
    } else {
      this.collectionClass = null;
    }

    // Are we searching tweets?
    if (args.searchtweets) {
      LOG.info("Searching tweets? true");
      analyzer = new TweetAnalyzer();
    } else if (args.analyzeWithHuggingFaceTokenizer != null){
      analyzer = new HuggingFaceTokenizerAnalyzer(args.analyzeWithHuggingFaceTokenizer);
      LOG.info("Bert Tokenizer");
    } else if (args.language.equals("ar")) {
      analyzer = new ArabicAnalyzer();
      LOG.info("Language: ar");
    } else if (args.language.equals("bn")) {
      analyzer = new BengaliAnalyzer();
      LOG.info("Language: bn");
    } else if (args.language.equals("da")) {
      analyzer = new DanishAnalyzer();
      LOG.info("Language: da");
    } else if (args.language.equals("de")) {
      analyzer = new GermanAnalyzer();
      LOG.info("Language: de");
    } else if (args.language.equals("es")) {
      analyzer = new SpanishAnalyzer();
      LOG.info("Language: es");
    } else if (args.language.equals("fa")) {
      analyzer = new PersianAnalyzer();
      LOG.info("Language: fa");
    } else if (args.language.equals("fi")) {
      analyzer = new FinnishAnalyzer();
      LOG.info("Language: fi");
    } else if (args.language.equals("fr")) {
      analyzer = new FrenchAnalyzer();
      LOG.info("Language: fr");
    } else if (args.language.equals("hi")) {
      analyzer = new HindiAnalyzer();
      LOG.info("Language: hi");
    } else if (args.language.equals("hu")) {
      analyzer = new HungarianAnalyzer();
      LOG.info("Language: hu");
    } else if (args.language.equals("id")) {
      analyzer = new IndonesianAnalyzer();
      LOG.info("Language: id");
    } else if (args.language.equals("it")) {
      analyzer = new ItalianAnalyzer();
      LOG.info("Language: it");
    } else if (args.language.equals("ja")) {
      analyzer = new JapaneseAnalyzer();
      LOG.info("Language: ja");
    } else if (args.language.equals("ko")) {
      analyzer = new CJKAnalyzer();
      LOG.info("Language: ko");
    } else if (args.language.equals("nl")) {
      analyzer = new DutchAnalyzer();
      LOG.info("Language: nl");
    } else if (args.language.equals("no")) {
      analyzer = new NorwegianAnalyzer();
      LOG.info("Language: no");
    } else if (args.language.equals("pl")) {
      analyzer = new MorfologikAnalyzer();
      LOG.info("Language: pl");
    } else if (args.language.equals("pt")) {
      analyzer = new PortugueseAnalyzer();
      LOG.info("Language: pt");
    } else if (args.language.equals("ru")) {
      analyzer = new RussianAnalyzer();
      LOG.info("Language: ru");
    } else if (args.language.equals("sv")) {
      analyzer = new SwedishAnalyzer();
      LOG.info("Language: sv");
    } else if (args.language.equals("te")) {
      analyzer = new TeluguAnalyzer();
      LOG.info("Language: te");
    } else if (args.language.equals("th")) {
      analyzer = new ThaiAnalyzer();
      LOG.info("Language: th");
    } else if (args.language.equals("tr")) {
      analyzer = new TurkishAnalyzer();
      LOG.info("Language: tr");
    } else if (args.language.equals("uk")) {
      analyzer = new UkrainianMorfologikAnalyzer();
      LOG.info("Language: uk");
    } else if (args.language.equals("zh")) {
      analyzer = new CJKAnalyzer();
      LOG.info("Language: zh");
    } else if (args.pretokenized || args.language.equals("sw") || args.language.equals("te")) {
      analyzer = new WhitespaceAnalyzer();
      LOG.info("Pretokenized");
    } else {
      // Default to English
      analyzer = DefaultEnglishAnalyzer.fromArguments(args.stemmer, args.keepstop, args.stopwords);
      LOG.info("Language: en");
      LOG.info("Stemmer: " + args.stemmer);
      LOG.info("Keep stopwords? " + args.keepstop);
      LOG.info("Stopwords file: " + args.stopwords);
      LOG.info("Number of threads for running different parameter configurations: " + args.threads);
      LOG.info("Number of threads for running each individual parameter configuration: " + args.parallelism);
    }

    isRerank = args.rm3 || args.axiom || args.bm25prf || args.rocchio;

    if (this.isRerank && args.rf_qrels != null) {
      loadQrels(args.rf_qrels);
    }

    // Fix for index compatibility issue between Lucene 8 and 9: https://github.com/castorini/anserini/issues/1952
    // If we detect an older index version, we turn off consistent tie-breaking, which avoids accessing docvalues,
    // which is the source of the incompatibility.
    if (!reader.toString().contains("lucene.version=9")) {
      args.arbitraryScoreTieBreak = true;
      args.axiom_deterministic = false;
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  private List<TaggedSimilarity> constructSimilarities() {
    List<TaggedSimilarity> similarities = new ArrayList<>();

    if (args.bm25) {
      for (String k1 : args.bm25_k1) {
        for (String b : args.bm25_b) {
          similarities.add(new TaggedSimilarity(new BM25Similarity(Float.valueOf(k1), Float.valueOf(b)),
              String.format("bm25(k1=%s,b=%s)", k1, b)));
        }
      }
    } else if (args.bm25Accurate) {
      for (String k1 : args.bm25_k1) {
        for (String b : args.bm25_b) {
          similarities.add(new TaggedSimilarity(new AccurateBM25Similarity(Float.valueOf(k1), Float.valueOf(b)),
              String.format("bm25accurate(k1=%s,b=%s)", k1, b)));
        }
      }
    } else if (args.qld) {
      for (String mu : args.qld_mu) {
        similarities.add(new TaggedSimilarity(new LMDirichletSimilarity(Float.valueOf(mu)),
            String.format("qld(mu=%s)", mu)));
      }
    } else if (args.qljm) {
      for (String lambda : args.qljm_lambda) {
        similarities.add(new TaggedSimilarity(new LMJelinekMercerSimilarity(Float.valueOf(lambda)),
            String.format("qljm(lambda=%s)", lambda)));
      }
    } else if (args.inl2) {
      for (String c : args.inl2_c) {
        similarities.add(new TaggedSimilarity(
            new DFRSimilarity(new BasicModelIn(), new AfterEffectL(), new NormalizationH2(Float.valueOf(c))),
            String.format("inl2(c=%s)", c)));
      }
    } else if (args.spl) {
      for (String c : args.spl_c) {
        similarities.add(new TaggedSimilarity(
            new IBSimilarity(new DistributionSPL(), new LambdaDF(), new NormalizationH2(Float.valueOf(c))),
            String.format("spl(c=%s)", c)));
      }
    } else if (args.f2exp) {
      for (String s : args.f2exp_s) {
        similarities.add(new TaggedSimilarity(new AxiomaticF2EXP(Float.valueOf(s)), String.format("f2exp(s=%s)", s)));
      }
    } else if (args.f2log) {
      for (String s : args.f2log_s) {
        similarities.add(new TaggedSimilarity(new AxiomaticF2LOG(Float.valueOf(s)), String.format("f2log(s=%s)", s)));
      }
    } else if (args.impact) {
      similarities.add(new TaggedSimilarity(new ImpactSimilarity(), "impact()"));
    } else {
      throw new IllegalArgumentException("Error: Must specify scoring model!");
    }
    return similarities;
  }

  private List<RerankerCascade> constructRerankers() throws IOException {
    List<RerankerCascade> cascades = new ArrayList<>();

    if (args.rm3) {
      for (String fbTerms : args.rm3_fbTerms) {
        for (String fbDocs : args.rm3_fbDocs) {
          for (String originalQueryWeight : args.rm3_originalQueryWeight) {
            String tag;
            if (this.args.rf_qrels != null) {
              tag = String.format("rm3Rf(fbTerms=%s,originalQueryWeight=%s)",
                  fbTerms, originalQueryWeight);
            } else {
              tag = String.format("rm3(fbTerms=%s,fbDocs=%s,originalQueryWeight=%s)",
                  fbTerms, fbDocs, originalQueryWeight);
            }

            RerankerCascade cascade = new RerankerCascade(tag);
            cascade.add(new Rm3Reranker(analyzer, collectionClass, Constants.CONTENTS, Integer.valueOf(fbTerms),
                Integer.valueOf(fbDocs), Float.valueOf(originalQueryWeight), args.rm3_outputQuery,
                !args.rm3_noTermFilter));
            cascade.add(new ScoreTiesAdjusterReranker());
            cascades.add(cascade);
          }
        }
      }
    } else if (args.axiom) {
      for (String r : args.axiom_r) {
        for (String n : args.axiom_n) {
          for (String beta : args.axiom_beta) {
            for (String top : args.axiom_top) {
              for (String seed : args.axiom_seed) {
                String tag;
                if (this.args.rf_qrels != null) {
                  tag = String.format("axRf(seed=%s,n=%s,beta=%s,top=%s)", seed, n, beta, top);
                } else {
                  tag = String.format("ax(seed=%s,r=%s,n=%s,beta=%s,top=%s)", seed, r, n, beta, top);
                }
                RerankerCascade cascade = new RerankerCascade(tag);
                cascade.add(new AxiomReranker(analyzer, collectionClass, args.index, args.axiom_index, Constants.CONTENTS,
                    args.axiom_deterministic, Integer.valueOf(seed), Integer.valueOf(r),
                    Integer.valueOf(n), Float.valueOf(beta), Integer.valueOf(top),
                    args.axiom_docids, args.axiom_outputQuery, args.searchtweets));
                cascade.add(new ScoreTiesAdjusterReranker());
                cascades.add(cascade);
              }
            }
          }
        }
      }
    } else if (args.bm25prf) {
      for (String fbTerms : args.bm25prf_fbTerms) {
        for (String fbDocs : args.bm25prf_fbDocs) {
          for (String k1 : args.bm25prf_k1) {
            for (String b : args.bm25prf_b) {
              for (String newTermWeight : args.bm25prf_newTermWeight) {
                String tag;
                if (this.args.rf_qrels != null) {
                  tag = String.format("bm25Rf(fbTerms=%s,k1=%s,b=%s,newTermWeight=%s)",
                      fbTerms, k1, b, newTermWeight);
                } else {
                  tag = String.format("bm25prf(fbTerms=%s,fbDocs=%s,k1=%s,b=%s,newTermWeight=%s)",
                      fbTerms, fbDocs, k1, b, newTermWeight);
                }
                RerankerCascade cascade = new RerankerCascade(tag);
                cascade.add(new BM25PrfReranker(analyzer, collectionClass, Constants.CONTENTS, Integer.valueOf(fbTerms),
                    Integer.valueOf(fbDocs), Float.valueOf(k1), Float.valueOf(b), Float.valueOf(newTermWeight),
                    args.bm25prf_outputQuery));
                cascade.add(new ScoreTiesAdjusterReranker());
                cascades.add(cascade);
              }
            }
          }
        }
      }
    } else if (args.rocchio) {
      for (String topFbTerms : args.rocchio_topFbTerms) {
        for (String topFbDocs : args.rocchio_topFbDocs) {
          for (String bottomFbTerms : args.rocchio_bottomFbTerms) {
            for (String bottomFbDocs : args.rocchio_bottomFbDocs) {
              for (String alpha : args.rocchio_alpha) {
                for (String beta : args.rocchio_beta) {
                  for (String gamma : args.rocchio_gamma) {
                    String tag;
                    if (args.rocchio_useNegative == false) {
                      gamma = "0";
                    }
                    if (this.args.rf_qrels != null) {
                      tag = String.format("rocchioRf(topFbTerms=%s,bottomFbTerms=%s,alpha=%s,beta=%s,gamma=%s)", topFbTerms, bottomFbTerms, alpha, beta, gamma);
                    } else {
                      tag = String.format("rocchio(topFbTerms=%s,topFbDocs=%s,bottomFbTerms=%s,bottomFbDocs=%s,alpha=%s,beta=%s,gamma=%s)", topFbTerms, topFbDocs, bottomFbTerms, bottomFbDocs, alpha, beta, gamma);
                    }
                    RerankerCascade cascade = new RerankerCascade(tag);
                    cascade.add(new RocchioReranker(analyzer, collectionClass, Constants.CONTENTS, Integer.valueOf(topFbTerms),
                        Integer.valueOf(topFbDocs), Integer.valueOf(bottomFbTerms), Integer.valueOf(bottomFbDocs),
                        Float.valueOf(alpha), Float.valueOf(beta), Float.valueOf(gamma), args.rocchio_outputQuery, args.rocchio_useNegative));
                    cascade.add(new ScoreTiesAdjusterReranker());
                    cascades.add(cascade);
                  }
                }
              }
            }
          }
        }
      }
    } else {
      RerankerCascade cascade = new RerankerCascade();
      cascade.add(new ScoreTiesAdjusterReranker());
      cascades.add(cascade);
    }

    return cascades;
  }

  private void loadQrels(String rf_qrels) throws IOException {
    LOG.info("============ Loading qrels ============");
    LOG.info("rf_qrels: " + rf_qrels);
    Path rfQrelsFilePath = Paths.get(rf_qrels);
    if (!Files.exists(rfQrelsFilePath) || !Files.isRegularFile(rfQrelsFilePath) || !Files.isReadable(rfQrelsFilePath)) {
      throw new IllegalArgumentException("Qrels file : " + rfQrelsFilePath + " does not exist or is not a (readable) file.");
    }
    Map<String, Map<String, Integer>> qrelsDocs = new HashMap<>();
    this.queriesWithRel = new HashSet<>();
    InputStream fin = Files.newInputStream(Paths.get(rf_qrels), StandardOpenOption.READ);
    BufferedInputStream in = new BufferedInputStream(fin);
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    for (String line : IOUtils.readLines(reader)) {
      String[] cols = line.split("\\s+");
      int rel = Integer.valueOf(cols[3]);
      String qid = cols[0];
      if (rel > 0) {
        this.queriesWithRel.add(qid);
      }
      String fbDocid = cols[2];
      Map<String, Integer> queryQrelsDocs = qrelsDocs.get(qid);
      if (queryQrelsDocs == null) {
        queryQrelsDocs = new HashMap<>();
        qrelsDocs.put(qid, queryQrelsDocs);
      }
      queryQrelsDocs.put(fbDocid, Integer.valueOf(rel));
    }

    this.qrels = new HashMap<>();
    for (Map.Entry<String, Map<String, Integer>> q : qrelsDocs.entrySet()) {
      String qid = q.getKey();
      Map<String, Integer> queryQrelsDocs = q.getValue();
      this.qrels.put(qid, ScoredDocuments.fromQrels(queryQrelsDocs, this.reader));
    }

  }

  @SuppressWarnings("unchecked")
  public <K> void runTopics() throws IOException {
    TopicReader<K> tr;
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();
    for (String singleTopicsFile : args.topics) {
      Path topicsFilePath = Paths.get(singleTopicsFile);
      if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        throw new IllegalArgumentException("Topics file : " + topicsFilePath + " does not exist or is not a (readable) file.");
      }
      try {
        tr = (TopicReader<K>) Class.forName("io.anserini.search.topicreader." + args.topicReader + "TopicReader")
            .getConstructor(Path.class).newInstance(topicsFilePath);
        topics.putAll(tr.read());
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Unable to load topic reader: " + args.topicReader);
      }
    }

    final String runTag = args.runtag == null ? "Anserini" : args.runtag;
    LOG.info("runtag: " + runTag);

    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    this.similarities = constructSimilarities();
    this.cascades = constructRerankers();

    LOG.info("============ Launching Search Threads ============");

    for (TaggedSimilarity taggedSimilarity : similarities) {
      for (RerankerCascade cascade : cascades) {
        final String outputPath;

        if (similarities.size() == 1 && cascades.size() == 1) {
          outputPath = args.output;
        } else {
          outputPath = String.format("%s_%s_%s", args.output, taggedSimilarity.getTag(), cascade.getTag());
        }

        if (args.skipexists && new File(outputPath).exists()) {
          LOG.info("Run already exists, skipping: " + outputPath);
          continue;
        }
        executor.execute(new SearcherThread<>(reader, topics, taggedSimilarity, cascade, this.qrels, outputPath, runTag));
      }
    }
    executor.shutdown();

    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {
      }
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  public <K> ScoredDocuments search(IndexSearcher searcher, K qid, String queryString, RerankerCascade cascade, ScoredDocuments queryQrels,
                                    boolean hasRelDocs) throws IOException {
    Query query;

    if (args.sdm) {
      query = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(Constants.CONTENTS, analyzer, queryString);
    } else {
      QueryGenerator generator;
      try {
        generator = (QueryGenerator) Class.forName("io.anserini.search.query." + args.queryGenerator)
            .getConstructor().newInstance();
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Unable to load QueryGenerator: " + args.topicReader);
      }

      // If fieldsMap isn't null, then it means that the -fields option is specified. In this case, we search across
      // multiple fields with the associated boosts.
      query = args.fields.length == 0 ? generator.buildQuery(Constants.CONTENTS, analyzer, queryString) :
          generator.buildQuery(args.fieldsMap, analyzer, queryString);
    }

    TopDocs rs = new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[]{});
    if (!isRerank || (args.rerankcutoff > 0 && args.rf_qrels == null) || (args.rf_qrels != null && !hasRelDocs)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(query, (isRerank && args.rf_qrels == null) ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(query, (isRerank && args.rf_qrels == null) ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true);
      }
    }

    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, queryString);
    RerankerContext context = new RerankerContext<>(searcher, qid, query, null, queryString, queryTokens, null, args);
    ScoredDocuments scoredFbDocs;
    if (isRerank && args.rf_qrels != null) {
      if (hasRelDocs) {
        scoredFbDocs = queryQrels;
      } else {//if no relevant documents, only perform score based tie breaking next
        LOG.info("No relevant documents for " + qid.toString());
        scoredFbDocs = ScoredDocuments.fromTopDocs(rs, searcher);
        cascade = new RerankerCascade();
        cascade.add(new ScoreTiesAdjusterReranker());
      }
    } else {
      scoredFbDocs = ScoredDocuments.fromTopDocs(rs, searcher);
    }

    return cascade.run(scoredFbDocs, context);
  }

  public <K> ScoredDocuments searchBackgroundLinking(IndexSearcher searcher, K qid, String docid,
                                                     RerankerCascade cascade) throws IOException {
    // Extract a list of analyzed terms from the document to compose a query.
    List<String> terms = BackgroundLinkingTopicReader.extractTerms(reader, docid, args.backgroundlinking_k, analyzer);
    // Since the terms are already analyzed, we just join them together and use the StandardQueryParser.
    Query docQuery;
    try {
      docQuery = new StandardQueryParser().parse(StringUtils.join(terms, " "), Constants.CONTENTS);
    } catch (QueryNodeException e) {
      throw new RuntimeException("Unable to create a Lucene query comprised of terms extracted from query document!");
    }

    // Per track guidelines, no opinion or editorials. Filter out articles of these types.
    Query filter = new TermInSetQuery(
        WashingtonPostGenerator.WashingtonPostField.KICKER.name, new BytesRef("Opinions"),
        new BytesRef("Letters to the Editor"), new BytesRef("The Post's View"));

    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filter, BooleanClause.Occur.MUST_NOT);
    builder.add(docQuery, BooleanClause.Occur.MUST);
    Query query = builder.build();

    // Search using constructed query.
    TopDocs rs;
    if (args.arbitraryScoreTieBreak) {
      rs = searcher.search(query, (isRerank && args.rf_qrels == null) ? args.rerankcutoff : args.hits);
    } else {
      rs = searcher.search(query, (isRerank && args.rf_qrels == null) ? args.rerankcutoff :
          args.hits, BREAK_SCORE_TIES_BY_DOCID, true);
    }

    RerankerContext context = new RerankerContext<>(searcher, qid, query, docid,
        StringUtils.join(", ", terms), terms, null, args);

    // Run the existing cascade.
    ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    // Perform post-processing (e.g., date filter, dedupping, etc.) as a final step.
    return new NewsBackgroundLinkingReranker(analyzer, collectionClass).rerank(docs, context);
  }

  public <K> ScoredDocuments searchTweets(IndexSearcher searcher, K qid, String queryString, long t, RerankerCascade cascade,
                                          ScoredDocuments queryQrels, boolean hasRelDocs) throws IOException {
    Query keywordQuery;
    if (args.sdm) {
      keywordQuery = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(Constants.CONTENTS, analyzer, queryString);
    } else {
      try {
        QueryGenerator generator = (QueryGenerator) Class.forName("io.anserini.search.query." + args.queryGenerator)
            .getConstructor().newInstance();
        keywordQuery = generator.buildQuery(Constants.CONTENTS, analyzer, queryString);
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalArgumentException("Unable to load QueryGenerator: " + args.topicReader);
      }
    }
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, queryString);

    // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
    // <querytweettime> tag contains the timestamp of the query in terms of the
    // chronologically nearest tweet id within the corpus
    Query filter = LongPoint.newRangeQuery(TweetGenerator.TweetField.ID_LONG.name, 0L, t);
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filter, BooleanClause.Occur.FILTER);
    builder.add(keywordQuery, BooleanClause.Occur.MUST);
    Query compositeQuery = builder.build();


    TopDocs rs = new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[]{});
    if (!isRerank || (args.rerankcutoff > 0 && args.rf_qrels == null) || (args.rf_qrels != null && !hasRelDocs)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(compositeQuery, (isRerank && args.rf_qrels == null) ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(compositeQuery, (isRerank && args.rf_qrels == null) ? args.rerankcutoff : args.hits,
            BREAK_SCORE_TIES_BY_TWEETID, true);
      }
    }

    RerankerContext context = new RerankerContext<>(searcher, qid, keywordQuery, null, queryString, queryTokens, filter, args);
    ScoredDocuments scoredFbDocs;
    if (isRerank && args.rf_qrels != null) {
      if (hasRelDocs) {
        scoredFbDocs = queryQrels;
      } else {//if no relevant documents, only perform score based tie breaking next
        scoredFbDocs = ScoredDocuments.fromTopDocs(rs, searcher);
        cascade = new RerankerCascade();
        cascade.add(new ScoreTiesAdjusterReranker());
      }
    } else {
      scoredFbDocs = ScoredDocuments.fromTopDocs(rs, searcher);
    }

    return cascade.run(scoredFbDocs, context);
  }

  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchCollection" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SearchCollection searcher;

    // We're at top-level already inside a main; makes no sense to propagate exceptions further, so reformat the
    // exception messages and display on console.
    try {
      searcher = new SearchCollection(searchArgs);
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
      return;
    }

    searcher.runTopics();
    searcher.close();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
