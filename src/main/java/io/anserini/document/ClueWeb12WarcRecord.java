package io.anserini.document;

/*
 * ClueWeb Tools: Hadoop tools for manipulating ClueWeb collections
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

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


import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;


public final class ClueWeb12WarcRecord implements WarcRecord {

  public static String WARC_VERSION = "WARC/1.0";
  public static String WARC_VERSION_LINE = "WARC/1.0\n";
  private static String NEWLINE = "\n";

  /**
   * Reads in a WARC record from a data input stream
   *
   * @param in the input stream
   * @return a WARC record (or null if eof)
   * @throws java.io.IOException
   */
  public static ClueWeb12WarcRecord readNextWarcRecord(DataInputStream in, String WARC_VERSION) throws IOException {
    StringBuilder recordHeader = new StringBuilder();
    byte[] recordContent = ClueWeb09WarcRecord.readNextRecord(in, recordHeader, WARC_VERSION);
    if (recordContent == null) {
      return null;
    }

    // extract out our header information
    String thisHeaderString = recordHeader.toString();
    String[] headerLines = thisHeaderString.split(NEWLINE);

    ClueWeb12WarcRecord retRecord = new ClueWeb12WarcRecord();
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

  @Override
  public String id() {
    return getDocid();
  }

  @Override
  public String type() {
    return getWARCType();
  }

  @Override
  public String content() {
    return getContent();
  }

  @Override
  public String url() {
    return getURL();
  }

  /**
   * Warc header class
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
     * Default constructor
     */
    public WarcHeader() {
    }

    /**
     * Copy Constructor
     *
     * @param o other WARC header
     */
    public WarcHeader(WarcHeader o) {
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
     * Serialization output
     *
     * @param out the data output stream
     * @throws java.io.IOException
     */
    public void write(DataOutput out) throws IOException {
      out.writeUTF(contentType);
      out.writeUTF(UUID);
      out.writeUTF(dateString);
      out.writeUTF(recordType);
      out.writeUTF(warcTrecId);
      out.writeUTF(warcTrecUrl);
      out.writeInt(metadata.size());
      Iterator<Entry<String, String>> metadataIterator = metadata
              .entrySet().iterator();
      while (metadataIterator.hasNext()) {
        Entry<String, String> thisEntry = metadataIterator.next();
        out.writeUTF(thisEntry.getKey());
        out.writeUTF(thisEntry.getValue());
      }
      out.writeInt(contentLength);
    }

    /**
     * Serialization input
     *
     * @param in the data input stream
     * @throws java.io.IOException
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
      Iterator<Entry<String, String>> metadataIterator = metadata
              .entrySet().iterator();
      while (metadataIterator.hasNext()) {
        Entry<String, String> thisEntry = metadataIterator.next();
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

  private WarcHeader warcHeader = new WarcHeader();
  private byte[] warcContent = null;
  private String warcFilePath = "";

  /**
   * Default Constructor
   */
  public ClueWeb12WarcRecord() {
  }

  /**
   * Copy Constructor
   *
   * @param o
   */
  public ClueWeb12WarcRecord(ClueWeb12WarcRecord o) {
    this.warcHeader = new WarcHeader(o.warcHeader);
    this.warcContent = o.warcContent;
  }

  /**
   * Retrieves the total record length (header and content)
   *
   * @return total record length
   */
  public int getTotalRecordLength() {
    int headerLength = warcHeader.toString().length();
    return (headerLength + warcContent.length);
  }

  /**
   * Sets the record content (copy)
   *
   * @param o record to copy from
   */
  public void set(ClueWeb12WarcRecord o) {
    this.warcHeader = new WarcHeader(o.warcHeader);
    this.warcContent = o.warcContent;
  }

  /**
   * Gets the file path from this WARC file (if set)
   */
  public String getWarcFilePath() {
    return warcFilePath;
  }

  /**
   * Sets the warc file path (optional - for use with getWarcFilePath)
   *
   * @param path
   */
  public void setWarcFilePath(String path) {
    warcFilePath = path;
  }

  /**
   * Sets the record type string
   *
   * @param recordType
   */
  public void setWarcRecordType(String recordType) {
    warcHeader.recordType = recordType;
  }

  /**
   * Sets the content type string
   *
   * @param contentType
   */
  public void setWarcContentType(String contentType) {
    warcHeader.contentType = contentType;
  }

  /**
   * Sets the WARC header date string
   *
   * @param dateString
   */
  public void setWarcDate(String dateString) {
    warcHeader.dateString = dateString;
  }

  /**
   * Sets the WARC uuid string
   *
   * @param UUID
   */
  public void setWarcUUID(String UUID) {
    warcHeader.UUID = UUID;
  }

  /**
   * Sets the WARC TREC ID
   *
   * @param warcTrecID
   */
  public void setWarcTrecID(String warcTrecID) {
    warcHeader.warcTrecId = warcTrecID;
  }

  /**
   * Sests the WARC URL
   *
   * @param warcUrl
   */
  public void setWarcUrl(String warcUrl) {

    warcHeader.warcTrecUrl = warcUrl;
  }

  /**
   * Adds a key/value pair to a WARC header. This is needed to filter out
   * known keys
   *
   * @param key
   * @param value
   */
  public void addHeaderMetadata(String key, String value) {
    // add all keys to the metadata keys


    warcHeader.metadata.put(key, value);
  }

  /**
   * Clears all metadata items from a header
   */
  public void clearHeaderMetadata() {
    warcHeader.metadata.clear();
  }

  /**
   * Gets the set of metadata items from the header
   */
  public Set<Entry<String, String>> getHeaderMetadata() {
    return warcHeader.metadata.entrySet();
  }

  /**
   * Gets a value for a specific header metadata key
   *
   * @param key
   */
  public String getHeaderMetadataItem(String key) {
    return warcHeader.metadata.get(key);
  }

  /**
   * Sets the byte content for this record
   *
   * @param content
   */
  public void setContent(byte[] content) {
    warcContent = content;
    warcHeader.contentLength = content.length;
  }

  /**
   * Sets the byte content for this record
   *
   * @param content
   */
  public void setContent(String content) {
    setContent(content.getBytes());
  }

  /**
   * Retrieves the byte content for this record
   */
  public byte[] getByteContent() {
    return warcContent;
  }

  /**
   * Retrieves the bytes content as a UTF-8 string
   */
  public String getContentUTF8() {
    return new String(warcContent, StandardCharsets.UTF_8);
  }

  /**
   * Gets the header record type string
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
   * Gets the WARC header as a string
   */
  public String getHeaderString() {
    return warcHeader.toString();
  }

  /**
   * Serialization output
   *
   * @param out
   * @throws java.io.IOException
   */
  public void write(DataOutput out) throws IOException {
    warcHeader.write(out);
    out.write(warcContent);
  }

  /**
   * Serialization input
   *
   * @param in
   * @throws java.io.IOException
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

  public String getDisplayContentType() {
    return "text/html";
  }
}