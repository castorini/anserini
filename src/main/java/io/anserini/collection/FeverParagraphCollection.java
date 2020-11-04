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
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

public class FeverParagraphCollection extends DocumentCollection<FeverParagraphCollection.Document> {

  public FeverParagraphCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".jsonl");
  }

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file in a FEVER collection, containing a document in JSON format on
   * each line.
   */
  public class Segment extends FileSegment<FeverParagraphCollection.Document> {
    private JsonNode node = null;
    private Iterator<JsonNode> iterator = null;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new FileReader(path.toString()));
      ObjectMapper mapper = new ObjectMapper();
      iterator = mapper.readerFor(JsonNode.class).readValues(bufferedReader);
      if (iterator.hasNext()) {
        node = iterator.next();
      }
    }

    @Override
    protected void readNext() throws NoSuchElementException {
      bufferedRecord = new FeverParagraphCollection.Document(node);
      if (iterator.hasNext()) { // if JSONL contains more lines, we parse the next record
        node = iterator.next();
      } else { // if there is no more JSON object in the bufferedReader
        atEOF = true;
      }
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
