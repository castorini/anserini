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

package io.anserini.eval;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.BeforeClass;
import io.anserini.fusion.FuseRuns;
import io.anserini.search.ScoredDoc;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;


public class ExcludeDocsTest {
  @BeforeClass
  public static void setupClass() {
    Configurator.setLevel(FuseRuns.class.getName(), Level.ERROR);
  }

  @Test
  public void testExcludable() {
    assertEquals(ExcludeDocs.isExcludable("bright-aops"), true);
    assertEquals(ExcludeDocs.isExcludable("bright-biology"), false);
    assertEquals(ExcludeDocs.isExcludable("beir-nfcorpus"), false);
  }

  @Test
  public void testExclude() {
    try {
      ExcludeDocs excludeDocs = new ExcludeDocs("bright-leetcode");
      ScoredDoc[] results = new ScoredDoc[3];
      results[0] = new ScoredDoc("leetcode/leetcode_1996.txt", 0, 3, null);
      results[1] = new ScoredDoc("good", 0, 2, null);
      results[2] = new ScoredDoc("alsogood", 0, 1, null);
      results = excludeDocs.exclude("0", results);
      assertEquals(results.length, 2);
      assertEquals(results[0].docid, "good");
      assertEquals(results[1].docid, "alsogood");
    } catch (Exception e) {
       throw new RuntimeException(e);
    }
  }
}
