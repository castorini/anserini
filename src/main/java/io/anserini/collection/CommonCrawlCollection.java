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

package io.anserini.collection;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.tukaani.xz.XZInputStream;

public class CommonCrawlCollection extends DocumentCollection<CommonCrawlCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(AfribertaCollection.class);
  
  public CommonCrawlCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".txt.xz", ".txt"));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public FileSegment<CommonCrawlCollection.Document> createFileSegment(Path p) throws IOException {
    return new CommonCrawlCollection.Segment(p);
  }
  
  /**
   * A file in a Common Crawl collection, typically containing multiple documents.
   */
  
  public static class Segment<T extends Document> extends AfribertaCollection.Segment<T>{
    private JsonNode node = null;
    private List<JsonNode> jsonNodeArray = null;
    private Iterator<JsonNode> iterator; // iterator for JSON line objects
  
    public Segment(Path path) throws IOException {
      super(path);
  
      if (path.toString().endsWith(".xz")) {
        bufferedReader = new BufferedReader(new InputStreamReader(
            new XZInputStream(new FileInputStream(path.toString()))));
      } else {
        bufferedReader = new BufferedReader(new FileReader(path.toString()));
      }
  
      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if (iterator.hasNext()) {
        node = iterator.next();
      }
    }
  }

  
  public static class Document extends AfribertaCollection.Document{
    public Document(JsonNode json) {
      super(json);
    }
  }
  
}
