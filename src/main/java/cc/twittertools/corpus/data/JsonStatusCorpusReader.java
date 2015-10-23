/**
 * Twitter Tools
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

package cc.twittertools.corpus.data;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import com.google.common.base.Preconditions;

/**
 * Abstraction for a corpus of statuses. A corpus is assumed to consist of a number of blocks, each
 * represented by a gzipped file within a root directory. This object will allow to caller to read
 * through all blocks, in sorted lexicographic order of the files.
 */
public class JsonStatusCorpusReader implements StatusStream {
  private final File[] files;
  private int nextFile = 0;
  private JsonStatusBlockReader currentBlock = null;

  public JsonStatusCorpusReader(File file) throws IOException {
    Preconditions.checkNotNull(file);

    if (!file.isDirectory()) {
      throw new IOException("Expecting " + file + " to be a directory!");
    }

    files = file.listFiles(new FileFilter() {
      public boolean accept(File path) {
        return path.getName().endsWith(".gz") ? true : false;
      }
    });

    if (files.length == 0) {
      throw new IOException(file + " does not contain any .gz files!");
    }
  }

  /**
   * Returns the next status, or <code>null</code> if no more statuses.
   */
  public Status next() throws IOException {
    if (currentBlock == null) {
      currentBlock = new JsonStatusBlockReader(files[nextFile]);
      nextFile++;
    }

    Status status = null;
    while (true) {
      status = currentBlock.next();
      if (status != null) {
        return status;
      }

      if (nextFile >= files.length) {
        // We're out of files to read. Must be the end of the corpus.
        return null;
      }

      currentBlock.close();
      // Move to next file.
      currentBlock = new JsonStatusBlockReader(files[nextFile]);
      nextFile++;
    }
  }

  public void close() throws IOException {
    currentBlock.close();
  }
}
