/**
 * Anserini: An information retrieval toolkit built on Lucene
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

package io.anserini.document;

import io.anserini.document.nyt.NYTCorpusDocument;
import io.anserini.document.nyt.NYTCorpusDocumentParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;

/**
 * A TREC Core document.
 */
public class TrecCoreDocument implements SourceDocument {
  protected String id;
  protected String contents;
  protected File file;

  public TrecCoreDocument(File file) {
    this.file = file;
  }

  @Override
  public TrecCoreDocument readNextRecord(BufferedReader bRdr) throws Exception {
    return readNextRecord(file);
  }

  public TrecCoreDocument readNextRecord(File fileName) throws IOException {
    NYTCorpusDocumentParser nytParser = new NYTCorpusDocumentParser();
    NYTCorpusDocument nytDoc = nytParser.parseNYTCorpusDocumentFromFile(fileName, false);

    id = String.valueOf(nytDoc.getGuid());
    contents = nytDoc.getBody();

    return this;
  }

  @Override
  public String id() {
    return id;
  }

  @Override
  public String content() {
    return contents;
  }

  @Override
  public boolean indexable() {
    return true;
  }
}
