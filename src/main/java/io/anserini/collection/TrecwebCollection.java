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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Class representing an instance of a TREC web collection.
 */
public class TrecwebCollection extends DocumentCollection
    implements FileSegmentProvider<TrecwebCollection.Document> {

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new FileSegment<>(p);
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    return discover(path, EMPTY_SET, EMPTY_SET,
        EMPTY_SET, EMPTY_SET, EMPTY_SET);
  }

  public static class FileSegment<T extends Document> extends TrecCollection.FileSegment<T> {
    @SuppressWarnings("unchecked")
    public FileSegment(Path path) throws IOException {
      super(path);
      dType = (T) new Document();
    }
  }

  /**
   * A document from the GOV2 collection.
   */
  public static class Document extends TrecCollection.Document {

    private final String DOCHDR = "<DOCHDR>";
    private final String TERMINATING_DOCHDR = "</DOCHDR>";

    @Override
    public Document readNextRecord(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      boolean found = false;

      String line;
      while ((line=reader.readLine()) != null) {
        line = line.trim();

        if (line.startsWith(DOC)) {
          found = true;
          continue;
        }

        if (line.startsWith(TERMINATING_DOC) && builder.length() > 0) {
          return parseRecord(builder);
        }

        if (found)
          builder.append(line).append("\n");
      }
      return null;
    }

    @Override
    public Document parseRecord(StringBuilder builder) {

      int i = builder.indexOf(DOCNO);
      if (i == -1) throw new RuntimeException("cannot find start tag " + DOCNO);

      if (i != 0) throw new RuntimeException("should start with " + DOCNO);

      int j = builder.indexOf(TERMINATING_DOCNO);
      if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

      id = builder.substring(i + DOCNO.length(), j).trim();

      i = builder.indexOf(DOCHDR);
      if (i == -1) throw new RuntimeException("cannot find header tag " + DOCHDR);

      j = builder.indexOf(TERMINATING_DOCHDR);
      if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCHDR);

      if (j < i) throw new RuntimeException(TERMINATING_DOCHDR + " comes before " + DOCHDR);

      content = builder.substring(j + TERMINATING_DOCHDR.length()).trim();

      return this;
    }
  }
}
