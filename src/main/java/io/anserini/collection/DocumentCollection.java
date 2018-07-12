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

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
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
 * <li>Create a subclass for {@link DocumentCollection}.</li>
 *
 * <li>Implement class {@link FileSegment} and function {@link FileSegmentProvider#getFileSegmentPaths},
 * {@link FileSegmentProvider#createFileSegment}. Take {@link TrecCollection} as an example.</li>
 *
 * <li>Create a subclass for {@link SourceDocument} and implement function {@link SourceDocument#readNextRecord},
 * which returns a single {@code SourceDocument}. Take {@link io.anserini.collection.TrecCollection.Document} as an example.</li>
 *
 * <li>[Optional] Create a new {@link io.anserini.index.generator}. Function
 * {@link io.anserini.index.generator.LuceneDocumentGenerator#createDocument} takes a {@code SourceDocument}
 * as the input and return a native Lucene {@link org.apache.lucene.document.Document}.</li>
 *
 * <li>Add unit test at {@code src/test/java/io/anserini/document}.</li>
 *
 * </ol>
 */
public abstract class DocumentCollection {
  private static final Logger LOG = LogManager.getLogger(DocumentCollection.class);
  protected static final Set<String> EMPTY_SET = new HashSet<>();
  protected Path path;

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
