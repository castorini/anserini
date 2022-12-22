/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
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

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;

/**
 * <p>A classic TREC <i>ad hoc</i> document collection.
 * Unlike {@link TrecCollection}, this collection assumes that the document contents are already "clean" and thus
 * does <i>not</i> call {@link JsoupStringTransform} to remove tags.
 * </p>
 */
public class CleanTrecCollection extends DocumentCollection<CleanTrecCollection.Document> {
  public CleanTrecCollection(Path path) {
    this.path = path;
  }

  public CleanTrecCollection() {
  }

  @Override
  public FileSegment<Document> createFileSegment(Path p) throws IOException {
    return new Segment<>(p);
  }

  @Override
  public FileSegment<Document> createFileSegment(BufferedReader bufferedReader) throws IOException {
    return new Segment<>(bufferedReader);
  }

  public static class Segment<T extends Document> extends TrecCollection.Segment<T> {
    public Segment(Path path) throws IOException {
      super(path);
    }

    public Segment(BufferedReader bufferedReader) throws IOException {
      super(bufferedReader);
    }

    @Override
    protected TrecCollection.Document createNewDocument() {
      return new Document();
    }
  }

  public static class Document extends TrecCollection.Document {
    @Override
    public String contents() {
      return raw;
    }
  }
}
