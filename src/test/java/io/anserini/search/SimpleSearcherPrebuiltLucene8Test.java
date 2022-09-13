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

package io.anserini.search;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SimpleSearcherPrebuiltLucene8Test {

  @Test
  public void testSearch1() throws Exception {
    SimpleSearcher searcher =
        new SimpleSearcher("src/test/resources/prebuilt_indexes/lucene8-index.sample_docs_trec_collection2");
    assertEquals(3, searcher.get_total_num_docs());

    SimpleSearcher.Result[] hits;

    hits = searcher.search("text", 10);
    assertEquals(3, hits.length);
    assertEquals("DOC222", hits[0].docid);
    assertEquals(0.1015f, hits[0].score, 10e-4);
    assertEquals("TREC_DOC_1", hits[1].docid);
    assertEquals(0.0738f, hits[1].score, 10e-4);
    assertEquals("WSJ_1", hits[2].docid);
    assertEquals(0.0687f, hits[2].score, 10e-4);

    hits = searcher.search("simple", 10);
    assertEquals(2, hits.length);
    assertEquals("TREC_DOC_1", hits[0].docid);
    assertEquals(0.2597f, hits[0].score, 10e-4);
    assertEquals("DOC222", hits[1].docid);
    assertEquals(0.2416f, hits[1].score, 10e-4);

    searcher.close();
  }

}
