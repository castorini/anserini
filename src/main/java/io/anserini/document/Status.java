package io.anserini.document;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.*;

/**
 * A Twitter document (status) class used in Jackson JSON parser
 */
public class Status {

  // Required fields
  protected String created_at;
  protected String id_str;
  protected String text;
  protected User user;

  // Optional fields
  protected OptionalLong retweet_count;
  protected OptionalLong in_reply_to_status_id;
  protected OptionalLong in_reply_to_user_id;
  protected Optional<RetweetedStatus> retweeted_status;
  protected Optional<String> lang;
  protected Optional<Delete> delete;
  protected Optional<Coordinates> coordinates;

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
    protected Optional<User> user;

    @JsonGetter("id")
    public OptionalLong id() {
      return id;
    }

    @JsonGetter("user")
    public Optional<User> user() {
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
  public Status(
    @JsonProperty(value = "created_at", required = true) String created_at,
    @JsonProperty(value = "id_str", required = true) String id_str,
    @JsonProperty(value = "text", required = true) String text,
    @JsonProperty(value = "user", required = true) User user) {
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
  public User user() {
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
}
