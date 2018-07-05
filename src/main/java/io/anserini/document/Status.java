package io.anserini.document;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.*;

/**
 * A Twitter document (status) class used in Jackson JSON parser
 */
public class Status {

  protected long id;
  protected String text;
  protected User user;
  protected String created_at;
  protected long retweet_count;

  protected OptionalLong in_reply_to_status_id;
  protected OptionalLong in_reply_to_user_id;
  protected Optional<RetweetedStatus> retweeted_status;
  protected Optional<String> lang;
  protected Optional<Delete> delete;
  protected Optional<Coordinates> coordinates;

  // Must make inner classes static for deserialization in Jackson
  // http://www.cowtowncoder.com/blog/archives/2010/08/entry_411.html
  public static class Delete {
    protected String timestamp_ms;

    @JsonGetter("timestamp_ms")
    public String timestamp_ms() {
      return timestamp_ms;
    }
  }

  public static class Coordinates {
    protected List<OptionalDouble> coordinates;

    @JsonGetter("coordinates")
    public List<OptionalDouble> coordinates() { return coordinates; }
  }

  public static class RetweetedStatus {
    protected long id;
    protected User user;

    @JsonGetter("id")
    public long id() {
      return id;
    }

    @JsonGetter("user")
    public User user() {
      return user;
    }
  }

  public static class User {
    protected String screen_name;
    protected String name;
    protected String profile_image_url;
    protected long id;
    protected int followers_count;
    protected int friends_count;
    protected int statuses_count;

    @JsonGetter("screen_name")
    public String screen_name() {
      return screen_name;
    }

    @JsonGetter("name")
    public String name() {
      return name;
    }

    @JsonGetter("profile_image_url")
    public String profile_image_url() {
      return profile_image_url;
    }

    @JsonGetter("id")
    public long id() {
      return id;
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
  }

  @JsonGetter("id")
  public long id() {
    return id;
  }

  @JsonGetter("text")
  public String text() {
    return text;
  }

  @JsonGetter("user")
  public User user() {
    return user;
  }

  @JsonGetter("created_at")
  public String created_at() {
    return created_at;
  }

  @JsonGetter("retweet_count")
  public long retweet_count() {
    return retweet_count;
  }

  @JsonSetter("retweet_count")
  public void set_retweet_count_internal(JsonNode retweet_count_internal) {
    if (retweet_count_internal != null) {
      if (retweet_count_internal.isTextual()) {
        // retweet_count might say "100+"
        // TODO: This is ugly, come back and fix later.
        retweet_count = Long.parseLong(retweet_count_internal.asText().replace("+", ""));
      } else if (retweet_count_internal.isNumber()) {
        retweet_count = retweet_count_internal.asLong();
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
  public Optional<RetweetedStatus> retweeted_status() {
    return retweeted_status;
  }

  @JsonGetter("lang")
  public Optional<String> lang() {
    return lang;
  }

  @JsonGetter("delete")
  public Optional<Delete> delete() {
    return delete;
  }

  @JsonGetter("coordinates")
  public Optional<Coordinates> coordinates() { return coordinates; }

//  @JsonSetter("coordinates")
//  public void set_coordinates(List<JsonNode> coordinates) {
//    if (coordinates != null) {
//      if (retweet_count_internal.isTextual()) {
//        // retweet_count might say "100+"
//        // TODO: This is ugly, come back and fix later.
//        retweet_count = Long.parseLong(retweet_count_internal.asText().replace("+", ""));
//      } else if (retweet_count_internal.isNumber()) {
//        retweet_count = retweet_count_internal.asLong();
//      }
//    }
//  }
}
