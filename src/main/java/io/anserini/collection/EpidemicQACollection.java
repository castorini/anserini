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
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * A document collection for the CORD-19 dataset provided by Semantic Scholar.
 */
public class EpidemicQACollection extends DocumentCollection<EpidemicQACollection.Document> {
  private static final Logger LOG = LogManager.getLogger(EpidemicQACollection.class);

  public EpidemicQACollection(Path path){
    this.path = path;
    // TODO ignore MD5.txt
    this.allowedFilePrefix = Set.of("info");
    this.allowedFileSuffix = Set.of(".txt");
  }

  @Override
  public FileSegment<EpidemicQACollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing a single txt document.
   */
  public class Segment extends FileSegment<EpidemicQACollection.Document> {
    public Segment(Path path) throws IOException {
      super(path);
      this.bufferedReader = new BufferedReader(new InputStreamReader(
          new FileInputStream(path.toString())));
      LOG.info("Path: " + path.toString());
    }

    @Override
    public void readNext() throws NoSuchElementException {
      String nextDocumentId;

      try {
        nextDocumentId = bufferedReader.readLine();
      } catch (IOException e) {
        LOG.error(e);
        throw new NoSuchElementException();
      }
      if (nextDocumentId == null) {
        throw new NoSuchElementException();
      }
      bufferedRecord = new EpidemicQACollection.Document(nextDocumentId);
    }

    @Override
    public void close() {
      super.close();
    }
  }

  /**
   * TODO(justinborromeo) Do documentation
   */
  public class Document implements SourceDocument {
    // The 8-char truncated sha id of the document from info.txt.
    private final String documentID;
    private final String rawDocument;

    // Details from the parsed document JSON.
    private String content;
    // The 40-char full SHA id of the document.
    private String sha;
    private String title;
    private String url;
    private List<String> authors;

    public Document(String documentId) {
      documentID = documentId;
      // Document JSON
      rawDocument = getEpidemicQAJson(EpidemicQACollection.this.path.toString());
      content = "";
      if (rawDocument != null) {
        // For the contents(), we're going to gather up all the text in body_text
        try {
          ObjectMapper mapper = new ObjectMapper();
          JsonNode recordJsonNode = mapper.readerFor(JsonNode.class).readTree(rawDocument);
          JsonNode metadataJsonNode = recordJsonNode.get("metadata");
          if (metadataJsonNode == null) {
            LOG.warn("Null metadata");
          }

          sha = recordJsonNode.get("document_id").asText();
          title = metadataJsonNode.get("title").asText();
          Iterator<JsonNode> urlIterator = metadataJsonNode.get("urls").elements();
          url = urlIterator.hasNext() ? urlIterator.next().asText() : "";

          Iterator<JsonNode> authorsIterator = metadataJsonNode.get("authors").elements();
          authors = new ArrayList<>();
          while (authorsIterator.hasNext()) {
            authors.add(authorsIterator.next().asText());
          }

          // Contexts in this correspond to paragraphs or sections as indicated by the HTML
          // markup of the document.
          Iterator<JsonNode> contextIterator = recordJsonNode.get("contexts").elements();

          while (contextIterator.hasNext()) {
            JsonNode node = contextIterator.next();
            content += "\n" + node.get("text").asText();
          }
        } catch (IOException e) {
          LOG.error("Error parsing file at " + EpidemicQACollection.this.path.toString() + "/" + this.documentID
                    + "\n" + e.getMessage());
        }
      }
    }

    @Override
    public String id() {
      return documentID;
    }

    @Override
    public String contents() {
      return content;
    }

    @Override
    public String raw() {
      return rawDocument;
    }

    public String sha() {
      return sha;
    }

    public String title() {
      return title;
    }

    public String url() {
      return url;
    }

    public List<String> authors() {
      return authors;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    private final String getEpidemicQAJson(String basePath) {
      if (this.documentID.isEmpty()) {
        return null;
      }

      String documentPath = "/" + this.documentID + ".json";
      String documentJson = null;
      try {
        documentJson = new String(Files.readAllBytes(
            Paths.get(basePath + documentPath)));
      } catch (IOException e) {
        LOG.error(e.getMessage());
        LOG.error("Error parsing file at " + (basePath+documentPath));
      }

      if (documentJson == null || documentJson.isEmpty()) {
        return null;
      }

      return documentJson;
    }
  }
}
