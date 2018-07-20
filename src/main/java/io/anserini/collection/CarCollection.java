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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * Class representing an instance of a CAR paragraph collection. Note that it is in .cbor format
 * and we can read it through the tool: https://github.com/TREMA-UNH/trec-car-tools.
 * Since a collection is assumed to be in a directory, place the cbor file in
 * a directory prior to indexing.
 */
public class CarCollection extends DocumentCollection
    implements FileSegmentProvider<CarCollection.Document> {

  @Override
  public List<Path> getFileSegmentPaths() {
    Set<String> allowedFileSuffix = new HashSet<>(Arrays.asList(".cbor"));

    return discover(path, EMPTY_SET, EMPTY_SET, EMPTY_SET,
        allowedFileSuffix, EMPTY_SET);
  }

  @Override
  public FileSegment createFileSegment(Path p) throws IOException {
    return new FileSegment(p);
  }

  public class FileSegment extends AbstractFileSegment<Document> {
    private final FileInputStream stream;
    private final Iterator<Data.Paragraph> iter;

    protected FileSegment(Path path) throws IOException {
      this.path = path;
      stream = new FileInputStream(new File(path.toString()));
      iter = DeserializeData.iterableParagraphs(stream).iterator();
    }

    @Override
    public boolean hasNext() {
      if (bufferedRecord != null) {
        return true;
      }

      System.setProperty("file.encoding", "UTF-8");
      Data.Paragraph p;
      try {
         p = iter.next();
      } catch (NoSuchElementException e) {
        return false;
      }
      bufferedRecord = new Document(p.getParaId(), p.getTextOnly());

      return true;
    }
  }

  /**
   * A paragraph object in the CAR dataset ver2.0. The paraID serves as the id.
   * Reference: http://trec-car.cs.unh.edu/datareleases/
   */
  public class Document implements SourceDocument {
    private final String paraID;
    private final String paragraph;

    public Document(String paraID, String paragraph) {
      this.paraID = paraID;
      this.paragraph = paragraph;
    }

    /**
     * readNextRecord() is not used because CarCollection will load the .cbor file directly from the disk
     * @param bRdr file BufferedReader
     * @return null
     * @throws IOException any io exception
     */
    @Override
    public Document readNextRecord(BufferedReader bRdr) throws IOException {
      return null;
    }

    @Override
    public String id() {
      return paraID;
    }

    @Override
    public String content() {
      return paragraph;
    }

    @Override
    public boolean indexable() {
      return true;
    }
  }
}
