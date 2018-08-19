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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * A classic TREC web collection (e.g., Gov2).
 */
public class TrecwebCollection extends DocumentCollection
    implements SegmentProvider<TrecwebCollection.Document> {

  private static final Logger LOG = LogManager.getLogger(TrecwebCollection.class);

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new FileSegment<>(p);
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    return discover(path, EMPTY_SET, EMPTY_SET,
        EMPTY_SET, EMPTY_SET, EMPTY_SET);
  }

  /**
   * A file in a TREC web collection (e.g., Gov2).
   *
   * @param <T> type of the document
   */
  public static class FileSegment<T extends Document> extends TrecCollection.FileSegment<T> {
    public FileSegment(Path path) throws IOException {
      super(path);
    }

    @Override
    public void readNext() throws IOException {
      readNextRecord(bufferedReader);
    }

    private void readNextRecord(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      boolean found = false;

      String line;
      while ((line=reader.readLine()) != null) {
        line = line.trim();

        if (line.startsWith(Document.DOC)) {
          found = true;
          continue;
        }

        if (line.startsWith(Document.TERMINATING_DOC) && builder.length() > 0) {
          parseRecord(builder);
          return;
        }

        if (found)
          builder.append(line).append("\n");
      }
    }

    @SuppressWarnings("unchecked")
    private void parseRecord(StringBuilder builder) {
      int i = builder.indexOf(Document.DOCNO);
      if (i == -1) throw new RuntimeException("cannot find start tag " + Document.DOCNO);

      if (i != 0) throw new RuntimeException("should start with " + Document.DOCNO);

      int j = builder.indexOf(Document.TERMINATING_DOCNO);
      if (j == -1) throw new RuntimeException("cannot find end tag " + Document.TERMINATING_DOCNO);

      bufferedRecord = (T) new Document();
      bufferedRecord.id = builder.substring(i + Document.DOCNO.length(), j).trim();

      i = builder.indexOf(Document.DOCHDR);
      if (i == -1) throw new RuntimeException("cannot find header tag " + Document.DOCHDR);

      j = builder.indexOf(Document.TERMINATING_DOCHDR);
      if (j == -1) throw new RuntimeException("cannot find end tag " + Document.TERMINATING_DOCHDR);

      if (j < i) throw new RuntimeException(Document.TERMINATING_DOCHDR + " comes before " + Document.DOCHDR);

      bufferedRecord.content = builder.substring(j + Document.TERMINATING_DOCHDR.length()).trim();
    }
  }

  /**
   * A document from a classic TREC web collection (e.g., Gov2).
   */
  public static class Document extends TrecCollection.Document {

    private static final String DOCHDR = "<DOCHDR>";
    private static final String TERMINATING_DOCHDR = "</DOCHDR>";

  }
}
