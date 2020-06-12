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
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * A collection of WARC files from CommonCrawl (https://commoncrawl.org/the-data/get-started/#WARC-Format).
 * This can be used to read the CommonCrawl WARC files
 */
public class CommonCrawlWarcCollection extends DocumentCollection<CommonCrawlWarcCollection.Document> {

  public CommonCrawlWarcCollection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".warc.gz");
  }

  @Override
  public FileSegment<CommonCrawlWarcCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * An individual WARC in CommonCrawl.
   */
  public static class Segment extends FileSegment<CommonCrawlWarcCollection.Document> {

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
   *
   * A document from the <a href="https://commoncrawl.org/the-data/get-started/#WARC-Format/">
   * CommonCrawl WARC collection</a>.
   *
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
      byte[] recordContent = readNextRecord(in, recordHeader, "WARC-Block-Digest");

      Document retRecord = new Document();
      //set the header
      retRecord.setHeader(recordHeader.toString());
      // set the content
      retRecord.setContent(recordContent);

      return retRecord;
    }
  }
}
