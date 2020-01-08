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

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.index.generator.WapoGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.AxiomReranker;
import io.anserini.rerank.lib.BM25PrfReranker;
import io.anserini.rerank.lib.NewsBackgroundLinkingReranker;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.SdmQueryGenerator;
import io.anserini.search.similarity.AccurateBM25Similarity;
import io.anserini.search.similarity.TaggedSimilarity;
import io.anserini.search.topicreader.NewsBackgroundLinkingTopicReader;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.bn.BengaliAnalyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.de.GermanAnalyzer;
import org.apache.lucene.analysis.es.SpanishAnalyzer;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.hi.HindiAnalyzer;
import org.apache.lucene.document.Document;
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
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

/*
* Entry point of the Retrieval.
* More advanced usage: one can provide multiple parameters to ranking models so that
* they can be processed in parallel and this also applies to the reranking methods.
* For example, one'd like to run BM25 with b=0.2 and b=0.75 then the command can be
* simplified as:
* <pre>SearchCollection -index /path/to/index/ -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.51-100.txt -inmem -threads 2 -bm25 -b 0.2 0.75</pre>
* To run reranking with multiple params onc can do:
* <pre>SearchCollection -index /path/to/index/ -topicreader Trec -topics src/main/resources/topics-and-qrels/topics.51-100.txt -inmem -threads 4 -bm25 -b 0.2 0.75 -rm3 -rm3.fbDocs 5 10 -rm3.originalQueryWeight 0.5 0.3</pre>
* this will generate 8 runs with parallelism as 4.
 */
