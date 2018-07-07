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

import io.anserini.document.ClueWeb12Document;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Class representing an instance of the
 * <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
 * This can be used to read the complete ClueWeb12 collection or the smaller ClueWeb12-B13 subset.
 * Note that the implementation inherits from {@link ClueWeb09Collection} because
 * {@link ClueWeb12Document} inherits from {@link io.anserini.document.ClueWeb09Document}.
 */
public class ClueWeb12Collection extends ClueWeb09Collection {

  /**
   * Represents an individual WARC in the ClueWeb12 collection.
   */
  public class FileSegment extends ClueWeb09Collection.FileSegment {
    private FileSegment(Path path) throws IOException {
      super(path);
    }

    @Override
    public ClueWeb12Document next() {
      ClueWeb12Document doc;
      try {
        doc = ClueWeb12Document.readNextWarcRecord(stream, ClueWeb12Document.WARC_VERSION);
        if (doc == null) {
          atEOF = true;
        }
      } catch (IOException e) {
        doc = null;
      }
      return doc;
    }
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
