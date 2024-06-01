package io.anserini.collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.HashMap;

/**
 * A document collection for encoded dense vectors for ANN (HNSW) search using SafeTensors.
 */
public class SafeTensorsDenseVectorCollection extends DocumentCollection<SafeTensorsDenseVectorCollection.SafeTensorsDocument> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorCollection.class);

  public SafeTensorsDenseVectorCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".safetensors", ".json"));
  }

  public SafeTensorsDenseVectorCollection() {
  }

  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.SafeTensorsDocument> createFileSegment(Path p) throws IOException {
    return new SafeTensorsDenseVectorCollection.Segment<>(p);
  }

  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.SafeTensorsDocument> createFileSegment(BufferedReader bufferedReader) throws IOException {
    throw new UnsupportedOperationException("BufferedReader not supported for SafeTensors");
  }

  /**
   * A file in a SafeTensors collection, typically containing multiple documents.
   */
  public static class Segment<T extends SafeTensorsDenseVectorCollection.SafeTensorsDocument> extends FileSegment<T> {
    private boolean hasRead = false;

    public Segment(Path path) throws IOException {
      super(Files.newBufferedReader(path));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void readNext() throws NoSuchElementException {
      if (hasRead) {
        atEOF = true;
        return;
      }
      // No-op: Document creation and reading are handled in the generator
      hasRead = true;
    }

    protected SafeTensorsDenseVectorCollection.SafeTensorsDocument createNewDocument(String id, double[] vector, String raw) {
      return new SafeTensorsDenseVectorCollection.SafeTensorsDocument(id, vector, raw);
    }
  }

  /**
   * A document in a SafeTensors collection.
   */
  public static class SafeTensorsDocument implements SourceDocument {
    private final String id;
    private final String contents;
    private final String raw;
    private final Map<String, String> fields;

    public SafeTensorsDocument(String id, double[] vector, String raw) {
      this.id = id;
      this.contents = serializeContents(vector);
      this.raw = raw;
      this.fields = new HashMap<>();
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return contents;
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }

    public Map<String, String> fields() {
      return fields;
    }

    private static String serializeContents(double[] contents) {
      StringBuilder sb = new StringBuilder();
      for (double value : contents) {
        sb.append(value).append(" ");
      }
      return sb.toString().trim();
    }
  }
}
