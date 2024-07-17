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

public class SafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorDocumentGenerator.class);
  private static final ConcurrentHashMap<String, AtomicBoolean> processedDocuments = new ConcurrentHashMap<>();

  @SuppressWarnings("unused")
  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    String docId = src.id();
    AtomicBoolean alreadyProcessed = processedDocuments.putIfAbsent(docId, new AtomicBoolean(true));

    if (alreadyProcessed != null && alreadyProcessed.get()) {
      LOG.warn("Document ID: " + docId + " is already being processed by another thread.");
      return null;
    }

    try {
      LOG.info("Processing document ID: " + src.id() + " with thread: " + Thread.currentThread().getName());

      float[] contents = parseVectorFromContents(src.contents());
      if (contents == null) {
        throw new InvalidDocumentException();
      }

      LOG.info("Vector length: " + contents.length + " for document ID: " + src.id());

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
      processedDocuments.get(docId).set(false);  // Mark processing as complete
    }
  }

  private float[] parseVectorFromContents(String contents) {
    String[] parts = contents.replace("[", "").replace("]", "").split(",");
    float[] vector = new float[parts.length];
    for (int i = 0; i < parts.length; i++) {
      vector[i] = Float.parseFloat(parts[i].trim());
    }
    return vector;
  }
}
