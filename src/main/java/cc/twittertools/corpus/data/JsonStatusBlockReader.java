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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Preconditions;

/**
 * Abstraction for an stream of statuses, backed by an underlying gzipped file with JSON-encoded
 * tweets, one per line.
 */
public class JsonStatusBlockReader implements StatusStream {
  private final BufferedReader br;

  public JsonStatusBlockReader(File file) throws IOException {
    Preconditions.checkNotNull(file);

    if (!file.getName().endsWith(".gz")) {
      throw new IOException("Expecting .gz compressed file!");
    }

    br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file)), "UTF-8"));
  }

  /**
   * Returns the next status, or <code>null</code> if no more statuses.
   */
  public Status next() throws IOException {
    Status nxt = null;
    String raw = null;

    while (nxt == null) {
      raw = br.readLine();

      // Check to see if we've reached end of file.
      if (raw == null) {
        return null;
      }

      nxt = Status.fromJson(raw);
    }
    return Status.fromJson(raw);
  }

  public void close() throws IOException {
    br.close();
  }
}
