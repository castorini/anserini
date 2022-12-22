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
import io.anserini.collection.Cord19BaseDocument;
import io.anserini.collection.TrialstreamerCollection;
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

/**
 * Converts a {@link Cord19BaseDocument} into a Lucene {@link Document}, ready to be indexed.
 */
public class Cord19Generator implements LuceneDocumentGenerator<Cord19BaseDocument> {
  private IndexCollection.Args args;

  public enum CovidField {
    SHA("sha"),
    SOURCE("source_x"),
    DOI("doi"),
    TITLE("title"),
    AUTHORS("authors"),
    AUTHOR_STRING("author_string"),
    ABSTRACT("abstract"),
    JOURNAL("journal"),
    PUBLISH_TIME("publish_time"),
    YEAR("year"),
    LICENSE("license"),
    PMC_ID("pmcid"),
    PUBMED_ID("pubmed_id"),
    MICROSOFT_ID("mag_id"),
    S2_ID("s2_id"),
    WHO("who_covidence_id"),
    URL("url");

    public final String name;

    CovidField(String s) {
      name = s;
    }
  }

  public enum TrialstreamerField {
    OUTCOMES_VOCAB("outcomes_vocab"),
    POPULATION_VOCAB("population_vocab"),
    INTERVENTIONS_VOCAB("interventions_vocab");

    public final String name;

    TrialstreamerField(String s) {
      name = s;
    }
  }

  public Cord19Generator(IndexCollection.Args args) {
    this.args = args;
  }

  @Override
  public Document createDocument(Cord19BaseDocument covidDoc) throws GeneratorException {
    String id = covidDoc.id();
    String content = covidDoc.contents();
    String raw = covidDoc.raw();

    // See https://github.com/castorini/anserini/issues/1127
    // Corner cases are hard-coded now; if this gets out of hand we should consider implementing a "blacklist" feature
    // and store these ids externally. Note we use startsWith here to handle the paragraph indexes as well.
    //
    // Update (2020/05/27): Note that we have dedicated script to handle exactly this issue in pyserini:
    //   pyserini/scripts/cord19/find_cord19_length_outlier.py
    // The list below represents the union of the top 5 output of the script and whatever docs were identified before.
    if (id.startsWith("ij3ncdb6") ||
        id.startsWith("c4pt07zk") ||
        id.startsWith("1vimqhdp") ||
        id.startsWith("pd1g119c") ||
        id.startsWith("hwjkbpqp") ||
        id.startsWith("gvh0wdxn")) {
      throw new SkippedDocumentException();
    }

    if (content == null || content.trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();

    // Store the collection docid.
    doc.add(new StringField(Constants.ID, id, Field.Store.YES));
    // This is needed to break score ties by docid.
    doc.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));

    if (args.storeRaw) {
      doc.add(new StoredField(Constants.RAW, raw));
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

    // normal fields
    doc.add(new Field(CovidField.TITLE.name, covidDoc.record().get(CovidField.TITLE.name), fieldType));
    doc.add(new Field(CovidField.ABSTRACT.name, covidDoc.record().get(CovidField.ABSTRACT.name), fieldType));

    // string fields
    doc.add(new StringField(CovidField.SHA.name, covidDoc.record().get(CovidField.SHA.name), Field.Store.YES));
    doc.add(new StringField(CovidField.DOI.name, covidDoc.record().get(CovidField.DOI.name), Field.Store.YES));
    doc.add(new StringField(CovidField.JOURNAL.name, covidDoc.record().get(CovidField.JOURNAL.name), Field.Store.YES));
    doc.add(new StringField(CovidField.WHO.name, covidDoc.record().get(CovidField.WHO.name), Field.Store.YES));
    doc.add(new StringField(CovidField.PMC_ID.name, covidDoc.record().get(CovidField.PMC_ID.name), Field.Store.YES));
    doc.add(new StringField(CovidField.PUBMED_ID.name,
      covidDoc.record().get(CovidField.PUBMED_ID.name), Field.Store.YES));
    doc.add(new StringField(CovidField.MICROSOFT_ID.name,
      covidDoc.record().get(CovidField.MICROSOFT_ID.name), Field.Store.YES));
    doc.add(new StringField(CovidField.S2_ID.name,
      covidDoc.record().get(CovidField.S2_ID.name), Field.Store.YES));
    doc.add(new StringField(CovidField.PUBLISH_TIME.name,
      covidDoc.record().get(CovidField.PUBLISH_TIME.name), Field.Store.YES));
    doc.add(new StringField(CovidField.LICENSE.name,
      covidDoc.record().get(CovidField.LICENSE.name), Field.Store.YES));

    // default to first URL in metadata
    doc.add(new StringField(CovidField.URL.name,
      covidDoc.record().get(CovidField.URL.name).split("; ")[0], Field.Store.YES));

    if (covidDoc instanceof TrialstreamerCollection.Document) {
      TrialstreamerCollection.Document tsDoc = (TrialstreamerCollection.Document) covidDoc;
      JsonNode facets = tsDoc.facets();
      addTrialstreamerFacet(doc, TrialstreamerField.OUTCOMES_VOCAB.name, facets);
      addTrialstreamerFacet(doc, TrialstreamerField.POPULATION_VOCAB.name, facets);
      addTrialstreamerFacet(doc, TrialstreamerField.INTERVENTIONS_VOCAB.name, facets);
    }
  
    // non-stemmed fields
    addAuthors(doc, covidDoc.record().get(CovidField.AUTHORS.name), fieldType);

    for (String source : covidDoc.record().get(CovidField.SOURCE.name).split(";")) {
      addNonStemmedField(doc, CovidField.SOURCE.name, source.strip(), fieldType);
    }

    // parse year published
    try {
      doc.add(new IntPoint(CovidField.YEAR.name, Integer.parseInt(
        covidDoc.record().get(CovidField.PUBLISH_TIME.name).strip().substring(0, 4))));
    } catch(Exception e) {
      // can't parse year
    }
    return doc;
  }

  private void addAuthors(Document doc, String authorString, FieldType fieldType) {
    if (authorString == null || authorString == "") {
      return;
    }

    // index raw author string
    addNonStemmedField(doc, CovidField.AUTHOR_STRING.name, authorString, fieldType);

    // process all individual author names
    for (String author : authorString.split(";")) {
      addNonStemmedField(doc, CovidField.AUTHORS.name, processAuthor(author), fieldType);
    }
  }

  // process author name into a standard order if it is reversed and comma separated
  // eg) Jones, Bob -> Bob Jones
  private String processAuthor(String author) {
    String processedName = "";
    String[] splitNames = author.split(",");
    for (int i = splitNames.length - 1; i >= 0; --i) {
      processedName += splitNames[i].strip() + " ";
    }
    return processedName.strip();
  }

  // indexes a list of facets from the trialstreamer COVID trials dataset
  private void addTrialstreamerFacet(Document doc, String key, JsonNode facets) {
    for (JsonNode value : facets.get(key)) {
      doc.add(new StringField(key, value.asText(), Field.Store.YES));
    }
  }

  // index field without stemming but store original string value
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
