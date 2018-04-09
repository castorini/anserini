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

import io.anserini.document.ClueWeb09WarcRecord;
import io.anserini.document.SourceDocumentResultWrapper;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Class representing an instance of the ClueWeb09 collection.
*/
public class CW09Collection extends WarcCollection {

  public class FileSegment extends WarcCollection.FileSegment {
    private FileSegment(Path path) throws IOException {
      super(path);
      dType = new ClueWeb09WarcRecord();

    }

    @Override
    public SourceDocumentResultWrapper<ClueWeb09WarcRecord> next() {
      ClueWeb09WarcRecord doc = new ClueWeb09WarcRecord();
      SourceDocumentResultWrapper<ClueWeb09WarcRecord> drw;
      try {
        drw = doc.readNextWarcRecord(stream, ClueWeb09WarcRecord.WARC_VERSION);
        if (!drw.getStatus()) {
          if (drw.getReason() == SourceDocumentResultWrapper.FailureReason.EOF) {
            atEOF = true;
          }
        }
      } catch (IOException e) {
        drw = new SourceDocumentResultWrapper<ClueWeb09WarcRecord>(
            null, false, SourceDocumentResultWrapper.FailureReason.IOError);
      }
      return drw;
    }
  }

  @Override
  public Collection.FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
