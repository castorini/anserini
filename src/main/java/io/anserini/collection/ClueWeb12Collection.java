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
import org.apache.logging.log4j.Logger;
import org.apache.tools.ant.filters.StringInputStream;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.NoSuchElementException;

/**
 * An instance of the <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
 * This can be used to read the complete ClueWeb12 collection or the smaller ClueWeb12-B13 subset.
 */
public class ClueWeb12Collection extends DocumentCollection<ClueWeb12Collection.Document> {
  private static final Logger LOG = LogManager.getLogger(ClueWeb12Collection.class);

  public ClueWeb12Collection(){
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".warc.gz"));
    this.skippedDir = new HashSet<>(Arrays.asList("OtherData"));
  }

  @Override
  public FileSegment<ClueWeb12Collection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  public FileSegment<ClueWeb12Collection.Document> createFileSegment(String raw) {
    return new Segment(raw);
  }

  /**
   * An individual WARC in the <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
   */
  public static class Segment extends FileSegment<ClueWeb12Collection.Document> {
    protected DataInputStream stream;

    protected Segment(Path path) throws IOException {
      super(path);
      this.stream = new DataInputStream(
              new GZIPInputStream(Files.newInputStream(path, StandardOpenOption.READ)));
    }

    protected Segment(String raw) {
      super(null);
      this.stream = new DataInputStream(new StringInputStream(raw));
    }

    @Override
    public void readNext() throws IOException, NoSuchElementException {
      bufferedRecord = readNextWarcRecord(stream, Document.WARC_VERSION);
    }

    @Override
    public void close() throws IOException {
      if (stream != null) {
        stream.close();
      }
      super.close();
    }

    /**
     * Reads in a WARC record from a data input stream.
     *
     * @param in the input stream
     * @param version WARC version
     * @return a WARC record (or null if EOF)
     * @throws IOException if error encountered reading from stream
     */
    public static Document readNextWarcRecord(DataInputStream in, String version)
        throws IOException {
      StringBuilder recordHeader = new StringBuilder();
      byte[] recordContent = ClueWeb09Collection.Segment.readNextRecord(in, recordHeader, version);
      if (recordContent == null) {
        return null;
      }

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

        retRecord.addHeaderMetadata(thisKey, thisValue);
      }

      // set the content
      retRecord.setContent(recordContent);

      return retRecord;
    }
  }

  /**
   * A document from the <a href="https://www.lemurproject.org/clueweb12.php/">ClueWeb12 collection</a>.
   * This class derives from tools provided by CMU for reading the ClueWeb12 collection. Note that
   * the implementation inherits from {@link ClueWeb09Collection.Document} for historic reasons, since the code
   * originally developed for reading ClueWeb09 was subsequently adapted for reading ClueWeb12.
   */
  public static class Document extends ClueWeb09Collection.Document {
    public static final String WARC_VERSION = "WARC/1.0";

    @Override
    public String id() {
      return getDocid();
    }

    @Override
    public String content() {
      return getContent();
    }

    @Override
    public boolean indexable() {
      return "response".equals(getWARCType());
    }

    /**
     * WARC header class.
     */
    public class WarcHeader {
      public String contentType = "";
      public String UUID = "";
      public String dateString = "";
      public String recordType = "";
      public HashMap<String, String> metadata = new HashMap<>();
      public int contentLength = 0;
      public String warcTrecId = "";
      public String warcTrecUrl = "";

      /**
       * Default constructor.
       */
      public WarcHeader() {
      }

      /**
       * Copy Constructor.
       *
       * @param o other WARC header
       */
      public WarcHeader(Document.WarcHeader o) {
        this.contentType = o.contentType;
        this.UUID = o.UUID;
        this.dateString = o.dateString;
        this.recordType = o.recordType;
        this.metadata.putAll(o.metadata);
        this.contentLength = o.contentLength;
        this.warcTrecId = o.warcTrecId;
        this.warcTrecUrl = o.warcTrecUrl;
      }

      /**
       * Serializes this header.
       *
       * @param out output
       * @throws IOException if error encountered during serialization
       */
      public void write(DataOutput out) throws IOException {
        out.writeUTF(contentType);
        out.writeUTF(UUID);
        out.writeUTF(dateString);
        out.writeUTF(recordType);
        out.writeUTF(warcTrecId);
        out.writeUTF(warcTrecUrl);
        out.writeInt(metadata.size());
        Iterator<Map.Entry<String, String>> metadataIterator = metadata
            .entrySet().iterator();
        while (metadataIterator.hasNext()) {
          Map.Entry<String, String> thisEntry = metadataIterator.next();
          out.writeUTF(thisEntry.getKey());
          out.writeUTF(thisEntry.getValue());
        }
        out.writeInt(contentLength);
      }

      /**
       * Deserializes this header.
       *
       * @param in input
       * @throws IOException if error encountered during deserialization
       */
      public void readFields(DataInput in) throws IOException {
        contentType = in.readUTF();
        UUID = in.readUTF();
        dateString = in.readUTF();
        recordType = in.readUTF();
        warcTrecId = in.readUTF();
        warcTrecUrl = in.readUTF();
        metadata.clear();
        int numMetaItems = in.readInt();
        for (int i = 0; i < numMetaItems; i++) {
          String thisKey = in.readUTF();
          String thisValue = in.readUTF();
          metadata.put(thisKey, thisValue);
        }
        contentLength = in.readInt();
      }

      @Override
      public String toString() {
        StringBuilder retBuffer = new StringBuilder();

        retBuffer.append(WARC_VERSION);
        retBuffer.append(NEWLINE);

        retBuffer.append("WARC-Type: " + recordType + NEWLINE);
        retBuffer.append("WARC-Date: " + dateString + NEWLINE);
        retBuffer.append("WARC-TREC-ID: " + warcTrecId + NEWLINE);
        retBuffer.append("WARC-Target-URI: " + warcTrecUrl + NEWLINE);
        retBuffer.append("WARC-Record-ID: " + UUID + NEWLINE);
        Iterator<Map.Entry<String, String>> metadataIterator = metadata
            .entrySet().iterator();
        while (metadataIterator.hasNext()) {
          Map.Entry<String, String> thisEntry = metadataIterator.next();
          retBuffer.append(thisEntry.getKey());
          retBuffer.append(": ");
          retBuffer.append(thisEntry.getValue());
          retBuffer.append(NEWLINE);
        }

        retBuffer.append("Content-Type: " + contentType + NEWLINE);
        retBuffer.append("Content-Length: " + contentLength + NEWLINE);

        return retBuffer.toString();
      }
    }

    private Document.WarcHeader warcHeader = new Document.WarcHeader();
    private byte[] warcContent = null;
    private String warcFilePath = "";

    /**
     * Default Constructor.
     */
    public Document() {
    }

    /**
     * Copy Constructor.
     *
     * @param o record to copy from
     */
    public Document(Document o) {
      this.warcHeader = new Document.WarcHeader(o.warcHeader);
      this.warcContent = o.warcContent;
    }

    /**
     * Returns the total record length (header and content).
     *
     * @return total record length
     */
    public int getTotalRecordLength() {
      int headerLength = warcHeader.toString().length();
      return (headerLength + warcContent.length);
    }

    /**
     * Sets the record content (copy).
     *
     * @param o record to copy from
     */
    public void set(Document o) {
      this.warcHeader = new Document.WarcHeader(o.warcHeader);
      this.warcContent = o.warcContent;
    }

    /**
     * Gets the file path of this WARC file (if set).
     *
     * @return file path of this WARC file
     */
    public String getWarcFilePath() {
      return warcFilePath;
    }

    /**
     * Sets the WARC file path. Optional, for use with {@link #getWarcFilePath()}.
     *
     * @param path path
     */
    public void setWarcFilePath(String path) {
      warcFilePath = path;
    }

    /**
     * Sets the record type string.
     *
     * @param recordType record type
     */
    public void setWarcRecordType(String recordType) {
      warcHeader.recordType = recordType;
    }

    /**
     * Sets the content type string
     *
     * @param contentType content type
     */
    public void setWarcContentType(String contentType) {
      warcHeader.contentType = contentType;
    }

    /**
     * Sets the WARC header date string.
     *
     * @param dateString date string
     */
    public void setWarcDate(String dateString) {
      warcHeader.dateString = dateString;
    }

    /**
     * Sets the WARC uuid string
     *
     * @param UUID uuid string
     */
    public void setWarcUUID(String UUID) {
      warcHeader.UUID = UUID;
    }

    /**
     * Sets the WARC TREC ID.
     *
     * @param warcTrecID TREC ID
     */
    public void setWarcTrecID(String warcTrecID) {
      warcHeader.warcTrecId = warcTrecID;
    }

    /**
     * Sets the WARC URL.
     *
     * @param warcUrl URL
     */
    public void setWarcUrl(String warcUrl) {
      warcHeader.warcTrecUrl = warcUrl;
    }

    /**
     * Adds a key/value pair to a WARC header. This is needed to filter out known keys.
     *
     * @param key key
     * @param value value
     */
    public void addHeaderMetadata(String key, String value) {
      // add all keys to the metadata keys

      warcHeader.metadata.put(key, value);
    }

    /**
     * Clears all metadata items from a header.
     */
    public void clearHeaderMetadata() {
      warcHeader.metadata.clear();
    }

    /**
     * Returns the set of metadata items from the header.
     *
     * @return metadata from the header
     */
    public Set<Map.Entry<String, String>> getHeaderMetadata() {
      return warcHeader.metadata.entrySet();
    }

    /**
     * Returns the value for a specific header metadata key.
     *
     * @param key key
     * @return value for a metadata key
     */
    public String getHeaderMetadataItem(String key) {
      return warcHeader.metadata.get(key);
    }

    /**
     * Sets the byte content for this record.
     *
     * @param content content
     */
    public void setContent(byte[] content) {
      warcContent = content;
      warcHeader.contentLength = content.length;
    }

    /**
     * Sets the byte content for this record.
     *
     * @param content content
     */
    public void setContent(String content) {
      setContent(content.getBytes());
    }

    /**
     * Returns the byte content for this record.
     *
     * @return byte content of this record
     */
    public byte[] getByteContent() {
      return warcContent;
    }

    /**
     * Returns the byte content as a UTF-8 string.
     *
     * @return byte content as a UTF-8 string
     */
    public String getContentUTF8() {
      return new String(warcContent, StandardCharsets.UTF_8);
    }

    /**
     * Returns the header record type string.
     *
     * @return header record type
     */
    public String getHeaderRecordType() {
      return warcHeader.recordType;
    }

    @Override
    public String toString() {
      StringBuilder retBuffer = new StringBuilder();
      retBuffer.append(warcHeader.toString());
      retBuffer.append(NEWLINE);
      retBuffer.append(warcContent);
      return retBuffer.toString();
    }

    /**
     * Returns the WARC header as a string.
     *
     * @return WARC header as a string
     */
    public String getHeaderString() {
      return warcHeader.toString();
    }

    /**
     * Serializes this record.
     *
     * @param out output
     * @throws IOException if error encountered during serialization
     */
    public void write(DataOutput out) throws IOException {
      warcHeader.write(out);
      out.write(warcContent);
    }

    /**
     * Deserializes this record.
     *
     * @param in input
     * @throws IOException if error encountered during deserialization
     */
    public void readFields(DataInput in) throws IOException {
      warcHeader.readFields(in);
      int contentLengthBytes = warcHeader.contentLength;
      warcContent = new byte[contentLengthBytes];
      in.readFully(warcContent);
    }

    public String getDocid() {
      return getHeaderMetadataItem("WARC-TREC-ID");
    }

    public String getURL() {
      return getHeaderMetadataItem("WARC-Target-URI");
    }

    public String getWARCType() {
      return getHeaderMetadataItem("WARC-Type");
    }

    public String getContent() {
      String str = getContentUTF8();
      int i = str.indexOf("Content-Length:");
      int j = str.indexOf("\n", i);

      // Get rid of HTTP headers. Look for the first '<'.
      int k = str.indexOf("<", j);

      return k != -1 ? str.substring(k) : str.substring(j + 1);
    }
  }
}
