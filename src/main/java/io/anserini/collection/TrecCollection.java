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
 * Class representing an instance of a TREC collection.
 */
public class TrecCollection extends DocumentCollection
    implements FileSegmentProvider<TrecCollection.Document> {

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

  public static class FileSegment<T extends Document> extends AbstractFileSegment<T> {
    @SuppressWarnings("unchecked")
    public FileSegment(Path path) throws IOException {
      dType = (T) new Document();

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
  }

  /**
   * A TREC document.
   */
  public static class Document implements SourceDocument {

    protected final String DOCNO = "<DOCNO>";
    protected final String TERMINATING_DOCNO = "</DOCNO>";

    protected final String DOC = "<DOC>";
    protected final String TERMINATING_DOC = "</DOC>";

    protected final int BUFFER_SIZE = 1 << 16; // 64K

    private final String[] startTags = {"<TEXT>", "<HEADLINE>", "<TITLE>", "<HL>", "<HEAD>",
        "<TTL>", "<DD>", "<DATE>", "<LP>", "<LEADPARA>"
    };
    private final String[] endTags = {"</TEXT>", "</HEADLINE>", "</TITLE>", "</HL>", "</HEAD>",
        "</TTL>", "</DD>", "</DATE>", "</LP>", "</LEADPARA>"
    };

    protected String id;
    protected String content;

    @Override
    public Document readNextRecord(BufferedReader reader) throws IOException {
      StringBuilder builder = new StringBuilder();
      boolean found = false;
      int inTag = -1;

      String line;
      while ((line=reader.readLine()) != null) {
        line = line.trim();
        if (line.startsWith(DOC)) {
          found = true;
          // continue to read DOCNO
          while ((line = reader.readLine()) != null) {
            if (line.startsWith(DOCNO)) {
              builder.append(line).append('\n');
              break;
            }
          }
          while (builder.indexOf(TERMINATING_DOCNO) == -1) {
            line = reader.readLine();
            if (line == null) break;
            builder.append(line).append('\n');
          }
          continue;
        }

        if (found) {
          if (line.startsWith("<")) {
            if (inTag >= 0 && line.startsWith(endTags[inTag])) {
              builder.append(line).append("\n");
              inTag = -1;
            } else if (inTag < 0) {
              for (int k = 0; k < startTags.length; k++) {
                if (line.startsWith(startTags[k])) {
                  inTag = k;
                  break;
                }
              }
            }
          }
          if (inTag >= 0) {
            if (line.endsWith(endTags[inTag])) {
              builder.append(line).append("\n");
              inTag = -1;
            } else {
              builder.append(line).append("\n");
            }
          }
        }

        if (line.startsWith(TERMINATING_DOC)) {
          return parseRecord(builder);
        }
      }

      return null;
    }

    public Document parseRecord(StringBuilder builder) {
      int i = builder.indexOf(DOCNO);
      if (i == -1) throw new RuntimeException("cannot find start tag " + DOCNO);

      if (i != 0) throw new RuntimeException("should start with " + DOCNO);

      int j = builder.indexOf(TERMINATING_DOCNO);
      if (j == -1) throw new RuntimeException("cannot find end tag " + TERMINATING_DOCNO);

      id = builder.substring(i + DOCNO.length(), j).trim();
      content = builder.substring(j + TERMINATING_DOCNO.length()).trim();

      return this;
    }

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
