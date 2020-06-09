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
import org.apache.lucene.document.Document;
import org.apache.lucene.document.StoredField;


public class Iso19115Generator extends DefaultLuceneDocumentGenerator<Iso19115Collection.Document>{
  protected IndexArgs args;

  // constants for storing
  public enum Iso19115Field {
    ID("id"),
    TITLE("title"),
    ABSTRACT("abstract");

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
    return document;
  }
}
