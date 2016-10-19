package io.anserini.rts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TRECTopic {

  @JsonProperty("topid")
  public String topid;
  @JsonProperty("title")
  public String title;
  @JsonProperty("description")
  public String description;
  @JsonProperty("narrative")
  public String narrative;

  @JsonCreator
  public TRECTopic(@JsonProperty("topid") String topid, @JsonProperty("title") String title,
      @JsonProperty("description") String description, @JsonProperty("narrative") String narrative) {
    super();
    this.topid = topid;
    this.title = title;
    this.description = description;
    this.narrative = narrative;
  }
}
