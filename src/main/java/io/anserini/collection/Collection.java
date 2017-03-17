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

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * An information retrieval collection, comprising a finite number of {@link SourceDocument}s.
 *
 * @param <D> type of the source document
 */
public abstract class Collection<D extends SourceDocument> {

  public abstract class CollectionFile implements Iterator<D>, Closeable {
    protected Path curInputFile;
    protected boolean atEOF = false;

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }

  protected Path path;

  protected Set<String> skippedFilePrefix = new HashSet<>();
  protected Set<String> allowedFilePrefix = new HashSet<>();
  protected Set<String> skippedFileSuffix = new HashSet<>();
  protected Set<String> allowedFileSuffix = new HashSet<>();
  protected Set<String> skippedDirs = new HashSet<>();

  public Deque<Path> discoverFiles() {
    return DiscoverFiles.discover(path, skippedFilePrefix, allowedFilePrefix,
            skippedFileSuffix, allowedFileSuffix, skippedDirs);
  }

  public final void setPath(Path inputDir) {
    this.path = inputDir;
  }

  public final Path getPath() {
    return path;
  }

  public abstract CollectionFile createCollectionFile(Path p) throws IOException;
}
