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
public class EpidemicQACollection extends DocumentCollection<EpidemicQACollection.Document> {
  private static final Logger LOG = LogManager.getLogger(EpidemicQACollection.class);

  public EpidemicQACollection(Path path){
    this.path = path;
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
    // The name of the document from info.txt.
    private final String documentID;
    private final String document;

    public Document(String documentId) {
      this.documentID = documentId;
      this.document = getEpidemicQAJson(EpidemicQACollection.this.path.toString());
    }

    @Override
    public String id() {
      return documentID;
    }

    @Override
    public String contents() {
      return document;
    }

    @Override
    public String raw() {
      return document;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    private final String getEpidemicQAJson(String basePath) {
      if (this.documentID.isEmpty()) {
        return null;
      }

      String documentPath = "/" + this.documentID;
      String documentJson = null;
      try {
        documentJson = new String(Files.readAllBytes(
            Paths.get(basePath + documentPath)));
      } catch (IOException e) {
        LOG.error("Error parsing file at " + documentPath);
      }
      return documentJson;
    }
  }
}
