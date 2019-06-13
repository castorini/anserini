/**
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;
import java.util.Iterator;

/**
 * Base implementation for a {@link FileSegment}.
 * A collection is comprised of one or more file segments. Note that implementations may have independent
 * existence outside a collection, and in principle multiple collections might share the same
 * {@code FileSegment} implementation.
 */
public abstract class FileSegment<T extends SourceDocument> implements Iterable<T> {

  public enum Status {
    SKIPPED, ERROR, VOID
  }

  protected Path path;
  protected final int BUFFER_SIZE = 1 << 16; // 64K
  protected BufferedReader bufferedReader;
  protected boolean atEOF = false;
  protected T bufferedRecord = null;
  protected Status nextRecordStatus = Status.VOID;

  /**
   * Move exception handling for skipped docs to within segment
   * Desired behaviour is to continue iteration and increment counter
   * Call getSkippedCount() at the end of segment iteration to return count of total docs skipped
   */
  protected int skipped = 0;

  public FileSegment(Path segmentPath) {
    this.path = segmentPath;
  }

  public final int getSkippedCount() {
    return skipped;
  }

  public final Path getSegmentPath() {
    return path;
  }

  public final Status getNextRecordStatus() {
    return nextRecordStatus;
  }

  public void close() throws IOException {
    atEOF = true;
    bufferedRecord = null;
    nextRecordStatus = Status.VOID;
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }

  /**
   * For concrete classes to implement depending on desired iterator behaviour
   *
   * @throws IOException if reader error encountered
   */

  protected abstract void readNext() throws IOException;

  /**
   * An iterator over {@code SourceDocument} for the {@code FileSegment} iterable.
   * A file segment is comprised of one or more source documents.
   */
  @Override
  public final Iterator<T> iterator(){

    return new Iterator<T>(){

      @Override
      public T next() throws NoSuchElementException {
        if (nextRecordStatus == Status.ERROR || bufferedRecord == null && !hasNext()) {
          nextRecordStatus = Status.VOID;
          throw new NoSuchElementException("EOF has been reached. No more documents to read.");
        }
        T ret = bufferedRecord;
        bufferedRecord = null;
        return ret;
      }

      @Override
      public boolean hasNext() {
        if (nextRecordStatus == Status.ERROR) {
          return false;
        }

        if (bufferedRecord != null) {
          return true;
        } else if (atEOF) {
          return false;
        }

        try {
          readNext();
        } catch (IOException | NoSuchElementException e1) {
          // Exceptions where expected behaviour is to stop iteration
          // For IOException, nextRecordStatus = Status.ERROR should be handled in readNext() depending on collection
          return false;
        } catch (RuntimeException e2) {
          // Exceptions where expected behaviour is to skip and continue iteration
          // Call getSkippedCount() at the end of segment iteration to return count of total docs skipped
          nextRecordStatus = Status.VOID;
          skipped += 1;
          return hasNext();
        }

        return bufferedRecord != null;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }
}

