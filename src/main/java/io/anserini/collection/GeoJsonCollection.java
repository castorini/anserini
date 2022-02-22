package io.anserini.collection;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

public class GeoJsonCollection extends DocumentCollection<GeoJsonCollection.Document> {

  public GeoJsonCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".json", ".geojson"));
  }

  public static class Document extends MultifieldSourceDocument {
    private String id;
    private String contents;
    private String raw;
    private Map<String, String> fields;

    public Document(JsonNode json) {
      this.raw = json.toPrettyString();

    }
  }
}