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

import io.anserini.document.ClueWeb12WarcRecord;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Class representing an instance of the ClueWeb12 collection.
 */
public class CW12Collection extends WarcCollection {

  public class FileSegment extends WarcCollection.FileSegment {
    public FileSegment(Path path) throws IOException {
      super(path);
      dType = new ClueWeb12WarcRecord();
    }

    @Override
    public ClueWeb12WarcRecord next() {
      ClueWeb12WarcRecord doc = new ClueWeb12WarcRecord();
      try {
        doc = doc.readNextWarcRecord(stream, ClueWeb12WarcRecord.WARC_VERSION);
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
