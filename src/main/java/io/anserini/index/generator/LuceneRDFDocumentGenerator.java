package io.anserini.index.generator;

import io.anserini.document.RDFDocument;
import io.anserini.document.SourceDocument;
import io.anserini.index.IndexRDFCollection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.*;
import org.openrdf.rio.ntriples.NTriplesUtil;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Converts a {@link io.anserini.document.RDFDocument} into
 * a Lucene {@link org.apache.lucene.document.Document}.
 */
public class LuceneRDFDocumentGenerator {
  private static final Logger LOG = LogManager.getLogger(LuceneRDFDocumentGenerator.class);

  public static final String FIELD_SUBJECT = "subject";

  // RDF object predicate types
  static final String VALUE_TYPE_URI = "URI";
  static final String VALUE_TYPE_STRING = "STRING";
  static final String VALUE_TYPE_TEXT = "TEXT";
  static final String VALUE_TYPE_OTHER = "OTHER";

  protected IndexRDFCollection.Counters counters;
  protected IndexRDFCollection.Args args;

  public void config(IndexRDFCollection.Args args) {
    this.args = args;
  }

  public void setCounters(IndexRDFCollection.Counters counters) {
    this.counters = counters;
  }

  public Document createDocument(SourceDocument src) {
    if (!(src instanceof RDFDocument)) {
      throw new IllegalArgumentException("Cannot create RDF document from source document of type: " + src.getClass().getSimpleName());
    }

    RDFDocument tripleDoc = (RDFDocument) src;

    // Convert the triple doc to lucene doc
    Document doc = new Document();

    // Index subject as a StringField to allow searching
    Field subjectField = new StringField(FIELD_SUBJECT,
            cleanUri(tripleDoc.getSubject()),
            Field.Store.YES);
    doc.add(subjectField);

    // Iterate over predicates and object values
    for (Map.Entry<String, List<String>> entry : tripleDoc.getPredicateValues().entrySet()) {
      String predicate = cleanUri(entry.getKey());
      List<String> values = entry.getValue();

      for (String value : values) {
        String valueType = getObjectType(value);
        value = normalizeObjectValue(value);
        if (isIndexedPredicate(predicate)) {
          if (valueType.equals(VALUE_TYPE_URI)) {
            // Always index URIs using StringField
            doc.add(new StringField(predicate, value, Field.Store.YES));
          } else {
            // Just store the predicate in a stored field, no index
            doc.add(new TextField(predicate, value, Field.Store.YES));
          }
        } else {
          // Just add the predicate as a stored field, no index on it
          doc.add(new StoredField(predicate, value));
        }
      }
    }

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

  /**
   * Figures out the type of the value of an object in a triple
   *
   * @param objectValue object value
   * @return uri, string, text, or other
   */
  public static String getObjectType(String objectValue) {
    // Determine the type of this N-Triples `value'.
    char first = objectValue.charAt(0);
    switch (first) {
      case '<':
        return VALUE_TYPE_URI;
      case '"':
        if (objectValue.charAt(objectValue.length() - 1) == '"')
          return VALUE_TYPE_STRING;
        else
          return VALUE_TYPE_TEXT;
      default:
        return VALUE_TYPE_OTHER;
    }
  }

  /**
   * Check if the predicate should be indexed
   *
   * @param predicate the predicate to check
   * @return true if the user specified it to be indexed, false otherwise.
   */
  boolean isIndexedPredicate(String predicate) {
    return args.predicatesToIndex != null && args.predicatesToIndex.contains(predicate);
  }

  /**
   * Do nothing for strings
   */
  public static String normalizeStringValue(String value) {
    return value;
  }

  /**
   * Un-escape strings
   */
  public static String normalizeTextValue(String value) {
    return NTriplesUtil.unescapeString(value);
  }

  /**
   * Normalize Object object value
   *
   * @param objectValue
   * @return
   */
  public static String normalizeObjectValue(String objectValue) {
    // Normalize a `objectValue' depending on its type.
    String type = getObjectType(objectValue);
    if (type.equals(VALUE_TYPE_URI))
      return cleanUri(objectValue);
    else if (type.equals(VALUE_TYPE_STRING))
      return normalizeStringValue(objectValue);
    else if (type.equals(VALUE_TYPE_TEXT))
      return normalizeTextValue(objectValue);
    else
      return objectValue;
  }
}
