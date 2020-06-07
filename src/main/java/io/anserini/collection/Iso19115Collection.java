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

public class Iso19115Collection extends DocumentCollection<Iso19115Collection.Document>{
  public Iso19115Collection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".jsonl"));
  }

  @Override
  public FileSegment<Iso19115Collection.Document> createFileSegment(Path p) throws IOException{
    return new Segment(p);
  }

  public static class Segment extends FileSegment<Iso19115Collection.Document> {
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
        bufferedRecord = new Iso19115Collection.Document(node);
        if(iterator.hasNext()) {
          node = iterator.next();
        } else {
          atEOF = true;
        }
      } else if (node.isArray()) {
        if (iter != null && iter.hasNext()) {
          JsonNode json = iter.next();
          bufferedRecord = new Iso19115Collection.Document(node);
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
    protected String abstractContent;

    public Document(JsonNode json) {
      // extracting the fields from the ISO19115 file
      String identifier = json.get("gmd:MD_Metadata").get("gmd:fileIdentifier").get("gco:CharacterString").asText();
      this.id = identifier.substring(0,identifier.length() - 8);
      this.title = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification").get("gmd:citation")
                   .get("gmd:CI_Citation").get("gmd:title").get("gco:CharacterString").asText();
      this.abstractContent = json.get("gmd:MD_Metadata").get("gmd:identificationInfo").get("gmd:MD_DataIdentification")
                             .get("gmd:abstract").get("gco:CharacterString").asText();
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return abstractContent;
    }

    @Override
    public String raw() {
      return title + "\n" + abstractContent;
    }

    public String getTitle() {
      return title;
    }

    public String getAbstract() {
      return abstractContent;
    }

    @Override
    public boolean indexable() {
      return true;
    }

  }
}
