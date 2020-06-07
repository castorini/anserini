package io.anserini.index.generator;

import io.anserini.collection.IsoCollection;
import io.anserini.index.IndexArgs;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.NumericDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.tools.bzip2.CBZip2InputStream;

import org.apache.lucene.util.BytesRef;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class IsoGenerator implements LuceneDocumentGenerator<IsoCollection.Document>{
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

  public IsoGenerator(IndexArgs args) {
    this.args = args;
  }

  public Document createDocument(IsoCollection.Document isoDoc) throws GeneratorException {
    String id = isoDoc.id();

    // handling empty doc
    if (isoDoc.contents().trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    // creating the Lucene Document
    Document doc = new Document();

    // adding fields to the document
    doc.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
    doc.add(new StoredField(IsoField.TITLE.name, isoDoc.getTitle()));
    doc.add(new StoredField(IsoField.ABSTRACT.name, isoDoc.getAbstract()));

    if (args.storeRaw) {
      doc.add(new StoredField(IndexArgs.RAW, isoDoc.raw()));
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
    return doc;
  }
}
