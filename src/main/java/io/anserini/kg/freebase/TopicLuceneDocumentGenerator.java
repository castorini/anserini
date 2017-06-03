package io.anserini.kg.freebase;

import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexOptions;
import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.util.List;
import java.util.Map;

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

  /**
   * Predicates for which the literals should be stored
   */
  public static final String WIKI_EN_URI = "http://rdf.freebase.com/key/wikipedia.en";
  public static final String WIKI_EN_TILE_URI = WIKI_EN_URI + "_title";
  public static final String W3_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";

  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private ValueFactory valueFactory = SimpleValueFactory.getInstance();

  protected IndexTopics.Counters counters;
  protected IndexTopics.Args args;

  public void config(IndexTopics.Args args) {
    this.args = args;
  }

  public void setCounters(IndexTopics.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(ObjectTriples src) {
    // make a Topic from the ObjectTriples
    String topicMid = src.getSubject();
    String title = "";
    String label = "";
    String text = "";
    Map<String, List<String>> predicateValues = src.getPredicateValues();
    for(Map.Entry<String, List<String>> entry: predicateValues.entrySet()) {
      String predicate = entry.getKey();
      List<String> objects = entry.getValue();
      for (String object : objects) {
        predicate = cleanUri(predicate);
        if (predicate.startsWith(WIKI_EN_URI)) {
          if (predicate.startsWith(WIKI_EN_TILE_URI)) {
            title = removeQuotes(object);
          } else {
            // concatenate other variants with a space
            text += removeQuotes(object) + " ";
          }
        } else if (predicate.startsWith(W3_LABEL_URI)) {
          Literal parsedLiteral = NTriplesUtil.parseLiteral(object, valueFactory);
          if (parsedLiteral.getLanguage().toString().equals("Optional[en]")) {
            label = parsedLiteral.stringValue();
          }
        }
      }
    }

    // Convert the triple doc to lucene doc
    Document doc = new Document();

    // Index subject as a StringField to allow searching
    Field topicMidField = new StringField(FIELD_TOPIC_MID, cleanUri(topicMid), Field.Store.YES);
    doc.add(topicMidField);

    Field titleField = new TextField(FIELD_TITLE, title, Field.Store.YES);
    doc.add(titleField);

    Field labelField = new TextField(FIELD_LABEL, label, Field.Store.YES);
    doc.add(labelField);

    Field textField = new TextField(FIELD_TEXT, text, Field.Store.YES);
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

  /**
   * Removes quotes from the literal value in object field
   */
  private String removeQuotes(String literal) {
    if (literal.charAt(0) == '\"' && literal.charAt(literal.length()-1) == '\"') {
      return literal.substring(1, literal.length() - 1);
    }
    return literal;
  }
}