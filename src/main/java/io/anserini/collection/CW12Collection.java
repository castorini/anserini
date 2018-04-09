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
import io.anserini.document.SourceDocumentResultWrapper;

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
    public SourceDocumentResultWrapper<ClueWeb12WarcRecord> next() {
      ClueWeb12WarcRecord doc = new ClueWeb12WarcRecord();
      SourceDocumentResultWrapper<ClueWeb12WarcRecord> drw;
      try {
        drw = doc.readNextWarcRecord(stream, ClueWeb12WarcRecord.WARC_VERSION);
        if (!drw.getDocument().isPresent()) {
          if (drw.getReason() == SourceDocumentResultWrapper.FailureReason.EOF) {
            atEOF = true;
          }
        }
      } catch (IOException e) {
        drw = new SourceDocumentResultWrapper<ClueWeb12WarcRecord>(
            null, SourceDocumentResultWrapper.FailureReason.IOError);
      }
      return drw;
    }
  }

  @Override
  public Collection.FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
