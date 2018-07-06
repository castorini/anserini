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

import io.anserini.document.NewYorkTimesDocument;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an instance of the New York Times Annotated Corpus,
 * <a href="https://catalog.ldc.upenn.edu/products/LDC2008T19">LDC2008T19</a>.
 */
public class NewYorkTimesCollection extends Collection<NewYorkTimesDocument> {
  public class FileSegment extends Collection<NewYorkTimesDocument>.FileSegment {
    // We're creating a parser for each file, just to parse a single document, which is
    // very inefficient. However, the parser is not thread safe, so this is our only option.
    private final NewYorkTimesDocument.Parser parser = new NewYorkTimesDocument.Parser();

    // Each file segment only has one file, boolean to keep track if it's been read.
    private boolean docRead = false;

    protected FileSegment(Path path) throws IOException {
      super.path = path;
    }

    @Override
    public void close() throws IOException {
      docRead = true;
      super.close();
    }

    @Override
    public boolean hasNext() {
      return !docRead;
    }

    @Override
    public NewYorkTimesDocument next() {
      if (docRead) return null;

      NewYorkTimesDocument doc;
      try {
        docRead = true;
        doc = parser.parseFile(path.toFile());
      } catch (IOException e) {
        return null;
      }

      return doc;
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".xml"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
