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

package io.anserini.collection;

import io.anserini.document.SourceDocument;
import io.anserini.document.TrecwebDocument;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

public abstract class TrecwebCollection<D extends TrecwebDocument> extends TrecCollection {
  public TrecwebCollection() throws IOException {
    super();
  }

  @Override
  public void prepareInput(Path curInputFile) throws IOException {
    this.curInputFile = curInputFile;
    this.bRdr = null;
    String fileName = curInputFile.toString();
    if (fileName.endsWith(".gz")) { //.gz
      InputStream stream = new GZIPInputStream(
              Files.newInputStream(curInputFile, StandardOpenOption.READ), BUFFER_SIZE);
      bRdr = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    } else { // in case user had already uncompressed the folder
      bRdr = new BufferedReader(new FileReader(fileName));
    }
  }

  @Override
  public SourceDocument next() {
    TrecwebDocument doc = new TrecwebDocument();
    try {
      doc = (D) doc.readNextRecord(bRdr);
      if (doc == null) {
        at_eof = true;
        doc = null;
      }
    } catch (IOException e1) {
      doc = null;
    }
    return doc;
  }
}
