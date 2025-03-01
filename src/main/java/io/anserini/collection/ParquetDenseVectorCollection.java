/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.collection;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.schema.PrimitiveType;

/**
 * Collection class for managing Parquet dense vectors
 * Extends the DocumentCollection class for handling documents.
 */
public class ParquetDenseVectorCollection extends DocumentCollection<ParquetDenseVectorCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(ParquetDenseVectorCollection.class);

  protected String docidField = "docid";
  protected String vectorField = "vector";
  protected boolean normalizeVectors = false;

  /**
   * Constructor that initializes the collection by reading vector and doc ID data
   * from the specified path.
   * 
   * @param path the path to the directory containing the data files.
   * @throws IOException if an I/O error occurs during file reading.
   */
  public ParquetDenseVectorCollection(Path path) throws IOException {
    this.path = path;
  }

  /**
   * Constructor that initializes the collection with custom field names.
   * 
   * @param path             the path to the directory containing the data files.
   * @param docidField       the field name for document IDs.
   * @param vectorField      the field name for vector data.
   * @param normalizeVectors whether to normalize the vectors.
   * @throws IOException if an I/O error occurs during file reading.
   */
  public ParquetDenseVectorCollection(Path path, String docidField, String vectorField, boolean normalizeVectors)
      throws IOException {
    this.path = path;
    this.docidField = docidField;
    this.vectorField = vectorField;
    this.normalizeVectors = normalizeVectors;
  }

  /**
   * Default constructor.
   */
  public ParquetDenseVectorCollection() {
  }

  /**
   * Set the document ID field name.
   * 
   * @param docidField the field name for document IDs.
   * @return this collection instance for chaining.
   */
  public ParquetDenseVectorCollection withDocidField(String docidField) {
    this.docidField = docidField;
    return this;
  }

  /**
   * Set the vector field name.
   * 
   * @param vectorField the field name for vector data.
   * @return this collection instance for chaining.
   */
  public ParquetDenseVectorCollection withVectorField(String vectorField) {
    this.vectorField = vectorField;
    return this;
  }

  /**
   * Set whether to normalize vectors.
   * 
   * @param normalizeVectors whether to normalize the vectors.
   * @return this collection instance for chaining.
   */
  public ParquetDenseVectorCollection withNormalizeVectors(boolean normalizeVectors) {
    this.normalizeVectors = normalizeVectors;
    return this;
  }

  /**
   * Creates a file segment for the specified path.
   * 
   * @param p the path to the file segment.
   * @return a FileSegment instance.
   * @throws IOException if an I/O error occurs.
   */
  @Override
  public FileSegment<ParquetDenseVectorCollection.Document> createFileSegment(Path p) throws IOException {
    return new ParquetDenseVectorCollection.Segment(p, docidField, vectorField, normalizeVectors);
  }

  /**
   * Throws UnsupportedOperationException as BufferedReader is not supported for
   * this collection.
   * 
   * @param bufferedReader the BufferedReader instance.
   * @throws UnsupportedOperationException indicating the method is not supported.
   */
  @Override
  public FileSegment<ParquetDenseVectorCollection.Document> createFileSegment(BufferedReader bufferedReader)
      throws IOException {
    throw new UnsupportedOperationException("BufferedReader is not supported for ParquetDenseVectorCollection.");
  }

  /**
   * Inner class representing a file segment for ParquetDenseVectorCollection.
   */
  public static class Segment extends FileSegment<ParquetDenseVectorCollection.Document> {
    private List<float[]> vectors; // List to store vectors from the Parquet file
    private List<String> ids; // List to store document IDs
    private ParquetReader<Group> reader;
    private boolean readerInitialized;
    private String docidField;
    private String vectorField;
    private boolean normalizeVectors;

    /**
     * Constructor for the Segment class using a file path.
     *
     * @param path the path to the file segment.
     * @throws IOException if an I/O error occurs during file reading.
     */
    public Segment(java.nio.file.Path path) throws IOException {
      this(path, "docid", "vector", false);
    }

    /**
     * Constructor for the Segment class using a file path with custom field names.
     *
     * @param path             the path to the file segment.
     * @param docidField       the field name for document IDs.
     * @param vectorField      the field name for vector data.
     * @param normalizeVectors whether to normalize the vectors.
     * @throws IOException if an I/O error occurs during file reading.
     */
    public Segment(java.nio.file.Path path, String docidField, String vectorField, boolean normalizeVectors)
        throws IOException {
      super(path);
      this.docidField = docidField;
      this.vectorField = vectorField;
      this.normalizeVectors = normalizeVectors;
      initializeParquetReader(path);
    }

    /**
     * Constructor for the Segment class using a BufferedReader.
     * 
     * This constructor might be used if you want to read from a different input
     * source instead of a file path. For Parquet files, we generally use the
     * file path constructor.
     *
     * @param bufferedReader the BufferedReader to read the file segment.
     */
    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
      throw new IOException("Not Implemented");
    }

    /**
     * Initializes the Parquet reader and loads data into memory.
     *
     * @param path the path to the Parquet file.
     * @throws IOException if an I/O error occurs during file reading.
     */
    private void initializeParquetReader(java.nio.file.Path path) throws IOException {
      // Convert the Java Path object to a Hadoop Path object using fully qualified
      // name
      org.apache.hadoop.fs.Path hadoopPath = new org.apache.hadoop.fs.Path(path.toString());

      reader = ParquetReader.builder(new GroupReadSupport(), hadoopPath).build();

      // Initialize lists to store data read from the Parquet file
      vectors = new ArrayList<>();
      ids = new ArrayList<>();
      readerInitialized = true;
    }

    /**
     * @param vector the vector to normalize.
     * @return the normalized vector.
     */
    private float[] normalizeVector(float[] vector) {
      float squaredSum = 0.0f;

      for (float value : vector) {
        squaredSum += value * value;
      }


      float norm = (float) Math.sqrt(squaredSum);
      float[] normalizedVector = new float[vector.length];

      for (int i = 0; i < vector.length; i++) {
        normalizedVector[i] = vector[i] / norm;
      }

      return normalizedVector;
    }

    /**
     * Reads the next document in the segment.
     *
     * @throws IOException            if an I/O error occurs during file reading.
     * @throws NoSuchElementException if end of file is reached.
     */
    @Override
    protected synchronized void readNext() throws IOException, NoSuchElementException {
      // Check if we have reached the end of the list
      if(atEOF || !readerInitialized){
        throw new NoSuchElementException("End of file reached");
      }
      Group record = reader.read();
      if (record == null) {
        atEOF = true;
        reader.close();
        readerInitialized = false;
        throw new NoSuchElementException("End of file reached");
      }

      String docid = record.getString(this.docidField, 0);
      ids.add(docid);

      Group vectorGroup = record.getGroup(this.vectorField, 0);
      int vectorSize = vectorGroup.getFieldRepetitionCount(0);
      float[] vector = new float[vectorSize];
      
      Group firstElement = vectorGroup.getGroup(0, 0);
      PrimitiveType.PrimitiveTypeName primitiveType = firstElement.getType().getFields().get(0).asPrimitiveType().getPrimitiveTypeName();
      boolean isDouble = primitiveType.equals(PrimitiveType.PrimitiveTypeName.DOUBLE);
      boolean isFloat = primitiveType.equals(PrimitiveType.PrimitiveTypeName.FLOAT);
      
      if (!isDouble && !isFloat) {
        throw new IllegalArgumentException(String.format("Vector elements must be either DOUBLE or FLOAT, found: %s", primitiveType));
      }

      for (int i = 0; i < vectorSize; i++) {
        Group listGroup = vectorGroup.getGroup(0, i);
        vector[i] = isDouble ? (float) listGroup.getDouble("element", 0) : listGroup.getFloat("element", 0);
      }
      

      if (this.normalizeVectors) {
        vector = normalizeVector(vector);
      }

      vectors.add(vector);

      // Create a new Document object with the retrieved data
      bufferedRecord = new ParquetDenseVectorCollection.Document(docid, vector, "");
    }
  }

  /**
   * Inner class representing a document in the ParquetDenseVectorCollection.
   */
  public static class Document implements SourceDocument {
    private final String id;
    private final float[] vector;
    private final String raw;

    /**
     * Constructor for the Document class.
     * 
     * @param id     the document ID.
     * @param vector the vector data.
     * @param raw    the raw data.
     */
    public Document(String id, float[] vector, String raw) {
      this.id = id;
      this.vector = vector;
      this.raw = raw;
    }

    /**
     * Returns the document ID.
     * 
     * @return the document ID.
     */
    @Override
    public String id() {
      return id;
    }

    /**
     * Returns the vector contents as a string.
     * 
     * @return the vector contents.
     */
    @Override
    public String contents() {
      return Arrays.toString(vector);
    }

    /**
     * Returns the raw data.
     * 
     * @return the raw data.
     */
    @Override
    public String raw() {
      return raw;
    }

    /**
     * Indicates whether the document is indexable.
     * 
     * @return true if the document is indexable, false otherwise.
     */
    @Override
    public boolean indexable() {
      return true;
    }

    @Override
    public float[] vector() {
      return vector;
    }
  }
}
