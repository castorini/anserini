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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.anserini.index.generator.WashingtonPostGenerator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

/**
 * An instance of the <a href="https://trec.nist.gov/data/wapost/">TREC Washington Post Corpus</a>.
 * This class works for both v2 and v3 of the Washington Post corpus.
 * 
 * Collection v2 contains 608,180 news articles and blog posts from January 2012 through August 2017,
 * stored in JSON format. The collection is 1.5GB compressed, 5.9GB uncompressed.
 * 
 * Collection v3 contains an additional 154,418 new documents from 2018 and 2019 and removes (near)
 * duplicates, resulting in a total of 671,947 news articles and blog posts from January 2012 
 * through December 2019, The collection is 1.8GB compressed, 6.8GB uncompressed.
 */
public class WashingtonPostCollection extends DocumentCollection<WashingtonPostCollection.Document> {

  public WashingtonPostCollection(Path path){
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".txt", ".jl"));
  }

  @Override
  public FileSegment<WashingtonPostCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing multiple documents from the <a href="https://trec.nist.gov/data/wapost/">TREC Washington Post Corpus</a>.
   * The corpus is distributed as a single file.
   */
  public static class Segment extends FileSegment<Document> {
    private String fileName;

    public Segment(Path path) throws IOException {
      super(path);
      this.fileName = path.toString();
      this.bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(this.fileName), "utf-8"));
    }

    @Override
    public void readNext() throws IOException {
      String nextRecord = bufferedReader.readLine();
      if (nextRecord == null) {
        throw new NoSuchElementException();
      }
      parseRecord(nextRecord);
    }

    private void parseRecord(String record) {
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
      bufferedRecord.publishDate = wapoObj.getPublishedDate();
      bufferedRecord.title = wapoObj.getTitle();
      bufferedRecord.articleUrl = wapoObj.getArticleUrl();
      bufferedRecord.author = wapoObj.getAuthor();
      bufferedRecord.obj = wapoObj;
      bufferedRecord.raw = record;
    }
  }

  /**
   * A document from the <a href="https://trec.nist.gov/data/wapost/">TREC Washington Post Corpus</a>.
   */
  public static class Document implements SourceDocument {
    public static final List<String> CONTENT_TYPE_TAG = Arrays.asList("sanitized_html", "tweet");

    // Required fields
    protected String id;
    protected Optional<String> articleUrl;
    protected Optional<String> author;
    protected long publishDate;
    protected Optional<String> title;
    protected String raw;
    protected WashingtonPostObject obj;

    protected String fullCaption = null;
    protected String kicker = null;

    private String removeTags(String content) {
      return Jsoup.parse(content).text();
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      StringBuilder contentBuilder = new StringBuilder();
      getTitle().ifPresent(title -> contentBuilder.append(title).append("\n"));

      getObj().getContents().ifPresent(contents -> {
        for (WashingtonPostObject.Content contentObj : contents) {
          if (contentObj == null) continue;
          if (contentObj.getType().isPresent() && contentObj.getContent().isPresent()) {
            contentObj.getType().ifPresent(type -> {
              contentObj.getContent().ifPresent(content -> {
                if (CONTENT_TYPE_TAG.contains(type)) {
                  contentBuilder.append(removeTags(content)).append("\n");
                } else if (type.compareToIgnoreCase("kicker") == 0) {
                  kicker = content;
                  contentBuilder.append(content).append("\n");
                } else if (type.compareToIgnoreCase("date") == 0 && this.publishDate == 0) {
                  this.publishDate = Long.parseLong(content);
                }
              });
            });
          }
          contentObj.getFullCaption().ifPresent(caption -> {
            fullCaption = contentObj.getFullCaption().get();
            contentBuilder.append(removeTags(fullCaption)).append("\n");
          });
        }
      });

      return contentBuilder.toString();
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public String getFullCaption() {
      return fullCaption;
    }

    public String getKicker() {
      return kicker;
    }

    public Optional<String> getArticleUrl() {
      return articleUrl;
    }
  
    public Optional<String> getAuthor() {
      return author;
    }
  
    public long getPublishDate() {
      return publishDate;
    }
  
    public Optional<String> getTitle() {
      return title;
    }
  
    public WashingtonPostObject getObj() {
      return obj;
    }
  
    /**
     * Used internally by Jackson for JSON parsing.
     */
    public static class WashingtonPostObject {
      // Required fields
      protected String id;
      protected Optional<String> articleUrl;
      protected Optional<String> author;
      protected Optional<String> title;

      // Optional fields
      protected long publishedDate;
      protected Optional<List<Content>> contents;

      /**
       * Used internally by Jackson for JSON parsing.
       */
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
            contentStringBuilder.append(contentObj);
          } else if (contentObj instanceof Long) {
            contentStringBuilder.append(String.valueOf(contentObj));
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
  
      @JsonGetter("article_url")
      public Optional<String> getArticleUrl() {
        return articleUrl;
      }
  
      @JsonGetter("author")
      public Optional<String> getAuthor() {
        return author;
      }

      @JsonGetter("published_date")
      public long getPublishedDate() {
        return publishedDate;
      }

      @JsonGetter("title")
      public Optional<String> getTitle() {
        return title;
      }

      @JsonGetter("contents")
      public Optional<List<Content>> getContents() { return contents; }

      @JsonCreator
      public WashingtonPostObject(
              @JsonProperty(value = "id", required = true) String id,
              @JsonProperty(value = "article_url", required = false) Optional<String> articleUrl,
              @JsonProperty(value = "author", required = false) Optional<String> author,
              @JsonProperty(value = "published_date", required = false) long publishedDate,
              @JsonProperty(value = "title", required = true) Optional<String> title) {
        this.id = id;
        this.articleUrl = articleUrl;
        this.author = author;
        this.publishedDate = publishedDate;
        this.title = title;
      }
    }
  }
}
