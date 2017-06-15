package io.anserini.kg.freebase;

import org.openrdf.rio.ntriples.NTriplesUtil;

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

  private String mid;
  private Map<String, List<String>> predicateValues = new TreeMap<>();

  public enum RdfObjectType {
    URI, STRING, TEXT, OTHER
  }

  /**
   * Constructor for an NT triples (NTriples).
   *
   * @param s mid
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
    this.mid = other.mid;
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
   * @param s mid
   * @param p predicate
   * @param o object
   */
  private void init(String s, String p, String o) {
    this.mid = s;
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

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    predicateValues.forEach((predicate, values) -> {
      for (String value : values) {
        sb.append(mid).append(TRIPLE_SPLITTER)
                .append(predicate).append(TRIPLE_SPLITTER)
                .append(value).append(TRIPLE_SPLITTER).append(".\n");
      }
    });
    return sb.toString();
  }

  public String mid() {
    return mid;
  }

  public Map<String, List<String>> getPredicateValues() {
    return predicateValues;
  }

  /**
   * Clears resources
   */
  public void clear() {
    predicateValues.clear();
    mid = null;
    predicateValues = null;
  }

  public static String cleanUri(String uri) {
    if (uri.charAt(0) == '<')
      return uri.substring(1, uri.length() - 1).toLowerCase();
    else
      return uri;
  }

  public static String shortenUri(String uri) {
    if (uri.charAt(0) == '<') {
      uri = uri.substring(1, uri.length() - 1).toLowerCase();
    }

    // Remove the prefix for mids, e.g., http://rdf.freebase.com/ns/m.02mjmr = Barack Obama
    uri = uri.replaceAll("^http://rdf.freebase.com/ns/m.", "");

    // These were standard namespace abbreviations used in the Freebase dumps prior to the
    // N-Triples format:
    //
    // @prefix ns: <http://rdf.freebase.com/ns/>.
    // @prefix key: <http://rdf.freebase.com/key/>.
    // @prefix owl: <http://www.w3.org/2002/07/owl#>.
    // @prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>.
    // @prefix xsd: <http://www.w3.org/2001/XMLSchema#>.
    //
    // See thread at: https://groups.google.com/forum/#!topic/freebase-discuss/AG5sl7K5KBE
    // Let's recover these namespace abbreviations to save space.
    uri = uri.replaceAll("^http://rdf.freebase.com/ns/", "ns:");
    uri = uri.replaceAll("^http://rdf.freebase.com/key/", "key:");
    uri = uri.replaceAll("^http://www.w3.org/2002/07/owl#", "owl:");
    uri = uri.replaceAll("^http://www.w3.org/2000/01/rdf-schema#", "rdfs:");
    uri = uri.replaceAll("^http://www.w3.org/2001/XMLSchema#", "xsd:");

    return uri;
  }

  public static String normalizeObjectValue(String objectValue) {
    FreebaseNode.RdfObjectType type = FreebaseNode.getObjectType(objectValue);
    if (type.equals(FreebaseNode.RdfObjectType.URI)) {
      return FreebaseNode.cleanUri(objectValue);
    } else if (type.equals(FreebaseNode.RdfObjectType.STRING)) {
      // If the object is a string, remove enclosing quote.
      if (objectValue.contains("$")) {
        // See comment below about MQL key escaping
        return removeEnclosingQuote(undoMqlKeyEscape(objectValue));
      } else {
        return removeEnclosingQuote(objectValue);
      }
    } else if (type.equals(FreebaseNode.RdfObjectType.TEXT)) {
      return NTriplesUtil.unescapeString(objectValue);
    } else {
      return objectValue;
    }
  }

  private static String removeEnclosingQuote(String s) {
    if (s.charAt(0) == '"')
      return s.substring(1, s.length() - 1);
    else
      return s;
  }

  // As an example, for "Barack Obama", one of the facts is:
  //   http://rdf.freebase.com/key/wikipedia.en: "Barack_Hussein_Obama$002C_Jr$002E"
  //
  // The $xxxx encoding is something called MQL key escape.
  //
  // Live version of page no longer exists, but see:
  //  http://web.archive.org/web/20160726102723/http://wiki.freebase.com/wiki/MQL_key_escaping
  //
  // Fortunately, I found a snippet of code on the web for handling this:
  //  https://github.com/hackreduce/Hackathon/blob/master/src/main/java/org/hackreduce/models/FreebaseQuadRecord.java
  private static String undoMqlKeyEscape(String s) {
    String[] part = s.split("\\$");
    StringBuffer sb = new StringBuffer(part[0]);
    for (int i = 1; i<part.length; i++) {
      try {
        int code = Integer.parseInt(part[i].substring(0, 4), 16);
        sb.appendCodePoint(code).append(part[i].substring(4));
      } catch (IndexOutOfBoundsException e) {
        sb.append(part[i]);
      } catch (NumberFormatException e) {
        sb.append(part[i]);
      }
    }
    return sb.toString();
  }

  public static RdfObjectType getObjectType(String objectValue) {
    // Determine the type of this N-Triples 'value'.
    switch (objectValue.charAt(0)) {
      case '<':
        // e.g., <http://rdf.freebase.com/ns/m.02mjmr>
        return RdfObjectType.URI;
      case '"':
        if (objectValue.charAt(objectValue.length() - 1) == '"') {
          // e.g., "Hanna Bieluszko"
          return RdfObjectType.STRING;
        } else {
          // e.g., "Hanna Bieluszko"@en";
          return RdfObjectType.TEXT;
        }
      default:
        return RdfObjectType.OTHER;
    }
  }
}
