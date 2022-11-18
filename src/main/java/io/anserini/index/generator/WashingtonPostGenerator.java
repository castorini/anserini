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

import io.anserini.collection.WashingtonPostCollection;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;

/**
 * Converts a {@link WashingtonPostCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class WashingtonPostGenerator extends DefaultLuceneDocumentGenerator<WashingtonPostCollection.Document> {

  public enum WashingtonPostField {
    AUTHOR("author"),
    ARTICLE_URL("article_url"),
    PUBLISHED_DATE("published_date"),
    TITLE("title"),
    FULL_CAPTION("fullCaption"),
    KICKER("kicker");

    public final String name;
  
    WashingtonPostField(String s) {
      name = s;
    }
  }

  public WashingtonPostGenerator(IndexCollection.Args args) {
    super.args = args;
  }
  
  @Override
  public Document createDocument(WashingtonPostCollection.Document src) throws GeneratorException {
    // Use the superclass to create a document with all the default fields.
    Document doc = super.createDocument(src);

    // Add additional fields that are specialized for the Washington Post
    doc.add(new LongPoint(WashingtonPostField.PUBLISHED_DATE.name, src.getPublishDate()));
    doc.add(new StoredField(WashingtonPostField.PUBLISHED_DATE.name, src.getPublishDate()));

    src.getAuthor().ifPresent(author ->
        doc.add(new StringField(WashingtonPostField.AUTHOR.name, author, Field.Store.NO)));
    src.getArticleUrl().ifPresent(url ->
        doc.add(new StringField(WashingtonPostField.ARTICLE_URL.name, url, Field.Store.NO)));
    src.getTitle().ifPresent(title ->
        doc.add(new StringField(WashingtonPostField.TITLE.name, title, Field.Store.NO)));

    if (src.getKicker() != null) {
      doc.add(new StringField(WashingtonPostGenerator.WashingtonPostField.KICKER.name,
          src.getKicker(), Field.Store.NO));
    }

    if (src.getFullCaption() != null) {
      doc.add(new StringField(WashingtonPostGenerator.WashingtonPostField.FULL_CAPTION.name,
          src.getFullCaption(), Field.Store.NO));
    }

    return doc;
  }
}
