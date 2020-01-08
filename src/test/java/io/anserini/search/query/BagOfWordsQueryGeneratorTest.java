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

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BagOfWordsQueryGeneratorTest {
  @Test
  public void test1() {
    Analyzer analyzer = new EnglishAnalyzer();
    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query = queryGenerator.buildQuery("contents", analyzer, "sample query");

    assertEquals("contents:sampl contents:queri", query.toString());
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery bq = (BooleanQuery) query;
    assertEquals(2, bq.clauses().size());
    assertEquals("sampl", ((TermQuery) bq.clauses().get(0).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(0).getQuery()).getTerm().field());
    assertEquals("queri", ((TermQuery) bq.clauses().get(1).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(1).getQuery()).getTerm().field());
  }

  @Test
  public void test2() {
    Analyzer analyzer = new EnglishAnalyzer();
    QueryGenerator queryGenerator = new BagOfWordsQueryGenerator();
    Query query = queryGenerator.buildQuery("contents", analyzer, "Mary had a little lamb");

    assertEquals("contents:mari contents:had contents:littl contents:lamb", query.toString());
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery bq = (BooleanQuery) query;
    assertEquals(4, bq.clauses().size());
    assertEquals("mari", ((TermQuery) bq.clauses().get(0).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(0).getQuery()).getTerm().field());
    assertEquals("had", ((TermQuery) bq.clauses().get(1).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(1).getQuery()).getTerm().field());
    assertEquals("littl", ((TermQuery) bq.clauses().get(2).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(2).getQuery()).getTerm().field());
    assertEquals("lamb", ((TermQuery) bq.clauses().get(3).getQuery()).getTerm().text());
    assertEquals("contents", ((TermQuery) bq.clauses().get(3).getQuery()).getTerm().field());
  }
}
