package io.anserini.kg.freebase;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;

/**
 * Class that converts an {@link ObjectTriples} object into a Lucene document for indexing.
 */
public class TopicLuceneDocumentGenerator {
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


  protected IndexTopics.Counters counters;
  protected IndexTopics.Args args;

  public void config(IndexTopics.Args args) {
    this.args = args;
  }

  public void setCounters(IndexTopics.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(Topic src) {
    // Convert the triple doc to lucene doc
    Document doc = new Document();

    // Index subject as a StringField to allow searching
    Field topicMidField = new StringField(FIELD_TOPIC_MID, cleanUri(src.getTopicMid()), Field.Store.YES);
    doc.add(topicMidField);

    Field titleField = new TextField(FIELD_TITLE, src.getTitle(), Field.Store.YES);
    doc.add(titleField);

    Field labelField = new TextField(FIELD_LABEL, src.getLabel(), Field.Store.YES);
    doc.add(labelField);

    Field textField = new TextField(FIELD_TEXT, src.getText(), Field.Store.YES);
    doc.add(textField);

    src.clear();
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