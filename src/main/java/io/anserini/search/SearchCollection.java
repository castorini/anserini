package io.anserini.search;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.ltr.TweetsLtrDataGenerator;
import io.anserini.ltr.WebCollectionLtrDataGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.AxiomReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.rerank.twitter.RemoveRetweetsTemporalTiebreakReranker;
import io.anserini.search.query.TopicReader;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
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
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintStream;
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

/**
 * Searcher for Gov2, ClueWeb09, and ClueWeb12 corpra.
 * TREC Web Tracks from 2009 to 2014
 * TREC Terabyte Tracks from 2004 to 2006
 */
public final class SearchCollection implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_DOCID =
      new Sort(SortField.FIELD_SCORE, new SortField(FIELD_ID, SortField.Type.STRING_VAL));

  private static final Logger LOG = LogManager.getLogger(SearchCollection.class);
  private final IndexReader reader;

  public SearchCollection(String indexDir) throws IOException {
    Path indexPath = Paths.get(indexDir);

    if (!Files.exists(indexPath) || !Files.isDirectory(indexPath) || !Files.isReadable(indexPath)) {
      throw new IllegalArgumentException(indexDir + " does not exist or is not a directory.");
    }

    this.reader = DirectoryReader.open(FSDirectory.open(indexPath));
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  /**
   * Prints TREC submission file to the standard output stream.
   *
   * @param topics     queries
   * @param similarity similarity
   * @throws IOException
   */
  public<K> void search(SortedMap<K, Map<String, String>> topics, Similarity similarity, RerankerCascade cascade,
      SearchArgs searchArgs) throws IOException {
    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);

    final String runTag = "Anserini_" + searchArgs.topicfield + "_" + (searchArgs.keepstop ? "KeepStopwords_" : "")
        + FIELD_BODY + "_" + (searchArgs.searchtweets ? "SearchTweets_" : "") + similarity.toString();

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(searchArgs.output), StandardCharsets.US_ASCII));

    Analyzer analyzer;
    if (searchArgs.searchtweets) {
      analyzer = new TweetAnalyzer();
    } else {
      analyzer = searchArgs.keepstop ? new EnglishAnalyzer(CharArraySet.EMPTY_SET) : new EnglishAnalyzer();
    }

    Query filter = null;
    for (Map.Entry<K, Map<String, String>> entry : topics.entrySet()) {
      K qID = entry.getKey();
      String queryString = entry.getValue().get(searchArgs.topicfield);
      Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, analyzer, queryString);

      if (searchArgs.searchtweets) {
        long queryTweetTime = Long.parseLong(entry.getValue().get("time"));
        // do not cosider the tweets with tweet ids that are beyond the queryTweetTime
        // <querytweettime> tag contains the timestamp of the query in terms of the
        // chronologically nearest tweet id within the corpus
        filter = LongPoint.newRangeQuery(TweetGenerator.StatusField.ID_LONG.name, 0L, queryTweetTime);
        BooleanQuery.Builder builder = new BooleanQuery.Builder();
        builder.add(filter, BooleanClause.Occur.FILTER);
        builder.add(query, BooleanClause.Occur.MUST);
        query = builder.build();
      }

      // Figure out how to break the scoring ties.
      TopDocs rs;
      if (searchArgs.arbitraryScoreTieBreak) {
        rs = searcher.search(query, searchArgs.hits);
      } else if (searchArgs.searchtweets) {
        // TODO: we need to build the proper tie-breaking code path for tweets.
        rs = searcher.search(query, searchArgs.hits);
      } else {
        rs = searcher.search(query, searchArgs.hits, BREAK_SCORE_TIES_BY_DOCID, true, true);
      }

      List<String> queryTokens = AnalyzerUtils.tokenize(analyzer, queryString);
      if (searchArgs.searchtweets) { // This is ugly, but we have to reform the tweet query here for reranking
        query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, analyzer, queryString);
      }

      RerankerContext context = new RerankerContext(searcher, query, String.valueOf(qID), queryString,
              queryTokens, FIELD_BODY, filter, searchArgs);
      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

      /**
       * the first column is the topic number.
       * the second column is currently unused and should always be "Q0".
       * the third column is the official document identifier of the retrieved document.
       * the fourth column is the rank the document is retrieved.
       * the fifth column shows the score (integer or floating point) that generated the ranking.
       * the sixth column is called the "run tag" and should be a unique identifier for your
       */
      for (int i = 0; i < docs.documents.length; i++) {
        out.println(String.format(Locale.US, "%s Q0 %s %d %f %s", qID,
            docs.documents[i].getField(FIELD_ID).stringValue(), (i + 1), docs.scores[i],
            ((i == 0 || i == docs.documents.length-1) ? runTag : "See_Line1")));
      }
    }
    out.flush();
    out.close();
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

    LOG.info("Reading index at " + searchArgs.index);
    Directory dir;
    if (searchArgs.inmem) {
      LOG.info("Using MMapDirectory with preload");
      dir = new MMapDirectory(Paths.get(searchArgs.index));
      ((MMapDirectory) dir).setPreload(true);
    } else {
      LOG.info("Using default FSDirectory");
      dir = FSDirectory.open(Paths.get(searchArgs.index));
    }

    Similarity similarity = null;

    if (searchArgs.ql) {
      LOG.info("Using QL scoring model");
      similarity = new LMDirichletSimilarity(searchArgs.mu);
    } else if (searchArgs.bm25) {
      LOG.info("Using BM25 scoring model");
      similarity = new BM25Similarity(searchArgs.k1, searchArgs.b);
    } else {
      LOG.error("Error: Must specify scoring model!");
      System.exit(-1);
    }

    Analyzer analyzer;
    if (searchArgs.searchtweets) {
      analyzer = new TweetAnalyzer();
    } else {
      analyzer = new EnglishAnalyzer();
    }

    RerankerCascade cascade = new RerankerCascade();
    if (searchArgs.rm3) {
      if (searchArgs.searchtweets) {
        cascade.add(new Rm3Reranker(analyzer, FIELD_BODY,
            "io/anserini/rerank/rm3/rm3-stoplist.twitter.txt", true));
        cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
      } else {
        cascade.add(new Rm3Reranker(analyzer, FIELD_BODY,
            "io/anserini/rerank/rm3/rm3-stoplist.gov2.txt", true));
        cascade.add(new ScoreTiesAdjusterReranker());
      }
    } else if (searchArgs.axiom) {
      if (searchArgs.searchtweets) {
        cascade.add(new AxiomReranker(FIELD_BODY, searchArgs.axiom_beta, searchArgs.axiom_external_index));
        cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
      } else {
        cascade.add(new AxiomReranker(FIELD_BODY, searchArgs.axiom_beta, searchArgs.axiom_external_index));
        cascade.add(new ScoreTiesAdjusterReranker());
      }
    } else {
      cascade.add(new ScoreTiesAdjusterReranker());

      if (searchArgs.searchtweets) {
        cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
      }
    }
    FeatureExtractors extractors = null;
    if (searchArgs.extractors != null) {
      extractors = FeatureExtractors.loadExtractor(searchArgs.extractors);
    }

    if (searchArgs.dumpFeatures) {
      PrintStream out = new PrintStream(searchArgs.featureFile);
      Qrels qrels = new Qrels(searchArgs.qrels);
      if (searchArgs.searchtweets) {
        cascade.add(new TweetsLtrDataGenerator(out, qrels, extractors));
      } else {
        cascade.add(new WebCollectionLtrDataGenerator(out,  qrels, extractors));
      }
    }

    Path topicsFile = Paths.get(searchArgs.topics);

    if (!Files.exists(topicsFile) || !Files.isRegularFile(topicsFile) || !Files.isReadable(topicsFile)) {
      throw new IllegalArgumentException("Topics file : " + topicsFile + " does not exist or is not a (readable) file.");
    }

    TopicReader tr = (TopicReader)Class.forName("io.anserini.search.query."+searchArgs.topicReader+"TopicReader")
            .getConstructor(Path.class).newInstance(topicsFile);
    SortedMap<String, Map<String, String>> topics = tr.read();

    final long start = System.nanoTime();
    SearchCollection searcher = new SearchCollection(searchArgs.index);
    searcher.search(topics, similarity, cascade, searchArgs);
    searcher.close();
    final long durationMillis =
        TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total " + topics.size() + " topics searched in "
        + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
