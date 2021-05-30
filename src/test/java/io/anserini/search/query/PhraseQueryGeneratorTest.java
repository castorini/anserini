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

package io.anserini.search.query;

import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PhraseQueryGeneratorTest {
  @Test
  public void test1() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    QueryGenerator queryGenerator = new PhraseQueryGenerator();
    Query query = queryGenerator.buildQuery("contents", analyzer, "sample query");

    assertTrue(query instanceof PhraseQuery);
    System.out.println(query.toString());
    assertEquals("contents:\"sampl queri\"", query.toString());
  }
}
