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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import io.anserini.document.SourceDocument;
import io.anserini.util.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of a Twitter collection.
 */
public class TweetCollection extends Collection {

  public class FileSegment extends Collection.FileSegment {
    protected FileSegment(Path path) throws IOException {
      dType = new TweetCollection.Document();

      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.endsWith(".gz")) { //.gz
        InputStream stream = new GZIPInputStream(
            Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else { // plain text file
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".gz"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, EMPTY_SET, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  /**
   * A Twitter document (status).
   */
  public static class Document implements SourceDocument {
    // Required fields
    protected String screenname;
    protected int followersCount;
    protected int friendsCount;
    protected int statusesCount;
    protected String createdAt;
    protected String id;
    protected long idLong;
    protected String text;
    protected TweetObject jsonObject;
    protected String jsonString;

    // Optional fields
    protected Optional<String> name;
    protected Optional<String> profile_image_url;
    protected OptionalLong timestamp_ms;
    protected OptionalLong epoch;
    protected Optional<String> lang;
    protected OptionalLong inReplyToStatusId;
    protected OptionalLong inReplyToUserId;
    protected OptionalDouble latitude;
    protected OptionalDouble longitude;
    protected OptionalLong retweetStatusId;
    protected OptionalLong retweetUserId;
    protected OptionalLong retweetCount;

    //private boolean keepRetweets;

    private static final Logger LOG = LogManager.getLogger(Document.class);
    private static final String DATE_FORMAT = "E MMM dd HH:mm:ss ZZZZZ yyyy"; // "Fri Mar 29 11:03:41 +0000 2013"

    public Document() {
      super();
    }

    @Override
    public Document readNextRecord(BufferedReader reader) throws IOException {
      String line;
      try {
        while ((line = reader.readLine()) != null) {
          if (fromJson(line)) {
            return this;
          } // else: not desired JSON data, read the next line
        }
      } catch (IOException e) {
        LOG.error("Exception from BufferedReader:", e);
      }
      return null;
    }

    public boolean fromJson(String json) {
      ObjectMapper mapper = new ObjectMapper();
      TweetObject tweetObj = null;
      try {
        tweetObj = mapper
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
                .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
                .readValue(json, TweetObject.class);
      } catch (IOException e) {
        return false;
      }

      if (JsonParser.isAvailable(tweetObj.delete())) {
        return false;
      }

      id = tweetObj.id_str();
      idLong = Long.parseLong(tweetObj.id_str());
      text = tweetObj.text();
      createdAt = tweetObj.created_at();

      try {
        timestamp_ms = OptionalLong.of((new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)).parse(createdAt).getTime());
        epoch = timestamp_ms.isPresent() ? OptionalLong.of(timestamp_ms.getAsLong() / 1000) : OptionalLong.empty();
      } catch (ParseException e) {
        timestamp_ms = OptionalLong.of(-1L);
        epoch = OptionalLong.of(-1L);
        return false;
      }

      if (JsonParser.isAvailable(tweetObj.in_reply_to_status_id())) {
        inReplyToStatusId = tweetObj.in_reply_to_status_id();
      } else {
        inReplyToStatusId = OptionalLong.empty();
      }

      if (JsonParser.isAvailable(tweetObj.in_reply_to_user_id())) {
        inReplyToUserId = tweetObj.in_reply_to_user_id();
      } else {
        inReplyToUserId = OptionalLong.empty();
      }

      if (JsonParser.isAvailable(tweetObj.retweeted_status())) {
        retweetStatusId = tweetObj.retweeted_status().get().id();
        if (JsonParser.isAvailable(tweetObj.retweeted_status().get().user())) {
          retweetUserId = tweetObj.retweeted_status().get().user().get().id();
        } else {
          retweetUserId = OptionalLong.empty();
        }
        retweetCount = tweetObj.retweet_count();
      } else {
        retweetStatusId = OptionalLong.empty();
        retweetUserId = OptionalLong.empty();
        retweetCount = OptionalLong.empty();
      }

      if (JsonParser.isAvailable(tweetObj.coordinates()) &&
          JsonParser.isAvailable(tweetObj.coordinates().get().coordinates()) &&
          tweetObj.coordinates().get().coordinates().get().size() >= 2) {
        longitude = tweetObj.coordinates().get().coordinates().get().get(0);
        latitude = tweetObj.coordinates().get().coordinates().get().get(1);
      } else {
        latitude = OptionalDouble.empty();
        longitude = OptionalDouble.empty();
      }

      if (JsonParser.isAvailable(tweetObj.lang())) {
        lang = tweetObj.lang();
      } else {
        lang = Optional.empty();
      }

      followersCount = tweetObj.user().followers_count();
      friendsCount = tweetObj.user().friends_count();
      statusesCount = tweetObj.user().statuses_count();
      screenname = tweetObj.user().screen_name();

      if (JsonParser.isAvailable(tweetObj.user().name())) {
        name = tweetObj.user().name();
      } else {
        name = Optional.empty();
      }

      if (JsonParser.isAvailable(tweetObj.user().profile_image_url())) {
        profile_image_url = tweetObj.user().profile_image_url();
      } else {
        profile_image_url = Optional.empty();
      }

      jsonString = json;
      jsonObject = tweetObj;

      return true;
    }

    public Document fromTSV(String tsv) {
      String[] columns = tsv.split("\t");

      if (columns.length < 4) {
        System.err.println("error parsing: " + tsv);
        return null;
      }

      id = columns[0];
      idLong = Long.parseLong(columns[0]);
      screenname = columns[1];
      createdAt = columns[2];

      StringBuilder b = new StringBuilder();
      for (int i = 3; i < columns.length; i++) {
        b.append(columns[i] + " ");
      }
      text = b.toString().trim();

      return this;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
      return text;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public long getIdLong() { return idLong; }
    public String getScreenname() {
      return screenname;
    }
    public String getCreatedAt() {
      return createdAt;
    }
    public String getText() {
      return text;
    }
    public TweetObject getJsonObject() { return jsonObject; }
    public String getJsonString() {
      return jsonString;
    }
    public int getFollowersCount() {
      return followersCount;
    }
    public int getFriendsCount() {
      return friendsCount;
    }
    public int getStatusesCount() {
      return statusesCount;
    }

    public Optional<String> getName() {
      return name;
    }
    public Optional<String> getProfileImageURL() {
      return profile_image_url;
    }
    public OptionalLong getTimestampMs() { return timestamp_ms; }
    public OptionalLong getEpoch() { return epoch; }
    public Optional<String> getLang() {
      return lang;
    }
    public OptionalLong getInReplyToStatusId() {
      return inReplyToStatusId;
    }
    public OptionalLong getInReplyToUserId() {
      return inReplyToUserId;
    }
    public OptionalDouble getlatitude() {
      return latitude;
    }
    public OptionalDouble getLongitude() {
      return longitude;
    }
    public OptionalLong getRetweetedStatusId() {
      return retweetStatusId;
    }
    public OptionalLong getRetweetedUserId() {
      return retweetUserId;
    }
    public OptionalLong getRetweetCount() {
      return retweetCount;
    }

    /**
     * A Twitter document object class used in Jackson JSON parser
     */
    public static class TweetObject {

      // Required fields
      protected String created_at;
      protected String id_str;
      protected String text;
      protected TweetObject.User user;

      // Optional fields
      protected OptionalLong retweet_count;
      protected OptionalLong in_reply_to_status_id;
      protected OptionalLong in_reply_to_user_id;
      protected Optional<TweetObject.RetweetedStatus> retweeted_status;
      protected Optional<String> lang;
      protected Optional<TweetObject.Delete> delete;
      protected Optional<TweetObject.Coordinates> coordinates;

      // Must make inner classes static for deserialization in Jackson
      // http://www.cowtowncoder.com/blog/archives/2010/08/entry_411.html
      public static class Delete {
        protected Optional<String> timestamp_ms;

        @JsonGetter("timestamp_ms")
        public Optional<String> timestamp_ms() {
          return timestamp_ms;
        }
      }

      public static class Coordinates {
        protected Optional<List<OptionalDouble>> coordinates;

        @JsonGetter("coordinates")
        public Optional<List<OptionalDouble>> coordinates() { return coordinates; }
      }

      public static class RetweetedStatus {
        protected OptionalLong id;
        protected Optional<TweetObject.User> user;

        @JsonGetter("id")
        public OptionalLong id() {
          return id;
        }

        @JsonGetter("user")
        public Optional<TweetObject.User> user() {
          return user;
        }
      }

      public static class User {
        // Required fields
        protected String screen_name;
        protected int followers_count;
        protected int friends_count;
        protected int statuses_count;

        // Opional fields
        protected Optional<String> name;
        protected Optional<String> profile_image_url;
        protected OptionalLong id;

        @JsonCreator
        public User(
                @JsonProperty(value = "followers_count", required = true) int followers_count,
                @JsonProperty(value = "friends_count", required = true) int friends_count,
                @JsonProperty(value = "statuses_count", required = true) int statuses_count,
                @JsonProperty(value = "screen_name", required = true) String screen_name) {
          this.followers_count = followers_count;
          this.friends_count = friends_count;
          this.statuses_count = statuses_count;
          this.screen_name = screen_name;
        }

        @JsonGetter("screen_name")
        public String screen_name() {
          return screen_name;
        }

        @JsonGetter("followers_count")
        public int followers_count() {
          return followers_count;
        }

        @JsonGetter("friends_count")
        public int friends_count() {
          return friends_count;
        }

        @JsonGetter("statuses_count")
        public int statuses_count() {
          return statuses_count;
        }

        @JsonGetter("name")
        public Optional<String> name() {
          return name;
        }

        @JsonGetter("profile_image_url")
        public Optional<String> profile_image_url() {
          return profile_image_url;
        }

        @JsonGetter("id")
        public OptionalLong id() {
          return id;
        }
      }

      @JsonCreator
      public TweetObject(
              @JsonProperty(value = "created_at", required = true) String created_at,
              @JsonProperty(value = "id_str", required = true) String id_str,
              @JsonProperty(value = "text", required = true) String text,
              @JsonProperty(value = "user", required = true) TweetObject.User user) {
        this.created_at = created_at;
        this.id_str = id_str;
        this.text = text;
        this.user = user;
      }

      @JsonGetter("id_str")
      public String id_str() {
        return id_str;
      }

      @JsonGetter("text")
      public String text() {
        return text;
      }

      @JsonGetter("user")
      public TweetObject.User user() {
        return user;
      }

      @JsonGetter("created_at")
      public String created_at() {
        return created_at;
      }

      @JsonGetter("retweet_count")
      public OptionalLong retweet_count() {
        return retweet_count;
      }

      @JsonSetter("retweet_count")
      public void set_retweet_count_internal(JsonNode retweet_count_internal) {
        if (retweet_count_internal != null) {
          if (retweet_count_internal.isTextual()) {
            // retweet_count might say "100+"
            // TODO: This is ugly, come back and fix later.
            retweet_count = OptionalLong.of(Long.parseLong(retweet_count_internal.asText().replace("+", "")));
          } else if (retweet_count_internal.isNumber()) {
            retweet_count = OptionalLong.of(retweet_count_internal.asLong());
          }
        }
      }

      @JsonGetter("in_reply_to_status_id")
      public OptionalLong in_reply_to_status_id() {
        return in_reply_to_status_id;
      }

      @JsonGetter("in_reply_to_user_id")
      public OptionalLong in_reply_to_user_id() {
        return in_reply_to_user_id;
      }

      @JsonGetter("retweeted_status")
      public Optional<TweetObject.RetweetedStatus> retweeted_status() {
        return retweeted_status;
      }

      @JsonGetter("lang")
      public Optional<String> lang() {
        return lang;
      }

      @JsonGetter("delete")
      public Optional<TweetObject.Delete> delete() {
        return delete;
      }

      @JsonGetter("coordinates")
      public Optional<TweetObject.Coordinates> coordinates() { return coordinates; }
    }


  }


}
