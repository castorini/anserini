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

import io.anserini.document.ClueWeb09Document;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of the
 * <a href="https://www.lemurproject.org/clueweb09.php/">ClueWeb09 collection</a>.
 * This can be used to read the complete ClueWeb09 collection or the smaller ClueWeb09b subset.
 */
public class ClueWeb09Collection extends Collection {

  /**
   * Represents an individual WARC in the ClueWeb09 collection.
   */
  public class FileSegment extends Collection.FileSegment {
    protected DataInputStream stream;

    protected FileSegment(Path path) throws IOException {
      super.path = path;
      this.stream = new DataInputStream(
          new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ)));
    }

    @Override
    public ClueWeb09Document next() {
      ClueWeb09Document doc;
      try {
        doc = ClueWeb09Document.readNextWarcRecord(stream, ClueWeb09Document.WARC_VERSION);
        if (doc == null) {
          atEOF = true;
        }
      } catch (IOException e) {
        doc = null;
      }
      return doc;
    }

    @Override
    public void close() throws IOException {
      atEOF = true;
      if (stream != null) {
        stream.close();
      }
    }
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".warc.gz"));
    Set<String> skippedDirs = new HashSet<>(Arrays.asList("OtherData"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, allowedFileSuffix, skippedDirs);
  }
}
