package io.anserini.index.generator;

import io.anserini.collection.SourceDocument;
import io.anserini.collection.SafeTensorsDenseVectorCollection;
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

import java.util.Arrays;

public class SafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorDocumentGenerator.class);
  private SafeTensorsDenseVectorCollection collection;

  public SafeTensorsDenseVectorDocumentGenerator(SafeTensorsDenseVectorCollection collection) {
    this.collection = collection;
  }

  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    try {
      LOG.info("Processing document ID: " + src.id());
      float[] contents = getVectorForDocId(src.id());

      if (contents == null) {
        throw new InvalidDocumentException();
      }

      final Document document = new Document();
      document.add(new StringField(Constants.ID, src.id(), Field.Store.YES));
      document.add(new BinaryDocValuesField(Constants.ID, new BytesRef(src.id())));
      document.add(new KnnFloatVectorField(Constants.VECTOR, contents, VectorSimilarityFunction.DOT_PRODUCT));

      return document;
    } catch (Exception e) {
      LOG.error("Error creating document", e);
      throw new InvalidDocumentException();
    }
  }

  private float[] getVectorForDocId(String docId) {
    int index = Arrays.asList(collection.docids).indexOf(docId);
    if (index == -1) {
      return null;
    }
    float[] vector = new float[collection.vectors[index].length];
    for (int i = 0; i < vector.length; i++) {
      vector[i] = (float) collection.vectors[index][i];
    }
    return vector;
  }
}
