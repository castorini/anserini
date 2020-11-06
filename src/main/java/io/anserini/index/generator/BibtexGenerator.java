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

import io.anserini.analysis.DefaultEnglishAnalyzer;
import io.anserini.collection.BibtexCollection;
import io.anserini.index.IndexArgs;
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
import java.util.List;
import java.util.Map;

/**
 * Converts a {@link BibtexCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class BibtexGenerator implements LuceneDocumentGenerator<BibtexCollection.Document> {
  public static final String TYPE = "type";

  private IndexArgs args;

  public enum BibtexField {
    DOI("doi"),
    TITLE("title"),
    AUTHOR("author"),
    PUBLISHER("publisher"),
    JOURNAL("journal"),
    YEAR("year"),
    NUMBER("number"),
    URL("url"),
    BOOKTITLE("booktitle"),
    ADDRESS("address"),
    EDITOR("editor"),
    ABSTRACT("abstract");

    public final String name;

    BibtexField(String s) {
      name = s;
    }
  }

  public static final List<String> STRING_FIELD_NAMES = List.of(
    BibtexField.DOI.name,
    BibtexField.URL.name);

  public static final List<String> FIELDS_WITHOUT_STEMMING = List.of(
    BibtexField.BOOKTITLE.name,
    BibtexField.AUTHOR.name,
    BibtexField.PUBLISHER.name,
    BibtexField.JOURNAL.name,
    BibtexField.ADDRESS.name,
    BibtexField.EDITOR.name,
    BibtexField.ABSTRACT.name);

  public BibtexGenerator(IndexArgs args) {
    this.args = args;
  }

  @Override
  public Document createDocument(BibtexCollection.Document bibtexDoc) throws GeneratorException {
    String id = bibtexDoc.id();
    String content = bibtexDoc.contents();
    String type = bibtexDoc.type();
    BibTeXEntry bibtexEntry = bibtexDoc.bibtexEntry();

    if (content == null || content.trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
    // Store the collection's bibtex type
    doc.add(new StringField(TYPE, type, Field.Store.YES));

    if (args.storeRaw) {
      doc.add(new StoredField(IndexArgs.RAW, bibtexDoc.raw()));
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

    doc.add(new Field(IndexArgs.CONTENTS, content, fieldType));

    for (Map.Entry<Key, Value> fieldEntry : bibtexEntry.getFields().entrySet()) {
      String fieldKey = fieldEntry.getKey().toString();
      String fieldValue = fieldEntry.getValue().toUserString();

      // causes indexing to fail on Solr due to inconsistent formatting
      // because Solr infers the field type to be number instead of String
      // not worth trying to parse/normalize all numbers at the moment
      if (fieldKey.equals(BibtexField.NUMBER.name)) {
        continue;
      }

      if (STRING_FIELD_NAMES.contains(fieldKey)) {
        // index field as single token
        doc.add(new StringField(fieldKey, fieldValue, Field.Store.YES));
      } else if (FIELDS_WITHOUT_STEMMING.contains(fieldKey)) {
        // index field without stemming but store original string value
        FieldType nonStemmedType = new FieldType(fieldType);
        nonStemmedType.setStored(true);

        // token stream to be indexed
        Analyzer nonStemmingAnalyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
        StringReader reader = new StringReader(fieldValue);
        TokenStream stream = nonStemmingAnalyzer.tokenStream(null, reader);

        Field field = new Field(fieldKey, fieldValue, nonStemmedType);
        field.setTokenStream(stream);
        doc.add(field);

        nonStemmingAnalyzer.close();
      } else if (fieldKey.equals(BibtexField.YEAR.name)) {
        if (fieldValue != "") {
          // index as numeric value to allow range queries
          doc.add(new IntPoint(fieldKey, Integer.parseInt(fieldValue)));
        }
        doc.add(new StoredField(fieldKey, fieldValue));
      } else {
        // default to normal Field with tokenization and stemming
        doc.add(new Field(fieldKey, fieldValue, fieldType));
      }
    }

    return doc;
  }
}
