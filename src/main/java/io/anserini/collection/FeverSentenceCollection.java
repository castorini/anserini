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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

public class FeverSentenceCollection extends DocumentCollection<FeverSentenceCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(FeverSentenceCollection.class);

  public FeverSentenceCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".jsonl");
  }

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file in a FEVER collection, containing a document in JSON format on
   * each line. For this sentence collection, we want to split each document
   * onto a sentence-level and treat each sentence as a document.
   */
  public class Segment extends FileSegment<FeverSentenceCollection.Document> {
    private JsonNode node = null;
    private Iterator<JsonNode> iterator = null;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      ObjectMapper mapper = new ObjectMapper();

      // read in lines as a stream, convert to json, flatten to sentences, convert back into json
      iterator = bufferedReader.lines()
              .map(content -> {
                try {
                  return mapper.readTree(content);
                } catch (JsonProcessingException e) {
                  LOG.error("Error processing JSON in file " + path.toString());
                  return null;
                }
              })
              .filter(Objects::nonNull)
              .flatMap(this::flattenToSentences)
              .iterator();

      if (iterator.hasNext()) {
        node = iterator.next();
      }
    }

    @Override
    protected void readNext() throws NoSuchElementException {
      bufferedRecord = new FeverSentenceCollection.Document(node);
      if (iterator.hasNext()) { // if JSONL contains more lines, we parse the next record
        node = iterator.next();
      } else { // if there is no more JSON object in the bufferedReader
        atEOF = true;
      }
    }

    /**
     * Extracts the sentences out of the "lines" field in the FEVER JSONL files.
     */
    protected Stream<JsonNode> flattenToSentences(JsonNode json) {
      ObjectMapper mapper = new ObjectMapper();
      List<JsonNode> sentenceNodes = new ArrayList<>();

      String id = json.get("id").asText();
      String lines = json.get("lines").asText();

      for (String line: lines.split("\n")) {
        // line is of the format: (sentence id)\t(sentence)[\t(tag)\t...\t(tag)]
        String[] tokens = line.split("\t");
        String sentence = tokens[1];
        if (!sentence.isEmpty()) {
          String jsonNodeStr = String.format("{\"id\": \"%s_%s\", \"text\": \"%s\", \"lines\": \"%s\"}", id, tokens[0],
                  sentence, sentence);
          JsonNode jsonNode;
          try {
            jsonNode = mapper.readTree(jsonNodeStr);
          } catch (JsonProcessingException e) {
            // should never reach this point
            LOG.error("Error processing JSON");
            continue;
          }
          sentenceNodes.add(jsonNode);
        }
      }

      return sentenceNodes.stream();
    }
  }

  /**
   * A document in a FEVER collection.
   */
  public class Document extends FeverBaseDocument {
    public Document(JsonNode json) {
      id = json.get("id").asText();
      content = json.get("text").asText();
      raw = json.get("lines").asText();
    }
  }
}
