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

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

// Since the CACM collection is checked into our repo, we can directly test it.
public class HtmlCollectionTest {

  @Test
  public void testCACM() throws IOException {
    HtmlCollection cacm = new HtmlCollection();
    FileSegment<HtmlCollection.Document> segment =
        cacm.createFileSegment(Paths.get("src/main/resources/cacm/cacm.tar.gz"));
    Iterator<HtmlCollection.Document> iter = segment.iterator();

    AtomicInteger cnt = new AtomicInteger();
    iter.forEachRemaining(d -> {
      cnt.getAndIncrement();
    });
    assertEquals(3204, cnt.get());
  }
}
