package io.anserini.search.query;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QaTopicReader extends TopicReader {

  public QaTopicReader(Path topicFile) {
    super(topicFile);
  }

  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> map = new TreeMap<>();
    Map<String,String> fields = new HashMap<>();

    String pattern = "<QApairs id=\'(.*)\'>";
    Pattern r = Pattern.compile(pattern);

    String prevLine = "";
    String id = "";

    String line;
    while ((line = bRdr.readLine()) != null) {
      Matcher m = r.matcher(line);

      if (m.find()) {
        id = m.group(1);
      }

      if (prevLine != null && prevLine.startsWith("<question>")) {
        fields.put("title", line);
        map.put(Integer.parseInt(id), fields);
      }
      prevLine = line;
    }
    return map;
  }
}
