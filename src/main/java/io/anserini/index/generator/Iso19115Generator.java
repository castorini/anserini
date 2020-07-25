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

import io.anserini.collection.Iso19115Collection;
import io.anserini.index.IndexArgs;
import org.apache.lucene.document.*;
import org.apache.lucene.document.LatLonShape;
import org.apache.lucene.geo.Polygon;
import org.apache.lucene.index.Fields;

public class Iso19115Generator extends DefaultLuceneDocumentGenerator<Iso19115Collection.Document>{
  protected IndexArgs args;

  // constants for storing
  public enum Iso19115Field {
    ID("id"),
    TITLE("title"),
    ABSTRACT("abstract"),
    ORGANISATION("organisation"),
    RESPONSIBLE_PARTY("responsible_party"),
    CATALOGUE("catalogue"),
    PUBLISH_TIME("publish_time"),
    URL("url"),
    COORDINATES("coordinates"),
    PURPOSE("purpose"),
    SUPPLINFO("supplinfo"),
    TOPIC_CATEGORY("topic_category"),
    KEYWORDS("keywords"),
    RECOMMENDED_CITATION("recommended_citation"),
    THEASURUS_NAME("theasurus_name");

    public final String name;

    Iso19115Field(String s) {
      name = s;
    }
  }

  public Iso19115Generator(IndexArgs args) {
    super(args);
    this.args = args;
  }

  public Document createDocument(Iso19115Collection.Document doc) throws GeneratorException {
    Document document = super.createDocument(doc);

    document.add(new StoredField(Iso19115Field.TITLE.name, doc.getTitle()));
    document.add(new StoredField(Iso19115Field.ABSTRACT.name, doc.getAbstract()));
    document.add(new StringField(Iso19115Field.ORGANISATION.name, doc.getOrganisation(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.CATALOGUE.name, doc.getCatalogue(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.PUBLISH_TIME.name, doc.getPublish_time(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.URL.name, doc.getUrl(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.PURPOSE.name, doc.getPurpose(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.SUPPLINFO.name, doc.getSupplInfo(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.TOPIC_CATEGORY.name, doc.getTopicCategory(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.RECOMMENDED_CITATION.name, doc.getRecommendedCitation(), Field.Store.YES));
    document.add(new StringField(Iso19115Field.THEASURUS_NAME.name, doc.getThesaurusName(), Field.Store.YES));


    // indexing the authors
    String[] responsibleParty = doc.getResponsibleParty();
    for(String author: responsibleParty) {
      document.add(new StringField(Iso19115Field.RESPONSIBLE_PARTY.name, author, Field.Store.YES));
    }

    // indexing the keywords
    String[] keywords = doc.getKeywords();
    for(String keyword: keywords) {
      document.add(new StringField(Iso19115Field.KEYWORDS.name, keyword, Field.Store.YES));
    }

    // indexing the coordinates
    document.add(new StringField(Iso19115Field.COORDINATES.name, doc.getCoordinates(), Field.Store.YES));
    /* Polygon indexing unused for now, but may be used later
    // indexing the longitude and latitudes, each field is an indexable ShapeField.Triangle object
    Field[] polygonField = LatLonShape.createIndexableFields(Iso19115Field.COORDINATES.name, new Polygon(
            doc.getLatitude(),
            doc.getLongitude()
    ));
    for(Field field: polygonField) {
      document.add(field);
    }
    */
    return document;
  }
}
