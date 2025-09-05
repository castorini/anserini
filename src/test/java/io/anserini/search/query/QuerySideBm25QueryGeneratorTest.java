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
import io.anserini.index.IndexReaderUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.FSDirectory;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuerySideBm25QueryGeneratorTest {
  @Test
  public void test1() throws IOException {
    String TEST_INDEX = "beir-v1.0.0-nfcorpus.flat";
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    Path indexPath = IndexReaderUtils.getIndex(TEST_INDEX);
    IndexReader reader = DirectoryReader.open(FSDirectory.open(indexPath));
    QueryGenerator queryGenerator = new QuerySideBm25QueryGenerator(0.9f, 0.4f, reader);
    Query query = queryGenerator.buildQuery("contents", analyzer, "Do Cholesterol Statin Drugs Cause Breast Cancer?");

    assertEquals("(contents:caus)^1.1822546 (contents:statin)^3.1420643 (contents:cholesterol)^1.6210032 (contents:cancer)^0.98464656 (contents:do)^2.0192628 (contents:breast)^1.6456642 (contents:drug)^1.7181631", query.toString());
    assertTrue(query instanceof BooleanQuery);

    BooleanQuery bq = (BooleanQuery) query;
    assertEquals(7, bq.clauses().size());
    assertEquals("(contents:caus)^1.1822546", (bq.clauses().get(0).getQuery().toString()));
    assertEquals("(contents:statin)^3.1420643", (bq.clauses().get(1).getQuery().toString()));  
    assertEquals("(contents:cholesterol)^1.6210032", (bq.clauses().get(2).getQuery().toString()));
    assertEquals("(contents:cancer)^0.98464656", (bq.clauses().get(3).getQuery().toString()));
    assertEquals("(contents:do)^2.0192628", (bq.clauses().get(4).getQuery().toString()));
    assertEquals("(contents:breast)^1.6456642", (bq.clauses().get(5).getQuery().toString()));
    assertEquals("(contents:drug)^1.7181631", (bq.clauses().get(6).getQuery().toString()));
  }
}
