package io.anserini.document;

import org.openrdf.model.Literal;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

/**
 * A document that represent an freebase entity
 */
public class FreebaseEntityDocument implements SourceDocument {

  /**
   * Splitter that describes how s,p,o are split in a triple line
   */
  public static final String TRIPLE_SPLITTER = "\t";
  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private ValueFactory valueFactory = SimpleValueFactory.getInstance();

  /**
   * FreebaseEntityDocument has three fields:
   * entityId - the MID of the entity
   * title - the object value of the (entityId, http://rdf.freebase.com/key/wikipedia.en_title)
   * text - all the values separated by space of the (entityId, http://rdf.freebase.com/key/wikipedia.en)
   */
  private String entityId;
  private String title = "";
  private String label = "";
  private String text = "";

  /**
   * Constructor for an NT triples (NTriples).
   *
   * @param docid subject
   * @param title predicate
   * @param text object
   */
  public FreebaseEntityDocument(String docid, String title, String text) {
    init(docid, title, text);
  }

  /**
   * Clone from another document
   * @param other
   */
  public FreebaseEntityDocument(FreebaseEntityDocument other) {
    this.entityId = other.entityId;
    this.label = other.label;
    this.title = other.title;
    this.text = other.text;
  }

  /**
   * Constructor from a line
   * @param line line that contains triple information
   */
  public FreebaseEntityDocument(String line) throws IllegalArgumentException {
    String[] pieces = line.split(TRIPLE_SPLITTER);
    if (pieces.length == 4) {
      init(pieces[0], pieces[1], pieces[2]);
    } else {
      throw new IllegalArgumentException("Cannot parse triple from line: " + line);
    }
  }

  /**
   * Assign values
   * @param s subject
   * @param p predicate
   * @param o object
   */
  private void init(String s, String p, String o) {
    this.entityId = s;
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
    String WIKI_EN_URI = "http://rdf.freebase.com/key/wikipedia.en";
    String WIKI_EN_TILE_URI = "http://rdf.freebase.com/key/wikipedia.en_title";
    String W3_LABEL_URI = "http://www.w3.org/2000/01/rdf-schema#label";

    if (p.startsWith(WIKI_EN_URI)) {
      if (p.startsWith(WIKI_EN_TILE_URI)) {
        this.title = removeQuotes(o);
      }
      else {
        // concatenate other variants with a space
        this.text += removeQuotes(o) + " ";
      }
    }
    else if (p.startsWith(W3_LABEL_URI)) {
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
    if (literal.charAt(0) == '\"' && literal.charAt(literal.length()-1) == '\"')
      return literal.substring(1, literal.length()-1);
    else
      return literal;
  }

  @Override
  public String id() {
    return entityId;
  }

  @Override
  public String content() {
    return this.toString();
  }

  /**
   * Always index all triples
   * @return true
   */
  @Override
  public boolean indexable() {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Entity ID: " + entityId + "\n");
    sb.append("Title: " + title + "\n");
    sb.append("Label: " + label + "\n");
    sb.append("Text:\n" + text + "\n\n");
    return sb.toString();
  }

  public String getEntityId() {
    return entityId;
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
    entityId = null;
    title = null;
    label = null;
    text = null;
  }
}
