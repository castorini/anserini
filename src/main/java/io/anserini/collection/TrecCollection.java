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

import io.anserini.document.SourceDocument;
import io.anserini.document.TrecDocument;
import org.apache.commons.compress.compressors.z.ZCompressorInputStream;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Class representing an instance of a TREC collection.
 */
public class TrecCollection<D extends TrecDocument> extends Collection {
  protected BufferedReader bufferedReader;
  protected final int BUFFER_SIZE = 1 << 16; // 64K

  public TrecCollection() throws IOException {
    super();
    skippedFilePrefix = new HashSet<>(Arrays.asList("readme"));
    skippedDirs = new HashSet<>(Arrays.asList("cr", "dtd", "dtds"));
  }

  @Override
  public void prepareInput(Path curInputFile) throws IOException {
    this.curInputFile = curInputFile;
    this.bufferedReader = null;
    String fileName = curInputFile.toString();
    if (fileName.matches(".*?\\.\\d*z$")) { // .z .0z .1z .2z
      FileInputStream fin = new FileInputStream(fileName);
      BufferedInputStream in = new BufferedInputStream(fin);
      ZCompressorInputStream zIn = new ZCompressorInputStream(in);
      bufferedReader = new BufferedReader(new InputStreamReader(zIn, StandardCharsets.UTF_8));
    } else if (fileName.endsWith(".gz")) { //.gz
      InputStream stream = new GZIPInputStream(
              Files.newInputStream(curInputFile, StandardOpenOption.READ), BUFFER_SIZE);
      bufferedReader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
    } else { // plain text file
      bufferedReader = new BufferedReader(new FileReader(fileName));
    }
  }

  @Override
  public void finishInput() throws IOException {
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
  public SourceDocument next() {
    TrecDocument doc = new TrecDocument();
    try {
      doc = (D) doc.readNextRecord(bufferedReader);
      if (doc == null) {
        atEOF = true;
        doc = null;
      }
    } catch (IOException e1) {
      doc = null;
    }
    return doc;
  }
}
