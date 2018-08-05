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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".txt", ".jl"));

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
        throw new RuntimeException(e);
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

      builder.append(wapoObj.getTitle()).append("\n");

      if (JsonParser.isFieldAvailable(wapoObj.getContents())) {
        for (Document.WashingtonPostObject.Content contentObj : wapoObj.getContents().get()) {
          if (contentObj == null) continue;
          if (JsonParser.isFieldAvailable(contentObj.getType())
                  && JsonParser.isFieldAvailable(contentObj.getContent())
                  && Document.CONTENT_TYPE_TAG.contains(contentObj.getType().get())) {
            String content = contentObj.getContent().get();
            builder.append(removeTags(content)).append("\n");
          }
          if (JsonParser.isFieldAvailable(contentObj.getFullCaption())) {
            String fullCaption = contentObj.getFullCaption().get();
            builder.append(removeTags(fullCaption)).append("\n");
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
    private static final String PATTERN = "<.+>";
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
      protected String title;

      // Optional fields
      protected Optional<List<Content>> contents;

      @SuppressWarnings("unchecked")
      public static class ContentJsonDeserializer extends JsonDeserializer<Content> {

        @Override
        public Content deserialize(com.fasterxml.jackson.core.JsonParser jsonParser,
                                   DeserializationContext context) throws IOException {
          Map<String, Object> contentMap = jsonParser.readValueAs(Map.class);

          Content content = new Content();
          content.setType(getType(contentMap));
          content.setContent(getContent(contentMap));
          content.setFullCaption(getFullCaption(contentMap));
          return content;
        }

        private Optional<String> getType(Map<String, Object> map) {
          Object type = map.get("type");
          if (type == null) {
            return Optional.empty();
          }
          return Optional.of(type.toString());
        }

        private Optional<String> getContent(Map<String, Object> map) {
          Object contentObj = map.get("content");
          if (contentObj == null) {
            return Optional.empty();
          }

          StringBuilder contentStringBuilder = new StringBuilder();
          if (contentObj instanceof String) {
            contentStringBuilder.append(contentObj).append(" ");
          } else if (contentObj instanceof List) {
            for (Object content: (List<Object>) contentObj) {
              contentStringBuilder.append(content).append(" ");
            }
          } else if (contentObj instanceof Map) {
            Object content = ((HashMap<String, Object>) contentObj).get("text");
            if (content == null) {
              return Optional.empty();
            }
            contentStringBuilder.append(content).append(" ");
          } else {
            return Optional.empty();
          }

          return Optional.of(contentStringBuilder.toString());
        }

        private Optional<String> getFullCaption(Map<String, Object> map) {
          Object fullCaption = map.get("fullcaption");
          if (fullCaption == null) {
            return Optional.empty();
          }
          return Optional.of(fullCaption.toString());
        }
      }

      /**
       * Used internally by Jackson for JSON parsing.
       */
      @JsonDeserialize(using = ContentJsonDeserializer.class)
      public static class Content {
        protected Optional<String> type;
        protected Optional<String> content;
        protected Optional<String> fullCaption;

        @JsonSetter("type")
        public void setType(Optional<String> type) {
          this.type = type;
        }

        @JsonSetter("content")
        public void setContent(Optional<String> content) {
          this.content = content;
        }

        @JsonSetter("fullcaption")
        public void setFullCaption(Optional<String> fullCaption) {
          this.fullCaption = fullCaption;
        }

        @JsonGetter("type")
        public Optional<String> getType() {
          return type;
        }

        @JsonGetter("content")
        public Optional<String> getContent() {
          return content;
        }

        @JsonGetter("fullcaption")
        public Optional<String> getFullCaption() {
          return fullCaption;
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

      @JsonGetter("title")
      public String getTitle() {
        return title;
      }

      @JsonGetter("contents")
      public Optional<List<Content>> getContents() { return contents; }

      @JsonCreator
      public WashingtonPostObject(
              @JsonProperty(value = "id", required = true) String id,
              @JsonProperty(value = "published_date", required = true) long publishedDate,
              @JsonProperty(value = "title", required = true) String title) {
        this.id = id;
        this.publishedDate = publishedDate;
        this.title = title;
      }
    }
  }
}
