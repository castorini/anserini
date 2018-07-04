package io.anserini.document.tweet;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.*;

public class Tweet {
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
  protected Optional<List<OptionalDouble>> coordinates;

  public long getId() {
    return id;
  }

  public String getText() {
    return text;
  }

  public User getUser() {
    return user;
  }

  public String getCreated_at() {
    return created_at;
  }

  public long getRetweet_count() {
    return retweet_count;
  }

  @JsonSetter("retweet_count")
  public void setRetweet_count_internal(JsonNode retweet_count_internal) {
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

  public OptionalLong getIn_reply_to_status_id() {
    return in_reply_to_status_id;
  }

  public OptionalLong getIn_reply_to_user_id() {
    return in_reply_to_user_id;
  }

  public Optional<RetweetedStatus> getRetweeted_status() {
    return retweeted_status;
  }

  public Optional<String> getLang() {
    return lang;
  }

  public Optional<Delete> getDelete() {
    return delete;
  }

  public Optional<List<OptionalDouble>> getCoordinates() {
    return coordinates;
  }
}
