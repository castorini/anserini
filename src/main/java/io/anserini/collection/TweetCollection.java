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
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
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
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.zip.GZIPInputStream;

/**
 * A collection of tweets.
 */
public class TweetCollection extends DocumentCollection<TweetCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(TweetCollection.class);

  @Override
  public FileSegment<TweetCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * A file containing multiple tweets.
   */
  public static class Segment extends FileSegment<TweetCollection.Document> {
    private static final String DATE_FORMAT = "E MMM dd HH:mm:ss ZZZZZ yyyy"; // "Fri Mar 29 11:03:41 +0000 2013"

    protected Segment(Path path) throws IOException {
      super(path);
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

    @Override
    public void readNext() throws IOException, NoSuchElementException, ParseException {
      String nextRecord = bufferedReader.readLine();
      if (nextRecord == null) {
        throw new NoSuchElementException();
      }
      parseJson(nextRecord);
    }

    private void parseJson(String json) throws ParseException {
      ObjectMapper mapper = new ObjectMapper();
      Document.TweetObject tweetObj = null;
      try {
        tweetObj = mapper
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
                .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
                .readValue(json, Document.TweetObject.class);
      } catch (IOException e) {
        throw new ParseException("IOException in parseJson", 0);
      }

      if (isFieldAvailable(tweetObj.getDelete())) {
        throw new ParseException("Ignore deleted tweets", 0);
      }

      bufferedRecord = new TweetCollection.Document();
      bufferedRecord.id = tweetObj.getIdStr();
      bufferedRecord.idLong = Long.parseLong(bufferedRecord.id);
      bufferedRecord.text = tweetObj.getText();
      bufferedRecord.createdAt = tweetObj.getCreatedAt();

      try {
        bufferedRecord.timestampMs = OptionalLong.of((new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)).parse(bufferedRecord.createdAt).getTime());
        bufferedRecord.epoch = bufferedRecord.timestampMs.isPresent() ? OptionalLong.of(bufferedRecord.timestampMs.getAsLong() / 1000) : OptionalLong.empty();
      } catch (ParseException e) {
        bufferedRecord.timestampMs = OptionalLong.of(-1L);
        bufferedRecord.epoch = OptionalLong.of(-1L);
        throw e;
      }

      if (isFieldAvailable(tweetObj.getInReplyToStatusId())) {
        bufferedRecord.inReplyToStatusId = tweetObj.getInReplyToStatusId();
      } else {
        bufferedRecord.inReplyToStatusId = OptionalLong.empty();
      }

      if (isFieldAvailable(tweetObj.getInReplyToUserId())) {
        bufferedRecord.inReplyToUserId = tweetObj.getInReplyToUserId();
      } else {
        bufferedRecord.inReplyToUserId = OptionalLong.empty();
      }

      if (isFieldAvailable(tweetObj.getRetweetedStatus())) {
        bufferedRecord.retweetStatusId = tweetObj.getRetweetedStatus().get().getId();
        if (isFieldAvailable(tweetObj.getRetweetedStatus().get().getUser())) {
          bufferedRecord.retweetUserId = tweetObj.getRetweetedStatus().get().getUser().get().getId();
        } else {
          bufferedRecord.retweetUserId = OptionalLong.empty();
        }
        bufferedRecord.retweetCount = tweetObj.getRetweetCount();
      } else {
        bufferedRecord.retweetStatusId = OptionalLong.empty();
        bufferedRecord.retweetUserId = OptionalLong.empty();
        bufferedRecord.retweetCount = OptionalLong.empty();
      }

      if (isFieldAvailable(tweetObj.getCoordinates()) &&
              isFieldAvailable(tweetObj.getCoordinates().get().getCoordinates()) &&
              tweetObj.getCoordinates().get().getCoordinates().get().size() >= 2) {
        bufferedRecord.longitude = tweetObj.getCoordinates().get().getCoordinates().get().get(0);
        bufferedRecord.latitude = tweetObj.getCoordinates().get().getCoordinates().get().get(1);
      } else {
        bufferedRecord.latitude = OptionalDouble.empty();
        bufferedRecord.longitude = OptionalDouble.empty();
      }

      if (isFieldAvailable(tweetObj.getLang())) {
        bufferedRecord.lang = tweetObj.getLang();
      } else {
        bufferedRecord.lang = Optional.empty();
      }

      bufferedRecord.followersCount = tweetObj.getUser().getFollowersCount();
      bufferedRecord.friendsCount = tweetObj.getUser().getFriendsCount();
      bufferedRecord.statusesCount = tweetObj.getUser().getStatusesCount();
      bufferedRecord.screenName = tweetObj.getUser().getScreenName();

      if (isFieldAvailable(tweetObj.getUser().getName())) {
        bufferedRecord.name = tweetObj.getUser().getName();
      } else {
        bufferedRecord.name = Optional.empty();
      }

      if (isFieldAvailable(tweetObj.getUser().getProfileImageUrl())) {
        bufferedRecord.profileImageUrl = tweetObj.getUser().getProfileImageUrl();
      } else {
        bufferedRecord.profileImageUrl = Optional.empty();
      }

      bufferedRecord.jsonString = json;
      bufferedRecord.jsonObject = tweetObj;
    }

    private boolean isFieldAvailable(Object field) {
      if (field == null) {
        return false;
      }
      boolean isPresent;
      if (field.getClass() == OptionalLong.class) {
        isPresent = ((OptionalLong)field).isPresent();
      } else if (field.getClass() == OptionalDouble.class) {
        isPresent = ((OptionalDouble)field).isPresent();
      } else if (field.getClass() == OptionalInt.class) {
        isPresent = ((OptionalInt)field).isPresent();
      } else {
        isPresent = ((Optional)field).isPresent();
      }
      return isPresent;
    }
  }

  /**
   * A tweet (i.e., status).
   */
  public static class Document implements SourceDocument {
    // Required fields
    protected String screenName;
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
    protected Optional<String> profileImageUrl;
    protected OptionalLong timestampMs;
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

    public Document() {
      super();
    }

    public Document fromTSV(String tsv) {
      String[] columns = tsv.split("\t");

      if (columns.length < 4) {
        System.err.println("error parsing: " + tsv);
        return null;
      }

      id = columns[0];
      idLong = Long.parseLong(columns[0]);
      screenName = columns[1];
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

    public long getIdLong() {
      return idLong;
    }

    public String getScreenName() {
      return screenName;
    }

    public String getCreatedAt() {
      return createdAt;
    }

    public String getText() {
      return text;
    }

    public TweetObject getJsonObject() {
      return jsonObject;
    }

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
      return profileImageUrl;
    }

    public OptionalLong getTimestampMs() {
      return timestampMs;
    }

    public OptionalLong getEpoch() {
      return epoch;
    }

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
     * Used internally by Jackson for JSON parsing.
     */
    public static class TweetObject {

      // Required fields
      protected String createdAt;
      protected String idStr;
      protected String text;
      protected TweetObject.User user;

      // Optional fields
      protected OptionalLong retweetCount;
      protected OptionalLong inReplyToStatusId;
      protected OptionalLong inReplyToUserId;
      protected Optional<TweetObject.RetweetedStatus> retweetedStatus;
      protected Optional<String> lang;
      protected Optional<TweetObject.Delete> delete;
      protected Optional<TweetObject.Coordinates> coordinates;

      // Must make inner classes static for deserialization in Jackson
      // http://www.cowtowncoder.com/blog/archives/2010/08/entry_411.html

      /**
       * Used internally by Jackson for JSON parsing.
       */
      public static class Delete {
        protected Optional<String> timestampMs;

        @JsonGetter("timestamp_ms")
        public Optional<String> getTimestampMs() {
          return timestampMs;
        }
      }

      /**
       * Used internally by Jackson for JSON parsing.
       */
      public static class Coordinates {
        protected Optional<List<OptionalDouble>> coordinates;

        @JsonGetter("coordinates")
        public Optional<List<OptionalDouble>> getCoordinates() { return coordinates; }
      }

      /**
       * Used internally by Jackson for JSON parsing.
       */
      public static class RetweetedStatus {
        protected OptionalLong id;
        protected Optional<TweetObject.User> user;

        @JsonGetter("id")
        public OptionalLong getId() {
          return id;
        }

        @JsonGetter("user")
        public Optional<TweetObject.User> getUser() {
          return user;
        }
      }

      /**
       * Used internally by Jackson for JSON parsing.
       */
      public static class User {
        // Required fields
        protected String screenName;
        protected int followersCount;
        protected int friendsCount;
        protected int statusesCount;

        // Opional fields
        protected Optional<String> name;
        protected Optional<String> profileImageUrl;
        protected OptionalLong id;

        @JsonCreator
        public User(
                @JsonProperty(value = "followers_count", required = true) int followersCount,
                @JsonProperty(value = "friends_count", required = true) int friendsCount,
                @JsonProperty(value = "statuses_count", required = true) int statusesCount,
                @JsonProperty(value = "screen_name", required = true) String screenName) {
          this.followersCount = followersCount;
          this.friendsCount = friendsCount;
          this.statusesCount = statusesCount;
          this.screenName = screenName;
        }

        @JsonGetter("screen_name")
        public String getScreenName() {
          return screenName;
        }

        @JsonGetter("followers_count")
        public int getFollowersCount() {
          return followersCount;
        }

        @JsonGetter("friends_count")
        public int getFriendsCount() {
          return friendsCount;
        }

        @JsonGetter("statuses_count")
        public int getStatusesCount() {
          return statusesCount;
        }

        @JsonGetter("name")
        public Optional<String> getName() {
          return name;
        }

        @JsonGetter("profile_image_url")
        public Optional<String> getProfileImageUrl() {
          return profileImageUrl;
        }

        @JsonGetter("id")
        public OptionalLong getId() {
          return id;
        }
      }

      @JsonCreator
      public TweetObject(
              @JsonProperty(value = "created_at", required = true) String createdAt,
              @JsonProperty(value = "id_str", required = true) String idStr,
              @JsonProperty(value = "text", required = true) String text,
              @JsonProperty(value = "user", required = true) TweetObject.User user) {
        this.createdAt = createdAt;
        this.idStr = idStr;
        this.text = text;
        this.user = user;
      }

      @JsonGetter("id_str")
      public String getIdStr() {
        return idStr;
      }

      @JsonGetter("text")
      public String getText() {
        return text;
      }

      @JsonGetter("user")
      public TweetObject.User getUser() {
        return user;
      }

      @JsonGetter("created_at")
      public String getCreatedAt() {
        return createdAt;
      }

      @JsonGetter("retweet_count")
      public OptionalLong getRetweetCount() {
        return retweetCount;
      }

      @JsonSetter("retweet_count")
      public void setRetweetCountInternal(JsonNode retweetCountInternal) {
        if (retweetCountInternal != null) {
          if (retweetCountInternal.isTextual()) {
            // retweet_count might say "100+"
            // TODO: This is ugly, come back and fix later.
            retweetCount = OptionalLong.of(Long.parseLong(retweetCountInternal.asText().replace("+", "")));
          } else if (retweetCountInternal.isNumber()) {
            retweetCount = OptionalLong.of(retweetCountInternal.asLong());
          }
        }
      }

      @JsonGetter("in_reply_to_status_id")
      public OptionalLong getInReplyToStatusId() {
        return inReplyToStatusId;
      }

      @JsonGetter("in_reply_to_user_id")
      public OptionalLong getInReplyToUserId() {
        return inReplyToUserId;
      }

      @JsonGetter("retweeted_status")
      public Optional<TweetObject.RetweetedStatus> getRetweetedStatus() {
        return retweetedStatus;
      }

      @JsonGetter("lang")
      public Optional<String> getLang() {
        return lang;
      }

      @JsonGetter("delete")
      public Optional<TweetObject.Delete> getDelete() {
        return delete;
      }

      @JsonGetter("coordinates")
      public Optional<TweetObject.Coordinates> getCoordinates() { return coordinates; }
    }
  }
}
