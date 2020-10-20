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
 * A document collection for the Epidemic QA dataset (https://bionlp.nlm.nih.gov/epic_qa/).
 */
public class EpidemicQACollection extends DocumentCollection<EpidemicQACollection.Document> {
  private static final Logger LOG = LogManager.getLogger(EpidemicQACollection.class);

  public EpidemicQACollection(Path path){
    this.path = path;
    // The prefix constraint is required otherwise the md5.txt file will be read as a segment.
    this.allowedFilePrefix = Set.of("info");
    this.allowedFileSuffix = Set.of(".txt");
  }

  @Override
  public FileSegment<EpidemicQACollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A single txt document listing documents. In the case of Epidemic QA, this is the info.txt file.
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
   * A class that maps to one of the Epidemic QA JSON documents.
   */
  public class Document implements SourceDocument {
    // The 8-char truncated sha id of the document from info.txt.
    private final String documentID;
    // The document's raw JSON.
    private final String rawDocument;

    // Details from the parsed document JSON.

    // A concatenation of the title and all the sentences in the document.
    private String content;
    // The title of the document (from the metadata).
    private String title;
    // The first URL in the document's semi-colon separated list of URLs.
    private String url;
    // A semi-colon separated string of authors.
    private String authors;

    public Document(String documentId) {
      documentID = documentId;
      // Document JSON
      rawDocument = getEpidemicQAJson(EpidemicQACollection.this.path.toString());
      content = "";
      authors = "";
      url = "";
      if (rawDocument != null) {
        // For the contents(), we're going to gather up all the text in body_text
        try {
          ObjectMapper mapper = new ObjectMapper();
          JsonNode recordJsonNode = mapper.readerFor(JsonNode.class).readTree(rawDocument);
          JsonNode metadataJsonNode = recordJsonNode.get("metadata");
          if (metadataJsonNode == null) {
            LOG.warn("Null metadata");
          } else {
            title = metadataJsonNode.get("title").asText();
            // Some of the data has multiple urls with the JSON property "urls".  For some reason, this isn't documented
            // in the schema.  If there are multiple, we pick the first url.
            if (metadataJsonNode.get("urls") != null) {
              Iterator<JsonNode> urlIterator = metadataJsonNode.get("urls").elements();
              url = urlIterator.hasNext() ? urlIterator.next().asText() : "";
            } else if (metadataJsonNode.get("url") != null) {
              url = metadataJsonNode.get("url").asText();
            }

            // The authors node won't be present for the consumer dataset.
            if (metadataJsonNode.get("authors") != null) {
              authors = metadataJsonNode.get("authors").asText();
            }
          }
          // Contexts in this correspond to paragraphs or sections as indicated by the HTML
          // markup of the document.
          Iterator<JsonNode> contextIterator = recordJsonNode.get("contexts").elements();
          content += title + "\n";
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

    public String title() {
      return title;
    }

    public String url() {
      return url;
    }

    public String authors() {
      return authors;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    /**
     * Read the corresponding JSON document file for the Document's documentId.
     */
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
