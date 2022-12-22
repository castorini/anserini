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

import java.util.ArrayList;
import java.util.List;

import io.anserini.index.Constants;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DisjunctionMaxQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class Covid19QueryGeneratorTest {
  @Test
  public void test1() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    QueryGenerator queryGenerator = new Covid19QueryGenerator();
    Query query;

    // no mention of covid-19, just pass through
    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "incubation period");
    assertEquals("(contents:incub)^1.0 (contents:period)^1.0", query.toString());

    Query targetQuery = getTargetQuery("incub", "period");

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "incubation period covid-19");
    assertEquals(targetQuery, query);

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "covid-19 incubation period");
    assertEquals(targetQuery, query);

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "incubation period COVID19");
    assertEquals(targetQuery, query);

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "SARS-cov-2 incubation period");
    assertEquals(targetQuery, query);

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "the 2019nCov incubation period");
    assertEquals(targetQuery, query);

    // no mention of covid-19, just pass through
    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "drugs");
    assertEquals("(contents:drug)^1.0", query.toString());

    Query targetQuery2 = getTargetQuery("drug");

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer, "coronavirus drugs");
    assertEquals(targetQuery2, query);
  }

  private Query getTargetQuery(String... terms) {
    BooleanQuery.Builder builder = new BooleanQuery.Builder();
    for (String term : terms) {
      builder.add(new TermQuery(new Term("contents", term)), BooleanClause.Occur.SHOULD);
    }

    List<Query> disjuncts = new ArrayList<>();
    disjuncts.add(new PhraseQuery("contents", "covid", "19"));
    disjuncts.add(new PhraseQuery("contents", "2019", "ncov"));
    disjuncts.add(new PhraseQuery("contents", "sar", "cov", "2"));
    builder.add(new DisjunctionMaxQuery(disjuncts, 0.0f), BooleanClause.Occur.SHOULD);

    return builder.build();
  }

  @Test
  public void testPattern() {
    Covid19QueryGenerator queryGenerator = new Covid19QueryGenerator();

    assertTrue(queryGenerator.isCovidQuery("incubation period covid19"));
    assertTrue(queryGenerator.isCovidQuery("covid19 incubation period"));
    assertFalse(queryGenerator.isCovidQuery("covidx19 incubation period"));

    assertTrue(queryGenerator.isCovidQuery("incubation period covid-19"));
    assertTrue(queryGenerator.isCovidQuery("covid-19 incubation period"));
    assertFalse(queryGenerator.isCovidQuery("covid-9 incubation period"));

    assertTrue(queryGenerator.isCovidQuery("incubation period 2019nCov"));
    assertTrue(queryGenerator.isCovidQuery("what's 2019nCov incubation period"));
    assertTrue(queryGenerator.isCovidQuery("2019-nCov incubation period"));
    assertFalse(queryGenerator.isCovidQuery("2019 incubation period"));

    assertTrue(queryGenerator.isCovidQuery("incubation period sars-cov-2"));
    assertTrue(queryGenerator.isCovidQuery("incubation period sars-cov2"));
    assertTrue(queryGenerator.isCovidQuery("incubation period sarscov2"));
    assertFalse(queryGenerator.isCovidQuery("incubation period sars"));

    assertTrue(queryGenerator.isCovidQuery("incubation period coronavirus"));
    assertTrue(queryGenerator.isCovidQuery("incubation period corona virus"));
    assertTrue(queryGenerator.isCovidQuery("the CoronaVirus incubation period"));
    assertTrue(queryGenerator.isCovidQuery("the Corona Virus incubation period"));
    assertFalse(queryGenerator.isCovidQuery("I like to drink Corona beers"));
  }

  @Test
  public void testRemoveBoilerplate() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    Covid19QueryGenerator queryGenerator = new Covid19QueryGenerator();
    Query query;

    query = queryGenerator.buildQuery(Constants.CONTENTS, analyzer,
        "I'm looking for information about the incubation period of COVID-19?");
    assertEquals(getTargetQuery("incub", "period"), query);

    assertEquals("the incubation period of COVID-19?",
        queryGenerator.removeBoilerplate("I'm looking for information about the incubation period of COVID-19?"));
    assertEquals("the incubation period of COVID-19?",
        queryGenerator.removeBoilerplate("What do we know about the incubation period of COVID-19?"));
    assertEquals("the incubation period of COVID-19?",
        queryGenerator.removeBoilerplate("What is known about the incubation period of COVID-19?"));

    // Make sure pattern is non-greedy.
    assertEquals("the incubation period of COVID-19 and related information?",
        queryGenerator.removeBoilerplate("I'm looking for information about the incubation period of COVID-19 and related information?"));

  }
}
