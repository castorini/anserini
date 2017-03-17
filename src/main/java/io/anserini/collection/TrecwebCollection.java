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

import io.anserini.document.TrecwebDocument;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of a TREC web collection.
 */
public abstract class TrecwebCollection<D extends TrecwebDocument> extends TrecCollection {

  public class FileSegment extends TrecCollection.FileSegment {
    protected BufferedReader bufferedReader;
    protected final int BUFFER_SIZE = 1 << 16; // 64K

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.endsWith(".gz")) { //.gz
        InputStream stream = new GZIPInputStream(
            Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else { // in case user had already uncompressed the folder
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }

    @Override
    public void close() throws IOException {
      atEOF = false;
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }

    @Override
    public D next() {
      TrecwebDocument doc = new TrecwebDocument();
      try {
        doc = (TrecwebDocument) doc.readNextRecord(bufferedReader);
        if (doc == null) {
          atEOF = true;
          doc = null;
        }
      } catch (IOException e1) {
        doc = null;
      }
      return (D) doc;
    }
  }

}
