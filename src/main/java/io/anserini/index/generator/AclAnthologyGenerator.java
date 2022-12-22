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
import io.anserini.collection.AclAnthology;
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
 * Converts a {@link AclAnthology.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class AclAnthologyGenerator implements LuceneDocumentGenerator<AclAnthology.Document> {
  private IndexCollection.Args args;

  private enum AclAnthologyField {
    ADDRESS("address"),
    AUTHOR_STRING("author_string"),
    BIBKEY("bibkey"),
    BIBTYPE("bibtype"),
    BOOKTITLE("booktitle"),
    PDF("pdf"),
    URL("url"),
    THUMBNAIL("thumbnail"),
    TITLE("title"),
    ABSTRACT_HTML("abstract_html"),
    PUBLISHER("publisher"),
    MONTH("month"),
    YEAR("year"),
    PAGE_FIRST("page_first"),
    PAGE_LAST("page_last");

    public final String name;

    AclAnthologyField(String s) {
      name = s;
    }
  }

  public static final List<String> STRING_FIELD_NAMES = List.of(
    AclAnthologyField.ADDRESS.name,
    AclAnthologyField.BIBKEY.name,
    AclAnthologyField.BIBTYPE.name,
    AclAnthologyField.PDF.name,
    AclAnthologyField.URL.name,
    AclAnthologyField.THUMBNAIL.name);

  public static final List<String> NUMERIC_FIELD_NAMES = List.of(
    AclAnthologyField.YEAR.name,
    AclAnthologyField.PAGE_FIRST.name,
    AclAnthologyField.PAGE_LAST.name);

  public static final List<String> FIELDS_WITHOUT_STEMMING = List.of(
    AclAnthologyField.AUTHOR_STRING.name,
    AclAnthologyField.PUBLISHER.name,
    AclAnthologyField.MONTH.name);

  public AclAnthologyGenerator(IndexCollection.Args args) {
    this.args = args;
  }

  @Override
  public Document createDocument(AclAnthology.Document aclDoc) throws GeneratorException {
    String id = aclDoc.id();
    String content = aclDoc.contents();

    if (content == null || content.trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

    if (args.storeRaw) {
      doc.add(new StoredField(Constants.RAW, aclDoc.raw()));
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

    // used to store original field valuees
    FieldType storedFieldType = new FieldType(fieldType);
    storedFieldType.setStored(true);

    // index individual paper fields
    aclDoc.paper().fieldNames().forEachRemaining(key -> {
      JsonNode value = aclDoc.paper().get(key);
      String fieldString = value.asText();

      if (STRING_FIELD_NAMES.contains(key)) {
        // index field as single token
        doc.add(new StringField(key, fieldString, Field.Store.YES));
      } else if (NUMERIC_FIELD_NAMES.contains(key)) {
        try {
          // index as numeric value to allow range queries
          doc.add(new IntPoint(key, Integer.parseInt(fieldString)));
        } catch (Exception e) {
          // do nothing, integer parsing failed
        }
        doc.add(new StoredField(key, fieldString));
      } else if (FIELDS_WITHOUT_STEMMING.contains(key)) {
        // token stream to be indexed
        Analyzer nonStemmingAnalyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
        StringReader reader = new StringReader(fieldString);
        TokenStream stream = nonStemmingAnalyzer.tokenStream(null, reader);

        Field field = new Field(key, fieldString, storedFieldType);
        field.setTokenStream(stream);
        doc.add(field);

        nonStemmingAnalyzer.close();
      } else {
        // default to normal Field with tokenization and stemming
        doc.add(new Field(key, fieldString, storedFieldType));
      }
    });

    // index authors
    aclDoc.authors().forEach(author ->
      doc.add(new StringField("authors", author, Field.Store.YES))
    );

    // index SIGs
    aclDoc.sigs().forEach(sig ->
      doc.add(new StringField("sigs", sig, Field.Store.YES))
    );

    // index venues
    aclDoc.venues().forEach(venue ->
      doc.add(new StringField("venues", venue, Field.Store.YES))
    );

    return doc;
  }
}
