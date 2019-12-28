/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.anserini.kg;

import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.SimpleValueFactory;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * An object representing a node in the Freebase knowledge graph. Each node is uniquely identified
 * by its URI and can represent a topic, a compound value type (CVT), or other metadata such as a
 * type.
 */
public class FreebaseNode {
  private final String uri;
  private final Map<String, List<String>> predicateValues = new TreeMap<>();

  public enum RdfObjectType {
    URI, STRING, TEXT, OTHER
  }

  /**
   * Simple value factory to parse literals using Sesame library.
   */
  private static ValueFactory valueFactory = SimpleValueFactory.getInstance();

  /**
   * Constructor.
   * @param uri URI of node
   */
  public FreebaseNode(String uri) {
    this.uri = uri;
  }

  /**
   * Adds a predicate and a value to this node.
   * @param p predicate value
   * @param o object value
   * @return the node itself
   */
  public FreebaseNode addPredicateValue(String p, String o) {
    List<String> values = predicateValues.get(p);

    if (values == null) {
      values = new ArrayList<>();
      predicateValues.put(p, values);
    }

    values.add(o);
    return this;
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    predicateValues.forEach((predicate, values) -> {
      for (String value : values) {
        sb.append(uri).append("\t").append(predicate).append("\t")
            .append(value).append("\t").append(".\n");
      }
    });
    return sb.toString();
  }

  public String uri() {
    return uri;
  }

  public Map<String, List<String>> getPredicateValues() {
    return predicateValues;
  }

  public static final String FREEBASE_NS_LONG = "^http://rdf.freebase.com/ns/";
  public static final String FREEBASE_NS_SHORT = "fb:";
  public static final String FREEBASE_KEY_LONG = "^http://rdf.freebase.com/key/";
  public static final String FREEBASE_KEY_SHORT = "fbkey:";

  public static String cleanUri(String uri) {
    if (uri.charAt(0) == '<') {
      uri = uri.substring(1, uri.length() - 1).toLowerCase();
    }

    // Manually shorten URIs. If there are more mappings, we might want to consider a more
    // general solution (e.g., using a Map).
    uri = uri.replaceAll(FREEBASE_NS_LONG, FREEBASE_NS_SHORT);
    uri = uri.replaceAll(FREEBASE_KEY_LONG, FREEBASE_KEY_SHORT);

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
    } else {
      return objectValue;
    }
  }

  private static String removeEnclosingQuote(String s) {
    if (s.charAt(0) == '"') {
      return s.substring(1, s.length() - 1);
    } else {
      return s;
    }
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

  /**
   * Helper function. Extracts value from literal that has a type
   * (whether the type is a language for a string literal or a basic data type
   * like date, int, etc.)
   *
   * @param literalString the string representation of the literal, including its type
   * @return value of the literal
   */
  public static String extractValueFromTypedLiteralString(String literalString) {
    return NTriplesUtil.parseLiteral(literalString, valueFactory).stringValue();
  }

  /**
   * Helper function. Converts freebase URI to freebase mention id
   *
   * @param freebaseUri freebase uri, similar to
   * @return freebase mention id
   */
  public static String freebaseUriToFreebaseId(String freebaseUri) {
    return freebaseUri.substring(freebaseUri.lastIndexOf('/')).replace('.', '/');
  }
}
