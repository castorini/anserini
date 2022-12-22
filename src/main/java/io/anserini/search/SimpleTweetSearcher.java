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
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.Constants;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.topicreader.TopicReader;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;

/**
 * Class that exposes basic search functionality, designed specifically to provide the bridge between Java and Python
 * via pyjnius.
 */
public class SimpleTweetSearcher extends SimpleSearcher implements Closeable {
  public static final Sort BREAK_SCORE_TIES_BY_TWEETID =
      new Sort(SortField.FIELD_SCORE,
          new SortField(TweetGenerator.TweetField.ID_LONG.name, SortField.Type.LONG, true));
  private static final Logger LOG = LogManager.getLogger(SimpleTweetSearcher.class);

  public static final class Args {
    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Path to Lucene index.")
    public String index;

    @Option(name = "-topics", metaVar = "[file]", required = true, usage = "Topics file.")
    public String topics;

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "Output run file.")
    public String output;

    @Option(name = "-rm3", usage = "Flag to use RM3.")
    public Boolean useRM3 = false;

    @Option(name = "-hits", metaVar = "[number]", usage = "max number of hits to return")
    public int hits = 1000;
  }

  protected SimpleTweetSearcher() {
  }

  public SimpleTweetSearcher(String indexDir) throws IOException {
    super(indexDir, new TweetAnalyzer());
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  public Result[] searchTweets(String q, int k, long t) throws IOException {
    Query query = new BagOfWordsQueryGenerator().buildQuery(Constants.CONTENTS, analyzer, q);
    List<String> queryTokens = AnalyzerUtils.analyze(analyzer, q);

    return searchTweets(query, queryTokens, q, k, t);
  }

  protected Result[] searchTweets(Query query, List<String> queryTokens, String queryString, int k, long t)
      throws IOException {
    // Create an IndexSearch only once. Note that the object is thread safe.
    if (searcher == null) {
      searcher = new IndexSearcher(reader);
      searcher.setSimilarity(similarity);
    }

    SearchCollection.Args searchArgs = new SearchCollection.Args();
    searchArgs.arbitraryScoreTieBreak = false;
    searchArgs.hits = k;
    searchArgs.searchtweets = true;

    TopDocs rs;
    RerankerContext context;

    // Do not consider the tweets with tweet ids that are beyond the queryTweetTime
    // <querytweettime> tag contains the timestamp of the query in terms of the
    // chronologically nearest tweet id within the corpus
    Query filter = LongPoint.newRangeQuery(TweetGenerator.TweetField.ID_LONG.name, 0L, t);
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    builder.add(filter, BooleanClause.Occur.FILTER);
    builder.add(query, BooleanClause.Occur.MUST);
    Query compositeQuery = builder.build();
    rs = searcher.search(compositeQuery, useRM3 ? searchArgs.rerankcutoff :
        k, BREAK_SCORE_TIES_BY_TWEETID, true);
    context = new RerankerContext<>(searcher, null, compositeQuery, null,
        queryString, queryTokens, filter, searchArgs);

    ScoredDocuments hits = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

    Result[] results = new Result[hits.ids.length];
    for (int i = 0; i < hits.ids.length; i++) {
      Document doc = hits.documents[i];
      String docid = doc.getField(Constants.ID).stringValue();

      IndexableField field;
      field = doc.getField(Constants.CONTENTS);
      String contents = field == null ? null : field.stringValue();

      field = doc.getField(Constants.RAW);
      String raw = field == null ? null : field.stringValue();

      results[i] = new Result(docid, hits.ids[i], hits.scores[i], contents, raw, doc);
    }

    return results;
  }

  // Note that this class is primarily meant to be used by automated regression scripts, not humans!
  // tl;dr - Do not use this class for running experiments. Use SearchCollection instead!
  //
  // SimpleTweetSearcher is the main class that exposes search functionality for Pyserini (in Python).
  // As such, it has a different code path than SearchCollection, the preferred entry point for running experiments
  // from Java. The main method here exposes only barebone options, primarily designed to verify that results from
  // SimpleSearcher are *exactly* the same as SearchCollection (e.g., via automated regression scripts).
  public static void main(String[] args) throws Exception {
    Args searchArgs = new Args();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(100));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SimpleTweetSearcher" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    final long start = System.nanoTime();
    SimpleTweetSearcher searcher = new SimpleTweetSearcher(searchArgs.index);
    SortedMap<Object, Map<String, String>> topics = TopicReader.getTopicsByFile(searchArgs.topics);

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(searchArgs.output), StandardCharsets.US_ASCII));

    if (searchArgs.useRM3) {
      searcher.set_rm3();
    }

    for (Object id : topics.keySet()) {
      long t = Long.parseLong(topics.get(id).get("time"));
      Result[] results = searcher.searchTweets(topics.get(id).get("title"), 1000, t);

      for (int i=0; i<results.length; i++) {
        out.println(String.format(Locale.US, "%s Q0 %s %d %f Anserini",
            id, results[i].docid, (i+1), results[i].score));
      }
    }
    out.close();

    final long durationMillis = TimeUnit.MILLISECONDS.convert(System.nanoTime() - start, TimeUnit.NANOSECONDS);
    LOG.info("Total run time: " + DurationFormatUtils.formatDuration(durationMillis, "HH:mm:ss"));
  }
}
