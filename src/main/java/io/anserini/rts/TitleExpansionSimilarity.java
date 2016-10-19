package io.anserini.rts;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.search.similarities.TFIDFSimilarity;
import org.apache.lucene.util.BytesRef;

public class TitleExpansionSimilarity extends TFIDFSimilarity {

  @Override
  public float coord(int overlap, int maxOverlap) {
    // TODO Auto-generated method stub
    return 1.0f;
  }

  @Override
  public float queryNorm(float sumOfSquaredWeights) {
    // TODO Auto-generated method stub
    return 1;
  }

  @Override
  public float tf(float freq) {
    // TODO Auto-generated method stub
    if (freq > 0)
      return 1.0f;
    else
      return 0;
  }

  @Override
  public float idf(long docFreq, long numDocs) {
    // TODO Auto-generated method stub
    return 1.0f;
  }

  @Override
  public float lengthNorm(FieldInvertState state) {
    // TODO Auto-generated method stub
    // return state.getLength();
    return 1.0f;
    // return state.getBoost();
  }

  @Override
  public float decodeNormValue(long norm) {
    // TODO Auto-generated method stub
    // return (float)norm;
    return 1.0f;
  }

  @Override
  public long encodeNormValue(float f) {
    // TODO Auto-generated method stub
    return (long) f;
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

  // @Override
  // public float queryNorm(float sumOfSquaredWeights) {
  // return 1;
  // }
  //
  // @Override
  // public float tf(float freq) {
  // if (freq>0)
  // return 3;
  // else return 0;
  // }
  //
  // @Override
  // public float idf(long docFreq, long numDocs) {
  // return 1;
  // }
  //
  // @Override
  // public float lengthNorm(FieldInvertState state) {
  // return (float)(1.0);
  // }

}
