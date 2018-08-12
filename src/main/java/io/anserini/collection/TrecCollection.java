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

import org.apache.commons.compress.compressors.z.ZCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPInputStream;

/**
 * A classic TREC <i>ad hoc</i> document collection.
 */
public class TrecCollection extends DocumentCollection
    implements SegmentProvider<TrecCollection.Document> {

  private static final Logger LOG = LogManager.getLogger(TrecCollection.class);

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> skippedFilePrefix = new HashSet<>(Arrays.asList("readme"));
    Set<String> skippedDirs = new HashSet<>(Arrays.asList("cr", "dtd", "dtds"));

    return discover(path, skippedFilePrefix, EMPTY_SET,
        EMPTY_SET, EMPTY_SET, skippedDirs);
  }

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new FileSegment<>(p);
  }

  /**
   * A file in a classic TREC <i>ad hoc</i> document collection.
   *
   * @param <T> type of the document
   */
  public static class FileSegment<T extends Document> extends BaseFileSegment<T> {
    @SuppressWarnings("unchecked")
    public FileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.matches("(?i:.*?\\.\\d*z$)")) { // .z .0z .1z .2z
        FileInputStream fin = new FileInputStream(fileName);
        BufferedInputStream in = new BufferedInputStream(fin);
        ZCompressorInputStream zIn = new ZCompressorInputStream(in);
        bufferedReader = new BufferedReader(new InputStreamReader(zIn, StandardCharsets.UTF_8));
      } else if (fileName.endsWith(".gz")) { //.gz
        InputStream stream = new GZIPInputStream(
            Files.newInputStream(path, StandardOpenOption.READ), BUFFER_SIZE);
        bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
      } else { // plain text file
        bufferedReader = new BufferedReader(new FileReader(fileName));
      }
    }

    @Override
    public void readNext() throws IOException {
      readNextRecord(bufferedReader);
    }

    private void readNextRecord(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      boolean found = false;
      int inTag = -1;

      String line;
      while ((line=reader.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(Document.DOC)) {
          found = true;
          // continue to read DOCNO
          while ((line = reader.readLine()) != null) {
            if (line.startsWith(Document.DOCNO)) {
              builder.append(line).append('\n');
              break;
            }
          }
          while (builder.indexOf(Document.TERMINATING_DOCNO) == -1) {
            line = reader.readLine();
            if (line == null) break;
            builder.append(line).append('\n');
          }
          continue;
        }

        if (found) {
          if (line.startsWith("<")) {
            if (inTag >= 0 && line.startsWith(Document.endTags[inTag])) {
              builder.append(line).append("\n");
              inTag = -1;
            } else if (inTag < 0) {
              for (int k = 0; k < Document.startTags.length; k++) {
                if (line.startsWith(Document.startTags[k])) {
                  inTag = k;
                  break;
                }
              }
            }
          }
          if (inTag >= 0) {
            if (line.endsWith(Document.endTags[inTag])) {
              builder.append(line).append("\n");
              inTag = -1;
            } else {
              builder.append(line).append("\n");
            }
          }
        }

        if (line.startsWith(Document.TERMINATING_DOC)) {
          parseRecord(builder);
          return;
        }
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
      bufferedRecord.content = builder.substring(j + Document.TERMINATING_DOCNO.length()).trim();
    }
  }

  /**
   * A document in a classic TREC <i>ad hoc</i> document collection.
   */
  public static class Document implements SourceDocument {

    protected static final String DOCNO = "<DOCNO>";
    protected static final String TERMINATING_DOCNO = "</DOCNO>";

    protected static final String DOC = "<DOC>";
    protected static final String TERMINATING_DOC = "</DOC>";

    protected final int BUFFER_SIZE = 1 << 16; // 64K

    private static final String[] startTags = {"<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
        "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>"
    };
    private static final String[] endTags = {"</TEXT>", "</HEADLINE>", "</TITLE>", "</HL>", "</HEAD>",
        "</TTL>", "</DD>", "</DATE>", "</LP>", "</LEADPARA>"
    };

    protected String id;
    protected String content;

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
      return content;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
