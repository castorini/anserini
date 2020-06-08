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

// Following header retained per conditions specified in license.

/*
 * Container for a generic Warc Record
 *
 * (C) 2009 - Carnegie Mellon University
 *
 * 1. Redistributions of this source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. The names "Lemur", "Indri", "University of Massachusetts",
 *    "Carnegie Mellon", and "lemurproject" must not be used to
 *    endorse or promote products derived from this software without
 *    prior written permission. To obtain permission, contact
 *    license@lemurproject.org.
 *
 * 4. Products derived from this software may not be called "Lemur" or "Indri"
 *    nor may "Lemur" or "Indri" appear in their names without prior written
 *    permission of The Lemur Project. To obtain permission,
 *    contact license@lemurproject.org.
 *
 * THIS SOFTWARE IS PROVIDED BY THE LEMUR PROJECT AS PART OF THE CLUEWEB09
 * PROJECT AND OTHER CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN
 * NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author mhoy@cs.cmu.edu (Mark J. Hoy)
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
 * An instance of the <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
 * This can be used to read the complete ClueWeb12 collection or the smaller ClueWeb12-B13 subset.
 */
public class ClueWeb12Collection extends DocumentCollection<ClueWeb12Collection.Document> {

  public ClueWeb12Collection(Path path) {
    this.path = path;
    this.allowedFileSuffix = Set.of(".warc.gz");
    this.skippedDir = Set.of("OtherData");
  }

  @Override
  public FileSegment<ClueWeb12Collection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * An individual WARC in the <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
   */
  public static class Segment extends FileSegment<ClueWeb12Collection.Document> {
    protected DataInputStream stream;

    public Segment(Path path) throws IOException {
      super(path);
      this.stream = new DataInputStream(new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ)));
    }

    @Override
    public void readNext() throws IOException, NoSuchElementException {
      bufferedRecord = Document.readNextWarcRecord(stream, Document.WARC_VERSION);
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
   * A document from the
   * <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12
   * collection</a>. This class derives from tools provided by CMU for reading the
   * ClueWeb12 collection. Note that the implementation inherits from
   * {@link ClueWeb09Collection.Document} for historic reasons, since the code
   * originally developed for reading ClueWeb09 was subsequently adapted for
   * reading ClueWeb12.
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
     * @param version WARC version
     * @return a WARC record (or null if EOF)
     * @throws IOException if error encountered reading from stream
     */

    public static Document readNextWarcRecord(DataInputStream in, String version)
        throws IOException {
      StringBuilder recordHeader = new StringBuilder();
      byte[] recordContent = readNextRecord(in, recordHeader, version);

      // extract out our header information
      String thisHeaderString = recordHeader.toString();
      String[] headerLines = thisHeaderString.split(Document.NEWLINE);

      Document retRecord = new Document();
      for (int i = 0; i < headerLines.length; i++) {
        String[] pieces = headerLines[i].split(":", 2);
        if (pieces.length != 2) {
          retRecord.addHeaderMetadata(pieces[0], "");
          continue;
        }
        String thisKey = pieces[0].trim();
        String thisValue = pieces[1].trim();

        // check for known keys
        if (thisKey.equals("WARC-Type")) {
          retRecord.setWarcRecordType(thisValue);
        } else if (thisKey.equals("WARC-Date")) {
          retRecord.setWarcDate(thisValue);
        } else if (thisKey.equals("WARC-Record-ID")) {
          retRecord.setWarcUUID(thisValue);
        } else if (thisKey.equals("Content-Type")) {
          retRecord.setWarcContentType(thisValue);
        } else {
          retRecord.addHeaderMetadata(thisKey, thisValue);
        }
      }

      // set the content
      retRecord.setContent(recordContent);

      return retRecord;
    }

    @Override
    public String getDocid() {
      return getHeaderMetadataItem("WARC-TREC-ID");
    }
  }
}
