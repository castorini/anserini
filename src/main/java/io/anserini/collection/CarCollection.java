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

import edu.unh.cs.treccar_v2.Data;
import edu.unh.cs.treccar_v2.read_data.DeserializeData;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * A document collection for the TREC Complex Answer Retrieval (CAR) Track.
 * This class provides a wrapper around <a href="https://github.com/TREMA-UNH/trec-car-tools">tools</a>
 * provided by the track for reading the <code>cbor</code> format.
 * Since a collection is assumed to be in a directory, place the <code>cbor</code> file in
 * a directory prior to indexing.
 */
public class CarCollection extends DocumentCollection<CarCollection.Document> {

  public CarCollection(){
    this.allowedFileSuffix = new HashSet<>(Arrays.asList(".cbor"));
  }

  @Override
  public FileSegment<CarCollection.Document> createFileSegment(Path p) throws IOException {
    return new Segment(p);
  }

  /**
   * An individual file in {@code CarCollection}.
   */
  public static class Segment extends FileSegment<CarCollection.Document> {
    private final FileInputStream stream;
    private final Iterator<Data.Paragraph> iter;

    protected Segment(Path path) throws IOException {
      super(path);
      stream = new FileInputStream(new File(path.toString()));
      iter = DeserializeData.iterableParagraphs(stream).iterator();
    }

    @Override
    public void readNext() {
      System.setProperty("file.encoding", "UTF-8");
      Data.Paragraph p;
      p = iter.next();
      bufferedRecord = new Document(p.getParaId(), p.getTextOnly());
      if (!iter.hasNext()) {
        atEOF = true;
      }
    }
  }

  /**
   * A document from a collection for the TREC Complex Answer Retrieval (CAR) Track.
   * The paraID serves as the id.
   * See <a href="http://trec-car.cs.unh.edu/datareleases/">this reference</a> for details.
   */
  public static class Document implements SourceDocument {
    private final String paraID;
    private final String paragraph;

    public Document(String paraID, String paragraph) {
      this.paraID = paraID;
      this.paragraph = paragraph;
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
