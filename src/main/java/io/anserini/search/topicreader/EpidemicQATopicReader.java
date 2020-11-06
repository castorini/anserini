package io.anserini.search.topicreader;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * A TopicReader class that reads from one of the Epidemic QA questions JSON files.
 */
public class EpidemicQATopicReader extends TopicReader<Integer> {
  private static final Logger LOG = LogManager.getLogger(EpidemicQATopicReader.class);
  private static final String QUESTION_ID_KEY = "question_id";
  private static final String QUESTION_KEY = "question";
  private static final String QUERY_KEY = "query";
  private static final String BACKGROUND_KEY = "background";

  public EpidemicQATopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader reader) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    ObjectMapper mapper = new ObjectMapper();
    String topicsJson;
    JsonNode arrayNode;
    try {
      InputStream stream =
          IOUtils.toInputStream(IOUtils.toString(reader), StandardCharsets.UTF_8);
      topicsJson = new String(stream.readAllBytes());
      arrayNode = mapper.readerFor(JsonNode.class).readTree(topicsJson);
      if (!arrayNode.isArray()) {
        throw new IllegalArgumentException("Top-level JSON node is not an array.");
      }
    } catch (IllegalArgumentException e) {
      LOG.error(e);
      return null;
    }

    for (JsonNode topicNode : arrayNode) {
      Map<String, String> fields = new HashMap<>();

      String questionId = topicNode.get(QUESTION_ID_KEY).asText();
      fields.put(QUESTION_ID_KEY, questionId);

      String question = topicNode.get(QUESTION_KEY).asText();
      fields.put(QUESTION_KEY, question);

      String query = topicNode.get(QUERY_KEY).asText();
      fields.put(QUERY_KEY, query);

      String background = topicNode.get(BACKGROUND_KEY).asText();
      fields.put(BACKGROUND_KEY, background);

      int id = Integer.parseInt(questionId.replaceAll("[^0-9]", ""));
      map.put(id, fields);
    }

    return map;
  }
}
