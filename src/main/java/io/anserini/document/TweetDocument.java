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

package io.anserini.document;

import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.databind.*;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import io.anserini.collection.SourceDocument;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Twitter document (status).
 */
public class TweetDocument implements SourceDocument {
  // Required fields
  protected String screenname;
  protected int followersCount;
  protected int friendsCount;
  protected int statusesCount;
  protected String createdAt;
  protected String id;
  protected long idLong;
  protected String text;
  protected Status jsonObject;
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

  private static final Logger LOG = LogManager.getLogger(TweetDocument.class);
  private static final String DATE_FORMAT = "E MMM dd HH:mm:ss ZZZZZ yyyy"; // "Fri Mar 29 11:03:41 +0000 2013"

  public TweetDocument() {
    super();
  }

  @Override
  public TweetDocument readNextRecord(BufferedReader reader) throws IOException {
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
    Status tweetObj = null;
    try {
      tweetObj = mapper
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
        .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
        .readValue(json, Status.class);
    } catch (IOException e) {
      return false;
    }

    if (tweetObj.delete() != null && tweetObj.delete().isPresent()) {
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

    if (tweetObj.in_reply_to_status_id() == null || !tweetObj.in_reply_to_status_id().isPresent()) {
      inReplyToStatusId = OptionalLong.empty();
    } else {
      inReplyToStatusId = tweetObj.in_reply_to_status_id();
    }

    if (tweetObj.in_reply_to_user_id() == null || !tweetObj.in_reply_to_user_id().isPresent()) {
      inReplyToUserId = OptionalLong.empty();
    } else {
      inReplyToUserId = tweetObj.in_reply_to_user_id();
    }

    if (tweetObj.retweeted_status() == null || !tweetObj.retweeted_status().isPresent()) {
      retweetStatusId = OptionalLong.empty();
      retweetUserId = OptionalLong.empty();
      retweetCount = OptionalLong.empty();
    } else {
      retweetStatusId = tweetObj.retweeted_status().get().id();
      if (tweetObj.retweeted_status().get().user() != null && tweetObj.retweeted_status().get().user().isPresent()) {
        retweetUserId = tweetObj.retweeted_status().get().user().get().id();
      } else {
        retweetUserId = OptionalLong.empty();
      }
      retweetCount = tweetObj.retweet_count();
    }

    if (tweetObj.coordinates() == null || !tweetObj.coordinates().isPresent() ||
        tweetObj.coordinates().get().coordinates() == null ||
        !tweetObj.coordinates().get().coordinates().isPresent() ||
        tweetObj.coordinates().get().coordinates().get().size() < 2) {
      latitude = OptionalDouble.empty();
      longitude = OptionalDouble.empty();
    } else {
      longitude = tweetObj.coordinates().get().coordinates().get().get(0);
      latitude = tweetObj.coordinates().get().coordinates().get().get(1);
    }

    if (tweetObj.lang() == null || !tweetObj.lang().isPresent()) {
      lang = Optional.empty();
    } else {
      lang = tweetObj.lang();
    }

    followersCount = tweetObj.user().followers_count();
    friendsCount = tweetObj.user().friends_count();
    statusesCount = tweetObj.user().statuses_count();
    screenname = tweetObj.user().screen_name();

    if (tweetObj.user().name() != null && tweetObj.user().name().isPresent()) {
      name = tweetObj.user().name();
    } else {
      name = Optional.empty();
    }

    if (tweetObj.user().profile_image_url() != null && tweetObj.user().profile_image_url().isPresent()) {
      profile_image_url = tweetObj.user().profile_image_url();
    } else {
      profile_image_url = Optional.empty();
    }

    jsonString = json;
    jsonObject = tweetObj;

    return true;
  }

  public TweetDocument fromTSV(String tsv) {
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
  public Status getJsonObject() { return jsonObject; }
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
}
