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

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;
import io.anserini.document.CarDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Class representing an instance of a CAR paragraph collection. Note that it is in .cbor format
 * and we can read it through the tool: https://github.com/TREMA-UNH/trec-car-tools.
 * Since a collection is assumed to be in a directory, place the cbor file in
 * a directory prior to indexing.
 */
public class CarCollection extends Collection<CarDocument> {

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".cbor"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
        allowedFileSuffix, EMPTY_SET);
  }

  public class FileSegment extends Collection<CarDocument>.FileSegment {
      private final FileInputStream stream;
      private final Iterator<Data.Paragraph> iter;

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      stream = new FileInputStream(new File(path.toString()));
      iter = DeserializeData.iterableParagraphs(stream).iterator();
    }

    @Override
    public CarDocument next() {
      System.setProperty("file.encoding", "UTF-8");
      Data.Paragraph p = iter.next();
      CarDocument doc = new CarDocument(p.getParaId(), p.getTextOnly());

      // If we've fall through here, we've either encountered an exception or we've reached the end
      // of the underlying stream.
      if (!iter.hasNext()) {
        atEOF = true;
      }
      return doc;
    }
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

}
