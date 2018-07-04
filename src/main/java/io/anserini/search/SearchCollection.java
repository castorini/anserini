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

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;

import io.anserini.rerank.lib.AxiomReranker;
import io.anserini.rerank.lib.Rm3Reranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.TopicReader;
import io.anserini.util.AnalyzerUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
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

  public SearchCollection(SearchArgs args) throws IOException {
    this.args = args;
    Path indexPath = Paths.get(args.index);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(args.index + " does not exist or is not a directory.");
    }

    LOG.info("Reading index at " + args.index);
    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));

    // Figure out which scoring model to use.
    if (args.ql) {
      LOG.info("Using QL scoring model");
      this.similarity = new LMDirichletSimilarity(args.mu);
    } else if (args.bm25) {
      LOG.info("Using BM25 scoring model");
      this.similarity = new BM25Similarity(args.k1, args.b);
    } else {
      throw new IllegalArgumentException("Error: Must specify scoring model!");
    }

    // Are we searching tweets?
    if (args.searchtweets) {
      analyzer = new TweetAnalyzer();
    } else {
      analyzer = args.keepstop ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    }

    isRerank = args.rm3 || args.axiom;

    // Set up the ranking cascade.
    cascade = new RerankerCascade();
    if (args.rm3) {
      cascade.add(new Rm3Reranker(analyzer, FIELD_BODY, args));
    } else if (args.axiom) {
      cascade.add(new AxiomReranker(FIELD_BODY, args));
    }

    cascade.add(new ScoreTiesAdjusterReranker());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public<K> int runTopics() throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    Path topicsFile = Paths.get(args.topics);

    if (!Files.exists(topicsFile) || !Files.isRegularFile(topicsFile) || !Files.isReadable(topicsFile)) {
      throw new IllegalArgumentException("Topics file : " + topicsFile + " does not exist or is not a (readable) file.");
    }

    TopicReader<K> tr;
    SortedMap<K, Map<String, String>> topics;
    try {
      tr = (TopicReader<K>) Class.forName("io.anserini.search.query." + args.topicReader + "TopicReader")
          .getConstructor(Path.class).newInstance(topicsFile);
      topics = tr.read();
    } catch (Exception e) {
      throw new IllegalArgumentException("Unable to load topic reader: " + args.topicReader);
    }

    final String runTag = "Anserini_" + args.topicfield + "_" + (args.keepstop ? "KeepStopwords_" : "")
        + FIELD_BODY + "_" + (args.searchtweets ? "SearchTweets_" : "") + similarity.toString();

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(args.output), StandardCharsets.US_ASCII));

    for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
      K qid = entry.getKey();
      String queryString = entry.getValue().get(args.topicfield);

      ScoredDocuments docs;
      if (args.searchtweets) {
        docs = searchTweets(searcher, qid, queryString, Long.parseLong(entry.getValue().get("time")));
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
            docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i],
            ((i == 0 || i == docs.documents.length-1) ? runTag : "See_Line1")));
      }
    }
    out.flush();
    out.close();

    return topics.size();
  }

  public<K> ScoredDocuments search(IndexSearcher searcher, K qid, String queryString) throws IOException {
    Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, analyzer, queryString);

    TopDocs rs = new TopDocs(0, new ScoreDoc[]{}, Float.NaN);
    if (!(isRerank && args.rerankcutoff <= 0)) {
      if (args.arbitraryScoreTieBreak) {// Figure out how to break the scoring ties.
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits);
      } else {
        rs = searcher.search(query, isRerank ? args.rerankcutoff : args.hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
      }
    }

    List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryString);
    RerankerContext context = new RerankerContext<>(searcher, qid, query, queryString, queryTokens, null, args);

    return cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
  }

  public<K> ScoredDocuments searchTweets(IndexSearcher searcher, K qid, String queryString, long t) throws IOException {
    Query keywordQuery = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, analyzer, queryString);
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

    RerankerContext context = new RerankerContext<>(searcher, qid, keywordQuery, queryString, queryTokens, filter, args);

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
