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

package io.anserini.rerank;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.ParserProperties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import io.anserini.index.Constants;
import io.anserini.index.IndexReaderUtils;
import io.anserini.index.prebuilt.PrebuiltInvertedIndex;
import io.anserini.search.ScoredDoc;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;
import io.anserini.util.PrebuiltIndexHandler;

public class GenerateRerankerRequests<K extends Comparable<K>> implements Closeable {
  private static final Logger LOG = LogManager.getLogger(GenerateRerankerRequests.class);

  public static class Args {
    @Option(name = "-index", required = true, usage = "Name or path of Lucene index with raw documents")
    public String index;

    @Option(name = "-run", metaVar = "[path]", required = true, usage = "Path to TREC run file to generate reranker requests for")
    public String run;

    @Option(name = "-topics", required = true, usage = "Name or path of original topics")
    public String topics;

    @Option(name = "-topicReader", usage = "TopicReader to use.")
    public String topicReader = "TsvString";

    @Option(name = "-output", metaVar = "[output]", required = true, usage = "output file")
    public String output;

    @Option(name = "-hits", metaVar = "[number]", usage = "Top number of results to generate for")
    public int hits = 100;

    @Option(name = "-options", usage = "Print information about options.")
    public Boolean options = false;
  }

  private static final String TOPIC_FIELD = "title"; // base topics field
  
  private final Args args;
  private List<K> qids= new ArrayList<>();
  private List<String> queries = new ArrayList<>();
  private final ObjectMapper mapper = new ObjectMapper(); 
  private List<Map<String, Object>> candidates;
  private IndexReader indexReader;
  private PrintWriter output;

  public GenerateRerankerRequests(Args args) throws IOException {
    this.args = args;
    this.indexReader = getIndexReader(args.index);
    this.output = new PrintWriter(Files.newBufferedWriter(Paths.get(args.output), StandardCharsets.UTF_8));
    this.candidates = new ArrayList<>();
    getTopics(args.topics);
  }

  public void addCandidate(String docid, float score) throws JsonProcessingException {
    String raw = IndexReaderUtils.documentRaw(indexReader, docid);
    if (raw == null) {
      throw new IllegalArgumentException("Raw document with docid " + docid + " not found in index.");
    }
    JsonNode rootNode = mapper.readTree(raw);
    Map<String, Object> content = mapper.convertValue(rootNode, new TypeReference<Map<String, Object>>() {});
    content.remove(Constants.ID); // Remove the ID field from the content
    content.remove("_id");
    content.remove("docid");
    Map<String, Object> candidate = new LinkedHashMap<>();
    candidate.put("docid", docid);
    candidate.put("score", score);
    candidate.put("doc", content);
    candidates.add(candidate);
  }

  public void writeQuery(K qid) throws JsonProcessingException {
    int index = qids.indexOf(qid);
    if (index == -1) {
      throw new IllegalArgumentException("Query ID not found in the list of topics: " + qid);
    }
    Map<String, Object> queryMap = new LinkedHashMap<>();
    queryMap.put("query", new LinkedHashMap<>(Map.of("qid", qid, "text", queries.get(index))));
    queryMap.put("candidates", candidates);
    output.println(mapper.writeValueAsString(queryMap));
    candidates = new ArrayList<>();
  }

  public void useScoredDoc(K qid, ScoredDoc[] results) throws JsonProcessingException, IOException {
    for (ScoredDoc r : results) {
        addCandidate(r.docid, r.score);
    }
    writeQuery(qid);
  }

  public void useRunFile() throws IOException {
    Path filepath = Paths.get(args.run);
    try (BufferedReader br = new BufferedReader(new FileReader(filepath.toFile()))) {
      String line, curQid = "";
      while ((line = br.readLine()) != null) {
        String[] data = line.split("\\s+");
        int rank = Integer.parseInt(data[3]);
        if (rank > args.hits) {
          continue; // Skip if rank exceeds the specified hits
        }
        String qid = data[0];
        if (!curQid.equals(qid)) {
          if (!curQid.isEmpty()) {
            writeQuery(findQid(curQid));
          }
          curQid = qid;
        }
        String docid = data[2];
        float score = Float.parseFloat(data[4]);
        addCandidate(docid, score);
      }
      writeQuery(findQid(curQid));
    }
  }

  private K findQid(String qidStr) {
    for (K qid : qids) {
      if (qid.toString().equals(qidStr)) {
        return qid;
      }
    }
    throw new IllegalArgumentException("Query ID not found in the list of topics: " + qidStr);
  }

