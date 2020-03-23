package io.anserini.collection;

import java.io.IOException;
import java.nio.file.Path;

public class CleanTrecCollection extends TrecCollection {
  public CleanTrecCollection(Path path) {
    super(path);
  }

  @Override
  @SuppressWarnings("unchecked")
  public FileSegment<TrecCollection.Document> createFileSegment(Path p) throws IOException {
    return (FileSegment<TrecCollection.Document>) new Segment(p);
  }

  public static class Segment extends TrecCollection.Segment {
    public Segment(Path path) throws IOException {
      super(path);
    }

    @Override
    protected TrecCollection.Document createNewDocument() {
      return new Document();
    }
  }

  public static class Document extends TrecCollection.Document {
    @Override
    public String content() {
      return raw;
    }
  }
}
