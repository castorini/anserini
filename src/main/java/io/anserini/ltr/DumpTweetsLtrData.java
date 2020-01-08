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

package io.anserini.ltr;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.analysis.TweetAnalyzer;
import io.anserini.index.generator.TweetGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.search.SearchArgs;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.topicreader.MicroblogTopicReader;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

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

  public static<K> void main(String[] argv) throws Exception {
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

    searcher.setSimilarity(new BM25Similarity());
    Qrels qrels = new Qrels(args.qrels);

    FeatureExtractors extractors = null;
    if (args.extractors != null) {
      extractors = FeatureExtractors.loadExtractor(args.extractors);
    }

    PrintStream out = new PrintStream(new FileOutputStream(new File(args.output)));
    RerankerCascade cascade = new RerankerCascade();
    cascade.add(new TweetsLtrDataGenerator(out, qrels, extractors));
  
    SortedMap<Integer, Map<String, String>> topics = new TreeMap<>();
    for (String singleTopicFile : args.topics) {
      Path topicsFilePath = Paths.get(singleTopicFile);
      if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        throw new IllegalArgumentException("Topics file : " + topicsFilePath + " does not exist or is not a (readable) file.");
      }
      TopicReader<Integer> tr = new MicroblogTopicReader(topicsFilePath);
      topics.putAll(tr.read());
    }

    LOG.info("Initialized complete! (elapsed time = " + (System.nanoTime()-curTime)/1000000 + "ms)");
    long totalTime = 0;
    int cnt = 0;
    for (Map.Entry<Integer, Map<String, String>> entry : topics.entrySet()) {
      long curQueryTime = System.nanoTime();
      Integer qID = entry.getKey();
      String queryString = entry.getValue().get("title");
      Long queryTime = Long.parseLong(entry.getValue().get("time"));
      Query filter = LongPoint.newRangeQuery(TweetGenerator.FIELD_ID, 0L, queryTime);
      Query query = new BagOfWordsQueryGenerator().buildQuery(TweetGenerator.FIELD_ID,
          new TweetAnalyzer(), queryString);
      BooleanQuery.Builder builder = new BooleanQuery.Builder();
      builder.add(filter, BooleanClause.Occur.FILTER);
      builder.add(query, BooleanClause.Occur.MUST);
      Query q = builder.build();

      TopDocs rs = searcher.search(q, args.hits);
      List<String> queryTokens = AnalyzerUtils.tokenize(new TweetAnalyzer(), queryString);

      RerankerContext<Integer> context = new RerankerContext<>(searcher, Integer.parseInt(queryString), query, null,
          queryString, queryTokens, filter, null);

      cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);
      long qtime = (System.nanoTime()-curQueryTime)/1000000;
      LOG.info("Query " + qID + " (elapsed time = " + qtime + "ms)");
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
