package io.anserini.index.generator;

import io.anserini.document.SourceDocument;
import io.anserini.index.IndexCollection;
import io.anserini.index.transform.StringTransformation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;

public class LuceneDocumentGenerator<T extends SourceDocument> {
  private static final Logger LOG = LogManager.getLogger(LuceneDocumentGenerator.class);

  public static final String FIELD_RAW = "raw";
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";

  private final StringTransformation transform;

  protected IndexCollection.Counters counters;
  protected IndexCollection.Args args;

  public LuceneDocumentGenerator() {
    this.transform = null;
  }

  public LuceneDocumentGenerator(StringTransformation transform) {
    this.transform = transform;
  }

  public void config(IndexCollection.Args args) {
    this.args = args;
  }

  public void setCounters(IndexCollection.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(SourceDocument src) {
    String id = src.id();
    String contents;

    try {
      contents = transform != null ? transform.apply(src.content()) : src.content();
    } catch (Exception e) {
      LOG.error("Error extracting document text, skipping document: " + id, e);
      counters.errors.incrementAndGet();
      return null;
    }

    if (contents.trim().length() == 0) {
      LOG.info("Empty document: " + id);
      counters.emptyDocuments.incrementAndGet();
      return null;
    }

    // make a new, empty document
    Document document = new Document();

    // document id
    document.add(new StringField(FIELD_ID, id, Field.Store.YES));

    if (args.storeRawDocs) {
      document.add(new StoredField(FIELD_RAW, src.content()));
    }

    FieldType fieldType = new FieldType();

    fieldType.setStored(args.storeTransformedDocs);

    // Are we storing document vectors?
    if (args.storeDocvectors) {
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    // Are we building a "positional" or "count" index?
    if (args.storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }

    document.add(new Field(FIELD_BODY, contents, fieldType));

    return document;
  }
}
