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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.NoSuchElementException;

/**
 * Base implementation for a {@link Segment} backed by a file.
 * A collection is comprised of one or more file segments. Note that implementations may have independent
 * existence outside a collection, and in principle multiple collections might share the same
 * {@code BaseFileSegment} implementation.
 */
public abstract class BaseFileSegment<T extends SourceDocument> implements Segment<T> {
  protected final int BUFFER_SIZE = 1 << 16; // 64K

  protected Path path;
  protected BufferedReader bufferedReader;
  protected boolean atEOF = false;
  protected T bufferedRecord = null;
  protected boolean skipped = false;
  protected boolean error = false;

  @Override
  public T next() {
    if (bufferedRecord == null && !hasNext()) {
      throw new NoSuchElementException("EOF has been reached. No more documents to read.");
    }
    T ret = bufferedRecord;
    bufferedRecord = null;
    return ret;
  }

  @Override
  public boolean hasNext() {
    error = false;
    skipped = false;

    if (bufferedRecord != null) {
      return true;
    } else if (atEOF) {
      return false;
    }

    try {
      readNext();
    } catch (IOException e1) {
      error = true;
      return false;
    } catch (NoSuchElementException e2) {
      return false;
    } catch (RuntimeException e3) {
      skipped = true;
      return true;
    }

    return bufferedRecord != null;
  }

  public abstract void readNext() throws IOException;

  public boolean isError() {
    return error;
  }

  public boolean isSkipped() {
    return skipped;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  public void close() throws IOException {
    atEOF = true;
    bufferedRecord = null;
    skipped = false;
    error = false;
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }
}