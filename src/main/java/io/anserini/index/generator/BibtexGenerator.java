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

import io.anserini.analysis.EnglishStemmingAnalyzer;
import io.anserini.collection.BibtexCollection;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;
import org.jbibtex.BibTeXEntry;
import org.jbibtex.Key;
import org.jbibtex.Value;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Converts a {@link BibtexCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class BibtexGenerator extends LuceneDocumentGenerator<BibtexCollection.Document> {
  public static final String FIELD_ID = "id";
  public static final String FIELD_BODY = "contents";
  public static final String FIELD_RAW = "raw";

  public static enum BibtexField {
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
    FULL_TEXT_IDENTIFIER("fullTextIdentifier"),
    NUMBER("number");

    public final String name;

    BibtexField(String s) {
      name = s;
    }
  }

  public static final List<String> STRING_FIELD_NAMES = new ArrayList<>(Arrays.asList(
    BibtexField.DOI.name,
    BibtexField.OAI.name,
    BibtexField.IDENTIFIERS.name,
    BibtexField.DOWNLOAD_URL.name,
    BibtexField.RELATIONS.name,
    BibtexField.FULL_TEXT_IDENTIFIER.name));

  public static final List<String> FIELDS_WITHOUT_STEMMING = new ArrayList<>(Arrays.asList(
    BibtexField.IDENTIFIERS.name,
    BibtexField.AUTHORS.name,
    BibtexField.CONTRIBUTORS.name,
    BibtexField.PUBLISHER.name,
    BibtexField.JOURNALS.name,
    BibtexField.LANGUAGE.name));

  public BibtexGenerator(IndexArgs args, IndexCollection.Counters counters) {
    super(args, counters);
  }

  @Override
  public Document createDocument(BibtexCollection.Document coreDoc) {
    String id = coreDoc.id();
    String content = coreDoc.content();
    BibTeXEntry bibtexEntry = coreDoc.bibtexEntry();

    if (content == null || content.trim().isEmpty()) {
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

    for (Map.Entry<Key, Value> fieldEntry : bibtexEntry.getFields().entrySet()) {
      String fieldKey = fieldEntry.getKey().toString();
      String fieldValue = fieldEntry.getValue().toUserString();

      if (STRING_FIELD_NAMES.contains(fieldKey)) {
        // index field as single token
        doc.add(new StringField(fieldKey, fieldValue, Field.Store.YES));
      } else if (FIELDS_WITHOUT_STEMMING.contains(fieldKey)) {
        // index field without stemming but store original string value
        FieldType nonStemmedType = new FieldType(fieldType);
        nonStemmedType.setStored(true);

        // token stream to be indexed
        Analyzer nonStemmingAnalyzer = new EnglishStemmingAnalyzer(CharArraySet.EMPTY_SET);
        StringReader reader = new StringReader(fieldValue);
        TokenStream stream = nonStemmingAnalyzer.tokenStream(null, reader);
        
        Field field = new Field(fieldKey, fieldValue, nonStemmedType);
        field.setTokenStream(stream);
        doc.add(field);
 
        nonStemmingAnalyzer.close();
      } else if (fieldKey == BibtexField.YEAR.name) {
        if (fieldValue != "") {
          // index as numeric value to allow range queries
          doc.add(new IntPoint(fieldKey, Integer.parseInt(fieldValue)));
        }
        doc.add(new StoredField(fieldKey, fieldValue));
      } else if (fieldKey == BibtexField.NUMBER.name) {
        doc.add(new Field("number__", fieldValue, fieldType));
      } else {
        // default to normal Field with tokenization and stemming
        doc.add(new Field(fieldKey, fieldValue, fieldType));
      }
    }

    return doc;
  }
}
