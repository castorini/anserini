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

package io.anserini.search.similarity;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.IndexOptions;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

import java.util.ArrayList;
import java.util.List;

public class AccurateBM25Similarity extends Similarity {
  private final float k1;
  private final float b;

  public AccurateBM25Similarity(float k1, float b) {
    if (!Float.isFinite(k1) || k1 < 0) {
      throw new IllegalArgumentException("illegal k1 value: " + k1 + ", must be a non-negative finite value");
    }
    if (Float.isNaN(b) || b < 0 || b > 1) {
      throw new IllegalArgumentException("illegal b value: " + b + ", must be between 0 and 1");
    }
    this.k1 = k1;
    this.b = b;
  }

  public AccurateBM25Similarity() {
    this(1.2f, 0.75f);
  }

  protected float idf(long docFreq, long docCount) {
    return (float) Math.log(1 + (docCount - docFreq + 0.5D) / (docFreq + 0.5D));
  }

  private float avgFieldLength(CollectionStatistics collectionStats) {
    return (float) (collectionStats.sumTotalTermFreq() / (double) collectionStats.docCount());
  }

  @Override
  public final long computeNorm(FieldInvertState state) {
    final int numTerms;
    if (state.getIndexOptions() == IndexOptions.DOCS && state.getIndexCreatedVersionMajor() >= 8) {
      numTerms = state.getUniqueTermCount();
    } else {
      numTerms = state.getLength();
    }
    return numTerms;
  }

  private Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
    final long df = termStats.docFreq();
    final long docCount = collectionStats.docCount();
    final float idf = idf(df, docCount);
    return Explanation.match(idf, "idf, computed as log(1 + (N - n + 0.5) / (n + 0.5)) from:",
        Explanation.match(df, "n, number of documents containing term"),
        Explanation.match(docCount, "N, total number of documents with field"));
  }

  private Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics[] termStats) {
    double idf = 0d; // sum into a double before casting into a float
    List<Explanation> details = new ArrayList<>();
    for (final TermStatistics stat : termStats) {
      Explanation idfExplain = idfExplain(collectionStats, stat);
      details.add(idfExplain);
      idf += idfExplain.getValue().floatValue();
    }
    return Explanation.match((float) idf, "idf, sum of:", details);
  }

  @Override
  public final SimScorer scorer(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
    Explanation idf = termStats.length == 1 ? idfExplain(collectionStats, termStats[0]) : idfExplain(collectionStats, termStats);
    float avgdl = avgFieldLength(collectionStats);

    return new BM25ScorerFixed(boost, k1, b, idf, avgdl);
  }

  @Override
  public String toString() {
    return "BM25(k1=" + k1 + ",b=" + b + ")";
  }

  public final float getK1() {
    return k1;
  }

  public final float getB() {
    return b;
  }

  private static class BM25ScorerFixed extends SimScorer {

    private final float boost;
    private final float k1;
    private final float b;
    private final Explanation idf;
    private final float avgdl;
    private final float multK1_b_InvAvgdl;
    private final float multK1minusB;

    /**
     * weight (idf * boost)
     */
    private final float weight;

    BM25ScorerFixed(float boost, float k1, float b, Explanation idf, float avgdl) {
      this.boost = boost;
      this.idf = idf;
      this.avgdl = avgdl;
      this.k1 = k1;
      this.b = b;
      this.weight = boost * idf.getValue().floatValue();
      this.multK1minusB = k1 * (1 - b);
      // Normally avgdl should be >= 1, but let's use Math.max to avoid division by zero just in case
      this.multK1_b_InvAvgdl = k1 * b / Math.max(1e-10f, avgdl);
    }

    @Override
    public float score(float freq, long norm) {
      float docLen = norm;
      float wf = this.weight * freq;
      float denominator = freq + this.multK1minusB + this.multK1_b_InvAvgdl * docLen;
      return wf / denominator;
    }

    @Override
    public Explanation explain(Explanation freq, long encodedNorm) {
      List<Explanation> subs = new ArrayList<>(explainConstantFactors());
      Explanation tfExpl = explainTF(freq, encodedNorm);
      subs.add(tfExpl);
      return Explanation.match(weight * tfExpl.getValue().floatValue(),
          "score(freq=" + freq.getValue() + "), product of:", subs);
    }

    private Explanation explainTF(Explanation freq, long norm) {
      List<Explanation> subs = new ArrayList<>();
      subs.add(freq);
      subs.add(Explanation.match(k1, "k1, term saturation parameter"));
      float docLen = norm;
      subs.add(Explanation.match(b, "b, length normalization parameter"));
      subs.add(Explanation.match(docLen, "dl, length of field"));
      subs.add(Explanation.match(avgdl, "avgdl, average length of field"));
      float normValue = k1 * ((1 - b) + b * docLen / avgdl);
      return Explanation.match(
          (float) (freq.getValue().floatValue() / (freq.getValue().floatValue() + (double) normValue)),
          "tf, computed as freq / (freq + k1 * (1 - b + b * dl / avgdl)) from:", subs);
    }

    private List<Explanation> explainConstantFactors() {
      List<Explanation> subs = new ArrayList<>();
      if (boost != 1.0f) {
        subs.add(Explanation.match(boost, "boost"));
      }
      subs.add(idf);
      return subs;
    }
  }
}
