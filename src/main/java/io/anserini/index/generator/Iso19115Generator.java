package io.anserini.index.generator;

import io.anserini.collection.Iso19115Collection;
import io.anserini.index.IndexArgs;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;

import org.apache.lucene.util.BytesRef;

public class Iso19115Generator implements LuceneDocumentGenerator<Iso19115Collection.Document>{
  protected IndexArgs args;

  // constants for storing
  public enum IsoField {
    ID("id"),
    TITLE("title"),
    ABSTRACT("abstract");

    public final String name;

    IsoField(String s) {
        name = s;
    }
  }

  public Iso19115Generator(IndexArgs args) {
    this.args = args;
  }

  public Document createDocument(Iso19115Collection.Document doc) throws GeneratorException {
    String id = doc.id();

    // handling empty doc
    if (doc.contents().trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    // creating the Lucene Document
    Document document = new Document();

    // adding fields to the document
    document.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    document.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
    document.add(new StoredField(IsoField.TITLE.name, doc.getTitle()));
    document.add(new StoredField(IsoField.ABSTRACT.name, doc.getAbstract()));

    if (args.storeRaw) {
      document.add(new StoredField(IndexArgs.RAW, doc.raw()));
    }

    FieldType fieldType = new FieldType();

    fieldType.setStored(args.storeContents);
    if (args.storeDocvectors) {
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    if (args.storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }
    return document;
  }
}
