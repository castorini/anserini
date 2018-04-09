package io.anserini.document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

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
  protected JsonObject jsonObject;
  protected String jsonString;
  protected String lang;
  protected long inReplyToStatusId;
  protected long inReplyToUserId;
  protected int followersCount;
  protected int friendsCount;
  protected int statusesCount;
  protected double latitude;
  protected double longitude;
  protected long retweetStatusId;
  protected long retweetUserId;
  protected int retweetCount;
  protected String retweetStatusString;

  //private boolean keepRetweets;
  protected String id;
  protected String text;

  private static final Logger LOG = LogManager.getLogger(TweetDocument.class);
  private static final JsonParser JSON_PARSER = new JsonParser();
  private static final String DATE_FORMAT = "E MMM dd HH:mm:ss ZZZZZ yyyy"; // "Fri Mar 29 11:03:41 +0000 2013"

  public TweetDocument() {
    super();
  }

  @Override
  public SourceDocumentResultWrapper<TweetDocument> readNextRecord(BufferedReader reader) throws IOException {
    while (true) {
      String line = reader.readLine();
      if (line == null) {
        return new SourceDocumentResultWrapper<TweetDocument>(
            null, false, SourceDocumentResultWrapper.FailureReason.EOF);
      }
      TweetDocument parsed = fromJson(line);
      if (parsed == null) {
        return new SourceDocumentResultWrapper<TweetDocument>(
            null, false, SourceDocumentResultWrapper.FailureReason.ParsingError);
      } else {
        return new SourceDocumentResultWrapper<TweetDocument>(parsed, true, null);
      }
    }
  }

  public TweetDocument fromJson(String json) {
    JsonObject obj = null;
    try {
      obj = (JsonObject) JSON_PARSER.parse(json);
    } catch (Exception e) {
      // Catch any malformed JSON.
      LOG.error("Error parsing: " + json);
      return null;
    }

    if (obj.get("text") == null) {
      LOG.info("Skip Tweet with empty text: " + json);
      return null;
    }

    id = obj.get("id").getAsString();
    text = obj.get("text").getAsString();
    screenname = obj.get("user").getAsJsonObject().get("screen_name").getAsString();
    name = obj.get("user").getAsJsonObject().get("name").getAsString();
    profile_image_url = obj.get("user").getAsJsonObject().get("profile_image_url").getAsString();
    createdAt = obj.get("created_at").getAsString();

    try {
      timestamp_ms = (new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH)).parse(createdAt).getTime();
      epoch = timestamp_ms / 1000;
    } catch (ParseException e) {
      LOG.warn(e);
      timestamp_ms = -1L;
      epoch = -1L;
    }

    // TODO: trying to fetch fields and then catching exceptions is bad
    // practice, fix!
    try {
      inReplyToStatusId = obj.get("in_reply_to_status_id").getAsLong();
    } catch (Exception e) {
      LOG.warn(e);
      inReplyToStatusId = -1L;
    }

    try {
      inReplyToUserId = obj.get("in_reply_to_user_id").getAsLong();
    } catch (Exception e) {
      LOG.warn(e);
      inReplyToUserId = -1L;
    }

    try {
      retweetStatusString = obj.get("retweeted_status").getAsString();
      retweetStatusId = obj.getAsJsonObject("retweeted_status").get("id").getAsLong();
      retweetUserId = obj.getAsJsonObject("retweeted_status").get("user").getAsJsonObject().get("id")
          .getAsLong();
      // retweet_count might say "100+"
      // TODO: This is ugly, come back and fix later.
      retweetCount = Integer.parseInt(obj.get("retweet_count").getAsString().replace("+", ""));
    } catch (Exception e) {
      LOG.warn(e);
      retweetStatusId = -1L;
      retweetUserId = -1L;
      retweetCount = -1;
    }

    try {
      inReplyToUserId = obj.get("in_reply_to_user_id").getAsLong();
    } catch (Exception e) {
      LOG.warn(e);
      inReplyToUserId = -1L;
    }

    try {
      latitude = obj.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(1).getAsDouble();
      longitude = obj.getAsJsonObject("coordinates").getAsJsonArray("coordinates").get(0).getAsDouble();
    } catch (Exception e) {
      LOG.warn(e);
      latitude = Double.NEGATIVE_INFINITY;
      longitude = Double.NEGATIVE_INFINITY;
    }

    try {
      lang = obj.get("lang").getAsString();
    } catch (Exception e) {
      LOG.warn(e);
      lang = "unknown";
    }

    followersCount = obj.get("user").getAsJsonObject().get("followers_count").getAsInt();
    friendsCount = obj.get("user").getAsJsonObject().get("friends_count").getAsInt();
    statusesCount = obj.get("user").getAsJsonObject().get("statuses_count").getAsInt();

    jsonObject = obj;
    jsonString = json;

    return this;
  }

  public TweetDocument fromTSV(String tsv) {
    String[] columns = tsv.split("\t");

    if (columns.length < 4) {
      System.err.println("error parsing: " + tsv);
      return null;
    }

    id = columns[0];
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
  public JsonObject getJsonObject() { return jsonObject; }
  public String getJsonString() {
    return jsonString;
  }
  public String getLang() {
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
  public long getInReplyToStatusId() {
    return inReplyToStatusId;
  }
  public long getInReplyToUserId() {
    return inReplyToUserId;
  }
  public double getlatitude() {
    return latitude;
  }
  public double getLongitude() {
    return longitude;
  }
  public long getRetweetedStatusId() {
    return retweetStatusId;
  }
  public long getRetweetedUserId() {
    return retweetUserId;
  }
  public int getRetweetCount() {
    return retweetCount;
  }
  public String getRetweetStatusString() {
    return retweetStatusString;
  }
}
