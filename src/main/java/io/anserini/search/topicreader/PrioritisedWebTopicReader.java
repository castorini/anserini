package io.anserini.search.topicreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;

public class PrioritisedWebTopicReader extends TopicReader<Integer> {

  public PrioritisedWebTopicReader(Path topicFile) {
    super(topicFile);
  }
  
  @Override
  public SortedMap<Integer, Map<String, String>> read(BufferedReader bRdr) throws IOException {
    SortedMap<Integer, Map<String, String>> ret =  new TreeMap<>();

    String line;
    while ((line = bRdr.readLine()) != null) {
      Map<String,String> fields = new HashMap<>();
      fields.put("title", StringUtils.substringAfterLast(line.trim(), ":"));
      fields.put("priority", StringUtils.substringBetween(line.trim(), ":"));
      ret.put(Integer.valueOf(StringUtils.substringBefore(line.trim(), ":")), fields);
    }

    return ret;
  }
}
