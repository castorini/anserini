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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A file containing one more source documents to be indexed. A collection is comprised of one or
 * more {@code AbstractFileSegment}s. Note that a {@code AbstractFileSegment} can have independent existence
 * outside a collection, and in principle multiple collections might share the same
 * {@code AbstractFileSegment} implementation.
 */
public abstract class AbstractFileSegment<T extends SourceDocument> implements Iterator<T>, Closeable {
  private static final Logger LOG = LogManager.getLogger(AbstractFileSegment.class);
  protected final int BUFFER_SIZE = 1 << 16; // 64K

  protected Path path;
  protected BufferedReader bufferedReader;
  protected boolean atEOF = false;
  protected T dType;
  protected T bufferedRecord = null;

  @Override
  public boolean hasNext() {
    return !atEOF;
  }

//  @Override
//  @SuppressWarnings("unchecked")
//  public T next() {
//    T d;
//    try {
//      d = (T) dType.readNextRecord(bufferedReader);
//      if (d == null) {
//        atEOF = true;
//      }
//    } catch (Exception e) {
//      LOG.warn("Exception when parsing document: ", e);
//      d = null;
//    }
//    return d;
//  }

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
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void close() throws IOException {
    atEOF = false;
    if (bufferedReader != null) {
      bufferedReader.close();
    }
  }
}