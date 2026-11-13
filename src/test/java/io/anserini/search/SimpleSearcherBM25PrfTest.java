/*
 * Anserini: A Lucene toolkit for reproducible information retrieval research
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 */
package io.anserini.search;

import java.util.List;
import java.util.Map;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.index.Constants;
import io.anserini.index.IndexerTestBase;
import io.anserini.rerank.RerankerContext;
import io.anserini.rerank.lib.BM25PrfReranker;
import io.anserini.rerank.lib.ScoreTiesAdjusterReranker;
import io.anserini.search.query.BagOfWordsQueryGenerator;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.junit.Test;

public class SimpleSearcherBM25PrfTest extends IndexerTestBase {
  private void addFeedbackDocuments() throws Exception {
    try (var dir = FSDirectory.open(tempDir1);
         var writer = new IndexWriter(dir, new IndexWriterConfig(new org.apache.lucene.analysis.en.EnglishAnalyzer()))) {
      FieldType type = new FieldType(TextField.TYPE_STORED);
      type.setStoreTermVectors(true);
      for (int i = 0; i < 3; i++) {
        Document doc = new Document();
        String id = "feedback" + i;
        doc.add(new StringField(Constants.ID, id, Field.Store.YES));
        doc.add(new BinaryDocValuesField(Constants.ID, new BytesRef(id)));
        doc.add(new Field(Constants.CONTENTS, i < 2 ? "anchor bridge" : "bridge", type));
        writer.addDocument(doc);
      }
    }
  }

  @Test
  public void testFeedbackDepthIndependentOfOutputDepth() throws Exception {
    addFeedbackDocuments();
    try (var searcher = new SimpleSearcher(tempDir1.toString())) {
      assertEquals(2, searcher.search("anchor", 10).length);
      searcher.set_bm25prf(null, 5, 2, 0.9f, 0.4f, 0.2f, false);
      ScoredDoc[] full = searcher.search("anchor", 10);
      assertEquals(3, full.length); // "bridge" admits a document without the original query.
      ScoredDoc[] one = searcher.search("anchor", 1);
      assertEquals(1, one.length);
      assertEquals(full[0].docid, one[0].docid);
      assertEquals(full[0].score, one[0].score, 0);
      assertEquals("feedback0", full[0].docid); // Stable document-id tie breaking.
      assertEquals("feedback1", full[1].docid);
    }
  }

  @Test
  public void testParityWithExistingRerankerAndBatchSearch() throws Exception {
    addFeedbackDocuments();
    try (var searcher = new SimpleSearcher(tempDir1.toString())) {
      searcher.set_bm25prf(null, 3, 2, 1.2f, 0.75f, 0.4f, false);
      ScoredDoc[] actual = searcher.search("anchor", 5);
      Query query = new BagOfWordsQueryGenerator().buildQuery(Constants.CONTENTS, searcher.analyzer, "anchor");
      SearchCollection.Args args = new SearchCollection.Args();
      args.hits = 5;
      var context = new RerankerContext<String>(searcher.searcher, "q", query, null, "anchor",
          AnalyzerUtils.analyze(searcher.analyzer, "anchor"), null, args);
      var initial = ScoredDocs.fromTopDocs(searcher.searcher.search(query, 5), searcher.searcher);
      var expected = new BM25PrfReranker<String>(searcher.analyzer, null, Constants.CONTENTS,
          3, 2, 1.2f, 0.75f, 0.4f, false).rerank(initial, context);
      expected = new ScoreTiesAdjusterReranker<String>().rerank(expected, context);
      assertEquals(expected.docids.length, actual.length);
      for (int i = 0; i < actual.length; i++) {
        assertEquals(expected.docids[i], actual[i].docid);
        assertEquals(expected.scores[i], actual[i].score, 0);
      }
      Map<String, ScoredDoc[]> batch = searcher.batch_search(List.of("anchor", "missing", ""),
          List.of("a", "b", "c"), 5, 3);
      assertEquals(actual.length, batch.get("a").length);
      for (int i = 0; i < actual.length; i++) {
        assertEquals(actual[i].docid, batch.get("a")[i].docid);
        assertEquals(actual[i].score, batch.get("a")[i].score, 0);
      }
      assertEquals(0, batch.get("b").length);
      assertEquals(0, batch.get("c").length);
    }
  }

  @Test
  public void testModeTransitionsAndValidation() throws Exception {
    try (var searcher = new SimpleSearcher(tempDir1.toString())) {
      var baseline = searcher.search("text");
      searcher.set_rm3();
      searcher.set_bm25prf();
      assertTrue(searcher.use_bm25prf());
      assertFalse(searcher.use_rm3());
      assertFalse(searcher.use_rocchio());
      searcher.unset_bm25prf();
      assertFalse(searcher.use_bm25prf());
      assertEquals(baseline[0].score, searcher.search("text")[0].score, 0);
      searcher.set_bm25prf();
      searcher.set_rocchio();
      assertFalse(searcher.use_bm25prf());
      searcher.set_bm25prf();
      searcher.set_rm3();
      assertFalse(searcher.use_bm25prf());
      searcher.set_bm25prf();
      expectThrows(IllegalArgumentException.class,
          () -> searcher.search(new TermQuery(new Term(Constants.CONTENTS, "text")), 1));
      expectThrows(IllegalArgumentException.class,
          () -> searcher.set_bm25prf(null, 2, 0, 0.9f, 0.4f, 0.2f, false));
      expectThrows(IllegalArgumentException.class,
          () -> searcher.set_bm25prf(null, 2, 2, Float.NaN, 0.4f, 0.2f, false));
      expectThrows(IllegalArgumentException.class,
          () -> searcher.set_bm25prf(null, 2, 2, 0.9f, 1.1f, 0.2f, false));
      expectThrows(IllegalArgumentException.class, () -> searcher.set_bm25prf("DoesNotExist"));
      assertTrue(searcher.use_bm25prf()); // Failed configurations leave the previous mode intact.
    }
  }
}
