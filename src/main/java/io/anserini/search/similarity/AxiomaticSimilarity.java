/**
 * Anserini: An information retrieval toolkit built on Lucene
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

import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.SmallFloat;

/**
 * Hui Fang and ChengXiang Zhai. 2005. An exploration of axiomatic approaches to information retrieval.
 * In Proceedings of the 28th annual international ACM SIGIR conference on Research and development in
 * information retrieval (SIGIR '05). ACM, New York, NY, USA, 480-487.
 */
public abstract class AxiomaticSimilarity extends Similarity {
  protected final float s;
  /** Cache of decoded bytes. */
  protected static final float[] LENGTH_TABLE = new float[256];
  
  static {
    for (int i = 0; i < 256; i++) {
      LENGTH_TABLE[i] = SmallFloat.byte4ToInt((byte) i);
    }
  }
  
  /**
   * @param s Generic parater s
   * @throws IllegalArgumentException if {@code s} is infinite or if {@code s} is
   *         not within the range {@code [0..1]}
   */
  AxiomaticSimilarity(float s) {
    if (Float.isNaN(s) || s < 0 || s > 1) {
      throw new IllegalArgumentException("illegal s value: " + s + ", must be between 0 and 1");
    }
    this.s = s;
  }
  
  /** Default parameter:
   * <ul>
   *   <li>{@code s = 0.5}</li>
   * </ul>
   */
  AxiomaticSimilarity() {
    this(0.5f);
  }
  
  /** Implemented as <code>log(1 + (docCount - docFreq + 0.5)/(docFreq + 0.5))</code>.
   *
   * @param docFreq terms's document frequency
   * @param docCount total document count in the index
   * @return inverted document frequency
   * */
  float idf(long docFreq, long docCount) {
    throw new UnsupportedOperationException();
  }
  
  /** Implemented as <code>1 / (distance + 1)</code>.
   *
   * @param distance distance
   * @return sloppy frequency
   * */
  float sloppyFreq(int distance) {
    return 1.0f / (distance + 1);
  }
  
  /** The default implementation returns <code>1</code>
   *
   * @param doc doc
   * @param start start
   * @param end end
   * @param payload payload
   * @return 1
   * */
  float scorePayload(int doc, int start, int end, BytesRef payload) {
    return 1;
  }
  
  /** The default implementation computes the average as <code>sumTotalTermFreq / docCount</code>,
   * or returns <code>1</code> if the index does not store sumTotalTermFreq:
   * any field that omits frequency information).
   *
   * @param collectionStats collection-wide statistics
   * @return average document length of FIELD_BODY
   * */
  float avgFieldLength(CollectionStatistics collectionStats) {
    final long sumTotalTermFreq = collectionStats.sumTotalTermFreq();
    if (sumTotalTermFreq <= 0) {
      return 1f;       // field does not exist, or stat is unsupported
    } else {
      final long docCount = collectionStats.docCount() == -1 ? collectionStats.maxDoc() : collectionStats.docCount();
      return (float) (sumTotalTermFreq / (double) docCount);
    }
  }
  
  /**
   * True if overlap tokens (tokens with a position of increment of zero) are
   * discounted from the document's length.
   */
  boolean discountOverlaps = true;
  
  /** Sets whether overlap tokens (Tokens with 0 position increment) are
   *  ignored when computing norm.  By default this is true, meaning overlap
   *  tokens do not count when computing norms.
   *
   * @param v v
   *  */
  public void setDiscountOverlaps(boolean v) {
    discountOverlaps = v;
  }
  
  /**
   * Returns true if overlap tokens are discounted from the document's length.
   * @see #setDiscountOverlaps
   *
   * @return discountOverlaps
   */
  public boolean getDiscountOverlaps() {
    return discountOverlaps;
  }
  
  /** Cache of decoded bytes. */
  private static final float[] NORM_TABLE = new float[256];
  
  static {
    for (int i = 1; i < 256; i++) {
      float f = SmallFloat.byte315ToFloat((byte)i);
      NORM_TABLE[i] = 1.0f / (f*f);
    }
    NORM_TABLE[0] = 1.0f / NORM_TABLE[255]; // otherwise inf
  }
  
  
  @Override
  public final long computeNorm(FieldInvertState state) {
    final int numTerms = discountOverlaps ? state.getLength() - state.getNumOverlap() : state.getLength();
    int indexCreatedVersionMajor = state.getIndexCreatedVersionMajor();
    if (indexCreatedVersionMajor >= 7) {
      return SmallFloat.intToByte4(numTerms);
    } else {
      return SmallFloat.floatToByte315((float) (1 / Math.sqrt(numTerms)));
    }
  }
  
