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

public class SafeTensorsDenseVectorDocumentGenerator<T extends SourceDocument> implements LuceneDocumentGenerator<T> {
  private static final Logger LOG = LogManager.getLogger(SafeTensorsDenseVectorDocumentGenerator.class);

  @Override
  public Document createDocument(T src) throws InvalidDocumentException {
    try {
      LOG.info("Processing document ID: " + src.id());

      // Assuming src.contents() returns the vector data as a float array or a string that can be parsed
      float[] contents = parseVectorFromContents(src.contents());

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

  private float[] parseVectorFromContents(String contents) {
    // Implement the logic to parse the vector from the contents string
    // This is just a placeholder implementation
    // Replace with actual logic to convert contents to a float array
    String[] parts = contents.replace("[", "").replace("]", "").split(",");
    float[] vector = new float[parts.length];
    for (int i = 0; i < parts.length; i++) {
      vector[i] = Float.parseFloat(parts[i].trim());
    }
    return vector;
  }
}
