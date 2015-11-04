package io.anserini.ltr.feature;

import io.anserini.rerank.RerankerContext;

import java.util.List;

import org.apache.lucene.index.Terms;

import com.google.common.collect.Lists;

/**
 * A collection of {@link IntFeatureExtractor}s.
 */
public class IntFeatureExtractors {
  public List<IntFeatureExtractor> extractors = Lists.newArrayList();

  public IntFeatureExtractors() {}

  public IntFeatureExtractors add(IntFeatureExtractor extractor) {
    extractors.add(extractor);
    return this;
  }

  public int[] extractAll(Terms terms, RerankerContext context) {
    int[] features = new int[extractors.size()];

    for (int i=0; i<extractors.size(); i++) {
      features[i] = extractors.get(i).extract(terms, context);
    }

    return features;
  }
}
