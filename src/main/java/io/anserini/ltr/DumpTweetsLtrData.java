package io.anserini.ltr;

import io.anserini.index.IndexTweets;
import io.anserini.index.IndexTweets.StatusField;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.twitter.RemoveRetweetsTemporalTiebreakReranker;
import io.anserini.search.MicroblogTopic;
import io.anserini.search.MicroblogTopicSet;
import io.anserini.search.SearchArgs;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

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
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import com.google.common.collect.Sets;

@SuppressWarnings("deprecation")
public class DumpTweetsLtrData {
  private static final Logger LOG = LogManager.getLogger(DumpTweetsLtrData.class);

  private DumpTweetsLtrData() {}

  private static class LtrArgs extends SearchArgs {
    @Option(name = "-qrels", metaVar = "[file]", required = true, usage = "qrels file")
    public String qrels;

    @Option(name = "-extractors", metaVar = "[file]", required = true, usage = "FeatureExtractors Definition File")
    public String extractors = null;
  }

  public static void main(String[] argv) throws Exception {
    long curTime = System.nanoTime();
    LtrArgs args = new LtrArgs();
    CmdLineParser parser = new CmdLineParser(args, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(argv);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: DumpTweetsLtrData" + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    LOG.info("Reading index at " + args.index);
    Directory dir = FSDirectory.open(Paths.get(args.index));
    IndexReader reader = DirectoryReader.open(dir);
    IndexSearcher searcher = new IndexSearcher(reader);

    if (args.ql) {
      LOG.info("Using QL scoring model");
      searcher.setSimilarity(new LMDirichletSimilarity(args.mu));
    } else if (args.bm25) {
      LOG.info("Using BM25 scoring model");
      searcher.setSimilarity(new BM25Similarity(args.k1, args.b));
    } else {
      LOG.error("Error: Must specify scoring model!");
      System.exit(-1);
    }

    Qrels qrels = new Qrels(args.qrels);

    FeatureExtractors extractors = null;
    if (args.extractors != null) {
      extractors = FeatureExtractors.loadExtractor(args.extractors);
    }

    PrintStream out = new PrintStream(new FileOutputStream(new File(args.output)));
    RerankerCascade cascade = new RerankerCascade();
    cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
    cascade.add(new TweetsLtrDataGenerator(out, qrels, extractors));

    MicroblogTopicSet topics = MicroblogTopicSet.fromFile(new File(args.topics));

    LOG.info("Initialized complete! (elapsed time = " + (System.nanoTime()-curTime)/1000000 + "ms)");
    long totalTime = 0;
    int cnt = 0;
    for ( MicroblogTopic topic : topics ) {
      long curQueryTime = System.nanoTime();

      Filter filter = NumericRangeFilter.newLongRange(StatusField.ID.name, 0L, topic.getQueryTweetTime(), true, true);
      Query query = AnalyzerUtils.buildBagOfWordsQuery(StatusField.TEXT.name, IndexTweets.ANALYZER, topic.getQuery());

      TopDocs rs = searcher.search(query, filter, args.hits);
      List<String> queryTokens = AnalyzerUtils.tokenize(IndexTweets.ANALYZER, topic.getQuery());
      RerankerContext context = new RerankerContext(searcher, query, topic.getId(), topic.getQuery(),
          queryTokens, StatusField.TEXT.name, filter);

      cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
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
