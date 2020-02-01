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

package io.anserini.index.generator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.anserini.collection.CoreCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Converts a {@link CoreCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class CoreGenerator extends LuceneDocumentGenerator<CoreCollection.Document> {
  public static final String FIELD_ID = "id";
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_RAW = "raw";

  public enum CoreField {
    DOI("doi"),
    OAI("oai"),
    IDENTIFIERS("identifiers"),
    TITLE("title"),
    AUTHORS("authors"),
    ENRICHMENTS("enrichments"),
    CONTRIBUTORS("contributors"),
    DATE_PUBLISHED("datePublished"),
    PUBLISHER("publisher"),
    DOWNLOAD_URL("downloadUrl"),
    JOURNALS("journals"),
    LANGUAGE("language"),
    RELATIONS("relations"),
    YEAR("year"),
    TOPICS("topics"),
    SUBJECTS("subjects"),
    FULL_TEXT("fullText"),
    FULL_TEXT_IDENTIFIER("fullTextIdentifier");

    public final String name;

    CoreField(String s) {
      name = s;
    }
  }

  public CoreGenerator(IndexArgs args, IndexCollection.Counters counters) {
    super(args, counters);
  }

  @Override
  public Document createDocument(CoreCollection.Document coreDoc) {
    String id = coreDoc.id();
    String content = coreDoc.content();

    if (content.trim().isEmpty()) {
      counters.empty.incrementAndGet();
      return null;
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(FIELD_ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new SortedDocValuesField(FIELD_ID, new BytesRef(id)));

    if (args.storeRawDocs) {
      doc.add(new StoredField(FIELD_RAW, content));
    }

    FieldType fieldType = new FieldType();
    fieldType.setStored(args.storeTransformedDocs);

    // Are we storing document vectors?
    if (args.storeDocvectors) {
      fieldType.setStoreTermVectors(true);
      fieldType.setStoreTermVectorPositions(true);
    }

    // Are we building a "positional" or "count" index?
    if (args.storePositions) {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS_AND_POSITIONS);
    } else {
      fieldType.setIndexOptions(IndexOptions.DOCS_AND_FREQS);
    }

    doc.add(new Field(FIELD_BODY, content, fieldType));

    coreDoc.jsonFields().forEach((k, v) -> {
      String fieldString = jsonNodeToString(v);
      
      if (List.of(
        CoreField.DOI.name,
        CoreField.OAI.name,
        CoreField.IDENTIFIERS.name,
        CoreField.AUTHORS.name,
        CoreField.CONTRIBUTORS.name,
        CoreField.DATE_PUBLISHED.name,
        CoreField.PUBLISHER.name,
        CoreField.DOWNLOAD_URL.name,
        CoreField.JOURNALS.name,
        CoreField.LANGUAGE.name,
        CoreField.RELATIONS.name,
        CoreField.FULL_TEXT_IDENTIFIER.name).contains(k)
      ) {
        // index field without stemming
        doc.add(new StringField(k, fieldString, Field.Store.YES));
      } else if (k == CoreField.YEAR.name) {
        if (fieldString != "null") {
          // index as numeric value to allow range queries
          doc.add(new IntPoint(k, Integer.parseInt(fieldString)));
        }
        doc.add(new StoredField(k, fieldString));
      } else {
        // default to normal Field with stemming
        doc.add(new Field(k, fieldString, fieldType));
      }
    });

    return doc;
  }

  private static String jsonNodeToString(JsonNode node) {
    if (node instanceof ArrayNode) {
      ArrayNode arrayField = (ArrayNode) node;
      StringJoiner sj = new StringJoiner(" :: ");

      // recursively parse array fields
      arrayField.elements().forEachRemaining(arrayElement -> {
        sj.add(jsonNodeToString(arrayElement));
      });

      return sj.toString();
    } else if (node instanceof ObjectNode) {
      ObjectNode nestedField = (ObjectNode) node;
      StringJoiner sj = new StringJoiner(" :: ");
      Iterator<Map.Entry<String, JsonNode>> items = nestedField.fields();

      while (items.hasNext()) {
        Map.Entry<String, JsonNode> item = items.next();

        // recursively parse nested JSON fields
        sj.add("{ " + item.getKey() + " -> " + jsonNodeToString(item.getValue()) + " }");
      }

      return sj.toString();
    } else {
      return node.asText();
    }
  }
}
