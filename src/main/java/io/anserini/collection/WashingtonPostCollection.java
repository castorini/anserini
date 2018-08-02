/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.anserini.util.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * An instance of the <a href="https://trec.nist.gov/data/wapost/">TREC Washington Post Corpus</a>.
 * The collection contains 608,180 news articles and blog posts from January 2012 through August 2017,
 * stored in JSON format. The collection is 1.5GB compressed, 5.9GB uncompressed.
 */
public class WashingtonPostCollection extends DocumentCollection
    implements SegmentProvider<WashingtonPostCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(WashingtonPostCollection.class);

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".txt"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
        allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  public class FileSegment extends BaseFileSegment<Document> {
    private String fileName;

    public FileSegment(Path path) throws IOException {
      this.path = path;
      this.fileName = path.toString();
      this.bufferedReader = new BufferedReader(new FileReader(fileName));
    }

    @Override
    public boolean hasNext() {
      if (bufferedRecord != null) {
        return true;
      } else if (atEOF) {
        return false;
      }

      String nextRecord = null;
      try {
         nextRecord = bufferedReader.readLine();
      } catch (IOException e) {
        throw new RuntimeException("File IOException: " + e.getMessage());
      }

      if (nextRecord == null) {
        return false;
      }

      parseRecord(nextRecord);
      return bufferedRecord != null;
    }

    private String removeTags(String content) {
      return content.replaceAll(Document.PATTERN, " ");
    }

    private void parseRecord(String record) {
      StringBuilder builder = new StringBuilder();
      ObjectMapper mapper = new ObjectMapper();
      Document.WashingtonPostObject wapoObj = null;
      try {
        wapoObj = mapper
          .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
          .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
          .readValue(record, Document.WashingtonPostObject.class);
      } catch (IOException e) {
        // For current dataset, we can make sure all record has unique id and
        // published date. So we just simply throw an RuntimeException
        // here in case future data may bring up this issue
        throw new RuntimeException(e);
      }

      bufferedRecord = new WashingtonPostCollection.Document();
      bufferedRecord.id = wapoObj.getId();
      bufferedRecord.publishedDate = wapoObj.getPublishedDate();

      if (JsonParser.isFieldAvailable(wapoObj.getContents())) {
        for (Document.WashingtonPostObject.Content contentObj : wapoObj.getContents().get()) {
          if (JsonParser.isFieldAvailable(contentObj.getType()) && JsonParser.isFieldAvailable(contentObj.getContent())) {
            if (Document.CONTENT_TYPE_TAG.contains(contentObj.getType().get())) {
              builder.append(removeTags(contentObj.getContent().get().trim())).append("\n");
            }
          } else {
            LOG.warn("No type or content tag defined in Article " + bufferedRecord.id + ", ignored this file.");
          }
        }
      }

      bufferedRecord.content = builder.toString();
    }
  }

  /**
   * A document from the <a href="https://trec.nist.gov/data/wapost/">TREC Washington Post Corpus</a>.
   */
  public static class Document implements SourceDocument {
    private static final Logger LOG = LogManager.getLogger(Document.class);
    private static final String PATTERN = "<\\/?\\w+>";
    private static final List<String> CONTENT_TYPE_TAG = Arrays.asList("sanitized_html", "tweet");

    // Required fields
    protected String id;
    protected long publishedDate;
    protected String content;

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

    public long getPublishedDate() {
      return publishedDate;
    }

    /**
     * Used internally by Jackson for JSON parsing.
     */
    public static class WashingtonPostObject {
      // Required fields
      protected String id;
      protected long publishedDate;
      protected String content;

      // Optional fields
      protected Optional<List<Content>> contents;

      /**
       * Used internally by Jackson for JSON parsing.
       */
      public static class Content {
        protected Optional<String> type;
        protected Optional<String> content;

        @JsonGetter("type")
        public Optional<String> getType() {
          return type;
        }

        @JsonGetter("content")
        public Optional<String> getContent() {
          return content;
        }
      }

      @JsonGetter("id")
      public String getId() {
        return id;
      }

      @JsonGetter("published_date")
      public long getPublishedDate() {
        return publishedDate;
      }

      @JsonGetter("contents")
      public Optional<List<Content>> getContents() { return contents; }

      @JsonCreator
      public WashingtonPostObject(
              @JsonProperty(value = "id", required = true) String id,
              @JsonProperty(value = "published_date", required = true) long publishedDate) {
        this.id = id;
        this.publishedDate = publishedDate;
      }
    }
  }
}
