package io.anserini.document;

import io.anserini.index.MultithreadedIndexer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.jsoup.Jsoup;

public class JsoupTransformer implements SourceDocumentTransformer<SourceDocument> {
  private static final Logger LOG = LogManager.getLogger(JsoupTransformer.class);

  public static final String FIELD_BODY = "contents";
  public static final String FIELD_ID = "id";

  private boolean keepStopwords = false;
  private boolean storePositions = false;
  private boolean storeDocVectors = false;
  private MultithreadedIndexer.Counters counters;

  public void setKeepStopwords(boolean keepStopwords) {
    this.keepStopwords = keepStopwords;
  }

  public void setStorePositions(boolean storePositions) {
    this.storePositions = storePositions;
  }

  public void setStoreDocVectors(boolean storeDocVectors) {
    this.storeDocVectors = storeDocVectors;
  }

  public void setCounters(MultithreadedIndexer.Counters counters) {
    this.counters = counters;
  }

  @Override
  public Document transform(SourceDocument src) {
    String id = src.id();
    String contents;

// TODO: We should try out the Lucene demo parser also.
//    if (useLucene) {
//      DemoHTMLParser dhp = new DemoHTMLParser();
//      DocData dd = new DocData();
//      dd = dhp.parse(dd, "", null, new StringReader(raw), null);
//      contents = dd.getTitle() + "\n" + dd.getBody();
//    } else {
//      org.jsoup.nodes.Document jDoc = Jsoup.parse(raw);
//      contents = jDoc.text();
//    }

    try {
      org.jsoup.nodes.Document jDoc = Jsoup.parse(src.content());
      contents = jDoc.text();
    } catch (Exception e) {
      LOG.error("Parsing document with JSoup failed, skipping document: " + id, e);
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

    FieldType fieldType = new FieldType();

    // Are we storing document vectors?
    if (storeDocVectors) {
      fieldType.setStored(false);
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    // Are we building a "positional" or "count" index?
    if (storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }

    document.add(new Field(FIELD_BODY, contents, fieldType));

    return document;
  }
}
