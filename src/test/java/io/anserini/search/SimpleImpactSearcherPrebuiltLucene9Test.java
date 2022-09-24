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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimpleImpactSearcherPrebuiltLucene9Test {

  @Test
  public void testSearch1() throws Exception {
    SimpleImpactSearcher searcher =
        new SimpleImpactSearcher("src/test/resources/prebuilt_indexes/lucene9-index.sample_docs_json_collection_tokenized");
    assertEquals(2, searcher.get_total_num_docs());

    SimpleImpactSearcher.Result[] hits;

    Map<String, Float> query = new HashMap<>();
    query.put("##ing", 1.0f);

    hits = searcher.search(query, 10);
    assertEquals(1, hits.length);
    assertEquals("2000001", hits[0].docid);
    assertEquals(2, (int) hits[0].score);

    query = new HashMap<>();
    query.put("test", 1.0f);
    hits = searcher.search(query, 10);
    assertEquals(1, hits.length);
    assertEquals("2000000", hits[0].docid);
    assertEquals(1, (int) hits[0].score);

    searcher.close();
  }

}
