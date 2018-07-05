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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * <p>A static collection of documents, comprised of one or more {@code FileSegment}s.
 * Each {@code FileSegment} contains one or more {@code SourceDocument}s.
 * A collection is assumed to be a directory. In the case where the collection is
 * a single file (e.g., a Wikipedia dump), place the file into an arbitrary directory.</p>
 *
 * <p>The collection class has two responsibilities:</p>
 *
 * <ol>
 * <li>Discover the files with qualified names in the input directory.</li>
 * <li>Extract documents from each file.</li>
 * </ol>
 *
 * <p>The detailed steps of adding a new collection class are:</p>
 *
 * <ol>
 *
 * <li>Create a subclass for {@link Collection}.</li>
 *
 * <li>Implement class {@link FileSegment} and function {@link Collection#getFileSegmentPaths},
 * {@link Collection#createFileSegment}. Take {@link TrecCollection} as an example.</li>
 *
 * <li>Create a subclass for {@link SourceDocument} and implement function {@link SourceDocument#readNextRecord},
 * which returns a single {@code SourceDocument}. Take {@link io.anserini.document.TrecDocument} as an example.</li>
 *
 * <li>[Optional] Create a new {@link io.anserini.index.generator}. Function
 * {@link io.anserini.index.generator.LuceneDocumentGenerator#createDocument} takes a {@code SourceDocument}
 * as the input and return a native Lucene {@link org.apache.lucene.document.Document}.</li>
 *
 * <li>Add unit test at {@code src/test/java/io/anserini/document}.</li>
 *
 * </ol>
 *
 * @param <T> type of the source document
 */
public abstract class Collection<T extends SourceDocument> {
  private static final Logger LOG = LogManager.getLogger(Collection.class);

  /**
   * A file containing one more source documents to be indexed. A collection is comprised of one or
   * more {@code FileSegment}s.
   */
  public abstract class FileSegment implements Iterator<T>, Closeable {
    protected Path path;
    protected BufferedReader bufferedReader;
    protected boolean atEOF = false;
    protected final int BUFFER_SIZE = 1 << 16; // 64K
    protected T dType;

    @Override
    public boolean hasNext() {
      return !atEOF;
    }

    @Override
    public T next() {
      T d;
      try {
        d = (T)dType.readNextRecord(bufferedReader);
        if (d == null) {
          atEOF = true;
        }
      } catch (Exception e) {
        LOG.warn("Exception when parsing document:" + e.getMessage());
        d = null;
      }
      return d;
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

  protected Path path;
  static protected final Set<String> EMPTY_SET = new HashSet<>();

  /**
   * Sets the path of the collection.
   *
   * @param path path of the collection
   */
  public final void setCollectionPath(Path path) {
    this.path = path;
  }

  /**
   * Returns the path of the collection.
   *
   * @return path of the collection
   */
  public final Path getCollectionPath() {
    return path;
  }

  /**
   * Returns a list of paths corresponding to file segments in the collection. Note that this
   * method returns paths, as opposed to {@code FileSegment} objects directly, because each
   * {@code FileSegment} object is backed by an open file, and thus having too many file handles
   * open may be problematic for large collections. Use {@link #createFileSegment(Path)} to
   * instantiate a {@code FileSegment} object from its path.
   *
   * @return a list of paths corresponding to file segments in the collection
   */
  public abstract List<Path> getFileSegmentPaths();

  /**
   * Creates a {@code FileSegment} from a path.
   *
   * @param p path
   * @return {@code FileSegment} with the specified path
   * @throws IOException if file access error encountered
   */
  public abstract FileSegment createFileSegment(Path p) throws IOException;

  /**
   * Used internally by implementations to walk a path and collect file segments.
   *
   * @param p path to walk
   * @param skippedFilePrefix set of file prefixes to skip
   * @param allowedFilePrefix set of file prefixes to allow
   * @param skippedFileSuffix set of file suffixes to skip
   * @param allowedFileSuffix set of file suffixes to allow
   * @param skippedDir set of directories to skip
   * @return result of walking the specified path according to the specified constraints
   */
  protected List<Path> discover(Path p, Set<String> skippedFilePrefix, Set<String> allowedFilePrefix,
      Set<String> skippedFileSuffix, Set<String> allowedFileSuffix, Set<String> skippedDir) {
    final List<Path> paths = new ArrayList<>();

    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path name = file.getFileName();
        boolean shouldAdd = true;
        if (name != null) {
          String fileName = name.toString();
          for (String s : skippedFileSuffix) {
            if (fileName.endsWith(s)) {
              shouldAdd = false;
              break;
            }
          }
          if (shouldAdd && !allowedFileSuffix.isEmpty()) {
            shouldAdd = false;
            for (String s : allowedFileSuffix) {
              if (fileName.endsWith(s)) {
                shouldAdd = true;
                break;
              }
            }
          }
          if (shouldAdd) {
            for (String s : skippedFilePrefix) {
              if (fileName.startsWith(s)) {
                shouldAdd = false;
                break;
              }
            }
          }
          if (shouldAdd && !allowedFilePrefix.isEmpty()) {
            shouldAdd = false;
            for (String s : allowedFilePrefix) {
              if (fileName.startsWith(s)) {
                shouldAdd = true;
                break;
              }
            }
          }
        }
        if (shouldAdd) {
          paths.add(file);
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        if (skippedDir.contains(dir.getFileName().toString())) {
          LOG.info("Skipping: " + dir);
          return FileVisitResult.SKIP_SUBTREE;
        }
        return FileVisitResult.CONTINUE;
      }

      @Override
      public FileVisitResult visitFileFailed(Path file, IOException ioe) {
        LOG.error("Visiting failed for " + file.toString(), ioe);
        return FileVisitResult.SKIP_SUBTREE;
      }
    };

    try {
      Files.walkFileTree(p, fv);
    } catch (IOException e) {
      LOG.error("IOException during file visiting", e);
    }

    return paths;
  }
}
