package io.anserini.search.similarity;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;

import java.io.IOException;

/**
 * Similarity that uses a Ranklib ranker to compute the score
 */
public class RankLibSimilarity extends Similarity{
  @Override
  public long computeNorm(FieldInvertState fieldInvertState) {
    return 0;
  }

  @Override
  public SimWeight computeWeight(float v, CollectionStatistics collectionStatistics, TermStatistics... termStatisticses) {
    return null;
  }

  @Override
  public SimScorer simScorer(SimWeight simWeight, LeafReaderContext leafReaderContext) throws IOException {
    return null;
  }
}
