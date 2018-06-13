package io.anserini.rts;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class TitleCoordSimilarity extends TFIDFSimilarity {

  @Override
  public float tf(float freq) {
    // TODO Auto-generated method stub
    if (freq > 0)
      return 1;
    else
      return 0;
  }

  @Override
  public float idf(long docFreq, long numDocs) {
    // TODO Auto-generated method stub
    return 1.0f;
  }

  @Override
  public float lengthNorm(int var) {
    // TODO Auto-generated method stub
    // return state.getLength();
    return 1.0f;
  }

  @Override
  public float sloppyFreq(int distance) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public float scorePayload(int doc, int start, int end, BytesRef payload) {
    // TODO Auto-generated method stub
    return 0;
  }
}
