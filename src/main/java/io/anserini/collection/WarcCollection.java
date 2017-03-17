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

import io.anserini.document.WarcRecord;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;

/**
 * Abstract class representing an instance of a WARC collection.
 */
public abstract class WarcCollection<D extends WarcRecord> extends Collection {
  public abstract class File extends CollectionFile {
    protected DataInputStream inStream;

    public File(Path curInputFile) throws IOException {
      this.curInputFile = curInputFile;
      this.inStream = new DataInputStream(
          new GZIPInputStream(Files.newInputStream(curInputFile, StandardOpenOption.READ)));
    }

    @Override
    public void close() throws IOException {
      atEOF = false;
      if (inStream != null)
        inStream.close();
    }

  }

  public WarcCollection() throws IOException {
    allowedFileSuffix = new HashSet<>(Arrays.asList(".warc.gz"));
    skippedDirs = new HashSet<>(Arrays.asList("OtherData"));
  }
}
