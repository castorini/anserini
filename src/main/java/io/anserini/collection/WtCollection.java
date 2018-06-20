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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class representing an instance of a Wt collection.
 */
public class WtCollection extends TrecwebCollection {

  public class FileSegment extends TrecwebCollection.FileSegment {
    public FileSegment(Path path) throws IOException {
      super(path);
    }
  }

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> skippedDirs = new HashSet<>(Arrays.asList("info"));

    return discover(path, EMPTY_SET, EMPTY_SET,
        EMPTY_SET, EMPTY_SET, skippedDirs);
  }
}
