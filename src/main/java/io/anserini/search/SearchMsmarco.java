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

import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import io.anserini.search.query.DisjunctionMaxQueryGenerator;
import io.anserini.search.query.QueryGenerator;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.OptionHandlerFilter;
import org.kohsuke.args4j.ParserProperties;
import org.kohsuke.args4j.spi.MapOptionHandler;

import java.io.File;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Class that performs retrieval for the MS MARCO passage ranking task.
 */
public class SearchMsmarco {
  public static class Args {
    // required arguments
    @Option(name = "-queries", metaVar = "[file]", required = true, usage="Queries file.")
    public String qid_queries = "";

    @Option(name = "-output", metaVar = "[file]", required = true, usage = "Output run file.")
    public String output = "";

    @Option(name = "-index", metaVar = "[path]", required = true, usage = "Index path.")
    public String index = "";

    // optional arguments
    @Option(name = "-threads", metaVar = "[number]", usage = "Maximum number of threads.")
    public int threads = 1;

    @Option(name = "-hits", metaVar = "[number]", usage = "Number of hits to retrieve.")
    public int hits = 10;

    @Option(name = "-k1", metaVar = "[value]", usage = "BM25 k1 parameter.")
    public float k1 = 0.82f;

    @Option(name = "-b", metaVar = "[value]", usage = "BM25 b parameter.")
    public float b = 0.68f;

    // See our MS MARCO documentation to understand how these parameter values were tuned.
    @Option(name = "-rm3", usage = "Use RM3.")
    public boolean rm3 = false;

    @Option(name = "-fbTerms", metaVar = "[number]", usage = "RM3: number of expansion terms.")
    public int fbTerms = 10;

    @Option(name = "-fbDocs", metaVar = "[number]", usage = "RM3: number of documents.")
    public int fbDocs = 10;

    @Option(name = "-originalQueryWeight", metaVar = "[value]", usage = "RM3: weight of original query.")
    public float originalQueryWeight = 0.5f;

    @Option(name = "-fields", metaVar = "[key=value]", handler = MapOptionHandler.class,
            usage = "Fields to search with assigned float weights.")
    public Map<String, String> fields = new HashMap<>();

    @Option(name = "-dismax", usage = "Use disjunction max queries when searching multiple fields.")
    public boolean dismax = false;

    @Option(name = "-dismax.tiebreaker", metaVar = "[value]", usage = "The tiebreaker weight to use in disjunction max queries.")
    public float dismax_tiebreaker = 0.0f;

    @Option(name = "-keepstopwords", usage = "Boolean switch to keep stopwords in the query topics")
    public boolean keepstop = false;

    @Option(name = "-stemmer", usage = "Stemmer: one of the following porter,krovetz,none. Default porter")
    public String stemmer = "porter";

    @Option(name = "-stopwords", metaVar = "[file]", forbids = "-keepStopwords",
            usage = "Path to file with stopwords.")
    public String stopwords = null;

  }

  public static void main(String[] args) throws Exception {
    Args retrieveArgs = new Args();
    CmdLineParser parser = new CmdLineParser(retrieveArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: Eval " + parser.printExample(OptionHandlerFilter.REQUIRED));
      return;
    }

    long totalStartTime = System.nanoTime();

    Analyzer analyzer = DefaultEnglishAnalyzer.fromArguments(
            retrieveArgs.stemmer, retrieveArgs.keepstop, retrieveArgs.stopwords);
    System.out.println("Initializing analyzer with stemmer=" + retrieveArgs.stemmer + ", keepstop=" +
            retrieveArgs.keepstop + ", stopwords=" + retrieveArgs.stopwords);

    SimpleSearcher searcher = new SimpleSearcher(retrieveArgs.index, analyzer);
    searcher.setBM25(retrieveArgs.k1, retrieveArgs.b);
    System.out.println("Initializing BM25, setting k1=" + retrieveArgs.k1 + " and b=" + retrieveArgs.b + "");

    if (retrieveArgs.rm3) {
      searcher.setRM3(retrieveArgs.fbTerms, retrieveArgs.fbDocs, retrieveArgs.originalQueryWeight);
      System.out.println("Initializing RM3, setting fbTerms=" + retrieveArgs.fbTerms + ", fbDocs=" + retrieveArgs.fbDocs
              + " and originalQueryWeight=" + retrieveArgs.originalQueryWeight);
    }

    Map<String, Float> fields = new HashMap<>();
    retrieveArgs.fields.forEach((key, value) -> fields.put(key, Float.valueOf(value)));
    if (retrieveArgs.fields.size() > 0) {
      System.out.println("Performing weighted field search with fields=" + retrieveArgs.fields);
    }

    QueryGenerator queryGenerator;
    if (retrieveArgs.dismax) {
      queryGenerator = new DisjunctionMaxQueryGenerator(retrieveArgs.dismax_tiebreaker);
      System.out.println("Initializing dismax query generator, with tiebreaker=" + retrieveArgs.dismax_tiebreaker);
    } else {
      queryGenerator = new BagOfWordsQueryGenerator();
      System.out.println("Initializing bag-of-words query generator.");
    }

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(retrieveArgs.output), StandardCharsets.US_ASCII));

    if (retrieveArgs.threads == 1) {
      // single-threaded retrieval
      long startTime = System.nanoTime();
      List<String> lines = FileUtils.readLines(new File(retrieveArgs.qid_queries), "utf-8");

      for (int lineNumber = 0; lineNumber < lines.size(); ++lineNumber) {
        String line = lines.get(lineNumber);
        String[] split = line.trim().split("\t");
        String qid = split[0];
        String query = split[1];

        SimpleSearcher.Result[] hits;
        if (retrieveArgs.fields.size() > 0) {
          hits = searcher.searchFields(queryGenerator, query, fields, retrieveArgs.hits);
        } else {
          hits = searcher.search(queryGenerator, query, retrieveArgs.hits);
        }

        if (lineNumber % 100 == 0) {
          double timePerQuery = (double) (System.nanoTime() - startTime) / (lineNumber + 1) / 1e9;
          System.out.format("Retrieving query " + lineNumber + " (%.3f s/query)\n", timePerQuery);
        }

        for (int rank = 0; rank < hits.length; ++rank) {
          String docno = hits[rank].docid;
          out.println(qid + "\t" + docno + "\t" + (rank + 1));
        }
      }
    } else {
      // multithreaded batch retrieval
      List<String> lines = FileUtils.readLines(new File(retrieveArgs.qid_queries), "utf-8");
      List<String> queries = lines.stream().map(x -> x.trim().split("\t")[1]).collect(Collectors.toList());
      List<String> qids = lines.stream().map(x -> x.trim().split("\t")[0]).collect(Collectors.toList());

      Map<String, SimpleSearcher.Result[]> results;
      if (retrieveArgs.fields.size() > 0) {
        results = searcher.batchSearchFields(queryGenerator, queries, qids, retrieveArgs.hits, retrieveArgs.threads, fields);
      } else {
        results = searcher.batchSearch(queryGenerator, queries, qids, retrieveArgs.hits, retrieveArgs.threads);
      }

      for (String qid : qids) {
        SimpleSearcher.Result[] hits = results.get(qid);
        for (int rank = 0; rank < hits.length; ++rank) {
          String docno = hits[rank].docid;
          out.println(qid + "\t" + docno + "\t" + (rank + 1));
        }
      }
    }

    searcher.close();
    out.flush();
    out.close();

    double totalTime = (double) (System.nanoTime() - totalStartTime) / 1e9;
    System.out.format("Total retrieval time: %.3f s\n", totalTime);
    System.out.println("Done!");
  }
}
