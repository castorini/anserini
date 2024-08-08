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

/**
 * Collection class for managing SafeTensors dense vectors and corresponding document IDs.
 * Extends the DocumentCollection class for handling documents.
 */
public class SafeTensorsDenseVectorCollection extends DocumentCollection<SafeTensorsDenseVectorCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorCollection.class);
  private String vectorsFilePath;  // Path to the vectors file
  private String docidsFilePath;   // Path to the document IDs file
  public double[][] vectors;       // Array to store vector data
  public String[] docids;          // Array to store document IDs
  private static final ConcurrentHashMap<String, Boolean> processedDocuments = new ConcurrentHashMap<>();  // Track processed documents

  /**
   * Constructor that initializes the collection by reading vector and doc ID data from the specified path.
   * @param path the path to the directory containing the data files.
   * @throws IOException if an I/O error occurs during file reading.
   */
  public SafeTensorsDenseVectorCollection(Path path) throws IOException {
    this.path = path;
    generateFilePaths(path);
    readData();
  }

  /**
   * Default constructor.
   */
  public SafeTensorsDenseVectorCollection() {
    // Default constructor
  }

  /**
   * Creates a file segment for the specified path.
   * @param p the path to the file segment.
   * @return a FileSegment instance.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.Document> createFileSegment(Path p) throws IOException {
    return new SafeTensorsDenseVectorCollection.Segment(p, vectors, docids);
  }

  /**
   * Throws UnsupportedOperationException as BufferedReader is not supported for this collection.
   * @param bufferedReader the BufferedReader instance.
   * @throws UnsupportedOperationException indicating the method is not supported.
   */
  @Override
  public FileSegment<SafeTensorsDenseVectorCollection.Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    throw new UnsupportedOperationException("BufferedReader is not supported for SafeTensorsDenseVectorCollection.");
  }

  /**
   * Generates file paths for vectors and doc IDs files from the input folder.
   * @param inputFolder the directory containing the data files.
   * @throws IOException if an I/O error occurs or files are not found.
   */
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

  /**
   * Reads the data from vectors and doc IDs files.
   * @throws IOException if an I/O error occurs during file reading.
   */
  private void readData() throws IOException {
    vectors = readVectors(vectorsFilePath);
    docids = readDocidAsciiValues(docidsFilePath);
  }

  /**
   * Reads vector data from the specified file path.
   * @param filePath the path to the vectors file.
   * @return a 2D array of vectors.
   * @throws IOException if an I/O error occurs during file reading.
   */
  private double[][] readVectors(String filePath) throws IOException {
    byte[] data = Files.readAllBytes(Paths.get(filePath));
    Map<String, Object> header = parseHeader(data);
    return extractVectors(data, header);
  }

  /**
   * Reads document ID ASCII values from the specified file path.
   * @param filePath the path to the doc IDs file.
   * @return an array of document IDs.
   * @throws IOException if an I/O error occurs during file reading.
   */
  private String[] readDocidAsciiValues(String filePath) throws IOException {
    byte[] data = Files.readAllBytes(Paths.get(filePath));
    Map<String, Object> header = parseHeader(data);
    return extractDocids(data, header);
  }

  /**
   * Parses the header from the byte data.
   * @param data the byte data.
   * @return a map representing the header.
   * @throws IOException if an I/O error occurs during parsing.
   */
  private Map<String, Object> parseHeader(byte[] data) throws IOException {
    ByteBuffer buffer = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN);
    long headerSize = buffer.getLong();
    byte[] headerBytes = new byte[(int) headerSize];
    buffer.get(headerBytes);
    String headerJson = new String(headerBytes, StandardCharsets.UTF_8).trim();
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(headerJson, Map.class);
  }

  /**
   * Extracts vectors from the byte data using the header information.
   * @param data the byte data.
   * @param header the header information.
   * @return a 2D array of vectors.
   */
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

  /**
   * Extracts document IDs from the byte data using the header information.
   * @param data the byte data.
   * @param header the header information.
   * @return an array of document IDs.
   */
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

  /**
   * Inner class representing a file segment for SafeTensorsDenseVectorCollection.
   */
  public static class Segment extends FileSegment<SafeTensorsDenseVectorCollection.Document> {
    private double[][] vectors;
    private String[] docids;
    private int currentIndex;

    /**
     * Constructor for the Segment class.
     * @param path the path to the file segment.
     * @param vectors the vectors data.
     * @param docids the document IDs data.
     * @throws IOException if an I/O error occurs during file reading.
     */
    public Segment(Path path, double[][] vectors, String[] docids) throws IOException {
      super(path);
      this.vectors = vectors;
      this.docids = docids;
      this.currentIndex = 0;
    }

    /**
     * Reads the next document in the segment.
     * @throws IOException if an I/O error occurs during file reading.
     * @throws NoSuchElementException if end of file is reached.
     */
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

  /**
   * Inner class representing a document in the SafeTensorsDenseVectorCollection.
   */
  public static class Document implements SourceDocument {
    private final String id;       // Document ID
    private final double[] vector; // Vector data
    private final String raw;      // Raw data

    /**
     * Constructor for the Document class.
     * @param id the document ID.
     * @param vector the vector data.
     * @param raw the raw data.
     */
    public Document(String id, double[] vector, String raw) {
      this.id = id;
      this.vector = vector;
      this.raw = raw;
    }

    /**
     * Returns the document ID.
     * @return the document ID.
     */
    @Override
    public String id() {
      return id;
    }

    /**
     * Returns the vector contents as a string.
     * @return the vector contents.
     */
    @Override
    public String contents() {
      return Arrays.toString(vector);
    }

    /**
     * Returns the raw data.
     * @return the raw data.
     */
    @Override
    public String raw() {
      return raw;
    }

    /**
     * Indicates whether the document is indexable.
     * @return true if the document is indexable, false otherwise.
     */
    @Override
    public boolean indexable() {
      return true;
    }
  }
}
