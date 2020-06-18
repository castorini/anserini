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
public class Cord19ParagraphCollection extends DocumentCollection<Cord19ParagraphCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(Cord19ParagraphCollection.class);

  private static final boolean DUPLICATE_ABSTRACT = true;
  // With paragraph indexing, early on in TREC-COVID, we had a question about exactly how to decompose full text into
  // paragraphs. The initial implementation was:
  //
  //   + docid: title + abstract
  //   + docid.00001: title + abstract + 1st paragraph
  //   + docid.00002: title + abstract + 2nd paragraph
  //   + docid.00003: title + abstract + 3rd paragraph
  //   + ...
  //
  // But an equally reasonable alternative would be *not* to repeat the abstract, which is actually the setup in [1]:
  //
  //   + docid: title + abstract
  //   + docid.00001: title + 1st paragraph
  //   + docid.00002: title + 2nd paragraph
  //   + docid.00003: title + 3rd paragraph
  //   + ...
  //
  // With TREC-COVID rounds 1+2 data, we can empirically confirm that the first method is more effective, which is
  // implemented by DUPLICATE_ABSTRACT = true above. However, since this remains an interesting question that should
  // be revisited from time to time, we're leaving a flag to switch between the different indexing modes easily.
  //
  // [1] Lin. Is Searching Full Text More Effective Than Searching Abstracts? BMC Bioinformatics, 10:46, 2009.

  public Cord19ParagraphCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = Set.of(".csv");
  }

  @Override
  public FileSegment<Cord19ParagraphCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing a single CSV document.
   */
  public class Segment extends FileSegment<Cord19ParagraphCollection.Document> {
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
        bufferedRecord = new Cord19ParagraphCollection.Document(record, paragraph, paragraphNumber);
      } else if (iterator.hasNext()) {
        // if CSV contains more lines, we parse the next record
        record = iterator.next();
        String recordFullText = "";

        // get paragraphs from full text file
        String fullTextPath = null;
        if (!record.get("pmc_json_files").isEmpty()) {
          fullTextPath = "/" + record.get("pmc_json_files").split(";")[0];
        } else if (!record.get("pdf_json_files").isEmpty()) {
          fullTextPath = "/" + record.get("pdf_json_files").split(";")[0];
        }

        if (fullTextPath != null){
          try {
            String recordFullTextPath = Cord19ParagraphCollection.this.path.toString() + fullTextPath;
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
        bufferedRecord = new Cord19ParagraphCollection.Document(record, recordFullText);
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
  public class Document extends Cord19BaseDocument {
    public Document(CSVRecord record, String paragraph, Integer paragraphNumber, String recordFullText) {
      this.record = record;

      if (paragraphNumber == 0) {
        id = record.get("cord_uid");
      } else {
        id = record.get("cord_uid") + "." + String.format("%05d", paragraphNumber);
      }

      if (DUPLICATE_ABSTRACT) {
        content = record.get("title").replace("\n", " ");
        content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");
        content += paragraph.isEmpty() ? "" : "\n" + paragraph;
      } else {
        if (paragraphNumber == 0) {
          content = record.get("title").replace("\n", " ");
          content += record.get("abstract").isEmpty() ? "" : "\n" + record.get("abstract");
        } else {
          content = record.get("title").replace("\n", " ");
          content += paragraph.isEmpty() ? "" : "\n" + paragraph;
        }
      }

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
