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

import io.anserini.ltr.TweetsLtrDataGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RankLibReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.rerank.twitter.RemoveRetweetsTemporalTiebreakReranker;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.MMapDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.List;

import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_BODY;
import static io.anserini.index.generator.LuceneDocumentGenerator.FIELD_ID;

@SuppressWarnings("deprecation")
public class SearchTweets {
  private static final Logger LOG = LogManager.getLogger(SearchTweets.class);

  private SearchTweets() {}

  public static void main(String[] args) throws Exception {
    long initializationTime = System.currentTimeMillis();
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
    EnglishAnalyzer englishAnalyzer = new EnglishAnalyzer();
    if (searchArgs.rm3) {
      cascade.add(new Rm3Reranker(englishAnalyzer, FIELD_BODY,
              "src/main/resources/io/anserini/rerank/rm3/rm3-stoplist.twitter.txt"));
      cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
    } else {
      cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
    }

    if (!searchArgs.model.isEmpty() && searchArgs.extractors != null) {
      LOG.debug(String.format("Ranklib model used, modeled loaded from %s", searchArgs.model));
      cascade.add(new RankLibReranker(searchArgs.model, FIELD_BODY, searchArgs.extractors));
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

    LOG.info("Initialized complete! (elapsed time = " + (System.currentTimeMillis()- initializationTime) + "ms)");
    long totalTime = 0;
    int cnt = 0;
    for ( MicroblogTopic topic : topics ) {
      long curQueryTime = System.currentTimeMillis();

      // do not cosider the tweets with tweet ids that are beyond the queryTweetTime
      // <querytweettime> tag contains the timestamp of the query in terms of the
      // chronologically nearest tweet id within the corpus
      Query filter = TermRangeQuery.newStringRange(FIELD_ID, "0", String.valueOf(topic.getQueryTweetTime()), true, true);
      Query query = AnalyzerUtils.buildBagOfWordsQuery(FIELD_BODY, englishAnalyzer, topic.getQuery());
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      builder.add(filter, BooleanClause.Occur.FILTER);
      builder.add(query, BooleanClause.Occur.MUST);
      Query q = builder.build();

      TopDocs rs = searcher.search(q, searchArgs.hits);
      List<String> queryTokens = AnalyzerUtils.tokenize(englishAnalyzer, topic.getQuery());

      RerankerContext context = new RerankerContext(searcher, query, topic.getId(), topic.getQuery(),
         queryTokens, FIELD_BODY, filter);
      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
      long queryTime = (System.currentTimeMillis() - curQueryTime);

      for (int i=0; i<docs.documents.length; i++) {
        String qid = topic.getId().replaceFirst("^MB0*", "");
        out.println(String.format("%s Q0 %s %d %f %s", qid,
            docs.documents[i].getField(FIELD_ID).stringValue(), (i+1), docs.scores[i], searchArgs.runtag));
      }

      LOG.info("Query " + topic.getId() + " (elapsed time = " + queryTime + "ms)");
      totalTime += queryTime;
      cnt++;
    }

    LOG.info("All queries completed!");
    LOG.info("Total elapsed time = " + totalTime + "ms");
    LOG.info("Average query latency = " + (totalTime/cnt) + "ms");

    reader.close();
    out.close();
  }
}
