/**
 * Anserini: A toolkit for reproducible information retrieval research built on Lucene
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

import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.index.generator.WapoGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;

import io.anserini.rerank.lib.AxiomReranker;
import io.anserini.rerank.lib.NewsBackgroundLinkingReranker;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.topicreader.NewsBackgroundLinkingTopicReader;
import io.anserini.search.similarity.F2ExpSimilarity;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.SdmQueryGenerator;
import io.anserini.search.similarity.F2LogSimilarity;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.util.AnalyzerUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.TermsQuery;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

public final class SearchCollection implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(FIELD_ID, SortField.Type.STRING_VAL));
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.StatusField.ID_LONG.name, SortField.Type.LONG, true));

  private static final Logger LOG = LogManager.getLogger(SearchCollection.class);

  private final SearchArgs args;
  private final IndexReader reader;
  private final Similarity similarity;
  private final Analyzer analyzer;
  private final boolean isRerank;
  private final RerankerCascade cascade;

  public enum QueryConstructor {
    BagOfTerms,
    SequentialDependenceModel
  }
  
  private final QueryConstructor qc;

  public SearchCollection(SearchArgs args) throws IOException {
    this.args = args;
    Path indexPath = Paths.get(args.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(args.index + " does not exist or is not a directory.");
    }

    LOG.info("Reading index at " + indexPath);
    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));

    // Figure out which scoring model to use.
    if (args.ql) {
      LOG.info("Using QL scoring model");
      this.similarity = new LMDirichletSimilarity(args.mu);
    } else if (args.bm25) {
      LOG.info("Using BM25 scoring model");
      this.similarity = new BM25Similarity(args.k1, args.b);
    } else if (args.pl2) {
      LOG.info("Using PL2 scoring model");
      this.similarity = new DFRSimilarity(new BasicModelP(), new AfterEffectL(), new NormalizationH2(args.pl2_c));
    } else if (args.spl) {
      LOG.info("Using SPL scoring model");
      this.similarity = new IBSimilarity(new DistributionSPL(), new LambdaDF(),  new NormalizationH2(args.spl_c));
    } else if (args.f2exp) {
      LOG.info("Using F2Exp scoring model");
      this.similarity = new F2ExpSimilarity(args.f2exp_s);
    } else if (args.f2log) {
      LOG.info("Using F2Log scoring model");
      this.similarity = new F2LogSimilarity(args.f2log_s);
    } else {
      throw new IllegalArgumentException("Error: Must specify scoring model!");
    }

    // Are we searching tweets?
    if (args.searchtweets) {
      LOG.info("Search Tweets");
      analyzer = new TweetAnalyzer();
    } else {
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

    isRerank = args.rm3 || args.axiom;

    // Set up the ranking cascade.
    cascade = new RerankerCascade();
    if (args.rm3) {
      LOG.info("Rerank with RM3");
      cascade.add(new Rm3Reranker(analyzer, FIELD_BODY, args));
    } else if (args.axiom) {
      LOG.info("Rerank with Axiomatic Reranking");
      cascade.add(new AxiomReranker(FIELD_BODY, args));
    }
    cascade.add(new ScoreTiesAdjusterReranker());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  @SuppressWarnings("unchecked")
  public<K> int runTopics() throws IOException, QueryNodeException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    Path topicsFile = Paths.get(args.topics);

    if (!Files.exists(topicsFile) || !Files.isRegularFile(topicsFile) || !Files.isReadable(topicsFile)) {
      throw new IllegalArgumentException("Topics file : " + topicsFile + " does not exist or is not a (readable) file.");
    }

    TopicReader<K> tr;
    SortedMap<K, Map<String, String>> topics;
    try {
      tr = (TopicReader<K>) Class.forName("io.anserini.search.topicreader." + args.topicReader + "TopicReader")
          .getConstructor(Path.class).newInstance(topicsFile);
      topics = tr.read();
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to load topic reader: " + args.topicReader);
    }

    final String runTag = args.runtag == null ? "Anserini" : args.runtag;

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(args.output), StandardCharsets.US_ASCII));

    for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
      K qid = entry.getKey();
      String queryString = entry.getValue().get(args.topicfield);
      ScoredDocuments docs;
      if (args.searchtweets) {
        docs = searchTweets(searcher, qid, queryString, Long.parseLong(entry.getValue().get("time")));
      } else if (args.searchnewsbackground) {
        docs = searchBackgroundLinking(searcher, qid, queryString);
      } else{
        docs = search(searcher, qid, queryString);
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

    return topics.size();
  }
  
  public<K> ScoredDocuments search(IndexSearcher searcher, K qid, String queryString)
      throws IOException, QueryNodeException {
    Query query = null;
    if (qc == QueryConstructor.SequentialDependenceModel) {
      query = new SdmQueryGenerator(args.sdm_tw, args.sdm_ow, args.sdm_uw).buildQuery(FIELD_BODY, analyzer, queryString);
    } else {
      query = new BagOfWordsQueryGenerator().buildQuery(FIELD_BODY, analyzer, queryString);
    }

    TopDocs rs = new TopDocs(0, new ScoreDoc[]{}, Float.NaN);
    if (!(isRerank && args.rerankcutoff <= 0)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
      }
    }

    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryString);
    RerankerContext context = new RerankerContext<>(searcher, qid, query, null, queryString, queryTokens, null, args);

    return cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
  }
  
  public<K> ScoredDocuments searchBackgroundLinking(IndexSearcher searcher, K qid, String queryString)
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
      Query filter = new TermsQuery(
          new Term(WapoGenerator.WapoField.KICKER.name, "Opinions"),
          new Term(WapoGenerator.WapoField.KICKER.name, "Letters to the Editor"),
          new Term(WapoGenerator.WapoField.KICKER.name, "The Post's View")
      );
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      builder.add(filter, BooleanClause.Occur.MUST_NOT);
      builder.add(q, BooleanClause.Occur.MUST);
      query = builder.build();
      
      TopDocs rs = new TopDocs(0, new ScoreDoc[]{}, Float.NaN);
      if (!(isRerank && args.rerankcutoff <= 0)) {
        if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
          rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits);
        } else {
          rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
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

  public<K> ScoredDocuments searchTweets(IndexSearcher searcher, K qid, String queryString, long t) throws IOException {
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


    TopDocs rs = new TopDocs(0, new ScoreDoc[]{}, Float.NaN);
    if (!(isRerank && args.rerankcutoff <= 0)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(compositeQuery, isRerank ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(compositeQuery, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_TWEETID, true, true);
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
    int numTopics = searcher.runTopics();
    searcher.close();
    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + numTopics + " topics searched in "
        + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
