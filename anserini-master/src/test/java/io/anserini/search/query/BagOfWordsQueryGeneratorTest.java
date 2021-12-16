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
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BagOfWordsQueryGeneratorTest {
  @Test
  public void test1() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query = queryGenerator.buildQuery("contents", analyzer, "sample query");

    assertEquals("(contents:queri)^1.0 (contents:sampl)^1.0", query.toString());
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery bq = (BooleanQuery) query;
    assertEquals(2, bq.clauses().size());
    assertEquals("(contents:queri)^1.0", (bq.clauses().get(0).getQuery().toString()));
    assertEquals("(contents:sampl)^1.0", (bq.clauses().get(1).getQuery().toString()));  }

  @Test
  public void test2() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query = queryGenerator.buildQuery("contents", analyzer, "Mary had a little lamb");

    assertEquals("(contents:lamb)^1.0 (contents:mari)^1.0 (contents:had)^1.0 (contents:littl)^1.0", query.toString());
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery bq = (BooleanQuery) query;
    assertEquals(4, bq.clauses().size());
    assertEquals("(contents:lamb)^1.0", (bq.clauses().get(0).getQuery().toString()));
    assertEquals("(contents:mari)^1.0", (bq.clauses().get(1).getQuery().toString()));
    assertEquals("(contents:had)^1.0", (bq.clauses().get(2).getQuery().toString()));
    assertEquals("(contents:littl)^1.0", (bq.clauses().get(3).getQuery().toString()));
  }

  @Test
  public void testMultipleFields() {
    Map<String, Float> fields = Map.of("field1", 3.14f, "field2", 2.178f);

    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query = queryGenerator.buildQuery(fields, IndexCollection.DEFAULT_ANALYZER, "Mary had a little lamb");
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery combinedQuery = (BooleanQuery) query;
    assertEquals(2, combinedQuery.clauses().size());
    assertTrue(combinedQuery.clauses().get(0).getQuery() instanceof BoostQuery);

    BoostQuery boostQuery = (BoostQuery) combinedQuery.clauses().get(0).getQuery();
    assertTrue(boostQuery.getBoost() > 1.0f);
    assertTrue(boostQuery.getQuery() instanceof BooleanQuery);

    BooleanQuery booleanQuery = (BooleanQuery) boostQuery.getQuery();
    assertEquals(4, booleanQuery.clauses().size());
  }
}
