package io.anserini.search;

/**
 * Twitter Tools
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

import io.anserini.index.IndexTweets;
import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.*;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.ltr.feature.base.MatchingTermCount;
import io.anserini.ltr.feature.base.QueryLength;
import io.anserini.ltr.feature.base.SumMatchingTf;
import io.anserini.ltr.feature.twitter.*;
import io.anserini.rerank.RankLibReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.rerank.twitter.RemoveRetweetsTemporalTiebreakReranker;
import io.anserini.util.AnalyzerUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class SearchTweets {
  private static final Logger LOG = LogManager.getLogger(SearchTweets.class);

  private SearchTweets() {}

  public static void main(String[] args) throws Exception {
    long curTime = System.nanoTime();
    SearchArgs searchArgs = new SearchArgs();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchTweets" + parser.printExample(OptionHandlerFilter.REQUIRED));
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

    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    if (searchArgs.ql) {
      LOG.info("Using QL scoring model");
      searcher.setSimilarity(new LMDirichletSimilarity(searchArgs.mu));
    } else if (searchArgs.bm25) {
      LOG.info("Using BM25 scoring model");
      searcher.setSimilarity(new BM25Similarity(searchArgs.k1, searchArgs.b));
    } else {
      LOG.error("Error: Must specify scoring model!");
      System.exit(-1);
    }

    RerankerCascade cascade = new RerankerCascade();
    if (searchArgs.rm3) {
      cascade.add(new Rm3Reranker(IndexTweets.ANALYZER, StatusField.TEXT.name, "src/main/resources/io/anserini/rerank/rm3/rm3-stoplist.twitter.txt"));
      cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
    } else {
      cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
    }

    if (!searchArgs.model.isEmpty() && searchArgs.extractors != null) {
      LOG.debug(String.format("Ranklib model used, modeled loaded from %s", searchArgs.model));
      cascade.add(new RankLibReranker(searchArgs.model, StatusField.TEXT.name, searchArgs.extractors));
    }

    FeatureExtractors extractorChain = null;
    if (searchArgs.extractors != null) {
      extractorChain = FeatureExtractors.loadExtractor(searchArgs.extractors);
    }

    if (searchArgs.dumpFeatures) {
      PrintStream out = new PrintStream(searchArgs.featureFile);
      Qrels qrels = new Qrels(searchArgs.qrels);
      cascade.add(new TweetsLtrDataGenerator(out, qrels, extractorChain));
    }

    MicroblogTopicSet topics = MicroblogTopicSet.fromFile(new File(searchArgs.topics));

    PrintStream out = new PrintStream(new FileOutputStream(new File(searchArgs.output)));
    LOG.info("Writing output to " + searchArgs.output);

    LOG.info("Initialized complete! (elapsed time = " + (System.nanoTime()-curTime)/1000000 + "ms)");
    long totalTime = 0;
    int cnt = 0;
    for ( MicroblogTopic topic : topics ) {
      long curQueryTime = System.nanoTime();

      Filter filter = NumericRangeFilter.newLongRange(StatusField.ID.name, 0L, topic.getQueryTweetTime(), true, true);
      Query query = AnalyzerUtils.buildBagOfWordsQuery(StatusField.TEXT.name, IndexTweets.ANALYZER, topic.getQuery());

      TopDocs rs = searcher.search(query, filter, searchArgs.hits);
      List<String> queryTokens = AnalyzerUtils.tokenize(IndexTweets.ANALYZER, topic.getQuery());

      RerankerContext context = new RerankerContext(searcher, query, topic.getId(), topic.getQuery(),
         queryTokens, StatusField.TEXT.name, filter);
      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

      for (int i=0; i<docs.documents.length; i++) {
        String qid = topic.getId().replaceFirst("^MB0*", "");
        out.println(String.format("%s Q0 %s %d %f %s", qid,
            docs.documents[i].getField(StatusField.ID.name).numericValue(), (i+1), docs.scores[i], searchArgs.runtag));
      }
      long qtime = (System.nanoTime()-curQueryTime)/1000000;
      LOG.info("Query " + topic.getId() + " (elapsed time = " + qtime + "ms)");
      totalTime += qtime;
      cnt++;
    }

    LOG.info("All queries completed!");
    LOG.info("Total elapsed time = " + totalTime + "ms");
    LOG.info("Average query latency = " + (totalTime/cnt) + "ms");

    reader.close();
    out.close();
  }
}
