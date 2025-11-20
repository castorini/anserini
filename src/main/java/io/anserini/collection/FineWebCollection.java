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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A FineWeb document collection stored in Parquet format.
 * This class reads all <code>.parquet</code> files in the input directory.
 * Each Parquet file contains multiple documents with fields such as:
 * <ul>
 *   <li><code>id</code>: unique document identifier (required)</li>
 *   <li><code>text</code> or <code>contents</code>: document text content (required)</li>
 *   <li>Additional metadata fields: url, domain, etc. (optional)</li>
 * </ul>
 */
public class FineWebCollection extends DocumentCollection<FineWebCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(FineWebCollection.class);
  
  protected String idField = "id";
  protected String contentsField = null; // Will auto-detect "text" or "contents"

  public FineWebCollection() {
  }

  public FineWebCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".parquet"));
  }

  public FineWebCollection(Path path, String idField, String contentsField) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".parquet"));
    this.idField = idField;
    this.contentsField = contentsField;
  }

  @Override
  public FileSegment<FineWebCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p, idField, contentsField);
  }

  @Override
  public FileSegment<FineWebCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    throw new UnsupportedOperationException("BufferedReader is not supported for FineWebCollection. Use Path-based constructor.");
  }

  /**
   * A file in a FineWeb collection, typically containing multiple documents.
   */
  public static class Segment extends FileSegment<FineWebCollection.Document> {
    private ParquetReader<Group> reader;
    private boolean readerInitialized;
    private String idField;
    private String contentsField;

    public Segment(Path path) throws IOException {
      this(path, "id", null);
    }

    public Segment(Path path, String idField, String contentsField) throws IOException {
      super(path);
      this.idField = idField;
      this.contentsField = contentsField;
      initializeParquetReader(path);
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      throw new IOException("BufferedReader constructor not supported for Parquet files");
    }

    /**
     * Initializes the Parquet reader.
     *
     * @param path the path to the Parquet file.
     * @throws IOException if an I/O error occurs during file reading.
     */
    private void initializeParquetReader(Path path) throws IOException {
      org.apache.hadoop.fs.Path hadoopPath = new org.apache.hadoop.fs.Path(path.toString());
      reader = ParquetReader.builder(new GroupReadSupport(), hadoopPath).build();
      readerInitialized = true;
    }

    @Override
    public void readNext() throws NoSuchElementException {
      if (atEOF || !readerInitialized) {
        throw new NoSuchElementException("End of file reached");
      }

      try {
        Group record = reader.read();
        if (record == null) {
          atEOF = true;
          if (reader != null) {
            reader.close();
          }
          readerInitialized = false;
          throw new NoSuchElementException("End of file reached");
        }

        bufferedRecord = createNewDocument(record);
      } catch (IOException e) {
        LOG.error("Error reading Parquet record", e);
        throw new NoSuchElementException("Error reading Parquet record: " + e.getMessage());
      }
    }

    protected Document createNewDocument(Group record) {
      return new Document(record, idField, contentsField);
    }
  }

  /**
   * A document in a FineWeb collection.
   */
  public static class Document extends MultifieldSourceDocument {
    private String id;
    private String contents;
    private String raw;
    private Map<String, String> fields;

    public Document(Group record, String idField, String contentsField) {
      this.fields = new HashMap<>();
      StringBuilder rawBuilder = new StringBuilder("{");
      boolean firstField = true;

      // Extract ID field - try the specified field first, then common alternatives
      String[] idFieldCandidates = new String[]{idField, "docid", "doc_id", "document_id"};
      for (String field : idFieldCandidates) {
        try {
          this.id = record.getString(field, 0);
          if (!firstField) {
            rawBuilder.append(",");
          }
          rawBuilder.append("\"").append(field).append("\":\"").append(escapeJson(id)).append("\"");
          firstField = false;
          break;
        } catch (RuntimeException ignored) {
          // Field doesn't exist or is wrong type, try next
        }
      }

      // Extract contents field - try "text" first, then "contents", or use provided field
      String[] contentsFieldCandidates = contentsField != null 
          ? new String[]{contentsField}
          : new String[]{"text", "contents", "content", "body"};
      
      for (String field : contentsFieldCandidates) {
        try {
          this.contents = record.getString(field, 0);
          if (!firstField) {
            rawBuilder.append(",");
          }
          rawBuilder.append("\"").append(field).append("\":\"").append(escapeJson(contents)).append("\"");
          firstField = false;
          break;
        } catch (RuntimeException ignored) {
          // Try next field
        }
      }

      // Extract all other fields as metadata
      try {
        int fieldCount = record.getType().getFieldCount();
        HashSet<String> processedFields = new HashSet<>();
        if (id != null) {
          processedFields.add(idField);
        }
        processedFields.addAll(Arrays.asList("text", "contents", "content", "body"));
        if (contentsField != null) {
          processedFields.add(contentsField);
        }
        
        for (int i = 0; i < fieldCount; i++) {
          String fieldName = record.getType().getFieldName(i);
          
          // Skip already processed fields
          if (processedFields.contains(fieldName)) {
            continue;
          }

          try {
            // Try to extract as string
            String value = record.getString(fieldName, 0);
            this.fields.put(fieldName, value);
            if (!firstField) {
              rawBuilder.append(",");
            }
            rawBuilder.append("\"").append(fieldName).append("\":\"").append(escapeJson(value)).append("\"");
            firstField = false;
          } catch (RuntimeException e) {
            // Field might not be a string type, skip it
            LOG.debug("Skipping non-string field: " + fieldName);
          }
        }
      } catch (Exception e) {
        LOG.debug("Error extracting additional fields: " + e.getMessage());
      }

      rawBuilder.append("}");
      this.raw = rawBuilder.toString();
    }

    /**
     * Escape JSON string for raw output.
     */
    private String escapeJson(String str) {
      if (str == null) {
        return "";
      }
      return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    @Override
    public String id() {
      if (id == null) {
        throw new RuntimeException("FineWeb document has no \"id\" field!");
      }
      return id;
    }

    @Override
    public String contents() {
      if (contents == null) {
        throw new RuntimeException("FineWeb document has no \"contents\" or \"text\" field!");
      }
      return contents;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    @Override
    public Map<String, String> fields() {
      return fields;
    }
  }
}

