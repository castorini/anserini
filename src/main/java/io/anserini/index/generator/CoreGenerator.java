/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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
import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.collection.CoreCollection;
import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.BinaryDocValuesField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.IntPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

import java.io.StringReader;
import java.util.List;

/**
 * Converts a {@link CoreCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class CoreGenerator implements LuceneDocumentGenerator<CoreCollection.Document> {
  private IndexCollection.Args args;

  public enum CoreField {
    DOI("doi"),
    OAI("oai"),
    IDENTIFIERS("identifiers"),
    TITLE("title"),
    AUTHORS("authors"),
    ENRICHMENTS("enrichments"),
    CONTRIBUTORS("contributors"),
    DATE_PUBLISHED("datePublished"),
    ABSTRACT("abstract"),
    PUBLISHER("publisher"),
    DOWNLOAD_URL("downloadUrl"),
    FULL_TEXT_IDENTIFIER("fullTextIdentifier"),
    PDF_HASH_VALUE("pdfHashValue"),
    JOURNALS("journals"),
    LANGUAGE("language"),
    RELATIONS("relations"),
    YEAR("year"),
    TOPICS("topics"),
    SUBJECTS("subjects"),
    FULL_TEXT("fullText");

    public final String name;

    CoreField(String s) {
      name = s;
    }
  }

  public static final List<String> STRING_FIELD_NAMES = List.of(
    CoreField.DOI.name,
    CoreField.OAI.name,
    CoreField.IDENTIFIERS.name,
    CoreField.DOWNLOAD_URL.name,
    CoreField.FULL_TEXT_IDENTIFIER.name,
    CoreField.PDF_HASH_VALUE.name,
    CoreField.RELATIONS.name);

  public static final List<String> FIELDS_WITHOUT_STEMMING = List.of(
    CoreField.IDENTIFIERS.name,
    CoreField.AUTHORS.name,
    CoreField.CONTRIBUTORS.name,
    CoreField.PUBLISHER.name,
    CoreField.JOURNALS.name,
    CoreField.LANGUAGE.name);

  public CoreGenerator(IndexCollection.Args args) {
    this.args = args;
  }

  @Override
  public Document createDocument(CoreCollection.Document coreDoc) throws GeneratorException {
    String id = coreDoc.id();
    String content = coreDoc.contents();

    if (content == null || content.trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

    if (args.storeRaw) {
      doc.add(new StoredField(Constants.RAW, coreDoc.raw()));
    }

    FieldType fieldType = new FieldType();
    fieldType.setStored(args.storeContents);

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

    doc.add(new Field(Constants.CONTENTS, content, fieldType));

    coreDoc.jsonNode().fieldNames().forEachRemaining(key -> {
      JsonNode value = coreDoc.jsonNode().get(key);
      if (value.isArray() && value.size() > 0) {
        value.elements().forEachRemaining(element ->
          addDocumentField(doc, key, element, fieldType)
        );
      } else {
        addDocumentField(doc, key, value, fieldType);
      }
    });

    return doc;
  }

  private void addDocumentField(Document doc, String key, JsonNode value, FieldType fieldType) {
    String valueText = value.asText() == "null" ? "" : value.asText();

    if (STRING_FIELD_NAMES.contains(key)) {
      doc.add(new StringField(key, valueText, Field.Store.YES));
    } else if (FIELDS_WITHOUT_STEMMING.contains(key)) {
      // index field without stemming but store original string value
      FieldType nonStemmedType = new FieldType(fieldType);
      nonStemmedType.setStored(true);

      // token stream to be indexed
      Analyzer nonStemmingAnalyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
      TokenStream stream = nonStemmingAnalyzer.tokenStream(null, new StringReader(value.asText()));
      Field field = new Field(key, valueText, nonStemmedType);
      field.setTokenStream(stream);
      doc.add(field);
      nonStemmingAnalyzer.close();
    } else if (key == CoreField.YEAR.name) {
      // index as numeric value to allow range queries
      try {
        doc.add(new IntPoint(key, Integer.parseInt(valueText)));
        doc.add(new StoredField(key, valueText));
      } catch(Exception e) {
        // year is not numeric value
      }
    } else {
      doc.add(new Field(key, valueText, fieldType));
    }
  }
}
