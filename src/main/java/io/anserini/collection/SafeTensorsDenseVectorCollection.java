package io.anserini.collection;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SafeTensorsDenseVectorCollection extends DocumentCollection<SafeTensorsDenseVectorCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorCollection.class);
  private String vectorsFilePath;
  private String docidsFilePath;
  public double[][] vectors;
  public String[] docids;
  private static final ConcurrentHashMap<String, Boolean> processedDocuments = new ConcurrentHashMap<>();

  public SafeTensorsDenseVectorCollection(Path path) throws IOException {
    this.path = path;
    generateFilePaths(path);
    readData();
  }

  public SafeTensorsDenseVectorCollection() {
    // Default constructor
  }

  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.Document> createFileSegment(Path p) throws IOException {
    return new SafeTensorsDenseVectorCollection.Segment(p, vectors, docids);
  }

  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    throw new UnsupportedOperationException("BufferedReader is not supported for SafeTensorsDenseVectorCollection.");
  }

  private void generateFilePaths(Path inputFolder) throws IOException {
    List<Path> files;
    try (Stream<Path> stream = Files.list(inputFolder)) {
      files = stream.collect(Collectors.toList());
    }

    vectorsFilePath = files.stream()
        .filter(file -> file.toString().contains("_vectors.safetensors"))
        .map(Path::toString)
        .findFirst()
        .orElseThrow(() -> new IOException("No vectors file found in the directory " + inputFolder));

    docidsFilePath = files.stream()
        .filter(file -> file.toString().contains("_docids.safetensors"))
        .map(Path::toString)
        .findFirst()
        .orElseThrow(() -> new IOException("No docids file found in the directory " + inputFolder));
  }

  private void readData() throws IOException {
    vectors = readVectors(vectorsFilePath);
    docids = readDocidAsciiValues(docidsFilePath);
  }

  private double[][] readVectors(String filePath) throws IOException {
    byte[] data = Files.readAllBytes(Paths.get(filePath));
    Map<String, Object> header = parseHeader(data);
    return extractVectors(data, header);
  }

  private String[] readDocidAsciiValues(String filePath) throws IOException {
    byte[] data = Files.readAllBytes(Paths.get(filePath));
    Map<String, Object> header = parseHeader(data);
    return extractDocids(data, header);
  }

  private Map<String, Object> parseHeader(byte[] data) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    long headerSize = buffer.getLong();
    byte[] headerBytes = new byte[(int) headerSize];
    buffer.get(headerBytes);
    String headerJson = new String(headerBytes, StandardCharsets.UTF_8).trim();
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(headerJson, Map.class);
  }

  private double[][] extractVectors(byte[] data, Map<String, Object> header) {
    Map<String, Object> vectorsInfo = (Map<String, Object>) header.get("vectors");
    String dtype = (String) vectorsInfo.get("dtype");

    List<Integer> shapeList = (List<Integer>) vectorsInfo.get("shape");
    int rows = shapeList.get(0);
    int cols = shapeList.get(1);
    List<Number> dataOffsets = (List<Number>) vectorsInfo.get("data_offsets");
    long begin = dataOffsets.get(0).longValue();
    long end = dataOffsets.get(1).longValue();

    ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    buffer.position((int) (begin + buffer.getLong(0) + 8));

    double[][] vectors = new double[rows][cols];
    if (dtype.equals("F64")) {
      for (int i = 0; i < rows; i++) {
        for (int j = 0; j < cols; j++) {
          vectors[i][j] = buffer.getDouble();
        }
      }
    } else {
      throw new UnsupportedOperationException("Unsupported data type: " + dtype);
    }

    return vectors;
  }

  private String[] extractDocids(byte[] data, Map<String, Object> header) {
    Map<String, Object> docidsInfo = (Map<String, Object>) header.get("docids");
    String dtype = (String) docidsInfo.get("dtype");

    List<Integer> shapeList = (List<Integer>) docidsInfo.get("shape");
    int length = shapeList.get(0);
    int maxCols = shapeList.get(1);

    List<Number> dataOffsets = (List<Number>) docidsInfo.get("data_offsets");
    long begin = dataOffsets.get(0).longValue();
    long end = dataOffsets.get(1).longValue();

    ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    buffer.position((int) (begin + buffer.getLong(0) + 8));

    String[] docids = new String[length];
    StringBuilder sb = new StringBuilder();
    if (dtype.equals("I64")) {
      for (int i = 0; i < length; i++) {
        sb.setLength(0);
        for (int j = 0; j < maxCols; j++) {
          char c = (char) buffer.getLong();
          if (c != 0)
            sb.append(c);
        }
        docids[i] = sb.toString();
      }
    } else {
      throw new UnsupportedOperationException("Unsupported data type: " + dtype);
    }

    return docids;
  }

  public static class Segment extends FileSegment<SafeTensorsDenseVectorCollection.Document> {
    private double[][] vectors;
    private String[] docids;
    private int currentIndex;

    public Segment(Path path, double[][] vectors, String[] docids) throws IOException {
      super(path);
      this.vectors = vectors;
      this.docids = docids;
      this.currentIndex = 0;
    }

    @Override
    protected synchronized void readNext() throws IOException, NoSuchElementException {
      if (currentIndex >= docids.length) {
        atEOF = true;
        throw new NoSuchElementException("End of file reached");
      }

      String id = docids[currentIndex];
      double[] vector = vectors[currentIndex];
      bufferedRecord = new SafeTensorsDenseVectorCollection.Document(id, vector, "");
      currentIndex++;
    }
  }

  public static class Document implements SourceDocument {
    private final String id;
    private final double[] vector;
    private final String raw;

    public Document(String id, double[] vector, String raw) {
      this.id = id;
      this.vector = vector;
      this.raw = raw;
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String contents() {
      return Arrays.toString(vector);
    }

    @Override
    public String raw() {
      return raw;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
