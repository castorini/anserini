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
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.rerank.twitter.RemoveRetweetsTemporalTiebreakReranker;
import io.anserini.util.AnalyzerUtils;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.Filter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.NumericRangeFilter;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.search.similarities.LMDirichletSimilarity;
import org.apache.lucene.store.FSDirectory;

public class SearchTweets {
  private static final String DEFAULT_RUNTAG = "lucene4lm";

  private static final String INDEX_OPTION = "index";
  private static final String QUERIES_OPTION = "queries";
  private static final String NUM_RESULTS_OPTION = "num_results";
  private static final String SIMILARITY_OPTION = "similarity";
  private static final String RUNTAG_OPTION = "runtag";
  private static final String RM3_OPTION = "rm3";

  private SearchTweets() {}

  @SuppressWarnings("static-access")
  public static void main(String[] args) throws Exception {
    Options options = new Options();

    options.addOption(new Option(RM3_OPTION, "apply relevance feedback with rm3"));

    options.addOption(OptionBuilder.withArgName("path").hasArg()
        .withDescription("index location").create(INDEX_OPTION));
    options.addOption(OptionBuilder.withArgName("num").hasArg()
        .withDescription("number of results to return").create(NUM_RESULTS_OPTION));
    options.addOption(OptionBuilder.withArgName("file").hasArg()
        .withDescription("file containing topics in TREC format").create(QUERIES_OPTION));
    options.addOption(OptionBuilder.withArgName("similarity").hasArg()
        .withDescription("similarity to use (BM25, LM)").create(SIMILARITY_OPTION));
    options.addOption(OptionBuilder.withArgName("string").hasArg()
        .withDescription("runtag").create(RUNTAG_OPTION));

    CommandLine cmdline = null;
    CommandLineParser parser = new GnuParser();
    try {
      cmdline = parser.parse(options, args);
    } catch (ParseException exp) {
      System.err.println("Error parsing command line: " + exp.getMessage());
      System.exit(-1);
    }

    if (!cmdline.hasOption(QUERIES_OPTION) || !cmdline.hasOption(INDEX_OPTION)) {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp(SearchTweets.class.getName(), options);
      System.exit(-1);
    }

    File indexLocation = new File(cmdline.getOptionValue(INDEX_OPTION));
    if (!indexLocation.exists()) {
      System.err.println("Error: " + indexLocation + " does not exist!");
      System.exit(-1);
    }

    String runtag = cmdline.hasOption(RUNTAG_OPTION) ?
        cmdline.getOptionValue(RUNTAG_OPTION) : DEFAULT_RUNTAG;

    String topicsFile = cmdline.getOptionValue(QUERIES_OPTION);
    
    int numResults = 1000;
    try {
      if (cmdline.hasOption(NUM_RESULTS_OPTION)) {
        numResults = Integer.parseInt(cmdline.getOptionValue(NUM_RESULTS_OPTION));
      }
    } catch (NumberFormatException e) {
      System.err.println("Invalid " + NUM_RESULTS_OPTION + ": " + cmdline.getOptionValue(NUM_RESULTS_OPTION));
      System.exit(-1);
    }

    String similarity = "LM";
    if (cmdline.hasOption(SIMILARITY_OPTION)) {
      similarity = cmdline.getOptionValue(SIMILARITY_OPTION);
    }

    PrintStream out = new PrintStream(System.out, true, "UTF-8");

    IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(indexLocation.getAbsolutePath())));
    IndexSearcher searcher = new IndexSearcher(reader);

    if (similarity.equalsIgnoreCase("BM25")) {
      searcher.setSimilarity(new BM25Similarity());
    } else if (similarity.equalsIgnoreCase("LM")) {
      searcher.setSimilarity(new LMDirichletSimilarity(2500.0f));
    }

    MicroblogTopicSet topics = MicroblogTopicSet.fromFile(new File(topicsFile));
    for ( MicroblogTopic topic : topics ) {
      Filter filter = NumericRangeFilter.newLongRange(StatusField.ID.name, 0L, topic.getQueryTweetTime(), true, true);
      Query query = AnalyzerUtils.buildBagOfWordsQuery(StatusField.TEXT.name, IndexTweets.ANALYZER, topic.getQuery());

      TopDocs rs = searcher.search(query, filter, numResults);

      RerankerContext context = new RerankerContext(searcher, query, topic.getQuery(), filter);
      RerankerCascade cascade = new RerankerCascade(context);

      if (cmdline.hasOption(RM3_OPTION)) {
        cascade.add(new Rm3Reranker(IndexTweets.ANALYZER, StatusField.TEXT.name));
        cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
      } else {
        cascade.add(new RemoveRetweetsTemporalTiebreakReranker());
      }

      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher));

      for (int i=0; i<docs.documents.length; i++) {
        String qid = topic.getId().replaceFirst("^MB0*", "");
        out.println(String.format("%s Q0 %s %d %f %s", qid,
            docs.documents[i].getField(StatusField.ID.name).numericValue(), (i+1), docs.scores[i], runtag));
      }
    }
    reader.close();
    out.close();
  }
}
