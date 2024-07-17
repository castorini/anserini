package io.anserini.index.generator;

import io.anserini.collection.SourceDocument;
import io.anserini.index.Constants;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.KnnFloatVectorField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.VectorSimilarityFunction;
import org.apache.lucene.util.BytesRef;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A document generator for creating Lucene documents with SafeTensors dense vector data.
 * Implements the LuceneDocumentGenerator interface.
 * 
 * @param <T> the type of SourceDocument
 */
public class SafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorDocumentGenerator.class);
  private static final ConcurrentHashMap<String, AtomicBoolean> processedDocuments = new ConcurrentHashMap<>();  // Track processed documents

  /**
   * Creates a Lucene document from the source document.
   * 
   * @param src the source document
   * @return the created Lucene document
   * @throws InvalidDocumentException if the document is invalid
   */
  @SuppressWarnings("unused")
  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    String docId = src.id();
    AtomicBoolean alreadyProcessed = processedDocuments.putIfAbsent(docId, new AtomicBoolean(true));

    // Check if the document is already being processed by another thread
    if (alreadyProcessed != null && alreadyProcessed.get()) {
      LOG.warn("Document ID: " + docId + " is already being processed by another thread.");
      return null;
    }

    try {
      LOG.info("Processing document ID: " + src.id() + " with thread: " + Thread.currentThread().getName());

      // Parse vector data from document contents
      float[] contents = parseVectorFromContents(src.contents());
      if (contents == null) {
        throw new InvalidDocumentException();
      }

      LOG.info("Vector length: " + contents.length + " for document ID: " + src.id());

      // Create and populate the Lucene document
      final Document document = new Document();
      document.add(new StringField(Constants.ID, src.id(), Field.Store.YES));
      document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(src.id())));
      document.add(new KnnFloatVectorField(Constants.VECTOR, contents, VectorSimilarityFunction.DOT_PRODUCT));

      LOG.info("Document created for ID: " + src.id());

      return document;
    } catch (Exception e) {
      LOG.error("Error creating document for ID: " + src.id(), e);
      throw new InvalidDocumentException();
    } finally {
      // Mark processing as complete
      processedDocuments.get(docId).set(false);
    }
  }

  /**
   * Parses the vector data from the document contents.
   * 
   * @param contents the contents of the document
   * @return the parsed vector as an array of floats
   */
  private float[] parseVectorFromContents(String contents) {
    String[] parts = contents.replace("[", "").replace("]", "").split(",");
    float[] vector = new float[parts.length];
    for (int i = 0; i < parts.length; i++) {
      vector[i] = Float.parseFloat(parts[i].trim());
    }
    return vector;
  }
}
