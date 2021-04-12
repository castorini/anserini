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

package io.anserini.search.query;

import io.anserini.index.IndexCollection;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.LuceneTestCase;
import org.junit.Test;

import java.util.Map;

public class DisjunctionMaxQueryGeneratorTest extends LuceneTestCase {
  @Test
  public void testSingleField() {
    float tiebreaker = 0.5f;
    QueryGenerator queryGenerator = new DisjunctionMaxQueryGenerator(tiebreaker);

    Query query = queryGenerator.buildQuery("contents", IndexCollection.DEFAULT_ANALYZER, "sample query");
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery booleanQuery = (BooleanQuery) query;
    assertEquals(2, booleanQuery.clauses().size());
  }

  @Test
  public void testMultipleFields() {
    float tiebreaker = 0.5f;
    QueryGenerator queryGenerator = new DisjunctionMaxQueryGenerator(tiebreaker);

    Map<String, Float> fields = Map.of("field1", 3.14f, "field2", 2.718f, "field3", 42.0f);
    Query query = queryGenerator.buildQuery(fields, IndexCollection.DEFAULT_ANALYZER, "sample query");
    assertTrue(query instanceof DisjunctionMaxQuery);

    DisjunctionMaxQuery combinedQuery = (DisjunctionMaxQuery) query;
    assertEquals(tiebreaker, combinedQuery.getTieBreakerMultiplier(), 1e-6);
    assertEquals(3, combinedQuery.getDisjuncts().size());
    assertTrue(combinedQuery.getDisjuncts().get(0) instanceof BoostQuery);

    BoostQuery boostQuery = (BoostQuery) combinedQuery.getDisjuncts().get(0);
    assertTrue(boostQuery.getBoost() > 1.0f);
    assertTrue(boostQuery.getQuery() instanceof BooleanQuery);

    BooleanQuery booleanQuery = (BooleanQuery) boostQuery.getQuery();
    assertEquals(2, booleanQuery.clauses().size());
  }
}
