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
public abstract class Collection<D extends SourceDocument> implements Iterator<SourceDocument> {
  protected Path inputDir;
  protected Path curInputFile;
  protected boolean at_eof = false;

  protected Set<String> skippedFilePrefix = new HashSet<>();
  protected Set<String> allowedFilePrefix = new HashSet<>();
  protected Set<String> skippedFileSuffix = new HashSet<>();
  protected Set<String> allowedFileSuffix = new HashSet<>();
  protected Set<String> skippedDirs = new HashSet<>();

  public Deque<Path> discoverFiles() {
    return DiscoverFiles.discover(inputDir, skippedFilePrefix, allowedFilePrefix,
            skippedFileSuffix, allowedFileSuffix, skippedDirs);
  }

  public Collection() throws IOException {}

  public void setInputDir(Path inputDir) {
    this.inputDir = inputDir;
  }

  public final Path getInputDir() {
    return inputDir;
  }

  @Override
  public boolean hasNext() {
    return !at_eof;
  }

  @Override
  public void remove() {}

  public abstract void prepareInput(Path p) throws IOException;
  public abstract void finishInput() throws IOException;
}
