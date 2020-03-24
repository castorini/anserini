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

import io.anserini.collection.WashingtonPostCollection;
import io.anserini.collection.WashingtonPostCollection.Document.WashingtonPostObject;
import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.FieldType;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.util.BytesRef;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.List;

/**
 * Converts a {@link WashingtonPostCollection.Document} into a Lucene {@link Document}, ready to be indexed.
 */
public class WashingtonPostGenerator implements LuceneDocumentGenerator<WashingtonPostCollection.Document> {
  public static final List<String> CONTENT_TYPE_TAG = Arrays.asList("sanitized_html", "tweet");

  private IndexArgs args;

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
  
  public WashingtonPostGenerator(IndexArgs args) {
    this.args = args;
  }
  
  public static String removeTags(String content) {
    return Jsoup.parse(content).text();
  }

  @Override
  public Document createDocument(WashingtonPostCollection.Document src) throws GeneratorExpection {
    String id = src.id();

    if (src.contents().trim().isEmpty()) {
      throw new EmptyDocumentException();
    }

    Document doc = new Document();
    doc.add(new StringField(IndexArgs.ID, id, Field.Store.YES));

    // This is needed to break score ties by docid.
    doc.add(new SortedDocValuesField(IndexArgs.ID, new BytesRef(id)));
    doc.add(new LongPoint(WashingtonPostField.PUBLISHED_DATE.name, src.getPublishDate()));
    doc.add(new StoredField(WashingtonPostField.PUBLISHED_DATE.name, src.getPublishDate()));

    src.getAuthor().ifPresent(author ->
        doc.add(new StringField(WashingtonPostField.AUTHOR.name, author, Field.Store.NO)));
    src.getArticleUrl().ifPresent(url ->
        doc.add(new StringField(WashingtonPostField.ARTICLE_URL.name, url, Field.Store.NO)));
    src.getTitle().ifPresent(title ->
        doc.add(new StringField(WashingtonPostField.TITLE.name, title, Field.Store.NO)));

    StringBuilder contentBuilder = new StringBuilder();
    src.getTitle().ifPresent(title -> contentBuilder.append(title).append("\n"));

    src.getObj().getContents().ifPresent(contents -> {
      for (WashingtonPostObject.Content contentObj : contents) {
        if (contentObj == null) continue;
        if (contentObj.getType().isPresent() && contentObj.getContent().isPresent()) {
          contentObj.getType().ifPresent(type -> {
            contentObj.getContent().ifPresent(content -> {
              if (CONTENT_TYPE_TAG.contains(type)) {
                contentBuilder.append(removeTags(content)).append("\n");
              } else if (type.compareToIgnoreCase("kicker") == 0) {
                doc.add(new StringField(WashingtonPostField.KICKER.name, content, Field.Store.NO));
                contentBuilder.append(content).append("\n");
              }
            });
          });
        }
        contentObj.getFullCaption().ifPresent(caption -> {
          String fullCaption = contentObj.getFullCaption().get();
          doc.add(new StringField(WashingtonPostField.FULL_CAPTION.name, fullCaption, Field.Store.NO));
          contentBuilder.append(removeTags(fullCaption)).append("\n");
        });
      }
    });

    if (args.storeRawDocs) { // store the raw json string as one single field
      doc.add(new StoredField(IndexArgs.RAW, src.getContent()));
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

    doc.add(new Field(IndexArgs.CONTENTS, contentBuilder.toString(), fieldType));

    return doc;
  }
}
