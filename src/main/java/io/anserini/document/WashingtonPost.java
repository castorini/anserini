package io.anserini.document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;

public class WashingtonPost {
  // Required fields
  protected String id;
  protected long published_date;
  protected String content;

  // Optional fields
  protected Optional<List<Content>> contents;

  public static class Content {
    protected Optional<String> type;
    protected Optional<String> content;

    @JsonGetter("type")
    public Optional<String> type() {
      return type;
    }

    @JsonGetter("content")
    public Optional<String> content() {
      return content;
    }
  }

  @JsonGetter("id")
  public String id() {
    return id;
  }

  @JsonGetter("published_date")
  public long published_date() {
    return published_date;
  }

  @JsonGetter("contents")
  public Optional<List<Content>> contents() { return contents; }

  @JsonCreator
  public WashingtonPost(
          @JsonProperty(value = "id", required = true) String id,
          @JsonProperty(value = "published_date", required = true) long published_date) {
    this.id = id;
    this.published_date = published_date;
  }

}
