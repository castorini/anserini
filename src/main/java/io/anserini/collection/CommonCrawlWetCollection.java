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
 *
 */

package io.anserini.collection;

import org.apache.logging.log4j.LogManager;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * A collection of WET files from CommonCrawl (https://commoncrawl.org/the-data/get-started/#WET-Format).
 * This can be used to read the CommonCrawl WET files
 */
public class CommonCrawlWetCollection extends DocumentCollection<CommonCrawlWetCollection.Document> {

  public CommonCrawlWetCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".warc.wet.gz");
  }

  @Override
  public FileSegment<CommonCrawlWetCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * An individual WARC in CommonCrawl.
   */
  public static class Segment extends FileSegment<CommonCrawlWetCollection.Document> {

    protected DataInputStream stream;

    public Segment(Path path) throws IOException {
      super(path);
      this.stream = new DataInputStream(new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ)));
    }

    @Override
    public void readNext() throws IOException, NoSuchElementException {
      bufferedRecord = Document.readNextWarcRecord(stream);
    }

    @Override
    public void close() {
      try {
        if (stream != null) {
          stream.close();
        }
        super.close();
      } catch (IOException e) {
        // There's really nothing to be done, so just silently eat the exception.
      }
    }
  }

  /**
   * A document from the <a href="https://commoncrawl.org/the-data/get-started/#WET-Format/">
   *   CommonCrawl WET collection</a>.
   */
  public static class Document extends WarcBaseDocument {
    static {
      LOG = LogManager.getLogger(Document.class);
      WARC_VERSION = "WARC/1.0";
    }

    /**
     * Reads in a WARC record from a data input stream.
     *
     * @param in      the input stream
     * @return a WARC record (or null if EOF)
     * @throws IOException if error encountered reading from stream
     */

    public static Document readNextWarcRecord(DataInputStream in)
        throws IOException {
      StringBuilder recordHeader = new StringBuilder();
      byte[] recordContent = readNextRecord(in, recordHeader, "Content-Length");

      Document retRecord = new Document();
      //set the header
      retRecord.setHeader(recordHeader.toString());
      // set the content
      retRecord.setContent(recordContent);

      return retRecord;
    }

    /**
     * Performs the actual heavy lifting of reading in the next WARC record.
     *
     * @param in the data input stream
     * @param headerBuffer a blank string buffer to contain the WARC header
     * @return the content bytes (with the headerBuffer populated)
     * @throws IOException if error encountered reading from stream
     */
    protected static byte[] readNextRecord(DataInputStream in, StringBuilder headerBuffer, String headerEndKey) throws IOException {
      if (in == null || headerBuffer == null) {
        throw new NoSuchElementException();
      }

      String line = null;
      boolean foundMark = false;
      boolean inHeader = true;
      byte[] retContent = null;

      // read WARC header
      // first - find the beginning of WARC header
      while ((!foundMark) && ((line = readLineFromInputStream(in)) != null)) {
        if (line.startsWith(WARC_VERSION)) {
          foundMark = true;
        }
      }

      // no WARC mark?
      if (!foundMark) {
        throw new NoSuchElementException();
      }

      // then read to the first newline
      // make sure we get the content length here
      int contentLength = -1;
      boolean reachHeaderEnd = false;
      while (!reachHeaderEnd && inHeader && ((line = readLineFromInputStream(in)) != null)) {
        if ((line.trim().length() == 0 && reachHeaderEnd)) {
          inHeader = false;
        } else {
          headerBuffer.append(line);
          headerBuffer.append(WarcBaseDocument.NEWLINE);
          String[] thisHeaderPieceParts = line.split(":", 2);
          if (thisHeaderPieceParts.length == 2) {
            if (thisHeaderPieceParts[0].toLowerCase(Locale.US).startsWith(headerEndKey.toLowerCase(Locale.US))){
              reachHeaderEnd = true;
            }
            if (thisHeaderPieceParts[0].toLowerCase(Locale.US).startsWith("content-length")) {
              try {
                contentLength = Integer.parseInt(thisHeaderPieceParts[1].trim()) + 1;
              } catch (NumberFormatException nfEx) {
                contentLength = -1;
              }
            }
          }
        }
      }

      if (contentLength < 0) {
        throw new NoSuchElementException();
      }

      // now read the bytes of the content
      retContent = new byte[contentLength];
      int totalWant = contentLength;
      int totalRead = 0;
      while (totalRead < contentLength) {
        try {
          int numRead = in.read(retContent, totalRead, totalWant);
          if (numRead < 0) {
            throw new NoSuchElementException();
          } else {
            totalRead += numRead;
            totalWant = contentLength - totalRead;
          } // end if (numRead < 0) / else
        } catch (EOFException eofEx) {
          // resize to what we have
          if (totalRead > 0) {
            byte[] newReturn = new byte[totalRead];
            System.arraycopy(retContent, 0, newReturn, 0, totalRead);
            return newReturn;
          } else {
            throw new NoSuchElementException();
          }
        } // end try/catch (EOFException)
      } // end while (totalRead < contentLength)

      return retContent;
    }

    @Override
    public String contents() {
      return getContent();
    }

    @Override
    public String raw() {
      return getContent();
    }

    @Override
    public String getContent() {
      return getContentUTF8().trim();
    }

    @Override
    public boolean indexable() {
      return "conversion".equals(getHeaderRecordType());
    }

    @Override
    public String getDocid() {
      return getHeaderMetadataItem("WARC-Refers-To");
    }
  }
}