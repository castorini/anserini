package io.anserini.kg.freebase;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

/**
 * A document that represent an freebase topic
 */
public class Topic {

  /**
   * Splitter that describes how s,p,o are split in a triple line
   */
  public static final String TRIPLE_SPLITTER = "\t";

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

  /**
   * FreebaseTopicDocument has four fields:
   * topicMid - the MID of the topic
   * title - the object value of the (topicMid, http://rdf.freebase.com/key/wikipedia.en_title)
   * label - title - the object value of the (topicMid, http://www.w3.org/2000/01/rdf-schema#label)
   * text - all the values separated by space of the (topicMid, http://rdf.freebase.com/key/wikipedia.en)
   */
  private String topicMid;
  private String title = "";
  private String label = "";
  private String text = "";

  /**
   * Constructor for an NT triples (NTriples).
   *
   * @param s subject
   * @param p predicate
   * @param o object
   */
  public Topic(String s, String p, String o) {
    init(s, p, o);
  }

  public Topic(String s) {
    this.topicMid = s;
  }

  /**
   * Clone from another document
   * @param other
   */
  public Topic(Topic other) {
    this.topicMid = other.topicMid;
    this.label = other.label;
    this.title = other.title;
    this.text = other.text;
  }

  /**
   * Assign values
   * @param s subject
   * @param p predicate
   * @param o object
   */
  private void init(String s, String p, String o) {
    this.topicMid = s;
    // Add the predicate and object as the first element in the list
    addPredicateAndValue(p, o);
  }

  /**
   * Add the predicate and its value in the predicateValues map
   * @param p predicate
   * @param o object value
   */
  public void addPredicateAndValue(String p, String o) {
    p = cleanUri(p);

    if (p.startsWith(WIKI_EN_URI)) {
      if (p.startsWith(WIKI_EN_TILE_URI)) {
        this.title = removeQuotes(o);
      } else {
        // concatenate other variants with a space
        this.text += removeQuotes(o) + " ";
      }
    } else if (p.startsWith(W3_LABEL_URI)) {
      Literal parsedLiteral = NTriplesUtil.parseLiteral(o, valueFactory);
      if (parsedLiteral.getLanguage().toString().equals("Optional[en]")) {
        this.label = parsedLiteral.stringValue();
      }
    }
  }

  /**
   * Removes '<', '>' if they exist, lower case
   */
  private static String cleanUri(String uri) {
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

  public String id() {
    return topicMid;
  }

  public String content() {
    return this.toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Topic MID: " + topicMid + "\n");
    sb.append("Title: " + title + "\n");
    sb.append("Label: " + label + "\n");
    sb.append("Text:\n" + text + "\n\n");
    return sb.toString();
  }

  public String getTopicMid() {
    return topicMid;
  }

  public String getTitle() {
    return title;
  }

  public String getLabel() {
    return label;
  }

  public String getText() {
    return text;
  }


  /**
   * Clears resources
   */
  public void clear() {
    topicMid = null;
    title = null;
    label = null;
    text = null;
  }
}