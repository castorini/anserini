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

import io.anserini.index.IndexArgs;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.search.BooleanQuery;
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
    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "incubation period");
    assertEquals("contents:incub contents:period", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "incubation period covid-19");
    assertEquals("contents:incub contents:period (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "covid-19 incubation period");
    assertEquals("contents:incub contents:period (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "incubation period COVID19");
    assertEquals("contents:incub contents:period (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "SARS-cov-2 incubation period");
    assertEquals("contents:incub contents:period (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "the 2019nCov incubation period");
    assertEquals("contents:incub contents:period (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());

    // no mention of covid-19, just pass through
    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "drugs");
    assertEquals("contents:drug", query.toString());

    query = queryGenerator.buildQuery(IndexArgs.CONTENTS, analyzer, "coronavirus drugs");
    assertEquals("contents:drug (contents:\"covid 19\" | contents:\"2019 ncov\" | contents:\"sar cov 2\")", query.toString());
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
}
