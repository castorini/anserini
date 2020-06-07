/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class IsoCollection extends DocumentCollection<IsoCollection.Document>{
  public IsoCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl"));
  }

  @Override
  public FileSegment<IsoCollection.Document> createFileSegment(Path p) throws IOException{
    return new Segment(p);
  }

  public static class Segment extends FileSegment<IsoCollection.Document> {
    private JsonNode node = null;
    private Iterator<JsonNode> iter = null;
    private MappingIterator<JsonNode> iterator;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if(iterator.hasNext()){
        node = iterator.next();
        if(node.isArray()) {
          iter = node.elements();
        }
      }
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (node == null){
        throw new NoSuchElementException("JsonNode is empty");
      } else if (node.isObject()) {
        bufferedRecord = new IsoCollection.Document(node);
        if(iterator.hasNext()) {
          node = iterator.next();
        } else {
          atEOF = true;
        }
      } else if (node.isArray()) {
        if (iter != null && iter.hasNext()) {
          JsonNode json = iter.next();
          bufferedRecord = new IsoCollection.Document(node);
        } else {
          throw new NoSuchElementException("Reached end of JsonNode iterator");
        }
      } else {
        throw new NoSuchElementException("Invalid JsonNode type");
      } 
    }
  }

  public static class Document implements SourceDocument{
    protected String id;
    protected String title;
    protected String abstract_;

    public Document(JsonNode json) {
      json.fields().forEachRemaining( e -> {
        if ("id".equals(e.getKey())) {
          this.id = json.get("id").asText();
        } else if ("title".equals(e.getKey())) {
          this.title = json.get("title").asText();
        } else if ("abstract".equals(e.getKey())) {
          this.abstract_ = json.get("abstract").asText();
        } else {
          throw new RuntimeException("JSON document contains illegal fields");
        }
      });
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return abstract_;
    }

    @Override
    public String raw() {
      return title + "\n" + abstract_;
    }

    public String getTitle() {
      return title;
    }

    public String getAbstract() {
      return abstract_;
    }

    @Override
    public boolean indexable() {
      return true;
    }

  }
}
