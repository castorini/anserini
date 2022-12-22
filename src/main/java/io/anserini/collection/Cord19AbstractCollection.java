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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A document collection for the CORD-19 dataset provided by Semantic Scholar.
 */
public class Cord19AbstractCollection extends DocumentCollection<Cord19AbstractCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(Cord19AbstractCollection.class);

  public Cord19AbstractCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".csv");
  }

  public Cord19AbstractCollection() {
  }

  @Override
  public FileSegment<Cord19AbstractCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<Cord19AbstractCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file containing a single CSV document.
   */
  public class Segment extends FileSegment<Cord19AbstractCollection.Document> {
    CSVParser csvParser = null;
    private CSVRecord record = null;
    private Iterator<CSVRecord> iterator = null; // iterator for CSV records
    private JsonNode node = null;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(path.toString())));

      csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT
          .withFirstRecordAsHeader()
          .withIgnoreHeaderCase()
          .withTrim());

      iterator = csvParser.iterator();
      if (iterator.hasNext()) {
        record = iterator.next();
      }
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);

      String jsonString = bufferedReader.lines().collect(Collectors.joining("\n"));

      ObjectMapper mapper = new ObjectMapper();
      node = mapper.readTree(jsonString);
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (record == null && node == null) {
        throw new NoSuchElementException("Record is empty");
      } else {
        if (record != null) {
          bufferedRecord = new Cord19AbstractCollection.Document(record);
          if (iterator.hasNext()) { // if CSV contains more lines, we parse the next record
            record = iterator.next();
          } else {
            atEOF = true; // there is no more JSON object in the bufferedReader
          }
        } else {
          bufferedRecord = new Cord19AbstractCollection.Document(node);
          atEOF = true; // there is no more JSON object in the bufferedReader
          node = null;
        }
      }
    }

    @Override
    public void close() {
      super.close();
      if (csvParser != null) {
        try {
          csvParser.close();
        } catch (IOException e) {
          // do nothing
        }
      }
    }
  }

  /**
   * A document in a CORD-19 collection.
   */
  public class Document extends Cord19BaseDocument {
    public Document(CSVRecord record) {
      this.record = record;

      id = record.get("cord_uid");
      content = record.get("title").replace("\n", " ");
      content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");

      String fullTextJson = getFullTextJson(Cord19AbstractCollection.this.path.toString());
      raw = buildRawJson(fullTextJson);
    }

    public Document(JsonNode jnode) {
      id = jnode.get("csv_metadata").get("cord_uid").asText();
      content = jnode.get("csv_metadata").get("title").asText().replace("\n", " ");
      content += jnode.get("csv_metadata").get("abstract").asText("").equals("") ? "" : "\n" + jnode.get("csv_metadata").get("abstract").asText();
      String fullTextJson = jnode.toPrettyString();
      raw = fullTextJson;
    }
  }
}
