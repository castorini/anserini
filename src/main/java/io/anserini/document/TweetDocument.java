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

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;

/**
 * A Twitter document (status).
 */
public class TweetDocument implements SourceDocument {
  protected String screenname;
  protected String name;
  protected String profile_image_url;
  protected String createdAt;
  protected long timestamp_ms;
  protected long epoch;
  protected Tweet jsonObject;
  protected String jsonString;
  protected Optional<String> lang;
  protected OptionalLong inReplyToStatusId;
  protected OptionalLong inReplyToUserId;
  protected int followersCount;
  protected int friendsCount;
  protected int statusesCount;
  protected OptionalDouble latitude;
  protected OptionalDouble longitude;
  protected OptionalLong retweetStatusId;
  protected OptionalLong retweetUserId;
  protected OptionalLong retweetCount;
  protected String retweetStatusString;

  //private boolean keepRetweets;
  protected long idLong;
  protected String id;
  protected String text;

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
        if (fromJson(line)) return this;
      }
    } catch (IOException e) {
    }
    return null;
  }

  public static void main(String[] args) {
//    //String json = "{\"name\":\"borislin\",\"id\":1234,\"createdAt\":\"Fri Feb 01 10:56:07 +0000 2018\"}";
//    String json = "{\"in_reply_to_status_id\":null,\"id\":123456789,\"text\":\"this is the tweet contents.\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\",\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1,\"statuses_count\":1}}";
////    json = "{\"id\":123456789,\"text\":\"" + "this is the tweet contents."
////            + "\",\"user\":{\"screen_name\":\"foo\",\"name\":\"foo\"," +
////            "\"profile_image_url\":\"foo\",\"followers_count\":1,\"friends_count\":1," +
////            "\"statuses_count\":1},\"created_at\":\"Fri Feb 01 10:56:07 +0000 2018\"}";
//    ObjectMapper mapper = new ObjectMapper();
//    Tweet tweetDocumentObj = null;
//    try {
//      tweetDocumentObj = mapper
//        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
//        .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
//        .readValue(json, Tweet.class);
//
//      System.out.println("Success!==================");
//      System.out.println(tweetDocumentObj.user.name);
//      System.out.println(tweetDocumentObj.id);
//      System.out.println(tweetDocumentObj.in_reply_to_status_id.isPresent());
////      System.out.println(tweetDocumentObj.user.name);
//    } catch (IOException e) {
//      System.out.println(e.getMessage());
//      System.out.println( "Exception!!!!!!!!!!!!!!!!!!");
//    }

    String sampleFile = "sampleJsonObject.json";

    String sampleDoc =
            "{\n " +
            "  \"id\": \"doc\",\n" +
            "  \"contents\": \"this is the contents.\"\n" +
            "}";
    try {
      Writer writer = new BufferedWriter(new OutputStreamWriter(
              new FileOutputStream(sampleFile), "utf-8"));
      writer.write(sampleDoc);
      JsonDocument doc = new JsonDocument(sampleFile);
      JsonParser raw = new JsonFactory().createParser(new BufferedReader(new FileReader(sampleFile)));
      ObjectMapper objectMapper = new ObjectMapper();
      ArrayNode node = objectMapper.readTree(raw);
      System.out.println(node.isArray());
    } catch (Exception e) {
    }

  }

  public boolean fromJson(String json) {
    ObjectMapper mapper = new ObjectMapper();
    Tweet tweetObj = null;
    try {
      tweetObj = mapper
        .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES) // Ignore unrecognized properties
        .registerModule(new Jdk8Module()) // Deserialize Java 8 Optional: http://www.baeldung.com/jackson-optional
        .readValue(json, Tweet.class);
    } catch (IOException e) {
      LOG.error(e.getMessage());
      return false;
    }

    if (tweetObj.getDelete() != null && tweetObj.getDelete().isPresent()) {
      return false;
    }

    id = Long.toString(tweetObj.id);
    idLong = tweetObj.id;
    text = tweetObj.text;
    screenname = tweetObj.user.screen_name;
    name = tweetObj.user.name;
    profile_image_url = tweetObj.user.profile_image_url;
    createdAt = tweetObj.created_at;

    try {
      timestamp_ms = (new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)).parse(createdAt).getTime();
      epoch = timestamp_ms / 1000;
    } catch (ParseException e) {
      timestamp_ms = -1L;
      epoch = -1L;
    }

    if (tweetObj.in_reply_to_status_id == null || !tweetObj.in_reply_to_status_id.isPresent()) {
      inReplyToStatusId = OptionalLong.empty();
    } else {
      inReplyToStatusId = tweetObj.in_reply_to_status_id;
    }

    if (tweetObj.in_reply_to_user_id == null || !tweetObj.in_reply_to_user_id.isPresent()) {
      inReplyToUserId = OptionalLong.empty();
    } else {
      inReplyToUserId = tweetObj.in_reply_to_user_id;
    }

    if (tweetObj.retweeted_status == null || !tweetObj.retweeted_status.isPresent()) {
      retweetStatusId = OptionalLong.empty();
      retweetUserId = OptionalLong.empty();
      retweetCount = OptionalLong.empty();
    } else {
      retweetStatusId = OptionalLong.of(tweetObj.retweeted_status.get().id);
      retweetUserId = OptionalLong.of(tweetObj.retweeted_status.get().user.id);
      // retweet_count might say "100+"
      // TODO: This is ugly, come back and fix later.
      retweetCount = OptionalLong.of(tweetObj.retweet_count);
      //retweet_count = OptionalLong.of(Long.parseLong(obj.get("retweet_count")
      //        .getAsString().replace("+", "")));
    }

    if (tweetObj.coordinates == null || !tweetObj.coordinates.isPresent()) {
      latitude = OptionalDouble.empty();
      longitude = OptionalDouble.empty();
    } else {
      longitude = tweetObj.coordinates.get().get(0);
      latitude = tweetObj.coordinates.get().get(1);
    }

    if (tweetObj.lang == null || !tweetObj.lang.isPresent()) {
      lang = Optional.empty();
    } else {
      lang = tweetObj.lang;
    }

    if (tweetObj.user != null) {
      followersCount = tweetObj.user.followers_count;
      friendsCount = tweetObj.user.friends_count;
      statusesCount = tweetObj.user.statuses_count;
    }

    jsonString = json;
    jsonObject = tweetObj;

    return true;