  public void getTopics(String topicsFile) throws IOException {
    SortedMap<K, Map<String, String>> topics = new TreeMap<>();
    Path topicsFilePath = Paths.get(topicsFile);
    if (!Files.exists(topicsFilePath) || !Files.isRegularFile(topicsFilePath) || !Files.isReadable(topicsFilePath)) {
        Topics ref = Topics.getBaseTopics(topicsFile);
        if (ref==null) {
          throw new IllegalArgumentException(String.format("\"%s\" does not refer to valid topics.", topicsFile));
        } else {
          LOG.info("Generating reranker requests with raw topics from: " + ref.toString());
          topics.putAll(TopicReader.getTopics(ref));
        }
    } else {
        try {
          @SuppressWarnings("unchecked")
          TopicReader<K> tr = (TopicReader<K>) Class
              .forName(String.format("io.anserini.search.topicreader.%sTopicReader", args.topicReader))
              .getConstructor(Path.class).newInstance(topicsFilePath);

          LOG.info("Generating reranker requests with raw topics from: " + topicsFilePath.toString());
          topics.putAll(tr.read());
        } catch (Exception e) {
          throw new IllegalArgumentException(String.format("Unable to load topic reader \"%s\".", args.topicReader));
        }
    }

    try {
      topics.forEach((qid, topic) -> {
        String query = topic.get(TOPIC_FIELD);
        assert query != null;
        qids.add(qid);
        queries.add(query);
      });
    } catch (AssertionError|Exception e) {
      throw new IllegalArgumentException(String.format("Unable to read topic field \"%s\".", TOPIC_FIELD));
    }
  }

  // TODO (2026/01/28): This method should really be in IndexReaderUtils and renamed something like getCorpusIndexReader.
  public IndexReader getIndexReader(String index) throws IOException {
    PrebuiltIndexHandler handler = PrebuiltIndexHandler.get(index);

    // TODO (2026/01/31): This method is janky, need to refactor.
    String resolvedIndex = null;

    boolean localExists = Files.exists(Paths.get(index));

    // If both a prebuilt label and a local path exist, fail fast with
    // a clear error to force disambiguation.
    if (handler != null && localExists) {
      throw new IllegalArgumentException(String.format(
          "Ambiguous index reference \"%s\": both a prebuilt index label and a local path exist. " +
          "Please disambiguate by specifying a full local path or removing/renaming the local directory.", index));
    }

    if (handler != null) {
      PrebuiltInvertedIndex.Entry entry = PrebuiltInvertedIndex.get(index);
      if (entry != null) {
        resolvedIndex = IndexReaderUtils.getIndex(entry.corpusIndex).toString();
      }
    } else {
      // Not a known prebuilt label; resolve as prebuilt (if any) or local path.
      resolvedIndex = IndexReaderUtils.getIndex(index).toString();
    }

    LOG.info("Generating reranker requests with raw documents from index: " + resolvedIndex);
    try {
      return IndexReaderUtils.getReader(resolvedIndex);
    } catch (IOException e) {
      throw new IllegalArgumentException(String.format("\"%s\" does not appear to have a valid inverted index.", resolvedIndex));
    }
  }

  public void close() {
    output.close();
  }

  public void run() throws IOException {
    useRunFile();
  }

  public static void main(String[] args) {
    Args generateArgs = new Args();
    CmdLineParser parser = new CmdLineParser(generateArgs, ParserProperties.defaults().withUsageWidth(120));

    try {
      parser.parseArgument(args);
    } catch (CmdLineException e) {
      if (generateArgs.options) {
        System.err.printf("Options for %s:\n\n", GenerateRerankerRequests.class.getSimpleName());
        parser.printUsage(System.err);

        List<String> required = new ArrayList<>();
        parser.getOptions().forEach((option) -> {
          if (option.option.required()) {
            required.add(option.option.toString());
          }
        });

        System.err.printf("\nRequired options are %s\n", required);
      } else {
        System.err.printf("Error: %s. For help, use \"-options\" to print out information about options.\n", e.getMessage());
      }

      return;
    }

    try(GenerateRerankerRequests<?> generator = new GenerateRerankerRequests<>(generateArgs)){
      generator.run();
    } catch (Exception e) {
      System.err.printf("Error: %s\n", e.getMessage());
    }
  }
}
