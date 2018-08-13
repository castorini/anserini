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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A collection of HTML documents.
 * The file name (excluding the extension) will be the docid and the stripped contents will be the contents.
 * Please note that we intentionally do not apply any restrictions on what the file extension should be --
 * this makes the class a more generic class for indexing other types of the files, e.g. plain text files.
 */
public class HtmlCollection extends DocumentCollection
    implements SegmentProvider<HtmlCollection.Document> {

  private static final Logger LOG = LogManager.getLogger(HtmlCollection.class);

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET, EMPTY_SET, EMPTY_SET);
  }

  public class FileSegment extends BaseFileSegment<Document> {
    private TarArchiveInputStream inputStream = null;
    private ArchiveEntry nextEntry = null;

    @SuppressWarnings("unchecked")
    public FileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      if (path.toString().endsWith(".tgz") || path.toString().endsWith(".tar.gz")) {
        inputStream = new TarArchiveInputStream(new GzipCompressorInputStream(new FileInputStream(path.toFile())));
      }
    }

    @Override
    public boolean hasNext() {
      if (nextRecordStatus == Status.ERROR) {
        return false;
      } else if (nextRecordStatus == Status.SKIPPED) {
        return true;
      }

      if (bufferedRecord != null) {
        return true;
      } else if (atEOF) {
        return false;
      }

      try {
        if (path.toString().endsWith(".tgz") || path.toString().endsWith(".tar.gz")) {
          getNextEntry();
          bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
          bufferedRecord = new Document(bufferedReader, Paths.get(nextEntry.getName()).getFileName().toString().replaceAll("\\.html$", ""));
        } else {
          bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(path.toFile()), StandardCharsets.UTF_8));
          bufferedRecord = new Document(bufferedReader, path.getFileName().toString().replaceAll("\\.html$", ""));
          atEOF = true;
        }
      } catch (IOException e1) {
        if (!path.toString().endsWith(".xml")) {
          nextRecordStatus = Status.ERROR;
        }
        return false;
      } catch (NoSuchElementException e2) {
        return false;
      } catch (RuntimeException e3) {
        nextRecordStatus = Status.SKIPPED;
        return true;
      }

      return bufferedRecord != null;
    }

    @Override
    public void readNext() {}

    private void getNextEntry() throws IOException {
      nextEntry = inputStream.getNextEntry();
      if (nextEntry == null) {
        throw new NoSuchElementException();
      }
      // an ArchiveEntry may be a directory, so we need to read a next one.
      //   this must be done after the null check.
      if (nextEntry.isDirectory()) {
        getNextEntry();
      }
    }
  }

  /**
   * A generic document in a collection of HTML documents.
   */
  public static class Document implements SourceDocument {
    private String id;
    private String contents;

    public Document(BufferedReader bRdr, String fileName) {
      StringBuilder sb = new StringBuilder();
      try {
        String line;
        while ((line = bRdr.readLine()) != null) {
          sb.append(line).append("\n");
        }
        this.contents = sb.toString();
        this.id = fileName;
      } catch (IOException e) {
        LOG.error("Error process file " + fileName);
        LOG.error(e);
      }
    }

    @Override
    public String id() {
      return id;
    }

    @Override
    public String content() {
      return contents;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
