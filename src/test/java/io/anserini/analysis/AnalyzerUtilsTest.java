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

package io.anserini.analysis;

import io.anserini.collection.FileSegment;
import io.anserini.collection.HtmlCollection;
import io.anserini.collection.JsonCollection;
import io.anserini.index.IndexCollection;
import org.apache.lucene.analysis.Analyzer;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AnalyzerUtilsTest {
  private String loadFirstCacmDocumentContents() throws IOException {
    HtmlCollection collection = new HtmlCollection(Paths.get("src/main/resources/cacm/"));
    List<Path> segmentPaths = collection.getSegmentPaths();
    assertFalse("CACM collection should provide at least one segment.", segmentPaths.isEmpty());

    try (FileSegment<HtmlCollection.Document> segment = collection.createFileSegment(segmentPaths.get(0))) {
      Iterator<HtmlCollection.Document> docs = segment.iterator();
      assertTrue("CACM segment should have at least one document.", docs.hasNext());
      return docs.next().contents();
    }
  }

  @Test
  public void testAnalyzeAndComputeVectorOnCacmDocument() throws IOException {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    String contents = loadFirstCacmDocumentContents();

    List<String> tokens = AnalyzerUtils.analyze(analyzer, contents);
    assertTrue(tokens.size() > 10);
    assertTrue(tokens.contains("preliminari"));
    assertTrue(tokens.contains("algebra"));
    assertTrue(tokens.contains("languag"));

    Map<String, Long> vector = AnalyzerUtils.computeDocumentVector(analyzer, contents);
    assertEquals(1L, vector.get("preliminari").longValue());
    assertEquals(1L, vector.get("algebra").longValue());
    assertEquals(1L, vector.get("languag").longValue());
    assertTrue("Token list and vector keys should overlap.", vector.keySet().containsAll(List.of("preliminari", "algebra", "languag")));
  }

  @Test
  public void testComputeDocumentVectorWithDocumentCollection() {
    Analyzer analyzer = IndexCollection.DEFAULT_ANALYZER;
    String contents = "parser vector test test tokens only";
    String json = "{\"id\":\"doc1\",\"contents\":\"" + contents + "\"}";

    Map<String, Long> viaParser = AnalyzerUtils.computeDocumentVector(analyzer, JsonCollection.class, json);
    Map<String, Long> direct = AnalyzerUtils.computeDocumentVector(analyzer, contents);

    assertEquals(direct, viaParser);
    assertEquals(2L, viaParser.get("test").longValue());
    assertFalse("Parser output should not include JSON metadata terms.", viaParser.containsKey("id"));
  }
}
