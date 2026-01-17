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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A SmolTalk document collection stored in Parquet format.
 * <p>
 * This collection is designed to index the HuggingFace SmolTalk dataset:
 * <a href="https://huggingface.co/datasets/HuggingFaceTB/smol-smoltalk">smol-smoltalk</a>
 * <p>
 * Each row in the Parquet file contains a "messages" field with an array of messages.
 * Each message has "content" and "role" fields. This collection extracts each
 * user question + assistant answer pair as a separate document for indexing.
 * <p>
 * Document structure:
 * <ul>
 *   <li>id: auto-generated as {filename}_{row}_{pair_index}</li>
 *   <li>contents: user question + assistant answer concatenated</li>
 *   <li>question: the user's question</li>
 *   <li>answer: the assistant's answer</li>
 *   <li>source: the source field from the parquet row (if present)</li>
 * </ul>
 */
public class SmolTalkCollection extends DocumentCollection<SmolTalkCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(SmolTalkCollection.class);

  public SmolTalkCollection() {
  }

  public SmolTalkCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".parquet"));
  }

  @Override
  public FileSegment<SmolTalkCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  @Override
  public FileSegment<SmolTalkCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    throw new UnsupportedOperationException("BufferedReader is not supported for SmolTalkCollection. Use Path-based constructor.");
  }

  /**
   * A file segment in a SmolTalk collection.
   * Each Parquet row may produce multiple documents (one per user-assistant pair).
   */
  public static class Segment extends FileSegment<SmolTalkCollection.Document> {
    private ParquetReader<Group> reader;
    private boolean readerInitialized;
    private long rowCounter = 0;
    private List<Document> pendingDocuments = new ArrayList<>();
    private int pendingIndex = 0;
    private String segmentName;

    public Segment(Path path) throws IOException {
      super(path);
      this.segmentName = path.getFileName().toString().replace(".parquet", "");
      initializeParquetReader(path);
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      throw new IOException("BufferedReader constructor not supported for Parquet files");
    }

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

      // If we have pending documents from the previous row, return the next one
      if (pendingIndex < pendingDocuments.size()) {
        bufferedRecord = pendingDocuments.get(pendingIndex++);
        return;
      }

      // Read next row and extract all Q&amp;A pairs
      try {
        while (true) {
          Group record = reader.read();
          if (record == null) {
            atEOF = true;
            if (reader != null) {
              reader.close();
            }
            readerInitialized = false;
            throw new NoSuchElementException("End of file reached");
          }

          // Extract documents from this row
          pendingDocuments = extractDocuments(record, rowCounter++);
          pendingIndex = 0;

          if (!pendingDocuments.isEmpty()) {
            bufferedRecord = pendingDocuments.get(pendingIndex++);
            return;
          }
          // If no documents were extracted, continue to next row
        }
      } catch (IOException e) {
        LOG.error("Error reading Parquet record", e);
        throw new NoSuchElementException("Error reading Parquet record: " + e.getMessage());
      }
    }

    /**
     * Extracts user-assistant Q&amp;A pairs from a single Parquet row.
     */
    private List<Document> extractDocuments(Group record, long rowNumber) {
      List<Document> documents = new ArrayList<>();
      
      // Get source field if present
      String source = null;
      try {
        source = record.getString("source", 0);
      } catch (RuntimeException ignored) {
        // source field not present
      }

      // Get messages array
      Group messages;
      try {
        messages = record.getGroup("messages", 0);
      } catch (RuntimeException e) {
        LOG.debug("No 'messages' field found in row " + rowNumber);
        return documents;
      }

      // Parse messages - looking for user/assistant pairs
      int messageCount = messages.getFieldRepetitionCount("list");
      
      String currentUserContent = null;
      int pairIndex = 0;

      for (int i = 0; i < messageCount; i++) {
        try {
          Group listElement = messages.getGroup("list", i);
          Group element = listElement.getGroup("element", 0);
          
          String role = element.getString("role", 0);
          String content = element.getString("content", 0);

          if ("user".equals(role)) {
            currentUserContent = content;
          } else if ("assistant".equals(role) && currentUserContent != null) {
            // Found a user-assistant pair, create a document
            String docId = segmentName + "_" + rowNumber + "_" + pairIndex;
            Document doc = new Document(docId, currentUserContent, content, source);
            documents.add(doc);
            pairIndex++;
            currentUserContent = null; // Reset for next pair
          }
        } catch (RuntimeException e) {
          LOG.debug("Error parsing message at index " + i + " in row " + rowNumber + ": " + e.getMessage());
        }
      }

      return documents;
    }
  }

  /**
   * A document in a SmolTalk collection representing a single user question + assistant answer.
   */
  public static class Document extends MultifieldSourceDocument {
    private final String id;
    private final String contents;
    private final String raw;
    private final Map<String, String> fields;

    public Document(String id, String question, String answer, String source) {
      this.id = id;
      this.fields = new HashMap<>();
      
      // Store question and answer as separate fields
      this.fields.put("question", question);
      this.fields.put("answer", answer);
      if (source != null) {
        this.fields.put("source", source);
      }

      // Contents is the concatenation of question and answer
      this.contents = "Question: " + question + "\n\nAnswer: " + answer;

      // Build raw JSON
      StringBuilder rawBuilder = new StringBuilder();
      rawBuilder.append("{");
      rawBuilder.append("\"id\":\"").append(escapeJson(id)).append("\",");
      rawBuilder.append("\"question\":\"").append(escapeJson(question)).append("\",");
      rawBuilder.append("\"answer\":\"").append(escapeJson(answer)).append("\"");
      if (source != null) {
        rawBuilder.append(",\"source\":\"").append(escapeJson(source)).append("\"");
      }
      rawBuilder.append("}");
      this.raw = rawBuilder.toString();
    }

    private static String escapeJson(String str) {
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
      return id;
    }

    @Override
    public String contents() {
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
    
    /**
     * Returns the user's question.
     */
    public String getQuestion() {
      return fields.get("question");
    }
    
    /**
     * Returns the assistant's answer.
     */
    public String getAnswer() {
      return fields.get("answer");
    }
    
    /**
     * Returns the source of this Q&amp;A pair.
     */
    public String getSource() {
      return fields.get("source");
    }
  }
}

