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
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A document collection for the Trialstreamer dataset modelled after CORD-19.
 */
public class TrialstreamerCollection extends DocumentCollection<TrialstreamerCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(TrialstreamerCollection.class);

  public TrialstreamerCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".csv");
  }

  public TrialstreamerCollection() {
  }

  @Override
  public FileSegment<TrialstreamerCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<TrialstreamerCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment(bufferedReader);
  }

  /**
   * A file containing a single CSV document.
   */
  public class Segment extends FileSegment<TrialstreamerCollection.Document> {
    CSVParser csvParser = null;
    private CSVRecord record = null;
    private Iterator<CSVRecord> iterator = null; // iterator for CSV records
    private String stringRecord = null;

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
      stringRecord = bufferedReader.lines().collect(Collectors.joining());
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (record == null && stringRecord == null) {
        throw new NoSuchElementException("Record is empty");
      } else {
        if (record != null) {
          bufferedRecord = new TrialstreamerCollection.Document(record);
          if (iterator.hasNext()) { // if CSV contains more lines, we parse the next record
            record = iterator.next();
          } else {
            atEOF = true; // there is no more JSON object in the bufferedReader
          }
        } else {
          bufferedRecord = new TrialstreamerCollection.Document(stringRecord);
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
    private JsonNode facets;

    public Document(CSVRecord record) {
      id = record.get("cord_uid");
      content = record.get("title").replace("\n", " ");
      content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");
      this.record = record;

      String fullTextJson = getFullTextJson(TrialstreamerCollection.this.path.toString());
      if (fullTextJson != null) {
        raw = fullTextJson;
        StringReader fullTextReader = new StringReader(fullTextJson);
        ObjectMapper mapper = new ObjectMapper();
        try {
          JsonNode recordJsonNode = mapper.readerFor(JsonNode.class).readTree(fullTextReader);
          facets = recordJsonNode.get("facets");
        } catch (IOException e) {
          LOG.error("Could not read JSON string");
        }
      } else {
        String recordJson = getRecordJson();
        raw = recordJson == null ? "" : recordJson;
      }
    }

    public Document(String fullTextJson) {
      if (fullTextJson != null) {
        raw = fullTextJson;
        StringReader fullTextReader = new StringReader(fullTextJson);
        ObjectMapper mapper = new ObjectMapper();
        try {
          JsonNode recordJsonNode = mapper.readerFor(JsonNode.class).readTree(fullTextReader);
          facets = recordJsonNode.get("facets");
        } catch (IOException e) {
          LOG.error("Could not read JSON string");
        }
      } else {
        String recordJson = getRecordJson();
        raw = recordJson == null ? "" : recordJson;
      }
    }

    public JsonNode facets() {
      return facets;
    }
  }
}
