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

import io.anserini.ltr.WebCollectionLtrDataGenerator;
import io.anserini.ltr.feature.FeatureExtractors;
import io.anserini.rerank.IdentityReranker;
import io.anserini.rerank.RerankerCascade;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.ScoredDocuments;
import io.anserini.rerank.rm3.Rm3Reranker;
import io.anserini.util.AnalyzerUtils;
import io.anserini.util.Qrels;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import static io.anserini.index.IndexWebCollection.FIELD_BODY;
import static io.anserini.index.IndexWebCollection.FIELD_ID;

/**
 * Searcher for Gov2, ClueWeb09, and ClueWeb12 corpra.
 * TREC Web Tracks from 2009 to 2014
 * TREC Terabyte Tracks from 2004 to 2006
 */
public final class SearchWebCollection implements Closeable {

  private static final Logger LOG = LogManager.getLogger(SearchWebCollection.class);

  private final IndexReader reader;

  public SearchWebCollection(String indexDir) throws IOException {

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

  private static String extract(String line, String tag) {

    int i = line.indexOf(tag);

    if (i == -1) throw new IllegalArgumentException("line does not contain the tag : " + tag);

    int j = line.indexOf("\"", i + tag.length() + 2);

    if (j == -1) throw new IllegalArgumentException("line does not contain quotation");

    return line.substring(i + tag.length() + 2, j);
  }

  /**
   * Read topics of TREC Web Tracks from 2009 to 2014
   *
   * @param topicsFile One of: topics.web.1-50.txt topics.web.51-100.txt topics.web.101-150.txt topics.web.151-200.txt topics.web.201-250.txt topics.web.251-300.txt
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException
   */
  public static SortedMap<Integer, String> readWebTrackQueries(Path topicsFile) throws IOException {

    SortedMap<Integer, String> map = new TreeMap<>();
    List<String> lines = Files.readAllLines(topicsFile, StandardCharsets.UTF_8);

    String number = "";
    String query = "";

    for (String line : lines) {

      line = line.trim();

      if (line.startsWith("<topic"))
        number = extract(line, "number");

      if (line.startsWith("<query>") && line.endsWith("</query>"))
        query = line.substring(7, line.length() - 8).trim();

      if (line.startsWith("</topic>"))
        map.put(Integer.parseInt(number), query);

    }

    lines.clear();
    return map;
  }

  /**
   * Read topics of TREC Terabyte Tracks from 2004 to 2006
   *
   * @param topicsFile One of: topics.701-750.txt topics.751-800.txt topics.801-850.txt
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException
   */
  public static SortedMap<Integer, String> readTeraByteTackQueries(Path topicsFile) throws IOException {

    SortedMap<Integer, String> map = new TreeMap<>();
    List<String> lines = Files.readAllLines(topicsFile, StandardCharsets.UTF_8);

    String number = "";
    String query = "";

    boolean found = false;
    for (String line : lines) {

      line = line.trim();

      if (!found && "<top>".equals(line)) {
        found = true;
        continue;
      }

      if (found && line.startsWith("<title>"))
        query = line.substring(7).trim();

      if (found && line.startsWith("<num>")) {
        int i = line.lastIndexOf(" ");
        if (-1 == i) throw new RuntimeException("cannot find space in : " + line);
        number = line.substring(i).trim();
      }

      if (found && "</top>".equals(line)) {
        found = false;
        int qID = Integer.parseInt(number);

        map.put(qID, query);

      }
    }
    lines.clear();
    return map;
  }

  /**
   * Prints TREC submission file to the standard output stream.
   *
   * @param topics     queries
   * @param similarity similarity
   * @throws IOException
   * @throws ParseException
   */

  public void search(SortedMap<Integer, String> topics, String submissionFile, Similarity similarity, int numHits, RerankerCascade cascade) throws IOException, ParseException {


    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(similarity);


    final String runTag = "BM25_EnglishAnalyzer_" + FIELD_BODY + "_" + similarity.toString();

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(submissionFile), StandardCharsets.US_ASCII));


    QueryParser queryParser = new QueryParser(FIELD_BODY, new EnglishAnalyzer());
    queryParser.setDefaultOperator(QueryParser.Operator.OR);

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {

      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = queryParser.parse(queryString);

      /**
       * For Web Tracks 2010,2011,and 2012; an experimental run consists of the top 10,000 documents for each topic query.
       */
      TopDocs rs = searcher.search(query, numHits);
      ScoreDoc[] hits = rs.scoreDocs;
      List<String> queryTokens = AnalyzerUtils.tokenize(new EnglishAnalyzer(), queryString);
      RerankerContext context = new RerankerContext(searcher, query, String.valueOf(qID), queryString,
              queryTokens, FIELD_BODY, null);
      ScoredDocuments docs = cascade.run(ScoredDocuments.fromTopDocs(rs, searcher), context);

      /**
       * the first column is the topic number.
       * the second column is currently unused and should always be "Q0".
       * the third column is the official document identifier of the retrieved document.
       * the fourth column is the rank the document is retrieved.
       * the fifth column shows the score (integer or floating point) that generated the ranking.
       * the sixth column is called the "run tag" and should be a unique identifier for your
       */
      for (int i = 0; i < hits.length; i++) {
        int docId = hits[i].doc;
        Document doc = searcher.doc(docId);
        out.print(qID);
        out.print("\tQ0\t");
        out.print(doc.get(FIELD_ID));
        out.print("\t");
        out.print(i + 1);
        out.print("\t");
        out.print(hits[i].score);
        out.print("\t");
        out.print(runTag);
        out.println();
      }
    }
    out.flush();
    out.close();
  }

  public static void main(String[] args) throws Exception {

    long curTime = System.nanoTime();
    SearchArgs searchArgs = new SearchArgs();
    CmdLineParser parser = new CmdLineParser(searchArgs, ParserProperties.defaults().withUsageWidth(90));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      System.err.println(e.getMessage());
      parser.printUsage(System.err);
      System.err.println("Example: SearchWebCollection" + parser.printExample(OptionHandlerFilter.REQUIRED));
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

    RerankerCascade cascade = new RerankerCascade();
    if (searchArgs.rm3) {
      cascade.add(new Rm3Reranker(new EnglishAnalyzer(), "body", "src/main/resources/io/anserini/rerank/rm3/rm3-stoplist.gov2.txt"));
    } else {
      cascade.add(new IdentityReranker());
    }
    FeatureExtractors extractors = null;
    if (searchArgs.extractors != null) {
      extractors = FeatureExtractors.loadExtractor(searchArgs.extractors);
    }

    if (searchArgs.dumpFeatures) {
      PrintStream out = new PrintStream(searchArgs.featureFile);
      Qrels qrels = new Qrels(searchArgs.qrels);
      cascade.add(new WebCollectionLtrDataGenerator(out,  qrels, extractors));
    }

    Path topicsFile = Paths.get(searchArgs.topics);

    if (!Files.exists(topicsFile) || !Files.isRegularFile(topicsFile) || !Files.isReadable(topicsFile)) {
      throw new IllegalArgumentException("Topics file : " + topicsFile + " does not exist or is not a (readable) file.");
    }

    SortedMap<Integer, String> topics = io.anserini.document.Collection.GOV2.equals(searchArgs.collection) ? readTeraByteTackQueries(topicsFile) : readWebTrackQueries(topicsFile);

    SearchWebCollection searcher = new SearchWebCollection(searchArgs.index);
    searcher.search(topics, searchArgs.output, similarity, searchArgs.hits, cascade);
    searcher.close();
  }
}
