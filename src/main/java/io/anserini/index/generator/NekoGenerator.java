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

package io.anserini.index.generator;

import io.anserini.index.IndexCollection;
import io.anserini.index.transform.NekoStringTransform;

public class NekoGenerator extends LuceneDocumentGenerator {
  public NekoGenerator() {
    super(new NekoStringTransform());
  }
  public NekoGenerator(IndexCollection.Args args, IndexCollection.Counters counters) {
    super(new NekoStringTransform(), args, counters);
  }
}
