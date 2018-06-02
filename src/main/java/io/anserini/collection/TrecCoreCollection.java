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
import io.anserini.document.SourceDocumentResultWrapper;
import io.anserini.document.TrecCoreDocument;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an instance of a TREC Core collection.
 */
public class TrecCoreCollection extends Collection<TrecCoreDocument> {
  public class FileSegment extends Collection.FileSegment {
    private String fileName;

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      this.fileName = path.toString();
    }

    @Override
    public void close() throws IOException {
      atEOF = false;
    }

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public TrecCoreDocument next() {
      TrecCoreDocument doc = new TrecCoreDocument(new File(fileName));
      atEOF = true;
      try {
        doc = doc.readNextRecord(bufferedReader);
        if (doc == null) {
          atEOF = true;
        }
      } catch (Exception e) {
        doc = null;
      }
      return doc;
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".xml"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
            allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

}
