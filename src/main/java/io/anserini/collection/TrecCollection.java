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
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * <p>A classic TREC <i>ad hoc</i> document collection.</p>
 *
 * <p>This class handles a collection comprising files containing documents of the form:</p>
 *
 * <pre>
 * &lt;DOC&gt;
 * &lt;DOCNO&gt;doc1&lt;/DOCNO&gt;
 * &lt;TEXT&gt;
 * ...
 * &lt;/TEXT&gt;
 * &lt;/DOC&gt;
 * </pre>
 *
 * <p>This class also handles the following alternative format (e.g., for NTCIR-8 ACLIA):</p>
 * <pre>
 * &lt;DOC id="doc1"&gt;
 * &lt;TEXT&gt;
 * ...
 * &lt;/TEXT&gt;
 * &lt;/DOC&gt;
 * </pre>
 *
 * <p>In both cases, compressed files are transparently handled.</p>
 */
public class TrecCollection extends DocumentCollection<TrecCollection.Document> {
  private static final Logger LOG = LogManager.getLogger(TrecCollection.class);

  public TrecCollection(){
    this.skippedFilePrefix = new HashSet<>(Arrays.asList("readme"));
    this.skippedDir = new HashSet<>(Arrays.asList("cr", "dtd", "dtds"));
  }

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new Segment<>(p);
  }

  /**
   * A file in a classic TREC <i>ad hoc</i> document collection, typically containing multiple documents.
   *
   * @param <T> type of the document
   */
  public static class Segment<T extends Document> extends FileSegment<T>{
    private static final Pattern ID_PATTERN = Pattern.compile(".*id=\\\"([^\\\"]+)\\\".*");

    protected Segment(Path path) throws IOException {
      super(path);
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
    public void readNext() throws IOException, ParseException {
      readNextRecord(bufferedReader);
    }

    private void readNextRecord(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      boolean found = false;
      int inTag = -1;

      String line;
      while ((line=reader.readLine()) != null) {
        line = line.trim();

        // Also handle the variant case where docid is an attributed of the <DOC> tag, e.g., <DOC id="abc">
        // The NTCIR-8 ACLIA task, which uses LDC2007T38, is organized in this way.
        if (line.startsWith(Document.DOC) || line.startsWith("<DOC ")) {
          found = true;

          Matcher matcher = ID_PATTERN.matcher(line);
          if (matcher.matches()) {
            // Handle cases like <DOC id="abc">
            builder.append(Document.DOCNO).append(matcher.group(1)).append(Document.TERMINATING_DOCNO);
          } else {
            // Continue to read DOCNO as normal.
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
          }
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
