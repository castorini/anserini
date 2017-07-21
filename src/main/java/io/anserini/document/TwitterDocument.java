package io.anserini.document;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

/**
 * A Twitter document (status).
 */
public class TwitterDocument implements SourceDocument {
  private String id;
  private String contents;

  private static final Logger LOG = LogManager.getLogger(io.anserini.document.twitter.Status.class);
  private static final JsonParser JSON_PARSER = new JsonParser();

  public SourceDocument readNextRecord(String json, boolean keepRetweets) throws IOException {
    JsonObject obj = null;
    try {
      obj = (JsonObject) JSON_PARSER.parse(json);
    } catch (Exception e) {
      // Catch any malformed JSON.
      LOG.error("Error parsing: " + json);
      return null;
    }

    if (obj.get("text") == null) {
      return null;
    }

    if (!keepRetweets) {
      try {
        long retweetStatusID = obj.getAsJsonObject("retweeted_status").get("id").getAsLong();
        return null;
      } catch (Exception e) {
        // retweeted_status key doesn't exist and therefore not a retweet
      }
    }

    contents = obj.get("text").getAsString();
    id = obj.get("id").getAsString();
    return this;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String content() {
    return contents;
  }

  @Override
  public boolean indexable() {
    return true;
  }
}