//    JsonObject obj = null;
//    try {
//      obj = (JsonObject) JSON_PARSER.parse(json);
//    } catch (JsonSyntaxException e) {
//      return false;
//    }
//    if (obj.has("delete")) {
//      return false;
//    }
//    id = obj.get("id").getAsString();
//    idLong = Long.parseLong(id);
//    text = obj.get("text").getAsString();
//    screenname = obj.get("user").getAsJsonObject().get("screen_name").getAsString();
//    name = obj.get("user").getAsJsonObject().get("name").getAsString();
//    profile_image_url = obj.get("user").getAsJsonObject().get("profile_image_url").getAsString();
//    createdAt = obj.get("created_at").getAsString();
//
//    try {
//      timestamp_ms = (new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)).parse(createdAt).getTime();
//      epoch = timestamp_ms / 1000;
//    } catch (ParseException e) {
//      //LOG.debug(e);
//      timestamp_ms = -1L;
//      epoch = -1L;
//    }
//
//    inReplyToStatusId = (!obj.has("in_reply_to_status_id") || obj.get("in_reply_to_status_id").isJsonNull()) ?
//            OptionalLong.empty() : OptionalLong.of(obj.get("in_reply_to_status_id").getAsLong());
//
//    inReplyToUserId = (!obj.has("in_reply_to_user_id") || obj.get("in_reply_to_user_id").isJsonNull()) ?
//            OptionalLong.empty() : OptionalLong.of(obj.get("in_reply_to_user_id").getAsLong());
//
//    if (!obj.has("retweeted_status") || obj.get("retweeted_status").isJsonNull()) {
//      retweetStatusId = OptionalLong.empty();
//      retweetUserId = OptionalLong.empty();
//      retweetCount = OptionalLong.empty();
//    } else {
//      retweetStatusId = OptionalLong.of(obj.getAsJsonObject("retweeted_status").get("id").getAsLong());
//      retweetUserId = OptionalLong.of(obj.getAsJsonObject("retweeted_status")
//              .get("user").getAsJsonObject().get("id").getAsLong());
//      // retweet_count might say "100+"
//      // TODO: This is ugly, come back and fix later.
//      retweetCount = OptionalLong.of(Long.parseLong(obj.get("retweet_count")
//              .getAsString().replace("+", "")));
//    }
//
//    if (!obj.has("coordinates") || obj.get("coordinates").isJsonNull()) {
//      latitude = OptionalDouble.empty();
//      longitude = OptionalDouble.empty();
//    } else {
//      latitude = OptionalDouble.of(obj.getAsJsonObject("coordinates")
//              .getAsJsonArray("coordinates").get(1).getAsDouble());
//      longitude = OptionalDouble.of(obj.getAsJsonObject("coordinates")
//              .getAsJsonArray("coordinates").get(0).getAsDouble());
//    }
//
//    String langOpt = (obj.get("lang") == null || obj.get("lang").isJsonNull()) ?
//            null  : obj.get("lang").getAsString();
//    lang = Optional.ofNullable(langOpt);
//
//    followersCount = obj.get("user").getAsJsonObject().get("followers_count").getAsInt();
//    friendsCount = obj.get("user").getAsJsonObject().get("friends_count").getAsInt();
//    statusesCount = obj.get("user").getAsJsonObject().get("statuses_count").getAsInt();
//
//    jsonString = json;
//
//    return true;
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
  public String getName() {
    return name;
  }
  public String getProfileImageURL() {
    return profile_image_url;
  }
  public String getCreatedAt() {
    return createdAt;
  }
  public long getTimestampMs() { return timestamp_ms; }
  public long getEpoch() { return epoch; }
  public String getText() {
    return text;
  }
  public Tweet getJsonObject() { return jsonObject; }
  public String getJsonString() {
    return jsonString;
  }
  public Optional<String> getLang() {
    return lang;
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
  public String getRetweetStatusString() {
    return retweetStatusString;
  }
}
