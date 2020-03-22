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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A document collection for the CORD-19 dataset provided by Semantic Scholar.
 */
public class CovidFullTextCollection extends DocumentCollection<CovidFullTextCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(CovidFullTextCollection.class);

  public CovidFullTextCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = Set.of(".csv");
  }

  @Override
  public FileSegment<CovidFullTextCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing a single CSV document.
   */
  public class Segment extends FileSegment<CovidFullTextCollection.Document> {
    CSVParser csvParser = null;
    private CSVRecord record = null;
    private Iterator<CSVRecord> iterator = null; // iterator for CSV records

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

    @Override
    public void readNext() throws NoSuchElementException {
      if (record == null) {
        throw new NoSuchElementException("Record is empty");
      } else {
        bufferedRecord = new CovidFullTextCollection.Document(record);
        if (iterator.hasNext()) { // if CSV contains more lines, we parse the next record
          record = iterator.next();
        } else {
          atEOF = true; // there is no more JSON object in the bufferedReader
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
  public class Document implements SourceDocument {
    private String id;
    private String content;
    private String raw;
    private CSVRecord record;

    public Document(CSVRecord record) {
      id = Long.toString(record.getRecordNumber());
      content = record.get("title").replace("\n", " ");
      content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");
      this.record = record;

      String fullTextJson = "";
      // index full text into raw content
      if (record.get("has_full_text").contains("True")) {
        String[] hashes = record.get("sha").split(";");
        String fullTextPath = "/" + record.get("full_text_file") + "/" + hashes[hashes.length - 1].strip() + ".json";
        try {
          fullTextJson = new String(Files.readAllBytes(
            Paths.get(CovidFullTextCollection.this.path.toString() + fullTextPath)));
        } catch (IOException e) {
          LOG.error("Error parsing file at " + fullTextPath);
          raw = record.toString();
        }
      }

      if (!fullTextJson.isEmpty()) {
        content += fullTextJson.isEmpty() ? "" : "\n " + fullTextJson;
        raw = fullTextJson;
      } else {
        raw = record.toString();
      }      
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
      return content;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public String raw() {
      return raw;
    }

    public CSVRecord record() {
      return record;
    }
  }
}
