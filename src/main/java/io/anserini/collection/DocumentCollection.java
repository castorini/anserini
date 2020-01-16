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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * <p>A static collection of documents, comprised of one or more {@link FileSegment}s.
 * Each {@link FileSegment} is a container for {@link SourceDocument}s.
 * A collection is assumed to be a directory. In the case where the collection is
 * a single file (e.g., a Wikipedia dump), place the file into an arbitrary directory.</p>
 *
 * <p>The collection is responsible for discovering files with qualified names in the input
 * directory. The file segment implementation is responsible for reading each file to generate
 * {@link SourceDocument}s for indexing. </p>
 *
 * <p>The steps of adding a new collection class are:</p>
 *
 * <ol>
 *
 * <li>Create a subclass for {@link DocumentCollection}.</li>
 *
 * <li>Implement class {@link FileSegment}, by convention as an inner class of the
 * {@code DocumentCollection}. See {@link TrecCollection.Segment} as an example.</li>
 *
 * <li>Create a subclass for {@link SourceDocument} implementing the corresponding document type.
 * See {@link TrecCollection.Document} as an example.</li>
 *
 * <li>Optionally create a new {@link io.anserini.index.generator.LuceneDocumentGenerator}.
 * The {@link io.anserini.index.generator.LuceneDocumentGenerator#createDocument}
 * method takes {@code SourceDocument} as the input and return a Lucene
 * {@link org.apache.lucene.document.Document} for indexing.</li>
 *
 * <li>Remember to add unit tests at {@code src/test/java/io/anserini/collection}!</li>
 *
 * </ol>
 */
public abstract class DocumentCollection<T extends SourceDocument> implements Iterable<FileSegment<T>> {
  private static final Logger LOG = LogManager.getLogger(DocumentCollection.class);

  protected Path path;
  protected Set<String> skippedFilePrefix = new HashSet<>();
  protected Set<String> allowedFilePrefix = new HashSet<>();
  protected Set<String> skippedFileSuffix = new HashSet<>();
  protected Set<String> allowedFileSuffix = new HashSet<>();
  protected Set<String> skippedDir = new HashSet<>();

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
   * Creates a {@code FileSegment} from a path.
   *
   * @param p path
   * @return {@code FileSegment} with the specified path
   * @throws IOException if file access error encountered
   */
  public abstract FileSegment<T> createFileSegment(Path p) throws IOException;

  /**
   * An iterator over {@code FileSegment} for the {@code DocumentCollection} iterable.
   * A collection is comprised of one or more file segments.
   */
  @Override
  public final Iterator<FileSegment<T>> iterator(){
    List<Path> paths = discover(this.path);
    Iterator<Path> pathsIterator = paths.iterator();

    return new Iterator<FileSegment<T>>(){
      Path segmentPath;
      FileSegment<T> segment;

      @Override
      public boolean hasNext(){
        if (segment != null){
          return true;
        }
        if (!pathsIterator.hasNext()){
          return false;
        } else {
          try {
            segmentPath = pathsIterator.next();
            segment = createFileSegment(segmentPath);
          } catch (IOException e){
            return false;
          }
        }
        return true;
      }

      @Override
      public FileSegment<T> next() throws NoSuchElementException {
        if (!hasNext()){
          throw new NoSuchElementException("No more file segments to read.");
        } else {
          FileSegment<T> seg = segment;
          segment = null;
          return seg;
        }
      }

      @Override
      public void remove(){
        throw new UnsupportedOperationException();
      }
    };
  }

  /**
   * Used internally by implementations to walk a path and collect file segments.
   *
   * @param p path to walk
   * @return result of walking the specified path according to the collection-specific constraints
   */
  public final List<Path> discover(Path p) {
    final List<Path> paths = new ArrayList<>();

    FileVisitor<Path> fv = new SimpleFileVisitor<Path>() {
      @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Path name = file.getFileName();
        boolean shouldAdd = true;
        if (Files.isSymbolicLink(file)) {
          name = Files.readSymbolicLink(file);
          if (Files.isDirectory(name)) {
            paths.addAll(discover(name));
            shouldAdd = false;
          }
        }
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
