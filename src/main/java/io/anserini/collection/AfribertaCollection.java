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
import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AfribertaCollection extends DocumentCollection<AfribertaCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(AfribertaCollection.class);

  public AfribertaCollection() {
  }

  public AfribertaCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".zip", ".txt"));
  }

  @SuppressWarnings("unchecked")
  @Override
  public FileSegment<AfribertaCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<AfribertaCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }


  public static class Segment<T extends Document> extends FileSegment<T> {
    private JsonNode node = null;
    private List<JsonNode> jsonNodeArray = null;
    private Iterator<JsonNode> iterator; // iterator for JSON line objects

    public Segment(Path path) throws IOException {
      super(path);
      
      ZipFile zip = new ZipFile(String.valueOf(path));
      for (Enumeration e = zip.entries(); e.hasMoreElements(); ) {
        ZipEntry entry = (ZipEntry) e.nextElement();
        if (!entry.isDirectory()) {
          if (FilenameUtils.getExtension(entry.getName()).equals("txt")) {
            jsonNodeArray = getTxtFiles(zip.getInputStream(entry));
          }
        }
      }

      iterator = jsonNodeArray.iterator();
      if (iterator.hasNext()) {
        node = iterator.next();
      }
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      ObjectMapper objectMapper = new ObjectMapper();
      String jsonString = bufferedReader.lines().collect(Collectors.joining("\n"));
      JsonNode jsonNode = objectMapper.readTree(jsonString);
      jsonNodeArray = new ArrayList<>();
      jsonNodeArray.add(jsonNode);
      iterator = jsonNodeArray.iterator();
      if (iterator.hasNext()) {
        node = iterator.next();
      }
    }

    private List<JsonNode> getTxtFiles(InputStream inputStream) {
      BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
      return getTxtFiles(reader);
    }

    private List<JsonNode> getTxtFiles(BufferedReader reader) {
      List<JsonNode> jsonNodeArray = new ArrayList<>();
      ObjectMapper objectMapper = new ObjectMapper();

      String line;
      try {
        int i = 0;
        while ((line = reader.readLine()) != null) {
          String json = "{ \"id\" : \"doc_" + i + "\", \"contents\" : \"" + line.replaceAll("[-+\"\'^[\\\\p{C}]\\\\]*", "").strip() + "\" }";
          JsonNode jsonNode = objectMapper.readTree(json);
          jsonNodeArray.add(jsonNode);
          i++;
        }
      } catch (IOException e) {
        LOG.error("Error: this is not a text file");
        e.printStackTrace();
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
    
    protected AfribertaCollection.Document createNewDocument(JsonNode node) {
      return new AfribertaCollection.Document(node);
    }
  }
  
  /**
   * A document in a language corpus for AfriBERTa.
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