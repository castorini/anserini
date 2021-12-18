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

package io.anserini.search;

import io.anserini.analysis.AnalyzerUtils;
import io.anserini.ann.ApproximateNearestNeighborSearch;
import io.anserini.ann.IndexVectors;
import io.anserini.ann.fw.FakeWordsEncoderAnalyzer;
import io.anserini.ann.lexlsh.LexicalLshAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queries.CommonTermsQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

/**
 * Convenience class to leverage Anserini {@link ApproximateNearestNeighborSearch} capabilities from code (e.g. Pyserini)
 * rather than command line. It assumes index has been created with {@code -stored} option enabled.
 */
public class SimpleNearestNeighborSearcher {

  private final Analyzer analyzer;
  private final IndexSearcher searcher;

  public SimpleNearestNeighborSearcher(String path) throws IOException {
    this(path, IndexVectors.FW);
  }

  public SimpleNearestNeighborSearcher(String path, String encoding) throws IOException {
    Directory d = FSDirectory.open(Paths.get(path));
    DirectoryReader reader = DirectoryReader.open(d);
    searcher = new IndexSearcher(reader);
    if (encoding.equalsIgnoreCase(IndexVectors.LEXLSH)) {
      analyzer = new LexicalLshAnalyzer();
    } else if (encoding.equalsIgnoreCase(IndexVectors.FW)) {
      analyzer = new FakeWordsEncoderAnalyzer();
      searcher.setSimilarity(new ClassicSimilarity());
    } else {
      throw new RuntimeException("unexpected encoding " + encoding);
    }
  }

  /**
   * Search for nearest neighbors of a certain document, given its identifier
   *
   * @param id the input document identifier
   * @param d  the number of nearest neighbors to retrieve
   * @return an array of nearest neighbors
   * @throws IOException if error encountered during search
   */
  public Result[] search(String id, int d) throws IOException {
    Result[][] neighbors = multisearch(id, 1, d);
    return neighbors.length > 0 ? neighbors[0] : new Result[0];
  }

  /**
   * Search for multiple nearest neighbors of documents having the same identifier
   *
   * @param id documents' identifier
   * @param k  the number of nearest neighbors to retrieve for each document with the given id
   * @return an array of nearest neighbors for each matching document
   * @throws IOException if error encountered during search
   */
  public Result[][] multisearch(String id, int k) throws IOException {
    return multisearch(id, Integer.MAX_VALUE, k);
  }

  protected Result[][] multisearch(String id, int k, int d) throws IOException {
    List<Result[]> results = new ArrayList<>();
    TopDocs wordDocs = searcher.search(new TermQuery(new Term(IndexVectors.FIELD_ID, id)), k);

    for (ScoreDoc scoreDoc : wordDocs.scoreDocs) {
      Document doc = searcher.doc(scoreDoc.doc);
      String vector = doc.get(IndexVectors.FIELD_VECTOR);
      CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, 0.999f);
      List<String> tokens = AnalyzerUtils.analyze(analyzer, vector);
      for (String token : tokens) {
        simQuery.add(new Term(IndexVectors.FIELD_VECTOR, token));
      }
      TopDocs nearest = searcher.search(simQuery, d);
      Result[] neighbors = new Result[nearest.scoreDocs.length];
      int i = 0;
      for (ScoreDoc nn : nearest.scoreDocs) {
        Document ndoc = searcher.doc(nn.doc);
        neighbors[i] = new Result(ndoc.get(IndexVectors.FIELD_ID), nn.score);
        i++;
      }
      results.add(neighbors);
    }
    return results.toArray(new Result[0][0]);
  }

  public static class Result {

    public final String id;
    public final float score;

    private Result(String id, float score) {
      this.id = id;
      this.score = score;
    }
  }
}
