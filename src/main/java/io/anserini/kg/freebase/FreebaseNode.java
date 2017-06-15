package io.anserini.kg.freebase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An object representing a node in the Freebase knowledge graph.
 */
public class FreebaseNode {

  /**
   * Splitter that describes how s,p,o are split in a triple line
   */
  public static final String TRIPLE_SPLITTER = "\t";

  /**
   * Subject of the triples doc, also the FreebaseNode id
   */
  private String subject;

  /**
   * The predicates and values of the subject entity
   */
  private Map<String, List<String>> predicateValues = new TreeMap<>();

  public enum RdfObjectType {
    URI, STRING, TEXT, OTHER
  }

  /**
   * Constructor for an NT triples (NTriples).
   *
   * @param s subject
   * @param p predicate
   * @param o object
   */
  public FreebaseNode(String s, String p, String o) {
    init(s, p, o);
  }

  /**
   * Clone from another document
   * @param other
   */
  public FreebaseNode(FreebaseNode other) {
    this.subject = other.subject;
    other.predicateValues.forEach((predicate, values) -> {
      this.predicateValues.put(predicate, new ArrayList<>(values));
    });
  }

  /**
   * Constructor from a line
   * @param line line that contains triple information
   */
  public FreebaseNode(String line) throws IllegalArgumentException {
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
    this.subject = s;
    // Add the predicate and object as the first element in the list
    addPredicateAndValue(p, o);
  }

  /**
   * Add the predicate and its value in the predicateValues map
   * @param p predicate
   * @param o object value
   */
  public void addPredicateAndValue(String p, String o) {
    List<String> values = predicateValues.get(p);

    if (values == null) {
      values = new ArrayList<>();
      predicateValues.put(p, values);
    }

    values.add(o);
  }

  public String id() {
    return subject;
  }

  public String content() {
    return this.toString();
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    predicateValues.forEach((predicate, values) -> {
      for (String value : values) {
        sb.append(subject).append(TRIPLE_SPLITTER)
                .append(predicate).append(TRIPLE_SPLITTER)
                .append(value).append(TRIPLE_SPLITTER).append(".\n");
      }
    });
    return sb.toString();
  }

  public String getSubject() {
    return subject;
  }

  public Map<String, List<String>> getPredicateValues() {
    return predicateValues;
  }

  /**
   * Clears resources
   */
  public void clear() {
    predicateValues.clear();
    subject = null;
    predicateValues = null;
  }

  public static String cleanUri(String uri) {
    if (uri.charAt(0) == '<')
      return uri.substring(1, uri.length() - 1).toLowerCase();
    else
      return uri;
  }

  public static RdfObjectType getObjectType(String objectValue) {
    // Determine the type of this N-Triples 'value'.
    switch (objectValue.charAt(0)) {
      case '<':
        // e.g., <http://rdf.freebase.com/ns/m.02mjmr>
        return RdfObjectType.URI;
      case '"':
        if (objectValue.charAt(objectValue.length() - 1) == '"') {
          // e.g., "Hanna Bieluszko"@en";
          return RdfObjectType.STRING;
        } else {
          // e.g., "Hanna Bieluszko"
          return RdfObjectType.TEXT;
        }
      default:
        return RdfObjectType.OTHER;
    }
  }
}
