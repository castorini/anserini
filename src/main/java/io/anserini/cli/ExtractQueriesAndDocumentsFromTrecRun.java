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

package io.anserini.cli;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.anserini.index.IndexReaderUtils;
import io.anserini.search.topicreader.Topics;
import io.anserini.util.LoggingBootstrap;

public final class ExtractQueriesAndDocumentsFromTrecRun {
  private static final Logger LOG = LogManager.getLogger(ExtractQueriesAndDocumentsFromTrecRun.class);

  private ExtractQueriesAndDocumentsFromTrecRun() {
    throw new UnsupportedOperationException();
  }

  public static class Args {
    @Option(name = "--index", metaVar = "[index]", required = true, usage = "Lucene index with raw documents.")
    public String index;

    @Option(name = "--run", metaVar = "[path]", required = true, usage = "Path to input TREC run file.")
    public String run;

    @Option(name = "--topics", metaVar = "[topics]", required = true, usage = "Topics used in the TREC run file.")
    public String topics;

    @Option(name = "--topic-reader", usage = "TopicReader for reading topics.")
    public String topicReader = "TsvString";

    @Option(name = "--topic-field", usage = "Default field in topic to extract.")
    public String topicField = "title";

    @Option(name = "--output", metaVar = "[output]", required = true, usage = "Output in jsonl format.")
    public String output;

    @Option(name = "--hits", metaVar = "[num]", usage = "Number of candidates to generate.")
    public int hits = 100;

    @Option(name = "--no-parse", usage = "Do not parse raw documents.")
    public boolean noParse = false;

    @Option(name = "--help", help = true, usage = "Print this help message and exit.")
    public boolean help = false;
  }

  private static final String[] argsOrdering = new String[] {
    "--index", "--run", "--topics", "--topic-reader", "--topic-field", "--output", "--hits", "--no-parse", "--help"};

  public static void main(String[] args) throws IOException {
    LoggingBootstrap.installJulToSlf4jBridge();

    Args parsedArgs = new Args();
    CmdLineParser parser = new CmdLineParser(parsedArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException exception) {
      System.err.println(String.format("Error: %s", exception.getMessage()));
      CliUtils.printUsage(parser, ExtractQueriesAndDocumentsFromTrecRun.class, argsOrdering);

      return;
    }

    if (parsedArgs.help) {
      CliUtils.printUsage(parser, ExtractQueriesAndDocumentsFromTrecRun.class, argsOrdering);
      return;
    }

    run(parsedArgs);
  }

  private static void run(Args args) throws IOException {
    SortedMap<String, Map<String, String>> topics = getTopics(args.topics, args.topicReader);
    ObjectMapper mapper = new ObjectMapper();
    boolean parse = !args.noParse;
    int qidCount = 0;
    try (IndexReader indexReader = getIndexReader(args.index);
         PrintWriter output = new PrintWriter(Files.newBufferedWriter(Paths.get(args.output), StandardCharsets.UTF_8));
         BufferedReader br = Files.newBufferedReader(Paths.get(args.run), StandardCharsets.UTF_8)) {
      List<Map<String, Object>> candidates = new ArrayList<>();
      String line;
      String curQid = "";
      while ((line = br.readLine()) != null) {
        String[] data = line.split("\\s+");
        int rank = Integer.parseInt(data[3]);
        if (rank > args.hits) {
          continue;
        }
        String qid = data[0];
        if (!curQid.equals(qid)) {
          if (!curQid.isEmpty()) {
            candidates = writeQuery(output, mapper, topics, args.topicField, candidates, curQid);
            qidCount++;
          }
          curQid = qid;
        }
        addCandidate(candidates, mapper, indexReader, data[2], Float.parseFloat(data[4]), parse);
      }

      if (!curQid.isEmpty()) {
        writeQuery(output, mapper, topics, args.topicField, candidates, curQid);
        qidCount++;
      }
    }

    LOG.info("Processed {} qids.", qidCount);
  }

  private static void addCandidate(List<Map<String, Object>> candidates, ObjectMapper mapper,
      IndexReader indexReader, String docid, float score, boolean parse) throws IOException {
    String raw = IndexReaderUtils.documentRaw(indexReader, docid);
    if (raw == null) {
      throw new IllegalArgumentException("Raw document with docid " + docid + " not found in index.");
    }

    Map<String, Object> candidate = new LinkedHashMap<>();
    candidate.put("docid", docid);
    candidate.put("score", score);

    candidate.put("doc", CliUtils.formatRawDocument(raw, parse, mapper));
    candidates.add(candidate);
  }

  private static List<Map<String, Object>> writeQuery(PrintWriter output, ObjectMapper mapper,
      SortedMap<String, Map<String, String>> topics, String topicField, List<Map<String, Object>> candidates,
      String qid) throws JsonProcessingException {
    Map<String, String> topic = topics.get(qid);
    String query = topic == null ? null : topic.get(topicField);
    if (query == null) {
      throw new IllegalArgumentException("Unable to find query for " + qid);
    }

    Map<String, Object> queryMap = new LinkedHashMap<>();
    queryMap.put("query", new LinkedHashMap<>(Map.of("qid", qid, "text", query)));
    queryMap.put("candidates", candidates);
    output.println(mapper.writeValueAsString(queryMap));

    return new ArrayList<>();
  }

  private static SortedMap<String, Map<String, String>> getTopics(String topics, String topicsReader) throws IOException {
    SortedMap<?, Map<String, String>> resolvedTopics = Topics.resolve(topics, topicsReader);
    SortedMap<String, Map<String, String>> convertedTopics = new TreeMap<>();
    for (Map.Entry<?, Map<String, String>> entry : resolvedTopics.entrySet()) {
      convertedTopics.put(String.valueOf(entry.getKey()), entry.getValue());
    }
    LOG.info("Successfully loaded topics: " + topics);

    return convertedTopics;
  }

  private static IndexReader getIndexReader(String index) throws IOException {
    String resolvedIndex = IndexReaderUtils.getIndex(index).toString();

    LOG.info("Fetching raw documents from index: " + resolvedIndex);
    try {
      return IndexReaderUtils.getReader(resolvedIndex);
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("\"%s\" does not appear to have a valid inverted index.", resolvedIndex));
    }
  }
}
