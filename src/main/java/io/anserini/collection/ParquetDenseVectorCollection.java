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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.parquet.example.data.Group;
import org.apache.parquet.example.data.simple.SimpleGroup;
import org.apache.parquet.example.data.simple.convert.GroupRecordConverter;
import org.apache.parquet.hadoop.ParquetReader;
import org.apache.parquet.hadoop.example.GroupReadSupport;
import org.apache.parquet.io.InputFile;
import org.apache.parquet.hadoop.util.HadoopInputFile;

import java.util.ArrayList;

/**
 * Collection class for managing Parquet dense vectors
 * Extends the DocumentCollection class for handling documents.
 */
public class ParquetDenseVectorCollection extends DocumentCollection<ParquetDenseVectorCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(ParquetDenseVectorCollection.class);

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
   * Default constructor.
   */
  public ParquetDenseVectorCollection() {
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
    return new ParquetDenseVectorCollection.Segment(p);
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
    private List<double[]> vectors; // List to store vectors from the Parquet file
    private List<String> ids; // List to store document IDs
    private List<String> contents; // List to store contents of the documents
    private int currentIndex; // Current index for iteration

    /**
     * Constructor for the Segment class using a file path.
     *
     * @param path the path to the file segment.
     * @throws IOException if an I/O error occurs during file reading.
     */
    public Segment(java.nio.file.Path path) throws IOException {
      super(path);
      initializeParquetReader(path); // Initialize the Parquet reader and load data
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
    public Segment(BufferedReader bufferedReader) {
      super(bufferedReader);
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

      // Create a ParquetReader with GroupReadSupport to read Group objects
      ParquetReader<Group> reader = ParquetReader.builder(new GroupReadSupport(), hadoopPath).build();

      // Initialize lists to store data read from the Parquet file
      vectors = new ArrayList<>();
      ids = new ArrayList<>();
      contents = new ArrayList<>();

      Group record;
      // Read each record from the Parquet file
      while ((record = reader.read()) != null) {
        // Extract the docid (String) from the record
        String docid = record.getString("docid", 0);
        ids.add(docid); // Add to the list of IDs

        // Extract the contents (String) from the record
        String content = record.getString("contents", 0);
        contents.add(content); // Add to the list of contents

        // Extract the vector (double[]) from the record
        int vectorSize = record.getFieldRepetitionCount("vector");
        double[] vector = new double[vectorSize];
        for (int i = 0; i < vectorSize; i++) {
          vector[i] = record.getDouble("vector", i);
        }
        vectors.add(vector); // Add to the list of vectors
      }

      reader.close(); // Close the reader
      currentIndex = 0; // Start iterating from the beginning
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
      if (currentIndex >= ids.size()) {
        atEOF = true; // Set the end-of-file flag
        throw new NoSuchElementException("End of file reached"); // Throw exception to signal end of data
      }

      // Get the current document's ID, contents, and vector
      String id = ids.get(currentIndex);
      String content = contents.get(currentIndex);
      double[] vector = vectors.get(currentIndex);

      // Create a new Document object with the retrieved data
      bufferedRecord = new ParquetDenseVectorCollection.Document(id, vector, content);

      // Move to the next document
      currentIndex++;
    }
  }

  /**
   * Inner class representing a document in the ParquetDenseVectorCollection.
   */
  public static class Document implements SourceDocument {
    private final String id; // Document ID
    private final double[] vector; // Vector data
    private final String raw; // Raw data

    /**
     * Constructor for the Document class.
     * 
     * @param id     the document ID.
     * @param vector the vector data.
     * @param raw    the raw data.
     */
    public Document(String id, double[] vector, String raw) {
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
  }

}
