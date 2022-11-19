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

import io.anserini.collection.WarcBaseDocument;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;

public class WarcGenerator extends DefaultLuceneDocumentGenerator<WarcBaseDocument> {
  protected IndexCollection.Args args;

  public enum WarcField {
    DATE("date"),
    URL("url");

    public final String name;

    WarcField(String s) {
      name = s;
    }
  }

  public WarcGenerator(IndexCollection.Args args) {
    super(args);
    this.args = args;
  }

  public Document createDocument(WarcBaseDocument doc) throws GeneratorException {
    Document document = super.createDocument(doc);
    document.add(new StringField(WarcField.DATE.name, doc.getDate(), Field.Store.YES));
    document.add(new StringField(WarcField.URL.name, doc.getURL(), Field.Store.YES));
    return document;
  }
}

