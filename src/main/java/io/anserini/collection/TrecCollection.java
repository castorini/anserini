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

import io.anserini.document.TrecDocument;
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
public class TrecCollection<D extends TrecDocument> extends Collection {

  public class FileSegment extends Collection.FileSegment {
    protected BufferedReader bufferedReader;
    protected final int BUFFER_SIZE = 1 << 16; // 64K

    protected FileSegment() {}

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      this.bufferedReader = null;
      String fileName = path.toString();
      if (fileName.matches(".*?\\.\\d*z$")) { // .z .0z .1z .2z
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
    public void close() throws IOException {
      atEOF = false;
      if (bufferedReader != null) {
        bufferedReader.close();
      }
    }

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public D next() {
      TrecDocument doc = new TrecDocument();
      try {
        doc = (TrecDocument) doc.readNextRecord(bufferedReader);
        if (doc == null) {
          atEOF = true;
          doc = null;
        }
      } catch (IOException e1) {
        doc = null;
      }
      return (D) doc;
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> skippedFilePrefix = new HashSet<>(Arrays.asList("readme"));
    Set<String> skippedDirs = new HashSet<>(Arrays.asList("cr", "dtd", "dtds"));

    return discover(path, skippedFilePrefix, EMPTY_SET,
        EMPTY_SET, EMPTY_SET, skippedDirs);
  }

  @Override
  public Collection.FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }
}
