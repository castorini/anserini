package io.anserini.collection;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Set;

public class WktCollection extends DocumentCollection<WktCollection.Document> {
  public WktCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".wkt");
  }

  @Override
  public FileSegment<WktCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  public static class Segment extends FileSegment<WktCollection.Document> {
    public Segment(Path path) throws IOException {
      super(path);
      this.bufferedReader = null;
      String fileName = path.toString();
      bufferedReader = new BufferedReader(new FileReader(fileName));
    }

    @Override
    public void readNext() throws IOException, NoSuchElementException {
      String nextRecord = bufferedReader.readLine();
      if (nextRecord == null) {
        throw new NoSuchElementException();
      }
      bufferedRecord = new WktCollection.Document(nextRecord);
    }
  }

  public static class Document implements SourceDocument {
    private String id;
    private String contents;

    public Document(String wkt) {
      this.contents = wkt;
    }

    @Override
    public String id() {
      return "";
    }

    @Override
    public String contents() {
      return contents;
    }

    @Override
    public String raw() {
      return contents;
    }

    @Override
    public boolean indexable() { return true; }
  }
}