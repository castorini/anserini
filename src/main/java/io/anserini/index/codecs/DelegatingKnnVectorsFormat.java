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

package io.anserini.index.codecs;

import java.io.IOException;
import org.apache.lucene.codecs.KnnVectorsFormat;
import org.apache.lucene.codecs.KnnVectorsReader;
import org.apache.lucene.codecs.KnnVectorsWriter;
import org.apache.lucene.index.SegmentReadState;
import org.apache.lucene.index.SegmentWriteState;

// We need this class exists because Lucene99HnswVectorsFormat is final, and so we can't override getMaxDimensions.
// Solution provided by Solr, see https://www.mail-archive.com/java-user@lucene.apache.org/msg52149.html
public final class DelegatingKnnVectorsFormat extends KnnVectorsFormat {
  private final KnnVectorsFormat delegate;
  private final int maxDimensions;

  public DelegatingKnnVectorsFormat(KnnVectorsFormat delegate, int maxDimensions) {
    super(delegate.getName());
    this.delegate = delegate;
    this.maxDimensions = maxDimensions;
  }

  @Override
  public KnnVectorsWriter fieldsWriter(SegmentWriteState state) throws IOException {
    return delegate.fieldsWriter(state);
  }

  @Override
  public KnnVectorsReader fieldsReader(SegmentReadState state) throws IOException {
    return delegate.fieldsReader(state);
  }

  @Override
  public int getMaxDimensions(String fieldName) {
    return maxDimensions;
  }
}