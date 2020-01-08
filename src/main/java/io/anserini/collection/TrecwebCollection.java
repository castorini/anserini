/*
 * Anserini: A Lucene toolkit for replicable information retrieval research
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
import java.text.ParseException;

/**
 * A classic TREC web collection (e.g., Gov2).
 */
public class TrecwebCollection extends DocumentCollection<TrecwebCollection.Document> {

  private static final Logger LOG = LogManager.getLogger(TrecwebCollection.class);

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new Segment<>(p);
  }

  /**
   * A file in a TREC web collection (e.g., Gov2), typically containing multiple documents.
   *
   * @param <T> type of the document
   */
  public static class Segment<T extends Document> extends TrecCollection.Segment<T> {

    protected Segment(Path path) throws IOException {
      super(path);
    }

    @Override
    public void readNext() throws IOException, ParseException {
        readNextRecord(bufferedReader);
    }

    private void readNextRecord(BufferedReader reader) throws IOException, ParseException {
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
    private void parseRecord(StringBuilder builder) throws ParseException {
      int i = builder.indexOf(Document.DOCNO);
      if (i == -1) throw new ParseException("cannot find start tag " + Document.DOCNO, 0);

      if (i != 0) throw new ParseException("should start with " + Document.DOCNO, 0);

      int j = builder.indexOf(Document.TERMINATING_DOCNO);
      if (j == -1) throw new ParseException("cannot find end tag " + Document.TERMINATING_DOCNO, 0);

      bufferedRecord = (T) new Document();
      bufferedRecord.id = builder.substring(i + Document.DOCNO.length(), j).trim();

      i = builder.indexOf(Document.DOCHDR);
      if (i == -1) throw new ParseException("cannot find header tag " + Document.DOCHDR, 0);

      j = builder.indexOf(Document.TERMINATING_DOCHDR);
      if (j == -1) throw new ParseException("cannot find end tag " + Document.TERMINATING_DOCHDR, 0);

      if (j < i) throw new ParseException(Document.TERMINATING_DOCHDR + " comes before " + Document.DOCHDR, 0);

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
