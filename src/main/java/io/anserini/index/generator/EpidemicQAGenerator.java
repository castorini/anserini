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
import io.anserini.collection.Cord19BaseDocument;
import io.anserini.collection.EpidemicQACollection;
import io.anserini.index.IndexArgs;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;

import java.io.StringReader;

/**
 * Converts a {@link EpidemicQACollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class EpidemicQAGenerator implements LuceneDocumentGenerator<EpidemicQACollection.Document> {
  private IndexArgs args;

  // From the schema at https://bionlp.nlm.nih.gov/epic_qa/#collection.
  public enum EpidemicQAField {
    SHA("sha"),
    TITLE("title"),
    URL("url"),
    AUTHORS("authors"),
    AUTHOR_STRING("author_string");

    public final String name;

    EpidemicQAField(String s) {
      name = s;
    }
  }

  public EpidemicQAGenerator(IndexArgs args) {
    this.args = args;
  }

  @Override
  public Document createDocument(EpidemicQACollection.Document covidDoc) throws GeneratorException {
    String id = covidDoc.id();
    String content = covidDoc.contents();
    String raw = covidDoc.raw();
    String title = covidDoc.title();
    String url = covidDoc.url();
    String authors = covidDoc.authors();

    if (content == null || content.trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(IndexArgs.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));

    if (args.storeRaw) {
      doc.add(new StoredField(IndexArgs.RAW, raw));
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

    doc.add(new StringField(EpidemicQAField.TITLE.name, title, Field.Store.YES));
    doc.add(new StringField(EpidemicQAField.URL.name, url, Field.Store.YES));

    // non-stemmed fields
    addAuthors(doc, authors, fieldType);

    return doc;
  }

  private void addAuthors(Document doc, String authors, FieldType fieldType) {
    if (authors.length() == 0) {
      return;
    }
    addNonStemmedField(doc, EpidemicQAField.AUTHOR_STRING.name, authors, fieldType);

    // process all individual author names
    for (String author : authors.split(";")) {
      addNonStemmedField(doc, EpidemicQAField.AUTHORS.name, processAuthor(author), fieldType);
    }
  }

  /**
   * Process author name into a standard order if it is reversed and comma separated.
   * e.g. Jones, Bob -> Bob Jones
   */

  private String processAuthor(String author) {
    String processedName = "";
    String[] splitNames = author.split(",");
    for (int i = splitNames.length - 1; i >= 0; --i) {
      processedName += splitNames[i].strip() + " ";
    }
    return processedName.strip();
  }

  /**
   * Index field without stemming and store original string value.
   */

  private void addNonStemmedField(Document doc, String key, String value, FieldType fieldType) {
    FieldType nonStemmedType = new FieldType(fieldType);
    nonStemmedType.setStored(true);

    // token stream to be indexed
    Analyzer nonStemmingAnalyzer = DefaultEnglishAnalyzer.newNonStemmingInstance(CharArraySet.EMPTY_SET);
    TokenStream stream = nonStemmingAnalyzer.tokenStream(null, new StringReader(value));
    Field field = new Field(key, value, nonStemmedType);
    field.setTokenStream(stream);
    doc.add(field);
    nonStemmingAnalyzer.close();
  }
}
