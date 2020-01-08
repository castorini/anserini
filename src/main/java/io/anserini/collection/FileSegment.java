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

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * Base implementation for a {@link FileSegment}.
 * A collection is comprised of one or more {@link FileSegment}s, each of which is a container for
 * {@link SourceDocument}s.
 * </p>
 *
 * <p>
 * Note that implementations may have independent existence outside a collection, and in principle
 * multiple collections might share the same {@code FileSegment} implementation.
 * </p>
 */
public abstract class FileSegment<T extends SourceDocument> implements Iterable<T>, Closeable {

  protected Path path;
  protected final int BUFFER_SIZE = 1 << 16; // 64K
  protected BufferedReader bufferedReader;
  protected boolean atEOF = false;
  protected T bufferedRecord = null;

  /*
   * Exception handling for skipped documents is contained within the iterator. If error is
   * encountered, counter is incremented but iteration continues. Call getSkippedCount() at the
   * end of segment iteration to return count of total documents skipped.
   */
  protected int skipped = 0;

  /*
   * Exception handling for file read errors is contained within the iterator. If error is
   * encountered, iteration is terminated and this error flag is set. Call getErrorStatus() at the
   * end of segment iteration to return error status.
   */
  protected boolean error = false;

  public FileSegment(Path segmentPath) {
    this.path = segmentPath;
  }

  public final int getSkippedCount() {
    return skipped;
  }

  public final boolean getErrorStatus() {
    return error;
  }

  public final Path getSegmentPath() {
    return path;
  }

  @Override
  public void close() throws IOException {
    atEOF = true;
    bufferedRecord = null;
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }

  /**
   * Advances to the next record in this file segment. The {@code Iterator} implementation in the
   * {@code FileSegment} conveniently wraps this method so that concrete classes only need to
   * override this method.
   *
   * @throws IOException if reader error encountered and iterator should stop
   * @throws ParseException if parse error encountered and iterator should continue
   * @throws NoSuchElementException if EOF encountered and iterator should stop
   */

  protected abstract void readNext() throws IOException, ParseException, NoSuchElementException;

  /**
   * An iterator that provides the {@code SourceDocument}s for this {@code FileSegment}.
   * The iterator {@code hasNext()} calls {@link FileSegment#readNext()} to advance to the next
   * record, which is overridden by concrete implementations of the {@code FileSegment}.
   */
  @Override
  public final Iterator<T> iterator(){
    return new Iterator<T>(){
      @Override
      public T next() throws NoSuchElementException {
        if (error) {
          throw new NoSuchElementException("Encountered file read error, stopping iteration.");
        }
        if (bufferedRecord == null && !hasNext()) {
          throw new NoSuchElementException("EOF has been reached. No more documents to read.");
        }
        T ret = bufferedRecord;
        bufferedRecord = null;
        return ret;
      }

      @Override
      public boolean hasNext() {
        if (bufferedRecord != null) {
          return true;
        } else if (atEOF) {
          return false;
        }

        try {
          readNext();
        } catch (IOException e1) {
          // Error, stop iteration
          // Call getErrorStatus() at the end of segment iteration and update error counter
          error = true;
          return false;
        } catch (NoSuchElementException e2){
          // EOF, stop iteration
          return false;
        } catch (ParseException e3) {
          // Skip and continue iteration
          // Call getSkippedCount() at the end of segment iteration and update skipped counter
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

