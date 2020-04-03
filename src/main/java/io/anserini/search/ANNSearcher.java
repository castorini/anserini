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
package io.anserini.search;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.anserini.analysis.AnalyzerUtils;
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

import static org.apache.lucene.search.BooleanClause.Occur.SHOULD;

public class ANNSearcher {

  private final Analyzer analyzer;
  private final IndexSearcher searcher;

  public ANNSearcher(String path) throws IOException {
    this(path, IndexVectors.FW);
  }

  public ANNSearcher(String path, String encoding) throws IOException {
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

  public NearestNeighbors[] annSearch(String word, int k) throws IOException {
    List<NearestNeighbors> results = new ArrayList<>();
    TopDocs wordDocs = searcher.search(new TermQuery(new Term(IndexVectors.FIELD_WORD, word)), k);

    for (ScoreDoc scoreDoc : wordDocs.scoreDocs) {
      Document doc = searcher.doc(scoreDoc.doc);
      String vector = doc.get(IndexVectors.FIELD_VECTOR);
      CommonTermsQuery simQuery = new CommonTermsQuery(SHOULD, SHOULD, 0);
      List<String> tokens = AnalyzerUtils.analyze(analyzer, vector);
      for (String token : tokens) {
        simQuery.add(new Term(IndexVectors.FIELD_VECTOR, token));
      }
      TopDocs nearest = searcher.search(simQuery, k);
      NearestNeighbors.Neighbor[] neighbors = new NearestNeighbors.Neighbor[nearest.scoreDocs.length];
      int i = 0;
      for (ScoreDoc nn : nearest.scoreDocs) {
        Document ndoc = searcher.doc(nn.doc);
        neighbors[i] = new NearestNeighbors.Neighbor(ndoc.get(IndexVectors.FIELD_WORD), nn.score);
        i++;
      }
      results.add(new NearestNeighbors(doc.get(IndexVectors.FIELD_WORD), neighbors));
    }
    return results.toArray(new NearestNeighbors[0]);
  }

  public static class NearestNeighbors {

    public final String id;
    public final Neighbor[] neighbors;

    private NearestNeighbors(String id, Neighbor[] neighbors) {
      this.id = id;
      this.neighbors = neighbors;
    }

    public static class Neighbor {

      public final String id;
      public final float score;

      private Neighbor(String id, float score) {
        this.id = id;
        this.score = score;
      }

      @Override
      public String toString() {
        return "Neighbor{" +
            "id='" + id + '\'' +
            ", score=" + score +
            '}';
      }
    }

    @Override
    public String toString() {
      return "NearestNeighbors{" +
          "id='" + id + '\'' +
          ", neighbors=" + Arrays.toString(neighbors) +
          '}';
    }
  }
}
