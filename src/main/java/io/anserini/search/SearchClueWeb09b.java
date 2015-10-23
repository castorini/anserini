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

import static io.anserini.index.IndexClueWeb09b.FIELD_BODY;
import static io.anserini.index.IndexClueWeb09b.FIELD_ID;
import static io.anserini.index.IndexClueWeb09b.analyzer;

import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.FSDirectory;

/**
 * Searcher for ClueWeb09 Category B Corpus.
 * 200 Topics from TREC 2009-1012 Web Track.
 */
public final class SearchClueWeb09b implements Closeable {

  private final IndexReader reader;

  public SearchClueWeb09b(String indexDir) throws IOException {

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
   * @param topicsFile One of: topics.web.1-50.txt topics.web.51-100.txt topics.web.101-150.txt topics.web.151-200.txt
   * @return SortedMap where keys are query/topic IDs and values are title portions of the topics
   * @throws IOException
   */
  static SortedMap<Integer, String> readQueries(Path topicsFile) throws IOException {

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
   * Prints TREC submission file to the standard output stream.
   *
   * @param topicsFile One of: topics.web.1-50.txt topics.web.51-100.txt topics.web.101-150.txt topics.web.151-200.txt
   * @param operator   Default search operator: AND or OR
   * @throws IOException
   * @throws ParseException
   */

  public void search(String topicsFile, String submissionFile, QueryParser.Operator operator) throws IOException, ParseException {

    Path topicsPath = Paths.get(topicsFile);

    if (!Files.exists(topicsPath) || !Files.isRegularFile(topicsPath) || !Files.isReadable(topicsPath)) {
      throw new IllegalArgumentException("Topics file : " + topicsFile + " does not exist or is not a (readable) file.");
    }

    IndexSearcher searcher = new IndexSearcher(reader);
    searcher.setSimilarity(new BM25Similarity());


    final String runTag = "BM25_Krovetz_" + FIELD_BODY + "_" + operator.toString();

    PrintWriter out = new PrintWriter(Files.newBufferedWriter(Paths.get(submissionFile), StandardCharsets.US_ASCII));


    QueryParser queryParser = new QueryParser(FIELD_BODY, analyzer());
    queryParser.setDefaultOperator(operator);


    SortedMap<Integer, String> topics = readQueries(topicsPath);

    for (Map.Entry<Integer, String> entry : topics.entrySet()) {

      int qID = entry.getKey();
      String queryString = entry.getValue();
      Query query = queryParser.parse(queryString);

      /**
       * For Web Tracks 2010,2011,and 2012; an experimental run consists of the top 10,000 documents for each topic query.
       */
      ScoreDoc[] hits = searcher.search(query, 1000).scoreDocs;

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
        out.print(i);
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

  public static void main(String[] args) throws IOException, ParseException {

    if (args.length != 3) {
      System.err.println("Usage: SearcherCW09B <topicsFile> <submissionFile> <indexDir>");
      System.err.println("topicsFile: input file containing queries. One of: topics.web.1-50.txt topics.web.51-100.txt topics.web.101-150.txt topics.web.151-200.txt");
      System.err.println("submissionFile: redirect stdout to capture the submission file for trec_eval or gdeval.pl");
      System.err.println("indexDir: index directory");
      System.exit(1);
    }

    String topicsFile = args[0];
    String submissionFile = args[1];
    String indexDir = args[2];

    SearchClueWeb09b searcher = new SearchClueWeb09b(indexDir);
    searcher.search(topicsFile, submissionFile, QueryParser.Operator.OR);
    searcher.close();
  }
}
