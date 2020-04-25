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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
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
public class CovidParagraphCollection extends DocumentCollection<CovidParagraphCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(CovidParagraphCollection.class);

  public CovidParagraphCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = Set.of(".csv");
  }

  @Override
  public FileSegment<CovidParagraphCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing a single CSV document.
   */
  public class Segment extends FileSegment<CovidParagraphCollection.Document> {
    CSVParser csvParser = null;
    private CSVRecord record = null;
    private Iterator<CSVRecord> iterator = null; // iterator for CSV records
    private Iterator<JsonNode> paragraphIterator = null; // iterator for paragraphs in a CSV record
    private Integer paragraphNumber = 0;

    public Segment(Path path) throws IOException {
      super(path);
      bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(path.toString())));

      csvParser = new CSVParser(bufferedReader, CSVFormat.DEFAULT
        .withFirstRecordAsHeader()
        .withIgnoreHeaderCase()
        .withTrim());

      iterator = csvParser.iterator();
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (paragraphIterator != null && paragraphIterator.hasNext()) {
        // if the record contains more paragraphs, we parse them
        String paragraph = paragraphIterator.next().get("text").asText();
        paragraphNumber += 1;
        bufferedRecord = new CovidParagraphCollection.Document(record, paragraph, paragraphNumber);
      } else if (iterator.hasNext()) {
        // if CSV contains more lines, we parse the next record
        record = iterator.next();
        String recordFullText = "";

        // get paragraphs from full text file
        String fullTextPath = null;
        if (record.get("has_pmc_xml_parse").contains("True")) {
          fullTextPath = "/" + record.get("full_text_file") + "/pmc_json/" +
          record.get("pmcid") + ".xml.json";
        } else if (record.get("has_pdf_parse").contains("True")) {
          String[] hashes = record.get("sha").split(";");
          fullTextPath = "/" + record.get("full_text_file") + "/pdf_json/" +
            hashes[hashes.length - 1].strip() + ".json";
        }

        if (fullTextPath != null){
          try {
            String recordFullTextPath = CovidParagraphCollection.this.path.toString() + fullTextPath;
            recordFullText = new String(Files.readAllBytes(Paths.get(recordFullTextPath)));
            FileReader recordFullTextFileReader = new FileReader(recordFullTextPath);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode recordJsonNode = mapper.readerFor(JsonNode.class).readTree(recordFullTextFileReader);
            paragraphIterator = recordJsonNode.get("body_text").elements();
          } catch (IOException e) {
            LOG.error("Error parsing file at " + fullTextPath + "\n" + e.getMessage());
          }
        } else {
          paragraphIterator = null;
        }

        paragraphNumber = 0;
        bufferedRecord = new CovidParagraphCollection.Document(record, recordFullText);
    } else {
      throw new NoSuchElementException("Reached end of CSVRecord Entries Iterator");
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
  public class Document extends CovidCollectionDocument {
    public Document(CSVRecord record, String paragraph, Integer paragraphNumber, String recordFullText) {
      this.record = record;

      if (paragraphNumber == 0) {
        id = record.get("cord_uid");
      } else {
        id = record.get("cord_uid") + "." + String.format("%05d", paragraphNumber);
      }

      content = record.get("title").replace("\n", " ");
      content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");
      content += paragraph.isEmpty() ? "" : "\n" + paragraph;

      raw = buildRawJson(recordFullText);
    }

    public Document(CSVRecord record, String paragraph, Integer paragraphNumber) {
      this(record, paragraph, paragraphNumber, "");
    }

    public Document(CSVRecord record, String recordFullText) {
      this(record, "", 0, recordFullText);
    }
  }
}
