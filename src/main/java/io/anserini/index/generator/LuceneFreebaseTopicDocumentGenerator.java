package io.anserini.index.generator;

import io.anserini.document.FreebaseTopicDocument;
import io.anserini.document.SourceDocument;
import io.anserini.index.IndexFreebaseTopicCollection;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

/**
 * Converts a {@link FreebaseTopicDocument} into
 * a Lucene {@link Document}.
 */
public class LuceneFreebaseTopicDocumentGenerator {

  /**
   * FreebaseTopicDocument has four fields:
   * topicMid - the MID of the topic
   * title - the object value of the (topicMid, http://rdf.freebase.com/key/wikipedia.en_title)
   * label - title - the object value of the (topicMid, http://www.w3.org/2000/01/rdf-schema#label)
   * text - all the values separated by space of the (topicMid, http://rdf.freebase.com/key/wikipedia.en)
   */
  public static final String FIELD_TOPIC_MID = "topicMid";
  public static final String FIELD_TITLE = "title";
  public static final String FIELD_LABEL = "label";
  public static final String FIELD_TEXT = "text";


  protected IndexFreebaseTopicCollection.Counters counters;
  protected IndexFreebaseTopicCollection.Args args;

  public void config(IndexFreebaseTopicCollection.Args args) {
    this.args = args;
  }

  public void setCounters(IndexFreebaseTopicCollection.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(SourceDocument src) {
    if (!(src instanceof FreebaseTopicDocument)) {
      throw new IllegalArgumentException("Cannot create FreebaseTopic document from source document of type: " + src.getClass().getSimpleName());
    }

    FreebaseTopicDocument tripleDoc = (FreebaseTopicDocument) src;

    // Convert the triple doc to lucene doc
    Document doc = new Document();

    // Index subject as a StringField to allow searching
    Field topicMidField = new StringField(FIELD_TOPIC_MID, cleanUri(tripleDoc.getTopicMid()), Field.Store.YES);
    doc.add(topicMidField);

    FieldType fieldType = new FieldType();
    fieldType.setStored(true);
    fieldType.setStoreTermVectors(true);
    fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);

    Field titleField = new TextField(FIELD_TITLE, tripleDoc.getTitle(), Field.Store.YES);
    doc.add(titleField);

    Field labelField = new TextField(FIELD_LABEL, tripleDoc.getLabel(), Field.Store.YES);
    doc.add(labelField);

    Field textField = new TextField(FIELD_TEXT, tripleDoc.getText(), Field.Store.YES);
    doc.add(textField);

    tripleDoc.clear();
    return doc;
  }

  /**
   * Removes '<', '>' if they exist, lower case
   * <p>
   * TODO - replace ':' with '_' because query parser doesn't like it
   *
   * @param uri
   * @return
   */
  public static String cleanUri(String uri) {
    if (uri.charAt(0) == '<')
      return uri.substring(1, uri.length() - 1).toLowerCase();
    else
      return uri;
  }

}