public final class SearchCollection implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(FIELD_ID, SortField.Type.STRING_VAL));
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.StatusField.ID_LONG.name, SortField.Type.LONG, true));

  private static final Logger LOG = LogManager.getLogger(SearchCollection.class);

  private final SearchArgs args;
  private final IndexReader reader;
  private final Analyzer analyzer;
  private List<TaggedSimilarity> similarities;
  private final boolean isRerank;
  public enum QueryConstructor {
    BagOfTerms,
    SequentialDependenceModel
  }
  private final QueryConstructor qc;
  
  private final class SearcherThread<K> extends Thread {
    final private IndexReader reader;
    final private IndexSearcher searcher;
    final private SortedMap<K, Map<String, String>> topics;
    final private TaggedSimilarity taggedSimilarity;
    final private String cascadeTag;
    final private RerankerCascade cascade;
    final private String outputPath;
    final private String runTag;
    
    private SearcherThread(IndexReader reader, SortedMap<K, Map<String, String>> topics, TaggedSimilarity taggedSimilarity,
                           String cascadeTag, RerankerCascade cascade, String outputPath, String runTag) throws IOException {
      this.reader = reader;
      this.topics = topics;
      this.taggedSimilarity = taggedSimilarity;
      this.cascadeTag = cascadeTag;
      this.cascade = cascade;
      this.runTag = runTag;
      this.outputPath = outputPath;
      this.searcher = new IndexSearcher(this.reader);
      this.searcher.setSimilarity(this.taggedSimilarity.similarity);
      setName(outputPath);
    }
    
    @Override
    public void run() {
      try {
        LOG.info("[Start] Ranking with similarity: " + taggedSimilarity.similarity.toString());
        final long start = System.nanoTime();
        if (!cascadeTag.isEmpty()) LOG.info("ReRanking with: " + cascadeTag);
        PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.US_ASCII));
        for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
          K qid = entry.getKey();
          String queryString = entry.getValue().get(args.topicfield);
          ScoredDocuments docs;
          if (args.searchtweets) {
            docs = searchTweets(this.searcher, qid, queryString, Long.parseLong(entry.getValue().get("time")), cascade);
          } else if (args.searchnewsbackground) {
            docs = searchBackgroundLinking(this.searcher, qid, queryString, cascade);
          } else{
            docs = search(this.searcher, qid, queryString, cascade);
          }
    
          /**
           * the first column is the topic number.
           * the second column is currently unused and should always be "Q0".
           * the third column is the official document identifier of the retrieved document.
           * the fourth column is the rank the document is retrieved.
           * the fifth column shows the score (integer or floating point) that generated the ranking.
           * the sixth column is called the "run tag" and should be a unique identifier for your
           */
          for (int i = 0; i < docs.documents.length; i++) {
            out.println(String.format(Locale.US, "%s Q0 %s %d %f %s", qid,
                docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i], runTag));
          }
        }
        out.flush();
        out.close();
        final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
        LOG.info("[Finished] Ranking with similarity: " + taggedSimilarity.similarity.toString());
        LOG.info("Run " + topics.size() + " topics searched in "
            + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
      } catch (Exception e) {
        LOG.error(Thread.currentThread().getName() + ": Unexpected Exception:", e);
      }
    }
  }

  public SearchCollection(SearchArgs args) throws IOException {
    this.args = args;
    Path indexPath = Paths.get(args.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(args.index + " does not exist or is not a directory.");
    }

    LOG.info("Reading index at " + indexPath);
    if (args.inmem) {
      this.reader = DirectoryReader.open(MMapDirectory.open(indexPath));
    } else {
      this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
    }

    // Are we searching tweets?
    if (args.searchtweets) {
      LOG.info("Search Tweets");
      analyzer = new TweetAnalyzer();
    } else if (args.language.equals("zh")) {
      analyzer = new CJKAnalyzer();
    } else if (args.language.equals("ar")) {
      analyzer = new ArabicAnalyzer();
    } else if (args.language.equals("fr")) {
      analyzer = new FrenchAnalyzer();
    } else if (args.language.equals("hi")) {
      analyzer = new HindiAnalyzer();
    } else if (args.language.equals("bn")) {
      analyzer = new BengaliAnalyzer();
    } else if (args.language.equals("de")) {
      analyzer = new GermanAnalyzer();
    } else if (args.language.equals("es")) {
      analyzer = new SpanishAnalyzer();
    } else {
      // Default to English
      analyzer = args.keepstop ?
          new EnglishStemmingAnalyzer(args.stemmer, CharArraySet.EMPTY_SET) : new EnglishStemmingAnalyzer(args.stemmer);
    }

    if (args.sdm) {
      LOG.info("Use Sequential Dependence Model query");
      qc = QueryConstructor.SequentialDependenceModel;
    } else {
      LOG.info("Use Bag of Terms query");
      qc = QueryConstructor.BagOfTerms;
    }
  
    isRerank = args.rm3 || args.axiom || args.bm25prf;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }
  
  public List<TaggedSimilarity> constructSimilarities() {
    // Figure out which scoring model to use.
    List<TaggedSimilarity> similarities = new ArrayList<>();
    if (args.ql || args.qld) {
      for (String mu : args.mu) {
        similarities.add(new TaggedSimilarity(new LMDirichletSimilarity(Float.valueOf(mu)), "mu="+mu));
      }
    } else if (args.qljm) {
      for (String lambda : args.qljm_lambda) {
        similarities.add(new TaggedSimilarity(new LMJelinekMercerSimilarity(Float.valueOf(lambda)), "lambda=" + lambda));
      }
    } else if (args.bm25) {
      for (String k1 : args.k1) {
        for (String b : args.b) {
          similarities.add(new TaggedSimilarity(new BM25Similarity(Float.valueOf(k1), Float.valueOf(b)), "k1="+k1+",b="+b));
        }
      }
    } else if (args.bm25Accurate) {
      for (String k1 : args.k1) {
        for (String b : args.b) {
          similarities.add(new TaggedSimilarity(new AccurateBM25Similarity(Float.valueOf(k1), Float.valueOf(b)), "k1="+k1+",b="+b));
        }
      }
    } else if (args.inl2) {
      for (String c : args.inl2_c) {
        similarities.add(new TaggedSimilarity(new DFRSimilarity(new BasicModelIn(), new AfterEffectL(), new NormalizationH2(Float.valueOf(c))), "c:"+c));
      };
    } else if (args.spl) {
      for (String c : args.spl_c) {
        similarities.add(new TaggedSimilarity(new IBSimilarity(new DistributionSPL(), new LambdaDF(),  new NormalizationH2(Float.valueOf(c))), "c="+c));
      }
    } else if (args.f2exp) {
      for (String s : args.f2exp_s) {
        similarities.add(new TaggedSimilarity(new AxiomaticF2EXP(Float.valueOf(s)), "s:"+s));
      }
    } else if (args.f2log) {
      for (String s : args.f2log_s) {
        similarities.add(new TaggedSimilarity(new AxiomaticF2LOG(Float.valueOf(s)), "s:"+s));
      }
    } else {
      throw new IllegalArgumentException("Error: Must specify scoring model!");
    }
    return similarities;
  }
  
  public Map<String, RerankerCascade> constructRerankerCascades() throws IOException {
    Map<String, RerankerCascade> cascades = new HashMap<>();
    // Set up the ranking cascade.
    if (args.rm3) {
      LOG.info("Rerank with RM3");
      for (String fbTerms : args.rm3_fbTerms) {
        for (String fbDocs : args.rm3_fbDocs) {
          for (String originalQueryWeight : args.rm3_originalQueryWeight) {
            RerankerCascade cascade = new RerankerCascade();
            cascade.add(new Rm3Reranker(analyzer, FIELD_BODY, Integer.valueOf(fbTerms),
                Integer.valueOf(fbDocs), Float.valueOf(originalQueryWeight), args.rm3_outputQuery));
            cascade.add(new ScoreTiesAdjusterReranker());
            String tag = "rm3.fbTerms:"+fbTerms+",rm3.fbDocs:"+fbDocs+",rm3.originalQueryWeight:"+originalQueryWeight;
            cascades.put(tag, cascade);
          }
        }
      }
    } else if (args.axiom) {
      for (String r : args.axiom_r) {
        for (String n : args.axiom_n) {
          for (String beta : args.axiom_beta) {
            for (String top : args.axiom_top) {
              for (String seed : args.axiom_seed) {
                RerankerCascade cascade = new RerankerCascade();
                cascade.add(new AxiomReranker(args.index, args.axiom_index, FIELD_BODY,
                    args.axiom_deterministic, Integer.valueOf(seed), Integer.valueOf(r),
                    Integer.valueOf(n), Float.valueOf(beta), Integer.valueOf(top),
                    args.axiom_docids, args.axiom_outputQuery, args.searchtweets));
                cascade.add(new ScoreTiesAdjusterReranker());
                String tag = "axiom.seed:"+seed+",axiom.r:"+r+",axiom.n:"+n+",axiom.beta:"+beta+",axiom.top:"+top;
                cascades.put(tag, cascade);
              }
            }
          }
        }
      }
    }else if (args.bm25prf) {
        for (String fbTerms : args.bm25prf_fbTerms) {
            for (String fbDocs : args.bm25prf_fbDocs) {
                for (String k1 : args.bm25prf_k1) {
                    for (String b : args.bm25prf_b) {
                        for (String newTermWeight : args.bm25prf_newTermWeight) {
                            RerankerCascade cascade = new RerankerCascade();
                            cascade.add(new BM25PrfReranker(analyzer, FIELD_BODY, Integer.valueOf(fbTerms),
                                    Integer.valueOf(fbDocs), Float.valueOf(k1), Float.valueOf(b), Float.valueOf(newTermWeight),
                                    args.bm25prf_outputQuery));
                            cascade.add(new ScoreTiesAdjusterReranker());
                            String tag = "bm25prf.fbTerms:" + fbTerms + ",fbDocs:" + fbDocs + ",bm25prf.k1:" + k1 + ",bm25prf.b:" + b + ",bm25prf.newTermWeight:" + newTermWeight;
                            cascades.put(tag, cascade);
                        }
                    }
                }
            }
        }
    }

    else {
      RerankerCascade cascade = new RerankerCascade();
      cascade.add(new ScoreTiesAdjusterReranker());
      cascades.put("", cascade);
    }
    
    return cascades;
  }

  @SuppressWarnings("unchecked")
  public<K> void runTopics() throws IOException {
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
    final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(args.threads);
    this.similarities = constructSimilarities();
    Map<String, RerankerCascade> cascades = constructRerankerCascades();
    for (TaggedSimilarity taggedSimilarity : this.similarities) {
      for (Map.Entry<String, RerankerCascade> cascade : cascades.entrySet()) {
        final String outputPath = (this.similarities.size()+cascades.size())>2 ?
            args.output+"_"+ taggedSimilarity.tag+(cascade.getKey().isEmpty()?"":",")+cascade.getKey() : args.output;
        if (args.skipexists && new File(outputPath).exists()) {
          LOG.info("Skipping True: "+outputPath);
          continue;
        }
        executor.execute(new SearcherThread<K>(reader, topics, taggedSimilarity, cascade.getKey(), cascade.getValue(),
            outputPath, runTag));
      }
    }
    executor.shutdown();
    try {
      // Wait for existing tasks to terminate
      while (!executor.awaitTermination(1, TimeUnit.MINUTES)) {}
    } catch (InterruptedException ie) {
      // (Re-)Cancel if current thread also interrupted
      executor.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }
  
  public<K> ScoredDocuments search(IndexSearcher searcher, K qid, String queryString, RerankerCascade cascade)
      throws IOException {
    Query query = null;
    if (qc == QueryConstructor.SequentialDependenceModel) {
      query = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(FIELD_BODY, analyzer, queryString);
    } else {
      query = new BagOfWordsQueryGenerator().buildQuery(FIELD_BODY, analyzer, queryString);
    }

    TopDocs rs = new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[]{});
    if (!(isRerank && args.rerankcutoff <= 0)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true);
      }
    }

    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryString);
    RerankerContext context = new RerankerContext<>(searcher, qid, query, null, queryString, queryTokens, null, args);

    return cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
  }
  
  public<K> ScoredDocuments searchBackgroundLinking(IndexSearcher searcher, K qid, String queryString, RerankerCascade cascade)
      throws IOException, QueryNodeException {
    Query query = null;
    String queryDocID = null;
    if (qc == QueryConstructor.SequentialDependenceModel) {
      args.backgroundlinking_weighted = false;
    }
    queryDocID = queryString;
    List<String> queryList = NewsBackgroundLinkingTopicReader.generateQueryString(reader, queryDocID,
        args.backgroundlinking_paragraph, args.backgroundlinking_k, args.backgroundlinking_weighted, qc, analyzer);
    List<ScoredDocuments> allRes = new ArrayList<>();
    for (String queryStr : queryList) {
      Query q = null;
      if (qc == QueryConstructor.SequentialDependenceModel) {
        q = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(FIELD_BODY, analyzer, queryStr);
      } else {
        // DO NOT use BagOfWordsQueryGenerator here!!!!
        // Because the actual query strings are extracted from tokenized document!!!
        q = new StandardQueryParser().parse(queryStr, FIELD_BODY);
      }
      Query filter = new TermInSetQuery(WapoGenerator.WapoField.KICKER.name, new BytesRef("Opinions"), new BytesRef("Letters to the Editor"), new BytesRef("The Post's View")
//          new Term(WapoGenerator.WapoField.KICKER.name, "Opinions"),
//          new Term(WapoGenerator.WapoField.KICKER.name, "Letters to the Editor"),
//          new Term(WapoGenerator.WapoField.KICKER.name, "The Post's View")
      );
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      builder.add(filter, BooleanClause.Occur.MUST_NOT);
      builder.add(q, BooleanClause.Occur.MUST);
      query = builder.build();
      
      TopDocs rs = new TopDocs(new TotalHits(0, TotalHits.Relation.EQUAL_TO), new ScoreDoc[]{});
      if (!(isRerank && args.rerankcutoff <= 0)) {
        if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
          rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits);
        } else {
          rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true);
        }
      }
      
      List<String> queryTokens = Arrays.asList(queryStr.split(" "));
      RerankerContext context = new RerankerContext<>(searcher, qid, query, queryDocID, queryStr, queryTokens, null, args);
  
      allRes.add(cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context));
    }
    
    // Finally do a round-robin picking
    int totalSize = 0;
    float[] scoresOfFirst = new float[allRes.size()];
    for (int i = 0; i < allRes.size(); i++) {
      totalSize += allRes.get(i).documents.length;
      scoresOfFirst[i] = allRes.get(i).scores.length > 0 ? allRes.get(i).scores[0] : Float.NEGATIVE_INFINITY;
    }
    totalSize = Math.min(args.hits, totalSize);
  
    ScoredDocuments scoredDocs = new ScoredDocuments();
    scoredDocs.documents = new Document[totalSize];
    scoredDocs.ids = new int[totalSize];
    scoredDocs.scores = new float[totalSize];
  
    int rowIdx = 0;
    int idx = 0;
    while(idx < totalSize) {
      for(int i = 0; i < allRes.size(); i++) {
        if (rowIdx < allRes.get(i).documents.length) {
          scoredDocs.documents[idx] = allRes.get(i).documents[rowIdx];
          scoredDocs.ids[idx] = allRes.get(i).ids[rowIdx];
          scoredDocs.scores[idx] = args.hits-idx;
          idx++;
        }
      }
      rowIdx++;
    }
  
    NewsBackgroundLinkingReranker postProcessor = new NewsBackgroundLinkingReranker();
    RerankerContext context = new RerankerContext<>(searcher, qid, null, queryDocID, null, null, null, args);
    scoredDocs = postProcessor.rerank(scoredDocs, context);
    return scoredDocs;
  }

  public<K> ScoredDocuments searchTweets(IndexSearcher searcher, K qid, String queryString, long t, RerankerCascade cascade) throws IOException {
    Query keywordQuery;
    if (qc == QueryConstructor.SequentialDependenceModel) {
      keywordQuery = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(FIELD_BODY, analyzer, queryString);
    } else {
      keywordQuery = new BagOfWordsQueryGenerator().buildQuery(FIELD_BODY, analyzer, queryString);
    }
    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryString);

    // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
    // <querytweettime> tag contains the timestamp of the query in terms of the
    // chronologically nearest tweet id within the corpus
    Query filter = LongPoint.newRangeQuery(TweetGenerator.StatusField.ID_LONG.name, 0L, t);
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filter, BooleanClause.Occur.FILTER);
    builder.add(keywordQuery, BooleanClause.Occur.MUST);
    Query compositeQuery = builder.build();


    TopDocs rs = new TopDocs(new TotalHits(0,TotalHits.Relation.EQUAL_TO), new ScoreDoc[]{});
    if (!(isRerank && args.rerankcutoff <= 0)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(compositeQuery, isRerank ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(compositeQuery, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_TWEETID, true);
      }
    }

    RerankerContext context = new RerankerContext<>(searcher, qid, keywordQuery, null, queryString, queryTokens, filter, args);

    return cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
  }

  public static void main(String[] args) throws Exception {
    SearchArgs searchArgs = new SearchArgs();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchCollection" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SearchCollection searcher = new SearchCollection(searchArgs);
    searcher.runTopics();
    searcher.close();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
