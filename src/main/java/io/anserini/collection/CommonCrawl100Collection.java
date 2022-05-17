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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.*;
import org.tukaani.xz.XZInputStream;

/**
 * A collection of files from CC-100 corpus (https://data.statmt.org/cc-100/).
 * This can be used to read the CommonCrawl files
 */

public class CommonCrawl100Collection extends DocumentCollection<CommonCrawl100Collection.Document> {
  private static final Logger LOG = LogManager.getLogger(CommonCrawl100Collection.class);
  
  public CommonCrawl100Collection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".txt.xz", ".txt"));
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public FileSegment<CommonCrawl100Collection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }
  
  /**
   * A file in a Common Crawl collection, typically containing multiple documents.
   */
  public static class Segment<T extends Document> extends FileSegment<T>{
    private JsonNode node = null;
    private List<JsonNode> jsonNodeArray = null;
    private Iterator<JsonNode> iterator; // iterator for JSON line objects
  
    public Segment(Path path) throws IOException {
      super(path);
  
      if (path.toString().endsWith(".xz")) {
        bufferedReader = new BufferedReader(new InputStreamReader(
            new XZInputStream(new FileInputStream(path.toString()))));
      } else {
        bufferedReader = new BufferedReader(new InputStreamReader(
            new FileInputStream(path.toString())));
      }
      
      List<JsonNode> jsonNodeArray = getTxtFiles(bufferedReader);
  
      iterator = jsonNodeArray.iterator();
      if (iterator.hasNext()) {
        node = iterator.next();
      }
      
    }
  
    private List<JsonNode> getTxtFiles(BufferedReader reader) throws IOException {
      List<JsonNode> jsonNodeArray = new ArrayList<>();
      ObjectMapper objectMapper = new ObjectMapper();
    
      String line;
      int i=0;
      while ((line = reader.readLine()) != null) {
        try {
          String json = "{ \"id\" : \"doc_"+i+"\", \"contents\" : \""+line.replaceAll("[-+\"\'^[\\\\p{C}]\\\\]*","").strip()+"\" }";
          JsonNode jsonNode = objectMapper.readTree(json);
          jsonNodeArray.add(jsonNode);
          i++;
        } catch (IOException e) {
          LOG.error("Error: this is not a text file");
          e.printStackTrace();
        }
      }
  
      return jsonNodeArray;
    }
  
    @SuppressWarnings("unchecked")
    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null) {
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = (T) createNewDocument(node);
        if (iterator.hasNext()) { // if bufferedReader contains JSON line objects, we parse the next JSON into node
          node = iterator.next();
        } else {
          atEOF = true; // there is no more JSON object in the bufferedReader
        }
      } else {
        LOG.error("Error: invalid JsonNode type");
        throw new NoSuchElementException("Invalid JsonNode type");
      }
    }
  
    protected CommonCrawl100Collection.Document createNewDocument(JsonNode node) {
      return new CommonCrawl100Collection.Document(node);
    }
    
  }
  
  /**
   * A document in a language corpus for CC-100.
   */


  public static class Document implements SourceDocument{
    private String id;
    private String raw;
    private String contents;
  
  
    public Document(JsonNode json) {
      this.raw = json.toPrettyString();
    
      json.fields().forEachRemaining( e -> {
        if ("id".equals(e.getKey())) {
          this.id = json.get("id").asText();
        } else if ("contents".equals(e.getKey())) {
          this.contents = json.get("contents").asText();
        }
      });
    }
  
    @Override
    public String id() {
      return id;
    }
  
    @Override
    public String contents() {
      return contents;
    }
  
    @Override
    public String raw() {
      return raw;
    }
  
    @Override
    public boolean indexable() {
      return true;
    }
  }
  
}
