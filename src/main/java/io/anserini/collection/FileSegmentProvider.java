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

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * An interface that provides {@link FileSegment}s, which provides {@link SourceDocument}s for
 * a particular {@link DocumentCollection}.
 */
public interface FileSegmentProvider<T extends SourceDocument> {
  /**
   * Returns a list of paths corresponding to file segments in the collection. Note that this
   * method returns paths, as opposed to {@code FileSegment} objects directly, because each
   * {@code FileSegment} object is backed by an open file, and thus having too many file handles
   * open may be problematic for large collections. Use {@link #createFileSegment(Path)} to
   * instantiate a {@code FileSegment} object from its path.
   *
   * @return a list of paths corresponding to file segments in the collection
   */
  List<Path> getFileSegmentPaths();

  /**
   * Creates a {@code FileSegment} from a path.
   *
   * @param p path
   * @return {@code FileSegment} with the specified path
   * @throws IOException if file access error encountered
   */
  FileSegment<T> createFileSegment(Path p) throws IOException;
}
