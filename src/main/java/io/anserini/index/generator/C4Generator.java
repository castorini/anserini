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

import io.anserini.collection.C4Collection;
import io.anserini.index.IndexCollection;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.StringField;

public class C4Generator extends DefaultLuceneDocumentGenerator<C4Collection.Document>{
  protected IndexCollection.Args args;

  // constants for storing
  public enum C4Field {
    ID("id"),
    URL("url"),
    TIMESTAMP("timestamp");

    public final String name;

    C4Field(String s) {
      name = s;
    }
  }

  public C4Generator(IndexCollection.Args args) {
    super(args);
    this.args = args;
  }

  public Document createDocument(C4Collection.Document doc) throws GeneratorException {
    Document document = super.createDocument(doc);

    // contents and id stored in superclass method
    document.add(new StringField(C4Field.URL.name, doc.getUrl(), Field.Store.YES));
    document.add(new LongPoint(C4Field.TIMESTAMP.name, doc.getTimestamp()));
    document.add(new StoredField(C4Field.TIMESTAMP.name, doc.getTimestamp()));
    return document;
  }
}