  /**
   * Computes a score factor for a simple term and returns an explanation
   * for that score factor.
   *
   * <p>
   * The default implementation uses:
   *
   * <pre class="prettyprint">
   * idf(docFreq, docCount);
   * </pre>
   *
   * Note that {@link CollectionStatistics#docCount()} is used instead of
   * {@link org.apache.lucene.index.IndexReader#numDocs() IndexReader#numDocs()} because also
   * {@link TermStatistics#docFreq()} is used, and when the latter
   * is inaccurate, so is {@link CollectionStatistics#docCount()}, and in the same direction.
   * In addition, {@link CollectionStatistics#docCount()} does not skew when fields are sparse.
   *
   * @param collectionStats collection-level statistics
   * @param termStats term-level statistics for the term
   * @return an Explain object that includes both an idf score factor
  and an explanation for the term.
   */
  public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats) {
    final long df = termStats.docFreq();
    final long docCount = collectionStats.docCount() == -1 ? collectionStats.maxDoc() : collectionStats.docCount();
    final float idf = idf(df, docCount);
    return Explanation.match(idf, "idf(docFreq=" + df + ", docCount=" + docCount + ")");
  }
  
  /**
   * Computes a score factor for a phrase.
   *
   * <p>
   * The default implementation sums the idf factor for
   * each term in the phrase.
   *
   * @param collectionStats collection-level statistics
   * @param termStats term-level statistics for the terms in the phrase
   * @return an Explain object that includes both an idf
   *         score factor for the phrase and an explanation
   *         for each term.
   */
  public Explanation idfExplain(CollectionStatistics collectionStats, TermStatistics termStats[]) {
    final long docCount = collectionStats.docCount() == -1 ? collectionStats.maxDoc() : collectionStats.docCount();
    double idf = 0d;
    List<Explanation> details = new ArrayList<>();
    for (final TermStatistics stat : termStats ) {
      final long df = stat.docFreq();
      final float termIdf = idf(df, docCount);
      details.add(Explanation.match(termIdf, "idf(docFreq=" + df + ", docCount=" + docCount + ")"));
      idf += termIdf;
    }
    return Explanation.match((float)idf, "idf(), sum of:", details);
  }
  
  @Override
  public final SimScorer scorer(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
    Explanation idf = termStats.length == 1 ? idfExplain(collectionStats, termStats[0]) : idfExplain(collectionStats, termStats);
    float avgdl = avgFieldLength(collectionStats);
  
    float[] cache = new float[256];
    for (int i = 0; i < cache.length; i++) {
      cache[i] = s + s * LENGTH_TABLE[i] / avgdl;
    }
    Stats axStats = new Stats(collectionStats.field(), boost, idf, avgdl, cache);
    return new AxDocScorer(axStats);
  }
  
  /** DocumentCollection statistics for the F2Log model. */
  static class Stats {
    /** F2Log's idf */
    public final Explanation idf;
    /** The average document length. */
    public final float avgdl;
    /** query boost */
    public float boost;
    /** weight (idf * boost) */
    public float weight;
    /** field name, for pulling norms */
    public final String field;
    /** precomputed norm[256] with k1 * ((1 - b) + b * dl / avgdl)
     *  for LENGTH_TABLE */
    private final float[] cache;
    
    Stats(String field, float boost, Explanation idf, float avgdl, float[] cache) {
      this.field = field;
      this.idf = idf;
      this.avgdl = avgdl;
      this.weight = (float) (idf.getValue().doubleValue() * boost);
      this.cache = cache;
    }
  }
  
  class AxDocScorer extends SimScorer {
    private final Stats stats;
    private final float weightValue; // boost * idf
    /** precomputed norm[256] with k1 * ((1 - b) + b * dl / avgdl) */
    private final float[] cache;
  
    AxDocScorer(Stats stats) {
      this.stats = stats;
      this.weightValue = stats.weight;
      cache = stats.cache;
    }
    
    /* Score function is:
     * <pre class="prettyprint">
                                                     occurrences
      score = termWeight * IDF * ---------------------------------------------------------
                                 occurrences + s + documentLength * s / avgDocLength
       </pre>
     */
    @Override
    public float score(float freq, long encodedNorm) {
      // if there are no norms, we act as if b=0
      double norm = cache[((byte) encodedNorm) & 0xFF];
      return weightValue * (float) (freq / (freq + norm));
    }
    
    @Override
    public Explanation explain(Explanation freq, long encodedNorm) {
      return explainScore(freq, encodedNorm, stats);
    }
  }
  
  private Explanation explainTFNorm(Explanation freq, long encodedNorm, Stats stats) {
    List<Explanation> subs = new ArrayList<>();
    subs.add(freq);
    subs.add(Explanation.match(s, "parameter s"));

    byte norm = (byte) encodedNorm;
    float doclen = LENGTH_TABLE[norm & 0xff];
    subs.add(Explanation.match(stats.avgdl, "avgFieldLength"));
    subs.add(Explanation.match(doclen, "fieldLength"));
    return Explanation.match(
        (freq.getValue().floatValue() / (freq.getValue().floatValue() + s + s * doclen/stats.avgdl)),
        "tfNorm, computed as (freq / (freq + s + s * fieldLength / avgFieldLength) from:", subs);
  }
  
  
  private Explanation explainScore(Explanation freq, long encodedNorm, Stats stats) {
    Explanation boostExpl = Explanation.match(stats.boost, "boost");
    List<Explanation> subs = new ArrayList<>();
    if (boostExpl.getValue().floatValue() != 1.0f)
      subs.add(boostExpl);
    subs.add(stats.idf);
    Explanation tfNormExpl = explainTFNorm(freq, encodedNorm, stats);
    subs.add(tfNormExpl);
    return Explanation.match(
        boostExpl.getValue().floatValue() * stats.idf.getValue().floatValue() * tfNormExpl.getValue().floatValue(),
        "score(freq="+freq+", length=" + LENGTH_TABLE[Byte.toUnsignedInt((byte) encodedNorm)] + "), product of:", subs);
  }
  
  @Override
  public String toString() {
    throw new UnsupportedOperationException();
  }
  
  /**
   * Returns the <code>b</code> parameter
   * @see #AxiomaticSimilarity(float)
   *
   * @return s
   */
  public float getS() {
    return s;
  }
}
