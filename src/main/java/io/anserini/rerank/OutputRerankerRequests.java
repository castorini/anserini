package io.anserini.rerank;

import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.StoredFields;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.index.Term;
import org.checkerframework.checker.units.qual.K;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import io.anserini.index.IndexInfo;
import io.anserini.index.IndexReaderUtils;
import io.anserini.search.ScoredDoc;
import io.anserini.search.topicreader.TopicReader;
import io.anserini.search.topicreader.Topics;

public class OutputRerankerRequests<K extends Comparable<K>> {
    private List<K> qids= new ArrayList<>();
    private List<String> queries = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper(); 
    private IndexReader indexReader;
    private PrintWriter output;

    public OutputRerankerRequests(String[] topics, String index, String outputPath) throws IOException {
        this.indexReader = getIndexReader(index);
        getTopics(topics);
        this.output = outputPath == null ? null : new PrintWriter(Files.newBufferedWriter(Paths.get(outputPath), StandardCharsets.UTF_8));;
    }

    public void fromScoredDoc(K qid, ScoredDoc[] results) throws JsonProcessingException, IOException {
      List<Map<String, Object>> candidates = new ArrayList<>();
      for (ScoredDoc r : results) {
        String raw = IndexReaderUtils.documentRaw(indexReader, r.docid);
        JsonNode rootNode = mapper.readTree(raw);
        Map<String, Object> content = mapper.convertValue(rootNode, Map.class);
        content.remove(Constants.ID); // Remove the ID field from the content
        content.remove("_id");
        content.remove("docid");
        Map<String, Object> candidate = new LinkedHashMap<>();
        candidate.put("docid", r.docid);
        candidate.put("score", r.score);
        candidate.put("doc", content);
        candidates.add(candidate);
      }
      Map<String, Object> queryMap = new LinkedHashMap<>();
      queryMap.put("query", new LinkedHashMap<>(Map.of("qid", qid, "text", queries.get(qids.indexOf(qid)))));
      queryMap.put("candidates", candidates);
      output.println(mapper.writeValueAsString(queryMap));
    }

    public void getTopics(String[] topicFiles) throws IOException {
      SortedMap<K, Map<String, String>> topics = new TreeMap<>();
      for (String topicsFile : topicFiles) {
        Path topicsFilePath = Paths.get(topicsFile);
        System.out.println("Loading raw topics from: " + topicsFilePath);
        if (Files.exists(topicsFilePath) && Files.isRegularFile(topicsFilePath) && Files.isReadable(topicsFilePath)) {
            topicsFile = topicsFilePath.getFileName().toString();
        }
        Topics ref = Topics.getBaseTopics(topicsFile);
        if (ref==null) {
            throw new IllegalArgumentException(String.format("\"%s\" does not refer to valid topics.", topicsFilePath));
        } else {
            topics.putAll(TopicReader.getTopics(ref));
        }

        try {
            topics.forEach((qid, topic) -> {
                String query = topic.get("title");
                assert query != null;
                qids.add(qid);
                queries.add(query);
            });
        } catch (AssertionError|Exception e) {
            throw new IllegalArgumentException(String.format("Unable to read topic field \"%s\".", "title"));
        }
       }
    }

    public IndexReader getIndexReader(String index) {
        IndexInfo currentIndex = IndexInfo.get(index);
        Path indexPath = IndexReaderUtils.getIndex(currentIndex.invertedIndex);
        System.out.println("Loading raw documents from index: " + indexPath);
        try {
            return IndexReaderUtils.getReader(indexPath);
        } catch (IOException e) {
            throw new IllegalArgumentException(String.format("\"%s\" does not appear to have a valid inverted index.", index));
        }
    }

    public void close() {
        output.close();
    }
}
